package com.takeshi.exception;

import cn.dev33.satoken.exception.*;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.takeshi.config.StaticConfig;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.basic.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.IbatisException;
import org.redisson.client.RedisException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;

/**
 * GlobalExceptionHandler
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String SQL_CAUSE = "java.sql.SQL";

    private final ObjectMapper objectMapper;

    /**
     * 异常处理
     *
     * @param exception 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(Exception.class)
    public ResponseData<?> exceptionHandler(Exception exception) {
        log.error("GlobalExceptionHandler.exceptionHandler --> Exception: ", exception);
        if (null != exception.getCause() && exception.getCause().toString().startsWith(SQL_CAUSE)) {
            return ResponseData.retData(TakeshiCode.DB_ERROR);
        } else {
            return ResponseData.fail(exception.getMessage());
        }
    }

    /**
     * 空指针异常处理
     *
     * @param nullPointerException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseData<?> nullPointerException(NullPointerException nullPointerException) {
        log.error("GlobalExceptionHandler.nullPointerException --> NullPointerException: ", nullPointerException);
        return ResponseData.retData(TakeshiCode.SYS_NULL_POINT);
    }

    /**
     * sql异常处理
     *
     * @param exception 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler({SQLException.class, DataAccessException.class, IbatisException.class})
    public ResponseData<?> sqlExceptionHandler(Exception exception) {
        log.error("GlobalExceptionHandler.sqlExceptionHandler --> SQLException: ", exception);
        return ResponseData.retData(TakeshiCode.DB_ERROR);
    }

    /**
     * redis异常处理
     *
     * @param redisException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(RedisException.class)
    public ResponseData<?> redisExceptionHandler(RedisException redisException) {
        log.error("GlobalExceptionHandler.redisExceptionHandler --> redisException: ", redisException);
        return ResponseData.retData(TakeshiCode.REDIS_ERROR);
    }

    /**
     * 请求的资源不存在异常处理
     *
     * @param noSuchElementException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseData<?> noSuchElementException(NoSuchElementException noSuchElementException) {
        log.error("GlobalExceptionHandler.noSuchElementException --> NoSuchElementException: ", noSuchElementException);
        return ResponseData.retData(TakeshiCode.RESOURCE_DOES_NOT_EXIST);
    }

    /**
     * 请求的接口路径或静态资源不存在异常处理
     *
     * @param noResourceFoundException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> noSuchElementException(NoResourceFoundException noResourceFoundException) {
        if (!"No static resource rdoc-project.md.".equals(noResourceFoundException.getMessage())) {
            // rdoc-project.md文件没有创建时不打印异常了
            log.error("GlobalExceptionHandler.noSuchElementException --> noResourceFoundException: ", noResourceFoundException);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 非法参数异常处理
     *
     * @param illegalArgumentException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseData<?> illegalArgumentException(IllegalArgumentException illegalArgumentException) {
        log.error("GlobalExceptionHandler.illegalArgumentException --> IllegalArgumentException: ", illegalArgumentException);
        if (StrUtil.startWith(illegalArgumentException.getMessage(), "[Assertion failed]")) {
            // 默认的非法参数异常
            return ResponseData.retData(TakeshiCode.PARAMETER_ERROR);
        }
        // 自定义的非法参数异常
        return ResponseData.fail(illegalArgumentException.getMessage());
    }

    /**
     * 无效格式异常处理
     *
     * @param invalidFormatException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseData<?> invalidFormatException(InvalidFormatException invalidFormatException) {
        log.error("GlobalExceptionHandler.invalidFormatException --> InvalidFormatException: ", invalidFormatException);
        if (invalidFormatException.getTargetType().isEnum()) {
            // 传递的值和接收的枚举类型值不匹配
            Object[] enumConstants = invalidFormatException.getTargetType().getEnumConstants();
            return ResponseData.retData(TakeshiCode.INVALID_VALUE, new Object[]{invalidFormatException.getValue(), Arrays.toString(enumConstants)});
        }
        return ResponseData.fail(invalidFormatException.getMessage());
    }

    /**
     * 自定义的TakeshiException异常处理
     *
     * @param takeshiException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(TakeshiException.class)
    public ResponseData<?> takeshiException(TakeshiException takeshiException) {
        log.error("GlobalExceptionHandler.takeshiException --> TakeshiException: ", takeshiException);
        return takeshiException.getResponseData();
    }

    /**
     * concurrent包下任务在完成结果或任务的过程中遇到错误或其他异常时抛出异常
     *
     * @param completionException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(CompletionException.class)
    public ResponseData<?> completionException(CompletionException completionException) {
        Throwable rootCause = ExceptionUtil.getRootCause(completionException);
        if (rootCause instanceof TakeshiException takeshiException) {
            return this.takeshiException(takeshiException);
        }
        log.error("GlobalExceptionHandler.completionException --> completionException: ", completionException);
        return ResponseData.fail(completionException.getMessage());
    }

    /**
     * 运行时异常处理
     *
     * @param runtimeException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseData<?> runtimeExceptionHandler(RuntimeException runtimeException) {
        log.error("GlobalExceptionHandler.runtimeExceptionHandler --> RuntimeException: ", runtimeException);
        return ResponseData.fail(ExceptionUtil.getRootCause(runtimeException).getMessage());
    }

    /**
     * 参数校验异常
     *
     * @param bindException 异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseData<?> parameterBindHandler(BindException bindException) {
        log.error("GlobalExceptionHandler.parameterBindHandler --> BindException: ", bindException);
        BindingResult bindingResult = bindException.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String msg = TakeshiCode.PARAMETER_ERROR.getMessage();
        if (ObjUtil.isNotNull(fieldError)) {
            msg = StaticConfig.takeshiProperties.isIncludeErrorFieldName()
                    ? StrUtil.format("[{}] {}", fieldError.getField(), fieldError.getDefaultMessage())
                    : fieldError.getDefaultMessage();
        }
        return ResponseData.retData(TakeshiCode.PARAMETER_ERROR.getCode(), msg);
    }

    /**
     * 全局异常拦截（拦截项目中的NotLoginException异常）
     *
     * @param notLoginException 登录异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseData<?> notLoginExceptionHandler(NotLoginException notLoginException) {
        log.error("GlobalExceptionHandler.notLoginExceptionHandler --> NotLoginException: ", notLoginException);
        if (notLoginException.getType().equals(NotLoginException.NOT_TOKEN)) {
            // 未提供token
            return ResponseData.retData(TakeshiCode.NOT_TOKEN);
        } else if (notLoginException.getType().equals(NotLoginException.INVALID_TOKEN)) {
            // token无效
            return ResponseData.retData(TakeshiCode.INVALID_TOKEN);
        } else if (notLoginException.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            // token已过期
            return ResponseData.retData(TakeshiCode.TOKEN_TIMEOUT);
        } else if (notLoginException.getType().equals(NotLoginException.BE_REPLACED)) {
            // token已被顶下线
            return ResponseData.retData(TakeshiCode.BE_REPLACED);
        } else if (notLoginException.getType().equals(NotLoginException.KICK_OUT)) {
            // token已被踢下线
            return ResponseData.retData(TakeshiCode.KICK_OUT);
        } else {
            // 当前会话未登录
            return ResponseData.retData(TakeshiCode.NOT_LOGGED);
        }
    }

    /**
     * 全局异常拦截（拦截项目中的SaSignException异常）
     *
     * @param saSignException 签名异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(SaSignException.class)
    public ResponseData<?> saSignExceptionHandler(SaSignException saSignException) {
        log.error("GlobalExceptionHandler.saSignExceptionHandler --> SaSignException: ", saSignException);
        return ResponseData.retData(TakeshiCode.SIGN_ERROR.cloneWithMessage(saSignException.getMessage()));
    }

    /**
     * 全局异常拦截（拦截项目中的NotRoleException异常）
     *
     * @param notRoleException 角色异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(NotRoleException.class)
    public ResponseData<?> notRoleExceptionHandler(NotRoleException notRoleException) {
        log.error("GlobalExceptionHandler.notRoleExceptionHandler --> NotRoleException: ", notRoleException);
        return ResponseData.retData(TakeshiCode.NOT_ROLE_EXCEPTION, new Object[]{notRoleException.getRole()});
    }

    /**
     * 全局异常拦截（拦截项目中的NotPermissionException异常）
     *
     * @param notPermissionException 权限异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseData<?> notPermissionExceptionHandler(NotPermissionException notPermissionException) {
        log.error("GlobalExceptionHandler.notPermissionExceptionHandler --> NotPermissionException: ", notPermissionException);
        return ResponseData.retData(TakeshiCode.NOT_PERMISSION_EXCEPTION, new Object[]{notPermissionException.getPermission()});
    }

    /**
     * 全局异常拦截（拦截项目中的DisableServiceException异常）
     *
     * @param disableServiceException 禁用异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(DisableServiceException.class)
    public ResponseData<?> disableLoginExceptionHandler(DisableServiceException disableServiceException) {
        log.error("GlobalExceptionHandler.disableLoginExceptionHandler --> DisableLoginException: ", disableServiceException);
        return ResponseData.retData(TakeshiCode.DISABLE_SERVICE_EXCEPTION, new Object[]{disableServiceException.getDisableTime()});
    }

    /**
     * 全局异常拦截（拦截项目中的BackResultException异常）
     *
     * @param backResultException 停止匹配异常
     * @return {@link ResponseData}
     */
    @ExceptionHandler(BackResultException.class)
    public ResponseData<?> backResultExceptionHandler(BackResultException backResultException) {
        log.error("GlobalExceptionHandler.disableLoginExceptionHandler --> BackResultException: ", backResultException);
        if (backResultException.result instanceof ResponseData<?> responseData) {
            return responseData;
        } else {
            return ResponseData.retData(backResultException.result);
        }
    }

}
