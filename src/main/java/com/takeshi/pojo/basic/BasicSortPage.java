package com.takeshi.pojo.basic;

import cn.hutool.core.util.StrUtil;
import com.takeshi.constraints.CheckSortColumn;
import com.takeshi.constraints.CheckString;
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

    @CheckString(value = {"DESC", "ASC"})
    @Parameter(description = "排序规则 DESC/ASC", schema = @Schema(description = "排序规则 DESC/ASC"))
    private String sortRule;

    /**
     * 将传入的参数转成下划线方式
     *
     * @param sortColumn
     */
    public BasicSortPage setSortColumn(String sortColumn) {
        this.sortColumn = StrUtil.isBlank(sortColumn) ? TakeshiUtil.getColumnName(AbstractBasicEntity::getCreateTime) : StrUtil.toUnderlineCase(sortColumn);
        return this;
    }

    /**
     * 设置默认值
     *
     * @param sortRule
     */
    public BasicSortPage setSortRule(String sortRule) {
        this.sortRule = StrUtil.blankToDefault(sortRule, "DESC");
        return this;
    }

    /**
     * 是否是正序排序
     *
     * @return boolean
     */
    @Schema(hidden = true)
    public boolean isAsc() {
        return !StrUtil.equals(this.sortRule, "ASC");
    }

}
