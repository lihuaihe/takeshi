package com.takeshi.config;

import cn.hutool.core.thread.ThreadException;
import cn.hutool.core.util.ArrayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * Async注解使用自定义线程池
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EnableAsync
@AutoConfiguration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 自定义 @Async 注解使用系统线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        return threadPoolTaskExecutor;
    }

    /**
     * 异步执行异常处理
     */
    @Override
    @SuppressWarnings("all")
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception message - ").append(throwable.getMessage()).append(", Method name - ").append(method.getName());
            if (ArrayUtil.isNotEmpty(objects)) {
                sb.append(", Parameter value - ").append(Arrays.toString(objects));
            }
            throw new ThreadException(sb.toString(), throwable);
        };
    }

}
