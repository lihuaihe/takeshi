package com.takeshi.pojo.basic;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页参数
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@Schema(name = "列表分页查询参数")
@Accessors(chain = true)
public class BasicPage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Min(1)
    @Parameter(description = "当前页数", example = "1", schema = @Schema(description = "当前页数", example = "1"))
    private Long pageNum;

    @NotNull
    @Min(1)
    @Max(50000)
    @Parameter(description = "每页数据条数", example = "10", schema = @Schema(description = "每页数据条数", example = "10"))
    private Long pageSize;

}
