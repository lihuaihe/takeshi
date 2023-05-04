package com.takeshi.pojo.bo;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.config.StaticConfig;
import com.takeshi.pojo.basic.AbstractBasicSerializable;
import com.takeshi.util.GsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ParamBO
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ParamBO extends AbstractBasicSerializable {

    /**
     * URL参数
     */
    private Map<String, String> urlParam;

    /**
     * 上传的文件信息，value里存放文件名+文件大小
     */
    private transient Map<String, List<String>> multipartData;

    /**
     * 签名需要用到的上传的文件摘要信息
     */
    private Map<String, String> multipart;

    /**
     * body的JsonObject内容
     */
    private Map<String, Object> bodyObject;

    /**
     * body的非JsonObject内容
     */
    private Object bodyOther;

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
            this.bodyObject = objectMapper.convertValue(jsonNode, new TypeReference<>() {
            });
        } else if (jsonNode.isArray()) {
            this.bodyOther = objectMapper.convertValue(jsonNode, new TypeReference<Collection<Object>>() {
            });
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
     * 转成JSON字符串
     *
     * @return JSON字符串
     */
    public String toJsonString() {
        return GsonUtil.toJson(this);
    }

    /**
     * 获取所有参数的Map，以用来进行签名
     *
     * @return Map
     */
    public Map<String, Object> getParamMap() {
        Map<String, Object> map = new HashMap<>(8);
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
