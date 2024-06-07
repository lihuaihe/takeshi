package com.takeshi.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.ownership.ObjectOwnership;
import com.amazonaws.services.s3.model.ownership.OwnershipControls;
import com.amazonaws.services.s3.model.ownership.OwnershipControlsRule;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.fasterxml.jackson.databind.JsonNode;
import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.constants.TakeshiDatePattern;
import com.takeshi.exception.TakeshiException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * AmazonS3Util
 * <pre>{@code
 * implementation 'com.amazonaws:aws-java-sdk-s3:+'
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class AmazonS3Util {

    /**
     * 保存到metadata中的数据的key
     */
    interface MetadataConstants {

        // 保存到S3的时间
        String CREATE_TIME = "nt-create-time";

        // 缩略图URL
        String THUMBNAIL = "nt-thumbnail";

        // 视频时长，单位（毫秒）
        String DURATION = "nt-duration";

    }

    /**
     * 文件URL中的参数名
     */
    interface UrlParamsConstants {

        // 保存到S3的时间
        String CREATE_TIME = "x-nt-create-time";

        // 文件大小，单位（字节）
        String CONTENT_LENGTH = "x-nt-content-length";

        // 内容类型
        String CONTENT_TYPE = "x-nt-content-type";

        // 缩略图URL
        String THUMBNAIL = "x-nt-thumbnail";

        // 视频时长，单位（毫秒）
        String DURATION = "x-nt-duration";

    }

    // 存储桶名称
    private static volatile String BUCKET_NAME;

    // 文件ACL
    private static volatile CannedAccessControlList FILE_ACL;

    /**
     * 用于管理到 Amazon S3 的传输的高级实用程序
     */
    private static volatile TransferManager transferManager;

    /**
     * 文件对象的访问控制列表 (ACL)
     */
    private CannedAccessControlList fileAcl;

    /**
     * 文件上传后的URL是否附带文件信息
     */
    private boolean fileInfoUrl;

    /**
     * 是否生成缩略图
     */
    private boolean thumbnail;

    /**
     * 是否获取视频时长
     */
    private boolean duration;

    /**
     * 图片质量，数字为0~1（不包括0和1）表示质量压缩比，除此数字外设置表示不压缩
     */
    private Float quality;

    /**
     * 用户自定义元数据
     */
    private Map<String, String> userMetadata;

    /**
     * 构造函数
     */
    private AmazonS3Util() {
    }

    /**
     * 获取TransferManager
     *
     * @return TransferManager
     */
    public static TransferManager getTransferManager() {
        if (ObjUtil.isNull(transferManager)) {
            synchronized (AmazonS3Util.class) {
                if (ObjUtil.isNull(transferManager)) {
                    // 获取密钥
                    AWSSecretsManagerCredentials awsSecrets = SpringUtil.getBean(AWSSecretsManagerCredentials.class);
                    JsonNode jsonNode = AwsSecretsManagerUtil.getSecret();
                    if (StrUtil.isBlank(awsSecrets.getS3AccessKeySecrets()) || !jsonNode.hasNonNull(awsSecrets.getS3AccessKeySecrets())) {
                        throw new IllegalArgumentException("S3 Access key cannot be null.");
                    }
                    if (StrUtil.isBlank(awsSecrets.getS3SecretKeySecrets()) || !jsonNode.hasNonNull(awsSecrets.getS3SecretKeySecrets())) {
                        throw new IllegalArgumentException("S3 Secret key cannot be null.");
                    }
                    String accessKey = jsonNode.get(awsSecrets.getS3AccessKeySecrets()).asText();
                    String secretKey = jsonNode.get(awsSecrets.getS3SecretKeySecrets()).asText();
                    BUCKET_NAME = awsSecrets.getBucketName();
                    FILE_ACL = Optional.ofNullable(awsSecrets.getFileAcl()).map(fileAcl -> Enum.valueOf(CannedAccessControlList.class, fileAcl.name())).orElse(null);
                    // S3
                    AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                                                             .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                                                             .withRegion(awsSecrets.getRegion())
                                                             // 存储桶启用传输加速
                                                             .withAccelerateModeEnabled(awsSecrets.isBucketAccelerate())
                                                             .build();
                    if (!amazonS3.doesBucketExistV2(BUCKET_NAME)) {
                        // 创建桶
                        amazonS3.createBucket(BUCKET_NAME);
                        if (!awsSecrets.isBlockPublicAccess()) {
                            // 设置是否阻止所有公开访问
                            PublicAccessBlockConfiguration publicAccessBlockConfiguration = new PublicAccessBlockConfiguration()
                                    .withBlockPublicAcls(false)
                                    .withIgnorePublicAcls(false)
                                    .withBlockPublicPolicy(false)
                                    .withRestrictPublicBuckets(false);
                            amazonS3.setPublicAccessBlock(new SetPublicAccessBlockRequest().withBucketName(BUCKET_NAME).withPublicAccessBlockConfiguration(publicAccessBlockConfiguration));
                            if (awsSecrets.isBucketAcl()) {
                                // 启用存储桶的ACL
                                amazonS3.setBucketOwnershipControls(BUCKET_NAME, new OwnershipControls().withRules(List.of(new OwnershipControlsRule().withOwnership(ObjectOwnership.BucketOwnerPreferred))));
                            }
                            if (awsSecrets.isBucketPolicyPublicRead()) {
                                amazonS3.setBucketPolicy(BUCKET_NAME, "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"PublicReadGetObject\",\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":\"s3:GetObject\",\"Resource\":\"arn:aws:s3:::" + BUCKET_NAME + "/*\"}]}");
                            }
                        }
                        // 设置生命周期规则，指示自生命周期启动后必须经过7天才能中止并删除不完整的分段上传
                        BucketLifecycleConfiguration.Rule lifecycleRule = new BucketLifecycleConfiguration.Rule()
                                .withId("Automatically delete incomplete multipart upload after seven days")
                                .withAbortIncompleteMultipartUpload(new AbortIncompleteMultipartUpload().withDaysAfterInitiation(7))
                                .withStatus(BucketLifecycleConfiguration.ENABLED);
                        // 将生命周期规则设置到桶中
                        amazonS3.setBucketLifecycleConfiguration(BUCKET_NAME, new BucketLifecycleConfiguration().withRules(lifecycleRule));
                        // 设置跨域规则
                        CORSRule corsRule =
                                new CORSRule().withAllowedHeaders(List.of("*"))
                                              .withAllowedMethods(List.of(CORSRule.AllowedMethods.GET, CORSRule.AllowedMethods.HEAD))
                                              .withAllowedOrigins(List.of("*"))
                                              // 配置ExposedHeader为了兼容浏览器跨域，尤其是Google浏览器
                                              .withExposedHeaders(List.of("ETag", "x-amz-meta-custom-header"))
                                              .withMaxAgeSeconds(3000);
                        // 将跨域规则设置到桶中
                        amazonS3.setBucketCrossOriginConfiguration(BUCKET_NAME, new BucketCrossOriginConfiguration().withRules(corsRule));
                        if (awsSecrets.isBucketAccelerate()) {
                            // 为指定的存储桶启用传输加速，非必要可以不启用这个，启用了会浪费带宽，但是如果是非同个地区的访问启用了则会提升访问速度
                            amazonS3.setBucketAccelerateConfiguration(new SetBucketAccelerateConfigurationRequest(BUCKET_NAME, new BucketAccelerateConfiguration(BucketAccelerateStatus.Enabled)));
                        }
                    }
                    transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
                    log.info("AmazonS3Util.static --> TransferManager Initialization successful");
                }
            }
        }
        return transferManager;
    }

    /**
     * 获取AmazonS3Util对象
     *
     * @return AmazonS3Util
     */
    public static AmazonS3Util of() {
        return new AmazonS3Util();
    }

    /**
     * 设置文件对象的访问控制列表 (ACL)
     *
     * @param fileAcl ACL
     * @return AmazonS3Util
     */
    public AmazonS3Util withCannedAcl(CannedAccessControlList fileAcl) {
        this.fileAcl = fileAcl;
        return this;
    }

    /**
     * 文件上传后的URL是否附带文件信息
     *
     * @return AmazonS3Util
     */
    public AmazonS3Util withFileInfoUrl() {
        this.fileInfoUrl = true;
        return this;
    }

    /**
     * 文件上传后的URL是否附带文件信息
     *
     * @param fileInfoUrl 是否附带文件信息
     * @return AmazonS3Util
     */
    public AmazonS3Util withFileInfoUrl(boolean fileInfoUrl) {
        this.fileInfoUrl = fileInfoUrl;
        return this;
    }

    /**
     * 是否生成缩略图
     *
     * @return AmazonS3Util
     */
    public AmazonS3Util withThumbnail() {
        this.thumbnail = true;
        return this;
    }

    /**
     * 是否生成缩略图
     *
     * @param thumbnail 是否生成缩略图
     * @return AmazonS3Util
     */
    public AmazonS3Util withThumbnail(boolean thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    /**
     * 是否获取视频时长
     *
     * @return AmazonS3Util
     */
    public AmazonS3Util withDuration() {
        this.duration = true;
        return this;
    }

    /**
     * 是否获取视频时长
     *
     * @param duration 是否获取视频时长
     * @return AmazonS3Util
     */
    public AmazonS3Util withDuration(boolean duration) {
        this.duration = duration;
        return this;
    }

    /**
     * 设置图片质量压缩比
     *
     * @param quality 图片质量压缩比
     * @return AmazonS3Util
     */
    public AmazonS3Util withQuality(float quality) {
        this.quality = quality;
        return this;
    }

    /**
     * 设置用户自定义元数据
     *
     * @param userMetadata 元数据
     * @return AmazonS3Util
     */
    public AmazonS3Util withUserMetadata(Map<String, String> userMetadata) {
        this.userMetadata = userMetadata;
        return this;
    }

    /**
     * 添加用户自定义元数据
     *
     * @param key   key
     * @param value value
     * @return AmazonS3Util
     */
    public AmazonS3Util putUserMetadata(String key, String value) {
        this.userMetadata = Optional.ofNullable(this.userMetadata).orElse(new HashMap<>());
        this.userMetadata.put(key, value);
        return this;
    }

    /**
     * 上传文件
     *
     * @param files 文件列表
     * @return URL列表
     */
    @SneakyThrows
    public List<URL> upload(File... files) {
        List<URL> list = new ArrayList<>();
        for (File file : files) {
            list.add(this.upload(TikaInputStream.get(file.toPath()), file.getName()));
        }
        return list;
    }

    /**
     * 上传文件
     *
     * @param multipartFiles 文件列表
     * @return URL列表
     */
    @SneakyThrows
    public List<URL> upload(MultipartFile... multipartFiles) {
        List<URL> list = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            list.add(this.upload(TikaInputStream.get(multipartFile.getBytes()), multipartFile.getOriginalFilename()));
        }
        return list;
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return URL
     */
    @SneakyThrows
    public URL upload(File file) {
        return this.upload(TikaInputStream.get(file.toPath()), file.getName());
    }

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @return URL
     */
    @SneakyThrows
    public URL upload(MultipartFile multipartFile) {
        return this.upload(TikaInputStream.get(multipartFile.getBytes()), multipartFile.getOriginalFilename());
    }

    /**
     * 上传文件
     *
     * @param bytes    文件的字节数组
     * @param fileName 文件名
     * @return URL
     */
    @SneakyThrows
    public URL upload(byte[] bytes, String fileName) {
        return this.upload(TikaInputStream.get(bytes), fileName);
    }

    /**
     * 上传文件
     *
     * @param inputStream 文件流
     * @param fileName    文件名
     * @return URL
     */
    @SneakyThrows
    public URL upload(InputStream inputStream, String fileName) {
        return this.upload(TikaInputStream.get(inputStream), fileName);
    }

    /**
     * 上传文件
     *
     * @param tikaInputStream 文件流
     * @param fileName        文件名
     * @return URL
     */
    @SneakyThrows
    public URL upload(TikaInputStream tikaInputStream, String fileName) {
        try {
            TransferManager transferManager = getTransferManager();
            String mediaType = TakeshiUtil.getTika().detect(tikaInputStream, fileName);
            MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(mediaType);
            String extension = mimeType.getExtension();
            if (StrUtil.isBlank(extension)) {
                throw new TakeshiException(TakeshiCode.FILE_TYPE_ERROR);
            }
            Instant instant = Instant.now();
            if (ObjUtil.isNull(this.fileAcl)) {
                this.fileAcl = FILE_ACL;
            }
            ObjectMetadata metadata = new ObjectMetadata();
            if (CollUtil.isNotEmpty(this.userMetadata)) {
                metadata.setUserMetadata(this.userMetadata);
            }
            metadata.setContentLength(tikaInputStream.getLength());
            metadata.setContentType(mediaType);
            // 添加用户自定义元数据
            String mainName = FileNameUtil.mainName(fileName);
            metadata.addUserMetadata(MetadataConstants.CREATE_TIME, TakeshiConstants.INSTANT_FORMATTER.format(instant));

            if (mediaType.startsWith("video/") || "image/gif".equals(mediaType)) {
                if (this.thumbnail || this.duration) {
                    FrameConverterUtil.saveThumbnail(tikaInputStream, BUCKET_NAME, metadata, mediaType, this.thumbnail, this.duration);
                }
            } else if (mediaType.startsWith("image/")) {
                if (ObjUtil.isNotNull(this.quality)) {
                    // 压缩图片
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    Img.from(tikaInputStream).setQuality(this.quality).write(byteArrayOutputStream);
                    tikaInputStream.close();
                    tikaInputStream = TikaInputStream.get(byteArrayOutputStream.toByteArray());
                }
                if (this.thumbnail) {
                    // 保存图片缩略图
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    Img.from(tikaInputStream).scale(200, -1).write(byteArrayOutputStream);
                    try (TikaInputStream thumbnailTikaInputStream = TikaInputStream.get(byteArrayOutputStream.toByteArray())) {
                        ObjectMetadata thumbnailMetadata = new ObjectMetadata();
                        thumbnailMetadata.setContentLength(thumbnailTikaInputStream.getLength());
                        thumbnailMetadata.setContentType("image/jpg");
                        // 添加用户自定义元数据
                        thumbnailMetadata.addUserMetadata(MetadataConstants.CREATE_TIME, Instant.now().toString());
                        String thumbnailObjKey = getThumbnailObjKey(instant);
                        // 添加缩略图的S3 key
                        metadata.addUserMetadata(MetadataConstants.THUMBNAIL, thumbnailObjKey);
                        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, thumbnailObjKey, thumbnailTikaInputStream, thumbnailMetadata);
                        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                        transferManager.upload(putObjectRequest);
                    }
                }
            }
            String fileObjKey = getFileObjKey(instant, mainName, extension);
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileObjKey, tikaInputStream, metadata);
            putObjectRequest.setCannedAcl(this.fileAcl);
            // TransferManager 异步处理所有传输,所以这个调用立即返回
            Upload upload = transferManager.upload(putObjectRequest);
            // 等待此传输完成，这是一个阻塞调用；当前线程被挂起，直到这个传输完成
            upload.waitForCompletion();
            URL url = transferManager.getAmazonS3Client().getUrl(BUCKET_NAME, fileObjKey);
            // if (transferManager.getAmazonS3Client().getBucketAccelerateConfiguration(BUCKET_NAME).isAccelerateEnabled()) {
            //     // 如果启用了传输加速，需要将URL中的域名替换为对应的加速域名
            //     url = new URL(url.getProtocol(), BUCKET_NAME + ".s3-accelerate.amazonaws.com", url.getPort(), url.getFile());
            // }
            if (this.fileInfoUrl) {
                UrlBuilder urlBuilder = UrlBuilder.of(url.toString(), null);
                urlBuilder.addQuery(UrlParamsConstants.CREATE_TIME, metadata.getUserMetaDataOf(MetadataConstants.CREATE_TIME));
                urlBuilder.addQuery(UrlParamsConstants.CONTENT_LENGTH, metadata.getContentLength());
                urlBuilder.addQuery(UrlParamsConstants.CONTENT_TYPE, metadata.getContentType());
                String videoDuration = metadata.getUserMetaDataOf(MetadataConstants.DURATION);
                if (StrUtil.isNotBlank(videoDuration)) {
                    urlBuilder.addQuery(UrlParamsConstants.DURATION, videoDuration);
                }
                String thumbnail = metadata.getUserMetaDataOf(MetadataConstants.THUMBNAIL);
                if (StrUtil.isNotBlank(thumbnail)) {
                    urlBuilder.addQuery(UrlParamsConstants.THUMBNAIL, thumbnail);
                }
                return urlBuilder.toURL();
            }
            return url;
        } finally {
            if (ObjUtil.isNotNull(tikaInputStream)) {
                tikaInputStream.close();
            }
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param key S3对象的键
     * @return URL
     */
    public static URL getUrl(String key) {
        return getTransferManager().getAmazonS3Client().getUrl(BUCKET_NAME, key);
    }

    /**
     * 返回一个客户端用来上传文件的预签名 URL，客户端使用 PUT 请求该URL来上传一个二进制文件，默认有效期5分钟
     *
     * @param fileName 文件名
     * @return URL
     */
    @SneakyThrows
    public static URL getPutPresignedUrl(String fileName) {
        String extName = FileNameUtil.extName(fileName);
        Assert.notBlank(extName, "The file name is not standardized and has no suffix.");
        String key = getFileObjKey(Instant.now(), FileNameUtil.mainName(fileName), StrUtil.DOT + extName);
        return getTransferManager().getAmazonS3Client().generatePresignedUrl(BUCKET_NAME, key, Date.from(Instant.now().plus(Duration.ofMinutes(5))), HttpMethod.PUT);
    }

    /**
     * 返回一个客户端用来上传文件的预签名 URL，客户端使用 PUT 请求该URL来上传一个二进制文件
     *
     * @param fileName 文件名
     * @param duration 预签名 URL 将过期的时间
     * @return URL
     */
    @SneakyThrows
    public static URL getPutPresignedUrl(String fileName, Duration duration) {
        String extName = FileNameUtil.extName(fileName);
        Assert.notBlank(extName, "The file name is not standardized and has no suffix.");
        String key = getFileObjKey(Instant.now(), FileNameUtil.mainName(fileName), StrUtil.DOT + extName);
        return getTransferManager().getAmazonS3Client().generatePresignedUrl(BUCKET_NAME, key, Date.from(Instant.now().plus(duration)), HttpMethod.PUT);
    }

    /**
     * 返回用于访问 Amazon S3 资源的预签名 URL
     *
     * @param url S3文件的URL
     * @return URL
     */
    @SneakyThrows
    public static URL getPresignedUrl(URL url) {
        return getPresignedUrl(new AmazonS3URI(url.toURI()).getKey());
    }

    /**
     * 返回用于访问 Amazon S3 资源的预签名 URL，默认有效期7天
     *
     * @param key S3对象的键
     * @return URL
     */
    public static URL getPresignedUrl(String key) {
        return getPresignedUrl(key, Duration.ofDays(7), false);
    }

    /**
     * 返回用于访问 Amazon S3 资源的预签名 URL
     *
     * @param key         S3对象的键
     * @param duration    预签名 URL 将过期的时间
     * @param fileInfoUrl 预签名的URL是否附带文件信息
     * @return URL
     */
    public static URL getPresignedUrl(String key, Duration duration, boolean fileInfoUrl) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        Date date = Date.from(Instant.now().plus(duration));
        if (fileInfoUrl) {
            ObjectMetadata objectMetadata = getObjectMetadata(key);
            if (Objects.isNull(objectMetadata)) {
                return null;
            }
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(BUCKET_NAME, key).withExpiration(date);
            generatePresignedUrlRequest.addRequestParameter(UrlParamsConstants.CREATE_TIME, objectMetadata.getUserMetaDataOf(MetadataConstants.CREATE_TIME));
            generatePresignedUrlRequest.addRequestParameter(UrlParamsConstants.CONTENT_LENGTH, String.valueOf(objectMetadata.getContentLength()));
            generatePresignedUrlRequest.addRequestParameter(UrlParamsConstants.CONTENT_TYPE, objectMetadata.getContentType());
            String videoDuration = objectMetadata.getUserMetaDataOf(MetadataConstants.DURATION);
            if (StrUtil.isNotBlank(videoDuration)) {
                generatePresignedUrlRequest.addRequestParameter(UrlParamsConstants.DURATION, videoDuration);
            }
            String thumbnailKey = objectMetadata.getUserMetaDataOf(MetadataConstants.THUMBNAIL);
            if (StrUtil.isNotBlank(thumbnailKey)) {
                // 如果有缩略图
                URL thumbnailUrl = getTransferManager().getAmazonS3Client().generatePresignedUrl(BUCKET_NAME, thumbnailKey, date);
                generatePresignedUrlRequest.addRequestParameter(UrlParamsConstants.THUMBNAIL, thumbnailUrl.toString());
            }
            return getTransferManager().getAmazonS3Client().generatePresignedUrl(generatePresignedUrlRequest);
        }
        return getTransferManager().getAmazonS3Client().generatePresignedUrl(BUCKET_NAME, key, date);
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
     * 根据S3文件的key删除文件
     *
     * @param key S3对象的键
     */
    public static void deleteFile(String key) {
        transferManager.getAmazonS3Client().deleteObject(BUCKET_NAME, key);
    }

    /**
     * 根据S3文件的key下载到指定目录文件
     *
     * @param key     S3对象的键
     * @param outFile 存储的目录文件
     */
    @SneakyThrows
    public static void download(String key, File outFile) {
        transferManager.download(BUCKET_NAME, key, outFile).waitForCompletion();
    }

    /**
     * 根据key判断文件对象是否存在
     *
     * @param key S3对象的键
     * @return boolean
     */
    public static boolean doesObjectExist(String key) {
        return transferManager.getAmazonS3Client().doesObjectExist(BUCKET_NAME, key);
    }

    /**
     * 从 Amazon S3 检索对象
     *
     * @param key S3对象的键
     * @return S3Object
     */
    public static S3Object getObject(String key) {
        return transferManager.getAmazonS3Client().getObject(BUCKET_NAME, key);
    }

    /**
     * 获取指定 Amazon S3 对象的元数据，而不实际获取对象本身。
     * 这在仅获取对象元数据时很有用，并避免在获取对象数据时浪费带宽。
     * 对象元数据包含内容类型、内容配置等信息，以及可以与 Amazon S3 中的对象相关联的自定义用户元数据
     *
     * @param key S3对象的键
     * @return ObjectMetadata
     */
    public static ObjectMetadata getObjectMetadata(String key) {
        try {
            return transferManager.getAmazonS3Client().getObjectMetadata(BUCKET_NAME, key);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                return null;
            }
            throw e;
        }
    }

    /**
     * 关闭 TransferManager 实例
     */
    public static void shutdown() {
        if (ObjUtil.isNotNull(transferManager)) {
            log.info("Close the TransferManager instance...");
            transferManager.shutdownNow();
        }
    }

    /**
     * 获取文件存储的完整路径（Key）
     *
     * @param instant   当前时间
     * @param mainName  主文件名
     * @param extension 带.的文件扩展名（例如：.png）
     * @return 完整路径
     * @throws IOException IOException
     */
    public static String getFileObjKey(Instant instant, String mainName, String extension) throws IOException {
        String dateFormat = TakeshiDatePattern.SLASH_SEPARATOR_DATE_PATTERN_FORMATTER.format(instant);
        return StrUtil.builder(StrUtil.removePrefix(extension, StrUtil.DOT), StrUtil.SLASH, dateFormat, StrUtil.SLASH, IdUtil.objectId(), StrUtil.SLASH, mainName, extension).toString();
    }

    /**
     * 获取缩略图文件存储的完整路径（Key）
     *
     * @param instant 当前时间
     * @return 完整路径
     */
    public static String getThumbnailObjKey(Instant instant) {
        String dateFormat = TakeshiDatePattern.SLASH_SEPARATOR_DATE_PATTERN_FORMATTER.format(instant);
        return StrUtil.builder("thumbnail", StrUtil.SLASH, dateFormat, StrUtil.SLASH, IdUtil.objectId(), StrUtil.DOT, ImgUtil.IMAGE_TYPE_JPG).toString();
    }

    /**
     * 获取GIF图像的时长（毫秒），不适用所有GIF图
     *
     * @param file 文件
     * @return int
     */
    @SneakyThrows
    public static int getGifDuration(File file) {
        // 获取GIF图像的时长（毫秒）
        int duration = 0;
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(imageInputStream);
        for (int i = 0; i < reader.getNumImages(true); i++) {
            IIOMetadata metadata = reader.getImageMetadata(i);
            String metaFormat = metadata.getNativeMetadataFormatName();
            Node tree = metadata.getAsTree(metaFormat);
            if (tree instanceof IIOMetadataNode rootNode) {
                NodeList delayNodes = rootNode.getElementsByTagName("GraphicControlExtension");
                if (delayNodes.getLength() > 0) {
                    IIOMetadataNode graphicControlNode = (IIOMetadataNode) delayNodes.item(0);
                    int delayTime = Integer.parseInt(graphicControlNode.getAttribute("delayTime"));
                    duration += delayTime * 10;
                } else {
                    System.err.println("No GraphicControlExtension node found in frame " + i);
                }
            } else {
                System.err.println("Unsupported metadata format: " + metaFormat);
                break;
            }
        }
        return duration;
    }

}
