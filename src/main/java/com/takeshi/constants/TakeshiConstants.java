package com.takeshi.constants;

import com.takeshi.config.StaticConfig;
import com.takeshi.pojo.bo.SmsInfoBO;

/**
 * 常量池
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface TakeshiConstants {

    /**
     * 签名参数名
     */
    String SIGN_NAME = "sign";

    /**
     * 调用接口header里面传的时间戳字段（毫秒级）
     */
    String TIMESTAMP_NAME = "timestamp";

    /**
     * 日志追踪ID
     */
    String TRACE_ID_KEY = "traceId";

    /**
     * 需要过滤的swagger页面路径
     */
    String[] EXCLUDE_SWAGGER_URL = {"/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**", "/v3/api-docs/**", "/doc.html", "/favicon.ico", "/error"};

    /**
     * APP的header参数名：经度
     */
    String LONGITUDE = "Longitude";

    /**
     * APP的header参数名：纬度
     */
    String LATITUDE = "Latitude";

    /**
     * APP的header参数名：timezone
     */
    String TIMEZONE = "timezone";

    /**
     * APP的header参数名：app-version
     */
    String APP_VERSION = "app-version";

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
     * BYTES
     */
    byte[] BYTES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    /**
     * INTS
     */
    int[] INTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    /**
     * LONGS
     */
    long[] LONGS = {0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
    /**
     * STRINGS
     */
    String[] STRINGS = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    /**
     * 短信（0：登录，1：注册，2：忘记密码）
     */
    SmsInfoBO[] SMS_INFOS = {
            new SmsInfoBO(StaticConfig.applicationName + ":login:code:", "Login Verification Code: "),
            new SmsInfoBO(StaticConfig.applicationName + ":register:code:", "Register verification code: "),
            new SmsInfoBO(StaticConfig.applicationName + ":forgetPwd:code:", "Password reset verification code: ")
    };

}
