package com.takeshi.pojo.bo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.takeshi.annotation.TakeshiLog;
import com.takeshi.config.StaticConfig;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.pojo.basic.AbstractBasicSerializable;
import com.takeshi.util.GsonUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.*;

/**
 * ParamBO
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema
public class ParamBO extends AbstractBasicSerializable {

    /**
     * 客户端IP
     */
    @Schema(description = "客户端IP")
    private String clientIp;

    /**
     * 请求的IP对应的地址
     */
    @Deprecated
    @Schema(description = "请求的IP对应的地址")
    private String clientIpAddress;

    /**
     * 登录的用户ID
     */
    @Schema(description = "登录的用户ID")
    private Object loginId;

    /**
     * SaSession里存储的数据
     */
    @Schema(description = "SaSession里存储的数据")
    private Map<String, Object> saSessionDataMap;

    /**
     * 请求URL地址
     */
    @Schema(description = "请求URL地址")
    private String requestUrl;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式")
    private String httpMethod;

    /**
     * 请求的方法，带包名类名的完整的方法名
     */
    @Schema(description = "请求的方法，带包名类名的完整的方法名")
    private String methodName;

    /**
     * TakeshiLog注解
     */
    @Schema(description = "TakeshiLog注解")
    private TakeshiLog takeshiLog;

    /**
     * header参数
     */
    @Schema(description = "header参数")
    private CaseInsensitiveMap<String, String> headerParam;

    /**
     * URL参数
     */
    @Schema(description = "URL参数")
    private Map<String, String> urlParam;

    /**
     * 上传的文件信息，value里存放MD5加密的文件流
     */
    @Schema(description = "上传的文件信息，value里存放MD5加密的文件流")
    private transient Map<String, List<String>> multipartData;

    /**
     * 签名需要用到的上传的文件摘要信息，value里存放文件名+文件大小
     */
    @Schema(description = "签名需要用到的上传的文件摘要信息，value里存放文件名+文件大小")
    private Map<String, String> multipart;

    /**
     * body的JsonObject内容
     */
    @Schema(description = "body的JsonObject内容")
    private Map<String, Object> bodyObject;

    /**
     * body的非JsonObject内容
     */
    @Schema(description = "body的非JsonObject内容")
    private Object bodyOther;

    /**
     * 设置header参数Map
     *
     * @param headerParam headerParam
     */
    public void setHeaderParam(CaseInsensitiveMap<String, String> headerParam) {
        this.headerParam = MapUtil.isEmpty(headerParam) ? null : headerParam;
    }

    /**
     * 设置url参数Map
     *
     * @param urlParam urlParam
     */
    public void setUrlParam(Map<String, String> urlParam) {
        this.urlParam = MapUtil.isEmpty(urlParam) ? null : urlParam;
    }

    /**
     * 设置body值
     *
     * @param body body
     */
    @SneakyThrows
    public void setBody(InputStream body) {
        ObjectMapper objectMapper = StaticConfig.objectMapper;
        JsonNode jsonNode = objectMapper.readTree(body);
        if (jsonNode.isNull()) {
            return;
        }
        if (jsonNode.isObject()) {
            Map<String, Object> map = objectMapper.convertValue(jsonNode, new TypeReference<>() {
            });
            this.bodyObject = MapUtil.isEmpty(map) ? null : map;
        } else if (jsonNode.isArray()) {
            Collection<Object> collection = objectMapper.convertValue(jsonNode, new TypeReference<>() {
            });
            this.bodyOther = CollUtil.isEmpty(collection) ? null : collection;
        } else if (jsonNode.isTextual()) {
            this.bodyOther = jsonNode.textValue();
        } else if (jsonNode.isNumber()) {
            this.bodyOther = jsonNode.numberValue();
        } else if (jsonNode.isBoolean()) {
            this.bodyOther = jsonNode.booleanValue();
        } else {
            this.bodyOther = jsonNode.toString();
        }
    }

    /**
     * 获取一些请求开始后需要展示在日志里的信息
     *
     * @return String
     */
    public String filterInfo() {
        Map<String, Object> map = new LinkedHashMap<>(20);
        map.put("Request Address", StrUtil.builder(StrUtil.BRACKET_START, this.httpMethod, StrUtil.BRACKET_END, this.getRequestUrl()));
        map.put("Requesting UserId", this.loginId);
        map.put("Requesting SaSessionData", MapUtil.defaultIfEmpty(this.saSessionDataMap, null));
        map.put("Request IP", this.clientIp);
        map.put("Request UserAgent", this.headerParam.get(Header.USER_AGENT.getValue()));
        map.put("Header GeoPoint", this.headerParam.get(TakeshiConstants.GEO_POINT_NAME));
        map.put("Header Timezone", this.headerParam.get(TakeshiConstants.TIMEZONE_NAME));
        map.put("Header Timestamp", this.headerParam.get(TakeshiConstants.TIMESTAMP_NAME));
        map.put("Header Nonce", this.headerParam.get(TakeshiConstants.NONCE_NAME));
        return GsonUtil.toJson(map);
    }

    /**
     * 获取请求的参数的ObjectNode
     *
     * @param exclusionFieldName 排除的字段名，会忽略大小写
     * @return ObjectNode
     */
    public ObjectNode getParamObjectNode(String... exclusionFieldName) {
        ObjectNode objectNode = StaticConfig.objectMapper.createObjectNode();
        if (CollUtil.isNotEmpty(this.urlParam)) {
            objectNode.putPOJO("urlParam", this.urlParam);
        }
        if (CollUtil.isNotEmpty(this.multipart)) {
            objectNode.putPOJO("multipart", this.multipart);
        }
        if (CollUtil.isNotEmpty(this.bodyObject)) {
            objectNode.putPOJO("bodyObject", this.bodyObject);
        }
        if (ObjUtil.isNotEmpty(this.bodyOther)) {
            objectNode.putPOJO("bodyOther", this.bodyOther);
        }
        if (ArrayUtil.isNotEmpty(exclusionFieldName)) {
            for (String fieldName : exclusionFieldName) {
                objectNode.findParents(fieldName).forEach(item -> ((ObjectNode) item).remove(fieldName));
            }
        }
        return objectNode;
    }

    /**
     * 获取所有参数的Map，以用来进行签名，bodyOther将会拼接到其他参数的后面，但是在signatureKey之前
     *
     * @return Map
     */
    public Map<String, Object> getParamMap() {
        Map<String, Object> map = new HashMap<>(12);
        if (MapUtil.isNotEmpty(this.urlParam)) {
            map.putAll(this.urlParam);
        }
        if (MapUtil.isNotEmpty(this.multipart)) {
            map.putAll(this.multipart);
        }
        if (MapUtil.isNotEmpty(this.bodyObject)) {
            map.putAll(this.bodyObject);
        }
        return map;
    }

}
