package com.takeshi.constants;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.format.FastDateFormat;

import java.time.format.DateTimeFormatter;

/**
 * 日期格式化类
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/6/20 10:00
 */
public final class TakeshiDatePattern extends DatePattern {

    public static final String NORM_MONTH_DAY_PATTERN = "MM-dd";
    public static final FastDateFormat NORM_MONTH_DAY_FORMAT = FastDateFormat.getInstance(NORM_MONTH_DAY_PATTERN);
    public static final DateTimeFormatter NORM_MONTH_DAY_FORMATTER = createFormatter(NORM_MONTH_DAY_PATTERN);

    public static final String NORM_DATE_PATTERN_REVERSE = "dd MMM yyyy";
    public static final FastDateFormat NORM_DATE_REVERSE_FORMAT = FastDateFormat.getInstance(NORM_DATE_PATTERN_REVERSE);
    public static final DateTimeFormatter NORM_DATE_REVERSE_FORMATTER = createFormatter(NORM_DATE_PATTERN_REVERSE);

    public static final String SIMPLE_MONTH_PATTERN = "yyyy MMM";
    public static final FastDateFormat SIMPLE_MONTH_FORMAT = FastDateFormat.getInstance(SIMPLE_MONTH_PATTERN);
    public static final DateTimeFormatter SIMPLE_MONTH_FORMATTER = createFormatter(SIMPLE_MONTH_PATTERN);

    public static final String SIMPLE_MONTH_PATTERN_REVERSE = "MMM yyyy";
    public static final FastDateFormat SIMPLE_MONTH_REVERSE_FORMAT = FastDateFormat.getInstance(SIMPLE_MONTH_PATTERN_REVERSE);
    public static final DateTimeFormatter SIMPLE_MONTH_REVERSE_FORMATTER = createFormatter(SIMPLE_MONTH_PATTERN_REVERSE);

    private TakeshiDatePattern() {
    }

}
