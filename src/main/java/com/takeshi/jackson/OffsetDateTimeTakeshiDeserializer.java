package com.takeshi.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * OffsetDateTimeTakeshiDeserializer
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/6/20 12:00
 */
@JacksonStdImpl
public class OffsetDateTimeTakeshiDeserializer extends StdDeserializer<OffsetDateTime> {

    protected OffsetDateTimeTakeshiDeserializer() {
        super(ZonedDateTime.class);
    }

    public static final OffsetDateTimeTakeshiDeserializer INSTANCE = new OffsetDateTimeTakeshiDeserializer();

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getText();
        return StrUtil.isNotBlank(value) ? OffsetDateTime.parse(value) : null;
    }

}
