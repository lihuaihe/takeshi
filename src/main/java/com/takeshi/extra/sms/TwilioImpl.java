package com.takeshi.extra.sms;

import cn.hutool.core.util.StrUtil;
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

    private static final Logger log = LoggerFactory.getLogger(TwilioImpl.class);

    static String messagingServiceSid;

    /**
     * 构造函数
     *
     * @param twilio twilio
     */
    public TwilioImpl(final TwilioProperties twilio) {
        JsonNode jsonNode = AwsSecretsManagerUtil.getSecret();
        String accountSid = StrUtil.isBlank(twilio.getAccountSidSecrets()) ? twilio.getAccountSid() : jsonNode.get(twilio.getAccountSidSecrets()).asText();
        String authToken = StrUtil.isBlank(twilio.getAuthTokenSecrets()) ? twilio.getAuthToken() : jsonNode.get(twilio.getAuthTokenSecrets()).asText();
        Twilio.init(accountSid, authToken);
        messagingServiceSid = StrUtil.isBlank(twilio.getMessagingServiceSidSecrets()) ? twilio.getMessagingServiceSid() : jsonNode.get(twilio.getMessagingServiceSidSecrets()).asText();
    }

    /**
     * 根据配置自动使用对应的短信平台发送短信<br/>
     * yml配置twilio或smsBroadcast
     *
     * @param send        是否发送
     * @param countryCode 区号
     * @param number      号码
     * @param message     消息内容
     */
    @Override
    public void sendMessage(boolean send, String countryCode, String number, String message) {
        if (send) {
            Message msg = Message.creator(
                                         new PhoneNumber(countryCode + number),
                                         messagingServiceSid,
                                         message)
                                 .create();
            log.info("TwilioImpl.sendMessage --> msg: {}", GsonUtil.toJson(msg));
        }
    }

}
