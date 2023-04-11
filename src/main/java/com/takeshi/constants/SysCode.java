package com.takeshi.constants;


import com.takeshi.pojo.vo.ResponseDataVO.ResBean;

/**
 * 全局默认 Response Code
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface SysCode {

    /**
     * 请输入{value}中的值
     */
    String VALIDATION_LIST = "{validation.list.message}";

    /**
     * 请求参数有误
     */
    String PARAM_ERROR = "{sysCode.parameterError.message}";

    /**
     * 请输入正确的版本号，例如：1.0.0
     */
    String VERSION_EXAMPLE_STR = "{sysCode.versionExample.message}";

    /**
     * 数值超出范围 (<[{minInteger} ~ {maxInteger}]位>.<[{minFraction} ~ {maxFraction}]位> 预期)
     */
    String NUMBER_DIGITS_STR = "{sysCode.numberDigits.message}";

    /**
     * 成功
     */
    ResBean SUCCESS = new ResBean(200, "sysCode.success.message");

    /**
     * 失败
     */
    ResBean FAIL = new ResBean(10000, "sysCode.fail.message");

    /**
     * 请求参数有误
     */
    ResBean PARAMETER_ERROR = new ResBean(10001, "sysCode.parameterError.message");

    /**
     * 空指针错误
     */
    ResBean SYS_NULL_POINT = new ResBean(10002, "sysCode.nullPointer.message");

    /**
     * 数据库错误
     */
    ResBean DB_ERROR = new ResBean(10003, "sysCode.dbError.message");

    /**
     * 当前访问人数过多，请稍后再试
     */
    ResBean CURRENTLY_TOO_MANY_VISITORS = new ResBean(10004, "sysCode.currentlyTooManyVisitors.message");

    /**
     * 不允许重复提交，请稍候再试
     */
    ResBean REPEAT_SUBMIT = new ResBean(10005, "sysCode.repeatSubmit.message");

    /**
     * 验证码过期/不存在
     */
    ResBean VERIFICATION_CODE_EXPIRE = new ResBean(20000, "sysCode.verificationCodeExpire.message");

    /**
     * 验证码不正确
     */
    ResBean VERIFICATION_CODE_ERROR = new ResBean(20001, "sysCode.verificationError.message");

    /**
     * 这个字段值已存在
     */
    ResBean IS_EXIST = new ResBean(20002, "sysCode.isExist.message");

    /**
     * 这个字段值不存在
     */
    ResBean NOT_EXIST = new ResBean(20003, "sysCode.notExist.message");

    /**
     * 请求的资源不存在
     */
    ResBean RESOURCE_DOES_NOT_EXIST = new ResBean(20004, "sysCode.resourceDoesNotExist.message");

    /**
     * 账号已存在
     */
    ResBean ACCOUNT_IS_EXIST = new ResBean(30000, "sysCode.accountExist.message");

    /**
     * 账号不存在
     */
    ResBean ACCOUNT_DOES_NOT_EXIST = new ResBean(30001, "sysCode.accountDoesNotExist.message");

    /**
     * 账号或密码错误
     */
    ResBean ACCOUNT_INCORRECT = new ResBean(30002, "sysCode.accountIncorrect.message");

    /**
     * 账号被禁用
     */
    ResBean ACCOUNT_DISABLE = new ResBean(30003, "sysCode.accountDisable.message");

    /**
     * 账号被注销
     */
    ResBean ACCOUNT_CANCELLED = new ResBean(30004, "sysCode.accountCancelled.message");

    /**
     * 手机号码格式错误
     */
    ResBean MOBILE_VALIDATION = new ResBean(30005, "validation.mobile.message");

    /**
     * 设置的新密码与旧密码相同
     */
    ResBean SAME_PASSWORD = new ResBean(30006, "sysCode.samePassword.message");

    /**
     * 请求参数认证失败
     */
    ResBean SIGN_ERROR = new ResBean(40001, "sysCode.signError.message");

    /**
     * 非正常客户端请求
     */
    ResBean USERAGENT_ERROR = new ResBean(40002, "sysCode.useragentError.message");

    /**
     * 系统请求频繁，请稍后再试
     */
    ResBean RATE_LIMIT = new ResBean(40003, "sysCode.rateLimit.message");

    /**
     * 最小版本不能大于当前版本
     */
    ResBean VERSION_MIN_ERROR = new ResBean(40004, "sysCode.versionMinError.message");

    /**
     * 当前版本已经是最新版本
     */
    ResBean VERSION_IS_LATEST = new ResBean(40005, "sysCode.versionIsLatest.message");

    /**
     * 未提供token
     */
    ResBean NOT_TOKEN = new ResBean(50000, "sysCode.notToken.message");

    /**
     * token无效
     */
    ResBean INVALID_TOKEN = new ResBean(50001, "sysCode.invalidToken.message");

    /**
     * token已过期
     */
    ResBean TOKEN_TIMEOUT = new ResBean(50002, "sysCode.tokenTimeout.message");

    /**
     * token已被顶下线
     */
    ResBean BE_REPLACED = new ResBean(50003, "sysCode.beReplaced.message");

    /**
     * token已被踢下线
     */
    ResBean KICK_OUT = new ResBean(50004, "sysCode.kickOut.message");

    /**
     * 当前会话未登录
     */
    ResBean NOT_LOGGED = new ResBean(50005, "sysCode.notLogged.message");

    /**
     * 无此角色：[{}]
     */
    ResBean NOT_ROLE_EXCEPTION = new ResBean(50006, "sysCode.notRoleException.message");

    /**
     * 无此权限：[{}]
     */
    ResBean NOT_PERMISSION_EXCEPTION = new ResBean(50007, "sysCode.notPermissionException.message");

    /**
     * 账号被封禁：{} 秒
     */
    ResBean DISABLE_SERVICE_EXCEPTION = new ResBean(50008, "sysCode.disableServiceException.message");

    /**
     * 空文件
     */
    ResBean FILE_IS_NULL = new ResBean(60000, "sysCode.fileIsNull.message");

    /**
     * 文件类型错误
     */
    ResBean FILE_TYPE_ERROR = new ResBean(60001, "sysCode.fileTypeError.message");

}
