package com.takeshi.component;

import cn.hutool.core.util.ObjUtil;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 确保应用退出时关闭一些东西
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShutdownManager {

    private final RedissonClient redissonClient;

    /**
     * destroy
     */
    @PreDestroy
    public void destroy() {
        log.info("Close the background task in the task thread pool...");
        if (ObjUtil.isNotNull(redissonClient)) {
            log.info("Close the Redisson client connection...");
            redissonClient.shutdown();
        }
    }

}