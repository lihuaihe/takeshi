package com.takeshi.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * 日志类型枚举
 *
 * @author 七濑武【Nanase Takeshi】
 */
public enum LogTypeEnum implements IEnum<String> {

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
     * 授权
     */
    GRANT,
    /**
     * 回调
     */
    CALLBACK,
    /**
     * 其它
     */
    OTHER,
    ;

    /**
     * 枚举数据库存储值
     */
    @Override
    public String getValue() {
        return this.name();
    }

}
