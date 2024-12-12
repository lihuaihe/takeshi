package com.takeshi.exception;

import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.pojo.bo.RetBO;

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
        super(ResponseData.retData(TakeshiCode.FAIL).toString());
    }

    /**
     * 异常
     *
     * @param message 消息
     */
    public TakeshiException(String message) {
        super(ResponseData.fail(message).toString());
    }

    /**
     * 异常
     *
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(String message, Object[] args) {
        super(ResponseData.fail(message, args).toString());
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     */
    public TakeshiException(int code, String message) {
        super(ResponseData.retData(code, message).toString());
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(int code, String message, Object[] args) {
        super(ResponseData.retData(code, message, args).toString());
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
        super(ResponseData.retData(code, message, data).toString());
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
        super(ResponseData.retData(code, message, data, args).toString());
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
        super(ResponseData.retData(code, message).setMetadata(metadata).toString());
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
        super(ResponseData.retData(code, message, args).setMetadata(metadata).toString());
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
        super(ResponseData.retData(code, message, data).setMetadata(metadata).toString());
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
        super(ResponseData.retData(code, message, data, args).setMetadata(metadata).toString());
    }

    /**
     * 异常
     *
     * @param retBO 消息
     */
    public TakeshiException(RetBO retBO) {
        super(ResponseData.retData(retBO).toString());
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(RetBO retBO, Object[] args) {
        super(ResponseData.retData(retBO, args).toString());
    }

    /**
     * 异常
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param <T>   T
     */
    public <T> TakeshiException(RetBO retBO, T data) {
        super(ResponseData.retData(retBO, data).toString());
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
        super(ResponseData.retData(retBO, data, args).toString());
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     */
    public TakeshiException(Object metadata, RetBO retBO) {
        super(ResponseData.retData(retBO).setMetadata(metadata).toString());
    }

    /**
     * 异常
     *
     * @param metadata 元数据
     * @param retBO    消息
     * @param args     将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(Object metadata, RetBO retBO, Object[] args) {
        super(ResponseData.retData(retBO, args).setMetadata(metadata).toString());
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
        super(ResponseData.retData(retBO, data).setMetadata(metadata).toString());
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
        super(ResponseData.retData(retBO, data, args).setMetadata(metadata).toString());
    }

    /**
     * 异常
     *
     * @param responseData responseData
     */
    public TakeshiException(ResponseData<?> responseData) {
        super(responseData.toString());
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
