package com.takeshi.annotation;

import java.lang.annotation.*;

/**
 * <p>放弃某些类型的校验</p>
 * and
 * <p>入参和出参数据加密解密，前端传递加密的参数时，即使后台使用的是对象接收，
 * 前端传递参数时也是传递rsa加密后的对象字符串过来</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemSecurity {

    /**
     * 放弃校验token
     *
     * @return boolean
     */
    boolean token() default false;

    /**
     * 放弃校验APP端调用接口的平台，需要配置yml文件中的 takeshi.app-platform 参数才会生效
     *
     * @return boolean
     */
    boolean platform() default false;

    /**
     * 放弃校验参数签名，需要配置yml文件中的 sa-token.sign.secret-key 值才会生效
     *
     * @return boolean
     */
    boolean signature() default false;

    /**
     * 放弃校验客户端时间戳，如果需要校验参数签名则该配置无效，只要校验了参数签名就一定需要校验客户端的时间戳
     *
     * @return boolean
     */
    boolean timestamp() default true;

    /**
     * 放弃上述token,platform,signature的校验，此属性优先级别最高
     *
     * @return boolean
     */
    boolean all() default false;

    /**
     * 入参是否解密，默认不解密
     *
     * @return boolean
     */
    boolean inDecode() default false;

    /**
     * 出参是否加密，默认不加密
     *
     * @return boolean
     */
    boolean outEncode() default false;

}
