package com.takeshi.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * OffsetDateTimeTakeshiSerializer
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/6/20 12:11
 */
@JacksonStdImpl
public class OffsetDateTimeTakeshiSerializer extends StdSerializer<OffsetDateTime> {

    protected OffsetDateTimeTakeshiSerializer() {
        super(OffsetDateTime.class);
    }

    public static final OffsetDateTimeTakeshiSerializer INSTANCE = new OffsetDateTimeTakeshiSerializer();

    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }

}
