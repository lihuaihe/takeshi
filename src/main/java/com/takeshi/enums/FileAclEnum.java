package com.takeshi.enums;

import lombok.Getter;

/**
 * FileAclEnum
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Getter
public enum FileAclEnum {

    /**
     * PRIVATE: 只有对象的所有者（即上传该对象的AWS账户）拥有对该对象的完全控制权限。
     * 这是S3对象的默认权限。
     */
    PRIVATE("private"),

    /**
     * PUBLIC_READ: 任何人都可以读取该对象的数据（即公开读权限），
     * 但只有对象的所有者拥有对该对象的写权限和其他特权。
     */
    PUBLIC_READ("public-read"),

    /**
     * PUBLIC_READ_WRITE: 任何人都可以读取和写入该对象的数据（即公开读写权限）。
     * 这种权限较为危险，通常不推荐使用，因为它允许任何人覆盖或删除对象。
     */
    PUBLIC_READ_WRITE("public-read-write"),

    /**
     * AUTHENTICATED_READ: 任何经过身份验证的AWS用户都可以读取该对象的数据。
     * 这意味着只要拥有AWS账户的人都可以访问该对象。
     */
    AUTHENTICATED_READ("authenticated-read"),

    /**
     * AWS_EXEC_READ: AWS可以为特定场景执行读取操作，例如将对象下载到EC2实例。
     * 这通常用于AWS服务之间的内部操作。
     */
    AWS_EXEC_READ("aws-exec-read"),

    /**
     * BUCKET_OWNER_READ: 桶的所有者拥有对该对象的读取权限，即使对象是由另一个AWS账户上传的。
     * 这确保桶的所有者能够访问桶中所有对象。
     */
    BUCKET_OWNER_READ("bucket-owner-read"),

    /**
     * BUCKET_OWNER_FULL_CONTROL: 桶的所有者拥有对该对象的完全控制权限，
     * 包括读、写和删除权限，即使对象是由另一个AWS账户上传的。
     * 这提供了桶所有者对桶中所有对象的完全管理权限。
     */
    BUCKET_OWNER_FULL_CONTROL("bucket-owner-full-control"),

    /**
     * UNKNOWN_TO_SDK_VERSION: 未知的S3对象权限类型。
     * 当SDK遇到未知或不支持的权限类型时使用这个值。
     */
    UNKNOWN_TO_SDK_VERSION(null);

    private final String value;

    FileAclEnum(String value) {
        this.value = value;
    }

}
