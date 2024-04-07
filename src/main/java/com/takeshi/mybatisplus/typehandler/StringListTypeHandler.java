package com.takeshi.mybatisplus.typehandler;

/**
 * <p>StringListTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = StringListTypeHandler.class)</p>
 * <p>将List转成英文逗号分隔的字符串存入数据库，并且从数据库取出时，将英文逗号分隔的字符串转成List</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class StringListTypeHandler extends ListTypeHandler<String> {

    /**
     * 具体类型，由子类提供
     *
     * @return 具体类型
     */
    @Override
    protected Class<String> specificType() {
        return String.class;
    }

}
