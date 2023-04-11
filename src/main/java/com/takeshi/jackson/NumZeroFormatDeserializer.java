package com.takeshi.jackson;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * 数字字符串去掉首位的零，JSON反序列化使用
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/5/18 16:36
 */
public class NumZeroFormatDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (NumberUtil.isNumber(StrUtil.trimToNull(value))) {
            return NumberUtil.parseNumber(value).toString();
        }
        return value;
    }

}
