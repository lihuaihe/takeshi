package com.takeshi.exception;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.vo.ResponseDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * GlobalExceptionHandler
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String SQL_CAUSE = "java.sql.SQL";

    /**
     * 异常处理
     *
     * @param exception 异常
     * @return {@link ResponseDataVO}
     */
    @ExceptionHandler(Exception.class)
    public ResponseDataVO<Object> exceptionHandler(Exception exception) {
        log.error("GlobalExceptionHandler.exceptionHandler --> Exception: ", exception);
        if (exception.getCause().toString().startsWith(SQL_CAUSE)) {
            return ResponseDataVO.success(TakeshiCode.DB_ERROR);
        } else {
            return ResponseDataVO.fail(exception.getMessage());
        }
    }

    /**
     * sql异常处理
     *
     * @param exception 异常
     * @return {@link ResponseDataVO}
     */
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseDataVO<Object> sqlExceptionHandler(Exception exception) {
        log.error("GlobalExceptionHandler.sqlExceptionHandler --> SQLException: ", exception);
        return ResponseDataVO.success(TakeshiCode.DB_ERROR);
    }

    /**
     * 运行时异常处理
     *
     * @param runtimeException 异常
     * @return {@link ResponseDataVO}
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseDataVO<Object> runtimeExceptionHandler(RuntimeException runtimeException) {
        Throwable rootCause = ExceptionUtil.getRootCause(runtimeException);
        if (rootCause instanceof NullPointerException) {
            log.error("GlobalExceptionHandler.runtimeExceptionHandler --> NullPointerException: ", rootCause);
            return ResponseDataVO.success(TakeshiCode.SYS_NULL_POINT);
        }
        if (rootCause instanceof SQLException) {
            log.error("GlobalExceptionHandler.runtimeExceptionHandler --> SQLException: ", rootCause);
            return ResponseDataVO.success(TakeshiCode.DB_ERROR);
        }
        if (rootCause instanceof NoSuchElementException) {
            log.error("GlobalExceptionHandler.runtimeExceptionHandler --> NoSuchElementException: ", rootCause);
            return ResponseDataVO.success(TakeshiCode.RESOURCE_DOES_NOT_EXIST);
        }
        if (rootCause instanceof TakeshiException) {
            log.error("GlobalExceptionHandler.takeshiExceptionHandler --> TakeshiException: ", rootCause);
            return JSONUtil.toBean(rootCause.getMessage(), new TypeReference<ResponseDataVO<Object>>() {
            }, false);
        }
        log.error("GlobalExceptionHandler.runtimeExceptionHandler --> RuntimeException: ", rootCause);
        return ResponseDataVO.fail(rootCause.getMessage());
    }

    /**
     * 参数校验异常
     *
     * @param bindException 异常
     * @return {@link ResponseDataVO}
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseDataVO<Object> parameterBindHandler(BindException bindException) {
        log.error("GlobalExceptionHandler.parameterBindHandler --> BindException: ", bindException);
        BindingResult bindingResult = bindException.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        assert fieldError != null;
        //TODO
        return ResponseDataVO.success(TakeshiCode.PARAMETER_ERROR.getCode(), StrUtil.BRACKET_START + fieldError.getField() + StrUtil.BRACKET_END + fieldError.getDefaultMessage());
    }

    /**
     * 全局异常拦截（拦截项目中的NotLoginException异常）
     *
     * @param notLoginException 登录异常
     * @return {@link ResponseDataVO}
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseDataVO<Object> notLoginExceptionHandler(NotLoginException notLoginException) {
        log.error("GlobalExceptionHandler.notLoginExceptionHandler --> NotLoginException: ", notLoginException);
        if (notLoginException.getType().equals(NotLoginException.NOT_TOKEN)) {
            // 未提供token
            return ResponseDataVO.success(TakeshiCode.NOT_TOKEN);
        } else if (notLoginException.getType().equals(NotLoginException.INVALID_TOKEN)) {
            // token无效
            return ResponseDataVO.success(TakeshiCode.INVALID_TOKEN);
        } else if (notLoginException.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            // token已过期
            return ResponseDataVO.success(TakeshiCode.TOKEN_TIMEOUT);
        } else if (notLoginException.getType().equals(NotLoginException.BE_REPLACED)) {
            // token已被顶下线
            return ResponseDataVO.success(TakeshiCode.BE_REPLACED);
        } else if (notLoginException.getType().equals(NotLoginException.KICK_OUT)) {
            // token已被踢下线
            return ResponseDataVO.success(TakeshiCode.KICK_OUT);
        } else {
            // 当前会话未登录
            return ResponseDataVO.success(TakeshiCode.NOT_LOGGED);
        }
    }

    /**
     * 全局异常拦截（拦截项目中的NotRoleException异常）
     *
     * @param notRoleException 角色异常
     * @return {@link ResponseDataVO}
     */
    @ExceptionHandler(NotRoleException.class)
    public ResponseDataVO<Object> notRoleExceptionHandler(NotRoleException notRoleException) {
        log.error("GlobalExceptionHandler.notRoleExceptionHandler --> NotRoleException: ", notRoleException);
        return ResponseDataVO.success(TakeshiCode.NOT_ROLE_EXCEPTION, new Object[]{notRoleException.getRole()});
    }

    /**
     * 全局异常拦截（拦截项目中的NotPermissionException异常）
     *
     * @param notPermissionException 权限异常
     * @return {@link ResponseDataVO}
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseDataVO<Object> notPermissionExceptionHandler(NotPermissionException notPermissionException) {
        log.error("GlobalExceptionHandler.notPermissionExceptionHandler --> NotPermissionException: ", notPermissionException);
        return ResponseDataVO.success(TakeshiCode.NOT_PERMISSION_EXCEPTION, new Object[]{notPermissionException.getPermission()});
    }

    /**
     * 全局异常拦截（拦截项目中的DisableServiceException异常）
     *
     * @param disableServiceException 禁用异常
     * @return {@link ResponseDataVO}
     */
    @ExceptionHandler(DisableServiceException.class)
    public ResponseDataVO<Object> disableLoginExceptionHandler(DisableServiceException disableServiceException) {
        log.error("GlobalExceptionHandler.disableLoginExceptionHandler --> DisableLoginException: ", disableServiceException);
        return ResponseDataVO.success(TakeshiCode.DISABLE_SERVICE_EXCEPTION, new Object[]{disableServiceException.getDisableTime()});
    }

}
