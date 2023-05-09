package com.takeshi.pojo.basic;

import cn.hutool.core.util.StrUtil;
import com.takeshi.constraints.VerifySortColumn;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 分页参数
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "列表分页自定义排序查询参数")
@Accessors(chain = true)
public class BasicSortPage extends BasicPage {

    /**
     * 排序字段
     */
    @VerifySortColumn
    @Parameter(description = "排序字段", schema = @Schema(description = "排序字段"))
    private String sortColumn;

    /**
     * 是否是升序排序
     */
    @Parameter(description = "是否是升序排序", schema = @Schema(description = "是否是升序排序", allowableValues = {"false", "true"}))
    private Boolean sortAsc;

    /**
     * 将传入的参数转成下划线方式
     *
     * @param sortColumn 排序字段
     * @return BasicSortPage
     */
    public BasicSortPage setSortColumn(String sortColumn) {
        this.sortColumn = StrUtil.isBlank(sortColumn) ? "create_time" : StrUtil.toUnderlineCase(sortColumn);
        return this;
    }

}
