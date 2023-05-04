package com.takeshi.extra.sms;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ServiceLoaderUtil;

/**
 * SmsFactory
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class SmsFactory {

    /**
     * 根据用户配置的短信平台，自动创建对应的对象 <br/>
     * 获得的是单例的SmsInterface
     *
     * @return SmsInterface
     */
    public static SmsInterface get() {
        return Singleton.get(SmsInterface.class.getName(), SmsFactory::create);
    }

    /**
     * 根据用户配置的短信平台，自动创建对应的对象 <br/>
     * 推荐创建的单例使用，此方法每次调用会返回新的对象
     *
     * @return SmsInterface
     */
    public static SmsInterface create() {
        final SmsInterface smsInterface = ServiceLoaderUtil.loadFirstAvailable(SmsInterface.class);
        if (null != smsInterface) {
            return smsInterface;
        }
        throw new RuntimeException("No sms found ! Please add one of sms yml/jar to your project !");
    }

}
