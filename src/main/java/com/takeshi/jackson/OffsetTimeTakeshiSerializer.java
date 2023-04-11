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
 * @date 2022/6/20 12:11
 */
@JacksonStdImpl
public class OffsetTimeTakeshiSerializer extends StdSerializer<OffsetTime> {

    protected OffsetTimeTakeshiSerializer() {
        super(OffsetTime.class);
    }

    public static final OffsetTimeTakeshiSerializer INSTANCE = new OffsetTimeTakeshiSerializer();

    @Override
    public void serialize(OffsetTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }

}
