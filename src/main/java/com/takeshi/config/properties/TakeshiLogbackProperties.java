package com.takeshi.config.properties;

import com.takeshi.enums.CompressionTypeEnum;
import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * TakeshiLogbackProperties
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "takeshi.logback")
@Validated
public class TakeshiLogbackProperties {

    /**
     * 压缩类型
     */
    private CompressionTypeEnum compressionType;

    /**
     * 开发环境的日志设置
     */
    private AppenderRef dev;

    /**
     * 测试环境的日志设置
     */
    private AppenderRef test;

    /**
     * 正式环境的日志设置
     */
    private AppenderRef prod;

    /**
     * 日志开启状态设置
     */
    @Data
    public static class AppenderRef {

        /**
         * 控制台输出
         */
        private boolean stdoutEnabled;

        /**
         * debug日志输出
         */
        private boolean debugEnabled;

        /**
         * info日志输出
         */
        private boolean infoEnabled;

        /**
         * warn日志输出
         */
        private boolean warnEnabled;

        /**
         * error日志输出
         */
        private boolean errorEnabled;

    }

}
