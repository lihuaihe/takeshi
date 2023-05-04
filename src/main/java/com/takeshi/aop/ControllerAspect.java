package com.takeshi.aop;

import com.takeshi.util.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

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

    /**
     * AOP环绕
     *
     * @param proceedingJoinPoint 程序加入点
     * @return Object
     */
    @SneakyThrows
    @Around("execution(* *..controller..*(..))")
    @AfterThrowing
    public Object around(ProceedingJoinPoint proceedingJoinPoint) {
        Object proceed = proceedingJoinPoint.proceed();
        log.info("响应报文: {}", GsonUtil.toJson(proceed));
        return proceed;
    }

}
