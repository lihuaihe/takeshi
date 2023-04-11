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

    public TakeshiException() {
        super(GsonUtil.toJson(ResponseDataVO.success(SysCode.FAIL)));
    }

    public TakeshiException(String message) {
        super(GsonUtil.toJson(ResponseDataVO.fail(message)));
    }

    public TakeshiException(int code, String message) {
        super(GsonUtil.toJson(ResponseDataVO.success(code, message)));
    }

    public TakeshiException(ResponseDataVO.ResBean resBean) {
        super(GsonUtil.toJson(ResponseDataVO.success(resBean)));
    }

    public TakeshiException(ResponseDataVO.ResBean resBean, Object... args) {
        super(GsonUtil.toJson(ResponseDataVO.success(resBean, args)));
    }

    public TakeshiException(ResponseDataVO<?> responseDataVO) {
        super(GsonUtil.toJson(responseDataVO));
    }

    public static Supplier<TakeshiException> supplier(String message) {
        return () -> new TakeshiException(message);
    }

    public static Supplier<TakeshiException> supplier(int code, String message) {
        return () -> new TakeshiException(code, message);
    }

    public static Supplier<TakeshiException> supplier(ResponseDataVO.ResBean resBean) {
        return () -> new TakeshiException(resBean);
    }

    public static Supplier<TakeshiException> supplier(ResponseDataVO.ResBean resBean, Object... args) {
        return () -> new TakeshiException(resBean, args);
    }

    public static Supplier<TakeshiException> supplier(ResponseDataVO<?> responseDataVO) {
        return () -> new TakeshiException(responseDataVO);
    }

}
