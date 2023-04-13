package com.takeshi.config.satoken;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.router.SaRouteFunction;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgentUtil;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.config.StaticConfig;
import com.takeshi.constants.SysCode;
import com.takeshi.constants.SysConstants;
import com.takeshi.pojo.vo.ResponseDataVO;
import com.takeshi.util.GsonUtil;
import com.takeshi.util.TakeshiUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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


    // ----------------- 验证方法 -----------------

    /**
     * 每次请求之前触发的方法
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return boolean
     * @throws Exception Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if (handler instanceof HandlerMethod handlerMethod) {
                StrBuilder name = StrUtil.strBuilder(StaticConfig.applicationName, StrUtil.COLON, TakeshiUtil.getClientIp(request), StrUtil.COLON, request.getServletPath());
                RRateLimiter rateLimiter = StaticConfig.redissonClient.getRateLimiter(name.toString());
                // 接口IP限流一秒钟30次
                rateLimiter.trySetRate(RateType.PER_CLIENT, 30, 1, RateIntervalUnit.SECONDS);
                if (!rateLimiter.tryAcquire()) {
                    throw new BackResultException(SysCode.RATE_LIMIT);
                }
                if (this.verifySystemSecurity(request, handlerMethod)) {
                    // 执行token认证函数
                    function.run(new SaRequestForServlet(request), new SaResponseForServlet(response), handler);
                }
                // 注解式鉴权，对角色和权限进行验证，需要实现StpInterface接口
                Method method = ((HandlerMethod) handler).getMethod();
                SaStrategy.me.checkMethodAnnotation.accept(method);
            }
        } catch (StopMatchException e) {
            // 停止匹配，进入Controller
        } catch (BackResultException e) {
            // 停止匹配，向前端输出结果
            response.setCharacterEncoding(CharsetUtil.UTF_8);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.OK.value());
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
     * 校验一些值
     *
     * @param request       request
     * @param handlerMethod handlerMethod
     * @return 是否需要执行token认证函数
     */
    private boolean verifySystemSecurity(HttpServletRequest request, HandlerMethod handlerMethod) {
        SystemSecurity methodAnnotation = handlerMethod.getMethodAnnotation(SystemSecurity.class);
        if (Objects.nonNull(methodAnnotation)) {
            this.saRouterBack(request, methodAnnotation.all(), methodAnnotation.platform(), methodAnnotation.signature());
            return !methodAnnotation.all() && !methodAnnotation.token();
        }
        SystemSecurity classAnnotation = AnnotationUtil.getAnnotation(handlerMethod.getBeanType(), SystemSecurity.class);
        if (Objects.nonNull(classAnnotation)) {
            this.saRouterBack(request, classAnnotation.all(), classAnnotation.platform(), classAnnotation.signature());
            return !classAnnotation.all() && !classAnnotation.token();
        }
        return true;
    }

    /**
     * 自定义校验
     *
     * @param request   request
     * @param all       all
     * @param platform  platform
     * @param signature signature
     */
    private void saRouterBack(HttpServletRequest request, boolean all, boolean platform, boolean signature) {
        if (!all) {
            if (!platform && StaticConfig.takeshiProperties.isAppPlatform()
                    && !UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.getValue())).isMobile()) {
                // 移动端请求工具校验不通过
                SaRouter.back(SysCode.USERAGENT_ERROR);
            }
            if (!signature && StaticConfig.takeshiProperties.isSignature()) {
                // 参数签名校验不通过
                String sign = request.getHeader(SysConstants.SIGN_NAME);
                String timestamp = request.getHeader(SysConstants.TIMESTAMP_NAME);
                if (StrUtil.isBlank(timestamp)) {
                    SaRouter.back(SysCode.PARAMETER_ERROR);
                }
                String signParams = TakeshiUtil.signParams(request, timestamp);
                if (StrUtil.isNotBlank(signParams) && !StrUtil.equals(sign, signParams)) {
                    SaRouter.back(SysCode.SIGN_ERROR);
                }
                // 接口调用时间必须早于接口接收时间，且在5秒内
                long between = ChronoUnit.SECONDS.between(Instant.ofEpochMilli(Long.parseLong(timestamp)), Instant.now());
                if (between > SysConstants.LONGS[5] || between < SysConstants.LONGS[0]) {
                    SaRouter.back(SysCode.SIGN_ERROR);
                }
            }
        }
    }

}
