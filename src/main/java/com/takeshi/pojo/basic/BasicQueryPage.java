package com.takeshi.pojo.basic;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.Instant;

/**
 * BasicQueryPage
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "列表分页条件自定义查询参数")
@Accessors(chain = true)
public class BasicQueryPage extends BasicPage {

    /**
     * 开始时间
     */
    @Parameter(description = "开始时间", schema = @Schema(description = "开始时间"))
    private Instant startTime;

    /**
     * 结束时间
     */
    @Parameter(description = "结束时间", schema = @Schema(description = "结束时间"))
    private Instant endTime;

}
