package com.takeshi.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.takeshi.jackson.NumZeroFormatDeserializer;

import java.lang.annotation.*;

/**
 * 去掉字符串前面多余的零
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/5/18 14:52
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonDeserialize(using = NumZeroFormatDeserializer.class)
public @interface NumZeroFormat {

}
