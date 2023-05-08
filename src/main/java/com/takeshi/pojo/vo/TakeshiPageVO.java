package com.takeshi.pojo.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TakeshiPageVO
 * mapper中使用<br/>
 * TakeshiPageVO&lt;Object&gt; selectAllList(TakeshiPageVO&lt;Object&gt; page);
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "列表分页对象")
public class TakeshiPageVO<T> extends Page<T> {

    /**
     * 额外的数据
     */
    @Schema(description = "额外的数据")
    private Object metaData;

    /**
     * 构造函数
     */
    public TakeshiPageVO() {
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public TakeshiPageVO(long current, long size) {
        super(current, size);
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     * @param total   总数
     */
    public TakeshiPageVO(long current, long size, long total) {
        super(current, size, total);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param searchCount 是否进行 count 查询
     */
    public TakeshiPageVO(long current, long size, boolean searchCount) {
        super(current, size, searchCount);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param total       总数
     * @param searchCount 是否进行 count 查询
     */
    public TakeshiPageVO(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     * @param <T>     T
     * @return TakeshiPageVO
     */
    public static <T> TakeshiPageVO<T> of(long current, long size) {
        return of(current, size, 0);
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     * @param total   总数
     * @param <T>     T
     * @return TakeshiPageVO
     */
    public static <T> TakeshiPageVO<T> of(long current, long size, long total) {
        return of(current, size, total, true);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param searchCount 是否进行 count 查询
     * @param <T>         T
     * @return TakeshiPageVO
     */
    public static <T> TakeshiPageVO<T> of(long current, long size, boolean searchCount) {
        return of(current, size, 0, searchCount);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param total       总数
     * @param searchCount 是否进行 count 查询
     * @param <T>         T
     * @return TakeshiPageVO
     */
    public static <T> TakeshiPageVO<T> of(long current, long size, long total, boolean searchCount) {
        return new TakeshiPageVO<>(current, size, total, searchCount);
    }

}
