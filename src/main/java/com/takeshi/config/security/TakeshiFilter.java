package com.takeshi.config.security;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.takeshi.component.TakeshiAsyncComponent;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.pojo.bo.ParamBO;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.stream.Stream;

/**
 * TakeshiFilter
 */
@Slf4j
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TakeshiFilter implements Filter {

    private final TakeshiAsyncComponent takeshiAsyncComponent;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AntPathMatcher antPathMatcher = Singleton.get(AntPathMatcher.class.getName(), AntPathMatcher::new);
        if (request instanceof HttpServletRequest httpServletRequest
                && response instanceof HttpServletResponse httpServletResponse
                && Stream.of(TakeshiConstants.EXCLUDE_SWAGGER_URL).noneMatch(item -> antPathMatcher.match(item, httpServletRequest.getServletPath()))) {
            long startTimeMillis = Instant.now().toEpochMilli();
            String traceId = IdUtil.fastSimpleUUID();
            // 填充traceId
            MDC.put(TakeshiConstants.TRACE_ID_KEY, traceId);
            StopWatch stopWatch = new StopWatch(traceId);
            stopWatch.start();
            // 获取当前账号ID
            Object loginId = StpUtil.getLoginIdDefaultNull();
            StandardServletMultipartResolver standardServletMultipartResolver = new StandardServletMultipartResolver();
            HttpServletRequest takeshiHttpRequestWrapper;
            if (standardServletMultipartResolver.isMultipart(httpServletRequest)) {
                takeshiHttpRequestWrapper = standardServletMultipartResolver.resolveMultipart(httpServletRequest);
                takeshiHttpRequestWrapper.setAttribute(TakeshiConstants.MULTIPART_REQUEST, takeshiHttpRequestWrapper);
            } else {
                takeshiHttpRequestWrapper = new TakeshiHttpRequestWrapper(httpServletRequest);
            }
            TakeshiHttpResponseWrapper takeshiHttpResponseWrapper = new TakeshiHttpResponseWrapper(httpServletResponse);
            chain.doFilter(takeshiHttpRequestWrapper, takeshiHttpResponseWrapper);
            // 获取 takeshiHttpResponseWrapper 的返回值
            byte[] bytes = takeshiHttpResponseWrapper.getResponseData();
            String responseData = StrUtil.str(bytes, StandardCharsets.UTF_8);
            log.info("Response Data: {}", responseData);
            // 将响应内容写回到原始的 HttpServletResponse 中
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.info("End Of Response, Time Consuming: {} ms", totalTimeMillis);
            // 新增一条接口请求相关信息到数据库
            takeshiAsyncComponent.insertSysLog((ParamBO) takeshiHttpRequestWrapper.getAttribute(TakeshiConstants.PARAM_BO), startTimeMillis, totalTimeMillis, responseData);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 请求结束时删除数据，否则会造成内存溢出
        MDC.remove(TakeshiConstants.TRACE_ID_KEY);
    }

}
