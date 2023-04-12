package com.takeshi.pojo.basic;

import cn.hutool.core.util.StrUtil;
import com.takeshi.constraints.CheckSortColumn;
import com.takeshi.util.TakeshiUtil;
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
@Schema(name = "列表分页自定义排序查询参数")
@Accessors(chain = true)
public class BasicSortPage extends BasicPage {

    @CheckSortColumn
    @Parameter(description = "排序字段", schema = @Schema(description = "排序字段"))
    private String sortColumn;

    @Parameter(description = "是否是生序排序", schema = @Schema(description = "是否是生序排序"))
    private Boolean sortAsc;

    /**
     * 将传入的参数转成下划线方式
     *
     * @param sortColumn
     */
    public BasicSortPage setSortColumn(String sortColumn) {
        this.sortColumn = StrUtil.isBlank(sortColumn) ? TakeshiUtil.getColumnName(AbstractBasicEntity::getCreateTime) : StrUtil.toUnderlineCase(sortColumn);
        return this;
    }

}
