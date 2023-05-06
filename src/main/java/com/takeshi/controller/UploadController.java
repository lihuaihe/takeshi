package com.takeshi.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.util.AmazonS3Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
     * @param sync sync
     * @return ResponseData
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     * @throws MimeTypeException    MimeTypeException
     */
    @SystemSecurity(all = true)
    @Operation(summary = "上传文件")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping(value = "/file", consumes = "multipart/form-data")
    public ResponseData<Object> uploadFile(@RequestPart MultipartFile file,
                                           @Parameter(description = "是否同步上传", schema = @Schema(allowableValues = {"false", "true"})) boolean sync) throws IOException, InterruptedException, MimeTypeException {
        if (file.isEmpty()) {
            return retData(TakeshiCode.FILE_IS_NULL);
        }
        return retData(AmazonS3Util.addFile(file, sync));
    }

    /**
     * 上传多个文件
     *
     * @param files files
     * @param sync  sync
     * @return ResponseData
     * @throws InterruptedException InterruptedException
     * @throws IOException          IOException
     * @throws MimeTypeException    MimeTypeException
     */
    @SystemSecurity(all = true)
    @Operation(summary = "上传多个文件", description = "上传多个文件，最多同时上传9个文件")
    @ApiOperationSupport(author = NANASE_TAKESHI)
    @PostMapping(value = "/multi-file", consumes = "multipart/form-data")
    public ResponseData<List<String>> uploadFile(@RequestPart @NotEmpty @Size(min = 1, max = 9) MultipartFile[] files,
                                                 @Parameter(description = "是否同步上传", schema = @Schema(allowableValues = {"false", "true"})) boolean sync) throws InterruptedException, IOException, MimeTypeException {
        return retData(AmazonS3Util.addFile(files, sync));
    }

}
