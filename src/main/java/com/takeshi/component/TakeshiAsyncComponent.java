package com.takeshi.component;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import com.takeshi.annotation.TakeshiLog;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.pojo.basic.TbSysLog;
import com.takeshi.util.GsonUtil;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * TakeshiAsyncComponent
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@Async
@Component
@RequiredArgsConstructor
public class TakeshiAsyncComponent {

    private final DataSource dataSource;

    private final Tracer tracer;

    /**
     * 新增一条接口请求相关信息到数据库
     *
     * @param takeshiLog       TakeshiLog注解
     * @param loginId          登录的用户ID
     * @param clientIp         请求的IP
     * @param userAgent        用户代理
     * @param headerMap        请求头部
     * @param paramObjectValue 请求的参数
     * @param httpMethod       请求方式
     * @param methodName       请求方法名称
     * @param requestUrl       请求路径
     * @param startTime        请求时间
     * @param totalTimeMillis  接口总耗时
     * @param responseData     接口响应数据
     */
    public void insertSysLog(TakeshiLog takeshiLog, Object loginId, String clientIp, String userAgent,
                             Map<String, String> headerMap, String paramObjectValue, String httpMethod,
                             String methodName, String requestUrl, Instant startTime, long totalTimeMillis,
                             String responseData) {
        try {
            TbSysLog tbSysLog = new TbSysLog();
            tbSysLog.setLogType(takeshiLog.logType().name());
            tbSysLog.setLoginId(loginId);
            tbSysLog.setClientIp(InetAddress.getByName(clientIp).getAddress());
            tbSysLog.setUserAgent(userAgent);
            tbSysLog.setHttpMethod(httpMethod);
            tbSysLog.setMethodName(methodName);
            tbSysLog.setRequestUrl(requestUrl);
            tbSysLog.setRequestHeader(GsonUtil.toJson(headerMap));
            tbSysLog.setRequestParams(paramObjectValue);
            tbSysLog.setResponseData(StrUtil.emptyToNull(responseData));
            tbSysLog.setTraceId(Optional.ofNullable(tracer.currentSpan()).map(Span::context).map(TraceContext::traceId).orElse(null));
            tbSysLog.setSuccessful(this.successful(responseData));
            tbSysLog.setRequestTime(startTime);
            tbSysLog.setCostTimeMillis(totalTimeMillis);
            Instant instant = Instant.now();
            tbSysLog.setCreateTime(instant);
            tbSysLog.setUpdateTime(instant);
            DbUtil.use(dataSource).insert(Entity.parseWithUnderlineCase(tbSysLog));
        } catch (Exception e) {
            log.error("TakeshiAsyncComponent.insertSysLog --> e: ", e);
        }
    }

    /**
     * 判断返回结果是否是成功的
     *
     * @param responseData 接口响应数据
     * @return boolean
     */
    private boolean successful(String responseData) {
        if (StrUtil.isEmpty(responseData)) {
            return true;
        }
        return GsonUtil.fromJson(responseData, ResponseData.class).getCode() == TakeshiCode.SUCCESS.getCode();
    }

}
