package com.takeshi.config;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.takeshi.constants.SysConstants;
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
                String traceId = MDC.get(SysConstants.TRACE_ID_KEY);
                if (StrUtil.isBlank(traceId)) {
                    traceId = IdUtil.fastUUID();
                    MDC.put(SysConstants.TRACE_ID_KEY, traceId);
                }
                runnable.run();
            } finally {
                MDC.remove(SysConstants.TRACE_ID_KEY);
            }
        };
    }

}
