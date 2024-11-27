package com.takeshi.constants;

import cn.dev33.satoken.context.SaHolder;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.google.gson.JsonSyntaxException;
import com.takeshi.pojo.bo.GeoPointBO;
import com.takeshi.util.GsonUtil;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

/**
 * RequestConstants
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface RequestConstants {

    /**
     * 日志追踪ID
     */
    String TRACE_ID = "traceId";

    /**
     * 登录用户ID
     */
    String LOGIN_ID = "loginId";

    /**
     * 调用接口方法名
     */
    String METHOD_NAME = "methodName";

    /**
     * TakeshiLog注解
     */
    String TAKESHI_LOG = "takeshiLog";

    /**
     * 接口请求的参数
     */
    String PARAM_OBJECT_VALUE = "paramObjectValue";

    /**
     * 客户端IP
     */
    String CLIENT_IP = "clientIp";

    /**
     * header
     */
    interface Header {

        /**
         * 调用接口header里面传的User-Agent字段
         */
        String USER_AGENT = "User-Agent";

        /**
         * 调用接口header里面传的时区字段(Asia/Shanghai)
         */
        String TIMEZONE = "x-timezone";

        /**
         * 调用接口header里面传的经纬度字段{"lon":1.0, "lat":2.0}
         */
        String GEO_POINT = "x-geo-point";

        /**
         * 调用接口header里面传的时间戳字段（毫秒级）
         */
        String TIMESTAMP = "x-timestamp";

        /**
         * 仅一次有效的随机字符串，可以使用用户信息+时间戳+随机数等信息做个哈希值，作为nonce值
         */
        String NONCE = "x-nonce";

        /**
         * 签名参数名
         */
        String SIGN = "x-sign";

        /**
         * 从header里面获取UserAgent，如果没有则抛出异常
         *
         * @return UserAgent
         */
        static UserAgent getUserAgent() {
            String userAgent = SaHolder.getRequest().getHeader(USER_AGENT);
            Assert.notBlank(userAgent, "User-Agent must not be null");
            return UserAgentUtil.parse(userAgent);
        }

        /**
         * 从header里面获取UserAgent，如果没有则返回NULL
         *
         * @return UserAgent
         */
        static UserAgent getUserAgentDefaultNull() {
            String userAgent = SaHolder.getRequest().getHeader(USER_AGENT);
            if (StrUtil.isBlank(userAgent)) {
                return null;
            }
            return UserAgentUtil.parse(userAgent);
        }

        /**
         * 从header里面获取时区，如果没有则抛出异常
         *
         * @return ZoneId
         */
        static ZoneId getTimezone() {
            try {
                String timezone = SaHolder.getRequest().getHeader(TIMEZONE);
                Assert.notBlank(timezone, "Timezone must not be null");
                return ZoneId.of(timezone);
            } catch (ZoneRulesException e) {
                throw new IllegalArgumentException(e.getLocalizedMessage());
            }
        }

        /**
         * 从header里面获取时区，如果没有则返回NULL
         *
         * @return ZoneId
         */
        static ZoneId getTimezoneDefaultNull() {
            String timezone = SaHolder.getRequest().getHeader(TIMEZONE);
            if (StrUtil.isBlank(timezone)) {
                return null;
            }
            return ZoneId.of(timezone);
        }

        /**
         * 从header里面获取经纬度，如果没有则抛出异常
         *
         * @return GeoPointBO
         */
        static GeoPointBO getGeoPoint() {
            try {
                String geoPoint = SaHolder.getRequest().getHeader(GEO_POINT);
                Assert.notBlank(geoPoint, "Geo point must not be null");
                return GsonUtil.fromJson(geoPoint, GeoPointBO.class);
            } catch (IllegalArgumentException | JsonSyntaxException e) {
                throw new IllegalArgumentException("Geo point data format error");
            }
        }

        /**
         * 从header里面获取经纬度，如果没有则返回NULL
         *
         * @return GeoPointBO
         */
        static GeoPointBO getGeoPointDefaultNull() {
            String geoPoint = SaHolder.getRequest().getHeader(GEO_POINT);
            if (StrUtil.isBlank(geoPoint)) {
                return null;
            }
            return GsonUtil.fromJson(geoPoint, GeoPointBO.class);
        }

    }

}
