package com.takeshi.jackson;

import cn.hutool.core.util.ObjUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.takeshi.constraints.VerifyPhoneNumber;
import com.takeshi.util.GlobalPhoneNumberUtil;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * 全球手机号码格式化，注解格式化工厂方式，只适用于非json参数
 *
 * @author 七濑武【Nanase Takeshi】
 */

public class PhoneNumberAnnotationFormatterFactory implements AnnotationFormatterFactory<VerifyPhoneNumber> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        return Collections.singleton(String.class);
    }

    @Override
    public Printer<?> getPrinter(VerifyPhoneNumber annotation, Class<?> fieldType) {
        return getFormatter(annotation.defaultRegion());
    }

    @Override
    public Parser<?> getParser(VerifyPhoneNumber annotation, Class<?> fieldType) {
        return getFormatter(annotation.defaultRegion());
    }

    /**
     * Formatter
     *
     * @param defaultRegion 默认国家区域，例如：CN
     * @return Formatter
     */
    protected Formatter<String> getFormatter(String defaultRegion) {
        return new Formatter<>() {
            @Override
            public String print(String object, Locale locale) {
                return object;
            }

            @Override
            public String parse(String text, Locale locale) {
                if (ObjUtil.isNull(text)) {
                    return text;
                }
                try {
                    return GlobalPhoneNumberUtil.formatE164(GlobalPhoneNumberUtil.parseWithRegion(text, defaultRegion));
                } catch (NumberParseException e) {
                    return text;
                }
            }
        };
    }

}
