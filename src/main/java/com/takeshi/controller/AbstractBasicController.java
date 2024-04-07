package com.takeshi.controller;

import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.pojo.bo.RetBO;

/**
 * controller 层通用数据处理
 *
 * @author 七濑武【Nanase Takeshi】
 */
public abstract class AbstractBasicController {

    /**
     * 接口开发作者
     */
    protected static final String NANASE_TAKESHI = "Nanase Takeshi";

    /**
     * 返回成功结果状态信息
     *
     * @param <T> T
     * @return T
     */
    protected static <T> ResponseData<T> success() {
        return ResponseData.success();
    }

    /**
     * 返回失败结果状态
     *
     * @param <T> T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> fail() {
        return ResponseData.fail();
    }

    /**
     * 返回失败结果状态
     *
     * @param message 消息
     * @param <T>     T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> fail(String message) {
        return ResponseData.fail(message);
    }

    /**
     * 返回失败结果状态
     *
     * @param message 消息
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>     T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> fail(String message, Object... args) {
        return ResponseData.fail(message, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> retData(int code, String message) {
        return ResponseData.retData(code, message);
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
    protected static <T> ResponseData<T> retData(int code, String message, Object[] args) {
        return ResponseData.retData(code, message, args);
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
    protected static <T> ResponseData<T> retData(int code, String message, T data) {
        return ResponseData.retData(code, message, data);
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
    protected static <T> ResponseData<T> retData(int code, String message, T data, Object... args) {
        return ResponseData.retData(code, message, data, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param data 附加对象
     * @param <T>  T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> retData(T data) {
        return ResponseData.retData(data);
    }

    /**
     * 返回结果状态信息
     *
     * @param retBO 消息
     * @param <T>   T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> retData(RetBO retBO) {
        return ResponseData.retData(retBO);
    }

    /**
     * 返回结果状态信息
     *
     * @param retBO 消息
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> retData(RetBO retBO, Object[] args) {
        return ResponseData.retData(retBO, args);
    }

    /**
     * 返回结果状态信息
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param <T>   T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> retData(RetBO retBO, T data) {
        return ResponseData.retData(retBO, data);
    }

    /**
     * 返回结果状态信息
     *
     * @param retBO 消息
     * @param data  附加对象
     * @param args  将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>   T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> retData(RetBO retBO, T data, Object... args) {
        return ResponseData.retData(retBO, data, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag 标志
     * @param <T>  T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> retBool(boolean flag) {
        return ResponseData.retBool(flag);
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
    protected static <T> ResponseData<T> retBool(boolean flag, String failMessage, Object... args) {
        return ResponseData.retBool(flag, failMessage, args);
    }

    /**
     * 根据布尔值返回结果状态信息
     *
     * @param flag         标志
     * @param retBOOfFalse flag为false该返回的结果
     * @param args         将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>          T
     * @return {@link ResponseData}
     */
    protected static <T> ResponseData<T> retBool(boolean flag, RetBO retBOOfFalse, Object... args) {
        return ResponseData.retBool(flag, retBOOfFalse, args);
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
    protected static <T> ResponseData<T> retBool(boolean flag, RetBO retBOOfTrue, RetBO retBOOfFalse) {
        return ResponseData.retBool(flag, retBOOfTrue, retBOOfFalse);
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
    protected static <T> ResponseData<T> retBool(boolean flag, RetBO retBOOfTrue, RetBO retBOOfFalse, Object... args) {
        return ResponseData.retBool(flag, retBOOfTrue, retBOOfFalse, args);
    }

    /**
     * 根据布尔值返回结果状态信息，数据是否存在
     *
     * @param flag 标志
     * @param args 将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @param <T>  T
     * @return {@link ResponseData}
     */
    private static <T> ResponseData<T> retExist(boolean flag, Object... args) {
        return ResponseData.retExist(flag, args);
    }

}
