package com.takeshi.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.OffsetTime;

/**
 * OffsetTimeTakeshiSerializer
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JacksonStdImpl
public class OffsetTimeTakeshiSerializer extends StdSerializer<OffsetTime> {

    /**
     * 构造函数
     */
    protected OffsetTimeTakeshiSerializer() {
        super(OffsetTime.class);
    }

    /**
     * OffsetTimeTakeshiSerializer Instance
     */
    public static final OffsetTimeTakeshiSerializer INSTANCE = new OffsetTimeTakeshiSerializer();

    @Override
    public void serialize(OffsetTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }

}
