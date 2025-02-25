package com.takeshi.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Singleton;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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

    private GsonUtil() {
    }

    /**
     * 获取一个对日期进行格式化的基础GSON
     *
     * @return Gson
     */
    public static Gson gson() {
        return Singleton.get("gson", () -> new GsonBuilder()
                .setDateFormat(TakeshiDatePattern.NORM_DATETIME_PATTERN)
                .registerTypeHierarchyAdapter(ZoneId.class, new ZoneIdTypeAdapter())
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDataTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDataTimeTypeAdapter())
                .registerTypeAdapter(Year.class, new YearTypeAdapter())
                .registerTypeAdapter(YearMonth.class, new YearMonthTypeAdapter())
                .registerTypeAdapter(MonthDay.class, new MonthDayTypeAdapter())
                .registerTypeAdapter(OffsetTime.class, new OffsetTimeTypeAdapter())
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeTypeAdapter())
                .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
                .create());
    }

    /**
     * 获取一个对日期进行格式化的基础GSON，且会将long转String
     *
     * @return Gson
     */
    public static Gson gsonLongToString() {
        return Singleton.get("gsonLongToString", () -> gson().newBuilder().setLongSerializationPolicy(LongSerializationPolicy.STRING).create());
    }

    /**
     * 获取一个对日期进行格式化的基础GSON，且包含null值
     *
     * @return Gson
     */
    public static Gson gsonIncludeNull() {
        return Singleton.get("gsonIncludeNull", () -> gson().newBuilder().serializeNulls().create());
    }

    /**
     * Object to json String
     *
     * @param src sec
     * @return String
     */
    public static String toJson(Object src) {
        return gson().toJson(src);
    }

    /**
     * Object to json String
     *
     * @param src             src
     * @param ignoreNullValue 是否忽略null值
     * @return String
     */
    public static String toJson(Object src, boolean ignoreNullValue) {
        return ignoreNullValue ? gson().toJson(src) : gsonIncludeNull().toJson(src);
    }

    /**
     * Object to json String
     *
     * @param src       src
     * @param typeOfSrc type
     * @return String
     */
    public static String toJson(Object src, Type typeOfSrc) {
        return gson().toJson(src, typeOfSrc);
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
        return gson().fromJson(json, classOfT);
    }

    /**
     * String to Class
     *
     * @param json      json str
     * @param typeToken typeToken
     * @param <T>       T
     * @return T
     * @throws JsonSyntaxException Json语法异常
     */
    public static <T> T fromJson(String json, TypeToken<T> typeToken) throws JsonSyntaxException {
        return gson().fromJson(json, typeToken.getType());
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
        return gson().fromJson(json, typeOfT);
    }

}
