package com.takeshi.jackson;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.takeshi.util.TakeshiUtil;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * CurrencyToCentDeserializer
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JacksonStdImpl
public class CurrencyToCentDeserializer extends StdDeserializer<BigDecimal> {

    /**
     * 构造函数
     */
    protected CurrencyToCentDeserializer() {
        super(BigDecimal.class);
    }

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return TakeshiUtil.currencyToCent(NumberUtil.toBigDecimal(p.getText()));
    }

}
