package com.takeshi.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义的sql注入器
 * TakeshiSqlInjector
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/09/15 16:03
 */
@Component
public class TakeshiSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
        methodList.add(new SelectIncludeDelById());
        return methodList;
    }
}
