package com.takeshi.constraints;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.constraintvalidators.VerifyPhoneNumberValidator;
import com.takeshi.jackson.PhoneNumberDeserializer;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 校验全球号码，null值也是有效的
 * <br/>
 * 校验的格式例如：+8618888888888，带了区号的
 * <br/>
 * 如果校验没有带区号则需要传入默认国家代码defaultRegion参数，例如：CN
 * <br/>
 * 使用了PhoneNumberDeserializer.class，在反序列化时会先对号码进行格式化在进行校验
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonDeserialize(using = PhoneNumberDeserializer.class)
@Constraint(validatedBy = VerifyPhoneNumberValidator.class)
public @interface VerifyPhoneNumber {

    /**
     * 提示信息,可以写死,可以填写国际化的key
     *
     * @return msg
     */
    String message() default TakeshiCode.MOBILE_VALIDATION_CODE_ERROR_STR;

    /**
     * 下面这两个属性必须添加
     *
     * @return groups
     */
    Class<?>[] groups() default {};

    /**
     * payload
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * @return 默认国家代码，例如：CN
     */
    String defaultRegion() default "";

}
