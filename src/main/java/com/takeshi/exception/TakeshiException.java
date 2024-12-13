package com.takeshi.exception;

import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.pojo.bo.RetBO;
import com.takeshi.util.TakeshiUtil;
import lombok.Getter;

import java.io.Serial;
import java.util.function.Supplier;

/**
 * TakeshiException
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Getter
public class TakeshiException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 接口统一返回值
     */
    private final ResponseData<?> responseData;

    /**
     * 构造函数
     */
    public TakeshiException() {
        super(TakeshiUtil.formatMessage(TakeshiCode.FAIL.getMessage()));
        this.responseData = ResponseData.instance(TakeshiCode.FAIL);
    }

    /**
     * 异常
     *
     * @param message 消息
     */
    public TakeshiException(String message) {
        super(TakeshiUtil.formatMessage(message));
        this.responseData = ResponseData.instance(super.getMessage());
    }

    /**
     * 异常
     *
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(String message, Object[] args) {
        super(TakeshiUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(super.getMessage());
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     */
    public TakeshiException(int code, String message) {
        super(TakeshiUtil.formatMessage(message));
        this.responseData = ResponseData.instance(code, super.getMessage());
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(int code, String message, Object[] args) {
        super(TakeshiUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(code, super.getMessage());
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param <T>     T
     */
    public <T> TakeshiException(int code, String message, T data) {
        super(TakeshiUtil.formatMessage(message));
        this.responseData = ResponseData.instance(code, super.getMessage(), data);
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     */
    public <T> TakeshiException(int code, String message, T data, Object[] args) {
        super(TakeshiUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(code, super.getMessage(), data);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param <T>      T
     */
    public <T> TakeshiException(Object metadata, int code, String message) {
        super(TakeshiUtil.formatMessage(message));
        this.responseData = ResponseData.instance(code, super.getMessage()).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     */
    public <T> TakeshiException(Object metadata, int code, String message, Object[] args) {
        super(TakeshiUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(code, super.getMessage()).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param data     附加对象
     * @param <T>      T
     */
    public <T> TakeshiException(Object metadata, int code, String message, T data) {
        super(TakeshiUtil.formatMessage(message));
        this.responseData = ResponseData.instance(code, super.getMessage(), data).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     */
    public <T> TakeshiException(Object metadata, int code, String message, T data, Object[] args) {
        super(TakeshiUtil.formatMessage(message, args));
        this.responseData = ResponseData.instance(code, super.getMessage(), data).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param retBO 消息
     */
    public TakeshiException(RetBO retBO) {
        super(TakeshiUtil.formatMessage(retBO.getMessage()));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage());
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(RetBO retBO, Object[] args) {
        super(TakeshiUtil.formatMessage(retBO.getMessage(), args));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage());
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param <T>   T
     */
    public <T> TakeshiException(RetBO retBO, T data) {
        super(TakeshiUtil.formatMessage(retBO.getMessage()));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage(), data);
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     */
    public <T> TakeshiException(RetBO retBO, T data, Object[] args) {
        super(TakeshiUtil.formatMessage(retBO.getMessage(), args));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage(), data);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     */
    public TakeshiException(Object metadata, RetBO retBO) {
        super(TakeshiUtil.formatMessage(retBO.getMessage()));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage()).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(Object metadata, RetBO retBO, Object[] args) {
        super(TakeshiUtil.formatMessage(retBO.getMessage(), args));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage()).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param <T>      T
     */
    public <T> TakeshiException(Object metadata, RetBO retBO, T data) {
        super(TakeshiUtil.formatMessage(retBO.getMessage()));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage(), data).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     */
    public <T> TakeshiException(Object metadata, RetBO retBO, T data, Object[] args) {
        super(TakeshiUtil.formatMessage(retBO.getMessage(), args));
        this.responseData = ResponseData.instance(retBO.getCode(), super.getMessage(), data).setMetadata(metadata);
    }

    /**
     * 异常
     *
     * @param responseData responseData
     */
    public TakeshiException(ResponseData<?> responseData) {
        super(responseData.getMessage());
        this.responseData = responseData;
    }

    /**
     * 包装成Supplier
     *
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier() {
        return TakeshiException::new;
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
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(String message, Object[] args) {
        return () -> new TakeshiException(message, args);
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
     * @param code    状态码
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(int code, String message, Object[] args) {
        return () -> new TakeshiException(code, message, args);
    }

    /**
     * 包装成Supplier
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param <T>     T
     * @return Supplier
     */
    public static <T> Supplier<TakeshiException> supplier(int code, String message, T data) {
        return () -> new TakeshiException(code, message, data);
    }

    /**
     * 包装成Supplier
     *
     * @param code    状态码
     * @param message 消息
     * @param data    附加对象
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return Supplier
     */
    public static <T> Supplier<TakeshiException> supplier(int code, String message, T data, Object[] args) {
        return () -> new TakeshiException(code, message, data, args);
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
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(Object metadata, int code, String message, Object[] args) {
        return () -> new TakeshiException(metadata, code, message, args);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param data     附加对象
     * @param <T>      T
     * @return Supplier
     */
    public static <T> Supplier<TakeshiException> supplier(Object metadata, int code, String message, T data) {
        return () -> new TakeshiException(metadata, code, message, data);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param code     状态码
     * @param message  消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return Supplier
     */
    public static <T> Supplier<TakeshiException> supplier(Object metadata, int code, String message, T data, Object[] args) {
        return () -> new TakeshiException(metadata, code, message, data, args);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(RetBO retBO) {
        return () -> new TakeshiException(retBO);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(RetBO retBO, Object[] args) {
        return () -> new TakeshiException(retBO, args);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param <T>   T
     * @return Supplier
     */
    public static <T> Supplier<TakeshiException> supplier(RetBO retBO, T data) {
        return () -> new TakeshiException(retBO, data);
    }

    /**
     * 包装成Supplier
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     * @return Supplier
     */
    public static <T> Supplier<TakeshiException> supplier(RetBO retBO, T data, Object[] args) {
        return () -> new TakeshiException(retBO, data, args);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(Object metadata, RetBO retBO) {
        return () -> new TakeshiException(metadata, retBO);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(Object metadata, RetBO retBO, Object[] args) {
        return () -> new TakeshiException(metadata, retBO, args);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param <T>      T
     * @return Supplier
     */
    public static <T> Supplier<TakeshiException> supplier(Object metadata, RetBO retBO, T data) {
        return () -> new TakeshiException(metadata, retBO, data);
    }

    /**
     * 包装成Supplier
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return Supplier
     */
    public static <T> Supplier<TakeshiException> supplier(Object metadata, RetBO retBO, T data, Object[] args) {
        return () -> new TakeshiException(metadata, retBO, data, args);
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
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, RetBO retBO) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(retBO);
        }
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
    public static <T> T execute(Supplier<T> supplier, RetBO retBO, Object[] args) {
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
     * @param retBO    消息
     * @param data     附加对象
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, RetBO retBO, T data) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(retBO, data);
        }
    }

    /**
     * 执行方法且将异常包装成TakeshiException抛出
     *
     * @param supplier supplier
     * @param retBO    消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, RetBO retBO, T data, Object[] args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(retBO, data, args);
        }
    }

    /**
     * 执行方法且将异常包装成TakeshiException抛出
     *
     * @param supplier supplier
     * @param metadata 元数据
     * @param retBO    消息
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(metadata, retBO);
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
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO, Object[] args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(metadata, retBO, args);
        }
    }

    /**
     * 执行方法且将异常包装成TakeshiException抛出
     *
     * @param supplier supplier
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO, T data) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(metadata, retBO, data);
        }
    }

    /**
     * 执行方法且将异常包装成TakeshiException抛出
     *
     * @param supplier supplier
     * @param metadata 元数据
     * @param retBO    消息
     * @param data     附加对象
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>      T
     * @return T
     */
    public static <T> T execute(Supplier<T> supplier, Object metadata, RetBO retBO, T data, Object[] args) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new TakeshiException(metadata, retBO, data, args);
        }
    }

}
