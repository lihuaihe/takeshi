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
 * @date 2021/6/1 11:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "列表分页条件自定义排序查询参数")
@Accessors(chain = true)
public class BasicSortQuery extends BasicSortPage {

    @NumZeroFormat
    @Parameter(description = "关键字模糊搜索", schema = @Schema(description = "关键字模糊搜索"))
    private String keyword;

    @NumberDigits(minInteger = 13, maxInteger = 13)
    @Parameter(description = "开始时间", schema = @Schema(description = "开始时间"))
    private Long startTime;

    @NumberDigits(minInteger = 13, maxInteger = 13)
    @Parameter(description = "结束时间", schema = @Schema(description = "结束时间"))
    private Long endTime;

}
