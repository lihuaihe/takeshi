package com.takeshi.jackson;

import cn.hutool.core.util.NumberUtil;
import com.takeshi.annotation.CurrencyConversion;
import com.takeshi.util.TakeshiUtil;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * 货币转化，只适用于非json参数
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class CurrencyConversionAnnotationFormatterFactory implements AnnotationFormatterFactory<CurrencyConversion> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        return Collections.singleton(BigDecimal.class);
    }

    @Override
    public Printer<?> getPrinter(CurrencyConversion annotation, Class<?> fieldType) {
        return getFormatter(annotation);
    }

    @Override
    public Parser<?> getParser(CurrencyConversion annotation, Class<?> fieldType) {
        return getFormatter(annotation);
    }

    /**
     * 格式
     *
     * @param annotation 注解
     * @return 格式
     */
    protected Formatter<BigDecimal> getFormatter(CurrencyConversion annotation) {
        return new Formatter<>() {
            @Override
            public String print(BigDecimal value, Locale locale) {
                return NumberUtil.decimalFormat(annotation.pattern(), TakeshiUtil.currencyToYuan(value));
            }

            @Override
            public BigDecimal parse(String text, Locale locale) throws ParseException {
                return TakeshiUtil.currencyToCent(NumberUtil.toBigDecimal(text));
            }
        };
    }

}
