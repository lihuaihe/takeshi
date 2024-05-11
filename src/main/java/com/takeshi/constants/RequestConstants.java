package com.takeshi.constants;

/**
 * RequestConstants
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface RequestConstants {

    /**
     * 日志追踪ID
     */
    String TRACE_ID = "traceId";

    /**
     * 登录用户ID
     */
    String LOGIN_ID = "loginId";

    /**
     * 调用接口方法名
     */
    String METHOD_NAME = "methodName";

    /**
     * TakeshiLog注解
     */
    String TAKESHI_LOG = "takeshiLog";

    /**
     * 接口请求的参数
     */
    String PARAM_OBJECT_VALUE = "paramObjectValue";

    /**
     * 客户端IP
     */
    String CLIENT_IP = "clientIp";

    /**
     * header
     */
    interface Header {

        /**
         * 调用接口header里面传的时区字段(Asia/Shanghai)
         */
        String TIMEZONE = "x-timezone";

        /**
         * 调用接口header里面传的经纬度字段
         */
        String GEO_POINT = "x-geo-point";

        /**
         * 调用接口header里面传的时间戳字段（毫秒级）
         */
        String TIMESTAMP = "x-timestamp";

        /**
         * 仅一次有效的随机字符串，可以使用用户信息+时间戳+随机数等信息做个哈希值，作为nonce值
         */
        String NONCE = "x-nonce";

        /**
         * 签名参数名
         */
        String SIGN = "x-sign";

    }

}
