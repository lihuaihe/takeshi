package com.takeshi.config.security;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import com.takeshi.annotation.TakeshiLog;
import com.takeshi.component.TakeshiAsyncComponent;
import com.takeshi.config.properties.TakeshiProperties;
import com.takeshi.constants.RequestConstants;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.util.GsonUtil;
import com.takeshi.util.TakeshiUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * TakeshiFilter
 */
@Slf4j
@AutoConfiguration(value = "takeshiFilter")
@RequiredArgsConstructor
public class TakeshiFilter implements Filter {

    private final TakeshiAsyncComponent takeshiAsyncComponent;

    private final TakeshiProperties takeshiProperties;

    @Value("${takeshi.enable-response-data-log:true}")
    private boolean enableResponseDataLog;

    private List<String> excludeUrlList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludeUrlList = new ArrayList<>(List.of(TakeshiConstants.EXCLUDE_URL));
        if (ArrayUtil.isNotEmpty(takeshiProperties.getExcludeUrl())) {
            excludeUrlList.addAll(List.of(takeshiProperties.getExcludeUrl()));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AntPathMatcher antPathMatcher = Singleton.get(AntPathMatcher.class.getSimpleName(), AntPathMatcher::new);
        if (request instanceof HttpServletRequest httpServletRequest
                && response instanceof HttpServletResponse httpServletResponse
                && excludeUrlList.stream().noneMatch(item -> antPathMatcher.match(item, httpServletRequest.getServletPath()))) {
            Instant startTime = Instant.now();
            String traceId = IdUtil.fastSimpleUUID();
            // 填充traceId
            MDC.put(RequestConstants.TRACE_ID, traceId);
            StopWatch stopWatch = new StopWatch(traceId);
            stopWatch.start();
            StandardServletMultipartResolver standardServletMultipartResolver = new StandardServletMultipartResolver();
            HttpServletRequest takeshiHttpRequestWrapper;
            if (standardServletMultipartResolver.isMultipart(httpServletRequest)) {
                takeshiHttpRequestWrapper = standardServletMultipartResolver.resolveMultipart(httpServletRequest);
            } else {
                takeshiHttpRequestWrapper = new TakeshiHttpRequestWrapper(httpServletRequest);
            }
            String clientIp = TakeshiUtil.getClientIp(takeshiHttpRequestWrapper);
            takeshiHttpRequestWrapper.setAttribute(RequestConstants.CLIENT_IP, clientIp);
            Map<String, Object> map = new LinkedHashMap<>(20);
            map.put("Request Address", StrUtil.builder(StrUtil.BRACKET_START, takeshiHttpRequestWrapper.getMethod(), StrUtil.BRACKET_END, takeshiHttpRequestWrapper.getRequestURL()));
            Object loginIdDefaultNull = StpUtil.getLoginIdDefaultNull();
            if (ObjUtil.isNotNull(loginIdDefaultNull)) {
                takeshiHttpRequestWrapper.setAttribute(RequestConstants.LOGIN_ID, loginIdDefaultNull);
                map.put("Requesting UserId", loginIdDefaultNull);
                Map<String, Object> saSessionDataMap = StpUtil.getSession().getDataMap();
                if (CollUtil.isNotEmpty(saSessionDataMap)) {
                    map.put("Requesting SaSessionData", saSessionDataMap);
                }
            }
            map.put("Request IP", clientIp);
            map.put("Request UserAgent", takeshiHttpRequestWrapper.getHeader(Header.USER_AGENT.getValue()));
            map.put("Header GeoPoint", takeshiHttpRequestWrapper.getHeader(RequestConstants.Header.GEO_POINT));
            map.put("Header Timezone", takeshiHttpRequestWrapper.getHeader(RequestConstants.Header.TIMEZONE));
            map.put("Header Timestamp", takeshiHttpRequestWrapper.getHeader(RequestConstants.Header.TIMESTAMP));
            map.put("Header Nonce", takeshiHttpRequestWrapper.getHeader(RequestConstants.Header.NONCE));
            log.info("TakeshiFilter.doFilter --> Request Start: {}", GsonUtil.toJson(map));
            TakeshiHttpResponseWrapper takeshiHttpResponseWrapper = new TakeshiHttpResponseWrapper(httpServletResponse);
            // 执行过滤器
            chain.doFilter(takeshiHttpRequestWrapper, takeshiHttpResponseWrapper);
            // 获取 takeshiHttpResponseWrapper 的返回值
            byte[] bytes = takeshiHttpResponseWrapper.getResponseData();
            String responseData = StrUtil.str(bytes, StandardCharsets.UTF_8);
            if (enableResponseDataLog) {
                log.info("Response Data: {}", responseData);
            }
            // 将响应内容写回到原始的 HttpServletResponse 中
            try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
                outputStream.write(bytes);
                outputStream.flush();
            }
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.info("End Of Response, Time Consuming: {} ms", totalTimeMillis);
            TakeshiLog takeshiLog = (TakeshiLog) takeshiHttpRequestWrapper.getAttribute(RequestConstants.TAKESHI_LOG);
            if (ObjUtil.isNotNull(takeshiLog)) {
                // 新增一条接口请求相关信息到数据库
                Object loginId = takeshiHttpRequestWrapper.getAttribute(RequestConstants.LOGIN_ID);
                String paramObjectValue = (String) takeshiHttpRequestWrapper.getAttribute(RequestConstants.PARAM_OBJECT_VALUE);
                String userAgent = takeshiHttpRequestWrapper.getHeader(Header.USER_AGENT.getValue());
                String methodName = (String) takeshiHttpRequestWrapper.getAttribute(RequestConstants.METHOD_NAME);
                Map<String, String> headerMap = new HashMap<>();
                Enumeration<String> headerNames = takeshiHttpRequestWrapper.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    headerMap.put(headerName, takeshiHttpRequestWrapper.getHeader(headerName));
                }
                takeshiAsyncComponent.insertSysLog(takeshiLog, loginId, clientIp, userAgent, headerMap, paramObjectValue, takeshiHttpRequestWrapper.getMethod(), methodName, takeshiHttpRequestWrapper.getRequestURL().toString(), startTime, totalTimeMillis, responseData);
            }
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 请求结束时删除数据，否则会造成内存溢出
        MDC.remove(RequestConstants.TRACE_ID);
    }

}
