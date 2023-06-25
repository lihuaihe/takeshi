package com.takeshi.config.security;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.IdUtil;
import com.takeshi.constants.TakeshiConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * TakeshiFilter
 */
@Slf4j
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class TakeshiFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AntPathMatcher antPathMatcher = Singleton.get(AntPathMatcher.class.getName(), AntPathMatcher::new);
        if (request instanceof HttpServletRequest httpServletRequest
                && Stream.of(TakeshiConstants.EXCLUDE_SWAGGER_URL).noneMatch(item -> antPathMatcher.match(item, httpServletRequest.getServletPath()))) {
            String uuid = IdUtil.fastSimpleUUID();
            // 填充traceId
            MDC.put(TakeshiConstants.TRACE_ID_KEY, uuid);
            StopWatch stopWatch = new StopWatch(uuid);
            stopWatch.start();
            StandardServletMultipartResolver standardServletMultipartResolver = new StandardServletMultipartResolver();
            if (standardServletMultipartResolver.isMultipart(httpServletRequest)) {
                request = standardServletMultipartResolver.resolveMultipart(httpServletRequest);
                request.setAttribute(TakeshiConstants.MULTIPART_REQUEST, request);
            } else {
                request = new TakeshiHttpRequestWrapper(httpServletRequest);
            }
            chain.doFilter(request, response);
            stopWatch.stop();
            log.info("End Of Response, Time Consuming: {} ms", stopWatch.getTotalTimeMillis());
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
