package com.takeshi.extra.sms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.takeshi.config.properties.SmsBroadcastProperties;
import com.takeshi.util.AwsSecretsManagerUtil;
import com.takeshi.util.GsonUtil;
import net.dreamlu.mica.auto.annotation.AutoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * SmsBroadcastImpl
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoService(SmsInterface.class)
public class SmsBroadcastImpl implements SmsInterface {

    /**
     * 此处不可以使用@Slf4j注解，否则会无法通过ServiceLoaderUtil.loadFirstAvailable获取到
     */
    private static final Logger log = LoggerFactory.getLogger(SmsBroadcastImpl.class);

    final String URL = "https://api.smsbroadcast.com.au/api-adv.php";

    static String userName;

    static String password;

    static String from;

    /**
     * 构造函数
     */
    public SmsBroadcastImpl() {
        SmsBroadcastProperties smsBroadcast = SpringUtil.getBean("SmsBroadcastProperties");
        JsonNode jsonNode = AwsSecretsManagerUtil.getSecret();
        userName = StrUtil.isBlank(smsBroadcast.getUserNameSecrets()) ? smsBroadcast.getUserName() : jsonNode.get(smsBroadcast.getUserNameSecrets()).asText();
        password = StrUtil.isBlank(smsBroadcast.getPasswordSecrets()) ? smsBroadcast.getPassword() : jsonNode.get(smsBroadcast.getPasswordSecrets()).asText();
        from = StrUtil.isBlank(smsBroadcast.getFromSecrets()) ? smsBroadcast.getFrom() : jsonNode.get(smsBroadcast.getFromSecrets()).asText();
        if (StrUtil.hasBlank(userName, password, from)) {
            throw new IllegalArgumentException("SmsBroadcast required parameter is empty, Please set the yml value of SmsBroadcast!");
        }
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
            log.info("SmsBroadcastImpl.sendMessage --> map: {}", GsonUtil.toJson(map));
            String result = HttpUtil.post(URL, map);
            log.info("SmsBroadcastImpl.sendMessage --> result: {}", result);
        }
    }

}
