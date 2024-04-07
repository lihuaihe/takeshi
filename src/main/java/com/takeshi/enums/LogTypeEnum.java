package com.takeshi.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
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
    INSERT("insert"),
    /**
     * 删除
     */
    DELETE("delete"),
    /**
     * 更新
     */
    UPDATE("update"),
    /**
     * 查询
     */
    SELECT("select"),
    /**
     * 导入
     */
    IMPORT("import"),
    /**
     * 导出
     */
    EXPORT("export"),
    /**
     * 上传文件
     */
    UPLOAD("upload"),
    /**
     * 下载文件
     */
    DOWNLOAD("download"),
    /**
     * 登录
     */
    LOGIN("login"),
    /**
     * 注册
     */
    REGISTER("register"),
    /**
     * 注销
     */
    LOGOUT("logout"),
    /**
     * 验证
     */
    VERIFY("verify"),
    /**
     * 授权认证
     */
    OAUTH("oauth"),
    /**
     * 回调
     */
    CALLBACK("callback"),
    /**
     * 其它
     */
    OTHER("other"),
    ;

    @EnumValue
    @JsonValue
    private final String value;

    LogTypeEnum(String value) {
        this.value = value;
    }

}
