package com.takeshi.component;

import cn.hutool.core.util.ObjUtil;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.takeshi.config.StaticConfig;
import com.takeshi.util.AmazonS3Util;
import com.takeshi.util.TakeshiThreadUtil;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
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
    private final RedissonClient redissonClient;

    /**
     * destroy
     */
    @PreDestroy
    public void destroy() {
        try {
            log.info("Close the background task in the task thread pool...");
            TakeshiThreadUtil.shutdownAndAwaitTermination(scheduledExecutorService, StaticConfig.takeshiProperties.getMaxExecutorCloseTimeout());
            if (ObjUtil.isNotNull(redissonClient)) {
                log.info("Close the Redisson client connection...");
                redissonClient.shutdown();
            }
            // 关闭 TransferManager，释放资源
            TransferManager transferManager = AmazonS3Util.transferManager;
            if (ObjUtil.isNotNull(transferManager)) {
                log.info("Close the TransferManager instance...");
                transferManager.shutdownNow();
            }
        } catch (Exception e) {
            log.error("ShutdownManager.destroy --> e: ", e);
        }
    }

}