package com.takeshi.pojo.basic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 系统操作日志
 * </p>
 *
 * @author 七濑武【Nanase Takeshi】
 * @since 2023-06-29
 */
@Data
@Accessors(chain = true)
public class TbSysLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long logId;

    /**
     * 日志类型
     */
    @Schema(description = "日志类型")
    private String logType;

    /**
     * 登录的用户ID
     */
    @Schema(description = "登录的用户ID")
    private Object loginId;

    /**
     * 请求的IP
     */
    @Schema(description = "请求的IP")
    private Long clientIp;

    /**
     * 请求的IP对应的地址
     */
    @Schema(description = "请求的IP对应的地址")
    private String clientIpAddress;

    /**
     * 用户代理
     */
    @Schema(description = "用户代理")
    private String userAgent;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式")
    private String httpMethod;

    /**
     * 请求的方法，带包名类名的完整的方法名
     */
    @Schema(description = "请求的方法，带包名类名的完整的方法名")
    private String methodName;

    /**
     * 请求URL地址
     */
    @Schema(description = "请求URL地址")
    private String requestUrl;

    /**
     * 请求头部
     */
    @Schema(description = "请求头部")
    private String requestHeader;

    /**
     * 请求的参数
     */
    @Schema(description = "请求的参数")
    private String requestParams;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private String responseData;

    /**
     * 日志追踪ID
     */
    @Schema(description = "日志追踪ID")
    private String traceId;

    /**
     * 响应成功了的
     */
    @Schema(description = "响应成功了的")
    private Boolean successful;

    /**
     * 请求时间
     */
    @Schema(description = "请求时间")
    private Long requestTime;

    /**
     * 消耗时间
     */
    @Schema(description = "消耗时间")
    private Long costTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Long createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Long updateTime;

}
