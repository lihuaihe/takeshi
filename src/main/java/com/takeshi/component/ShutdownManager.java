package com.takeshi.component;

import com.takeshi.util.AmazonS3Util;
import com.takeshi.util.TakeshiThreadUtil;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.context.LifecycleProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 确保应用退出时关闭一些东西
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShutdownManager {

    private final ScheduledExecutorService scheduledExecutorService;

    private final LifecycleProperties lifecycleProperties;

    /**
     * destroy
     */
    @PreDestroy
    public void destroy() {
        TakeshiThreadUtil.shutdownAndAwaitTermination(scheduledExecutorService, lifecycleProperties.getTimeoutPerShutdownPhase());
        AmazonS3Util.shutdown();
    }

}