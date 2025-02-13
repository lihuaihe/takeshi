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
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.ContentCachingResponseWrapper;

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
public class TakeshiFilter extends OncePerRequestFilter {

    private final TakeshiAsyncComponent takeshiAsyncComponent;

    private final TakeshiProperties takeshiProperties;

    private final MultipartResolver multipartResolver;

    @Value("${takeshi.enable-response-data-log:true}")
    private boolean enableResponseDataLog;

    private List<String> excludeUrlList;

    @Override
    public void initFilterBean() throws ServletException {
        excludeUrlList = new ArrayList<>(List.of(TakeshiConstants.EXCLUDE_URL));
        if (ArrayUtil.isNotEmpty(takeshiProperties.getExcludeUrl())) {
            excludeUrlList.addAll(List.of(takeshiProperties.getExcludeUrl()));
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AntPathMatcher antPathMatcher = Singleton.get(AntPathMatcher.class.getSimpleName(), AntPathMatcher::new);
        String servletPath = request.getServletPath();
        // 如果接口返回值是流式响应，则应将流式响应接口路径配置在excludeUrl中，这样就不会记录日志，否则会无法持续返回流
        if (excludeUrlList.stream().noneMatch(item -> antPathMatcher.match(item, servletPath))) {
            Instant startTime = Instant.now();
            String traceId = IdUtil.fastSimpleUUID();
            // 填充traceId
            MDC.put(RequestConstants.TRACE_ID, traceId);
            StopWatch stopWatch = new StopWatch(traceId);
            stopWatch.start();
            if (multipartResolver.isMultipart(request)) {
                request = multipartResolver.resolveMultipart(request);
            } else {
                request = new CachedBodyHttpServletRequest(request);
            }
            String clientIp = TakeshiUtil.getClientIp(request);
            request.setAttribute(RequestConstants.CLIENT_IP, clientIp);
            Map<String, Object> map = new LinkedHashMap<>(20);
            map.put("Request Address", StrUtil.builder(StrUtil.BRACKET_START, request.getMethod(), StrUtil.BRACKET_END, request.getRequestURL()));
            Object loginIdDefaultNull = StpUtil.getLoginIdDefaultNull();
            if (ObjUtil.isNotNull(loginIdDefaultNull)) {
                request.setAttribute(RequestConstants.LOGIN_ID, loginIdDefaultNull);
                map.put("Requesting UserId", loginIdDefaultNull);
                Map<String, Object> saSessionDataMap = StpUtil.getSession().getDataMap();
                if (CollUtil.isNotEmpty(saSessionDataMap)) {
                    map.put("Requesting SaSessionData", saSessionDataMap);
                }
            }
            map.put("Request IP", clientIp);
            map.put("Header AcceptVersion", request.getHeader(RequestConstants.Header.ACCEPT_VERSION));
            map.put("Request UserAgent", request.getHeader(Header.USER_AGENT.getValue()));
            map.put("Header GeoPoint", request.getHeader(RequestConstants.Header.GEO_POINT));
            map.put("Header Timezone", request.getHeader(RequestConstants.Header.TIMEZONE));
            map.put("Header Timestamp", request.getHeader(RequestConstants.Header.TIMESTAMP));
            map.put("Header Nonce", request.getHeader(RequestConstants.Header.NONCE));
            log.info("TakeshiFilter.doFilter --> Request Start: {}", GsonUtil.toJson(map));

            response = new ContentCachingResponseWrapper(response);
            // 执行过滤器
            filterChain.doFilter(request, response);
            // 获取 contentCachingResponseWrapper 的返回值
            byte[] bytes = ((ContentCachingResponseWrapper) response).getContentAsByteArray();
            String responseData = StrUtil.str(bytes, StandardCharsets.UTF_8);
            if (enableResponseDataLog) {
                log.info("Response Data: {}", responseData);
            }
            ((ContentCachingResponseWrapper) response).copyBodyToResponse();
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.info("End Of Response, Time Consuming: {} ms", totalTimeMillis);
            TakeshiLog takeshiLog = (TakeshiLog) request.getAttribute(RequestConstants.TAKESHI_LOG);
            if (ObjUtil.isNotNull(takeshiLog)) {
                // 新增一条接口请求相关信息到数据库
                Object loginId = request.getAttribute(RequestConstants.LOGIN_ID);
                String paramObjectValue = (String) request.getAttribute(RequestConstants.PARAM_OBJECT_VALUE);
                String userAgent = request.getHeader(Header.USER_AGENT.getValue());
                String methodName = (String) request.getAttribute(RequestConstants.METHOD_NAME);
                Map<String, String> headerMap = new HashMap<>();
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    headerMap.put(headerName, request.getHeader(headerName));
                }
                takeshiAsyncComponent.insertSysLog(takeshiLog, loginId, clientIp, userAgent, headerMap, paramObjectValue, request.getMethod(), methodName, request.getRequestURL().toString(), startTime, totalTimeMillis, responseData);
            }
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 请求结束时删除数据，否则会造成内存溢出
        MDC.remove(RequestConstants.TRACE_ID);
    }

}
