package com.takeshi.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.YearMonth;

/**
 * YearMonthTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/09/14 14:54
 */
public class YearMonthTypeAdapter implements JsonSerializer<YearMonth>, JsonDeserializer<YearMonth> {

    @Override
    public YearMonth deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return YearMonth.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(YearMonth yearMonth, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(yearMonth.toString());
    }

}