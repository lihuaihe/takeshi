package com.takeshi.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import com.takeshi.util.AwsSecretsManagerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

/**
 * AwsConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@AutoConfiguration(value = "awsConfig")
@ConditionalOnClass(S3TransferManager.class)
@RequiredArgsConstructor
public class AwsConfig {

    private final AWSSecretsManagerCredentials awsSecretsManagerCredentials;

    /**
     * 用户同步访问 Amazon S3 的服务客户端
     *
     * @return S3Client
     */
    @Bean
    @ConditionalOnMissingBean
    @DependsOn("secretsManagerClient")
    public S3Client s3Client() {
        JsonNode secret = AwsSecretsManagerUtil.getSecret();
        String accessKey = secret.path(this.awsSecretsManagerCredentials.getS3AccessKeySecrets()).asText();
        String secretKey = secret.path(this.awsSecretsManagerCredentials.getS3SecretKeySecrets()).asText();
        String bucketName = this.awsSecretsManagerCredentials.getBucketName();
        StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        S3Client s3Client = S3Client.builder()
                                    .region(Region.of(this.awsSecretsManagerCredentials.getRegion()))
                                    .credentialsProvider(staticCredentialsProvider)
                                    .build();
        try {
            s3Client.headBucket(builder -> builder.bucket(bucketName).build());
            log.info("AwsConfig.s3Client --> The bucket [{}] already exists", bucketName);
        } catch (NoSuchBucketException e) {
            createBucket(s3Client, bucketName);
            configurePublicAccessBlock(s3Client, bucketName);
            configureRemainingSettings(s3Client, bucketName);
            log.info("AwsConfig.s3Client --> The bucket [{}] has been successfully created and related configurations have been set", bucketName);
        }
        return s3Client;
    }

    /**
     * 创建存储桶
     *
     * @param s3Client   s3Client
     * @param bucketName bucketName
     */
    private void createBucket(S3Client s3Client, String bucketName) {
        log.info("AwsConfig.createBucket --> Bucket [{}] does not exist, create bucket [{}]", bucketName, bucketName);
        s3Client.createBucket(builder -> builder.bucket(bucketName).build());
    }

    /**
     * 设置是否阻止所有公开访问，必须先阻止公共访问才可以配置ACL和访问策略
     *
     * @param s3Client   s3Client
     * @param bucketName bucketName
     */
    private void configurePublicAccessBlock(S3Client s3Client, String bucketName) {
        boolean blockPublicAccess = this.awsSecretsManagerCredentials.isBlockPublicAccess();
        log.info("AwsConfig.configurePublicAccessBlock --> Public Access Block Configuration: {}", blockPublicAccess);
        s3Client.putPublicAccessBlock(builder -> builder.bucket(bucketName)
                                                        .publicAccessBlockConfiguration(config ->
                                                                                                config.blockPublicAcls(blockPublicAccess)
                                                                                                      .ignorePublicAcls(blockPublicAccess)
                                                                                                      .blockPublicPolicy(blockPublicAccess)
                                                                                                      .restrictPublicBuckets(blockPublicAccess)
                                                                                                      .build()
                                                        )
                                                        .build()
        );
    }

    /**
     * 配置其他设置
     *
     * @param s3Client   s3Client
     * @param bucketName bucketName
     */
    private void configureRemainingSettings(S3Client s3Client, String bucketName) {
        // 如果开启了公共访问，则可以配置ACL和访问策略
        if (!this.awsSecretsManagerCredentials.isBlockPublicAccess()) {
            // ACL和公共读策略二选一即可，优先ACL
            if (this.awsSecretsManagerCredentials.isBucketAcl()) {
                // 启用存储桶的ACL
                s3Client.putBucketOwnershipControls(builder ->
                                                            builder.bucket(bucketName)
                                                                   .ownershipControls(
                                                                           OwnershipControls.builder()
                                                                                            .rules(
                                                                                                    OwnershipControlsRule.builder()
                                                                                                                         .objectOwnership(ObjectOwnership.BUCKET_OWNER_PREFERRED)
                                                                                                                         .build()
                                                                                            )
                                                                                            .build()
                                                                   )
                                                                   .build()
                );
                log.info("AwsConfig.configureRemainingSettings --> Enable ACLs for buckets");
            } else if (this.awsSecretsManagerCredentials.isBucketPolicyPublicRead()) {
                // 配置存储桶公共读策略
                s3Client.putBucketPolicy(builder ->
                                                 builder.bucket(bucketName)
                                                        .policy("{\"Version\":\"2012-10-17\",\"Statement\":[{\"Sid\":\"PublicReadGetObject\",\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":\"s3:GetObject\",\"Resource\":\"arn:aws:s3:::" + bucketName + "/*\"}]}")
                                                        .build()
                );
                log.info("AwsConfig.configureRemainingSettings --> Configure bucket public read policy");
            }
        }

        if (this.awsSecretsManagerCredentials.isBucketAccelerate()) {
            // 为指定的存储桶启用传输加速，非必要可以不启用这个，启用了会浪费带宽，但是如果是非同个地区的访问启用了则会提升访问速度
            s3Client.putBucketAccelerateConfiguration(builder ->
                                                              builder.bucket(bucketName)
                                                                     .accelerateConfiguration(AccelerateConfiguration.builder().status(BucketAccelerateStatus.ENABLED).build())
                                                                     .build()
            );
            log.info("AwsConfig.configureRemainingSettings --> Enable transfer acceleration for a bucket");
        }

        // 配置存储桶的跨域规则
        s3Client.putBucketCors(builder -> builder.bucket(bucketName)
                                                 .corsConfiguration(
                                                         CORSConfiguration.builder()
                                                                          .corsRules(
                                                                                  CORSRule.builder()
                                                                                          .allowedHeaders("*")
                                                                                          .allowedMethods("GET", "PUT", "HEAD")
                                                                                          .allowedOrigins("*")
                                                                                          .exposeHeaders("ETag", "x-amz-meta-custom-header")
                                                                                          .maxAgeSeconds(3000)
                                                                                          .build()
                                                                          )
                                                                          .build()
                                                 )
                                                 .build()
        );
        log.info("AwsConfig.configureRemainingSettings --> Configure cross-domain rules for buckets");

        // 配置存储桶分段上传的生命周期规则
        s3Client.putBucketLifecycleConfiguration(builder ->
                                                         builder.bucket(bucketName)
                                                                .lifecycleConfiguration(
                                                                        BucketLifecycleConfiguration
                                                                                .builder()
                                                                                .rules(
                                                                                        LifecycleRule
                                                                                                .builder()
                                                                                                .id("Automatically delete incomplete multipart upload after seven days")
                                                                                                .filter(LifecycleRuleFilter.builder().build())
                                                                                                .abortIncompleteMultipartUpload(AbortIncompleteMultipartUpload.builder().daysAfterInitiation(7).build())
                                                                                                .status(ExpirationStatus.ENABLED)
                                                                                                .build()
                                                                                )
                                                                                .build()
                                                                )
                                                                .build()
        );
        log.info("AwsConfig.configureRemainingSettings --> Configure lifecycle rules for bucket multipart uploads");
    }

    /**
     * 用于异步访问 Amazon S3 的服务客户端
     *
     * @return S3AsyncClient
     */
    @Bean
    @ConditionalOnMissingBean
    @DependsOn("secretsManagerClient")
    public S3AsyncClient s3AsyncClient() {
        JsonNode secret = AwsSecretsManagerUtil.getSecret();
        String accessKey = secret.path(this.awsSecretsManagerCredentials.getS3AccessKeySecrets()).asText();
        String secretKey = secret.path(this.awsSecretsManagerCredentials.getS3SecretKeySecrets()).asText();
        StaticCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        return S3AsyncClient.builder()
                            .region(Region.of(this.awsSecretsManagerCredentials.getRegion()))
                            .credentialsProvider(staticCredentialsProvider)
                            .build();
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
    @DependsOn("s3AsyncClient")
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
    @DependsOn("secretsManagerClient")
    public S3Presigner s3Presigner() {
        JsonNode secret = AwsSecretsManagerUtil.getSecret();
        String accessKey = secret.path(this.awsSecretsManagerCredentials.getS3AccessKeySecrets()).asText();
        String secretKey = secret.path(this.awsSecretsManagerCredentials.getS3SecretKeySecrets()).asText();
        return S3Presigner.builder()
                          .region(Region.of(this.awsSecretsManagerCredentials.getRegion()))
                          .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                          .build();
    }

}
