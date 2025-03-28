package com.takeshi.config.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaSignException;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.config.security.CachedBodyHttpServletRequest;
import com.takeshi.constants.RequestConstants;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static cn.dev33.satoken.SaManager.log;

/**
 * TakeshiSaSignTemplate
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Component
@RequiredArgsConstructor
public class TakeshiSaSignTemplate extends SaSignTemplate {

    private final ObjectMapper objectMapper;

    /**
     * body
     */
    public static final String BODY = "body";

    /**
     * 将所有参数连接成一个字符串(不排序)，形如：b=28a=18c=3，忽略null值
     *
     * @param paramsMap 参数列表
     * @return 拼接出的参数字符串
     */
    @Override
    public String joinParams(Map<String, ?> paramsMap) {
        return MapUtil.join(paramsMap, "&", "=", true);
    }

    /**
     * 将所有参数按照字典顺序连接成一个字符串，形如：a=18b=28c=3，忽略null值
     *
     * @param paramsMap 参数列表
     * @return 拼接出的参数字符串
     */
    @Override
    public String joinParamsDictSort(Map<String, ?> paramsMap) {
        return MapUtil.sortJoin(paramsMap, "&", "=", true);
    }

    /**
     * 创建签名：md5(paramsStr + keyStr)，忽略null值
     *
     * @param paramsMap 参数列表
     * @return 签名
     */
    @Override
    public String createSign(Map<String, ?> paramsMap) {
        String secretKey = this.getSecretKey();
        SaSignException.notEmpty(secretKey, "The secret key participating in parameter signature cannot be empty.", SaErrorCode.CODE_12201);
        // 如果调用者不小心传入了 sign 参数，则此处需要将 sign 参数排除在外
        if (paramsMap.containsKey(sign)) {
            // 为了保证不影响原有的 paramsMap，此处需要再复制一份
            paramsMap = new TreeMap<>(paramsMap);
            paramsMap.remove(sign);
        }
        // 计算签名
        String paramsStr = this.joinParamsDictSort(paramsMap);
        String fullStr = paramsStr + "&" + key + "=" + secretKey;
        String signStr = this.digestFullStr(fullStr);

        // 输入日志，方便调试
        log.debug("fullStr：{}", fullStr);
        log.debug("signStr：{}", signStr);

        return signStr;
    }

    /**
     * 创建签名：md5(paramsStr + keyStr)，忽略null值
     *
     * @param request 请求对象
     * @return 签名
     */
    public String createSign(SaRequest request) {
        return this.createSign(this.getAllParamMap(request));
    }

    /**
     * 校验：指定时间戳与当前时间戳的差距是否在允许的范围内，如果超出则抛出异常
     *
     * @param timestamp 待校验的时间戳
     */
    @Override
    public void checkTimestamp(long timestamp) {
        if (!this.isValidTimestamp(timestamp)) {
            throw new SaSignException("timestamp beyond the allowed range：" + timestamp).setCode(SaErrorCode.CODE_12203);
        }
    }

    /**
     * 校验：随机字符串 nonce 是否有效，如果无效则抛出异常。
     * 注意：同一 nonce 只可以被校验通过一次，校验后将保存在缓存中，再次校验将无法通过
     *
     * @param nonce 待校验的随机字符串
     */
    @Override
    public void checkNonce(String nonce) {
        // 为空代表无效
        if (SaFoxUtil.isEmpty(nonce)) {
            throw new SaSignException("nonce Empty, invalid");
        }
        // 校验此 nonce 是否已被使用过
        String key = this.splicingNonceSaveKey(nonce);
        if (SaManager.getSaTokenDao().get(key) != null) {
            throw new SaSignException("This nonce has already been used and cannot be reused.：" + nonce);
        }
        // 校验通过后，将此 nonce 保存在缓存中，保证下次校验无法通过
        SaManager.getSaTokenDao().set(key, nonce, this.getSignConfigOrGlobal().getSaveNonceExpire() * 2 + 2);
    }

    /**
     * 判断：给定的参数 + 秘钥 生成的签名是否为有效签名
     *
     * @param paramsMap 参数列表
     * @param sign      待验证的签名
     * @return 签名是否有效
     */
    @Override
    public boolean isValidSign(Map<String, ?> paramsMap, String sign) {
        String theSign = this.createSign(paramsMap);
        return theSign.equals(sign);
    }

    /**
     * 校验：给定的参数 + 秘钥 生成的签名是否为有效签名，如果签名无效则抛出异常
     *
     * @param paramsMap 参数列表
     * @param sign      待验证的签名
     */
    @Override
    public void checkSign(Map<String, ?> paramsMap, String sign) {
        if (!this.isValidSign(paramsMap, sign)) {
            throw new SaSignException("Invalid signature：" + sign).setCode(SaErrorCode.CODE_12202);
        }
    }

    /**
     * 判断：参数列表中的 nonce、timestamp、sign 是否均为合法的
     *
     * @param paramMap 待校验的请求参数集合
     * @return 是否合法
     */
    @Override
    public boolean isValidParamMap(Map<String, String> paramMap) {
        // 获取必须的三个参数
        String timestampValue = paramMap.get(timestamp);
        String nonceValue = paramMap.get(nonce);
        String signValue = paramMap.get(sign);
        // 参数非空校验
        if (SaFoxUtil.isEmpty(timestampValue) || SaFoxUtil.isEmpty(signValue)) {
            return false;
        }
        // 三个值的校验必须全部通过
        return this.isValidTimestamp(Long.parseLong(timestampValue))
                && this.isValidNonce(nonceValue)
                && this.isValidSign(paramMap, signValue);
    }

    /**
     * 校验：参数列表中的 nonce、timestamp、sign 是否均为合法的，如果不合法，则抛出对应的异常
     *
     * @param paramMap 待校验的请求参数集合
     */
    @Override
    public void checkParamMap(Map<String, String> paramMap) {
        // 获取必须的三个参数
        String timestampValue = paramMap.get(timestamp);
        String nonceValue = paramMap.get(nonce);
        String signValue = paramMap.get(sign);
        // 参数非空校验
        SaSignException.notEmpty(timestampValue, "Missing timestamp field");
        SaSignException.notEmpty(nonceValue, "Missing nonce field");
        SaSignException.notEmpty(signValue, "Missing sign field");
        // 依次校验三个参数
        this.checkTimestamp(Long.parseLong(timestampValue));
        this.checkNonce(nonceValue);
        this.checkSign(paramMap, signValue);
        // 通过 √
    }

    /**
     * 判断：一个请求中的 nonce、timestamp、sign 是否均为合法的
     *
     * @param request    待校验的请求对象
     * @param paramNames 指定参与签名的参数有哪些，如果不填写则默认为全部参数
     * @return 是否合法
     */
    @Override
    public boolean isValidRequest(SaRequest request, String... paramNames) {
        if (paramNames.length == 0) {
            return this.isValidParamMap(this.getAllParamMap(request));
        } else {
            return this.isValidParamMap(this.takeRequestParam(request, paramNames));
        }
    }

    /**
     * 校验：一个请求的 nonce、timestamp、sign 是否均为合法的，如果不合法，则抛出对应的异常
     *
     * @param paramNames 指定参与签名的参数有哪些，如果不填写则默认为全部参数
     * @param request    待校验的请求对象
     */
    @Override
    public void checkRequest(SaRequest request, String... paramNames) {
        if (paramNames.length == 0) {
            checkParamMap(this.getAllParamMap(request));
        } else {
            checkParamMap(takeRequestParam(request, paramNames));
        }
    }

    /**
     * 从请求中提取指定的参数
     *
     * @param request    请求对象
     * @param paramNames 指定的参数名称，不可为空，如果传入空数组则代表只拿 timestamp、nonce、sign 三个参数
     * @return 提取出的参数
     */
    @Override
    protected Map<String, String> takeRequestParam(SaRequest request, String[] paramNames) {
        return this.getAllParamMap(request, paramNames);
    }

    @SneakyThrows
    private Map<String, String> getAllParamMap(SaRequest request) {
        Map<String, String> paramMap = new HashMap<>(request.getParamMap());
        if (!HttpMethod.GET.matches(request.getMethod())
                && request.getSource() instanceof CachedBodyHttpServletRequest cachedBodyHttpServletRequest) {
            JsonNode jsonNode = objectMapper.readTree(cachedBodyHttpServletRequest.getInputStream());
            if (!jsonNode.isNull()) {
                if (jsonNode.isObject()) {
                    Map<String, String> map = objectMapper.convertValue(jsonNode, new TypeReference<>() {
                    });
                    if (CollUtil.isNotEmpty(map)) {
                        paramMap.putAll(map);
                    }
                } else {
                    paramMap.put(BODY, objectMapper.writeValueAsString(jsonNode));
                }
            }
        }
        paramMap.put(timestamp, request.getHeader(RequestConstants.Header.TIMESTAMP));
        paramMap.put(nonce, request.getHeader(RequestConstants.Header.NONCE));
        paramMap.put(sign, request.getHeader(RequestConstants.Header.SIGN));
        return paramMap;
    }

    @SneakyThrows
    private Map<String, String> getAllParamMap(SaRequest request, String... paramNames) {
        Map<String, String> paramMap = new HashMap<>();
        // 获取指定的参数
        for (String paramName : paramNames) {
            paramMap.put(paramName, request.getParam(paramName));
        }
        if (!HttpMethod.GET.matches(request.getMethod())
                && request.getSource() instanceof CachedBodyHttpServletRequest cachedBodyHttpServletRequest) {
            JsonNode jsonNode = objectMapper.readTree(cachedBodyHttpServletRequest.getInputStream());
            if (!jsonNode.isNull()) {
                if (jsonNode.isObject()) {
                    Map<String, String> map = objectMapper.convertValue(jsonNode, new TypeReference<>() {
                    });
                    if (CollUtil.isNotEmpty(map)) {
                        for (String paramName : paramNames) {
                            paramMap.put(paramName, map.get(paramName));
                        }
                    }
                } else {
                    paramMap.put(BODY, objectMapper.writeValueAsString(jsonNode));
                }
            }
        }
        paramMap.put(timestamp, request.getHeader(RequestConstants.Header.TIMESTAMP));
        paramMap.put(nonce, request.getHeader(RequestConstants.Header.NONCE));
        paramMap.put(sign, request.getHeader(RequestConstants.Header.SIGN));
        return paramMap;
    }

}
