package com.takeshi.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Range;
import cn.hutool.core.util.ObjUtil;
import com.takeshi.constants.TakeshiDatePattern;

import java.io.Serial;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * ZonedDateTimeUtil
 *
 * @author 七濑武【Nanase Takeshi】
 */
public final class ZonedDateTimeUtil {

    /**
     * 构造函数
     */
    private ZonedDateTimeUtil() {
    }

    /**
     * now
     *
     * @return ZonedDateTime
     */
    public static ZonedDateTime now() {
        return ZonedDateTime.now();
    }

    /**
     * now
     *
     * @param clock clock
     * @return ZonedDateTime
     */
    public static ZonedDateTime now(Clock clock) {
        return ZonedDateTime.now(clock);
    }

    /**
     * now
     *
     * @param zoneId zoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime now(ZoneId zoneId) {
        return ZonedDateTime.now(zoneId);
    }

    /**
     * of
     *
     * @param year         year
     * @param month        month
     * @param dayOfMonth   dayOfMonth
     * @param hour         hour
     * @param minute       minute
     * @param second       second
     * @param nanoOfSecond nanoOfSecond
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        return of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, ZoneId.systemDefault());
    }

    /**
     * of
     *
     * @param year         year
     * @param month        month
     * @param dayOfMonth   dayOfMonth
     * @param hour         hour
     * @param minute       minute
     * @param second       second
     * @param nanoOfSecond nanoOfSecond
     * @param timeZone     timeZone
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, TimeZone timeZone) {
        return of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, timeZone.toZoneId());
    }

    /**
     * of
     *
     * @param year         year
     * @param month        month
     * @param dayOfMonth   dayOfMonth
     * @param hour         hour
     * @param minute       minute
     * @param second       second
     * @param nanoOfSecond nanoOfSecond
     * @param zoneId       zoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond, ZoneId zoneId) {
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zoneId);
    }

    /**
     * ofInstant
     *
     * @param instant instant
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofInstant(Instant instant) {
        return ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * ofInstant
     *
     * @param instant  instant
     * @param timeZone timeZone
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofInstant(Instant instant, TimeZone timeZone) {
        return ofInstant(instant, ObjUtil.defaultIfNull(timeZone, TimeZone::getDefault).toZoneId());
    }

    /**
     * ofInstant
     *
     * @param instant instant
     * @param zoneId  zoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofInstant(Instant instant, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    /**
     * ofDate
     *
     * @param date date
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofDate(Date date) {
        if (date instanceof DateTime) {
            return ofInstant(date.toInstant(), ((DateTime) date).getZoneId());
        }
        return ofInstant(date.toInstant());
    }

    /**
     * of
     *
     * @param localDateTime localDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
    }

    /**
     * of
     *
     * @param localDateTime localDateTime
     * @param timeZone      timeZone
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(LocalDateTime localDateTime, TimeZone timeZone) {
        return ZonedDateTime.of(localDateTime, timeZone.toZoneId());
    }

    /**
     * of
     *
     * @param localDateTime localDateTime
     * @param zoneId        zoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(LocalDateTime localDateTime, ZoneId zoneId) {
        return ZonedDateTime.of(localDateTime, zoneId);
    }

    /**
     * of
     *
     * @param localDate localDate
     * @param timeZone  timeZone
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(LocalDate localDate, TimeZone timeZone) {
        return of(localDate, LocalTime.now(), timeZone.toZoneId());
    }

    /**
     * of
     *
     * @param localDate localDate
     * @param zoneId    zoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(LocalDate localDate, ZoneId zoneId) {
        return of(localDate, LocalTime.now(), zoneId);
    }

    /**
     * of
     *
     * @param localDate localDate
     * @param localTime localTime
     * @param timeZone  timeZone
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(LocalDate localDate, LocalTime localTime, TimeZone timeZone) {
        return ZonedDateTime.of(localDate, localTime, timeZone.toZoneId());
    }

    /**
     * of
     *
     * @param localDate localDate
     * @param localTime localTime
     * @param zoneId    zoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime of(LocalDate localDate, LocalTime localTime, ZoneId zoneId) {
        return ZonedDateTime.of(localDate, localTime, zoneId);
    }

    /**
     * ofLocal
     *
     * @param localDateTime   localDateTime
     * @param preferredOffset preferredOffset
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofLocal(LocalDateTime localDateTime, ZoneOffset preferredOffset) {
        return ZonedDateTime.ofLocal(localDateTime, ZoneId.systemDefault(), preferredOffset);
    }

    /**
     * ofLocal
     *
     * @param localDateTime   localDateTime
     * @param zoneId          zoneId
     * @param preferredOffset preferredOffset
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofLocal(LocalDateTime localDateTime, ZoneId zoneId, ZoneOffset preferredOffset) {
        return ZonedDateTime.ofLocal(localDateTime, zoneId, preferredOffset);
    }

    /**
     * ofEpochSecond
     *
     * @param epochSecond epochSecond
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofEpochSecond(long epochSecond) {
        return ofInstant(Instant.ofEpochSecond(epochSecond));
    }

    /**
     * ofEpochSecond
     *
     * @param epochSecond epochSecond
     * @param zoneId      zoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofEpochSecond(long epochSecond, ZoneId zoneId) {
        return ofInstant(Instant.ofEpochSecond(epochSecond), zoneId);
    }

    /**
     * ofEpochSecond
     *
     * @param epochSecond epochSecond
     * @param timeZone    timeZone
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofEpochSecond(long epochSecond, TimeZone timeZone) {
        return ofInstant(Instant.ofEpochSecond(epochSecond), timeZone);
    }

    /**
     * ofEpochMilli
     *
     * @param epochMilli epochMilli
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofEpochMilli(long epochMilli) {
        return ofInstant(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * ofEpochMilli
     *
     * @param epochMilli epochMilli
     * @param zoneId     zoneId
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofEpochMilli(long epochMilli, ZoneId zoneId) {
        return ofInstant(Instant.ofEpochMilli(epochMilli), zoneId);
    }

    /**
     * ofEpochMilli
     *
     * @param epochMilli epochMilli
     * @param timeZone   timeZone
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofEpochMilli(long epochMilli, TimeZone timeZone) {
        return ofInstant(Instant.ofEpochMilli(epochMilli), timeZone);
    }

    /**
     * ofEpochSecondUtc
     *
     * @param epochSecond epochSecond
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofEpochSecondUtc(long epochSecond) {
        return ofUtc(Instant.ofEpochSecond(epochSecond));
    }

    /**
     * ofEpochMilliUtc
     *
     * @param epochMilli epochMilli
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofEpochMilliUtc(long epochMilli) {
        return ofUtc(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * ofUtc
     *
     * @param instant instant
     * @return ZonedDateTime
     */
    public static ZonedDateTime ofUtc(Instant instant) {
        return ofInstant(instant, ZoneId.of("UTC"));
    }

    /**
     * from
     *
     * @param temporalAccessor temporalAccessor
     * @return ZonedDateTime
     */
    public static ZonedDateTime from(TemporalAccessor temporalAccessor) {
        return ZonedDateTime.from(temporalAccessor);
    }

    /**
     * parse
     *
     * @param text text
     * @return ZonedDateTime
     */
    public static ZonedDateTime parse(CharSequence text) {
        return parse(text, null);
    }

    /**
     * parse
     *
     * @param text      text
     * @param formatter formatter
     * @return ZonedDateTime
     */
    public static ZonedDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        if (null == text) {
            return null;
        }
        if (null == formatter) {
            return ZonedDateTime.parse(text);
        }
        return ZonedDateTime.parse(text, formatter);
    }

    /**
     * formatNormal
     *
     * @param zonedDateTime zonedDateTime
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formatNormal(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, TakeshiDatePattern.NORM_DATETIME_FORMATTER);
    }

    /**
     * formatNormalDate
     *
     * @param zonedDateTime zonedDateTime
     * @return yyyy-MM-dd
     */
    public static String formatNormalDate(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, TakeshiDatePattern.NORM_DATE_FORMATTER);
    }

    /**
     * format
     *
     * @param zonedDateTime zonedDateTime
     * @param formatter     formatter
     * @return String
     */
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
     */
    public static Duration between(ZonedDateTime startTimeInclude, ZonedDateTime endTimeExclude) {
        return Duration.between(startTimeInclude, endTimeExclude);
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
        return unit.between(startTimeInclude, endTimeExclude);
    }

    /**
     * 根据步进单位获取起始日期时间和结束日期时间的时间区间集合
     *
     * @param start 起始日期时间
     * @param end   结束日期时间
     * @param unit  步进单位
     * @return List
     */
    public static List<ZonedDateTime> rangeToList(ZonedDateTime start, ZonedDateTime end, ChronoUnit unit) {
        return StreamSupport.stream(new ZonedDateTimeRange(start, end, unit).spliterator(), false).collect(Collectors.toList());
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
    public static List<ZonedDateTime> rangeToList(ZonedDateTime start, ZonedDateTime end, final ChronoUnit unit, int step) {
        return StreamSupport.stream(new ZonedDateTimeRange(start, end, unit, step).spliterator(), false).collect(Collectors.toList());
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

    /**
     * 获取某天的开始时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime beginOfDay(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(LocalTime.MIN);
    }

    /**
     * 获取某天的结束时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfDay(ZonedDateTime zonedDateTime) {
        return endOfDay(zonedDateTime, false);
    }

    /**
     * 获取某天的结束时间
     *
     * @param zonedDateTime       zonedDateTime
     * @param truncateMillisecond 是否截断毫秒
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfDay(ZonedDateTime zonedDateTime, boolean truncateMillisecond) {
        if (truncateMillisecond) {
            return zonedDateTime.with(LocalTime.of(23, 59, 59));
        }
        return zonedDateTime.with(LocalTime.MAX);
    }

    /**
     * 获取某周的开始时间，周一定为一周的开始
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime beginOfWeek(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
    }

    /**
     * 获取某周的结束时间，周日定为一周的结束
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfWeek(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MAX);
    }

    /**
     * 获取某个时间的上周的开始时间，周一定为一周的开始
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime beginOfLastWeek(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1L).with(LocalTime.MIN);
    }

    /**
     * 获取某个时间上周的结束时间，周日定为一周的结束
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfLastWeek(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).with(LocalTime.MAX);
    }

    /**
     * 获取某个时间的下周的开始时间，周一定为一周的开始
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime beginOfNextWeek(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).with(LocalTime.MIN);
    }

    /**
     * 获取某个时间下周的结束时间，周日定为一周的结束
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfNextWeek(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusWeeks(1L).with(LocalTime.MAX);
    }

    /**
     * 获取某月的开始时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime beginOfMonth(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
    }

    /**
     * 获取某月的结束时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfMonth(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
    }

    /**
     * 获取某个时间上月的开始时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime beginOfLastMonth(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1L).with(LocalTime.MIN);
    }

    /**
     * 获取某个时间上月的结束时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfLastMonth(ZonedDateTime zonedDateTime) {
        return zonedDateTime.minusMonths(1L).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
    }

    /**
     * 获取某个时间下月的开始时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime beginOfNextMonth(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1L).with(LocalTime.MIN);
    }

    /**
     * 获取某个时间下月的结束时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfNextMonth(ZonedDateTime zonedDateTime) {
        return zonedDateTime.plusMonths(1L).with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
    }

    /**
     * 获取某年的开始时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime beginOfYear(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.firstDayOfYear()).with(LocalTime.MIN);
    }

    /**
     * 获取某年的结束时间
     *
     * @param zonedDateTime zonedDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime endOfYear(ZonedDateTime zonedDateTime) {
        return zonedDateTime.with(TemporalAdjusters.lastDayOfYear()).with(LocalTime.MAX);
    }


    /**
     * 判断是否是周末
     *
     * @param zonedDateTime zonedDateTime
     * @return boolean
     */
    public static boolean isWeekend(ZonedDateTime zonedDateTime) {
        DayOfWeek dayOfWeek = zonedDateTime.getDayOfWeek();
        return DayOfWeek.SATURDAY == dayOfWeek || DayOfWeek.SUNDAY == dayOfWeek;
    }

    /**
     * 判断两个日期时间是否是同一天
     *
     * @param date1 date1
     * @param date2 date2
     * @return boolean
     */
    public static boolean isSameDay(ZonedDateTime date1, ZonedDateTime date2) {
        return date1 != null && date2 != null && isSameDay(date1.toLocalDate(), date2.toLocalDate());
    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1 date1
     * @param date2 date2
     * @return boolean
     */
    public static boolean isSameDay(LocalDate date1, LocalDate date2) {
        return date1 != null && date2 != null && date1.isEqual(date2);
    }

    /**
     * 是否在两个日期时间之间，不包含开始和结束日期时间
     *
     * @param zonedDateTime zonedDateTime
     * @param beginDate     beginDate
     * @param endDate       endDate
     * @return boolean
     */
    public static boolean isIn(ZonedDateTime zonedDateTime, ZonedDateTime beginDate, ZonedDateTime endDate) {
        return zonedDateTime.isAfter(beginDate) && zonedDateTime.isBefore(endDate);
    }

    /**
     * 转LocalDateTime
     *
     * @param zonedDateTime zonedDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        if (null == zonedDateTime) {
            return null;
        }
        return zonedDateTime.toLocalDateTime();
    }

    /**
     * 某个时间到当天结束时间的间隔
     *
     * @param zonedDateTime zonedDateTime
     * @return Duration
     */
    public static Duration untilEndOfDay(ZonedDateTime zonedDateTime) {
        return Duration.between(zonedDateTime, ZonedDateTime.now(zonedDateTime.getZone()).with(LocalTime.MAX));
    }

    /**
     * 到当天结束时间的间隔
     *
     * @return Duration
     */
    public static Duration untilEndOfDay() {
        return untilEndOfDay(ZonedDateTime.now());
    }

    /**
     * 到当天结束时间的间隔
     *
     * @param zoneId zoneId
     * @return Duration
     */
    public static Duration untilEndOfDay(ZoneId zoneId) {
        return untilEndOfDay(ZonedDateTime.now(zoneId));
    }

    /**
     * 某个时间到本周日结束时间的间隔
     *
     * @param zonedDateTime zonedDateTime
     * @return Duration
     */
    public static Duration untilEndOfWeek(ZonedDateTime zonedDateTime) {
        return Duration.between(zonedDateTime, endOfWeek(ZonedDateTime.now(zonedDateTime.getZone())));
    }

    /**
     * 到本周日结束时间的间隔
     *
     * @return Duration
     */
    public static Duration untilEndOfWeek() {
        return untilEndOfWeek(ZonedDateTime.now());
    }

    /**
     * 到本周日结束时间的间隔
     *
     * @param zoneId zoneId
     * @return Duration
     */
    public static Duration untilEndOfWeek(ZoneId zoneId) {
        return untilEndOfWeek(ZonedDateTime.now(zoneId));
    }

    /**
     * 某个时间到本月结束时间的间隔
     *
     * @param zonedDateTime zonedDateTime
     * @return Duration
     */
    public static Duration untilEndOfMonth(ZonedDateTime zonedDateTime) {
        return Duration.between(zonedDateTime, endOfMonth(ZonedDateTime.now(zonedDateTime.getZone())));
    }

    /**
     * 到本月结束时间的间隔
     *
     * @return Duration
     */
    public static Duration untilEndOfMonth() {
        return untilEndOfMonth(ZonedDateTime.now());
    }

    /**
     * 到本月结束时间的间隔
     *
     * @param zoneId zoneId
     * @return Duration
     */
    public static Duration untilEndOfMonth(ZoneId zoneId) {
        return untilEndOfMonth(ZonedDateTime.now(zoneId));
    }

    /**
     * 某个时间到本年结束时间的间隔
     *
     * @param zonedDateTime zonedDateTime
     * @return Duration
     */
    public static Duration untilEndOfYear(ZonedDateTime zonedDateTime) {
        return Duration.between(zonedDateTime, endOfYear(ZonedDateTime.now(zonedDateTime.getZone())));
    }

    /**
     * 到本年结束时间的间隔
     *
     * @return Duration
     */
    public static Duration untilEndOfYear() {
        return untilEndOfYear(ZonedDateTime.now());
    }

    /**
     * 到本年结束时间的间隔
     *
     * @param zoneId zoneId
     * @return Duration
     */
    public static Duration untilEndOfYear(ZoneId zoneId) {
        return untilEndOfYear(ZonedDateTime.now(zoneId));
    }

    /**
     * 日期范围
     *
     * @author looly
     */
    public static class ZonedDateTimeRange extends Range<ZonedDateTime> {

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
                final ZonedDateTime zonedDateTime = start.plus((index + 1L) * step, unit);
                if (zonedDateTime.isAfter(end1)) {
                    return null;
                }
                return zonedDateTime;
            }, isIncludeStart, isIncludeEnd);
        }

    }

}
