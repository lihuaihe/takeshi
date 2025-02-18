package com.takeshi.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZoneId;

/**
 * ZoneIdTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class ZoneIdTypeAdapter implements JsonSerializer<ZoneId>, JsonDeserializer<ZoneId> {

    @Override
    public ZoneId deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ZoneId.of(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(ZoneId zoneId, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(zoneId.toString());
    }

}