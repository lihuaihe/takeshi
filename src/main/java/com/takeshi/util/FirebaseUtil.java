package com.takeshi.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.messaging.*;
import com.takeshi.config.properties.FirebaseCredentials;
import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * FirebaseUtil
 * <pre>{@code
 * implementation 'com.google.firebase:firebase-admin:+'
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class FirebaseUtil {

    private static volatile FirebaseApp FIREBASE_APP;

    /**
     * 获取FirebaseApp
     *
     * @return FirebaseApp
     */
    @SneakyThrows
    public static FirebaseApp getFirebaseApp() {
        if (ObjUtil.isNull(FIREBASE_APP)) {
            synchronized (FirebaseUtil.class) {
                if (ObjUtil.isNull(FIREBASE_APP)) {
                    FirebaseCredentials firebase = SpringUtil.getBean(FirebaseCredentials.class);
                    String firebaseJsonFileName = firebase.getJsonFileName();
                    // Firebase需要的JSON文件
                    InputStream inputStream = ResourceUtil.getStreamSafe(firebaseJsonFileName);
                    Assert.notNull(inputStream, "firebaseJsonFileName [{}] not found", firebaseJsonFileName);
                    // Firebase Database用于数据存储的实时数据库 示例URL {https://<DATABASE_NAME>.firebaseio.com}
                    String databaseUrl = StrUtil.removeSuffix(
                            StrUtil.blankToDefault(AwsSecretsManagerUtil.getSecret().path(firebase.getDatabaseUrlSecrets()).asText(), firebase.getDatabaseUrl())
                            , StrUtil.SLASH);
                    FirebaseOptions options = FirebaseOptions.builder()
                                                             .setCredentials(GoogleCredentials.fromStream(inputStream))
                                                             .setDatabaseUrl(databaseUrl)
                                                             .setJsonFactory(GsonFactory.getDefaultInstance())
                                                             .build();
                    FIREBASE_APP = FirebaseApp.initializeApp(options);
                    log.info("FirebaseUtil.getFirebaseApp --> FirebaseApp Initialization successful");
                }
            }
        }
        return FIREBASE_APP;
    }

    /**
     * 构造函数
     */
    private FirebaseUtil() {
    }

    /**
     * Firebase Database
     */
    public static final class Database {

        private static volatile DatabaseReference DATABASE_REFERENCE;

        private Database() {
        }

        /**
         * 获取DatabaseReference
         *
         * @return DatabaseReference
         */
        public static DatabaseReference getDatabaseReference() {
            if (ObjUtil.isNull(DATABASE_REFERENCE)) {
                synchronized (Database.class) {
                    if (ObjUtil.isNull(DATABASE_REFERENCE)) {
                        DATABASE_REFERENCE = FirebaseDatabase.getInstance(getFirebaseApp()).getReference();
                    }
                }
            }
            return DATABASE_REFERENCE;
        }

        /**
         * 获取指定位置的的值
         *
         * @param pathString 子路径，例如：/child
         * @return 数据库位置的数据
         */
        public static DataSnapshot getValue(String pathString) {
            CompletableFuture<DataSnapshot> completableFuture = new CompletableFuture<>();
            getDatabaseReference().child(pathString)
                                  .addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(DataSnapshot snapshot) {
                                          completableFuture.complete(snapshot);
                                      }

                                      @Override
                                      public void onCancelled(DatabaseError error) {
                                          completableFuture.completeExceptionally(error.toException());
                                      }
                                  });
            return completableFuture.join();
        }

        /**
         * 对路径下的值自增1
         *
         * @param pathString 子路径，例如：/child
         * @return 数据库位置的数据
         */
        public static DataSnapshot increment(String pathString) {
            return runTransactionOfSelfChange(pathString, 1);
        }

        /**
         * 对路径下的值自减1
         *
         * @param pathString 子路径，例如：/child
         * @return 数据库位置的数据
         */
        public static DataSnapshot decrement(String pathString) {
            return runTransactionOfSelfChange(pathString, -1);
        }

        /**
         * 清空初始化该路径下的值，也就是将值设置为0
         *
         * @param pathString 子路径，例如：/child
         * @return 数据库位置的数据
         */
        public static DataSnapshot initialize(String pathString) {
            return runTransactionOfSelfChange(pathString, 0);
        }

        /**
         * <p>对路径下的值自增delta</p>
         * <p style="color:yellow;">注意：如果传了0，则直接将值设置为0，不进行加减</p>
         *
         * @param pathString 子路径，例如：/child
         * @param delta      值，如果传了0，则直接将值设置为0，不进行加减
         * @return 数据库位置的数据
         */
        public static DataSnapshot runTransactionOfSelfChange(String pathString, int delta) {
            CompletableFuture<DataSnapshot> completableFuture = new CompletableFuture<>();
            getDatabaseReference().child(pathString)
                                  .runTransaction(new Transaction.Handler() {
                                      @Override
                                      public Transaction.Result doTransaction(MutableData currentData) {
                                          Integer finalValue = (0 == delta) ? delta : ObjUtil.defaultIfNull(currentData.getValue(Integer.class), 0) + delta;
                                          currentData.setValue(finalValue);
                                          return Transaction.success(currentData);
                                      }

                                      @Override
                                      public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                                          if (error != null) {
                                              log.error("Database.onComplete --> error: ", error.toException());
                                          } else {
                                              completableFuture.complete(currentData);
                                          }
                                      }
                                  });
            return completableFuture.join();
        }

        /**
         * 将此位置的数据设置为给定值。将 null 传递给 setValue() 将删除指定位置的数据
         *
         * @param pathString 子路径，例如：/child
         * @param value      值，可以不特地转JSON字符串<p style="color:yellow;">注意：数值类型的值过大时web端展示会精度丢失，如果是Long和BigInteger类型会转成String在存入</p>
         * @return {@link ApiFuture}
         */
        public static ApiFuture<Void> setValueAsync(String pathString, Object value) {
            return getDatabaseReference().child(pathString).setValueAsync((value instanceof Long || value instanceof BigInteger) ? StrUtil.toStringOrNull(value) : value);
        }

        /**
         * 更新此位置下的子节点。将映射中的 null 传递给 updateChildren() 将删除指定位置的值。
         *
         * @param pathString 子路径
         * @param update     更新的路径及其新值
         * @return {@link ApiFuture}
         */
        public static ApiFuture<Void> updateChildValuesAsync(String pathString, Map<String, Object> update) {
            return getDatabaseReference().child(pathString).updateChildrenAsync(update);
        }

        /**
         * 将此位置的值设置为 null，即删除指定位置的数据
         *
         * @param pathString 子路径，例如：/child
         * @return {@link ApiFuture}
         */
        public static ApiFuture<Void> removeValueAsync(String pathString) {
            return getDatabaseReference().child(pathString).removeValueAsync();
        }

    }

    /**
     * Firebase Messaging
     */
    public static final class Messaging {

        /**
         * Firebase Messaging Instance
         */
        public static volatile FirebaseMessaging FIREBASE_MESSAGING;

        private Messaging() {
        }

        /**
         * 消息参数
         */
        @EqualsAndHashCode(callSuper = true)
        @Data
        @Schema
        @Accessors(chain = true)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class MessageParams extends AbstractBasicSerializable {

            /**
             * 设备的注册令牌
             */
            @Schema(description = "设备的注册令牌")
            private String token;

            /**
             * 通知的标题（默认为应用名称）
             * <br/>
             * 发送透传消息时如果title有值了，map就不要放入title的key了，会被此title覆盖
             */
            @Schema(description = "通知的标题")
            private String title;

            /**
             * 通知的正文
             * <br/>
             * 发送透传消息时如果body有值了，map就不要放入body的key了，会被此body覆盖
             */
            @Schema(description = "通知的正文")
            private String body;

            /**
             * 将给定映射中的所有键值对作为数据字段添加到消息中
             */
            @Schema(description = "将给定映射中的所有键值对作为数据字段添加到消息中")
            private Map<String, String> map;

            /**
             * 设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
             * <br/>
             * 发送透传消息时此参数不需要
             */
            @Schema(description = "设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变")
            private Integer iosBadge;

            /**
             * 设置与用户点击外部通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
             * <p style="color:yellow;">无法做到完全自定义跳转APP应用内界面，一般通过map传递需要clickAction</p>
             * 发送透传消息时此参数不需要
             */
            @Schema(description = "设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动")
            private String clickAction;

            /**
             * 构造函数
             *
             * @param token 设备的注册令牌
             */
            public MessageParams(String token) {
                this.token = token;
            }

            /**
             * 构造函数
             *
             * @param token 设备的注册令牌
             * @param body  通知的正文
             */
            public MessageParams(String token, String body) {
                this.token = token;
                this.body = body;
            }

            /**
             * 构造函数
             *
             * @param token 设备的注册令牌
             * @param body  通知的正文
             * @param map   附加值
             */
            public MessageParams(String token, String body, Map<String, String> map) {
                this.token = token;
                this.body = body;
                this.map = map;
            }

            /**
             * 构造函数
             *
             * @param token    设备的注册令牌
             * @param body     通知的正文
             * @param map      附加值
             * @param iosBadge ios徽章
             */
            public MessageParams(String token, String body, Map<String, String> map, Integer iosBadge) {
                this.token = token;
                this.body = body;
                this.map = map;
                this.iosBadge = iosBadge;
            }

            /**
             * 构造函数
             *
             * @param token 设备的注册令牌
             * @param title 通知的标题
             * @param body  通知的正文
             */
            public MessageParams(String token, String title, String body) {
                this.token = token;
                this.title = title;
                this.body = body;
            }

            /**
             * 构造函数
             *
             * @param token 设备的注册令牌
             * @param title 通知的标题
             * @param body  通知的正文
             * @param map   附加值
             */
            public MessageParams(String token, String title, String body, Map<String, String> map) {
                this.token = token;
                this.title = title;
                this.body = body;
                this.map = map;
            }

            /**
             * 构造函数
             *
             * @param token    设备的注册令牌
             * @param title    通知的标题
             * @param body     通知的正文
             * @param map      附加值
             * @param iosBadge ios徽章
             */
            public MessageParams(String token, String title, String body, Map<String, String> map, Integer iosBadge) {
                this.token = token;
                this.title = title;
                this.body = body;
                this.map = map;
                this.iosBadge = iosBadge;
            }

        }

        /**
         * 多播消息参数
         */
        @EqualsAndHashCode(callSuper = true)
        @Data
        @Schema
        @Accessors(chain = true)
        @AllArgsConstructor
        @NoArgsConstructor
        public static class MulticastMessageParams extends AbstractBasicSerializable {

            /**
             * 设备的注册令牌
             */
            @Schema(description = "设备的注册令牌")
            private Collection<String> tokens;

            /**
             * 通知的标题（默认为应用名称）
             * <br/>
             * 发送透传消息时如果title有值了，map就不要放入title的key了，会被此title覆盖
             */
            @Schema(description = "通知的标题")
            private String title;

            /**
             * 通知的正文
             * <br/>
             * 发送透传消息时如果body有值了，map就不要放入body的key了，会被此body覆盖
             */
            @Schema(description = "通知的正文")
            private String body;

            /**
             * 将给定映射中的所有键值对作为数据字段添加到消息中
             */
            @Schema(description = "将给定映射中的所有键值对作为数据字段添加到消息中")
            private Map<String, String> map;

            /**
             * 设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
             * <br/>
             * 发送透传消息时此参数不需要
             */
            @Schema(description = "设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变")
            private Integer iosBadge;

            /**
             * 设置与用户点击外部通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
             * <p style="color:yellow;">无法做到完全自定义跳转APP应用内界面，一般通过map传递需要clickAction</p>
             * 发送透传消息时此参数不需要
             */
            @Schema(description = "设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动")
            private String clickAction;

            /**
             * 构造函数
             *
             * @param tokens 设备的注册令牌
             */
            public MulticastMessageParams(Collection<String> tokens) {
                this.tokens = tokens;
            }

            /**
             * 构造函数
             *
             * @param tokens 设备的注册令牌
             * @param body   通知的正文
             */
            public MulticastMessageParams(Collection<String> tokens, String body) {
                this.tokens = tokens;
                this.body = body;
            }

            /**
             * 构造函数
             *
             * @param tokens 设备的注册令牌
             * @param body   通知的正文
             * @param map    附加值
             */
            public MulticastMessageParams(Collection<String> tokens, String body, Map<String, String> map) {
                this.tokens = tokens;
                this.body = body;
                this.map = map;
            }

            /**
             * 构造函数
             *
             * @param tokens   设备的注册令牌
             * @param body     通知的正文
             * @param map      附加值
             * @param iosBadge ios徽章
             */
            public MulticastMessageParams(Collection<String> tokens, String body, Map<String, String> map, Integer iosBadge) {
                this.tokens = tokens;
                this.body = body;
                this.map = map;
                this.iosBadge = iosBadge;
            }

            /**
             * 构造函数
             *
             * @param tokens 设备的注册令牌
             * @param title  通知的标题
             * @param body   通知的正文
             */
            public MulticastMessageParams(Collection<String> tokens, String title, String body) {
                this.tokens = tokens;
                this.title = title;
                this.body = body;
            }

            /**
             * 构造函数
             *
             * @param tokens 设备的注册令牌
             * @param title  通知的标题
             * @param body   通知的正文
             * @param map    附加值
             */
            public MulticastMessageParams(Collection<String> tokens, String title, String body, Map<String, String> map) {
                this.tokens = tokens;
                this.title = title;
                this.body = body;
                this.map = map;
            }

            /**
             * 构造函数
             *
             * @param tokens   设备的注册令牌
             * @param title    通知的标题
             * @param body     通知的正文
             * @param map      附加值
             * @param iosBadge ios徽章
             */
            public MulticastMessageParams(Collection<String> tokens, String title, String body, Map<String, String> map, Integer iosBadge) {
                this.tokens = tokens;
                this.title = title;
                this.body = body;
                this.map = map;
                this.iosBadge = iosBadge;
            }

        }

        /**
         * 获取FirebaseMessaging
         *
         * @return FirebaseMessaging
         */
        public static FirebaseMessaging getFirebaseMessaging() {
            if (ObjUtil.isNull(FIREBASE_MESSAGING)) {
                synchronized (Messaging.class) {
                    if (ObjUtil.isNull(FIREBASE_MESSAGING)) {
                        FIREBASE_MESSAGING = FirebaseMessaging.getInstance(getFirebaseApp());
                    }
                }
            }
            return FIREBASE_MESSAGING;
        }

        /**
         * 通过 Firebase Cloud Messaging 发送外部通知给指定的token
         *
         * @param messageParams 消息参数
         * @return String
         * @throws FirebaseMessagingException FirebaseMessagingException
         */
        public static String sendByToken(MessageParams messageParams) throws FirebaseMessagingException {
            return getFirebaseMessaging().send(buildMessage(messageParams));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送透传消息（内部通知，APP关闭通知也能收到）给指定的token
         *
         * @param messageParams 消息参数
         * @return String
         * @throws FirebaseMessagingException FirebaseMessagingException
         */
        public static String sendTransparentByToken(MessageParams messageParams) throws FirebaseMessagingException {
            return getFirebaseMessaging().send(buildTransparentMessage(messageParams));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送外部通知给指定的token
         *
         * @param messageParams 消息参数
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(MessageParams messageParams) {
            return getFirebaseMessaging().sendAsync(buildMessage(messageParams));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送透传消息（内部通知，APP关闭通知也能收到）给指定的token
         *
         * @param messageParams 消息参数
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendTransparentByTokenAsync(MessageParams messageParams) {
            return getFirebaseMessaging().sendAsync(buildTransparentMessage(messageParams));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param multicastMessageParams 多播消息参数
         * @return {@link BatchResponse}
         * @throws FirebaseMessagingException FirebaseMessagingException
         */
        public static BatchResponse sendMulticastByTokens(MulticastMessageParams multicastMessageParams) throws FirebaseMessagingException {
            return getFirebaseMessaging().sendEachForMulticast(buildMulticastMessage(multicastMessageParams));
        }

        /**
         * 将给定的多播透传消息（内部通知，APP关闭通知也能收到）发送到其中指定的所有设备的注册令牌
         *
         * @param multicastMessageParams 多播消息参数
         * @return {@link BatchResponse}
         * @throws FirebaseMessagingException FirebaseMessagingException
         */
        public static BatchResponse sendTransparentMulticastByTokens(MulticastMessageParams multicastMessageParams) throws FirebaseMessagingException {
            return getFirebaseMessaging().sendEachForMulticast(buildTransparentMulticastMessage(multicastMessageParams));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param multicastMessageParams 多播消息参数
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(MulticastMessageParams multicastMessageParams) {
            return getFirebaseMessaging().sendEachForMulticastAsync(buildMulticastMessage(multicastMessageParams));
        }

        /**
         * 将给定的多播透传消息（内部通知，APP关闭通知也能收到）发送到其中指定的所有设备的注册令牌
         *
         * @param multicastMessageParams 多播消息参数
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendTransparentMulticastByTokensAsync(MulticastMessageParams multicastMessageParams) {
            return getFirebaseMessaging().sendEachForMulticastAsync(buildTransparentMulticastMessage(multicastMessageParams));
        }

        /**
         * 外部通知消息实例
         *
         * @param messageParams 消息参数
         * @return 消息
         */
        public static Message buildMessage(MessageParams messageParams) {
            AndroidNotification.Builder androidBuilder = AndroidNotification.builder().setSound("default");
            Aps.Builder apsBuilder = Aps.builder().setSound("default");
            if (StrUtil.isNotBlank(messageParams.clickAction)) {
                androidBuilder.setClickAction(messageParams.clickAction);
                apsBuilder.setCategory(messageParams.clickAction);
            }
            if (ObjUtil.isNotNull(messageParams.iosBadge)) {
                apsBuilder.setBadge(messageParams.iosBadge);
            }
            Message.Builder builder =
                    Message.builder()
                           .setToken(messageParams.token)
                           .setNotification(Notification.builder().setTitle(messageParams.title).setBody(messageParams.body).build())
                           .setAndroidConfig(
                                   AndroidConfig.builder()
                                                .setPriority(AndroidConfig.Priority.HIGH)
                                                .setNotification(androidBuilder.build())
                                                .build())
                           .setApnsConfig(
                                   ApnsConfig.builder()
                                             .putHeader("apns-priority", "10")
                                             .setAps(apsBuilder.build())
                                             .build());
            if (CollUtil.isNotEmpty(messageParams.map)) {
                // 将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空，移除key或value为null的数据
                messageParams.map.entrySet().removeIf(entry -> ObjUtil.hasNull(entry.getKey(), entry.getValue()));
                builder.putAllData(messageParams.map);
            }
            return builder.build();
        }

        /**
         * 透传消息（内部通知消息）实例
         *
         * @param messageParams 消息参数
         * @return 消息
         */
        public static Message buildTransparentMessage(MessageParams messageParams) {
            Message.Builder builder = Message.builder().setToken(messageParams.token);
            if (CollUtil.isNotEmpty(messageParams.map)) {
                // 将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空，移除key或value为null的数据
                messageParams.map.entrySet().removeIf(entry -> ObjUtil.hasNull(entry.getKey(), entry.getValue()));
                builder.putAllData(messageParams.map);
            }
            if (StrUtil.isNotBlank(messageParams.title)) {
                builder.putData("title", messageParams.title);
            }
            if (StrUtil.isNotBlank(messageParams.body)) {
                builder.putData("body", messageParams.body);
            }
            return builder.build();
        }

        /**
         * 多播消息实例
         *
         * @param multicastMessageParams 多播消息参数
         * @return 多播消息
         */
        public static MulticastMessage buildMulticastMessage(MulticastMessageParams multicastMessageParams) {
            AndroidNotification.Builder androidBuilder = AndroidNotification.builder().setSound("default");
            Aps.Builder apsBuilder = Aps.builder().setSound("default");
            if (StrUtil.isNotBlank(multicastMessageParams.clickAction)) {
                androidBuilder.setClickAction(multicastMessageParams.clickAction);
                apsBuilder.setCategory(multicastMessageParams.clickAction);
            }
            if (ObjUtil.isNotNull(multicastMessageParams.iosBadge)) {
                apsBuilder.setBadge(multicastMessageParams.iosBadge);
            }
            MulticastMessage.Builder builder =
                    MulticastMessage.builder()
                                    .addAllTokens(multicastMessageParams.tokens)
                                    .setNotification(Notification.builder().setTitle(multicastMessageParams.title).setBody(multicastMessageParams.body).build())
                                    .setAndroidConfig(
                                            AndroidConfig.builder()
                                                         .setPriority(AndroidConfig.Priority.HIGH)
                                                         .setNotification(androidBuilder.build())
                                                         .build())
                                    .setApnsConfig(
                                            ApnsConfig.builder()
                                                      .putHeader("apns-priority", "10")
                                                      .setAps(apsBuilder.build())
                                                      .build());
            if (CollUtil.isNotEmpty(multicastMessageParams.map)) {
                // 将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空，移除key或value为null的数据
                multicastMessageParams.map.entrySet().removeIf(entry -> ObjUtil.hasNull(entry.getKey(), entry.getValue()));
                builder.putAllData(multicastMessageParams.map);
            }
            return builder.build();
        }

        /**
         * 多播透传消息（内部通知消息）实例
         *
         * @param multicastMessageParams 多播消息参数
         * @return 多播消息
         */
        public static MulticastMessage buildTransparentMulticastMessage(MulticastMessageParams multicastMessageParams) {
            MulticastMessage.Builder builder = MulticastMessage.builder().addAllTokens(multicastMessageParams.tokens);
            if (CollUtil.isNotEmpty(multicastMessageParams.map)) {
                // 将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空，移除key或value为null的数据
                multicastMessageParams.map.entrySet().removeIf(entry -> ObjUtil.hasNull(entry.getKey(), entry.getValue()));
                builder.putAllData(multicastMessageParams.map);
            }
            if (StrUtil.isNotBlank(multicastMessageParams.title)) {
                builder.putData("title", multicastMessageParams.title);
            }
            if (StrUtil.isNotBlank(multicastMessageParams.body)) {
                builder.putData("body", multicastMessageParams.body);
            }
            return builder.build();
        }

    }

}
