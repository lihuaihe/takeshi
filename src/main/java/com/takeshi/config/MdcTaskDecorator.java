package com.takeshi.config;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.takeshi.constants.TakeshiConstants;
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
                MDC.setContextMap(copyOfContextMap);
                String traceId = MDC.get(TakeshiConstants.TRACE_ID_KEY);
                if (StrUtil.isBlank(traceId)) {
                    traceId = IdUtil.fastUUID();
                    MDC.put(TakeshiConstants.TRACE_ID_KEY, traceId);
                }
                runnable.run();
            } finally {
                MDC.remove(TakeshiConstants.TRACE_ID_KEY);
            }
        };
    }

}
