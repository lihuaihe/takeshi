package com.takeshi.gson;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * LongTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2023/3/14 15:37
 */
public class LongTypeAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return jsonElement.getAsLong();
    }

    @Override
    public JsonElement serialize(Long val, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(String.valueOf(val));
    }

}
