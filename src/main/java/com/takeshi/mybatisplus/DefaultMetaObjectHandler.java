package com.takeshi.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.takeshi.pojo.basic.AbstractBasicEntity;
import com.takeshi.util.TakeshiUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * DefaultMetaObjectHandler
 * <p>
 * mybatis plus 操作时对字段进行统一默认操作
 * <p>
 * 在实体类字段上添加注解@TableField (fill = FieldFill.INSERT)
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Component
public class DefaultMetaObjectHandler implements MetaObjectHandler {

    private final String CREATE_TIME = TakeshiUtil.getPropertyName(AbstractBasicEntity::getCreateTime);
    private final String UPDATE_TIME = TakeshiUtil.getPropertyName(AbstractBasicEntity::getUpdateTime);

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, CREATE_TIME, () -> Instant.now().toEpochMilli(), Long.class);
        this.strictInsertFill(metaObject, UPDATE_TIME, () -> Instant.now().toEpochMilli(), Long.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, UPDATE_TIME, () -> Instant.now().toEpochMilli(), Long.class);
    }

}
