package com.takeshi.pojo.basic;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * Entity基类
 *
 * @author 83565
 */
@Data
@Schema(description = "AbstractBasicEntity")
@Accessors(chain = true)
public abstract class AbstractBasicEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建时间，数据库中建议使用TIMESTAMP(3)类型
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Instant createTime;

    /**
     * 更新时间，数据库中建议使用TIMESTAMP(3)类型
     */
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant updateTime;

}
