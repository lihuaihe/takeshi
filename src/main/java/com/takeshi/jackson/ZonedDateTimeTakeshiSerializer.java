package com.takeshi.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * ZonedDateTimeTakeshiSerializer
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/6/20 12:11
 */
@JacksonStdImpl
public class ZonedDateTimeTakeshiSerializer extends StdSerializer<ZonedDateTime> {

    protected ZonedDateTimeTakeshiSerializer() {
        super(ZonedDateTime.class);
    }

    public static final ZonedDateTimeTakeshiSerializer INSTANCE = new ZonedDateTimeTakeshiSerializer();

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }

}
