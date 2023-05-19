package com.takeshi.extra.sms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.StaticLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.properties.SmsBroadcastProperties;
import com.takeshi.util.AmazonS3Util;
import com.takeshi.util.GsonUtil;
import net.dreamlu.mica.auto.annotation.AutoService;

import java.util.HashMap;
import java.util.Map;

/**
 * SmsBroadcastImpl
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoService(SmsInterface.class)
public class SmsBroadcastImpl implements SmsInterface {

    final String URL = "https://api.smsbroadcast.com.au/api-adv.php";
    static String userName;
    static String password;
    static String from;

    /**
     * 构造函数
     */
    public SmsBroadcastImpl() {
        SmsBroadcastProperties smsBroadcast = StaticConfig.takeshiProperties.getSmsBroadcast();
        JsonNode jsonNode = new ObjectMapper().valueToTree(AmazonS3Util.SECRET);
        userName = StrUtil.isBlank(smsBroadcast.getUserNameSecrets()) ? smsBroadcast.getUserName() : jsonNode.get(smsBroadcast.getUserNameSecrets()).asText();
        password = StrUtil.isBlank(smsBroadcast.getPasswordSecrets()) ? smsBroadcast.getPassword() : jsonNode.get(smsBroadcast.getPasswordSecrets()).asText();
        from = StrUtil.isBlank(smsBroadcast.getFromSecrets()) ? smsBroadcast.getFrom() : jsonNode.get(smsBroadcast.getFromSecrets()).asText();
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
            Map<String, Object> map = new HashMap<>(18);
            map.put("username", userName);
            map.put("password", password);
            map.put("from", from);
            map.put("to", countryCode + number);
            map.put("message", message);
            map.put("delay", "0");
            map.put("maxsplit", String.valueOf((StrUtil.length(message) / 160) + 1));
            map.put("Content-Type", "UTF-8");
            StaticLog.info("SmsBroadcastImpl.sendMessage --> map: {}", GsonUtil.toJson(map));
            String result = HttpUtil.post(URL, map);
            StaticLog.info("SmsBroadcastImpl.sendMessage --> result: {}", result);
        }
    }

}
