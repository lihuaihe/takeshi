package com.takeshi.exception;

import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.pojo.bo.RetBO;
import com.takeshi.util.GsonUtil;

import java.io.Serial;
import java.util.function.Supplier;

/**
 * TakeshiException
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class TakeshiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     */
    public TakeshiException() {
        super(GsonUtil.toJson(ResponseData.retData(TakeshiCode.FAIL)));
    }

    /**
     * 异常
     *
     * @param message 消息
     */
    public TakeshiException(String message) {
        super(GsonUtil.toJson(ResponseData.fail(message)));
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     */
    public TakeshiException(int code, String message) {
        super(GsonUtil.toJson(ResponseData.retData(code, message)));
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     */
    public TakeshiException(Object metadata, int code, String message) {
        super(GsonUtil.toJson(ResponseData.retData(code, message).setMetadata(metadata)));
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(RetBO retBO, Object... args) {
        super(GsonUtil.toJson(ResponseData.retData(retBO, args)));
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(Object metadata, RetBO retBO, Object... args) {
        super(GsonUtil.toJson(ResponseData.retData(retBO, args).setMetadata(metadata)));
    }

    /**
     * 异常
     *
     * @param responseData responseData
     */
    public TakeshiException(ResponseData<?> responseData) {
        super(GsonUtil.toJson(responseData));
    }

    /**
     * 包装成Supplier
     *
     * @param message 消息
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(String message) {
        return () -> new TakeshiException(message);
    }

    /**
     * 包装成Supplier
     *
     * @param code    状态码
     * @param message 消息
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(int code, String message) {
        return () -> new TakeshiException(code, message);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(Object metadata, int code, String message) {
        return () -> new TakeshiException(metadata, code, message);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(RetBO retBO, Object... args) {
        return () -> new TakeshiException(retBO, args);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(Object metadata, RetBO retBO, Object... args) {
        return () -> new TakeshiException(metadata, retBO, args);
    }

    /**
     * 包装成Supplier
     *
     * @param responseData responseData
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(ResponseData<?> responseData) {
        return () -> new TakeshiException(responseData);
    }

    /**
     * 执行方法且将异常包装成TakeshiException抛出
     *
     * @param supplier supplier
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, RetBO retBO, Object... args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(retBO, args);
        }
    }

    /**
     * 执行方法且将异常包装成TakeshiException抛出
     *
     * @param supplier supplier
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO, Object... args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(metadata, retBO, args);
        }
    }

}
