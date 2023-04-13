package com.takeshi.gson;

import com.google.gson.*;
import com.takeshi.constants.TakeshiDatePattern;

import java.lang.reflect.Type;
import java.time.MonthDay;

/**
 * MonthDayTypeAdapter
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class MonthDayTypeAdapter implements JsonSerializer<MonthDay>, JsonDeserializer<MonthDay> {

    @Override
    public MonthDay deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return MonthDay.parse(jsonElement.getAsString(), TakeshiDatePattern.NORM_MONTH_DAY_FORMATTER);
    }

    @Override
    public JsonElement serialize(MonthDay monthDay, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(monthDay.format(TakeshiDatePattern.NORM_MONTH_DAY_FORMATTER));
    }

}