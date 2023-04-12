package com.takeshi.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Year;

/**
 * YearTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class YearTypeAdapter implements JsonSerializer<Year>, JsonDeserializer<Year> {

    @Override
    public Year deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Year.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(Year year, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(year.getValue());
    }

}