package com.takeshi.extra.sms;

/**
 * SmsUtil
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class SmsUtil {

    /**
     * 根据配置自动使用对应的短信平台发送短信<br/>
     * yml配置twilio或smsBroadcast
     *
     * @param send        是否发送
     * @param countryCode 区号
     * @param number      号码
     * @param message     消息内容
     */
    public static void sendMessage(boolean send, String countryCode, String number, String message) {
        SmsFactory.get().sendMessage(send, countryCode, number, message);
    }

}
