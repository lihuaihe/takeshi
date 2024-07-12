package com.takeshi.util;

import cn.hutool.core.lang.Range;

import java.io.Serial;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * InstantUtil
 *
 * @author 七濑武【Nanase Takeshi】
 */
public final class InstantUtil {

    /**
     * 构造函数
     */
    private InstantUtil() {
    }

    /**
     * 根据步进单位获取起始日期时间和结束日期时间的时间区间集合
     *
     * @param start 起始日期时间
     * @param end   结束日期时间
     * @param unit  步进单位
     * @return List
     */
    public static List<Instant> rangeToList(Instant start, Instant end, ChronoUnit unit) {
        return StreamSupport.stream(new InstantRange(start, end, unit).spliterator(), false).collect(Collectors.toList());
    }

    /**
     * 根据步进单位和步进获取起始日期时间和结束日期时间的时间区间集合
     *
     * @param start 起始日期时间
     * @param end   结束日期时间
     * @param unit  步进单位
     * @param step  步进
     * @return List
     */
    public static List<Instant> rangeToList(Instant start, Instant end, final ChronoUnit unit, int step) {
        return StreamSupport.stream(new InstantRange(start, end, unit, step).spliterator(), false).collect(Collectors.toList());
    }

    /**
     * 创建日期范围生成器
     *
     * @param start 起始日期时间（包括）
     * @param end   结束日期时间
     * @param unit  步进单位
     * @return {@link InstantUtil.InstantRange}
     */
    public static InstantRange range(Instant start, Instant end, final ChronoUnit unit) {
        return new InstantRange(start, end, unit);
    }

    /**
     * 日期范围
     *
     * @author looly
     */
    public static class InstantRange extends Range<Instant> {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 构造，包含开始和结束日期时间
         *
         * @param start 起始日期时间（包括）
         * @param end   结束日期时间（包括）
         * @param unit  步进单位
         */
        public InstantRange(Instant start, Instant end, ChronoUnit unit) {
            this(start, end, unit, 1);
        }

        /**
         * 构造，包含开始和结束日期时间
         *
         * @param start 起始日期时间（包括）
         * @param end   结束日期时间（包括）
         * @param unit  步进单位
         * @param step  步进数
         */
        public InstantRange(Instant start, Instant end, ChronoUnit unit, int step) {
            this(start, end, unit, step, true, true);
        }

        /**
         * 构造
         *
         * @param start          起始日期时间
         * @param end            结束日期时间
         * @param unit           步进单位
         * @param step           步进数
         * @param isIncludeStart 是否包含开始的时间
         * @param isIncludeEnd   是否包含结束的时间
         */
        public InstantRange(Instant start, Instant end, ChronoUnit unit, int step, boolean isIncludeStart, boolean isIncludeEnd) {
            super(start, end, (current, end1, index) -> {
                Instant instant = start.plus((index + 1L) * step, unit);
                return instant.isAfter(end1) ? null : instant;
            }, isIncludeStart, isIncludeEnd);
        }

    }

}
