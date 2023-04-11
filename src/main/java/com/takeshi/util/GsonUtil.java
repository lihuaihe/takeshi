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

    public static Gson gson() {
        return GSON;
    }

    public static Gson gsonLongToString() {
        return GSON_LONG_TO_STRING;
    }

    public static Gson gsonIncludeNull() {
        return GSON_INCLUDE_NULL;
    }

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static String toJson(Object src, boolean ignoreNullValue) {
        return ignoreNullValue ? GSON.toJson(src) : GSON_INCLUDE_NULL.toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return GSON.toJson(src, typeOfSrc);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, typeOfT);
    }

}
