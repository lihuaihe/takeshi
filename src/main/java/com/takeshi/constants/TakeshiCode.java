package com.takeshi.constants;

import com.takeshi.pojo.bo.RetBO;

/**
 * 全局默认 Response Code
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface TakeshiCode {

    /**
     * 请输入{value}中的值
     */
    String VALIDATION_LIST = "{validation.list.message}";

    /**
     * 请求参数有误
     */
    String PARAM_ERROR = "{parameterError.message}";

    /**
     * 请输入正确的版本号，例如：1.0.0
     */
    String VERSION_EXAMPLE_STR = "{versionExample.message}";

    /**
     * 数值超出范围 (&lt;[{minInteger} ~ {maxInteger}]位&gt;.&lt;[{minFraction} ~ {maxFraction}]位&gt; 预期)
     */
    String NUMBER_DIGITS_STR = "{numberDigits.message}";

    /**
     * 不允许重复提交，请稍候再试
     */
    String REPEAT_SUBMIT_STR = "{repeatSubmit.message}";

    /**
     * 短信验证码已过期
     */
    String SMS_VERIFICATION_CODE_EXPIRE_STR = "{smsVerificationCodeExpire.message}";

    /**
     * 短信验证码不正确
     */
    String SMS_VERIFICATION_CODE_ERROR_STR = "{smsVerificationCodeError.message}";

    /**
     * 邮箱验证码已过期
     */
    String EMAIL_VERIFICATION_CODE_EXPIRE_STR = "{emailVerificationCodeExpire.message}";

    /**
     * 邮箱验证码不正确
     */
    String EMAIL_VERIFICATION_CODE_ERROR_STR = "{emailVerificationCodeError.message}";

    /**
     * 成功
     */
    RetBO SUCCESS = new RetBO(200, "success.message");

    /**
     * 失败
     */
    RetBO FAIL = new RetBO(1000, "fail.message");

    /**
     * 请求参数有误
     */
    RetBO PARAMETER_ERROR = new RetBO(1001, "parameterError.message");

    /**
     * 空指针错误
     */
    RetBO SYS_NULL_POINT = new RetBO(1002, "nullPointer.message");

    /**
     * 数据库错误
     */
    RetBO DB_ERROR = new RetBO(1003, "dbError.message");

    /**
     * redis错误
     */
    RetBO REDIS_ERROR = new RetBO(1004, "redisError.message");

    /**
     * 当前访问人数过多，请稍后再试
     */
    RetBO CURRENTLY_TOO_MANY_VISITORS = new RetBO(1005, "currentlyTooManyVisitors.message");

    /**
     * 不允许重复提交，请稍候再试
     */
    RetBO REPEAT_SUBMIT = new RetBO(1006, "repeatSubmit.message");

    /**
     * 传递的值和接收的值不匹配
     */
    RetBO INVALID_VALUE = new RetBO(1007, "invalidValue.message");

    /**
     * 验证码过期/不存在
     */
    RetBO VERIFICATION_CODE_EXPIRE = new RetBO(2000, "verificationCodeExpire.message");

    /**
     * 验证码不正确
     */
    RetBO VERIFICATION_CODE_ERROR = new RetBO(2001, "verificationCodeError.message");

    /**
     * 这个字段值已存在
     */
    RetBO IS_EXIST = new RetBO(2002, "isExist.message");

    /**
     * 这个字段值不存在
     */
    RetBO NOT_EXIST = new RetBO(2003, "notExist.message");

    /**
     * 请求的资源不存在
     */
    RetBO RESOURCE_DOES_NOT_EXIST = new RetBO(2004, "resourceDoesNotExist.message");

    /**
     * 账号已存在
     */
    RetBO ACCOUNT_IS_EXIST = new RetBO(3000, "accountExist.message");

    /**
     * 账号不存在
     */
    RetBO ACCOUNT_DOES_NOT_EXIST = new RetBO(3001, "accountDoesNotExist.message");

    /**
     * 账号或密码错误
     */
    RetBO ACCOUNT_INCORRECT = new RetBO(3002, "accountIncorrect.message");

    /**
     * 账号被禁用
     */
    RetBO ACCOUNT_DISABLE = new RetBO(3003, "accountDisable.message");

    /**
     * 账号被注销
     */
    RetBO ACCOUNT_CANCELLED = new RetBO(3004, "accountCancelled.message");

    /**
     * 手机号码格式错误
     */
    RetBO MOBILE_VALIDATION = new RetBO(3005, "validation.mobile.message");

    /**
     * 设置的新密码与旧密码相同
     */
    RetBO SAME_PASSWORD = new RetBO(3006, "samePassword.message");

    /**
     * 客户端日期时间错误
     */
    RetBO CLIENT_DATE_TIME_ERROR = new RetBO(4000, "clientDateTimeError.message");

    /**
     * 参数签名认证失败
     */
    RetBO SIGN_ERROR = new RetBO(4001, "signError.message");

    /**
     * 非正常客户端请求
     */
    RetBO USERAGENT_ERROR = new RetBO(4002, "useragentError.message");

    /**
     * 系统请求频繁，请稍后再试
     */
    RetBO RATE_LIMIT = new RetBO(4003, "rateLimit.message");

    /**
     * 最小版本不能大于当前版本
     */
    RetBO VERSION_MIN_ERROR = new RetBO(4004, "versionMinError.message");

    /**
     * 当前版本已经是最新版本
     */
    RetBO VERSION_IS_LATEST = new RetBO(4005, "versionIsLatest.message");

    /**
     * 进入了黑名单，系统请求频繁，请稍后再试
     */
    RetBO BLACK_LIST_RATE_LIMIT = new RetBO(4006, "rateLimit.message");

    /**
     * 未提供token
     */
    RetBO NOT_TOKEN = new RetBO(5000, "notToken.message");

    /**
     * token无效
     */
    RetBO INVALID_TOKEN = new RetBO(5001, "invalidToken.message");

    /**
     * token已过期
     */
    RetBO TOKEN_TIMEOUT = new RetBO(5002, "tokenTimeout.message");

    /**
     * token已被顶下线
     */
    RetBO BE_REPLACED = new RetBO(5003, "beReplaced.message");

    /**
     * token已被踢下线
     */
    RetBO KICK_OUT = new RetBO(5004, "kickOut.message");

    /**
     * 当前会话未登录
     */
    RetBO NOT_LOGGED = new RetBO(5005, "notLogged.message");

    /**
     * 无此角色：[{}]
     */
    RetBO NOT_ROLE_EXCEPTION = new RetBO(5006, "notRoleException.message");

    /**
     * 无此权限：[{}]
     */
    RetBO NOT_PERMISSION_EXCEPTION = new RetBO(5007, "notPermissionException.message");

    /**
     * 账号被封禁：{} 秒
     */
    RetBO DISABLE_SERVICE_EXCEPTION = new RetBO(5008, "disableServiceException.message");

    /**
     * 空文件
     */
    RetBO FILE_IS_NULL = new RetBO(6000, "fileIsNull.message");

    /**
     * 文件类型错误
     */
    RetBO FILE_TYPE_ERROR = new RetBO(6001, "fileTypeError.message");

}
