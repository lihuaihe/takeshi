package com.takeshi.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.pojo.vo.AmazonS3VO;
import com.takeshi.util.AmazonS3Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * UploadController
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Validated
@RestController
@RequestMapping("/upload")
@Tag(name = "上传文件")
public class UploadController extends AbstractBaseController {

    /**
     * 上传文件
     *
     * @param file file
     * @return ResponseData
     */
    @SystemSecurity(all = true)
    @Operation(summary = "上传文件", description = "对于后台返回的URL，可以通过【^https?:\\/\\/[^\\/]+\\/([^?]+)】正则来提取URL中的key")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping(value = "/file", consumes = "multipart/form-data")
    public ResponseData<AmazonS3VO> uploadFile(@RequestPart MultipartFile file) {
        if (file.isEmpty()) {
            return retData(TakeshiCode.FILE_IS_NULL);
        }
        return retData(AmazonS3Util.uploadFile(file));
    }

    /**
     * 上传多个文件
     *
     * @param files files
     * @return ResponseData
     */
    @SystemSecurity(all = true)
    @Operation(summary = "上传多个文件", description = "上传多个文件，最多同时上传9个文件，对于后台返回的URL，可以通过【^https?:\\/\\/[^\\/]+\\/([^?]+)】正则来提取URL中的key")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping(value = "/multi-file", consumes = "multipart/form-data")
    public ResponseData<List<AmazonS3VO>> uploadFile(@RequestPart @NotEmpty @Size(min = 1, max = 9) MultipartFile[] files) {
        return retData(AmazonS3Util.uploadFile(files));
    }

}
