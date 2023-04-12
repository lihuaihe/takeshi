package com.takeshi.exception;

import com.takeshi.constants.SysCode;
import com.takeshi.pojo.vo.ResponseDataVO;
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
        super(GsonUtil.toJson(ResponseDataVO.success(SysCode.FAIL)));
    }

    /**
     * 异常
     *
     * @param message 消息
     */
    public TakeshiException(String message) {
        super(GsonUtil.toJson(ResponseDataVO.fail(message)));
    }

    /**
     * 异常
     *
     * @param code    状态码
     * @param message 消息
     */
    public TakeshiException(int code, String message) {
        super(GsonUtil.toJson(ResponseDataVO.success(code, message)));
    }

    /**
     * 异常
     *
     * @param resBean 消息
     */
    public TakeshiException(ResponseDataVO.ResBean resBean) {
        super(GsonUtil.toJson(ResponseDataVO.success(resBean)));
    }

    /**
     * 异常
     *
     * @param resBean 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public TakeshiException(ResponseDataVO.ResBean resBean, Object... args) {
        super(GsonUtil.toJson(ResponseDataVO.success(resBean, args)));
    }

    /**
     * 异常
     *
     * @param responseDataVO responseDataVO
     */
    public TakeshiException(ResponseDataVO<?> responseDataVO) {
        super(GsonUtil.toJson(responseDataVO));
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
     * @param resBean 消息
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(ResponseDataVO.ResBean resBean) {
        return () -> new TakeshiException(resBean);
    }

    /**
     * 包装成Supplier
     *
     * @param resBean 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(ResponseDataVO.ResBean resBean, Object... args) {
        return () -> new TakeshiException(resBean, args);
    }

    /**
     * 包装成Supplier
     *
     * @param responseDataVO responseDataVO
     * @return Supplier
     */
    public static Supplier<TakeshiException> supplier(ResponseDataVO<?> responseDataVO) {
        return () -> new TakeshiException(responseDataVO);
    }

}
