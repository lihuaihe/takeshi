package com.takeshi.util;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

/**
 * 模板引擎
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2023/2/16 14:42
 */
public final class TemplateEngineUtil {

    public static volatile TemplateEngine engine;

    static {
        if (ObjUtil.isNull(engine)) {
            synchronized (TemplateEngineUtil.class) {
                if (ObjUtil.isNull(engine)) {
                    // 设置html模板的位置
                    engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
                }
            }
        }
    }

    private TemplateEngineUtil() {
    }

}
