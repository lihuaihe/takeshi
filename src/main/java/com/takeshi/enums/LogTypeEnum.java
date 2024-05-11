package com.takeshi.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 日志类型枚举
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Getter
@Schema(description = "日志类型", enumAsRef = true)
public enum LogTypeEnum {

    /**
     * 新增
     */
    INSERT,
    /**
     * 删除
     */
    DELETE,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 查询
     */
    SELECT,
    /**
     * 导入
     */
    IMPORT,
    /**
     * 导出
     */
    EXPORT,
    /**
     * 上传文件
     */
    UPLOAD,
    /**
     * 下载文件
     */
    DOWNLOAD,
    /**
     * 登录
     */
    LOGIN,
    /**
     * 注册
     */
    REGISTER,
    /**
     * 注销
     */
    LOGOUT,
    /**
     * 验证
     */
    VERIFY,
    /**
     * 授权认证
     */
    OAUTH,
    /**
     * 回调
     */
    CALLBACK,
    /**
     * 其它
     */
    OTHER,
    ;

}
