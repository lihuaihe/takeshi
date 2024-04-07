package com.takeshi.jackson;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.takeshi.annotation.CurrencyConversion;
import com.takeshi.util.TakeshiUtil;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * CurrencyToYuanSerializer
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JacksonStdImpl
public class CurrencyToYuanSerializer extends StdSerializer<BigDecimal> implements ContextualSerializer {

    /**
     * pattern
     */
    private String pattern;

    /**
     * 构造函数
     */
    protected CurrencyToYuanSerializer() {
        super(BigDecimal.class);
    }

    /**
     * 有参构造函数
     *
     * @param pattern pattern
     */
    protected CurrencyToYuanSerializer(String pattern) {
        super(BigDecimal.class);
        this.pattern = pattern;
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(NumberUtil.decimalFormat(pattern, TakeshiUtil.currencyToYuan(value)));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        CurrencyConversion annotation = property.getAnnotation(CurrencyConversion.class);
        return new CurrencyToYuanSerializer(annotation.pattern());
    }

}
