package com.takeshi.pojo.bo;

import com.google.gson.annotations.SerializedName;
import com.takeshi.pojo.basic.AbstractBasicSerializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SecretInfoBO
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SecretInfoBO extends AbstractBasicSerializable {
    /**
     * awsS3AccessKeyId
     */
    @SerializedName(value = "AWS-S3-Access-key-ID")
    private String awsS3AccessKeyId;
    /**
     * awsS3SecretAccessKey
     */
    @SerializedName(value = "AWS-S3-Secret-access-key")
    private String awsS3SecretAccessKey;
}
