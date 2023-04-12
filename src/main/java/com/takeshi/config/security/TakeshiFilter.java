package com.takeshi.config.security;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.takeshi.constants.SysConstants;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * TakeshiFilter
 */
@Configuration
@Order(HIGHEST_PRECEDENCE)  // 优先级最高
public class TakeshiFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 填充traceId
        MDC.put(SysConstants.TRACE_ID_KEY, IdUtil.fastSimpleUUID());
        if (request instanceof HttpServletRequest httpServletRequest) {
            if (StrUtil.contains(httpServletRequest.getContentType(), "multipart/form-data")) {
                httpServletRequest = new StandardServletMultipartResolver().resolveMultipart(httpServletRequest);
            }
            chain.doFilter(TakeshiHttpRequestWrapper.build(httpServletRequest), response);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 请求结束时删除数据，否则会造成内存溢出
        MDC.remove(SysConstants.TRACE_ID_KEY);
    }

}
