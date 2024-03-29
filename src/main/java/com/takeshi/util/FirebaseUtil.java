package com.takeshi.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.google.firebase.messaging.*;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.properties.FirebaseCredentials;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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

    static {
        if (ObjUtil.isNull(FIREBASE_APP)) {
            synchronized (FirebaseUtil.class) {
                if (ObjUtil.isNull(FIREBASE_APP)) {
                    try {
                        FirebaseCredentials firebase = StaticConfig.takeshiProperties.getFirebase();
                        String firebaseJsonFileName = firebase.getJsonFileName();
                        // Firebase需要的JSON文件
                        InputStream inputStream = ResourceUtil.getStreamSafe(firebaseJsonFileName);
                        if (ObjUtil.isNull(inputStream)) {
                            log.error("FirebaseUtil.static --> firebaseJsonFileName [{}] not found", firebaseJsonFileName);
                        } else {
                            // Firebase Database用于数据存储的实时数据库 示例URL {https://<DATABASE_NAME>.firebaseio.com}
                            String databaseUrl = StrUtil.isBlank(firebase.getDatabaseUrlSecrets())
                                    ? firebase.getDatabaseUrl()
                                    : AmazonS3Util.getSecret().get(firebase.getDatabaseUrlSecrets()).asText();
                            FirebaseOptions options = FirebaseOptions.builder()
                                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                                    .setDatabaseUrl(databaseUrl)
                                    .build();
                            FIREBASE_APP = FirebaseApp.initializeApp(options);
                            log.info("FirebaseUtil.static --> FirebaseApp Initialization successful");
                        }
                    } catch (IOException e) {
                        log.error("FirebaseUtil.static --> FirebaseApp initialization failed, e: ", e);
                    }
                }
            }
        }
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

        /**
         * Database Reference
         */
        public static final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance(FIREBASE_APP).getReference();

        private Database() {
        }

        /**
         * 获取指定位置的的值
         *
         * @param pathString 子路径
         * @return 数据库位置的数据
         */
        public static DataSnapshot getValue(String pathString) {
            CompletableFuture<DataSnapshot> completableFuture = new CompletableFuture<>();
            DATABASE_REFERENCE.child(pathString)
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
         * @param pathString 子路径
         * @return 数据库位置的数据
         */
        public static DataSnapshot increment(String pathString) {
            return runTransactionOfSelfChange(pathString, 1);
        }

        /**
         * 对路径下的值自减1
         *
         * @param pathString 子路径
         * @return 数据库位置的数据
         */
        public static DataSnapshot decrement(String pathString) {
            return runTransactionOfSelfChange(pathString, -1);
        }

        /**
         * 清空初始化该路径下的值，也就是将值设置为0
         *
         * @param pathString 子路径
         * @return 数据库位置的数据
         */
        public static DataSnapshot initialize(String pathString) {
            return runTransactionOfSelfChange(pathString, 0);
        }

        /**
         * <p>对路径下的值自增delta</p>
         * <p style="color:yellow;">注意：如果传了0，则直接将值设置为0，不进行加减</p>
         *
         * @param pathString 子路径
         * @param delta      值，如果传了0，则直接将值设置为0，不进行加减
         * @return 数据库位置的数据
         */
        public static DataSnapshot runTransactionOfSelfChange(String pathString, int delta) {
            CompletableFuture<DataSnapshot> completableFuture = new CompletableFuture<>();
            DATABASE_REFERENCE.child(pathString)
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
         * @param pathString 子路径
         * @param value      值，不需要特地转JSON字符串，
         * @return {@link ApiFuture}
         */
        public static ApiFuture<Void> setValueAsync(String pathString, Object value) {
            return DATABASE_REFERENCE.child(pathString).setValueAsync((value instanceof Long || value instanceof BigInteger) ? StrUtil.toStringOrNull(value) : value);
        }

        /**
         * 将此位置的值设置为 null，即删除指定位置的数据
         *
         * @param pathString 子路径
         * @return {@link ApiFuture}
         */
        public static ApiFuture<Void> removeValueAsync(String pathString) {
            return DATABASE_REFERENCE.child(pathString).removeValueAsync();
        }

    }

    /**
     * Firebase Messaging
     */
    public static final class Messaging {

        /**
         * Firebase Messaging Instance
         */
        public static final FirebaseMessaging FIREBASE_MESSAGING = FirebaseMessaging.getInstance(FIREBASE_APP);

        private Messaging() {
        }

        /**
         * 通过 Firebase Cloud Messaging 发送Message给指定的token
         *
         * @param token 设备的注册令牌
         * @param title 通知的标题
         * @param body  通知正文
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(String token, String title, String body) {
            return FIREBASE_MESSAGING.sendAsync(buildMessage(token, title, body, null, null, null));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送Message给指定的token
         *
         * @param token    设备的注册令牌
         * @param title    通知的标题
         * @param body     通知正文
         * @param iosBadge 设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(String token, String title, String body, Integer iosBadge) {
            return FIREBASE_MESSAGING.sendAsync(buildMessage(token, title, body, null, iosBadge, null));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送Message给指定的token
         *
         * @param token       设备的注册令牌
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(String token, String title, String body, String clickAction) {
            return FIREBASE_MESSAGING.sendAsync(buildMessage(token, title, body, clickAction, null, null));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送Message给指定的token
         *
         * @param token       设备的注册令牌
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @param iosBadge    设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(String token, String title, String body, String clickAction, Integer iosBadge) {
            return FIREBASE_MESSAGING.sendAsync(buildMessage(token, title, body, clickAction, iosBadge, null));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送Message给指定的token
         *
         * @param token 设备的注册令牌
         * @param title 通知的标题
         * @param body  通知正文
         * @param map   将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(String token, String title, String body, Map<String, String> map) {
            return FIREBASE_MESSAGING.sendAsync(buildMessage(token, title, body, null, null, map));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送Message给指定的token
         *
         * @param token    设备的注册令牌
         * @param title    通知的标题
         * @param body     通知正文
         * @param map      将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @param iosBadge 设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(String token, String title, String body, Map<String, String> map, Integer iosBadge) {
            return FIREBASE_MESSAGING.sendAsync(buildMessage(token, title, body, null, iosBadge, map));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送Message给指定的token
         *
         * @param token       设备的注册令牌
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @param map         将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(String token, String title, String body, String clickAction, Map<String, String> map) {
            return FIREBASE_MESSAGING.sendAsync(buildMessage(token, title, body, clickAction, null, map));
        }

        /**
         * 通过 Firebase Cloud Messaging 发送Message给指定的token
         *
         * @param token       设备的注册令牌
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @param map         将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @param iosBadge    设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return {@link ApiFuture}
         */
        public static ApiFuture<String> sendByTokenAsync(String token, String title, String body, String clickAction, Map<String, String> map, Integer iosBadge) {
            return FIREBASE_MESSAGING.sendAsync(buildMessage(token, title, body, clickAction, iosBadge, map));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param tokens 设备注册令牌的集合
         * @param title  通知的标题
         * @param body   通知正文
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(Collection<String> tokens, String title, String body) {
            return FIREBASE_MESSAGING.sendEachForMulticastAsync(buildMulticastMessage(tokens, title, body, null, null, null));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param tokens   设备注册令牌的集合
         * @param title    通知的标题
         * @param body     通知正文
         * @param iosBadge 设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(Collection<String> tokens, String title, String body, Integer iosBadge) {
            return FIREBASE_MESSAGING.sendEachForMulticastAsync(buildMulticastMessage(tokens, title, body, null, null, iosBadge));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param tokens      设备注册令牌的集合
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(Collection<String> tokens, String title, String body, String clickAction) {
            return FIREBASE_MESSAGING.sendEachForMulticastAsync(buildMulticastMessage(tokens, title, body, clickAction, null, null));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param tokens      设备注册令牌的集合
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @param iosBadge    设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(Collection<String> tokens, String title, String body, String clickAction, Integer iosBadge) {
            return FIREBASE_MESSAGING.sendEachForMulticastAsync(buildMulticastMessage(tokens, title, body, clickAction, null, iosBadge));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param tokens 设备注册令牌的集合
         * @param title  通知的标题
         * @param body   通知正文
         * @param map    将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(Collection<String> tokens, String title, String body, Map<String, String> map) {
            return FIREBASE_MESSAGING.sendEachForMulticastAsync(buildMulticastMessage(tokens, title, body, null, map, null));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param tokens   设备注册令牌的集合
         * @param title    通知的标题
         * @param body     通知正文
         * @param map      将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @param iosBadge 设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(Collection<String> tokens, String title, String body, Map<String, String> map, Integer iosBadge) {
            return FIREBASE_MESSAGING.sendEachForMulticastAsync(buildMulticastMessage(tokens, title, body, null, map, iosBadge));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param tokens      设备注册令牌的集合
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @param map         将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(Collection<String> tokens, String title, String body, String clickAction, Map<String, String> map) {
            return FIREBASE_MESSAGING.sendEachForMulticastAsync(buildMulticastMessage(tokens, title, body, clickAction, map, null));
        }

        /**
         * 将给定的多播消息发送到其中指定的所有设备的注册令牌
         *
         * @param tokens      设备注册令牌的集合
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @param map         将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @param iosBadge    设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return {@link ApiFuture}
         */
        public static ApiFuture<BatchResponse> sendMulticastByTokensAsync(Collection<String> tokens, String title, String body, String clickAction, Map<String, String> map, Integer iosBadge) {
            return FIREBASE_MESSAGING.sendEachForMulticastAsync(buildMulticastMessage(tokens, title, body, clickAction, map, iosBadge));
        }

        /**
         * 消息实例
         *
         * @param token       设备的注册令牌
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @param iosBadge    设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @param map         将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @return 消息
         */
        private static Message buildMessage(String token, String title, String body, String clickAction, Integer iosBadge, Map<String, String> map) {
            Message.Builder builder = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setClickAction(clickAction)
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .putHeader("apns-priority", "10")
                            .setAps(Aps.builder().setSound("default").setCategory(clickAction).setBadge(iosBadge).build())
                            .build());
            if (CollUtil.isNotEmpty(map)) {
                builder.putAllData(map);
            }
            return builder.build();
        }

        /**
         * 多播消息实例
         *
         * @param tokens      设备注册令牌的集合
         * @param title       通知的标题
         * @param body        通知正文
         * @param clickAction 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
         * @param map         将给定映射中的所有键值对作为数据字段添加到消息中。任何键或值都不能为空
         * @param iosBadge    设置IOS中与消息一起显示的徽章。 设置为 0 可删除徽章。 设置为null徽章将保持不变
         * @return 多播消息
         */
        private static MulticastMessage buildMulticastMessage(Collection<String> tokens, String title, String body, String clickAction, Map<String, String> map, Integer iosBadge) {
            MulticastMessage.Builder builder = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .setNotification(AndroidNotification.builder()
                                    .setSound("default")
                                    .setClickAction(clickAction)
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .putHeader("apns-priority", "10")
                            .setAps(Aps.builder().setSound("default").setCategory(clickAction).setBadge(iosBadge).build())
                            .build());
            if (CollUtil.isNotEmpty(map)) {
                builder.putAllData(map);
            }
            return builder.build();
        }

    }

}
