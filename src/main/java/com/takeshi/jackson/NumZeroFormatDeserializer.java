package com.takeshi.jackson;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.takeshi.constraints.VerifyPhoneNumber;

import java.io.IOException;

/**
 * 数字字符串去掉首位的零，JSON反序列化使用
 * <br/>
 * 之前该注解主要是用于处理手机号码前面的0，现在可改成使用 {@link VerifyPhoneNumber}
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Deprecated
@JacksonStdImpl
public class NumZeroFormatDeserializer extends StdDeserializer<String> {

    /**
     * 默认构造函数
     */
    protected NumZeroFormatDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        return NumberUtil.isNumber(StrUtil.trim(value)) ? NumberUtil.parseNumber(value).toString() : value;
    }

}
