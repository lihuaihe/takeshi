package com.takeshi.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.impl.CollectionConverter;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.AlgorithmUtil;
import cn.hutool.jwt.signers.EllipticCurveJWTSigner;
import cn.hutool.jwt.signers.JWTSigner;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.satoken.TakeshiSaSignTemplate;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.exception.TakeshiException;
import com.takeshi.mybatisplus.ColumnResolverWrapper;
import com.takeshi.pojo.bo.GeoPointBO;
import com.takeshi.pojo.bo.RetBO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.tika.Tika;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

/**
 * 工具类
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class TakeshiUtil {

    private static final String LOCAL_IP = "0:0:0:0:0:0:0:1";

    /**
     * 构造函数
     */
    private TakeshiUtil() {
    }

    /**
     * 获取单例的模版引擎，默认模版路径在resources/templates目录下
     *
     * @return TemplateEngine
     */
    public static TemplateEngine getTemplateEngine() {
        return Singleton.get(TemplateEngine.class.getSimpleName(), () -> TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH)));
    }

    /**
     * 获取单例的Tika对象
     *
     * @return Tika
     */
    public static Tika getTika() {
        return Singleton.get(Tika.class.getSimpleName(), Tika::new);
    }

    /**
     * 解析lambda方法名转成属性名
     *
     * @param func func
     * @param <T>  T
     * @return String
     */
    public static <T> String lambdaExtract(SFunction<T, ?> func) {
        return PropertyNamer.methodToProperty(LambdaUtils.extract(func).getImplMethodName());
    }

    /**
     * 获取本机IP
     *
     * @return 本机IP
     */
    public static String getLocalhostStr() {
        try {
            return Collections.list(NetworkInterface.getNetworkInterfaces())
                              .stream()
                              .flatMap(networkInterface -> Collections.list(networkInterface.getInetAddresses()).stream())
                              .filter(inetAddress -> !inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress())
                              .map(InetAddress::getHostAddress)
                              .findFirst()
                              .orElse(InetAddress.getLocalHost().getHostAddress());
        } catch (SocketException | UnknownHostException e) {
            return "Unknown Host";
        }
    }

    /**
     * 获取客户端IP
     *
     * @param request request
     * @return 客户端IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String clientIp = JakartaServletUtil.getClientIP(request);
        return StrUtil.equals(LOCAL_IP, clientIp) ? getLocalhostStr() : clientIp;
    }

    /**
     * 通过IP获取真实地址
     *
     * @param ip ip
     * @return 真实地址
     */
    public static String getRealAddressByIp(String ip) {
        try {
            HashMap<String, Object> map = new HashMap<>(6);
            map.put("ip", ip);
            map.put("json", true);
            return JSONUtil.parseObj(HttpUtil.get("https://whois.pconline.com.cn/ipJson.jsp", map)).getStr("addr");
        } catch (Exception e) {
            log.error("TakeshiUtil.getRealAddressByIp --> e: ", e);
            return null;
        }
    }

    /**
     * 使用Sa-token的sign逻辑对参数做MD5签名
     *
     * @return 签名后的值
     */
    public static String signParams() {
        return ((TakeshiSaSignTemplate) SaManager.getSaSignTemplate()).createSign(SaHolder.getRequest());
    }

    /**
     * 将JSONArray字符串转换为Bean的List，默认为ArrayList
     *
     * @param jsonArray     JSONArray字符串
     * @param typeReference List中元素类型
     * @param <T>           Bean类型
     * @return list
     */
    public static <T> List<T> toList(String jsonArray, TypeReference<T> typeReference) {
        return toList(JSONUtil.parseArray(jsonArray), typeReference);
    }

    /**
     * 将JSONArray字符串转换为Bean的集合
     *
     * @param jsonArray      JSONArray字符串
     * @param typeReference  集合中元素类型
     * @param collectionType 集合类型（例如：ArrayList.class）
     * @param <T>            Bean类型
     * @return list
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(String jsonArray, TypeReference<T> typeReference, Class<?> collectionType) {
        return (List<T>) toList(JSONUtil.parseArray(jsonArray), typeReference, collectionType);
    }

    /**
     * 将JSONArray转换为Bean的List，默认为ArrayList
     *
     * @param jsonArray     {@link JSONArray}
     * @param typeReference List中元素类型
     * @param <T>           Bean类型
     * @return list
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(JSONArray jsonArray, TypeReference<T> typeReference) {
        return (List<T>) toList(jsonArray, typeReference, ArrayList.class);
    }

    /**
     * 将JSONArray转换为Bean的集合
     *
     * @param jsonArray      {@link JSONArray}
     * @param typeReference  集合中元素类型
     * @param collectionType 集合类型（例如：ArrayList.class）
     * @param <T>            Bean类型
     * @return list
     */
    public static <T> Collection<?> toList(JSONArray jsonArray, TypeReference<T> typeReference,
                                           Class<?> collectionType) {
        return new CollectionConverter(collectionType, typeReference).convert(jsonArray, null);
    }

    /**
     * 获得指定目录下所有文件名<br>
     * 不会扫描子目录
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return 文件名列表
     * @throws IORuntimeException IO异常
     */
    public static List<String> listFileNames(String path) throws IORuntimeException {
        if (StrUtil.isBlank(path)) {
            return new ArrayList<>();
        }
        final URL url = ResourceUtil.getResource(path);
        if (URLUtil.isJarURL(url)) {
            // jar文件
            return ZipUtil.listFileNames(URLUtil.getJarFile(url), path);
        }
        // 普通目录
        final List<String> paths = new ArrayList<>();
        final File[] files = FileUtil.ls(path);
        for (File file : files) {
            if (file.isFile()) {
                paths.add(file.getAbsolutePath());
            }
        }
        return paths;
    }

    /**
     * 获取ColumnResolverWrapper
     *
     * @param entityClass 实体类class
     * @param <T>         T
     * @return ColumnResolverWrapper
     */
    public static <T> ColumnResolverWrapper<T> columnResolver(Class<T> entityClass) {
        return new ColumnResolverWrapper<>(entityClass);
    }

    /**
     * 解析lambda获取属性名
     *
     * @param func func
     * @param <T>  T
     * @return 属性名
     */
    public static <T> String getPropertyName(SFunction<T, ?> func) {
        return PropertyNamer.methodToProperty(LambdaUtils.extract(func).getImplMethodName());
    }

    /**
     * 解析lambda获取字段名
     *
     * @param func func
     * @param <T>  T
     * @return 字段名
     */
    public static <T> String getColumnName(SFunction<T, ?> func) {
        return StrUtil.toUnderlineCase(PropertyNamer.methodToProperty(LambdaUtils.extract(func).getImplMethodName()));
    }

    /**
     * 尝试解决该消息。如果未找到消息，则返回默认消息
     *
     * @param message 要查找的消息代码，例如“calculator.noRateSet”。鼓励 MessageSource 用户将消息名称基于合格的类或包名称，避免潜在的冲突并确保最大程度的清晰度
     * @param args    将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     * @return 消息
     */
    public static String formatMessage(String message, Object... args) {
        MessageSource messageSource;
        if (SpringMVCUtil.isWeb()) {
            messageSource = SpringUtil.getBean(MessageSource.class);
        } else {
            ReloadableResourceBundleMessageSource resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
            resourceBundleMessageSource.setBasenames("ValidationMessages", "takeshi-i18n/messages");
            resourceBundleMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
            messageSource = resourceBundleMessageSource;
        }
        message = StrUtil.strip(message, StrUtil.DELIM_START, StrUtil.DELIM_END);
        return messageSource.getMessage(message, args, message, LocaleContextHolder.getLocale());
    }

    /**
     * 比较大小，参数1 &gt; 参数2 返回true
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否大于
     */
    public static boolean isGreater(Long num1, Long num2) {
        Assert.notNull(num1);
        Assert.notNull(num2);
        return num1.compareTo(num2) > 0;
    }

    /**
     * 比较大小，参数1 &gt;= 参数2 返回true
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否大于等于
     */
    public static boolean isGreaterOrEqual(Long num1, Long num2) {
        Assert.notNull(num1);
        Assert.notNull(num2);
        return num1.compareTo(num2) >= 0;
    }

    /**
     * 比较大小，参数1 &lt; 参数2 返回true
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否小于
     */
    public static boolean isLess(Long num1, Long num2) {
        Assert.notNull(num1);
        Assert.notNull(num2);
        return num1.compareTo(num2) < 0;
    }

    /**
     * 比较大小，参数1&lt;=参数2 返回true
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否小于等于
     */
    public static boolean isLessOrEqual(Long num1, Long num2) {
        Assert.notNull(num1);
        Assert.notNull(num2);
        return num1.compareTo(num2) <= 0;
    }

    /**
     * 比较大小，值相等 返回true
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 是否相等
     */
    public static boolean equals(Long num1, Long num2) {
        if (ObjUtil.hasNull(num1, num2)) {
            return false;
        }
        return num1.compareTo(num2) == 0;
    }

    /**
     * 获取某个月的开始时间戳
     *
     * @param yearMonth yearMonth
     * @return 时间戳
     */
    public static Long firstDayOfMonth(YearMonth yearMonth) {
        return LocalDateTimeUtil.toEpochMilli(yearMonth.atDay(1).atTime(LocalTime.MIN));
    }

    /**
     * 获取某个月的结束时间戳
     *
     * @param yearMonth yearMonth
     * @return 时间戳
     */
    public static Long lastDayOfMonth(YearMonth yearMonth) {
        return LocalDateTimeUtil.toEpochMilli(yearMonth.atEndOfMonth().atTime(LocalTime.MAX));
    }

    /**
     * 货币元转分
     *
     * @param decimal 元
     * @return 分
     */
    public static BigDecimal currencyToCent(BigDecimal decimal) {
        return ObjUtil.defaultIfNull(decimal, BigDecimal.ZERO).movePointRight(2);
    }

    /**
     * 货币分转元
     *
     * @param decimal 分
     * @return 元
     */
    public static BigDecimal currencyToYuan(BigDecimal decimal) {
        // 此处的decimal值单位应该是分，一般分转成元，分应该没有小数点
        return ObjUtil.defaultIfNull(decimal, BigDecimal.ZERO).movePointLeft(2).setScale(2, RoundingMode.UNNECESSARY);
    }

    /**
     * 使用MySQL中的地球半径，单位：米
     */
    private static final double EARTH_RADIUS = 6370986D;

    /**
     * 判断给定的经纬度坐标是否在指定半径范围内。
     *
     * @param sourcePoint 原始经纬度坐标
     * @param targetPoint 目标经纬度坐标
     * @param radius      半径范围（单位：米）
     * @return 如果目标经纬度坐标在指定半径范围内，则返回 true；否则返回 false。
     * @throws IllegalArgumentException 如果任一参数为 null 或经纬度值非法。
     */
    public static boolean isCoordinatesWithinRadius(GeoPointBO sourcePoint, GeoPointBO targetPoint, double radius) {
        if (sourcePoint == null || targetPoint == null) {
            throw new IllegalArgumentException("Coordinate points cannot be null");
        }
        double dLon = (targetPoint.getLon() - sourcePoint.getLon()) * Math.PI / 180;
        double dLat = (targetPoint.getLat() - sourcePoint.getLat()) * Math.PI / 180;
        double sourceLat = sourcePoint.getLat() * Math.PI / 180;
        double targetLat = targetPoint.getLat() * Math.PI / 180;
        // Haversine 公式
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(sourceLat) * Math.cos(targetLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        // 返回两点之间的距离，单位为米
        double distance = EARTH_RADIUS * c;
        return distance <= radius;
    }

    /**
     * 判断给定的经纬度坐标是否在指定半径范围内。不再范围内则抛出异常
     *
     * @param sourcePoint 原始经纬度坐标
     * @param targetPoint 目标经纬度坐标
     * @param radius      半径范围（单位：米）
     * @param message     消息
     */
    public static void coordinatesWithinRadius(GeoPointBO sourcePoint, GeoPointBO targetPoint, double radius, String message) {
        if (!isCoordinatesWithinRadius(sourcePoint, targetPoint, radius)) {
            throw new TakeshiException(message);
        }
    }

    /**
     * 判断给定的经纬度坐标是否在指定半径范围内。不再范围内则抛出异常
     *
     * @param sourcePoint 原始经纬度坐标
     * @param targetPoint 目标经纬度坐标
     * @param radius      半径范围（单位：米）
     * @param retBO       消息
     * @param args        将为消息中的参数填充的参数数组（参数在消息中类似于“{0}”、“{1,date}”、“{2,time}”），如果没有则为null
     */
    public static void coordinatesWithinRadius(GeoPointBO sourcePoint, GeoPointBO targetPoint, double radius, RetBO retBO, Object... args) {
        if (!isCoordinatesWithinRadius(sourcePoint, targetPoint, radius)) {
            throw new TakeshiException(retBO, args);
        }
    }

    /**
     * 校验Apple订阅通知回调中的负载内容
     *
     * @param jwt 负载内容解析出来的JWT
     * @return boolean
     */
    @SneakyThrows
    public static boolean verifyAppleNotifyPayload(JWT jwt) {
        Certificate appleRootCaG3 = Singleton.get("AppleRootCA-G3Cer", () -> KeyUtil.readX509Certificate(IoUtil.toStream(HttpUtil.downloadBytes("https://www.apple.com/certificateauthority/AppleRootCA-G3.cer"))));
        List<String> x5c = jwt.getHeaders().getBeanList("x5c", String.class);
        Certificate jwtSignCa = KeyUtil.readX509Certificate(IoUtil.toStream(cn.hutool.core.codec.Base64.decode(x5c.get(0))));
        Certificate jwtRootCa = KeyUtil.readX509Certificate(IoUtil.toStream(Base64.decode(x5c.get(2))));
        // 验证是否是苹果颁发的证书
        appleRootCaG3.verify(jwtRootCa.getPublicKey());
        PublicKey publicKey = jwtSignCa.getPublicKey();
        JWTSigner jwtSigner = new EllipticCurveJWTSigner(AlgorithmUtil.getAlgorithm(jwt.getAlgorithm()), publicKey);
        jwt.setSigner(jwtSigner);
        boolean verify = jwt.verify();
        JSONObject data = jwt.getPayloads().getJSONObject("data");
        if (ObjUtil.isNotNull(data)) {
            String signedTransactionInfo = data.getStr("signedTransactionInfo");
            if (StrUtil.isNotBlank(signedTransactionInfo) && verify) {
                verify = JWTUtil.parseToken(signedTransactionInfo).setSigner(jwtSigner).verify();
            }
            String signedRenewalInfo = data.getStr("signedRenewalInfo");
            if (StrUtil.isNotBlank(signedRenewalInfo) && verify) {
                verify = JWTUtil.parseToken(signedRenewalInfo).setSigner(jwtSigner).verify();
            }
        }
        return verify;
    }

    /**
     * 截取纳秒部份，只保留前3位
     *
     * @param nano 纳秒
     * @return 截取后的值
     */
    public static int interceptNano(int nano) {
        int div = 100_000_000;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            int digit = nano / div;
            buf.append((char) (digit + '0'));
            nano = nano - (digit * div);
            div = div / 10;
        }
        return Integer.parseInt(buf.toString()) * 1_000_000;
    }

    /**
     * 私钥解密
     *
     * @param data 数据
     * @return 解密后的数据
     */
    public static String decryptByPrivateKey(String data) {
        try {
            return StaticConfig.rsa.decryptStr(data, KeyType.PrivateKey);
        } catch (Exception e) {
            throw new TakeshiException(TakeshiCode.PARAMETER_ERROR);
        }
    }

}
