package com.takeshi.pojo.basic;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springdoc.core.annotations.ParameterObject;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页参数
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@Schema(description = "列表分页查询参数")
@Accessors(chain = true)
@ParameterObject
public class BasicPage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页数
     */
    @NotNull
    @Positive
    @Parameter(description = "当前页数", example = "1", schema = @Schema(description = "当前页数", example = "1"))
    private Long pageNum;

    /**
     * 每页数据条数
     */
    @NotNull
    @Min(-1)
    @Parameter(description = "每页数据条数，传-1则不分页", example = "10", schema = @Schema(description = "每页数据条数，传-1则不分页", example = "10"))
    private Long pageSize;

}
