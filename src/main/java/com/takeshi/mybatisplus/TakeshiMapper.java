package com.takeshi.mybatisplus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.takeshi.exception.TakeshiException;
import com.takeshi.pojo.basic.TakeshiPage;
import com.takeshi.pojo.bo.RetBO;
import com.takeshi.util.TakeshiUtil;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.LogFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 扩展的 mybatis-plus mapper 层接口
 * TakeshiMapper
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface TakeshiMapper<T> extends BaseMapper<T> {

    /**
     * 默认批次提交数量
     */
    int DEFAULT_BATCH_SIZE = 1000;

    /**
     * 根据 entity 条件，查询对象，并转成一个pojo对象，本质上只是替代做了BeanUtil.copyProperties
     *
     * @param queryWrapper 实体对象封装操作类
     * @param clazz        pojo类
     * @param <E>          E
     * @return E
     */
    default <E> E selectOne(Wrapper<T> queryWrapper, Class<E> clazz) {
        return BeanUtil.copyProperties(this.selectOne(queryWrapper), clazz);
    }

    /**
     * 根据 Wrapper 条件，查询全部记录，本质上只是替代做了类型强制转换
     * <p>注意： 只返回第一个字段的值</p>
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     * @param clazz        返回的集合中泛型类型
     * @param <E>          泛型
     * @return 集合
     */
    default <E> List<E> selectObjs(Wrapper<T> queryWrapper, Class<E> clazz) {
        return this.selectObjs(queryWrapper).stream().map(clazz::cast).collect(Collectors.toList());
    }

    /**
     * 根据主键ID查询（不区分是否已逻辑删除）
     *
     * @param id 主键ID值
     * @return T
     */
    T selectIncludeDelById(Serializable id);

    /**
     * 根据 entity 条件，查询全部记录（并翻页），传入page时需要指定resultClass，本质上只是替代做了BeanUtil.copyProperties
     *
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     * @param <V>          V
     * @return TakeshiPage
     */
    default <V> TakeshiPage<V> selectPojoPage(TakeshiPage<V> page, Wrapper<T> queryWrapper) {
        Assert.notNull(page.getResultClass(), "error: can not execute. because can not find resultClass of page!");
        TakeshiPage<T> of = TakeshiPage.of(page.getCurrent(), page.getSize());
        of.setOrders(page.orders());
        return this.selectPage(of, queryWrapper).convert(item -> BeanUtil.copyProperties(item, page.getResultClass()));
    }

    /**
     * 根据 entity 条件，查询列表，并转成pojo对象列表，本质上只是替代做了BeanUtil.copyProperties
     *
     * @param queryWrapper 实体对象封装操作类
     * @param clazz        返回的集合中泛型类型
     * @param <E>          E
     * @return List
     */
    default <E> List<E> selectPojoList(Wrapper<T> queryWrapper, Class<E> clazz) {
        return this.selectList(queryWrapper).stream().map(item -> BeanUtil.copyProperties(item, clazz)).collect(Collectors.toList());
    }

    /**
     * 查询全部记录
     *
     * @return List
     */
    default List<T> selectList() {
        return this.selectList(Wrappers.emptyWrapper());
    }

    /**
     * <p>新增记录时更新排序</p>
     * <p>注意：应该添加事务且在调用该方法时加个锁，并且需要先调用该方法更新已有记录再新增数据</p>
     *
     * @param sortColumn 排序的字段
     * @param val        排序值
     * @param consumer   示例：item -> item.eq(User::getUserId,1)
     * @return int
     */
    default boolean insertWithSort(SFunction<T, ?> sortColumn, Integer val, Consumer<LambdaUpdateWrapper<T>> consumer) {
        String columnName = TakeshiUtil.getColumnName(sortColumn);
        LambdaUpdateWrapper<T> updateWrapper =
                new UpdateWrapper<T>().lambda()
                        .setSql(columnName + " = " + columnName + " + 1")
                        .ge(sortColumn, val)
                        .func(ObjUtil.isNotNull(consumer), consumer);
        return SqlHelper.retBool(this.update(null, updateWrapper));
    }

    /**
     * <p>删除记录时更新排序</p>
     * <p>注意：应该添加事务且在调用该方法时加个锁，先调用删除方法或先调用该方法都可以</p>
     *
     * @param id         主键ID值
     * @param sortColumn 排序的字段
     * @param consumer   示例：item -> item.eq(User::getUserId,1)
     * @return int
     */
    default boolean deleteWithSort(Serializable id, SFunction<T, ?> sortColumn, Consumer<LambdaUpdateWrapper<T>> consumer) {
        Class<T> entityClass = this.getEntityClass();
        String columnName = TakeshiUtil.getColumnName(sortColumn);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        List<Object> ts = this.selectObjs(new QueryWrapper<T>().eq(tableInfo.getKeyColumn(), id).lambda().select(sortColumn));
        if (CollUtil.isEmpty(ts)) {
            return false;
        }
        if (ts.size() != 1) {
            throw ExceptionUtils.mpe("One record is expected, but the query result is multiple records");
        }
        Integer oldVal = (Integer) ts.get(0);
        LambdaUpdateWrapper<T> updateWrapper =
                new UpdateWrapper<T>().lambda()
                        .setSql(columnName + " = " + columnName + " - 1")
                        .gt(sortColumn, oldVal)
                        .func(ObjUtil.isNotNull(consumer), consumer);
        return SqlHelper.retBool(this.update(null, updateWrapper));
    }

    /**
     * <p>更新记录时更新排序</p>
     * <p>注意：应该添加事务且在调用该方法时加个锁，并且需要先调用该方法更新排序字段再更新其他字段值</p>
     *
     * @param id         主键ID值
     * @param sortColumn 排序的字段
     * @param newVal     新的排序值
     * @param consumer   示例：item -> item.eq(User::getUserId,1)
     * @return int
     */
    default boolean updateWithSort(Serializable id, SFunction<T, ?> sortColumn, Integer newVal, Consumer<LambdaUpdateWrapper<T>> consumer) {
        Class<T> entityClass = this.getEntityClass();
        String columnName = TakeshiUtil.getColumnName(sortColumn);
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        List<Object> ts = this.selectObjs(new QueryWrapper<T>().eq(tableInfo.getKeyColumn(), id).lambda().select(sortColumn));
        if (CollUtil.isEmpty(ts)) {
            return false;
        }
        if (ts.size() != 1) {
            throw ExceptionUtils.mpe("One record is expected, but the query result is multiple records");
        }
        Integer oldVal = (Integer) ts.get(0);
        if (oldVal.compareTo(newVal) == 0) {
            return false;
        }
        LambdaUpdateWrapper<T> updateWrapper =
                Wrappers.lambdaUpdate(entityClass)
                        .setSql(columnName +
                                " = CASE " +
                                " WHEN " + tableInfo.getKeyColumn() + " = " + id + " THEN " + newVal + "" +
                                " WHEN " + columnName + " < " + oldVal + " THEN " + columnName + " + 1" +
                                " WHEN " + columnName + " > " + oldVal + " THEN " + columnName + " - 1 " +
                                " ELSE " + columnName +
                                " END")
                        .func(ObjUtil.isNotNull(consumer), consumer);
        updateWrapper.last(StringUtils.isBlank(updateWrapper.getCustomSqlSegment()) ? " WHERE " : " and " + columnName + " >= LEAST(" + oldVal + "," + newVal + ") and " + columnName + " <= GREATEST(" + oldVal + "," + newVal + ")");
        return SqlHelper.retBool(this.update(null, updateWrapper));
    }

    /**
     * 判断当前实体对象中某个字段值是否已存在
     *
     * @param column 查询的字段
     * @param val    查询的值
     * @return boolean
     */
    default boolean columnExists(SFunction<T, ?> column, Object val) {
        return this.exists(Wrappers.lambdaQuery(this.getEntityClass()).eq(column, val));
    }

    /**
     * 判断当前实体对象中某个字段值是否已存在，已存在时抛出异常
     *
     * @param column 查询的字段
     * @param val    查询的值
     * @param retBO  异常信息对象
     * @param args   将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    default void columnExists(SFunction<T, ?> column, Object val, RetBO retBO, Object... args) {
        if (this.exists(Wrappers.lambdaQuery(this.getEntityClass()).eq(column, val))) {
            throw new TakeshiException(retBO, args);
        }
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
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        return this.exists(new QueryWrapper<T>().ne(tableInfo.getKeyColumn(), id).lambda().eq(column, val));
    }

    /**
     * 判断当前实体对象中某个字段值是否已存在，不包括本身，已存在时抛出异常
     *
     * @param column 查询的字段
     * @param val    查询的值
     * @param id     主键ID值
     * @param retBO  结果对象
     * @param args   将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    default void columnExists(SFunction<T, ?> column, Object val, Serializable id, RetBO retBO, Object... args) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        if (this.exists(new QueryWrapper<T>().ne(tableInfo.getKeyColumn(), id).lambda().eq(column, val))) {
            throw new TakeshiException(retBO, args);
        }
    }

    /**
     * 新增数据（先按queryWrapper查询记录是否存在，如果不存在则新增）
     *
     * @param queryWrapper 实体对象封装操作类
     * @param entity       实体对象
     * @return boolean
     */
    default boolean insertIgnore(Wrapper<T> queryWrapper, T entity) {
        if (null == entity) {
            return false;
        }
        if (this.exists(queryWrapper)) {
            // 已存在，直接返回false
            return false;
        }
        return SqlHelper.retBool(this.insert(entity));
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     * @return boolean
     */
    default boolean insertOrUpdate(T entity) {
        if (null == entity) {
            return false;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
        return StringUtils.checkValNull(idVal) || ObjUtil.isNull(this.selectById((Serializable) idVal)) ? SqlHelper.retBool(this.insert(entity)) : SqlHelper.retBool(this.updateById(entity));
    }

    /**
     * 根据updateWrapper尝试更新，否继续执行saveOrUpdate(T)方法
     * 此次修改主要是减少了此项业务代码的代码量（存在性验证之后的insertOrUpdate操作）
     *
     * @param entity        实体对象
     * @param updateWrapper 实体对象封装操作类
     * @return boolean
     */
    default boolean insertOrUpdate(T entity, Wrapper<T> updateWrapper) {
        return SqlHelper.retBool(this.update(entity, updateWrapper)) || this.insertOrUpdate(entity);
    }

    /**
     * 插入数据（批量）
     *
     * @param list 实体对象集合
     * @return boolean
     */
    default boolean insertBatch(Collection<T> list) {
        return this.insertBatch(list, DEFAULT_BATCH_SIZE);
    }

    /**
     * 插入数据（批量）
     *
     * @param list      实体对象集合
     * @param batchSize 插入批次数量
     * @return boolean
     */
    default boolean insertBatch(Collection<T> list, int batchSize) {
        Class<T> entityClass = this.getEntityClass();
        String sqlStatement = this.getSqlStatement(SqlMethod.INSERT_ONE);
        return SqlHelper.executeBatch(entityClass, LogFactory.getLog(entityClass), list, batchSize, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entityList 实体对象集合
     * @return boolean
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList) {
        return this.insertOrUpdateBatch(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entityList 实体对象集合
     * @param batchSize  批次提交数量
     * @return boolean
     */
    default boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        Class<T> entityClass = this.getEntityClass();
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        return SqlHelper.saveOrUpdateBatch(entityClass, this.getMapperClass(), LogFactory.getLog(entityClass), entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                    || CollUtil.isEmpty(sqlSession.selectList(this.getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(this.getSqlStatement(SqlMethod.UPDATE_BY_ID), param);
        });
    }

    /**
     * 根据 UpdateWrapper 条件，更新记录 需要设置sqlset
     *
     * @param updateWrapper 实体对象封装操作类
     * @return boolean
     */
    default boolean update(Wrapper<T> updateWrapper) {
        // 由于调用update(T t,Wrapper updateWrapper)时t不能为空,否则自动填充失效
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        return SqlHelper.retBool(this.update(tableInfo.newInstance(), updateWrapper));
    }

    /**
     * <p>更新某一个字段的值</p>
     * <p>例如：更新禁用/启用状态</p>
     *
     * @param id     主键ID值
     * @param column 需要更新的字段
     * @param val    更新后的值
     * @return boolean
     */
    default boolean updateColumnById(Serializable id, SFunction<T, ?> column, Serializable val) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getEntityClass());
        T instance = tableInfo.newInstance();
        tableInfo.setPropertyValue(instance, tableInfo.getKeyProperty(), id);
        tableInfo.setPropertyValue(instance, TakeshiUtil.getPropertyName(column), val);
        return SqlHelper.retBool(this.updateById(instance));
    }

    /**
     * 根据 TableId 批量更新记录
     *
     * @param entityList 实体对象集合
     * @return boolean
     */
    default boolean updateBatchById(Collection<T> entityList) {
        return this.updateBatchById(entityList, DEFAULT_BATCH_SIZE);
    }

    /**
     * 根据 TableId 批量更新记录
     *
     * @param entityList 实体对象集合
     * @param batchSize  批次提交数量
     * @return boolean
     */
    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        Class<T> entityClass = this.getEntityClass();
        String sqlStatement = this.getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return SqlHelper.executeBatch(entityClass, LogFactory.getLog(entityClass), entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    /**
     * 获取 entity 的 class
     *
     * @return Class
     */
    @SuppressWarnings("unchecked")
    default Class<T> getEntityClass() {
        return (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), TakeshiMapper.class, 0);
    }

    /**
     * 获取 mapper 的 class
     *
     * @return Class
     */
    default Class<?> getMapperClass() {
        Class<T> entityClass = this.getEntityClass();
        TableInfo tableInfo = Optional.ofNullable(TableInfoHelper.getTableInfo(entityClass)).orElseThrow(() -> ExceptionUtils.mpe("Can not find TableInfo from Class: \"%s\".", entityClass.getName()));
        return ClassUtils.toClassConfident(tableInfo.getCurrentNamespace());
    }

    /**
     * 获取mapperStatementId
     *
     * @param sqlMethod MybatisPlus 支持的 SQL 方法
     * @return sql
     */
    default String getSqlStatement(SqlMethod sqlMethod) {
        return SqlHelper.getSqlStatement(this.getMapperClass(), sqlMethod);
    }

}
