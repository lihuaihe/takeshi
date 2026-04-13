package com.takeshi.config;

import ch.qos.logback.classic.LoggerContext;
import com.takeshi.component.TakeSqlDenyTurboFilter;
import com.takeshi.config.properties.TakeshiProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * SQL 日志过滤自动配置
 * <p>
 * 当 {@code takeshi.sql-log-filter.enabled=true} 时自动生效，
 * 将 {@link TakeSqlDenyTurboFilter} 注册到 Logback 上下文中。
 * <p>
 * application.yml 配置示例：
 * <pre>{@code
 * takeshi:
 *   sql-log-filter:
 *     enabled: true
 *     extra-packages:
 *       - com.example.mapper
 * }</pre>
 *
 * @author Lil' Doe
 */
@AutoConfiguration(value = "takeshiSqlLogFilterConfig", after = TakeshiProperties.class)
@ConditionalOnProperty(prefix = "takeshi.sql-log-filter", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class TakeshiSqlLogFilterConfig {

    private final TakeshiProperties takeshiProperties;

    /**
     * 将 {@link TakeSqlDenyTurboFilter} 编程式注册到 Logback 的 LoggerContext 中
     *
     * @author Lil' Doe
     *  2026/4/13 00:00
     */
    @PostConstruct
    public void registerSqlDenyTurboFilter() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        TakeSqlDenyTurboFilter filter = new TakeSqlDenyTurboFilter(
                takeshiProperties.getSqlLogFilter().getExtraPackages()
        );
        filter.setContext(loggerContext);
        filter.start();
        loggerContext.addTurboFilter(filter);
    }

}
