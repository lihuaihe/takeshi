package com.takeshi.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.util.List;

/**
 * SQL 日志屏蔽 TurboFilter
 * <p>
 * 当 MDC 中存在 {@link #MDC_KEY} = "1" 时，屏蔽所有 SQL 相关框架的日志输出，
 * 适用于批量任务、定时任务等场景，避免产生大量无意义的 SQL 日志。
 * <p>
 * 使用方式：
 * <pre>{@code
 * MDC.put(TakeSqlDenyTurboFilter.MDC_KEY, "1");
 * try {
 *     // 执行业务逻辑，SQL 日志自动屏蔽
 * } finally {
 *     MDC.remove(TakeSqlDenyTurboFilter.MDC_KEY);
 * }
 * }</pre>
 * <p>
 * 通过 {@code takeshi.sql-log-filter.enabled=true} 开启，并可通过
 * {@code takeshi.sql-log-filter.extra-packages} 配置额外需要屏蔽的包前缀。
 *
 * @author Lil' Doe
 */
public class TakeSqlDenyTurboFilter extends TurboFilter {

    /**
     * MDC 标记键名，值为 "1" 时激活 SQL 日志屏蔽
     */
    public static final String MDC_KEY = "takeshi_sql_deny";

    /**
     * 框架级别默认屏蔽的 logger 包前缀
     */
    private static final List<String> DEFAULT_DENY_PREFIXES = List.of(
            "org.apache.ibatis",
            "com.baomidou.mybatisplus",
            "com.takeshi.mybatisplus",
            "com.alibaba.druid",
            "druid.sql",
            "com.mysql.cj",
            "java.sql",
            "javax.sql",
            "org.springframework.jdbc"
    );

    /**
     * 用户自定义的额外屏蔽包前缀（通常为业务项目的 mapper 包）
     */
    private final List<String> extraPackages;

    /**
     * 构造方法
     *
     * @param extraPackages 额外需要屏蔽的 logger 包前缀列表
     * @author Lil' Doe
     */
    public TakeSqlDenyTurboFilter(List<String> extraPackages) {
        this.extraPackages = extraPackages != null ? extraPackages : List.of();
    }

    @Override
    public FilterReply decide(Marker marker,
                              Logger logger,
                              Level level,
                              String format,
                              Object[] params,
                              Throwable t) {
        if (!"1".equals(MDC.get(MDC_KEY))) {
            return FilterReply.NEUTRAL;
        }

        String loggerName = logger == null ? null : logger.getName();
        if (loggerName == null) {
            return FilterReply.NEUTRAL;
        }

        for (String prefix : DEFAULT_DENY_PREFIXES) {
            if (loggerName.startsWith(prefix)) {
                return FilterReply.DENY;
            }
        }

        for (String prefix : extraPackages) {
            if (loggerName.startsWith(prefix)) {
                return FilterReply.DENY;
            }
        }

        return FilterReply.NEUTRAL;
    }

}
