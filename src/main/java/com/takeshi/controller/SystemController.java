package com.takeshi.controller;

import cn.hutool.crypto.asymmetric.KeyType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.config.StaticConfig;
import com.takeshi.pojo.vo.ResponseDataVO;
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
public class SystemController extends BaseController {

    /**
     * 使用公钥加密数据
     *
     * @param object object
     * @return ResponseDataVO
     */
    @Operation(summary = "使用公钥加密数据")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping("/encrypt")
    public ResponseDataVO<Object> encrypt(@RequestBody Object object) {
        String data = GsonUtil.toJson(object);
        log.info("SystemController.encrypt --> data: {}", data);
        return success(StaticConfig.rsa.encryptBase64(data, KeyType.PublicKey));
    }

    /**
     * 使用公钥解密数据
     *
     * @param object object
     * @return ResponseDataVO
     * @throws JsonProcessingException JsonProcessingException
     */
    @Operation(summary = "使用公钥解密数据")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping("/decrypt")
    public ResponseDataVO<Object> decrypt(@RequestBody Object object) throws JsonProcessingException {
        String data = GsonUtil.toJson(object);
        log.info("SystemController.decrypt --> data: {}", data);
        return success(new ObjectMapper().readTree(StaticConfig.rsa.decryptStr(data, KeyType.PublicKey)));
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
    public ResponseDataVO<Object> generate(HttpServletRequest request) {
        return success(TakeshiUtil.signParams(request));
    }

}
