package com.takeshi.jackson;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import java.time.format.DateTimeFormatter;

/**
 * OffsetTimeTakeshiDeserializer
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/6/20 12:11
 */
@JacksonStdImpl
public class OffsetTimeTakeshiDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer {

    protected OffsetTimeTakeshiDeserializer() {
        super(DateTimeFormatter.ISO_OFFSET_TIME);
    }

    public static final OffsetTimeTakeshiDeserializer INSTANCE = new OffsetTimeTakeshiDeserializer();

}
