package com.takeshi.jackson;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import java.time.format.DateTimeFormatter;

/**
 * OffsetTimeTakeshiDeserializer
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JacksonStdImpl
public class OffsetTimeTakeshiDeserializer extends com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer {

    /**
     * 构造函数
     */
    protected OffsetTimeTakeshiDeserializer() {
        super(DateTimeFormatter.ISO_OFFSET_TIME);
    }

    /**
     * OffsetTimeTakeshiDeserializer Instance
     */
    public static final OffsetTimeTakeshiDeserializer INSTANCE = new OffsetTimeTakeshiDeserializer();

}
