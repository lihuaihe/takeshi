package com.takeshi.jackson;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.i18n.phonenumbers.NumberParseException;
import com.takeshi.constraints.VerifyPhoneNumber;
import com.takeshi.util.GlobalPhoneNumberUtil;

import java.io.IOException;

/**
 * 全球手机号码格式化，JSON反序列化使用
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JacksonStdImpl
public class PhoneNumberDeserializer extends StdDeserializer<String> implements ContextualDeserializer {

    /**
     * 默认国家区域，例如：CN
     */
    private String defaultRegion;

    /**
     * 默认无参构造
     */
    public PhoneNumberDeserializer() {
        super(String.class);
    }

    /**
     * 构造函数
     *
     * @param defaultRegion 默认国家区域，例如：CN
     */
    public PhoneNumberDeserializer(String defaultRegion) {
        super(String.class);
        this.defaultRegion = StrUtil.blankToDefault(defaultRegion, null);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (ObjUtil.isNull(value)) {
            return value;
        }
        try {
            return GlobalPhoneNumberUtil.formatE164(GlobalPhoneNumberUtil.parseWithRegion(value, defaultRegion));
        } catch (NumberParseException e) {
            return value;
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        VerifyPhoneNumber annotation = beanProperty.getAnnotation(VerifyPhoneNumber.class);
        return new PhoneNumberDeserializer(annotation.defaultRegion());
    }

}
