package com.takeshi.component;

import cn.hutool.core.util.ObjUtil;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import com.takeshi.util.AmazonS3Util;
import com.takeshi.util.TakeshiThreadUtil;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * destroy
     */
    @PreDestroy
    public void destroy() {
        TakeshiThreadUtil.shutdownAndAwaitTermination(scheduledExecutorService, StaticConfig.takeshiProperties.getMaxExecutorCloseTimeout());
        try {
            // 关闭 TransferManager，释放资源
            AWSSecretsManagerCredentials awsSecrets = StaticConfig.takeshiProperties.getAwsSecrets();
            if (awsSecrets.isEnabled()) {
                TransferManager transferManager = AmazonS3Util.transferManager;
                if (ObjUtil.isNotNull(transferManager)) {
                    log.info("Close the TransferManager instance...");
                    transferManager.shutdownNow();
                }
            }
        } catch (Exception e) {
            log.error("ShutdownManager.destroy --> e: ", e);
        }
    }

}