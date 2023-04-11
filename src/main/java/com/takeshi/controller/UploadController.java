package com.takeshi.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.constants.SysCode;
import com.takeshi.pojo.vo.ResponseDataVO;
import com.takeshi.util.AmazonS3Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * UploadController
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/10/13 09:27
 */
@Validated
@RestController
@RequestMapping("/common/upload")
@Tag(name = "上传文件")
public class UploadController extends BaseController {

    @SystemSecurity(all = true)
    @Operation(summary = "上传文件")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping("/file")
    public ResponseDataVO<Object> uploadFile(@RequestPart MultipartFile file, @Parameter(description = "是否同步上传") boolean sync) throws IOException, InterruptedException, MimeTypeException {
        if (file.isEmpty()) {
            return success(SysCode.FILE_IS_NULL);
        }
        return success(AmazonS3Util.addFile(file, sync));
    }

    @SystemSecurity(all = true)
    @Operation(summary = "上传多个文件", description = "上传多个文件，最多同时上传9个文件")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping("/multi-file")
    public ResponseDataVO<List<String>> uploadFile(@RequestPart @NotEmpty @Size(min = 1, max = 9) MultipartFile[] files,
                                                   @Parameter(description = "是否同步上传") boolean sync) throws InterruptedException, IOException, MimeTypeException {
        return success(AmazonS3Util.addFile(files, sync));
    }

}
