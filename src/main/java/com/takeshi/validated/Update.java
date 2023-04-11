package com.takeshi.validated;

import jakarta.validation.groups.Default;

/**
 * Validated分组校验
 * 搭配 @ApiOperationSupport(ignoreParameters = "id") 使用
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2022/4/12 16:25
 */
public interface Update extends Default {
}
