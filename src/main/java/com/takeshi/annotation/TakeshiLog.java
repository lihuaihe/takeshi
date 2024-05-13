package com.takeshi.annotation;

import com.takeshi.enums.LogTypeEnum;

import java.lang.annotation.*;

/**
 * AOP记录接口日志到数据库中，只需要数据库中存在下面表即可，不需要创建Entity/Mapper/XML
 * <pre>{@code
 * CREATE TABLE `tb_sys_log`
 * (
 *     `log_id`           bigint     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
 *     `log_type`         varchar(16)                             DEFAULT NULL COMMENT '日志类型',
 *     `login_id`         bigint                                  DEFAULT NULL COMMENT '登录的用户ID',
 *     `client_ip`        bigint                                  DEFAULT NULL COMMENT '请求的IP',
 *     `user_agent`       varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户代理',
 *     `http_method`      varchar(8) COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '请求方式',
 *     `method_name`      varchar(300) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求的方法，带包名类名的完整的方法名',
 *     `request_url`      varchar(300) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求URL地址',
 *     `request_header`   json                                    DEFAULT NULL COMMENT '请求头部',
 *     `request_params`   json                                    DEFAULT NULL COMMENT '请求的参数',
 *     `response_data`    json                                    DEFAULT NULL COMMENT '响应数据',
 *     `trace_id`         varchar(32) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '日志追踪ID',
 *     `successful`       tinyint(1) NOT NULL COMMENT '响应成功了的',
 *     `request_time`     timestamp(3)                            DEFAULT NULL COMMENT '请求时间',
 *     `cost_time_millis` bigint                                  DEFAULT NULL COMMENT '消耗时间（单位：毫秒）',
 *     `create_time`      timestamp(3)                            DEFAULT NULL COMMENT '创建时间',
 *     `update_time`      timestamp(3)                            DEFAULT NULL COMMENT '更新时间',
 *     PRIMARY KEY (`log_id`)
 * ) ENGINE = InnoDB
 *   AUTO_INCREMENT = 0
 *   DEFAULT CHARSET = utf8mb4
 *   COLLATE = utf8mb4_general_ci COMMENT ='系统操作日志';
 * }
 * </pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TakeshiLog {

    /**
     * 日志类型
     *
     * @return LogTypeEnum
     */
    LogTypeEnum logType();

    /**
     * 排除的字段名称<br/>
     * 这些参数字段将不会保存到数据库，例如："password"<br/>
     * 自动排除 ["password", "oldPassword", "newPassword", "confirmPassword"]
     *
     * @return String[]
     */
    String[] exclusionFieldName() default {};

}
