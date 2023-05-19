package com.takeshi.util;

import cn.hutool.core.convert.impl.CollectionConverter;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.Platform;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.takeshi.config.StaticConfig;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.exception.TakeshiException;
import com.takeshi.mybatisplus.ColumnResolverWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
     * 获取单例的模版引擎
     *
     * @return TemplateEngine
     */
    public static TemplateEngine getTemplateEngine() {
        return Singleton.get(TemplateEngine.class.getName(), () -> TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH)));
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
     * 判断是ios还是android
     *
     * @param request request
     * @return 1: ios, 2: android
     */
    public static int getDeviceType(HttpServletRequest request) {
        Platform platform = UserAgentUtil.parse(request.getHeader(Header.USER_AGENT.getValue())).getPlatform();
        if (platform.isIos()) {
            return 1;
        }
        if (platform.isAndroid()) {
            return 2;
        }
        throw new TakeshiException(TakeshiCode.USERAGENT_ERROR);
    }

    /**
     * 获取客户端IP
     *
     * @param request request
     * @return 客户端IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String clientIp = JakartaServletUtil.getClientIP(request);
        if (StrUtil.equals(LOCAL_IP, clientIp)) {
            return NetUtil.getLocalhostStr();
        }
        return clientIp;
    }

    /**
     * 通过IP获取真实地址
     *
     * @param ip ip
     * @return 真实地址
     */
    public static String getRealAddressByIp(String ip) {
        HashMap<String, Object> map = new HashMap<>(6);
        map.put("ip", ip);
        map.put("json", true);
        return JSONUtil.parseObj(HttpUtil.get("https://whois.pconline.com.cn/ipJson.jsp", map)).getStr("addr");
    }

    /**
     * 将URL的参数和body参数合并，path参数不参与，如果有上传文件，则，key使用参数名，value使用文件流进行MD5加密
     *
     * @param request request
     * @return 按key排序后的map
     */
    public static Map<String, Object> getAllParams(HttpServletRequest request) {
        // 获取参数
        Map<String, String> paramMap = JakartaServletUtil.getParamMap(request);
        Map<String, Object> map = new HashMap<>(paramMap);
        if (JakartaServletUtil.isGetMethod(request)) {
            return map;
        }
        return map;
    }

    /**
     * 对参数做MD5签名<br>
     * 参数签名为对Map参数按照key的顺序排序后拼接为字符串，然后根据提供的签名算法生成签名字符串<br>
     * 拼接后的字符串键值对之间无符号，键值对之间无符号，忽略null值
     *
     * @param request     request
     * @param otherParams 其它附加参数字符串（例如密钥）
     * @return 签名后的值
     */
    public static String signParams(HttpServletRequest request, String... otherParams) {
        Map<String, Object> allParams = getAllParams(request);
        return SecureUtil.signParamsMd5(allParams, otherParams);
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
     * 获得指定目录下所有文件<br>
     * 不会扫描子目录
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return 文件路径列表
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
                paths.add(file.getName());
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
        MessageSource messageSource = Optional.ofNullable(StaticConfig.messageSource)
                .orElseGet(() -> {
                    ReloadableResourceBundleMessageSource resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
                    resourceBundleMessageSource.setBasenames("i18n/messages", "ValidationMessages");
                    resourceBundleMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
                    return resourceBundleMessageSource;
                });
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

}
