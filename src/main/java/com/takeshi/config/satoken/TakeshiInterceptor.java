package com.takeshi.config.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.exception.SaSignException;
import cn.dev33.satoken.fun.SaParamFunction;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.sign.SaSignUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgentUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.takeshi.annotation.RepeatSubmit;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.annotation.TakeshiLog;
import com.takeshi.config.properties.TakeshiProperties;
import com.takeshi.constants.RequestConstants;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.enums.TakeshiRedisKeyEnum;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.pojo.bo.IpBlackInfoBO;
import com.takeshi.pojo.bo.RetBO;
import com.takeshi.util.GsonUtil;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TakeshiInterceptor
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public class TakeshiInterceptor implements HandlerInterceptor {

    private final Duration DURATION = Duration.ofDays(7);

    /**
     * 认证函数：每次请求执行
     * <p> 参数：路由处理函数指针
     */
    public SaParamFunction<Object> auth = handler -> {
    };


    /**
     * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力
     */
    public TakeshiInterceptor() {
    }

    /**
     * 创建一个 Sa-Token 综合拦截器，默认带有注解鉴权能力
     *
     * @param auth 认证函数，每次请求执行
     */
    private TakeshiInterceptor(SaParamFunction<Object> auth) {
        this.auth = auth;
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
     * @param auth 自定义模式下的执行函数
     * @return sa路由拦截器
     */
    public static TakeshiInterceptor newInstance(SaParamFunction<Object> auth) {
        return new TakeshiInterceptor(auth);
    }

    /**
     * 排除敏感属性字段
     */
    private final String[] EXCLUSION_FIELD_NAME = {"password", "oldPassword", "newPassword", "confirmPassword"};

    /**
     * Interception point before the execution of a handler. Called after
     * HandlerMapping determined an appropriate handler object, but before
     * HandlerAdapter invokes the handler.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can decide to abort the execution chain,
     * typically sending an HTTP error or writing a custom response.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation returns {@code true}.
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
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            String methodName = StrUtil.builder(method.getDeclaringClass().getName(), StrUtil.DOT, method.getName()).toString();
            request.setAttribute(RequestConstants.METHOD_NAME, methodName);
            TakeshiLog takeshiLog = method.getAnnotation(TakeshiLog.class);
            log.info("TakeshiInterceptor.preHandle --> Request Http Method: {}", StrUtil.builder(StrUtil.BRACKET_START, request.getMethod(), StrUtil.BRACKET_END, methodName));
            ObjectMapper objectMapper = SpringUtil.getBean(ObjectMapper.class);
            Map<String, String> urlParam = JakartaServletUtil.getParamMap(request);
            Map<String, String> multipart = null;
            Object bodyObject = null;
            if (HttpMethod.POST.matches(request.getMethod())
                    && request instanceof StandardMultipartHttpServletRequest multipartRequest) {
                MultiValueMap<String, MultipartFile> multiFileMap = multipartRequest.getMultiFileMap();
                multipart = multiFileMap.entrySet()
                                        .stream()
                                        .collect(Collectors.toMap(
                                                         Map.Entry::getKey,
                                                         entry -> entry.getValue()
                                                                       .stream()
                                                                       .map(multipartFile -> StrUtil.builder(multipartFile.getOriginalFilename(), StrUtil.BRACKET_START, DataSizeUtil.format(multipartFile.getSize()), StrUtil.BRACKET_END))
                                                                       .collect(Collectors.joining(StrUtil.COMMA))
                                                 )
                                        );
            } else if (!HttpMethod.GET.matches(request.getMethod())) {
                JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
                if (!jsonNode.isNull()) {
                    if (jsonNode.isObject()) {
                        bodyObject = objectMapper.<Map<String, Object>>convertValue(jsonNode, new TypeReference<>() {
                        });
                    } else if (jsonNode.isArray()) {
                        bodyObject = objectMapper.<Collection<Object>>convertValue(jsonNode, new TypeReference<>() {
                        });
                    } else if (jsonNode.isTextual()) {
                        bodyObject = jsonNode.textValue();
                    } else if (jsonNode.isNumber()) {
                        bodyObject = jsonNode.numberValue();
                    } else if (jsonNode.isBoolean()) {
                        bodyObject = jsonNode.booleanValue();
                    } else {
                        bodyObject = jsonNode.toString();
                    }
                }
            }
            ObjectNode paramObjectNode = objectMapper.createObjectNode();
            if (CollUtil.isNotEmpty(urlParam)) {
                paramObjectNode.putPOJO("urlParam", urlParam);
            }
            if (CollUtil.isNotEmpty(multipart)) {
                paramObjectNode.putPOJO("multipart", multipart);
            }
            if (ObjUtil.isNotEmpty(bodyObject)) {
                paramObjectNode.putPOJO("bodyObject", bodyObject);
            }
            String paramObjectValue = objectMapper.writeValueAsString(paramObjectNode);
            TakeshiProperties takeshiProperties = SpringUtil.getBean(TakeshiProperties.class);
            if (takeshiProperties.isEnableRequestParamLog()) {
                log.info("Request Parameters: {}", paramObjectValue);
            }
            if (ObjUtil.isNotNull(takeshiLog)) {
                String[] exclusionFieldName = Stream.of(EXCLUSION_FIELD_NAME, takeshiLog.exclusionFieldName()).flatMap(Arrays::stream).toArray(String[]::new);
                for (String fieldName : exclusionFieldName) {
                    paramObjectNode.findParents(fieldName).forEach(item -> ((ObjectNode) item).remove(fieldName));
                }
                request.setAttribute(RequestConstants.TAKESHI_LOG, takeshiLog);
                request.setAttribute(RequestConstants.PARAM_OBJECT_VALUE, objectMapper.writeValueAsString(paramObjectNode));
            }
            // 速率限制
            SystemSecurity systemSecurity = this.rateLimit(request, handlerMethod, objectMapper, objectMapper.readValue(paramObjectValue, ObjectNode.class), takeshiProperties);
            if (ObjUtil.isNull(systemSecurity) || (!systemSecurity.all() && !systemSecurity.token())) {
                // 执行token认证函数
                auth.run(handlerMethod);
            }
            // 注解式鉴权，对角色和权限进行验证，需要实现StpInterface接口
            SaStrategy.instance.checkMethodAnnotation.accept(method);
        }
        // 通过验证
        return true;
    }

    /**
     * 速率限制
     *
     * @param request           request
     * @param handlerMethod     handlerMethod
     * @param objectMapper      objectMapper
     * @param paramObjectNode   paramObjectNode
     * @param takeshiProperties takeshiProperties
     */
    private SystemSecurity rateLimit(HttpServletRequest request, HandlerMethod handlerMethod, ObjectMapper objectMapper, ObjectNode paramObjectNode, TakeshiProperties takeshiProperties) throws Exception {
        SystemSecurity systemSecurity = Optional.ofNullable(handlerMethod.getMethodAnnotation(SystemSecurity.class))
                                                .orElse(handlerMethod.getBeanType().getAnnotation(SystemSecurity.class));
        String clientIp = (String) request.getAttribute(RequestConstants.CLIENT_IP);
        boolean passPlatform = false, passSignature = false, passTimestamp = true;
        if (ObjUtil.isNotNull(systemSecurity)) {
            passPlatform = systemSecurity.all() || systemSecurity.platform();
            passSignature = systemSecurity.all() || systemSecurity.signature();
            passTimestamp = systemSecurity.all() || systemSecurity.timestamp();
        }
        if (takeshiProperties.isAppPlatform() && !passPlatform && !UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.getValue())).isMobile()) {
            // 移动端请求工具校验
            SaRouter.back(ResponseData.retData(TakeshiCode.USERAGENT_ERROR));
        }
        // 获取方法上的RepeatSubmit注解
        RepeatSubmit repeatSubmit = handlerMethod.getMethodAnnotation(RepeatSubmit.class);
        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        Object loginId = request.getAttribute(RequestConstants.LOGIN_ID);
        String ipBlacklistKey = TakeshiRedisKeyEnum.IP_BLACKLIST.projectKey(clientIp);
        RedissonClient redissonClient = SpringUtil.getBean(RedissonClient.class);
        // ip速率校验
        this.verifyIp(redissonClient, repeatSubmit, takeshiProperties.isOpenIpBlacklist(), clientIp, httpMethod, requestURI, ipBlacklistKey);
        // sign校验
        this.verifySign(passSignature, passTimestamp, new SaRequestForServlet(request));
        // 重复提交校验
        this.verifyRepeatSubmit(redissonClient, repeatSubmit, objectMapper, clientIp, httpMethod, requestURI, loginId, paramObjectNode);
        return systemSecurity;
    }

    /**
     * ip速率校验
     *
     * @param repeatSubmit    注解
     * @param openIpBlacklist 是否开启IP黑名单
     * @param clientIp        客户端IP
     * @param httpMethod      接口方法类型
     * @param requestURI      接口路径
     * @param ipBlacklistKey  是否开启了黑名单
     */
    private void verifyIp(RedissonClient redissonClient, @Nullable RepeatSubmit repeatSubmit, boolean openIpBlacklist,
                          String clientIp, String httpMethod, String requestURI, String ipBlacklistKey) {
        if (ObjUtil.isNotNull(repeatSubmit) && repeatSubmit.ipRateInterval() > 0) {
            // 通过RepeatSubmit注解的值重新设定当前接口的IP限制速率
            int iRate = repeatSubmit.ipRate();
            int iRateInterval = repeatSubmit.ipRateInterval();
            RateIntervalUnit iRateIntervalUnit = repeatSubmit.ipRateIntervalUnit();
            if (openIpBlacklist && redissonClient.getBucket(ipBlacklistKey).isExists()) {
                // 是黑名单中的IP，禁止访问
                SaRouter.back(ResponseData.retData(TakeshiCode.BLACK_LIST_RATE_LIMIT));
            }
            String ipRateLimitKey = TakeshiRedisKeyEnum.IP_RATE_LIMIT.projectKey(clientIp, httpMethod, requestURI);
            RRateLimiter ipRateLimiter = redissonClient.getRateLimiter(ipRateLimitKey);
            // 接口IP限流
            ipRateLimiter.trySetRate(RateType.PER_CLIENT, iRate, iRateInterval, iRateIntervalUnit);
            // 设置限流器过期时间
            ipRateLimiter.expire(DURATION);
            if (!ipRateLimiter.tryAcquire()) {
                if (openIpBlacklist) {
                    IpBlackInfoBO.IpRate ipBlackInfoIpRate = new IpBlackInfoBO.IpRate(iRate, iRateInterval, iRateIntervalUnit);
                    // 超过请求次数则将IP加入黑名单内24小时
                    IpBlackInfoBO ipBlackInfoBO = new IpBlackInfoBO(clientIp, httpMethod, requestURI, ipBlackInfoIpRate, Instant.now());
                    redissonClient.getBucket(ipBlacklistKey).set(ipBlackInfoBO, Duration.ofHours(24));
                }
                SaRouter.back(ResponseData.retData(TakeshiCode.RATE_LIMIT));
            }
        }
    }

    /**
     * sign校验
     *
     * @param passSignature 是否放弃校验参数签名，如果需要校验参数签名，那么一定就会校验客户端的时间戳
     * @param passTimestamp 是否放弃校验客户端的时间戳，只有放弃校验参数签名的情况下才会判断是否校验客户端的时间戳
     * @param saRequest     saRequest
     */
    private void verifySign(boolean passSignature, boolean passTimestamp, SaRequest saRequest) {
        if (!passSignature && StrUtil.isNotBlank(SaManager.getSaSignTemplate().getSecretKey())) {
            SaSignUtil.checkRequest(saRequest);
        } else if (!passTimestamp) {
            String timestampValue = saRequest.getHeader(RequestConstants.Header.TIMESTAMP);
            SaSignException.notEmpty(timestampValue, "Missing timestamp field");
            SaSignUtil.checkTimestamp(Long.parseLong(timestampValue));
        }
    }

    /**
     * 重复提交校验
     *
     * @param repeatSubmit    注解
     * @param objectMapper    objectMapper
     * @param clientIp        客户端IP
     * @param httpMethod      接口方法类型
     * @param servletPath     接口路径
     * @param loginId         登陆用户ID
     * @param paramObjectNode 请求的参数
     * @throws JsonProcessingException JsonProcessingException
     */
    private void verifyRepeatSubmit(RedissonClient redissonClient, @Nullable RepeatSubmit repeatSubmit,
                                    ObjectMapper objectMapper, String clientIp, String httpMethod, String servletPath,
                                    Object loginId, ObjectNode paramObjectNode) throws JsonProcessingException {
        if (ObjUtil.isNotNull(repeatSubmit) && repeatSubmit.rateInterval() > 0) {
            RetBO retBO = TakeshiCode.REPEAT_SUBMIT.cloneWithMessage(repeatSubmit.msg());
            long rateInterval = repeatSubmit.rateInterval();
            Map<String, Object> map = new HashMap<>(8);
            map.put("repeatIp", clientIp);
            map.put("repeatMethod", httpMethod);
            map.put("repeatUrl", servletPath);
            map.put("repeatLoginId", loginId);
            if (ArrayUtil.isNotEmpty(repeatSubmit.exclusionFieldName())) {
                for (String fieldName : repeatSubmit.exclusionFieldName()) {
                    paramObjectNode.findParents(fieldName).forEach(item -> ((ObjectNode) item).remove(fieldName));
                }
            }
            map.put("repeatParams", objectMapper.writeValueAsString(paramObjectNode));
            String repeatSubmitKey = TakeshiRedisKeyEnum.REPEAT_SUBMIT.projectKey(SecureUtil.md5(GsonUtil.toJson(map)));
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(repeatSubmitKey);
            // 限制xx毫秒1次
            rateLimiter.trySetRate(RateType.PER_CLIENT, 1, rateInterval, repeatSubmit.rateIntervalUnit());
            // 设置限流器过期时间
            rateLimiter.expire(DURATION);
            if (!rateLimiter.tryAcquire()) {
                SaRouter.back(ResponseData.retData(retBO));
            }
        }
    }

}
