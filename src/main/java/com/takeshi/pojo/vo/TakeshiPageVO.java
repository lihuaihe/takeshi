package com.takeshi.pojo.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TakeshiPageVO
 * mapper中使用<br/>
 * TakeshiPageVO<Object> selectAllList(TakeshiPageVO<Object> page);
 *
 * @author 七濑武【Nanase Takeshi】
 * @since 2022/12/16 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "列表分页对象")
public class TakeshiPageVO<T> extends Page<T> {

    @Schema(description = "额外的数据")
    private Object metaData;

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

    public TakeshiPageVO(long current, long size, long total) {
        super(current, size, total);
    }

    public TakeshiPageVO(long current, long size, boolean searchCount) {
        super(current, size, searchCount);
    }

    public TakeshiPageVO(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }

    public static <T> TakeshiPageVO<T> of(long current, long size) {
        return of(current, size, 0);
    }

    public static <T> TakeshiPageVO<T> of(long current, long size, long total) {
        return of(current, size, total, true);
    }

    public static <T> TakeshiPageVO<T> of(long current, long size, boolean searchCount) {
        return of(current, size, 0, searchCount);
    }

    public static <T> TakeshiPageVO<T> of(long current, long size, long total, boolean searchCount) {
        return new TakeshiPageVO<>(current, size, total, searchCount);
    }

}
