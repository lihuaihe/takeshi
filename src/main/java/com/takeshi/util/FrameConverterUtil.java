package com.takeshi.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjUtil;
import org.apache.tika.io.TikaInputStream;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * FrameConverterUtil
 * <pre>{@code
 * implementation "org.bytedeco:javacv:+"
 * // 由于使用了获取视频时长的API，所以需要引入ffmpeg，为了减少依赖包大小，可自行按开发和生产环境导入对应平台的ffmpeg包
 * implementation "org.bytedeco:ffmpeg:+:macosx-x86_64"
 * implementation "org.bytedeco:ffmpeg:+:windows-x86_64"
 * implementation "org.bytedeco:ffmpeg:+:linux-x86_64"
 * }
 * </pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
class FrameConverterUtil {

    /**
     * 构造函数
     */
    FrameConverterUtil() {
    }

    /**
     * 保存缩略图
     *
     * @param tikaInputStream tikaInputStream
     * @param originalFileKey 原始文件的key
     * @param fileAcl         文件对象的访问控制列表 (ACL)
     * @param mediaType       mediaType
     * @param thumbnail       thumbnail
     * @param videoDuration   videoDuration
     * @return Map
     * @throws IOException IOException
     */
    static Map<String, String> saveThumbnail(TikaInputStream tikaInputStream, String originalFileKey, ObjectCannedACL fileAcl, String mediaType, boolean thumbnail, boolean videoDuration) throws IOException {
        Map<String, String> userMetadata = new HashMap<>(5);
        if (mediaType.startsWith("video/") || "image/gif".equals(mediaType)) {
            // 是视频或GIF
            try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(tikaInputStream);
                frameGrabber.start();
                if (videoDuration && mediaType.startsWith("video/")) {
                    // 获取视频时长
                    userMetadata.put(AmazonS3Util.MetadataConstants.DURATION, String.valueOf(TimeUnit.MICROSECONDS.toMillis(frameGrabber.getLengthInTime())));
                }
                BufferedImage bufferedImage = null;
                if (thumbnail) {
                    Frame frame = frameGrabber.grabImage();
                    // 提取第一帧作为封面
                    bufferedImage = converter.getBufferedImage(frame);
                }
                frameGrabber.stop();
                if (ObjUtil.isNotNull(bufferedImage)) {
                    // 保存图片缩略图
                    byte[] bytes = ImgUtil.toBytes(ImgUtil.scale(bufferedImage, 200, -1), ImgUtil.IMAGE_TYPE_JPG);
                    String thumbnailObjKey = AmazonS3Util.getThumbnailObjKey(originalFileKey);
                    // 添加缩略图的S3 key
                    userMetadata.put(AmazonS3Util.MetadataConstants.THUMBNAIL, thumbnailObjKey);
                    AmazonS3Util.getS3TransferManager().upload(
                            UploadRequest.builder()
                                         .requestBody(
                                                 AsyncRequestBody.fromBytes(bytes)
                                         )
                                         .putObjectRequest(
                                                 PutObjectRequest.builder()
                                                                 .bucket(AmazonS3Util.getBucketName())
                                                                 .key(thumbnailObjKey)
                                                                 .contentType("image/jpg")
                                                                 .contentLength((long) bytes.length)
                                                                 .acl(fileAcl)
                                                                 .build()
                                         )
                                         .build()
                    );
                }
            }
        }
        return userMetadata;
    }

}
