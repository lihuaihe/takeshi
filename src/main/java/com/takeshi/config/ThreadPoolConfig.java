package com.takeshi.config;

import cn.hutool.core.thread.ThreadUtil;
import com.takeshi.util.TakeshiThreadUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author Lion Li
 **/
@AutoConfiguration
public class ThreadPoolConfig {

    /**
     * 核心线程数 = cpu 核心数 + 1
     */
    private final int CORE = Runtime.getRuntime().availableProcessors() + 1;

    /**
     * threadPoolTaskExecutor
     *
     * @return ThreadPoolTaskExecutor
     */
    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE);
        executor.setMaxPoolSize(CORE * 2);
        // 队列最大长度
        int QUEUE_CAPACITY = 128;
        executor.setQueueCapacity(QUEUE_CAPACITY);
        // 线程池维护线程所允许的空闲时间
        int KEEP_ALIVE_SECONDS = 300;
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setThreadNamePrefix("task-" + StaticConfig.serverPort + "-exec-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    /**
     * 执行周期性或定时任务
     *
     * @return ScheduledExecutorService
     */
    @Bean("scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(CORE,
                ThreadUtil.newNamedThreadFactory("schedule-" + StaticConfig.serverPort + "-exec-", true),
                new ThreadPoolExecutor.CallerRunsPolicy()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                TakeshiThreadUtil.printException(r, t);
            }
        };
    }
}
