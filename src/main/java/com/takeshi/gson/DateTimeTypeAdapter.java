package com.takeshi.gson;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * DateTimeTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/09/14 14:54
 */
public class DateTimeTypeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return DateUtil.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(DateTime dateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateTime.toString());
    }

}