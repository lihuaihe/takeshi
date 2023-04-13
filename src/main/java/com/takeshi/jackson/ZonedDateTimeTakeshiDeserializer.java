package com.takeshi.jackson;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * ZonedDateTimeTakeshiDeserializer
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JacksonStdImpl
public class ZonedDateTimeTakeshiDeserializer extends StdDeserializer<ZonedDateTime> {

    /**
     * 构造函数
     */
    protected ZonedDateTimeTakeshiDeserializer() {
        super(ZonedDateTime.class);
    }

    /**
     * ZonedDateTimeTakeshiDeserializer Instance
     */
    public static final ZonedDateTimeTakeshiDeserializer INSTANCE = new ZonedDateTimeTakeshiDeserializer();

    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getText();
        return StrUtil.isNotBlank(value) ? ZonedDateTime.parse(value) : null;
    }

}
