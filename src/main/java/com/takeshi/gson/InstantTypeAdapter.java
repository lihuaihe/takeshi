package com.takeshi.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

/**
 * InstantTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class InstantTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Instant.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(Instant instant, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(instant.toString());
    }

}