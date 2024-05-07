package com.takeshi.constants;

import cn.hutool.http.useragent.Platform;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * 常量池
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface TakeshiConstants {

    /**
     * 安卓平板
     */
    Platform ANDROID_TABLET = new Platform("AndroidTablet", "android.*tablet");

    /**
     * 需要排除的页面路径
     */
    String[] EXCLUDE_URL = {"/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**", "/v3/api-docs/**", "/doc.html", "/favicon.ico", "/error"};

    /**
     * 版本号校验正则
     */
    String VERSION_REGEXP = "^\\d+(?:\\.\\d+){2}$";

    /**
     * yyyy-MM-dd HH:mm:ss日期时间格式校验正则
     */
    String DATE_TIME_REGEXP = "^\\d{4}(-)(1[0-2]|0\\d)\\1([0-2]\\d|30|31) (?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$";

    /**
     * yyyy-MM-dd日期格式校验正则
     */
    String DATE_REGEXP = "^\\d{4}(-)(1[0-2]|0\\d)\\1([0-2]\\d|30|31)$";

    /**
     * 英文月份的日期校验正则
     * dd MMM yyyy
     * 示例：02 Jan 2022
     */
    String DATE_US_REGEXP = "^([0-2]\\d|30|31) (Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May?|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember)?) \\d{4}$";

    /**
     * 日期时间格式化，毫秒保留3位，例如：2022-01-01T04:34:56.000Z
     */
    DateTimeFormatter INSTANT_FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive().appendInstant(3).toFormatter();

}
