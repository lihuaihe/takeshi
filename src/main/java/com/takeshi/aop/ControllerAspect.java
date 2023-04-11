package com.takeshi.aop;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.Header;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.takeshi.annotation.RepeatSubmit;
import com.takeshi.config.StaticConfig;
import com.takeshi.constants.SysCode;
import com.takeshi.constants.SysConstants;
import com.takeshi.exception.TakeshiException;
import com.takeshi.pojo.vo.ResponseDataVO;
import com.takeshi.util.GsonUtil;
import com.takeshi.util.TakeshiUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller层AOP日志和接口重复提交判断
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ControllerAspect {

    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;

    @Around("execution(* com..*..controller..*(..))")
    @AfterThrowing
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        StopWatch stopWatch = new StopWatch(MDC.get(SysConstants.TRACE_ID_KEY));
        stopWatch.start();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        String userAgent = request.getHeader(Header.USER_AGENT.getValue());
        String timestamp = request.getHeader(SysConstants.TIMESTAMP_NAME);
        log.info("请求开始, 请求时间: {}, 请求IP: {}, 请求工具: {}", timestamp, TakeshiUtil.getClientIp(request), userAgent);
        log.info("请求地址: {}, 请求方法: [{}] {}.{}", request.getRequestURL(), request.getMethod(), signature.getDeclaringTypeName(), signature.getName());
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (ObjUtil.isNotNull(loginId)) {
            log.info("请求用户ID: {}", loginId);
        }
        // 重复提交
        this.isRepeatSubmit(request, signature, proceedingJoinPoint.getArgs());
        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
            log.info("响应报文: {}", GsonUtil.toJson(proceed));
        } catch (Throwable e) {
            ExceptionUtil.wrapAndThrow(e);
        } finally {
            stopWatch.stop();
            log.info("响应结束,耗时: {} ms", stopWatch.getTotalTimeMillis());
        }
        return proceed;
    }

    /**
     * 重复提交
     *
     * @param request
     * @param signature
     * @param args
     */
    private void isRepeatSubmit(HttpServletRequest request, MethodSignature signature, Object[] args) {
        List<Object> list = Stream.of(args)
                .filter(arg -> !(arg instanceof ServletRequest || arg instanceof ServletResponse))
                .map(arg -> {
                    if (arg instanceof MultipartFile file) {
                        return StrUtil.concat(true, file.getOriginalFilename(), StrUtil.BRACKET_START, DataSizeUtil.format(file.getSize()), StrUtil.BRACKET_END);
                    } else if (arg instanceof MultipartFile[] files) {
                        return Arrays.stream(files).map(file -> StrUtil.concat(true, file.getOriginalFilename(), StrUtil.BRACKET_START, DataSizeUtil.format(file.getSize()), StrUtil.BRACKET_END)).toList();
                    }
                    return arg;
                })
                .collect(Collectors.toList());
        log.info("请求报文: {} ", GsonUtil.toJson(list));
        RepeatSubmit repeatSubmit = signature.getMethod().getAnnotation(RepeatSubmit.class);
        if (ObjUtil.isNotEmpty(repeatSubmit)) {
            ResponseDataVO.ResBean resBean = SysCode.REPEAT_SUBMIT;
            long interval = repeatSubmit.interval();
            if (StrUtil.isNotBlank(repeatSubmit.msg())) {
                resBean.setInfo(repeatSubmit.msg());
            }
            Map<String, Object> map = new HashMap<>(8);
            map.put("repeatUrl", request.getServletPath());
            map.put("repeatToken", StpUtil.getTokenValue());
            JSONObject jsonObject = JSONUtil.createObj();
            String[] parameterNames = signature.getParameterNames();
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg instanceof ServletRequest || arg instanceof ServletResponse || arg instanceof MultipartFile || arg instanceof File) {
                    break;
                }
                jsonObject.set(parameterNames[i], arg);
            }
            ObjectNode objectNode = objectMapper.convertValue(jsonObject, ObjectNode.class);
            List<String> ignoredFieldList = Arrays.asList(repeatSubmit.ignoredFieldNames());
            if (CollUtil.isNotEmpty(ignoredFieldList)) {
                objectNode.remove(ignoredFieldList);
                ignoredFieldList.forEach(item -> {
                    ObjectNode parent = objectNode.findParent(item);
                    if (ObjUtil.isNotEmpty(parent)) {
                        parent.remove(item);
                    }
                });
            }
            map.put("repeatParams", objectNode);
            String key = StaticConfig.applicationName + ":repeatSubmit:" + SecureUtil.md5(GsonUtil.toJson(map));
            RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
            // 限制xx毫秒1次
            rateLimiter.trySetRate(RateType.PER_CLIENT, 1, interval, RateIntervalUnit.MILLISECONDS);
            if (!rateLimiter.tryAcquire()) {
                throw new TakeshiException(resBean);
            }
        }
    }

}
