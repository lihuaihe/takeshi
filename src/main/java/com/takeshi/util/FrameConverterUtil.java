package com.takeshi.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjUtil;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.takeshi.constants.TakeshiConstants;
import org.apache.tika.io.TikaInputStream;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * FrameConverterUtil
 * <pre>{@code
 * // 由于使用了获取视频时长的API，所以需要引入ffmpeg，为了减少依赖包大小，可自行按开发和生产环境导入对应平台的ffmpeg包
 * implementation "org.bytedeco:ffmpeg:+:macosx-x86_64"
 * implementation "org.bytedeco:ffmpeg:+:windows-x86_64"
 * implementation "org.bytedeco:ffmpeg:+:linux-x86_64"
 * implementation "org.bytedeco:javacv:+"
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
     * @param bucketName      bucketName
     * @param metadata        metadata
     * @param mediaType       mediaType
     * @param thumbnail       thumbnail
     * @param videoDuration   videoDuration
     * @throws IOException IOException
     */
    static void saveThumbnail(TikaInputStream tikaInputStream, String bucketName, ObjectMetadata metadata, String mediaType, boolean thumbnail, boolean videoDuration) throws IOException {
        if (mediaType.startsWith("video/") || "image/gif".equals(mediaType)) {
            Instant instant = Instant.now();
            // 是视频或GIF
            try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(tikaInputStream);
                frameGrabber.start();
                if (videoDuration && mediaType.startsWith("video/")) {
                    // 获取视频时长
                    metadata.addUserMetadata(AmazonS3Util.MetadataConstants.DURATION, String.valueOf(TimeUnit.MICROSECONDS.toMillis(frameGrabber.getLengthInTime())));
                }
                BufferedImage bufferedImage = null;
                if (thumbnail) {
                    Frame frame = frameGrabber.grabImage();
                    // 提取第一帧作为封面
                    bufferedImage = converter.getBufferedImage(frame);
                }
                frameGrabber.stop();
                if (ObjUtil.isNotNull(bufferedImage)) {
                    try (TikaInputStream thumbnailTikaInputStream = TikaInputStream.get(ImgUtil.toBytes(ImgUtil.scale(bufferedImage, 200, -1), ImgUtil.IMAGE_TYPE_JPG))) {
                        ObjectMetadata thumbnailMetadata = new ObjectMetadata();
                        thumbnailMetadata.setContentLength(thumbnailTikaInputStream.getLength());
                        thumbnailMetadata.setContentType("image/jpg");
                        // 添加用户自定义元数据
                        thumbnailMetadata.addUserMetadata(AmazonS3Util.MetadataConstants.CREATE_TIME, TakeshiConstants.INSTANT_FORMATTER.format(instant));
                        String thumbnailObjKey = AmazonS3Util.getThumbnailObjKey(instant);
                        // 添加缩略图的S3 key
                        metadata.addUserMetadata(AmazonS3Util.MetadataConstants.THUMBNAIL, thumbnailObjKey);
                        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, thumbnailObjKey, thumbnailTikaInputStream, thumbnailMetadata);
                        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                        AmazonS3Util.getTransferManager().upload(putObjectRequest);
                    }
                }
            }
        }
    }

}
