package com.takeshi.constants;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.format.FastDateFormat;

import java.time.format.DateTimeFormatter;

/**
 * 日期格式化类
 *
 * @author 七濑武【Nanase Takeshi】
 */
public abstract class TakeshiDatePattern extends DatePattern {

    /**
     * yyyy-MM-dd'T'HH:mm:ss
     */
    public static final DateTimeFormatter UTC_SIMPLE_FORMATTER = createFormatter(UTC_SIMPLE_PATTERN);

    /**
     * MM-dd
     */
    public static final String NORM_MONTH_DAY_PATTERN = "MM-dd";

    /**
     * MM-dd
     */
    public static final FastDateFormat NORM_MONTH_DAY_FORMAT = FastDateFormat.getInstance(NORM_MONTH_DAY_PATTERN);

    /**
     * MM-dd
     */
    public static final DateTimeFormatter NORM_MONTH_DAY_FORMATTER = createFormatter(NORM_MONTH_DAY_PATTERN);

    /**
     * dd MMM yyyy
     */
    public static final String NORM_DATE_PATTERN_REVERSE = "dd MMM yyyy";

    /**
     * dd MMM yyyy
     */
    public static final FastDateFormat NORM_DATE_REVERSE_FORMAT = FastDateFormat.getInstance(NORM_DATE_PATTERN_REVERSE);

    /**
     * dd MMM yyyy
     */
    public static final DateTimeFormatter NORM_DATE_REVERSE_FORMATTER = createFormatter(NORM_DATE_PATTERN_REVERSE);

    /**
     * yyyy MMM
     */
    public static final String SIMPLE_MONTH_PATTERN = "yyyy MMM";

    /**
     * yyyy MMM
     */
    public static final FastDateFormat SIMPLE_MONTH_FORMAT = FastDateFormat.getInstance(SIMPLE_MONTH_PATTERN);

    /**
     * yyyy MMM
     */
    public static final DateTimeFormatter SIMPLE_MONTH_FORMATTER = createFormatter(SIMPLE_MONTH_PATTERN);

    /**
     * MMM yyyy
     */
    public static final String SIMPLE_MONTH_PATTERN_REVERSE = "MMM yyyy";

    /**
     * MMM yyyy
     */
    public static final FastDateFormat SIMPLE_MONTH_REVERSE_FORMAT = FastDateFormat.getInstance(SIMPLE_MONTH_PATTERN_REVERSE);

    /**
     * MMM yyyy
     */
    public static final DateTimeFormatter SIMPLE_MONTH_REVERSE_FORMATTER = createFormatter(SIMPLE_MONTH_PATTERN_REVERSE);

    /**
     * yyyy/MM/dd
     */
    public static final String SLASH_SEPARATOR_DATE_PATTERN = "yyyy/MM/dd";

    /**
     * yyyy/MM/dd
     */
    public static final FastDateFormat SLASH_SEPARATOR_DATE_PATTERN_FORMAT = FastDateFormat.getInstance(SLASH_SEPARATOR_DATE_PATTERN);

    /**
     * yyyy/MM/dd
     */
    public static final DateTimeFormatter SLASH_SEPARATOR_DATE_PATTERN_FORMATTER = createFormatter(SLASH_SEPARATOR_DATE_PATTERN);

    /**
     * 构造函数
     */
    private TakeshiDatePattern() {
    }

}
