package com.takeshi.config.satoken;

import cn.dev33.satoken.router.SaRouteFunction;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgentUtil;
import com.takeshi.annotation.RepeatSubmit;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.annotation.TakeshiLog;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.properties.RateLimitProperties;
import com.takeshi.config.properties.TakeshiProperties;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.enums.TakeshiRedisKeyEnum;
import com.takeshi.pojo.bo.IpBlackInfoBO;
import com.takeshi.pojo.bo.ParamBO;
import com.takeshi.pojo.bo.RetBO;
import com.takeshi.util.GsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            ParamBO paramBO = (ParamBO) request.getAttribute(TakeshiConstants.PARAM_BO);
            String methodName = StrUtil.builder(method.getDeclaringClass().getName(), StrUtil.DOT, method.getName()).toString();
            paramBO.setMethodName(methodName);
            paramBO.setTakeshiLog(method.getAnnotation(TakeshiLog.class));
            log.info("TakeshiInterceptor.preHandle --> Request Delivery:\nRequest Http Method: {}", StrUtil.builder(StrUtil.BRACKET_START, paramBO.getHttpMethod(), StrUtil.BRACKET_END, methodName));
            log.info("Request Parameters: {}", paramBO.getParamObjectNode());
            // 速率限制
            SystemSecurity systemSecurity = this.rateLimit(request, handlerMethod, paramBO);
            if (ObjUtil.isNull(systemSecurity) || (!systemSecurity.all() && !systemSecurity.token())) {
                // 执行token认证函数
                function.run(new SaRequestForServlet(request), new SaResponseForServlet(response), handlerMethod);
            }
            // 注解式鉴权，对角色和权限进行验证，需要实现StpInterface接口
            SaStrategy.me.checkMethodAnnotation.accept(method);
        }
        // 通过验证
        return true;
    }

    /**
     * 速率限制
     *
     * @param request       request
     * @param handlerMethod handlerMethod
     * @param paramBO       paramBO
     */
    private SystemSecurity rateLimit(HttpServletRequest request, HandlerMethod handlerMethod, ParamBO paramBO) {
        SystemSecurity systemSecurity = Optional.ofNullable(handlerMethod.getMethodAnnotation(SystemSecurity.class))
                .orElse(handlerMethod.getBeanType().getAnnotation(SystemSecurity.class));
        String clientIp = paramBO.getClientIp();
        TakeshiProperties takeshiProperties = StaticConfig.takeshiProperties;
        boolean passPlatform = false;
        boolean passSignature = false;
        if (ObjUtil.isNotNull(systemSecurity)) {
            passPlatform = systemSecurity.all() || systemSecurity.platform();
            passSignature = systemSecurity.all() || systemSecurity.signature();
        }
        if (takeshiProperties.isAppPlatform() && !passPlatform && !UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.getValue())).isMobile()) {
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
        String signatureKey = takeshiProperties.getSignatureKey();
        // 开启了sign校验
        boolean signVerify = StrUtil.isNotBlank(signatureKey) && !passSignature;
        RateLimitProperties.NonceRate nonceRate = rate.getNonce();
        if (signVerify && nonceRate.getRateInterval() > 0) {
            String nonceRateLimitKey = TakeshiRedisKeyEnum.NONCE_RATE_LIMIT.projectKey(nonce);
            RRateLimiter nonceRateLimiter = StaticConfig.redisComponent.getRateLimiter(nonceRateLimitKey);
            nonceRateLimiter.trySetRate(RateType.PER_CLIENT, nonceRate.getRate(), nonceRate.getRateInterval(), nonceRate.getRateIntervalUnit());
            nonceRateLimiter.expire(Duration.ofMillis(nonceRate.getRateIntervalUnit().toMillis(nonceRate.getRateInterval())));
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
            String ipRateLimitKey = TakeshiRedisKeyEnum.IP_RATE_LIMIT.projectKey(clientIp);
            RRateLimiter ipRateLimiter = StaticConfig.redisComponent.getRateLimiter(ipRateLimitKey);
            // 接口IP限流
            ipRateLimiter.trySetRate(RateType.PER_CLIENT, ipRate.getRate(), ipRate.getRateInterval(), ipRate.getRateIntervalUnit());
            ipRateLimiter.expire(Duration.ofMillis(ipRate.getRateIntervalUnit().toMillis(ipRate.getRateInterval())));
            if (!ipRateLimiter.tryAcquire()) {
                if (ipRate.isOpenBlacklist()) {
                    // 超过请求次数则将IP加入黑名单到当天结束时间释放（例如：2023-04-23 23:59:59）
                    IpBlackInfoBO ipBlackInfoBO = new IpBlackInfoBO(servletPath, Instant.now());
                    StaticConfig.redisComponent.saveMidnight(ipBlacklistKey, GsonUtil.toJson(ipBlackInfoBO));
                }
                SaRouter.back(TakeshiCode.RATE_LIMIT);
            }
        }

        if (signVerify) {
            // 校验参数签名，如果body参数是非JsonObject值，则直接将值与其他值直接拼接
            String sign = request.getHeader(TakeshiConstants.SIGN_NAME);
            String signParamsMd5 = SecureUtil.signParamsMd5(paramBO.getParamMap(), StrUtil.toStringOrNull(paramBO.getBodyOther()), signatureKey, nonce, timestamp);
            if (!StrUtil.equals(sign, signParamsMd5)) {
                // 签名验证错误
                SaRouter.back(TakeshiCode.SIGN_ERROR);
            }
        }

        if (ObjUtil.isNotNull(repeatSubmit) && repeatSubmit.rateInterval() > 0) {
            RetBO retBO = TakeshiCode.REPEAT_SUBMIT;
            long rateInterval = repeatSubmit.rateInterval();
            if (StrUtil.isNotBlank(repeatSubmit.msg())) {
                retBO.setMessage(repeatSubmit.msg());
            }
            Map<String, Object> map = new HashMap<>(8);
            map.put("repeatUrl", servletPath);
            map.put("repeatLoginId", paramBO.getLoginId());
            map.put("repeatParams", paramBO.getParamObjectNode(repeatSubmit.exclusionFieldName()));
            String repeatSubmitKey = TakeshiRedisKeyEnum.REPEAT_SUBMIT.projectKey(SecureUtil.md5(GsonUtil.toJson(map)));
            RRateLimiter rateLimiter = StaticConfig.redisComponent.getRateLimiter(repeatSubmitKey);
            // 限制xx毫秒1次
            rateLimiter.trySetRate(RateType.PER_CLIENT, 1, rateInterval, repeatSubmit.rateIntervalUnit());
            rateLimiter.expire(Duration.ofMillis(repeatSubmit.rateIntervalUnit().toMillis(rateInterval)));
            if (!rateLimiter.tryAcquire()) {
                SaRouter.back(retBO);
            }
        }
        return systemSecurity;
    }

}
