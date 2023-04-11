package com.takeshi.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.TemporalUtil;
import cn.hutool.core.lang.Range;
import cn.hutool.core.util.ObjUtil;
import com.takeshi.constants.TakeshiDatePattern;

import java.io.Serial;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * ZonedDateTimeUtil
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/10/10 09:35
 */
public final class ZonedDateTimeUtil {

    private ZonedDateTimeUtil() {
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    public static ZonedDateTime now(Clock clock) {
        return ZonedDateTime.now(clock);
    }

    public static ZonedDateTime now(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    public static ZonedDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, ZoneId.systemDefault());
    }

    public static ZonedDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, TimeZone timeZone) {
        return of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, timeZone.toZoneId());
    }

    public static ZonedDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, ZoneId zoneId) {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zoneId);
    }

    public static ZonedDateTime ofInstant(Instant instant) {
        return ofInstant(instant, ZoneId.systemDefault());
    }

    public static ZonedDateTime ofInstant(Instant instant, TimeZone timeZone) {
        return ofInstant(instant, ObjUtil.defaultIfNull(timeZone, TimeZone::getDefault).toZoneId());
    }

    public static ZonedDateTime ofInstant(Instant instant, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    public static ZonedDateTime ofDate(Date date) {
        if (date instanceof DateTime) {
            return ofInstant(date.toInstant(), ((DateTime) date).getZoneId());
        }
        return ofInstant(date.toInstant());
    }

    public static ZonedDateTime of(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
    }

    public static ZonedDateTime of(LocalDateTime localDateTime, TimeZone timeZone) {
        return ZonedDateTime.of(localDateTime, timeZone.toZoneId());
    }

    public static ZonedDateTime of(LocalDateTime localDateTime, ZoneId zoneId) {
        return ZonedDateTime.of(localDateTime, zoneId);
    }

    public static ZonedDateTime of(LocalDate localDate, TimeZone timeZone) {
        return of(localDate, LocalTime.now(), timeZone.toZoneId());
    }

    public static ZonedDateTime of(LocalDate localDate, ZoneId zoneId) {
        return of(localDate, LocalTime.now(), zoneId);
    }

    public static ZonedDateTime of(LocalDate localDate, LocalTime localTime, TimeZone timeZone) {
        return ZonedDateTime.of(localDate, localTime, timeZone.toZoneId());
    }

    public static ZonedDateTime of(LocalDate localDate, LocalTime localTime, ZoneId zoneId) {
        return ZonedDateTime.of(localDate, localTime, zoneId);
    }

    public static ZonedDateTime ofLocal(LocalDateTime localDateTime, ZoneOffset preferredOffset) {
        return ZonedDateTime.ofLocal(localDateTime, ZoneId.systemDefault(), preferredOffset);
    }

    public static ZonedDateTime ofLocal(LocalDateTime localDateTime, ZoneId zoneId, ZoneOffset preferredOffset) {
        return ZonedDateTime.ofLocal(localDateTime, zoneId, preferredOffset);
    }

    public static ZonedDateTime ofEpochSecond(long epochSecond) {
        return ofInstant(Instant.ofEpochSecond(epochSecond));
    }

    public static ZonedDateTime ofEpochSecond(long epochSecond, ZoneId zoneId) {
        return ofInstant(Instant.ofEpochSecond(epochSecond), zoneId);
    }

    public static ZonedDateTime ofEpochSecond(long epochSecond, TimeZone timeZone) {
        return ofInstant(Instant.ofEpochSecond(epochSecond), timeZone);
    }

    public static ZonedDateTime ofEpochMilli(long epochMilli) {
        return ofInstant(Instant.ofEpochMilli(epochMilli));
    }

    public static ZonedDateTime ofEpochMilli(long epochMilli, ZoneId zoneId) {
        return ofInstant(Instant.ofEpochMilli(epochMilli), zoneId);
    }

    public static ZonedDateTime ofEpochMilli(long epochMilli, TimeZone timeZone) {
        return ofInstant(Instant.ofEpochMilli(epochMilli), timeZone);
    }

    public static ZonedDateTime ofEpochSecondUtc(long epochSecond) {
        return ofUtc(Instant.ofEpochSecond(epochSecond));
    }

    public static ZonedDateTime ofEpochMilliUtc(long epochMilli) {
        return ofUtc(Instant.ofEpochMilli(epochMilli));
    }

    public static ZonedDateTime ofUtc(Instant instant) {
        return ofInstant(instant, ZoneId.of("UTC"));
    }

    public static ZonedDateTime from(TemporalAccessor temporalAccessor) {
        return ZonedDateTime.from(temporalAccessor);
    }

    public ZonedDateTime parse(CharSequence text) {
        return parse(text, null);
    }

    public ZonedDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        if (null == text) {
            return null;
        }
        if (null == formatter) {
            return ZonedDateTime.parse(text);
        }
        return ZonedDateTime.parse(text, formatter);
    }

    public static String formatNormal(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, TakeshiDatePattern.NORM_DATETIME_FORMATTER);
    }

    public static String formatNormalDate(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, TakeshiDatePattern.NORM_DATE_FORMATTER);
    }

    public static String format(ZonedDateTime zonedDateTime, DateTimeFormatter formatter) {
        if (null == formatter) {
            return zonedDateTime.toString();
        }
        return zonedDateTime.format(formatter);
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * <p>
     * 返回结果为{@link Duration}对象，通过调用toXXX方法返回相差单位
     *
     * @param startTimeInclude 开始时间（包含）
     * @param endTimeExclude   结束时间（不包含）
     * @return 时间差 {@link Duration}对象
     *
     * @see TemporalUtil#between(Temporal, Temporal)
     */
    public static Duration between(ZonedDateTime startTimeInclude, ZonedDateTime endTimeExclude) {
        return TemporalUtil.between(startTimeInclude, endTimeExclude);
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * <p>
     * 返回结果为时间差的long值
     *
     * @param startTimeInclude 开始时间（包括）
     * @param endTimeExclude   结束时间（不包括）
     * @param unit             时间差单位
     * @return 时间差
     */
    public static long between(ZonedDateTime startTimeInclude, ZonedDateTime endTimeExclude, ChronoUnit unit) {
        return TemporalUtil.between(startTimeInclude, endTimeExclude, unit);
    }

    /**
     * 根据步进单位获取起始日期时间和结束日期时间的时间区间集合
     *
     * @param start 起始日期时间
     * @param end   结束日期时间
     * @param unit  步进单位
     * @return {@link ZonedDateTimeRange}
     */
    public static List<ZonedDateTime> rangeToList(ZonedDateTime start, ZonedDateTime end, ChronoUnit unit) {
        return CollUtil.newArrayList((Iterable<ZonedDateTime>) range(start, end, unit));
    }

    /**
     * 根据步进单位和步进获取起始日期时间和结束日期时间的时间区间集合
     *
     * @param start 起始日期时间
     * @param end   结束日期时间
     * @param unit  步进单位
     * @param step  步进
     * @return {@link ZonedDateTimeRange}
     */
    public static List<ZonedDateTime> rangeToList(ZonedDateTime start, ZonedDateTime end, final ChronoUnit unit, int step) {
        return CollUtil.newArrayList((Iterable<ZonedDateTime>) new ZonedDateTimeRange(start, end, unit, step));
    }

    /**
     * 创建日期范围生成器
     *
     * @param start 起始日期时间（包括）
     * @param end   结束日期时间
     * @param unit  步进单位
     * @return {@link ZonedDateTimeRange}
     */
    public static ZonedDateTimeRange range(ZonedDateTime start, ZonedDateTime end, final ChronoUnit unit) {
        return new ZonedDateTimeRange(start, end, unit);
    }

    public ZonedDateTime beginOfDay(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(LocalTime.MIN);
    }

    public ZonedDateTime endOfDay(ZonedDateTime zonedDateTime) {
        return endOfDay(zonedDateTime, false);
    }

    public ZonedDateTime endOfDay(ZonedDateTime zonedDateTime, boolean truncateMillisecond) {
        if (truncateMillisecond) {
            return zonedDateTime.with(LocalTime.of(23, 59, 59));
        }
        return zonedDateTime.with(LocalTime.MAX);
    }

    public ZonedDateTime beginOfWeek(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public ZonedDateTime endOfWeek(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    public static boolean isWeekend(ZonedDateTime zonedDateTime) {
        DayOfWeek dayOfWeek = zonedDateTime.getDayOfWeek();
        return DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek;
    }

    public static boolean isSameDay(ZonedDateTime date1, ZonedDateTime date2) {
        return date1 != null && date2 != null && isSameDay(date1.toLocalDate(), date2.toLocalDate());
    }

    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        return date1 != null && date2 != null && date1.isEqual(date2);
    }

    public static boolean isIn(ZonedDateTime zonedDateTime, ZonedDateTime beginDate, ZonedDateTime endDate) {
        return zonedDateTime.isAfter(beginDate) && zonedDateTime.isBefore(endDate);
    }

    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        if (null == zonedDateTime) {
            return null;
        }
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * 日期范围
     *
     * @author looly
     * @since 4.1.0
     */
    static class ZonedDateTimeRange extends Range<ZonedDateTime> {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 构造，包含开始和结束日期时间
         *
         * @param start 起始日期时间（包括）
         * @param end   结束日期时间（包括）
         * @param unit  步进单位
         */
        public ZonedDateTimeRange(ZonedDateTime start, ZonedDateTime end, ChronoUnit unit) {
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
        public ZonedDateTimeRange(ZonedDateTime start, ZonedDateTime end, ChronoUnit unit, int step) {
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
        public ZonedDateTimeRange(ZonedDateTime start, ZonedDateTime end, ChronoUnit unit, int step, boolean isIncludeStart, boolean isIncludeEnd) {
            super(start, end, (current, end1, index) -> {
                final ZonedDateTime zonedDateTime = start.plus((long) (index + 1) * step, unit);
                if (zonedDateTime.isAfter(end1)) {
                    return null;
                }
                return zonedDateTime;
            }, isIncludeStart, isIncludeEnd);
        }

    }

}
