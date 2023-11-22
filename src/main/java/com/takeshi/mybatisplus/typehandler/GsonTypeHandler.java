/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.takeshi.mybatisplus.typehandler;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.takeshi.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * <p>GsonTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = GsonTypeHandler.class)</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class GsonTypeHandler extends AbstractJsonTypeHandler<Object> {

    private final Class<?> type;

    /**
     * 构造函数
     *
     * @param type 类型
     */
    public GsonTypeHandler(Class<?> type) {
        if (log.isTraceEnabled()) {
            log.trace("GsonTypeHandler(" + type + ")");
        }
        Assert.notNull(type, "Type argument cannot be null");
        this.type = type;
    }

    @Override
    protected Object parse(String json) {
        return GsonUtil.fromJson(json, type);
    }

    @Override
    protected String toJson(Object obj) {
        return GsonUtil.toJson(obj);
    }

}
