package com.takeshi.controller;

import com.takeshi.pojo.vo.ResponseDataVO;

/**
 * controller 层通用数据处理
 * <pre>{@code
 * // ignoreParameters 可以指定排除接口文档中不需要显示的字段
 * @ApiOperationSupport(author = NANASE_TAKESHI, ignoreParameters = "notesId")
 * @Validated 是@Valid的高级用法，可以进行分组校验，例如 @NotNull(groups = Update.class)
 * }
 * </pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public abstract class BaseController {

    /**
     * 接口开发作者
     */
    public static final String NANASE_TAKESHI = "Nanase Takeshi";

    /**
     * 返回结果状态信息
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success() {
        return ResponseDataVO.success();
    }

    /**
     * 返回结果状态信息
     *
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(int code, String message) {
        return ResponseDataVO.success(code, message);
    }

    /**
     * 返回结果状态信息
     *
     * @param code
     * @param message
     * @param args
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(int code, String message, Object[] args) {
        return ResponseDataVO.success(code, message, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(int code, String message, T data) {
        return ResponseDataVO.success(code, message, data);
    }

    /**
     * 返回结果状态信息
     *
     * @param code
     * @param message
     * @param data
     * @param args
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(int code, String message, T data, Object... args) {
        return ResponseDataVO.success(code, message, data, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(T data) {
        return ResponseDataVO.success(data);
    }

    /**
     * 返回结果状态信息
     *
     * @param resBean
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(ResponseDataVO.ResBean resBean) {
        return ResponseDataVO.success(resBean);
    }

    /**
     * 返回结果状态信息
     *
     * @param resBean
     * @param args
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(ResponseDataVO.ResBean resBean, Object[] args) {
        return ResponseDataVO.success(resBean, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param resBean
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(ResponseDataVO.ResBean resBean, T date) {
        return ResponseDataVO.success(resBean, date);
    }

    /**
     * 返回结果状态信息
     *
     * @param resBean
     * @param args
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> success(ResponseDataVO.ResBean resBean, T date, Object... args) {
        return ResponseDataVO.success(resBean, date, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag) {
        return ResponseDataVO.retBool(flag);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag
     * @param failMessage
     * @param args
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag, String failMessage, Object... args) {
        return ResponseDataVO.retBool(flag, failMessage, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag
     * @param resBean
     * @param args
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag, ResponseDataVO.ResBean resBean, Object... args) {
        return ResponseDataVO.retBool(flag, resBean, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag
     * @param resBeanOfTrue  flag为true该返回的结果
     * @param resBeanOfFalse flag为false该返回的结果
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag, ResponseDataVO.ResBean resBeanOfTrue, ResponseDataVO.ResBean resBeanOfFalse) {
        return ResponseDataVO.retBool(flag, resBeanOfTrue, resBeanOfFalse);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag
     * @param resBeanOfTrue  flag为true该返回的结果
     * @param resBeanOfFalse flag为false该返回的结果
     * @param args
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> retBool(boolean flag, ResponseDataVO.ResBean resBeanOfTrue, ResponseDataVO.ResBean resBeanOfFalse, Object... args) {
        return ResponseDataVO.retBool(flag, resBeanOfTrue, resBeanOfFalse, args);
    }

    /**
     * 返回失败结果状态
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> fail() {
        return ResponseDataVO.fail();
    }

    /**
     * 返回失败结果状态
     *
     * @param message
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> fail(String message) {
        return ResponseDataVO.fail(message);
    }

    /**
     * 返回失败结果状态
     *
     * @param message
     * @param args
     * @param <T>
     * @return
     */
    public static <T> ResponseDataVO<T> fail(String message, Object... args) {
        return ResponseDataVO.fail(message, args);
    }

}
