package com.takeshi.pojo.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.pojo.bo.RetBO;
import com.takeshi.util.TakeshiUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.MDC;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/**
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@Accessors(chain = true)
@Schema(description = "接口返回值对象")
public class ResponseData<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    @Schema(description = "状态码", example = "200")
    private int code;

    /**
     * 消息
     */
    @Schema(description = "消息")
    private String message;

    /**
     * 为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    @JsonIgnore
    @Schema(hidden = true)
    private Object[] args;

    /**
     * 数据
     */
    @Schema(description = "数据", nullable = true)
    private T data;

    /**
     * 附加数据
     */
    @Schema(description = "附加数据", nullable = true)
    private Object metaData;

    /**
     * 返回时间
     */
    @Schema(description = "返回时间")
    private Instant time;

    /**
     * 日志追踪ID
     */
    @Schema(description = "日志追踪ID")
    private String traceId;

    /**
     * 返回成功结果状态信息
     *
     * @param <T> T
     * @return T
     */
    public static <T> ResponseData<T> success() {
        return new ResponseData<>();
    }

    /**
     * 返回失败结果状态
     *
     * @param <T> T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> fail() {
        return new ResponseData<>(TakeshiCode.FAIL);
    }

    /**
     * 返回失败结果状态
     *
     * @param message 消息
     * @param <T>     T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> fail(String message) {
        return new ResponseData<>(TakeshiCode.FAIL.getCode(), message);
    }

    /**
     * 返回失败结果状态
     *
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> fail(String message, Object... args) {
        return new ResponseData<>(TakeshiCode.FAIL.getCode(), message, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(int code, String message) {
        return new ResponseData<>(code, message);
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(int code, String message, Object[] args) {
        return new ResponseData<>(code, message, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param <T>     T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(int code, String message, T data) {
        return new ResponseData<>(code, message, data);
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(int code, String message, T data, Object... args) {
        return new ResponseData<>(code, message, data, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param data 附加对象
     * @param <T>  T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(T data) {
        return new ResponseData<>(data);
    }

    /**
     * 返回结果状态信息
     *
     * @param RetBO 消息
     * @param <T>   T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(RetBO RetBO) {
        return new ResponseData<>(RetBO);
    }

    /**
     * 返回结果状态信息
     *
     * @param RetBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(RetBO RetBO, Object[] args) {
        return new ResponseData<>(RetBO, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param RetBO 消息
     * @param data  附加对象
     * @param <T>   T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(RetBO RetBO, T data) {
        return new ResponseData<>(RetBO, data);
    }

    /**
     * 返回结果状态信息
     *
     * @param RetBO 消息
     * @param data  附加对象
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retData(RetBO RetBO, T data, Object... args) {
        return new ResponseData<>(RetBO, data, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag 标志
     * @param <T>  T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retBool(boolean flag) {
        return flag ? success() : fail();
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag        标志
     * @param failMessage 失败消息
     * @param args        将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>         T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retBool(boolean flag, String failMessage, Object... args) {
        return flag ? success() : fail(failMessage, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag  标志
     * @param RetBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retBool(boolean flag, RetBO RetBO, Object... args) {
        return flag ? success() : retData(RetBO, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag         标志
     * @param retBOOfTrue  flag为true该返回的结果
     * @param retBOOfFalse flag为false该返回的结果
     * @param <T>          T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retBool(boolean flag, RetBO retBOOfTrue, RetBO retBOOfFalse) {
        return flag ? retData(retBOOfTrue) : retData(retBOOfFalse);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag         标志
     * @param retBOOfTrue  flag为true该返回的结果
     * @param retBOOfFalse flag为false该返回的结果
     * @param args         将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>          T
     * @return {@link ResponseData}
     */
    public static <T> ResponseData<T> retBool(boolean flag, RetBO retBOOfTrue, RetBO retBOOfFalse, Object... args) {
        return flag ? retData(retBOOfTrue, args) : retData(retBOOfFalse, args);
    }

    private ResponseData() {
        this.init(TakeshiCode.SUCCESS, null);
    }

    private ResponseData(int code, String message) {
        this.init(code, message, null);
    }

    private ResponseData(int code, String message, Object[] args) {
        this.init(code, message, null, args);
    }

    private ResponseData(int code, String message, T data) {
        this.init(code, message, data);
    }

    private ResponseData(T data) {
        this.init(TakeshiCode.SUCCESS, data);
    }

    private ResponseData(RetBO retBO) {
        this.init(retBO, null);
    }

    private ResponseData(RetBO retBO, Object[] args) {
        this.init(retBO, null, args);
    }

    private ResponseData(RetBO retBO, T data) {
        this.init(retBO, data);
    }

    private ResponseData(RetBO retBO, T data, Object... args) {
        this.init(retBO, data, args);
    }

    private ResponseData(int code, String message, T data, Object... args) {
        this.init(code, message, data, args);
    }

    /**
     * 初始化一些值
     *
     * @param retBO retBO
     * @param data  附加对象
     * @param args  参数
     */
    void init(RetBO retBO, T data, Object... args) {
        this.init(retBO.getCode(), retBO.getMessage(), data, args);
    }

    /**
     * 初始化一些值
     *
     * @param code    状态码
     * @param message 提示信息
     * @param data    附加对象
     * @param args    参数
     */
    void init(int code, String message, T data, Object... args) {
        this.code = code;
        this.message = TakeshiUtil.formatMessage(message, args);
        this.data = data;
        this.time = Instant.now();
        this.traceId = MDC.get(TakeshiConstants.TRACE_ID_KEY);
    }

}

