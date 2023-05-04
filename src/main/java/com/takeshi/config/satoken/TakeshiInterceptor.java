package com.takeshi.config.satoken;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.router.SaRouteFunction;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.useragent.UserAgentUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.takeshi.annotation.RepeatSubmit;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.properties.RateLimitProperties;
import com.takeshi.config.properties.TakeshiProperties;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.enums.TakeshiRedisKeyEnum;
import com.takeshi.exception.Either;
import com.takeshi.pojo.bo.ParamBO;
import com.takeshi.pojo.vo.ResponseDataVO;
import com.takeshi.util.GsonUtil;
import com.takeshi.util.TakeshiUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TakeshiInterceptor
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public class TakeshiInterceptor implements HandlerInterceptor {

    /**
     * 每次进入拦截器的[执行函数]，默认为登录校验
     */
    public SaRouteFunction function = (req, res, handler) -> StpUtil.checkLogin();

    /**
     * 创建一个路由拦截器
     */
    public TakeshiInterceptor() {
    }

    /**
     * 创建, 并指定[执行函数]
     *
     * @param function [执行函数]
     */
    private TakeshiInterceptor(SaRouteFunction function) {
        this.function = function;
    }

    /**
     * 设置执行函数
     *
     * @return sa路由拦截器
     */
    public static TakeshiInterceptor newInstance() {
        return new TakeshiInterceptor();
    }

    /**
     * 设置执行函数
     *
     * @param function 自定义模式下的执行函数
     * @return sa路由拦截器
     */
    public static TakeshiInterceptor newInstance(SaRouteFunction function) {
        return new TakeshiInterceptor(function);
    }

    /**
     * 每次请求之前触发的方法
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (handler instanceof HandlerMethod handlerMethod) {
                String userAgent = request.getHeader(Header.USER_AGENT.getValue());
                String timestamp = request.getHeader(TakeshiConstants.TIMESTAMP_NAME);
                String clientIp = TakeshiUtil.getClientIp(request);
                Object loginId = StpUtil.getLoginIdDefaultNull();
                Method method = handlerMethod.getMethod();
                log.info("请求开始, 请求IP: {}, 请求工具: {}, timestamp: {}", clientIp, userAgent, timestamp);
                log.info("请求的用户ID: {}, 请求地址: {}, 请求方法: [{}] {}.{}", loginId, request.getRequestURL(), request.getMethod(), method.getDeclaringClass().getName(), method.getName());

                SystemSecurity systemSecurity = Optional.ofNullable(handlerMethod.getMethodAnnotation(SystemSecurity.class))
                        .orElse(handlerMethod.getBeanType().getAnnotation(SystemSecurity.class));
                // 速率限制
                this.rateLimit(request, handlerMethod, userAgent, clientIp, loginId, systemSecurity);
                if (ObjUtil.isNull(systemSecurity) || (!systemSecurity.all() && !systemSecurity.token())) {
                    // 执行token认证函数
                    function.run(new SaRequestForServlet(request), new SaResponseForServlet(response), handlerMethod);
                }
                // 注解式鉴权，对角色和权限进行验证，需要实现StpInterface接口
                SaStrategy.me.checkMethodAnnotation.accept(method);
            }
        } catch (StopMatchException e) {
            // 停止匹配，进入Controller
        } catch (BackResultException e) {
            // 停止匹配，向前端输出结果
            response.setCharacterEncoding(CharsetUtil.UTF_8);
            response.setContentType(ContentType.JSON.getValue());
            response.setStatus(HttpStatus.HTTP_OK);
            String str;
            if (e.result instanceof ResponseDataVO.ResBean resBean) {
                str = GsonUtil.toJson(ResponseDataVO.success(resBean));
            } else {
                str = e.getMessage();
            }
            log.error("TakeshiInterceptor.preHandle --> 请求URL: " + request.getRequestURL() + ", 接口验证错误: " + str, e);
            response.getWriter().write(str);
            return false;
        }
        // 通过验证
        return true;
    }

    /**
     * 速率限制
     *
     * @param request        request
     * @param handlerMethod  handlerMethod
     * @param userAgent      userAgent
     * @param clientIp       clientIp
     * @param loginId        loginId
     * @param systemSecurity systemSecurity
     */
    private void rateLimit(HttpServletRequest request, HandlerMethod handlerMethod, String userAgent,
                           String clientIp, Object loginId, SystemSecurity systemSecurity) throws IOException {
        TakeshiProperties takeshiProperties = StaticConfig.takeshiProperties;
        boolean platform = false;
        boolean signature = false;
        if (ObjUtil.isNotNull(systemSecurity)) {
            platform = systemSecurity.all() || systemSecurity.platform();
            signature = systemSecurity.all() || systemSecurity.signature();
        }
        if (takeshiProperties.isAppPlatform() && !platform && !UserAgentUtil.parse(userAgent).isMobile()) {
            // 移动端请求工具校验
            SaRouter.back(TakeshiCode.USERAGENT_ERROR);
        }
        RateLimitProperties rate = takeshiProperties.getRate();
        String timestamp = request.getHeader(TakeshiConstants.TIMESTAMP_NAME);
        String nonce = request.getHeader(TakeshiConstants.NONCE_NAME);
        String servletPath = request.getServletPath();

        String ipBlacklistKey = TakeshiRedisKeyEnum.IP_BLACKLIST.projectKey(clientIp);
        if (StaticConfig.redisComponent.hasKey(ipBlacklistKey)) {
            // 黑名单中的IP
            SaRouter.back(TakeshiCode.RATE_LIMIT);
        }

        if (rate.getMaxTimeDiff() > 0) {
            if (StrUtil.isBlank(timestamp)) {
                SaRouter.back(TakeshiCode.PARAMETER_ERROR);
            }
            long seconds = Duration.between(Instant.ofEpochMilli(Long.parseLong(timestamp)), Instant.now()).getSeconds();
            if (seconds > rate.getMaxTimeDiff() || seconds < TakeshiConstants.LONGS[0]) {
                // 请求时间与当前时间相差过早
                SaRouter.back(TakeshiCode.SIGN_ERROR);
            }
        }

        RateLimitProperties.NonceRate nonceRate = rate.getNonce();
        if (nonceRate.getRateInterval() > 0) {
            String nonceRateLimitKey = TakeshiRedisKeyEnum.NONCE_RATE_LIMIT.projectKey(clientIp, servletPath);
            RRateLimiter nonceRateLimiter = StaticConfig.redisComponent.getRateLimiter(nonceRateLimitKey);
            nonceRateLimiter.trySetRate(RateType.PER_CLIENT, nonceRate.getRate(), nonceRate.getRateInterval(), nonceRate.getRateIntervalUnit());
            if (!nonceRateLimiter.tryAcquire()) {
                // nonce重复使用
                SaRouter.back(TakeshiCode.RATE_LIMIT);
            }
        }

        RepeatSubmit repeatSubmit = handlerMethod.getMethodAnnotation(RepeatSubmit.class);
        RateLimitProperties.IpRate ipRate = rate.getIp();
        if (ObjUtil.isNotNull(repeatSubmit) && repeatSubmit.ipRateInterval() > 0) {
            // 通过RepeatSubmit注解的值重新设定当前接口的IP限制速率
            ipRate.setRate(repeatSubmit.ipRate());
            ipRate.setRateInterval(repeatSubmit.ipRateInterval());
            ipRate.setRateIntervalUnit(repeatSubmit.ipRateIntervalUnit());
            ipRate.setOpenBlacklist(repeatSubmit.ipRateOpenBlacklist());
        }
        if (ipRate.getRateInterval() > 0) {
            String ipRateLimitKey = TakeshiRedisKeyEnum.IP_RATE_LIMIT.projectKey(clientIp, servletPath);
            RRateLimiter ipRateLimiter = StaticConfig.redisComponent.getRateLimiter(ipRateLimitKey);
            // 接口IP限流
            ipRateLimiter.trySetRate(RateType.PER_CLIENT, ipRate.getRate(), ipRate.getRateInterval(), ipRate.getRateIntervalUnit());
            if (!ipRateLimiter.tryAcquire()) {
                if (ipRate.isOpenBlacklist()) {
                    // 超过请求次数则将IP加入黑名单到当天结束时间释放（例如：2023-04-23 23:59:59）
                    StaticConfig.redisComponent.saveMidnight(ipBlacklistKey, Instant.now().toString());
                }
                SaRouter.back(TakeshiCode.RATE_LIMIT);
            }
        }

        ParamBO paramBO = this.getParamBO(request);
        String paramBOJsonString = paramBO.toJsonString();
        log.info("请求参数: {}", paramBOJsonString);

        String signatureKey = takeshiProperties.getSignatureKey();
        if (StrUtil.isNotBlank(signatureKey) && !signature) {
            // 校验参数签名，如果body参数是非JsonObject值，则直接将值与其他值直接拼接
            String sign = request.getHeader(TakeshiConstants.SIGN_NAME);
            String signParamsMd5 = SecureUtil.signParamsMd5(paramBO.getParamMap(), StrUtil.toStringOrNull(paramBO.getBodyOther()), signatureKey, nonce, timestamp);
            if (!StrUtil.equals(sign, signParamsMd5)) {
                // 签名验证错误
                SaRouter.back(TakeshiCode.SIGN_ERROR);
            }
        }

        if (ObjUtil.isNotNull(repeatSubmit) && repeatSubmit.rateInterval() > 0) {
            ResponseDataVO.ResBean resBean = TakeshiCode.REPEAT_SUBMIT;
            long rateInterval = repeatSubmit.rateInterval();
            if (StrUtil.isNotBlank(repeatSubmit.msg())) {
                resBean.setInfo(repeatSubmit.msg());
            }
            Map<String, Object> map = new HashMap<>(8);
            map.put("repeatUrl", servletPath);
            map.put("repeatLoginId", loginId);
            JsonNode jsonNode = StaticConfig.objectMapper.readTree(paramBOJsonString);
            List<String> ignoredFieldList = Arrays.asList(repeatSubmit.ignoredFieldNames());
            ignoredFieldList.forEach(fieldName -> {
                jsonNode.findParents(fieldName)
                        .forEach(parent -> {
                            ObjectNode parentNode = (ObjectNode) parent;
                            parentNode.remove(fieldName);
                        });
            });
            map.put("repeatParams", jsonNode);
            String repeatSubmitKey = TakeshiRedisKeyEnum.REPEAT_SUBMIT.projectKey(SecureUtil.md5(GsonUtil.toJson(map)));
            RRateLimiter rateLimiter = StaticConfig.redisComponent.getRateLimiter(repeatSubmitKey);
            // 限制xx毫秒1次
            rateLimiter.trySetRate(RateType.PER_CLIENT, 1, rateInterval, repeatSubmit.rateIntervalUnit());
            if (!rateLimiter.tryAcquire()) {
                SaRouter.back(resBean);
            }
        }
    }

    /**
     * 获取所有参数，包装成一个对象
     *
     * @param request request
     * @return ParamBO
     */
    private ParamBO getParamBO(HttpServletRequest request) throws IOException {
        ParamBO paramBO = new ParamBO();
        paramBO.setUrlParam(JakartaServletUtil.getParamMap(request));
        Object attribute = request.getAttribute(TakeshiConstants.MULTIPART_REQUEST);
        // 从request移除该值，因为后续都不会使用到这个值，在后续的request传递中会有一点点浪费内存🤏
        request.removeAttribute(TakeshiConstants.MULTIPART_REQUEST);
        if (JakartaServletUtil.isPostMethod(request)
                && attribute instanceof StandardMultipartHttpServletRequest multipartRequest) {
            MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();
            Map<String, List<String>> multipartData = multiFileMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue()
                                            .stream()
                                            .map(Either.warp(v -> SecureUtil.md5(v.getInputStream())))
                                            .collect(Collectors.toList())
                            )
                    );
            paramBO.setMultipartData(multipartData);
            Map<String, String> multipart = multiFileMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue()
                                            .stream()
                                            .map(multipartFile -> StrUtil.builder(multipartFile.getOriginalFilename(), StrUtil.BRACKET_START, DataSizeUtil.format(multipartFile.getSize()), StrUtil.BRACKET_END))
                                            .collect(Collectors.joining(StrUtil.COMMA))
                            )
                    );
            paramBO.setMultipart(multipart);
        } else if (!JakartaServletUtil.isGetMethod(request)) {
            paramBO.setBody(request.getInputStream());
        }
        return paramBO;
    }

}
