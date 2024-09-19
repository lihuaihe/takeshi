package com.takeshi.pojo.basic;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
     * 元数据
     */
    @Schema(description = "元数据", nullable = true)
    private Object metadata;

    @JsonIgnore
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
     * 分页构造函数
     *
     * @param basicPage basicPage
     * @param <E>       e
     * @param <T>       t
     * @return TakeshiPage
     */
    public static <E extends BasicPage, T> TakeshiPage<T> of(E basicPage) {
        return of(basicPage, null);
    }

    /**
     * 分页构造函数
     *
     * @param basicPage   basicPage
     * @param resultClass 泛型类型
     * @param <E>         e
     * @param <T>         t
     * @return TakeshiPage
     */
    public static <E extends BasicPage, T> TakeshiPage<T> of(E basicPage, Class<T> resultClass) {
        TakeshiPage<T> page = TakeshiPage.of(basicPage.getPageNum(), basicPage.getPageSize(), resultClass);
        if (basicPage instanceof BasicSortPage basicSortPage) {
            page.addOrder(new OrderItem().setColumn(TakeshiPage.sortColumnToUnderline(basicSortPage.getSortColumn())).setAsc(BooleanUtil.isTrue(basicSortPage.getSortAsc())));
        }
        return page;
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
     * 设置数据总数
     *
     * @param total 数据总数
     * @return TakeshiPage
     */
    @Override
    public TakeshiPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    /**
     * 设置每页显示条数
     *
     * @param size 每页显示条数
     * @return TakeshiPage
     */
    @Override
    public TakeshiPage<T> setSize(long size) {
        this.size = size;
        return this;
    }

    /**
     * 设置当前页
     *
     * @param current 当前页
     * @return TakeshiPage
     */
    @Override
    public TakeshiPage<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    /**
     * 设置是否进行 count 查询
     *
     * @param searchCount 是否进行 count 查询
     * @return TakeshiPage
     */
    @Override
    public TakeshiPage<T> setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
        return this;
    }

    /**
     * 设置是否自动优化 COUNT SQL
     *
     * @param optimizeCountSql 是否自动优化 COUNT SQL
     * @return TakeshiPage
     */
    @Override
    public TakeshiPage<T> setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
        return this;
    }

    /**
     * TakeshiPage 的泛型转换
     *
     * @param mapper 转换函数
     * @return 转换泛型后的TakeshiPage
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R> TakeshiPage<R> convert(Function<? super T, ? extends R> mapper) {
        List<R> collect = this.getRecords().stream().map(mapper).collect(toList());
        return ((TakeshiPage<R>) this).setRecords(collect);
    }

    /**
     * TakeshiPage 的泛型转换
     *
     * @param clazz 转换的类
     * @param <R>   Bean类型
     * @return 转换泛型后的TakeshiPage
     */
    @SuppressWarnings("unchecked")
    public <R> TakeshiPage<R> convert(Class<R> clazz) {
        return ((TakeshiPage<R>) this).setRecords(BeanUtil.copyToList(this.getRecords(), clazz));
    }

    /**
     * TakeshiPage 的泛型转换
     *
     * @param clazz       转换的类
     * @param copyOptions 拷贝选项
     * @param <R>         Bean类型
     * @return 转换泛型后的TakeshiPage
     */
    @SuppressWarnings("unchecked")
    public <R> TakeshiPage<R> convert(Class<R> clazz, CopyOptions copyOptions) {
        return ((TakeshiPage<R>) this).setRecords(BeanUtil.copyToList(this.getRecords(), clazz, copyOptions));
    }

    /**
     * 排序字段转下划线
     *
     * @param sortColumn 排序字段
     * @return String
     */
    public static String sortColumnToUnderline(String sortColumn) {
        return StrUtil.isBlank(sortColumn) ? "create_time" : StrUtil.toUnderlineCase(sortColumn);
    }

}
