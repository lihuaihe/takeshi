package com.takeshi.jackson;

import cn.hutool.core.util.NumberUtil;
import com.takeshi.annotation.BigDecimalFormat;
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
 * 格式化为数字，只适用于非json参数
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class BigDecimalFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<BigDecimalFormat> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        return Collections.singleton(BigDecimal.class);
    }

    @Override
    public Printer<?> getPrinter(BigDecimalFormat annotation, Class<?> fieldType) {
        return getFormatter(annotation);
    }

    @Override
    public Parser<?> getParser(BigDecimalFormat annotation, Class<?> fieldType) {
        return getFormatter(annotation);
    }

    /**
     * 格式
     *
     * @param annotation 注解
     * @return 格式
     */
    protected Formatter<BigDecimal> getFormatter(BigDecimalFormat annotation) {
        return new Formatter<>() {
            @Override
            public String print(BigDecimal value, Locale locale) {
                return NumberUtil.decimalFormat(annotation.pattern(), value);
            }

            @Override
            public BigDecimal parse(String text, Locale locale) throws ParseException {
                return NumberUtil.toBigDecimal(text);
            }
        };
    }

}
