package com.takeshi.controller;

import cn.hutool.crypto.asymmetric.KeyType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.config.StaticConfig;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.util.GsonUtil;
import com.takeshi.util.TakeshiUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * SystemController
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@RestController
@RequestMapping("/system")
@Hidden
@Tag(name = "系统接口")
@SystemSecurity(all = true)
public class SystemController extends AbstractBaseController {

    /**
     * 使用公钥加密数据
     *
     * @param object object
     * @return ResponseData
     */
    @Operation(summary = "使用公钥加密数据")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping("/encrypt")
    public ResponseData<Object> encrypt(@RequestBody Object object) {
        String data = GsonUtil.toJson(object);
        log.info("SystemController.encrypt --> data: {}", data);
        return retData(StaticConfig.rsa.encryptBase64(data, KeyType.PublicKey));
    }

    /**
     * 使用公钥解密数据
     *
     * @param object object
     * @return ResponseData
     * @throws JsonProcessingException JsonProcessingException
     */
    @Operation(summary = "使用公钥解密数据")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping("/decrypt")
    public ResponseData<Object> decrypt(@RequestBody Object object) throws JsonProcessingException {
        String data = GsonUtil.toJson(object);
        log.info("SystemController.decrypt --> data: {}", data);
        return retData(new ObjectMapper().readTree(StaticConfig.rsa.decryptStr(data, KeyType.PublicKey)));
    }

    /**
     * 测试生成sign值
     *
     * @param request request
     * @return 测试生成sign值
     */
    @Operation(summary = "测试生成sign值")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @RequestMapping(value = "/sign", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
    public ResponseData<Object> generate(HttpServletRequest request) {
        return retData(TakeshiUtil.signParams(request));
    }

}
