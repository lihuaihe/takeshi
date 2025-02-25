package com.takeshi.gson;

import com.google.gson.*;
import com.takeshi.constants.TakeshiDatePattern;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * LocalDataTimeTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class LocalDataTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.format(TakeshiDatePattern.NORM_DATETIME_FORMATTER));
    }

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDateTime.parse(jsonElement.getAsString(), TakeshiDatePattern.NORM_DATETIME_FORMATTER);
    }

}