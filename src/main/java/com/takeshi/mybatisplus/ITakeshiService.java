package com.takeshi.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.takeshi.pojo.basic.AbstractBasicEntity;
import com.takeshi.pojo.basic.BasicSortPage;
import com.takeshi.pojo.basic.BasicSortQuery;
import com.takeshi.pojo.vo.ResponseDataVO;
import com.takeshi.util.TakeshiUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 扩展的mybatis-plus service 层接口
 * <p>
 * 自定义多表关联分页查询，在mapper层新建一个方法
 * //示例：
 * //@Select("select ${ew.sqlSelect} from tableName t1 left join tableName t2 on t1.t1_id = t2.t1_id ${ew.customSqlSegment}")
 * //Page<T> pageList(Page<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
 * </p>
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2020/12/11 15:17
 */
public interface ITakeshiService<T> extends IService<T> {

    /**
     * 构建一个有排序的分页对象
     *
     * @param basicSortPage
     * @return
     */
    default Page<T> buildSortPage(BasicSortPage basicSortPage) {
        Page<T> page = Page.of(basicSortPage.getPageNum(), basicSortPage.getPageSize());
        if (StrUtil.isNotBlank(basicSortPage.getSortColumn())) {
            page.addOrder(new OrderItem(basicSortPage.getSortColumn(), basicSortPage.isAsc()));
        }
        return page;
    }

    /**
     * 扩展的mybatis-plus分页接口
     *
     * @param basicSortPage 列表分页查询参数
     * @return
     */
    default Page<T> page(BasicSortPage basicSortPage) {
        return this.getBaseMapper().selectPage(this.buildSortPage(basicSortPage), Wrappers.emptyWrapper());
    }

    /**
     * 扩展的mybatis-plus分页接口
     * <p>通用的列表分页查询接口</p>
     *
     * @param baseQuery 列表查询参数
     * @return
     */
    default Page<T> listPage(BasicSortQuery baseQuery) {
        String createTime = TakeshiUtil.getColumnName(AbstractBasicEntity::getCreateTime);
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>()
                .ge(ObjUtil.isNotNull(baseQuery.getStartTime()), createTime, baseQuery.getStartTime())
                .le(ObjUtil.isNotNull(baseQuery.getEndTime()), createTime, baseQuery.getEndTime());
        return this.getBaseMapper().selectPage(this.buildSortPage(baseQuery), queryWrapper);
    }

    /**
     * 扩展的mybatis-plus分页接口
     * <p>通用的列表分页查询接口</p>
     * <p>columns 示例："user_name"</p>
     *
     * @param baseQuery 列表查询参数
     * @param columns   需要进行模糊搜索的数据库字段名
     * @return
     */
    default Page<T> listPage(BasicSortQuery baseQuery, List<SFunction<T, ?>> columns) {
        String createTime = TakeshiUtil.getColumnName(AbstractBasicEntity::getCreateTime);
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>()
                .ge(ObjUtil.isNotNull(baseQuery.getStartTime()), createTime, baseQuery.getStartTime())
                .le(ObjUtil.isNotNull(baseQuery.getEndTime()), createTime, baseQuery.getEndTime());
        if (StrUtil.isNotBlank(baseQuery.getKeyword()) && CollUtil.isNotEmpty(columns)) {
            String sql = "CONCAT_WS(' '," + columns.stream().map(TakeshiUtil::getColumnName).collect(Collectors.joining(StrUtil.COMMA)) + ") like '%" + baseQuery.getKeyword() + "%'";
            queryWrapper.apply(sql);
        }
        return this.getBaseMapper().selectPage(this.buildSortPage(baseQuery), queryWrapper);
    }

    /**
     * 扩展的mybatis-plus分页接口
     * 示例：xxxService.queryWrapperPage([basePage类或集成了BasePage的类], item -> item.eq(User::getUserId,1));
     *
     * @param basicSortPage 列表分页查询参数
     * @param consumer      item -> item.eq("user_id",1)
     * @return
     */
    default Page<T> queryWrapperPage(BasicSortPage basicSortPage, Consumer<QueryWrapper<T>> consumer) {
        return this.getBaseMapper().selectPage(this.buildSortPage(basicSortPage), new QueryWrapper<T>().func(Objects.nonNull(consumer), consumer));
    }

    /**
     * 扩展的mybatis-plus分页接口
     * 示例：xxxService.queryWrapperPage([basePage类或继承了BasePage的类], item -> item.eq(User::getUserId,1));
     *
     * @param basicSortPage 列表分页查询参数
     * @param consumer      item -> item.eq(User::getUserId,1)
     * @return
     */
    default Page<T> lambdaQueryWrapperPage(BasicSortPage basicSortPage, Consumer<LambdaQueryWrapper<T>> consumer) {
        return this.getBaseMapper().selectPage(this.buildSortPage(basicSortPage), new QueryWrapper<T>().lambda().func(Objects.nonNull(consumer), consumer));
    }

    /**
     * 判断当前实体对象中某个字段值是否已存在
     *
     * @param column 查询的字段
     * @param val    查询的值
     * @return boolean
     */
    default boolean columnExists(SFunction<T, ?> column, Object val) {
        return this.getBaseMapper().columnExists(column, val);
    }

    /**
     * 判断当前实体对象中某个字段值是否已存在，已存在时抛出异常
     *
     * @param column  查询的字段
     * @param val     查询的值
     * @param resBean 异常信息对象
     * @param args
     */
    default void columnExists(SFunction<T, ?> column, Object val, ResponseDataVO.ResBean resBean, Object... args) {
        this.getBaseMapper().columnExists(column, val, resBean, args);
    }

    /**
     * 判断当前实体对象中某个字段值是否已存在，不包括本身
     *
     * @param column 查询的字段
     * @param val    查询的值
     * @param id     主键ID值
     * @return boolean
     */
    default boolean columnExists(SFunction<T, ?> column, Object val, Serializable id) {
        return this.getBaseMapper().columnExists(column, val, id);
    }

    /**
     * 判断当前实体对象中某个字段值是否已存在，不包括本身，已存在时抛出异常
     *
     * @param column  查询的字段
     * @param val     查询的值
     * @param id      主键ID值
     * @param resBean 异常信息对象
     * @param args
     */
    default void columnExists(SFunction<T, ?> column, Object val, Serializable id, ResponseDataVO.ResBean resBean, Object... args) {
        this.getBaseMapper().columnExists(column, val, id, resBean, args);
    }

    /**
     * 根据 UpdateWrapper 条件，更新记录 需要设置sqlset
     *
     * @param updateWrapper 实体对象封装操作类 {@link UpdateWrapper}
     * @return boolean
     */
    @Override
    default boolean update(Wrapper<T> updateWrapper) {
        // 由于调用update(T t,Wrapper updateWrapper)时t不能为空,否则自动填充失效
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        return this.update(tableInfo.newInstance(), updateWrapper);
    }

    /**
     * <p>更新某一个字段的值</p>
     * <p>例如：更新禁用/启用状态</p>
     *
     * @param id     主键ID值
     * @param column 需要更新的字段
     * @param val    更新后的值
     * @return
     */
    default boolean updateColumnById(Serializable id, SFunction<T, ?> column, Serializable val) {
        return this.getBaseMapper().updateColumnById(id, column, val);
    }

    /**
     * 根据 TableId 逻辑删除
     *
     * @param id
     * @return
     */
    default boolean logicDeleteById(Serializable id) {
        return this.getBaseMapper().logicDeleteById(id);
    }

    /**
     * 根据主键ID查询（不区分是否已逻辑删除）
     *
     * @param id
     * @return
     */
    default T getIncludeDelById(Serializable id) {
        return this.getBaseMapper().selectIncludeDelById(id);
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    TakeshiMapper<T> getBaseMapper();

}
