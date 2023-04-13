package com.takeshi.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetTime;

/**
 * OffsetTimeTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class OffsetTimeTypeAdapter implements JsonSerializer<OffsetTime>, JsonDeserializer<OffsetTime> {

    @Override
    public OffsetTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return OffsetTime.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(OffsetTime offsetTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(offsetTime.toString());
    }

}