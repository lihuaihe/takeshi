package com.takeshi.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义的sql注入器
 * TakeshiSqlInjector
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Component
public class TakeshiSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        methodList.add(new SelectIncludeDelById());
        return methodList;
    }

}
