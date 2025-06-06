package com.takeshi.config;

import cn.hutool.core.thread.ThreadUtil;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author Lion Li
 **/
@Slf4j
@EnableAsync
@AutoConfiguration(value = "threadPoolConfig")
@AutoConfigureOrder(Integer.MIN_VALUE)
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final Tracer tracer;

    @Value("${server.port:8080}")
    private Integer serverPort;

    /**
     * 核心线程数 = cpu 核心数 + 1
     */
    private final int CORE = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * taskDecorator
     *
     * @return TaskDecorator
     */
    @Bean
    @ConditionalOnMissingBean
    public TaskDecorator taskDecorator() {
        return runnable -> {
            Span currentSpan = tracer.currentSpan();
            if (currentSpan == null) {
                return runnable;
            }
            return () -> {
                Span childSpan = tracer.spanBuilder().setParent(currentSpan.context()).start();
                try (Tracer.SpanInScope spanInScope = tracer.withSpan(childSpan)) {
                    runnable.run();
                } finally {
                    childSpan.end();
                }
            };
        };
    }

    /**
     * 线程池
     *
     * @param taskDecorator taskDecorator
     * @return ThreadPoolTaskExecutor
     */
    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor(TaskDecorator taskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE);
        executor.setMaxPoolSize(CORE * 2);
        // 队列最大长度
        int QUEUE_CAPACITY = 128;
        executor.setQueueCapacity(QUEUE_CAPACITY);
        // 线程池维护线程所允许的空闲时间
        int KEEP_ALIVE_SECONDS = 300;
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setTaskDecorator(taskDecorator);
        executor.setThreadNamePrefix("thread-" + serverPort + "-exec-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * 执行周期性或定时任务
     *
     * @return ScheduledExecutorService
     */
    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService scheduledExecutorService() {
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory("schedule-" + serverPort + "-exec-", true);
        return new ScheduledThreadPoolExecutor(CORE, threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
