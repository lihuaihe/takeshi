package com.takeshi.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LongSerializationPolicy;
import com.takeshi.constants.TakeshiDatePattern;
import com.takeshi.gson.*;

import java.lang.reflect.Type;
import java.time.*;

/**
 * GsonUtil
 *
 * @author 七濑武【Nanase Takeshi】
 */
public final class GsonUtil {

    private static volatile Gson GSON = null;
    private static volatile Gson GSON_LONG_TO_STRING = null;
    private static volatile Gson GSON_INCLUDE_NULL = null;

    static {
        if (ObjUtil.isNull(GSON_INCLUDE_NULL)) {
            synchronized (GsonUtil.class) {
                if (ObjUtil.isNull(GSON_INCLUDE_NULL)) {
                    GsonBuilder gsonBuilder = new GsonBuilder()
                            .setDateFormat(TakeshiDatePattern.NORM_DATETIME_PATTERN)
                            .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
                            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                            .registerTypeAdapter(LocalDate.class, new LocalDataTypeAdapter())
                            .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                            .registerTypeAdapter(LocalDateTime.class, new LocalDataTimeTypeAdapter())
                            .registerTypeAdapter(Year.class, new YearTypeAdapter())
                            .registerTypeAdapter(YearMonth.class, new YearMonthTypeAdapter())
                            .registerTypeAdapter(MonthDay.class, new MonthDayTypeAdapter())
                            .registerTypeAdapter(OffsetTime.class, new OffsetTimeTypeAdapter())
                            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter());
                    GSON = gsonBuilder.create();
                    GSON_LONG_TO_STRING = gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING).create();
                    GSON_INCLUDE_NULL = gsonBuilder.serializeNulls().create();
                }
            }
        }
    }

    private GsonUtil() {
    }

    /**
     * 获取一个对日期进行格式化的基础Gson
     *
     * @return Gson
     */
    public static Gson gson() {
        return GSON;
    }

    /**
     * 获取一个基础GSON，且会将long转String
     *
     * @return Gson
     */
    public static Gson gsonLongToString() {
        return GSON_LONG_TO_STRING;
    }

    /**
     * 获取一个基础GSON，且包含null值
     *
     * @return Gson
     */
    public static Gson gsonIncludeNull() {
        return GSON_INCLUDE_NULL;
    }

    /**
     * Object to json String
     *
     * @param src sec
     * @return String
     */
    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    /**
     * Object to json String
     *
     * @param src             src
     * @param ignoreNullValue 是否忽略null值
     * @return String
     */
    public static String toJson(Object src, boolean ignoreNullValue) {
        return ignoreNullValue ? GSON.toJson(src) : GSON_INCLUDE_NULL.toJson(src);
    }

    /**
     * Object to json String
     *
     * @param src       src
     * @param typeOfSrc type
     * @return String
     */
    public static String toJson(Object src, Type typeOfSrc) {
        return GSON.toJson(src, typeOfSrc);
    }

    /**
     * String to Class
     *
     * @param json     json str
     * @param classOfT classOfT
     * @param <T>      T
     * @return T
     * @throws JsonSyntaxException Json语法异常
     */
    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * String to Class
     *
     * @param json    json str
     * @param typeOfT typeOfT
     * @param <T>     T
     * @return T
     * @throws JsonSyntaxException Json语法异常
     */
    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, typeOfT);
    }

}
