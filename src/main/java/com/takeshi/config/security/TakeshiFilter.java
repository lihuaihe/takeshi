package com.takeshi.config.security;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.takeshi.component.TakeshiAsyncComponent;
import com.takeshi.config.properties.TakeshiProperties;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.exception.Either;
import com.takeshi.pojo.bo.ParamBO;
import com.takeshi.util.TakeshiUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TakeshiFilter
 */
@Slf4j
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TakeshiFilter implements Filter {

    private final TakeshiAsyncComponent takeshiAsyncComponent;
    private final TakeshiProperties takeshiProperties;

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
            MDC.put(TakeshiConstants.TRACE_ID_KEY, traceId);
            StopWatch stopWatch = new StopWatch(traceId);
            stopWatch.start();
            StandardServletMultipartResolver standardServletMultipartResolver = new StandardServletMultipartResolver();
            HttpServletRequest takeshiHttpRequestWrapper;
            if (standardServletMultipartResolver.isMultipart(httpServletRequest)) {
                takeshiHttpRequestWrapper = standardServletMultipartResolver.resolveMultipart(httpServletRequest);
            } else {
                takeshiHttpRequestWrapper = new TakeshiHttpRequestWrapper(httpServletRequest);
            }
            ParamBO paramBO = this.setParamBOAttribute(takeshiHttpRequestWrapper);
            log.info("TakeshiFilter.doFilter --> Request Start: {}", paramBO.filterInfo());
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
            // 关闭输出流
            outputStream.close();
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.info("End Of Response, Time Consuming: {} ms", totalTimeMillis);
            // 新增一条接口请求相关信息到数据库
            takeshiAsyncComponent.insertSysLog((ParamBO) takeshiHttpRequestWrapper.getAttribute(TakeshiConstants.PARAM_BO), startTime, totalTimeMillis, responseData);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 请求结束时删除数据，否则会造成内存溢出
        MDC.remove(TakeshiConstants.TRACE_ID_KEY);
    }

    /**
     * 获取所有参数，包装成一个对象
     *
     * @param request request
     * @return ParamBO
     */
    private ParamBO setParamBOAttribute(HttpServletRequest request) throws IOException {
        ParamBO paramBO = new ParamBO();

        String clientIp = TakeshiUtil.getClientIp(request);
        paramBO.setClientIp(clientIp);
        // paramBO.setClientIpAddress(TakeshiUtil.getRealAddressByIp(clientIp));
        paramBO.setLoginId(StpUtil.getLoginIdDefaultNull());
        paramBO.setDataMap(StpUtil.getSession().getDataMap());
        paramBO.setRequestUrl(request.getRequestURL().toString());
        paramBO.setHttpMethod(request.getMethod());

        CaseInsensitiveMap<String, String> headerMap = new CaseInsensitiveMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        paramBO.setHeaderParam(headerMap);

        paramBO.setUrlParam(JakartaServletUtil.getParamMap(request));
        if (HttpMethod.POST.matches(request.getMethod())
                && request instanceof StandardMultipartHttpServletRequest multipartRequest) {
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
        } else if (!HttpMethod.GET.matches(request.getMethod())) {
            paramBO.setBody(request.getInputStream());
        }
        // 接口请求的参数，放在request的attribute传递下去，以免频繁获取
        request.setAttribute(TakeshiConstants.PARAM_BO, paramBO);
        return paramBO;
    }

}
