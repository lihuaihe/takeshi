package com.takeshi.component;

import com.takeshi.util.TakeshiThreadUtil;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 确保应用退出时能关闭后台线程
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShutdownManager {

    private final ScheduledExecutorService scheduledExecutorService;

    private static final long TIMEOUT = 60;

    /**
     * destroy
     */
    @PreDestroy
    public void destroy() {
        shutdownAsyncManager();
    }

    /**
     * 停止异步执行任务
     */
    private void shutdownAsyncManager() {
        try {
            log.info("====关闭后台任务任务线程池====");
            TakeshiThreadUtil.shutdownAndAwaitTermination(scheduledExecutorService, TIMEOUT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}