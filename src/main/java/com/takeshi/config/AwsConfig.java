package com.takeshi.config;

import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import com.takeshi.util.AwsSecretsManagerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.util.concurrent.CompletableFuture;

/**
 * AwsConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@AutoConfiguration(value = "awsConfig")
@ConditionalOnClass(S3TransferManager.class)
@ConditionalOnBean(SecretsManagerClient.class)
@RequiredArgsConstructor
public class AwsConfig {

    private final AWSSecretsManagerCredentials awsSecretsManagerCredentials;

    /**
     * 用于异步访问 Amazon S3 的服务客户端
     *
     * @return S3AsyncClient
     */
    @Bean
    @ConditionalOnMissingBean
    public S3AsyncClient s3AsyncClient() {
        String accessKey = AwsSecretsManagerUtil.SECRET.get(awsSecretsManagerCredentials.getS3AccessKeySecrets()).asText();
        String secretKey = AwsSecretsManagerUtil.SECRET.get(awsSecretsManagerCredentials.getS3SecretKeySecrets()).asText();
        String bucketName = awsSecretsManagerCredentials.getBucketName();
        StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
                                                   .region(Region.of(awsSecretsManagerCredentials.getRegion()))
                                                   .credentialsProvider(staticCredentialsProvider)
                                                   .build();
        s3AsyncClient.headBucket(builder -> builder.bucket(bucketName).build())
                     .handle((headBucketResponse, throwable) -> {
                         if (throwable != null && throwable.getCause() instanceof NoSuchBucketException) {
                             return createBucketAndConfigure(s3AsyncClient, bucketName);
                         }
                         return CompletableFuture.completedFuture(null);
                     })
                     .join();
        return s3AsyncClient;
    }

    /**
     * 创建桶
     *
     * @param s3AsyncClient s3AsyncClient
     * @param bucketName    bucketName
     * @return CompletableFuture
     */
    private CompletableFuture<Void> createBucketAndConfigure(S3AsyncClient s3AsyncClient, String bucketName) {
        return s3AsyncClient.createBucket(builder -> builder.bucket(bucketName).build())
                            .thenCompose(createBucketResponse -> {
                                log.info("AwsConfig.createBucketAndConfigure --> Bucket [{}] created successfully: {}", bucketName, createBucketResponse.sdkHttpResponse().isSuccessful());
                                return configurePublicAccessBlock(s3AsyncClient, bucketName);
                            });
    }

    /**
     * 设置是否阻止所有公开访问，必须先阻止公共访问才可以配置ACL和访问策略
     *
     * @param s3AsyncClient s3AsyncClient
     * @param bucketName    bucketName
     * @return CompletableFuture
     */
    private CompletableFuture<Void> configurePublicAccessBlock(S3AsyncClient s3AsyncClient, String bucketName) {
        return s3AsyncClient.putPublicAccessBlock(
                PutPublicAccessBlockRequest.builder()
                                           .bucket(bucketName)
                                           .publicAccessBlockConfiguration(
                                                   PublicAccessBlockConfiguration.builder()
                                                                                 .blockPublicAcls(false)
                                                                                 .ignorePublicAcls(false)
                                                                                 .blockPublicPolicy(false)
                                                                                 .restrictPublicBuckets(false)
                                                                                 .build()
                                           )
                                           .build()
        ).thenCompose(response -> {
            log.info("AwsConfig.configurePublicAccessBlock --> Bucket [{}] blocks all public access successfully: {}", bucketName, response.sdkHttpResponse().isSuccessful());
            return configureRemainingSettings(s3AsyncClient, bucketName);
        });
    }

    /**
     * 配置其他设置
     *
     * @param s3AsyncClient s3AsyncClient
     * @param bucketName    bucketName
     * @return CompletableFuture
     */
    private CompletableFuture<Void> configureRemainingSettings(S3AsyncClient s3AsyncClient, String bucketName) {
        CompletableFuture<PutBucketOwnershipControlsResponse> aclFuture = CompletableFuture.completedFuture(null);
        if (awsSecretsManagerCredentials.isBucketAcl()) {
            // 启用存储桶的ACL
            log.info("AwsConfig.configureRemainingSettings --> Enable ACLs for buckets");
            aclFuture = s3AsyncClient.putBucketOwnershipControls(
                    PutBucketOwnershipControlsRequest.builder()
                                                     .bucket(bucketName)
                                                     .ownershipControls(
                                                             OwnershipControls.builder()
                                                                              .rules(
                                                                                      OwnershipControlsRule.builder()
                                                                                                           .objectOwnership(ObjectOwnership.BUCKET_OWNER_PREFERRED)
                                                                                                           .build())
                                                                              .build()
                                                     )
                                                     .build()
            );
        }
        CompletableFuture<PutBucketPolicyResponse> policyFuture = CompletableFuture.completedFuture(null);
        if (awsSecretsManagerCredentials.isBucketPolicyPublicRead()) {
            // 配置存储桶公共读策略
            log.info("AwsConfig.configureRemainingSettings --> Configure bucket public read policy");
            policyFuture = s3AsyncClient.putBucketPolicy(
                    PutBucketPolicyRequest.builder()
                                          .bucket(bucketName)
                                          .policy("{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"PublicReadGetObject\",\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":\"s3:GetObject\",\"Resource\":\"arn:aws:s3:::" + bucketName + "/*\"}]}")
                                          .build()
            );
        }
        CompletableFuture<PutBucketAccelerateConfigurationResponse> accelerateFuture = CompletableFuture.completedFuture(null);
        if (awsSecretsManagerCredentials.isBucketAccelerate()) {
            // 为指定的存储桶启用传输加速，非必要可以不启用这个，启用了会浪费带宽，但是如果是非同个地区的访问启用了则会提升访问速度
            log.info("AwsConfig.configureRemainingSettings --> Enable transfer acceleration for a bucket");
            accelerateFuture = s3AsyncClient.putBucketAccelerateConfiguration(
                    PutBucketAccelerateConfigurationRequest.builder()
                                                           .bucket(bucketName)
                                                           .accelerateConfiguration(
                                                                   AccelerateConfiguration.builder()
                                                                                          .status(BucketAccelerateStatus.ENABLED)
                                                                                          .build()
                                                           ).build()
            );
        }
        // 配置存储桶的跨域规则
        log.info("AwsConfig.configureRemainingSettings --> Configure cross-domain rules for buckets");
        CompletableFuture<PutBucketCorsResponse> corsFuture = s3AsyncClient.putBucketCors(
                PutBucketCorsRequest.builder()
                                    .bucket(bucketName)
                                    .corsConfiguration(
                                            CORSConfiguration.builder()
                                                             .corsRules(
                                                                     CORSRule.builder()
                                                                             .allowedHeaders("*")
                                                                             .allowedMethods("GET", "HEAD")
                                                                             .allowedOrigins("*")
                                                                             .exposeHeaders("ETag", "x-amz-meta-custom-header")
                                                                             .maxAgeSeconds(3000)
                                                                             .build()
                                                             )
                                                             .build()
                                    )
                                    .build()
        );
        // 配置存储桶分段上传的生命周期规则
        log.info("AwsConfig.configureRemainingSettings --> Configure lifecycle rules for bucket multipart uploads");
        CompletableFuture<PutBucketLifecycleConfigurationResponse> lifecycleFuture =
                s3AsyncClient.putBucketLifecycleConfiguration(
                        PutBucketLifecycleConfigurationRequest.builder()
                                                              .bucket(bucketName)
                                                              .lifecycleConfiguration(
                                                                      BucketLifecycleConfiguration.builder()
                                                                                                  .rules(
                                                                                                          LifecycleRule.builder()
                                                                                                                       .id("Automatically delete incomplete multipart upload after seven days")
                                                                                                                       .abortIncompleteMultipartUpload(AbortIncompleteMultipartUpload.builder().daysAfterInitiation(7).build())
                                                                                                                       .status(ExpirationStatus.ENABLED)
                                                                                                                       .build()
                                                                                                  )
                                                                                                  .build()
                                                              )
                                                              .build()
                );

        // 并行执行所有剩余的配置操作
        return CompletableFuture.allOf(aclFuture, policyFuture, accelerateFuture, corsFuture, lifecycleFuture);
    }

    /**
     * 用于管理到 Amazon S3 的传输的高级实用程序
     *
     * @param s3AsyncClient          s3AsyncClient
     * @param threadPoolTaskExecutor threadPoolTaskExecutor
     * @return S3TransferManager
     */
    @Bean
    @ConditionalOnMissingBean
    public S3TransferManager s3TransferManager(S3AsyncClient s3AsyncClient, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return S3TransferManager.builder()
                                .s3Client(s3AsyncClient)
                                .executor(threadPoolTaskExecutor)
                                .build();
    }

    /**
     * 用于管理 Amazon S3 对象签名
     *
     * @return S3Presigner
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner() {
        String accessKey = AwsSecretsManagerUtil.SECRET.get(awsSecretsManagerCredentials.getS3AccessKeySecrets()).asText();
        String secretKey = AwsSecretsManagerUtil.SECRET.get(awsSecretsManagerCredentials.getS3SecretKeySecrets()).asText();
        return S3Presigner.builder()
                          .region(Region.of(awsSecretsManagerCredentials.getRegion()))
                          .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                          .build();
    }

}
