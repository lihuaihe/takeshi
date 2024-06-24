package com.takeshi.util;

import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.exception.TakeshiException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileDownload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

/**
 * AmazonS3Util
 * <pre>{@code
 * implementation 'software.amazon.awssdk:s3-transfer-manager:+'
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class AmazonS3Util {

    /**
     * 存储桶名称
     */
    public static final String BUCKET_NAME = SpringUtil.getBean(AWSSecretsManagerCredentials.class).getBucketName();

    /**
     * 文件ACL
     */
    public static final ObjectCannedACL FILE_ACL = ObjectCannedACL.fromValue(SpringUtil.getBean(AWSSecretsManagerCredentials.class).getFileAcl().getValue());

    /**
     * 用于异步访问 Amazon S3 的服务客户端
     */
    public static final S3AsyncClient s3AsyncClient = SpringUtil.getBean(S3AsyncClient.class);

    /**
     * 用于管理到 Amazon S3 的传输的高级实用程序
     */
    public static final S3TransferManager s3TransferManager = SpringUtil.getBean(S3TransferManager.class);

    /**
     * 用于管理 Amazon S3 对象签名
     */
    public static final S3Presigner s3Presigner = SpringUtil.getBean(S3Presigner.class);

    /**
     * 预签名URL的有效期
     */
    private static final Duration PRESIGNED_URL_DURATION = Duration.ofDays(7);

    /**
     * 文件对象的访问控制列表 (ACL)
     */
    private ObjectCannedACL fileAcl = ObjectCannedACL.PRIVATE;

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
    private Map<String, String> userMetadata = new HashMap<>();

    /**
     * 构造函数
     */
    private AmazonS3Util() {
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
    public AmazonS3Util withCannedAcl(ObjectCannedACL fileAcl) {
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
        this.userMetadata.put(key, value);
        return this;
    }

    /**
     * 添加用户自定义元数据
     *
     * @param map map
     * @return AmazonS3Util
     */
    public AmazonS3Util putUserMetadata(Map<String, String> map) {
        this.userMetadata.putAll(map);
        return this;
    }

    /**
     * 上传文件
     *
     * @param files 文件列表
     * @return URL列表
     */
    public List<URL> upload(File... files) {
        return Arrays.stream(files).map(this::upload).toList();
    }

    /**
     * 上传文件
     *
     * @param fileList 文件列表
     * @return URL列表
     */
    public List<URL> uploadFileList(List<File> fileList) {
        return fileList.stream().map(this::upload).toList();
    }

    /**
     * 上传文件
     *
     * @param multipartFiles 文件列表
     * @return URL列表
     */
    public List<URL> upload(MultipartFile... multipartFiles) {
        return Arrays.stream(multipartFiles).map(this::upload).toList();
    }

    /**
     * 上传文件
     *
     * @param multipartFileList 文件列表
     * @return URL列表
     */
    public List<URL> uploadMultipartFileList(List<MultipartFile> multipartFileList) {
        return multipartFileList.stream().map(this::upload).toList();
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return URL
     */
    @SneakyThrows
    public URL upload(File file) {
        return this.upload(FileUtil.readBytes(file), file.getName());
    }

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @return URL
     */
    @SneakyThrows
    public URL upload(MultipartFile multipartFile) {
        return this.upload(multipartFile.getBytes(), multipartFile.getOriginalFilename());
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
        return this.upload(IoUtil.readBytes(inputStream), fileName);
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
        TikaInputStream tikaInputStream = TikaInputStream.get(bytes);
        try (tikaInputStream) {
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
            // 添加用户自定义元数据
            // String mainName = FileNameUtil.mainName(fileName);
            String fileObjKey = getFileObjKey(fileName);
            if (mediaType.startsWith("video/") || "image/gif".equals(mediaType)) {
                if (this.thumbnail || this.duration) {
                    Map<String, String> map = FrameConverterUtil.saveThumbnail(tikaInputStream, fileObjKey, this.fileAcl, mediaType, this.thumbnail, this.duration);
                    this.putUserMetadata(map);
                }
            } else if (mediaType.startsWith("image/")) {
                if (this.thumbnail) {
                    // 保存图片缩略图
                    byte[] thumbnailBytes = ImgUtil.toBytes(ImgUtil.scale(ImgUtil.toImage(bytes), 200, -1), ImgUtil.IMAGE_TYPE_JPG);
                    String thumbnailObjKey = getThumbnailObjKey(fileObjKey);
                    // 添加缩略图的S3 key
                    this.putUserMetadata(MetadataConstants.THUMBNAIL, thumbnailObjKey);
                    s3TransferManager.upload(
                            UploadRequest.builder()
                                         .requestBody(
                                                 AsyncRequestBody.fromBytes(thumbnailBytes)
                                         )
                                         .putObjectRequest(
                                                 PutObjectRequest.builder()
                                                                 .bucket(BUCKET_NAME)
                                                                 .key(thumbnailObjKey)
                                                                 .contentType("image/jpg")
                                                                 .contentLength((long) thumbnailBytes.length)
                                                                 .acl(this.fileAcl)
                                                                 .build()
                                         )
                                         .build()
                    );
                }
                if (ObjUtil.isNotNull(this.quality)) {
                    // 压缩图片
                    bytes = ImgUtil.toBytes(Img.from(ImgUtil.toImage(bytes)).setQuality(this.quality).getImg(), ImgUtil.IMAGE_TYPE_JPG);
                }
            }
            long contentLength = bytes.length;
            // TransferManager 异步处理所有传输,所以这个调用立即返回
            return s3TransferManager.upload(
                                            UploadRequest.builder()
                                                         .requestBody(AsyncRequestBody.fromBytes(bytes))
                                                         .putObjectRequest(
                                                                 PutObjectRequest.builder()
                                                                                 .bucket(BUCKET_NAME)
                                                                                 .key(fileObjKey)
                                                                                 .metadata(this.userMetadata)
                                                                                 .contentType(mediaType)
                                                                                 .contentLength(contentLength)
                                                                                 .acl(this.fileAcl)
                                                                                 .build()
                                                         )
                                                         .build()
                                    )
                                    .completionFuture()
                                    .thenApply(completedUpload -> {
                                                   URL url = getUrl(fileObjKey);
                                                   if (this.fileInfoUrl) {
                                                       // 此处编码格式传null，目的是为了避免对原始URL反编码，导致访问的URL不正确
                                                       UrlBuilder urlBuilder = UrlBuilder.of(url.toString(), null);
                                                       urlBuilder.addQuery(UrlParamsConstants.CONTENT_LENGTH, contentLength);
                                                       urlBuilder.addQuery(UrlParamsConstants.CONTENT_TYPE, mediaType);
                                                       String videoDuration = userMetadata.get(MetadataConstants.DURATION);
                                                       if (StrUtil.isNotBlank(videoDuration)) {
                                                           urlBuilder.addQuery(UrlParamsConstants.DURATION, videoDuration);
                                                       }
                                                       String thumbnail = userMetadata.get(MetadataConstants.THUMBNAIL);
                                                       if (StrUtil.isNotBlank(thumbnail)) {
                                                           urlBuilder.addQuery(UrlParamsConstants.THUMBNAIL, thumbnail);
                                                       }
                                                       return urlBuilder.toURL();
                                                   }
                                                   return url;
                                               }
                                    )
                                    .join();

        }
    }

    /**
     * 获取文件访问URL
     *
     * @param key S3对象的键
     * @return URL
     */
    public static URL getUrl(String key) {
        return s3AsyncClient.utilities().getUrl(builder -> builder.bucket(BUCKET_NAME).key(key).build());
    }

    /**
     * 获取一个客户端用来上传文件的预签名 URL列表，客户端使用 PUT 请求该URL来上传一个二进制文件，默认有效期5分钟
     * <br/>
     * 使用随机的key保存文件，不包含原始文件名
     *
     * @param size 需要的URL数量
     * @return URL列表
     */
    public static List<URL> getPutPresignedUrl(int size) {
        return IntStream.of(size).boxed().map(i -> getPutPresignedUrl(StrUtil.EMPTY).url()).toList();
    }

    /**
     * 获取一个客户端用来上传文件的预签名 URL列表，客户端使用 PUT 请求该URL来上传一个二进制文件，默认有效期5分钟
     * <br/>
     * 使用随机的key保存文件，不包含原始文件名
     *
     * @param size     需要的URL数量
     * @param duration 预签名 URL 将过期的时间
     * @return URL列表
     */
    public static List<URL> getPutPresignedUrl(int size, Duration duration) {
        return IntStream.of(size).boxed().map(i -> getPutPresignedUrl(StrUtil.EMPTY, duration).url()).toList();
    }

    /**
     * 获取一个客户端用来上传文件的预签名 URL，客户端使用 PUT 请求该URL来上传一个二进制文件，默认有效期5分钟
     * <br/>
     * 使用随机的key保存文件，不包含原始文件名
     *
     * @return PresignedPutObjectRequest
     */
    @SneakyThrows
    public static PresignedPutObjectRequest getPutPresignedUrl() {
        return getPutPresignedUrl(StrUtil.EMPTY);
    }

    /**
     * 获取一个客户端用来上传文件的预签名 URL，客户端使用 PUT 请求该URL来上传一个二进制文件，默认有效期60秒
     *
     * @param fileName 文件名
     * @return PresignedPutObjectRequest
     */
    @SneakyThrows
    public static PresignedPutObjectRequest getPutPresignedUrl(String fileName) {
        return getPutPresignedUrl(fileName, Duration.ofSeconds(60));
    }

    /**
     * 获取一个客户端用来上传文件的预签名 URL，客户端使用 PUT 请求该URL来上传一个二进制文件，默认有效期5分钟
     *
     * @param fileName 文件名
     * @param duration 预签名 URL 将过期的时间
     * @return PresignedPutObjectRequest
     */
    @SneakyThrows
    public static PresignedPutObjectRequest getPutPresignedUrl(String fileName, Duration duration) {
        return getPutPresignedUrl(fileName, duration, null);
    }

    /**
     * 获取一个客户端用来上传文件的预签名 URL，客户端使用 PUT 请求该URL来上传一个二进制文件，默认有效期5分钟
     *
     * @param fileName        文件名
     * @param duration        预签名 URL 将过期的时间
     * @param objectCannedACL 对象ACL，配置ACL需要存储桶开启ACL，并且PUT上传时需要header里设置`x-amz-acl`值
     * @return PresignedPutObjectRequest
     */
    @SneakyThrows
    public static PresignedPutObjectRequest getPutPresignedUrl(String fileName, Duration duration, ObjectCannedACL objectCannedACL) {
        return getPutPresignedUrl(fileName, duration, objectCannedACL, null);
    }

    /**
     * 获取一个客户端用来上传文件的预签名 URL，客户端使用 PUT 请求该URL来上传一个二进制文件
     *
     * @param fileName        文件名
     * @param duration        预签名 URL 将过期的时间
     * @param objectCannedACL 对象ACL，配置ACL需要存储桶开启ACL，并且PUT上传时需要header里设置`x-amz-acl`值
     * @param metadata        元数据，如果配置了metadata，则PUT上传时需要header里设置`x-amz-meta-`前缀，例如map是{'test-key':'test-value'}，则header里设置`x-amz-meta-test-key`，且值是`test-value`
     * @return URL
     */
    @SneakyThrows
    public static PresignedPutObjectRequest getPutPresignedUrl(String fileName, Duration duration, ObjectCannedACL objectCannedACL, Map<String, String> metadata) {
        String key = getPutFileObjKey(fileName);
        return s3Presigner.presignPutObject(
                PutObjectPresignRequest.builder()
                                       .signatureDuration(duration)
                                       .putObjectRequest(PutObjectRequest.builder().bucket(BUCKET_NAME).key(key).acl(objectCannedACL).metadata(metadata).build())
                                       .build()
        );
    }

    /**
     * 获取一个用于访问 Amazon S3 资源的预签名 URL，默认有效期7天
     *
     * @param url S3文件的URL
     * @return PresignedGetObjectRequest
     */
    public static PresignedGetObjectRequest getPresignedUrl(URL url) {
        return getPresignedUrl(url, PRESIGNED_URL_DURATION);
    }

    /**
     * 获取一个用于访问 Amazon S3 资源的预签名 URL
     *
     * @param url      S3文件的URL
     * @param duration 预签名 URL 将过期的时间
     * @return PresignedGetObjectRequest
     */
    public static PresignedGetObjectRequest getPresignedUrl(URL url, Duration duration) {
        return getPresignedUrl(url, duration, false);
    }

    /**
     * 获取一个用于访问 Amazon S3 资源的预签名 URL，默认有效期7天
     *
     * @param url         S3文件的URL
     * @param fileInfoUrl 预签名的URL是否附带文件信息
     * @return PresignedGetObjectRequest
     */
    public static PresignedGetObjectRequest getPresignedUrl(URL url, boolean fileInfoUrl) {
        return getPresignedUrl(url, PRESIGNED_URL_DURATION, fileInfoUrl);
    }

    /**
     * 获取一个用于访问 Amazon S3 资源的预签名 URL
     *
     * @param url         S3文件的URL
     * @param duration    预签名 URL 将过期的时间
     * @param fileInfoUrl 预签名的URL是否附带文件信息
     * @return PresignedGetObjectRequest
     */
    @SneakyThrows
    public static PresignedGetObjectRequest getPresignedUrl(URL url, Duration duration, boolean fileInfoUrl) {
        return getPresignedUrl(s3AsyncClient.utilities().parseUri(url.toURI()).key().orElseThrow(), duration, fileInfoUrl);
    }

    /**
     * 获取一个用于访问 Amazon S3 资源的预签名 URL，默认有效期7天
     *
     * @param key S3对象的键
     * @return PresignedGetObjectRequest
     */
    public static PresignedGetObjectRequest getPresignedUrl(String key) {
        return getPresignedUrl(key, Duration.ofDays(7));
    }

    /**
     * 获取一个用于访问 Amazon S3 资源的预签名 URL
     *
     * @param key      S3对象的键
     * @param duration 预签名 URL 将过期的时间
     * @return PresignedGetObjectRequest
     */
    public static PresignedGetObjectRequest getPresignedUrl(String key, Duration duration) {
        return getPresignedUrl(key, duration, false);
    }

    /**
     * 获取一个用于访问 Amazon S3 资源的预签名 URL，默认有效期7天
     *
     * @param key         S3对象的键
     * @param fileInfoUrl 预签名的URL是否附带文件信息
     * @return PresignedGetObjectRequest
     */
    public static PresignedGetObjectRequest getPresignedUrl(String key, boolean fileInfoUrl) {
        return getPresignedUrl(key, Duration.ofDays(7), fileInfoUrl);
    }

    /**
     * 获取一个用于访问 Amazon S3 资源的预签名 URL
     *
     * @param key         S3对象的键
     * @param duration    预签名 URL 将过期的时间
     * @param fileInfoUrl 预签名的URL是否附带文件信息
     * @return PresignedGetObjectRequest
     */
    public static PresignedGetObjectRequest getPresignedUrl(String key, Duration duration, boolean fileInfoUrl) {
        if (StrUtil.isBlank(key)) {
            return null;
        }
        if (fileInfoUrl) {
            HeadObjectResponse headObjectResponse = Optional.ofNullable(getObject(key)).orElseThrow();
            Map<String, String> metadata = headObjectResponse.metadata();
            AwsRequestOverrideConfiguration.Builder awsRequestOverrideConfigurationBuilder = AwsRequestOverrideConfiguration.builder();
            awsRequestOverrideConfigurationBuilder.putRawQueryParameter(UrlParamsConstants.CONTENT_TYPE, headObjectResponse.contentType());
            awsRequestOverrideConfigurationBuilder.putRawQueryParameter(UrlParamsConstants.CONTENT_LENGTH, String.valueOf(headObjectResponse.contentLength()));
            String videoDuration = metadata.get(MetadataConstants.DURATION);
            if (StrUtil.isNotBlank(videoDuration)) {
                awsRequestOverrideConfigurationBuilder.putRawQueryParameter(UrlParamsConstants.DURATION, videoDuration);
            }
            String thumbnailKey = metadata.get(MetadataConstants.THUMBNAIL);
            if (StrUtil.isNotBlank(thumbnailKey)) {
                URL thumbnailUrl = s3Presigner.presignGetObject(builder -> builder.signatureDuration(duration).getObjectRequest(request -> request.bucket(BUCKET_NAME).key(thumbnailKey).build()).build()).url();
                awsRequestOverrideConfigurationBuilder.putRawQueryParameter(UrlParamsConstants.THUMBNAIL, thumbnailUrl.toString());
            }
            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                                    .bucket(BUCKET_NAME)
                                    .key(key)
                                    .overrideConfiguration(awsRequestOverrideConfigurationBuilder.build())
                                    .build();
            GetObjectPresignRequest getObjectPresignRequest =
                    GetObjectPresignRequest.builder()
                                           .signatureDuration(duration)
                                           .getObjectRequest(getObjectRequest)
                                           .build();
            return s3Presigner.presignGetObject(getObjectPresignRequest);
        }
        return s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                                       .signatureDuration(duration)
                                       .getObjectRequest(GetObjectRequest.builder().bucket(BUCKET_NAME).key(key).build())
                                       .build()
        );
    }

    /**
     * 删除桶
     *
     * @param bucketName 桶名称
     * @return CompletableFuture
     */
    public static CompletableFuture<DeleteBucketResponse> deleteBucket(String bucketName) {
        return s3AsyncClient.deleteBucket(builder -> builder.bucket(bucketName).build());
    }

    /**
     * 根据S3文件的key删除文件
     *
     * @param key S3对象的键
     * @return CompletableFuture
     */
    public static CompletableFuture<DeleteObjectResponse> deleteFile(String key) {
        return s3AsyncClient.deleteObject(builder -> builder.bucket(BUCKET_NAME).key(key).build());
    }

    /**
     * 根据S3文件的key下载到指定目录文件
     *
     * @param key     S3对象的键
     * @param outFile 存储的目录文件
     * @return CompletableFuture
     */
    @SneakyThrows
    public static CompletableFuture<CompletedFileDownload> download(String key, File outFile) {
        return s3TransferManager.downloadFile(builder ->
                                                      builder.getObjectRequest(
                                                                     GetObjectRequest.builder()
                                                                                     .bucket(BUCKET_NAME)
                                                                                     .key(key)
                                                                                     .build()
                                                             )
                                                             .destination(outFile)
                                                             .build()
        ).completionFuture();
    }

    /**
     * 根据key判断文件对象是否存在
     *
     * @param key S3对象的键
     * @return boolean
     */
    public static boolean doesObjectExist(String key) {
        return s3AsyncClient.headObject(builder -> builder.bucket(BUCKET_NAME).key(key).build())
                            .thenApply(HeadObjectResponse::sdkHttpResponse)
                            .thenApply(SdkHttpResponse::isSuccessful)
                            .exceptionally(throwable -> {
                                if (throwable.getCause() instanceof NoSuchKeyException) {
                                    return false;
                                } else {
                                    throw new RuntimeException(throwable);
                                }
                            })
                            .join();
    }

    /**
     * 从 Amazon S3 检索对象
     *
     * @param key S3对象的键
     * @return S3Object
     */
    public static HeadObjectResponse getObject(String key) {
        return s3AsyncClient.headObject(builder -> builder.bucket(BUCKET_NAME).key(key).build()).join();
    }

    /**
     * 关闭 S3TransferManager 实例
     */
    public static void shutdown() {
        if (ObjUtil.isNotNull(s3TransferManager)) {
            log.info("Close the S3TransferManager instance...");
            s3TransferManager.close();
        }
    }

    private static final DateTimeFormatter PATH_DATE_PATTERN_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd/", Locale.getDefault()).withZone(ZoneId.systemDefault());

    /**
     * 获取预签名上传文件时存储的完整路径（Key）
     *
     * @param fileName 文件名
     * @return 完整路径
     */
    public static String getPutFileObjKey(String fileName) {
        String dateFormat = PATH_DATE_PATTERN_FORMATTER.format(Instant.now());
        String objectId = IdUtil.objectId();
        if (StrUtil.isBlank(fileName)) {
            return dateFormat + objectId;
        }
        return StrUtil.builder(dateFormat, objectId, StrUtil.SLASH, fileName).toString();
    }

    /**
     * 获取文件存储的完整路径（Key）
     *
     * @param fileName 文件名
     * @return 完整路径
     */
    public static String getFileObjKey(String fileName) {
        String dateFormat = PATH_DATE_PATTERN_FORMATTER.format(Instant.now());
        String objectId = IdUtil.objectId();
        if (StrUtil.isBlank(fileName)) {
            fileName = IdUtil.objectId();
        }
        return StrUtil.builder(dateFormat, objectId, StrUtil.SLASH, fileName).toString();
    }

    /**
     * 获取缩略图文件存储的完整路径（Key）
     *
     * @param originalFileKey 原始文件的key
     * @return 完整路径
     */
    public static String getThumbnailObjKey(String originalFileKey) {
        return Paths.get(originalFileKey).getParent().resolve("thumbnail.jpg").toString();
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

    /**
     * 保存到metadata中的数据的key
     */
    interface MetadataConstants {

        // 缩略图URL
        String THUMBNAIL = "nt-thumbnail";

        // 视频时长，单位（毫秒）
        String DURATION = "nt-duration";

    }

    /**
     * 文件URL中的参数名
     */
    interface UrlParamsConstants {

        // 文件大小，单位（字节）
        String CONTENT_LENGTH = "x-nt-content-length";

        // 内容类型
        String CONTENT_TYPE = "x-nt-content-type";

        // 缩略图URL
        String THUMBNAIL = "x-nt-thumbnail";

        // 视频时长，单位（毫秒）
        String DURATION = "x-nt-duration";

    }

}
