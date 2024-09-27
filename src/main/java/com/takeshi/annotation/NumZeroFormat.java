package com.takeshi.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.takeshi.constraints.VerifyPhoneNumber;
import com.takeshi.jackson.NumZeroFormatDeserializer;

import java.lang.annotation.*;

/**
 * 去掉前端入参时数字字符串前面多余的零
 * <br/>
 * 之前该注解主要是用于处理手机号码前面的0，现在可改成使用 {@link VerifyPhoneNumber}
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonDeserialize(using = NumZeroFormatDeserializer.class)
@Deprecated
public @interface NumZeroFormat {

}
