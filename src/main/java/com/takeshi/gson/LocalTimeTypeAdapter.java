package com.takeshi.gson;

import com.google.gson.*;
import com.takeshi.constants.TakeshiDatePattern;

import java.lang.reflect.Type;
import java.time.LocalTime;

/**
 * LocalTimeTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalTime.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(LocalTime localTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localTime.format(TakeshiDatePattern.NORM_TIME_FORMATTER));
    }

}