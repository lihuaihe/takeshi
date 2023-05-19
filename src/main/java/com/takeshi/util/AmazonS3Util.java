package com.takeshi.util;

import cn.hutool.core.img.Img;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.constants.TakeshiDatePattern;
import com.takeshi.exception.TakeshiException;
import com.takeshi.pojo.vo.AmazonS3FileInfoVO;
import lombok.SneakyThrows;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AmazonS3Util
 * <pre>{@code
 * <dependency>
 * <groupId>com.amazonaws</groupId>
 * <artifactId>aws-java-sdk-s3</artifactId>
 * <version>1.12.125</version>
 * </dependency>
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public final class AmazonS3Util {

    private static final String ORIGINAL_NAME = "Original-Name";
    private static final String EXTENSION_NAME = "Extension-Name";
    private static final String CREATE_TIME = "Create-Time";

    private static String BUCKET_NAME;
    private static final MimeTypes MIME_REPOSITORY = TikaConfig.getDefaultConfig().getMimeRepository();

    /**
     * 获取到的密钥信息
     */
    public static volatile Object SECRET;
    /**
     * 获取到的密钥信息
     */
    public static volatile JsonNode JSON_NODE;

    /**
     * 用于管理到 Amazon S3 的传输的高级实用程序
     */
    private static volatile TransferManager transferManager;

    static {
        if (ObjUtil.isNull(transferManager)) {
            synchronized (AmazonS3Util.class) {
                if (ObjUtil.isNull(transferManager)) {
                    try {
                        // 获取密钥
                        AWSSecretsManagerCredentials awsSecrets = StaticConfig.takeshiProperties.getAwsSecrets();
                        BUCKET_NAME = awsSecrets.getBucketName();
                        AWSSecretsManager awsSecretsManager = AWSSecretsManagerClientBuilder.standard()
                                .withRegion(awsSecrets.getRegion())
                                .withCredentials(new AWSStaticCredentialsProvider(awsSecrets))
                                .build();
                        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
                        getSecretValueRequest.setSecretId(awsSecrets.getSecretId());
                        GetSecretValueResult getSecretValueResult = awsSecretsManager.getSecretValue(getSecretValueRequest);
                        String secret = StrUtil.isNotBlank(getSecretValueResult.getSecretString()) ? getSecretValueResult.getSecretString() : new String(java.util.Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
                        SECRET = GsonUtil.fromJson(secret, awsSecrets.getSecretClass());
                        JSON_NODE = new ObjectMapper().readValue(secret, JsonNode.class);
                        String accessKey = JSON_NODE.get(awsSecrets.getAccessKeyName()).asText();
                        String secretKey = JSON_NODE.get(awsSecrets.getSecretKeyName()).asText();
                        // S3
                        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                                .withRegion(awsSecrets.getRegion())
                                .build();
                        if (!amazonS3.doesBucketExistV2(BUCKET_NAME)) {
                            // 创建桶
                            amazonS3.createBucket(BUCKET_NAME);
                            // 设置生命周期规则，指示自生命周期启动后必须经过7天才能中止并删除不完整的分段上传
                            BucketLifecycleConfiguration.Rule lifecycleRule = new BucketLifecycleConfiguration.Rule()
                                    .withId("Automatically delete incomplete multipart upload after seven days")
                                    .withAbortIncompleteMultipartUpload(new AbortIncompleteMultipartUpload().withDaysAfterInitiation(7))
                                    .withStatus(BucketLifecycleConfiguration.ENABLED);
                            // 将生命周期规则设置到桶中
                            amazonS3.setBucketLifecycleConfiguration(BUCKET_NAME, new BucketLifecycleConfiguration().withRules(lifecycleRule));
                            // 设置跨域规则
                            CORSRule corsRule = new CORSRule().withAllowedMethods(Collections.singletonList(CORSRule.AllowedMethods.GET)).withAllowedOrigins(Collections.singletonList("*"));
                            // 将跨域规则设置到桶中
                            amazonS3.setBucketCrossOriginConfiguration(BUCKET_NAME, new BucketCrossOriginConfiguration().withRules(corsRule));
                            // 为指定的存储桶启用传输加速
                            amazonS3.setBucketAccelerateConfiguration(new SetBucketAccelerateConfigurationRequest(BUCKET_NAME, new BucketAccelerateConfiguration(BucketAccelerateStatus.Enabled)));
                        }
                        transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private AmazonS3Util() {
    }

    /**
     * 删除桶
     *
     * @param bucketName 桶名称
     */
    public static void deleteBucket(String bucketName) {
        transferManager.getAmazonS3Client().deleteBucket(bucketName);
    }

    /**
     * 上传经过压缩的图片，默认使用异步上传
     *
     * @param file    要上传的图片文件
     * @param quality 压缩比例，必须为0~1
     * @return S3文件访问URL
     */
    public static String addCompressImg(File file, float quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Img.from(file).setQuality(quality).write(byteArrayOutputStream);
        return addFile(FileUtil.readBytes(file), file.getName());
    }

    /**
     * 上传文件，自动根据不同文件类型创建不同目录存放文件
     *
     * @param file 要上传的文件
     * @return S3文件访问URL
     */
    public static String addFile(File file) {
        return addFile(file, false);
    }

    /**
     * 上传文件，自动根据不同文件类型创建不同目录存放文件
     *
     * @param multipartFile 要上传的文件
     * @return S3文件访问URL
     */
    @SneakyThrows
    public static String addFile(MultipartFile multipartFile) {
        return addFile(multipartFile.getBytes(), multipartFile.getOriginalFilename());
    }

    /**
     * 上传文件，自动根据不同文件类型创建不同目录存放文件
     *
     * @param file 要上传的文件
     * @param sync 是否等待传输完成再返回URL
     * @return S3文件访问URL
     */
    public static String addFile(File file, boolean sync) {
        return addFile(FileUtil.readBytes(file), file.getName(), sync);
    }

    /**
     * 上传文件，自动根据不同文件类型创建不同目录存放文件
     *
     * @param multipartFile 要上传的文件
     * @param sync          是否等待传输完成再返回URL
     * @return S3文件访问URL
     */
    @SneakyThrows
    public static String addFile(MultipartFile multipartFile, boolean sync) {
        return addFile(multipartFile.getBytes(), multipartFile.getOriginalFilename(), sync);
    }

    /**
     * 上传文件，通过文件流上传，默认使用异步上传
     *
     * @param data     要上传的文件字节数组
     * @param fileName 完整的文件名
     * @return S3文件访问URL
     */
    public static String addFile(byte[] data, String fileName) {
        return addFile(data, fileName, false);
    }

    /**
     * 上传文件，自动根据不同文件类型创建不同目录存放文件
     *
     * @param data     要上传的文件字节数组
     * @param fileName 完整的文件名
     * @param sync     是否等待传输完成再返回URL
     * @return S3文件访问URL
     */
    @SneakyThrows
    public static String addFile(byte[] data, String fileName, boolean sync) {
        try (TikaInputStream tikaInputStream = TikaInputStream.get(data)) {
            String mediaType = getMediaType(tikaInputStream, fileName);
            String extension = getMimeType(mediaType).getExtension();
            if (StrUtil.isBlank(extension)) {
                throw new TakeshiException(TakeshiCode.FILE_TYPE_ERROR);
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(tikaInputStream.getLength());
            metadata.setContentType(mediaType);
            // 添加用户自定义元数据
            String mainName = FileNameUtil.mainName(fileName);
            metadata.addUserMetadata(ORIGINAL_NAME, mainName);
            metadata.addUserMetadata(CREATE_TIME, String.valueOf(Instant.now().toEpochMilli()));
            metadata.addUserMetadata(EXTENSION_NAME, extension);
            String fileObjKeyName = getFileObjKeyName(mainName, extension);
            // withCannedAcl 设置对象可以公共读
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileObjKeyName, tikaInputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            // TransferManager 异步处理所有传输,所以这个调用立即返回
            Upload upload = transferManager.upload(putObjectRequest);
            if (sync) {
                // 等待此传输完成，这是一个阻塞调用；当前线程被挂起，直到这个传输完成
                upload.waitForCompletion();
            }
            return transferManager.getAmazonS3Client().getUrl(BUCKET_NAME, fileObjKeyName).toString();
        }
    }

    /**
     * 上传多个文件
     *
     * @param multipartFiles 要上传的多文件数组
     * @param sync           是否等待传输完成再返回URL
     * @return 多个S3文件访问URL
     */
    public static List<String> addFile(MultipartFile[] multipartFiles, boolean sync) {
        ArrayList<String> list = new ArrayList<>();
        for (MultipartFile item : multipartFiles) {
            list.add(addFile(item, sync));
        }
        return list;
    }

    /**
     * 上传多个文件，上传完成后删除文件
     *
     * @param files 要上传的多文件数组
     * @return 多个S3文件访问URL
     */
    public static List<String> addFileList(File[] files) {
        ArrayList<String> list = new ArrayList<>();
        for (File file : files) {
            list.add(addFile(file));
        }
        return list;
    }

    /**
     * 根据S3文件的访问URL删除文件
     *
     * @param url s3文件的访问URL
     */
    public static void deleteFile(String url) {
        transferManager.getAmazonS3Client().deleteObject(BUCKET_NAME, StrUtil.removePrefix(URLUtil.getPath(url), StrUtil.SLASH));
    }

    /**
     * 根据S3文件的访问URL下载文件
     *
     * @param url s3文件的访问URL
     * @return 输入流
     */
    public static S3ObjectInputStream download(String url) {
        return transferManager.getAmazonS3Client().download(new PresignedUrlDownloadRequest(URLUtil.url(url))).getS3Object().getObjectContent();
    }

    /**
     * 根据S3文件的访问URL下载到指定目录文件
     *
     * @param url     s3文件的访问URL
     * @param outFile 存储的目录文件
     */
    public static void download(String url, File outFile) {
        transferManager.download(new PresignedUrlDownloadRequest(URLUtil.url(url)), outFile);
    }

    /**
     * 获取指定 Amazon S3 对象的元数据，而不实际获取对象本身。
     * 这在仅获取对象元数据时很有用，并避免在获取对象数据时浪费带宽。
     * 对象元数据包含内容类型、内容配置等信息，以及可以与 Amazon S3 中的对象相关联的自定义用户元数据
     *
     * @param url s3文件的访问URL
     * @return ObjectMetadata
     */
    public static ObjectMetadata getObjectMetadata(String url) {
        // 文件访问URL（https://xxxx.amazonaws.com/png/2022/12/16/1603647146821951488/6906feb925d949928818655764be315a.png）中去掉（https://xxxx.amazonaws.com/）则是key
        String key = StrUtil.removePrefix(URLUtil.getPath(url), StrUtil.SLASH);
        return transferManager.getAmazonS3Client().getObjectMetadata(BUCKET_NAME, key);
    }

    /**
     * 获取指定 Amazon S3 对象的元数据，而不实际获取对象本身。
     * 这在仅获取对象元数据时很有用，并避免在获取对象数据时浪费带宽。
     * 对象元数据包含内容类型、内容配置等信息，以及可以与 Amazon S3 中的对象相关联的自定义用户元数据
     *
     * @param url s3文件的访问URL
     * @return 封装后的文件对象
     */
    public static AmazonS3FileInfoVO getAmazonS3FileInfo(String url) {
        try {
            if (StrUtil.isBlank(url)) {
                return null;
            }
            ObjectMetadata objectMetadata = getObjectMetadata(url);
            return new AmazonS3FileInfoVO(url, objectMetadata.getUserMetaDataOf(ORIGINAL_NAME), objectMetadata.getContentType(), objectMetadata.getUserMetaDataOf(EXTENSION_NAME), objectMetadata.getUserMetaDataOf(CREATE_TIME), objectMetadata.getContentLength());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文件存储的完整路径
     *
     * @param mainName  文件名
     * @param extension 扩展名
     * @return 完整路径
     */
    public static String getFileObjKeyName(String mainName, String extension) {
        String dateFormat = LocalDate.now().format(TakeshiDatePattern.SLASH_SEPARATOR_DATE_PATTERN_FORMATTER);
        return StrUtil.builder(StrUtil.removePrefix(extension, StrUtil.DOT), StrUtil.SLASH, dateFormat, StrUtil.SLASH, IdUtil.getSnowflakeNextIdStr(), StrUtil.DOT, mainName, extension).toString();
    }

    private static String getMediaType(InputStream stream, String fileName) throws IOException {
        Metadata metadata = new Metadata();
        metadata.add(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);
        return MIME_REPOSITORY.detect(stream, metadata).toString();
    }

    private static MimeType getMimeType(String mediaType) throws MimeTypeException {
        return MIME_REPOSITORY.forName(mediaType);
    }

}
