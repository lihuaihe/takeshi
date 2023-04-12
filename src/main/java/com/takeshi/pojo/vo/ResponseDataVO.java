package com.takeshi.pojo.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.takeshi.config.StaticConfig;
import com.takeshi.constants.SysCode;
import com.takeshi.constants.SysConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.annotation.Transient;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@Schema(name = "接口返回值对象")
public class ResponseDataVO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    @Schema(description = "状态码", example = "200")
    private Integer code;

    /**
     * 消息
     */
    @Schema(description = "消息")
    private String message;

    /**
     * 为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    @Transient
    @JsonIgnore
    @Schema(hidden = true)
    private Object[] args;

    /**
     * 附加对象
     */
    @Schema(description = "附加对象")
    private T data;

    /**
     * 返回时间，毫秒级别
     */
    @Schema(description = "返回时间，毫秒级别", example = "1625101951214")
    private Long time;

    /**
     * 日志追踪ID
     */
    @Schema(description = "日志追踪ID")
    private String traceId;

    /**
     * 返回结果状态信息
     *
     * @param <T> T
     * @return T
     */
    public static <T> ResponseDataVO<T> success() {
        return new ResponseDataVO<>();
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(int code, String message) {
        return new ResponseDataVO<>(code, message);
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(int code, String message, Object[] args) {
        return new ResponseDataVO<>(code, message, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(int code, String message, T data) {
        return new ResponseDataVO<>(code, message, data);
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(int code, String message, T data, Object... args) {
        return new ResponseDataVO<>(code, message, data, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param data 附加对象
     * @param <T>  T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(T data) {
        return new ResponseDataVO<>(data);
    }

    /**
     * 返回结果状态信息
     *
     * @param resBean 消息
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(ResBean resBean) {
        return new ResponseDataVO<>(resBean);
    }

    /**
     * 返回结果状态信息
     *
     * @param resBean 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(ResBean resBean, Object[] args) {
        return new ResponseDataVO<>(resBean, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param resBean 消息
     * @param date    附加对象
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(ResBean resBean, T date) {
        return new ResponseDataVO<>(resBean, date);
    }

    /**
     * 返回结果状态信息
     *
     * @param resBean 消息
     * @param date    附加对象
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> success(ResBean resBean, T date, Object... args) {
        return new ResponseDataVO<>(resBean, date, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag 标志
     * @param <T>  T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag) {
        return flag ? success() : fail();
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag        标志
     * @param failMessage 失败消息
     * @param args        将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>         T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag, String failMessage, Object... args) {
        return flag ? success() : fail(failMessage, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag    标志
     * @param resBean 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag, ResBean resBean, Object... args) {
        return flag ? success() : success(resBean, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag           标志
     * @param resBeanOfTrue  flag为true该返回的结果
     * @param resBeanOfFalse flag为false该返回的结果
     * @param <T>            T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag, ResBean resBeanOfTrue, ResBean resBeanOfFalse) {
        return flag ? success(resBeanOfTrue) : success(resBeanOfFalse);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag           标志
     * @param resBeanOfTrue  flag为true该返回的结果
     * @param resBeanOfFalse flag为false该返回的结果
     * @param args           将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>            T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag, ResBean resBeanOfTrue, ResBean resBeanOfFalse, Object... args) {
        return flag ? success(resBeanOfTrue, args) : success(resBeanOfFalse, args);
    }

    /**
     * 返回失败结果状态
     *
     * @param <T> T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> fail() {
        return new ResponseDataVO<>(SysCode.FAIL);
    }

    /**
     * 返回失败结果状态
     *
     * @param message 消息
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> fail(String message) {
        return new ResponseDataVO<>(SysCode.FAIL.getCode(), message);
    }

    /**
     * 返回失败结果状态
     *
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseDataVO}
     */
    public static <T> ResponseDataVO<T> fail(String message, Object... args) {
        return new ResponseDataVO<>(SysCode.FAIL.getCode(), message, args);
    }

    private ResponseDataVO() {
        this.code = SysCode.SUCCESS.getCode();
        this.message = this.formatMessage(SysCode.SUCCESS.getInfo());
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(int code, String message) {
        this.code = code;
        this.message = this.formatMessage(message);
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(int code, String message, Object[] args) {
        this.code = code;
        this.message = this.formatMessage(message, args);
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(int code, String message, T data) {
        this.code = code;
        this.message = this.formatMessage(message);
        this.data = data;
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(int code, String message, T data, Object... args) {
        this.code = code;
        this.message = this.formatMessage(message, args);
        this.data = data;
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(T data) {
        this.code = SysCode.SUCCESS.getCode();
        this.message = this.formatMessage(SysCode.SUCCESS.getInfo());
        this.data = data;
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(ResBean resCode) {
        this.code = resCode.getCode();
        this.message = this.formatMessage(resCode.getInfo());
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(ResBean resCode, Object[] args) {
        this.code = resCode.getCode();
        this.message = this.formatMessage(resCode.getInfo(), args);
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(ResBean resCode, T data) {
        this.code = resCode.getCode();
        this.message = this.formatMessage(resCode.getInfo());
        this.data = data;
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    private ResponseDataVO(ResBean resCode, T data, Object... args) {
        this.code = resCode.getCode();
        this.message = StrUtil.maxLength(this.formatMessage(resCode.getInfo(), args), 100);
        this.data = data;
        this.time = System.currentTimeMillis();
        this.traceId = MDC.get(SysConstants.TRACE_ID_KEY);
    }

    /**
     * 尝试解决该消息。如果未找到消息，则返回默认消息
     *
     * @param message 要查找的消息代码，例如“calculator.noRateSet”。鼓励 MessageSource 用户将消息名称基于合格的类或包名称，避免潜在的冲突并确保最大程度的清晰度
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return 消息
     */
    private String formatMessage(String message, Object... args) {
        return StaticConfig.messageSource.getMessage(message, args, message, LocaleContextHolder.getLocale());
    }

    /**
     * 结果
     */
    @Data
    @AllArgsConstructor
    public static class ResBean {

        /**
         * 状态码
         */
        private int code;

        /**
         * 结果信息
         */
        private String info;

    }

}

