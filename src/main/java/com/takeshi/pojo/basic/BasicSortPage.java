package com.takeshi.pojo.basic;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.takeshi.constraints.VerifySortColumn;
import com.takeshi.jackson.SortColumnDeserializer;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 分页参数
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "列表分页自定义排序查询参数")
@Accessors(chain = true)
@ParameterObject
public class BasicSortPage extends BasicPage {

    /**
     * 排序字段
     */
    @VerifySortColumn
    @JsonDeserialize(using = SortColumnDeserializer.class)
    @Parameter(description = "排序字段", schema = @Schema(description = "排序字段"))
    private String sortColumn;

    /**
     * 是否是升序排序
     */
    @Parameter(description = "是否是升序排序（true/false）", schema = @Schema(description = "是否是升序排序"))
    private Boolean sortAsc;

}
