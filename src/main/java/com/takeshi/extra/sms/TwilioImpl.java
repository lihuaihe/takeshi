package com.takeshi.extra.sms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.takeshi.config.properties.TwilioProperties;
import com.takeshi.util.AwsSecretsManagerUtil;
import com.takeshi.util.GsonUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import net.dreamlu.mica.auto.annotation.AutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TwilioImpl
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoService(SmsInterface.class)
public class TwilioImpl implements SmsInterface {

    /**
     * 此处不可以使用@Slf4j注解，否则会无法通过ServiceLoaderUtil.loadFirstAvailable获取到
     */
    private static final Logger log = LoggerFactory.getLogger(TwilioImpl.class);

    static String messagingServiceSid;

    /**
     * 构造函数
     */
    public TwilioImpl() {
        TwilioProperties twilio = SpringUtil.getBean(TwilioProperties.class);
        JsonNode jsonNode = AwsSecretsManagerUtil.getSecret();
        String accountSid = StrUtil.blankToDefault(jsonNode.path(twilio.getAccountSidSecrets()).asText(), twilio.getAccountSid());
        String authToken = StrUtil.blankToDefault(jsonNode.path(twilio.getAuthTokenSecrets()).asText(), twilio.getAuthToken());
        Twilio.init(accountSid, authToken);
        messagingServiceSid = StrUtil.blankToDefault(jsonNode.path(twilio.getMessagingServiceSidSecrets()).asText(), twilio.getMessagingServiceSid());
    }

    /**
     * 根据配置自动使用对应的短信平台发送短信<br/>
     * yml配置twilio或smsBroadcast
     *
     * @param send        是否发送
     * @param phoneNumber 带区号的手机号码
     * @param message     消息内容
     */
    @Override
    public void sendMessage(boolean send, String phoneNumber, String message) {
        if (send) {
            Message msg = Message.creator(
                                         new PhoneNumber(phoneNumber),
                                         messagingServiceSid,
                                         message)
                                 .create();
            log.info("TwilioImpl.sendMessage --> msg: {}", GsonUtil.toJson(msg));
        }
    }

}
