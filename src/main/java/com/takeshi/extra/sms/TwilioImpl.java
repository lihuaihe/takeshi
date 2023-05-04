package com.takeshi.extra.sms;

import cn.hutool.log.StaticLog;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.properties.TwilioProperties;
import com.takeshi.util.GsonUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import net.dreamlu.mica.auto.annotation.AutoService;

/**
 * TwilioImpl
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoService(SmsInterface.class)
public class TwilioImpl implements SmsInterface {

    static String messagingServiceSid;

    /**
     * 构造函数
     */
    public TwilioImpl() {
        TwilioProperties twilio = StaticConfig.takeshiProperties.getTwilio();
        messagingServiceSid = twilio.getMessagingServiceSid();
        Twilio.init(twilio.getAccountSid(), twilio.getAuthToken());
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
            StaticLog.info("TwilioImpl.sendMessage --> msg: {}", GsonUtil.toJson(msg));
        }
    }

}
