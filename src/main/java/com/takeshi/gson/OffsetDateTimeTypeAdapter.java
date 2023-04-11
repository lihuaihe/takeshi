package com.takeshi.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

/**
 * OffsetDateTimeTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/09/14 14:54
 */
public class OffsetDateTimeTypeAdapter implements JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return OffsetDateTime.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(OffsetDateTime offsetDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(offsetDateTime.toString());
    }

}