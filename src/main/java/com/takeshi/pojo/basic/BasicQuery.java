package com.takeshi.pojo.basic;

import com.takeshi.annotation.NumZeroFormat;
import com.takeshi.constraints.NumberDigits;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * BasicSortQuery
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "列表分页条件自定义查询参数")
@Accessors(chain = true)
public class BasicQuery extends BasicPage {

    /**
     * 关键字模糊搜索
     */
    @NumZeroFormat
    @Parameter(description = "关键字模糊搜索", schema = @Schema(description = "关键字模糊搜索"))
    private String keyword;

    /**
     * 开始时间
     */
    @NumberDigits(minInteger = 13, maxInteger = 13)
    @Parameter(description = "开始时间", schema = @Schema(description = "开始时间"))
    private Long startTime;

    /**
     * 结束时间
     */
    @NumberDigits(minInteger = 13, maxInteger = 13)
    @Parameter(description = "结束时间", schema = @Schema(description = "结束时间"))
    private Long endTime;

}
