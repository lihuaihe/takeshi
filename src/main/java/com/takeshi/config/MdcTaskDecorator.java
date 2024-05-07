package com.takeshi.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.takeshi.constants.RequestConstants;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * MdcTaskDecorator
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class MdcTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                if (CollUtil.isNotEmpty(copyOfContextMap)) {
                    MDC.setContextMap(copyOfContextMap);
                }
                if (StrUtil.isBlank(MDC.get(RequestConstants.TRACE_ID))) {
                    MDC.put(RequestConstants.TRACE_ID, IdUtil.fastSimpleUUID());
                }
                runnable.run();
            } finally {
                MDC.remove(RequestConstants.TRACE_ID);
            }
        };
    }

}
