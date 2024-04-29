package com.takeshi.enums;

import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.Permission;
import lombok.Getter;

/**
 * FileAclEnum
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Getter
public enum FileAclEnum {

    /**
     * 指定所有者被授予{@link Permission#FullControl}其他人没有访问权限。
     * <br/>
     * 这是任何新存储桶或对象的默认访问控制策略。
     */
    Private("private"),

    /**
     * 指定所有者被授予{@link Permission#FullControl}，而{@link GroupGrantee#AllUsers}组被授予者被授予{@link Permission#Read}访问权限。
     * <br/>
     * 如果此策略用于对象，则无需身份验证即可从浏览器读取该对象。
     */
    PublicRead("public-read"),

    /**
     * 指定所有者被授予{@link Permission#FullControl}，而{@link GroupGrantee#AllUsers}组被授予者被授予{@link Permission#Read}和{@link Permission#Write}访问权限。
     * <br/>
     * 不建议一般使用此访问策略
     */
    PublicReadWrite("public-read-write"),

    /**
     * 指定所有者被授予{@link Permission#FullControl}，并且{@link GroupGrantee#AuthenticatedUsers}组被授予者被授予{@link Permission#Read}访问权限。
     */
    AuthenticatedRead("authenticated-read"),

    /**
     * 指定所有者被授予{@link Permission#FullControl}，并且{@link GroupGrantee#LogDelivery}组被授予者被授予{@link Permission#Write}访问权限，以便可以传送访问日志。
     * <br/>
     * 使用此访问策略可为存储桶启用 Amazon S3 存储桶日志记录。目标存储桶需要这些权限才能传送访问日志。
     */
    LogDeliveryWrite("log-delivery-write"),

    /**
     * 指定存储桶的所有者，但不一定与对象的所有者相同，被授予{@link Permission#Read}。
     * <br/>
     * 将对象上传到其他所有者的存储桶时，请使用此访问策略。此访问策略授予存储桶所有者对对象的读取权限，但不向所有用户授予读取权限。
     */
    BucketOwnerRead("bucket-owner-read"),

    /**
     * 指定存储桶的所有者，但不一定与对象的所有者相同，被授予{@link Permission#FullControl}。
     * <br/>
     * 使用此访问策略将对象上传到其他所有者的存储桶。此访问策略授予存储桶所有者对对象的完全访问权限，但不向所有用户授予完全访问权限。
     */
    BucketOwnerFullControl("bucket-owner-full-control"),

    /**
     * 指定所有者被授予{@link Permission#FullControl}。 Amazon EC2 被授予{@link Permission#Read}访问权限以从 Amazon S3 获取 Amazon 系统映像 (AMI) 捆绑包。
     */
    AwsExecRead("aws-exec-read");

    private final String cannedAclHeader;

    FileAclEnum(String cannedAclHeader) {
        this.cannedAclHeader = cannedAclHeader;
    }

}
