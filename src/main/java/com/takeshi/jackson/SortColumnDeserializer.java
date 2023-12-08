package com.takeshi.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.takeshi.pojo.basic.TakeshiPage;

import java.io.IOException;

/**
 * SortColumnDeserializer
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JacksonStdImpl
public class SortColumnDeserializer extends StdDeserializer<String> {

    /**
     * 构造函数
     */
    protected SortColumnDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return TakeshiPage.sortColumnToUnderline(p.getText());
    }

}
