package com.takeshi.jackson;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * BigDecimalFormatDeserializer
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/6/20 12:00
 */
@JacksonStdImpl
public class BigDecimalFormatDeserializer extends StdDeserializer<BigDecimal> {

    protected BigDecimalFormatDeserializer() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (ObjUtil.isEmpty(p.getText())) {
            return null;
        }
        return NumberUtil.toBigDecimal(p.getText());
    }

}
