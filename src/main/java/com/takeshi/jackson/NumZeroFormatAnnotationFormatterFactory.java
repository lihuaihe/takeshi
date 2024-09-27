package com.takeshi.jackson;

import cn.hutool.core.util.NumberUtil;
import com.takeshi.annotation.NumZeroFormat;
import com.takeshi.constraints.VerifyPhoneNumber;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * 数字字符串去掉首位的零，注解格式化工厂方式，只适用于非json参数
 * <br/>
 * 之前该注解主要是用于处理手机号码前面的0，现在可改成使用 {@link VerifyPhoneNumber}
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Deprecated
public class NumZeroFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<NumZeroFormat> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        return Collections.singleton(String.class);
    }

    @Override
    public Printer<?> getPrinter(NumZeroFormat annotation, Class<?> fieldType) {
        return getFormatter();
    }

    @Override
    public Parser<?> getParser(NumZeroFormat annotation, Class<?> fieldType) {
        return getFormatter();
    }

    /**
     * Formatter
     *
     * @return Formatter
     */
    protected Formatter<String> getFormatter() {
        return new Formatter<>() {
            @Override
            public String print(String object, Locale locale) {
                return object;
            }

            @Override
            public String parse(String text, Locale locale) {
                return NumberUtil.isNumber(text) ? NumberUtil.parseNumber(text).toString() : text;
            }
        };
    }

}
