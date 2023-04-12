package com.takeshi.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * LocalDataTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class LocalDataTypeAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDate.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDate.toString());
    }

}