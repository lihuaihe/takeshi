package com.takeshi.pojo.bo;

import com.takeshi.pojo.basic.AbstractBasicSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * SmsText
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/09/17 11:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class SmsInfoBO extends AbstractBasicSerializable {

    /**
     * redis key
     */
    private String key;

    /**
     * sms text
     */
    private String value;

}
