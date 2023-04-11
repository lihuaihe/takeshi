package com.takeshi.jackson;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * DateTakeshiDeserializer
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/6/20 12:11
 */
@JacksonStdImpl
public class DateTakeshiDeserializer extends StdDeserializer<Date> {

    public static final DateTakeshiDeserializer INSTANCE = new DateTakeshiDeserializer();

    protected DateTakeshiDeserializer() {
        super(Date.class);
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return DateUtil.parse(p.getText());
    }

}
