package com.takeshi.pojo.basic;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * TakeshiPage
 * mapper中使用<br/>
 * TakeshiPage&lt;Object&gt; selectAllList(TakeshiPage&lt;Object&gt; page);
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(description = "列表分页对象")
public class TakeshiPage<T> extends Page<T> {

    /**
     * 额外的数据
     */
    @Schema(description = "额外的数据")
    private Object metaData;

    @Schema(description = "泛型类型", hidden = true)
    private transient Class<T> resultClass;

    /**
     * 构造函数
     */
    public TakeshiPage() {
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     */
    public TakeshiPage(long current, long size) {
        super(current, size);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param resultClass 泛型类型
     */
    public TakeshiPage(long current, long size, Class<T> resultClass) {
        super(current, size);
        this.resultClass = resultClass;
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     * @param total   总数
     */
    public TakeshiPage(long current, long size, long total) {
        super(current, size, total);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param total       总数
     * @param resultClass 泛型类型
     */
    public TakeshiPage(long current, long size, long total, Class<T> resultClass) {
        super(current, size, total);
        this.resultClass = resultClass;
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param searchCount 是否进行 count 查询
     */
    public TakeshiPage(long current, long size, boolean searchCount) {
        super(current, size, searchCount);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param searchCount 是否进行 count 查询
     * @param resultClass 泛型类型
     */
    public TakeshiPage(long current, long size, boolean searchCount, Class<T> resultClass) {
        super(current, size, searchCount);
        this.resultClass = resultClass;
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param total       总数
     * @param searchCount 是否进行 count 查询
     */
    public TakeshiPage(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param total       总数
     * @param searchCount 是否进行 count 查询
     * @param resultClass 泛型类型
     */
    public TakeshiPage(long current, long size, long total, boolean searchCount, Class<T> resultClass) {
        super(current, size, total, searchCount);
        this.resultClass = resultClass;
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     * @param <T>     T
     * @return TakeshiPage
     */
    public static <T> TakeshiPage<T> of(long current, long size) {
        return of(current, size, 0);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param resultClass 泛型类型
     * @param <T>         T
     * @return TakeshiPage
     */
    public static <T> TakeshiPage<T> of(long current, long size, Class<T> resultClass) {
        return of(current, size, 0, resultClass);
    }

    /**
     * 分页构造函数
     *
     * @param current 当前页
     * @param size    每页显示条数
     * @param total   总数
     * @param <T>     T
     * @return TakeshiPage
     */
    public static <T> TakeshiPage<T> of(long current, long size, long total) {
        return of(current, size, total, true);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param total       总数
     * @param resultClass 泛型类型
     * @param <T>         T
     * @return TakeshiPage
     */
    public static <T> TakeshiPage<T> of(long current, long size, long total, Class<T> resultClass) {
        return of(current, size, total, true, resultClass);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param searchCount 是否进行 count 查询
     * @param <T>         T
     * @return TakeshiPage
     */
    public static <T> TakeshiPage<T> of(long current, long size, boolean searchCount) {
        return of(current, size, 0, searchCount);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param searchCount 是否进行 count 查询
     * @param resultClass 泛型类型
     * @param <T>         T
     * @return TakeshiPage
     */
    public static <T> TakeshiPage<T> of(long current, long size, boolean searchCount, Class<T> resultClass) {
        return of(current, size, 0, searchCount, resultClass);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param total       总数
     * @param searchCount 是否进行 count 查询
     * @param <T>         T
     * @return TakeshiPage
     */
    public static <T> TakeshiPage<T> of(long current, long size, long total, boolean searchCount) {
        return new TakeshiPage<>(current, size, total, searchCount);
    }

    /**
     * 分页构造函数
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param total       总数
     * @param searchCount 是否进行 count 查询
     * @param resultClass 泛型类型
     * @param <T>         T
     * @return TakeshiPage
     */
    public static <T> TakeshiPage<T> of(long current, long size, long total, boolean searchCount, Class<T> resultClass) {
        return new TakeshiPage<>(current, size, total, searchCount, resultClass);
    }

    /**
     * 设置数据列表
     *
     * @param records 数据列表
     * @return TakeshiPage
     */
    @Override
    public TakeshiPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    /**
     * IPage 的泛型转换
     *
     * @param mapper 转换函数
     * @return 转换泛型后的 IPage
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> TakeshiPage<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getRecords().stream().map(mapper).collect(toList());
        return ((TakeshiPage<R>) this).setRecords(collect);
    }

}
