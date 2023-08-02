package com.takeshi.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import com.takeshi.config.StaticConfig;
import com.takeshi.config.properties.MandrillCredentials;
import com.takeshi.exception.TakeshiException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.io.TikaInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * MailUtil
 * <pre>{@code
 * implementation 'com.mandrillapp.wrapper.lutung:lutung:0.0.8'
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class MandrillUtil {

    private static final String REJECTED = "rejected";
    private static final String INVALID = "invalid";

    private static volatile String FROM_EMAIL;
    private static volatile String FROM_NAME;
    private static volatile MandrillApi MANDRILL_API;

    private MandrillMessage message;
    private String subject, fromEmail, fromName, content;
    /**
     * 是否是html文本内容
     */
    private boolean isHtml;
    /**
     * 收件人列表
     */
    private List<MandrillMessage.Recipient> to = new ArrayList<>();
    /**
     * 附件列表
     */
    private List<MandrillMessage.MessageContent> attachments = new ArrayList<>();
    /**
     * 嵌入的图像列表
     */
    private List<MandrillMessage.MessageContent> images = new ArrayList<>();

    static {
        if (ObjUtil.isNull(MANDRILL_API)) {
            synchronized (MandrillUtil.class) {
                if (ObjUtil.isNull(MANDRILL_API)) {
                    try {
                        MandrillCredentials mandrill = StaticConfig.takeshiProperties.getMandrill();
                        JsonNode jsonNode = AmazonS3Util.getSecret();
                        FROM_EMAIL = StrUtil.isBlank(mandrill.getFromEmailSecrets()) ? mandrill.getFromEmail() : jsonNode.get(mandrill.getFromEmailSecrets()).asText();
                        FROM_NAME = StrUtil.isBlank(mandrill.getFromNameSecrets()) ? mandrill.getFromName() : jsonNode.get(mandrill.getFromNameSecrets()).asText();
                        String apiKey = StrUtil.isBlank(mandrill.getApiKeySecrets()) ? mandrill.getApiKey() : jsonNode.get(mandrill.getApiKeySecrets()).asText();
                        MANDRILL_API = new MandrillApi(apiKey);
                        log.info("MandrillUtil.static --> Mandrill Initialization successful");
                    } catch (Exception e) {
                        log.error("MandrillUtil.static --> Mandrill initialization failed, e: ", e);
                    }
                }
            }
        }
    }

    private MandrillUtil() {
    }

    /**
     * 创建一个MandrillMessage对象
     *
     * @return this
     */
    private MandrillUtil message() {
        this.message = new MandrillMessage();
        this.fromEmail = FROM_EMAIL;
        this.fromName = FROM_NAME;
        return this;
    }

    /**
     * 创建一个可以发送邮件的对象
     *
     * @return MandrillUtil
     */
    public static MandrillUtil create() {
        return new MandrillUtil().message();
    }

    /**
     * 设置邮件主题
     *
     * @param subject 主题
     * @return this
     */
    public MandrillUtil subject(String subject) {
        this.subject = subject;
        return this;
    }

    /**
     * 设置发件人信息
     *
     * @param fromEmail 发件人邮箱
     * @param fromName  发件人名称
     * @return this
     */
    public MandrillUtil from(String fromEmail, String fromName) {
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        return this;
    }

    /**
     * 设置正文<br>
     * 正文可以是普通文本也可以是HTML（默认普通文本），可以通过调用{@link #isHtml(boolean)} 设置是否为HTML
     *
     * @param content 正文
     * @return this
     */
    public MandrillUtil content(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置正文
     *
     * @param content 正文内容
     * @param isHtml  是否为HTML
     * @return this
     */
    public MandrillUtil content(String content, boolean isHtml) {
        this.content = content;
        this.isHtml = isHtml;
        return this;
    }

    /**
     * 设置是否是HTML
     *
     * @param isHtml 是否为HTML
     * @return this
     */
    public MandrillUtil isHtml(boolean isHtml) {
        this.isHtml = isHtml;
        return this;
    }

    /**
     * 通过HTML模板与绑定参数融合后返回字符串，设置正文，需要将模版文件放在resources的默认模版路径template目录
     *
     * @param templateName 模板文件名
     * @param bindingMap   绑定的参数，此Map中的参数会替换模板中的变量
     * @return this
     */
    public MandrillUtil generateHtmlContent(String templateName, Map<?, ?> bindingMap) {
        if (StrUtil.isBlank(FileNameUtil.extName(templateName))) {
            templateName += ".html";
        }
        String htmlStr = TakeshiUtil.getTemplateEngine().getTemplate(templateName).render(bindingMap);
        return this.content(htmlStr, true);
    }

    /**
     * 设置收件人信息
     *
     * @param email 收件人邮箱
     * @param name  收件人名称
     * @return this
     */
    public MandrillUtil addRecipient(String email, String name) {
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(email);
        recipient.setName(name);
        this.to.add(recipient);
        return this;
    }

    /**
     * 设置收件人信息
     *
     * @param email 收件人邮箱
     * @param name  收件人名称
     * @param type  收件人类型，可以通过调用{@link MandrillMessage.Recipient.Type}设置是否类型
     * @return this
     */
    public MandrillUtil addRecipient(String email, String name, MandrillMessage.Recipient.Type type) {
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(email);
        recipient.setName(name);
        recipient.setType(type);
        this.to.add(recipient);
        return this;
    }

    /**
     * 设置收件人信息
     *
     * @param recipients 收件人信息
     * @return this
     */
    public MandrillUtil addRecipient(MandrillMessage.Recipient... recipients) {
        this.to.addAll(Arrays.asList(recipients));
        return this;
    }

    /**
     * 设置收件人信息
     *
     * @param list 收件人信息列表
     * @return this
     */
    public MandrillUtil addRecipient(List<MandrillMessage.Recipient> list) {
        this.to.addAll(list);
        return this;
    }

    /**
     * 设置附件
     *
     * @param files 附件列表
     * @return this
     */
    public MandrillUtil addAttachment(File... files) {
        for (File file : files) {
            MandrillMessage.MessageContent messageContent = new MandrillMessage.MessageContent();
            messageContent.setType(FileUtil.getMimeType(file.getName()));
            messageContent.setName(file.getName());
            messageContent.setContent(Base64.encode(file));
            this.attachments.add(messageContent);
        }
        return this;
    }

    /**
     * 使用cid引用设置嵌入的图像，调用此方法会默认设置 isHtml = true
     * <br/>
     * 正文中需要使用 img 标签 src="cid: 名称"
     * <br/>
     * 该名称要与下面代码中的 setName 中的值一值
     *
     * @param files 嵌入的图像文件列表
     * @return this
     */
    @SneakyThrows
    public MandrillUtil addImages(File... files) {
        for (File file : files) {
            MandrillMessage.MessageContent messageContent = new MandrillMessage.MessageContent();
            messageContent.setType(TakeshiUtil.getTika().detect(file));
            messageContent.setName(file.getName());
            messageContent.setContent(Base64.encode(file));
            this.images.add(messageContent);
        }
        return this;
    }

    /**
     * 使用cid引用设置嵌入的图像，调用此方法会默认设置 isHtml = true，读取完毕后关闭流
     * <br/>
     * 正文中需要使用 img 标签 src="cid:名称"
     * <br/>
     * 该名称要与下面代码中的 setName 中的值一值
     *
     * @param inputStream 嵌入的图像文件流
     * @param name        cid的名称
     * @return this
     */
    @SneakyThrows
    public MandrillUtil addImages(InputStream inputStream, String name) {
        try (TikaInputStream tikaInputStream = TikaInputStream.get(inputStream)) {
            MandrillMessage.MessageContent messageContent = new MandrillMessage.MessageContent();
            messageContent.setType(TakeshiUtil.getTika().detect(tikaInputStream));
            messageContent.setName(name);
            messageContent.setContent(Base64.encode(tikaInputStream));
            this.images.add(messageContent);
            return this;
        }
    }

    /**
     * 发送邮件，会抛出异常
     */
    public void sendErr() {
        if (StrUtil.hasBlank(this.subject, this.fromEmail, this.fromName) || CollUtil.isEmpty(this.to)) {
            throw new TakeshiException("MandrillUtil send hasBlank: [subject, fromEmail, fromName, to]");
        }
        this.format();
        MandrillMessageStatus[] result;
        try {
            result = MANDRILL_API.messages().send(this.message, false);
        } catch (MandrillApiError | IOException mandrillApiError) {
            log.error("MandrillUtil.send --> mandrillApiError: ", mandrillApiError);
            throw new TakeshiException(mandrillApiError.getMessage());
        }
        if (ArrayUtil.isEmpty(result)) {
            throw new TakeshiException("MandrillUtil send result is empty");
        }
        if (StrUtil.equalsAny(result[0].getStatus(), REJECTED, INVALID)) {
            throw new TakeshiException(GsonUtil.toJson(result));
        }
    }

    /**
     * 发送邮件，不会抛出异常
     */
    public void send() {
        if (StrUtil.hasBlank(this.subject, this.fromEmail, this.fromName) || CollUtil.isEmpty(this.to)) {
            log.error("MandrillUtil.send --> hasBlank: [subject, fromEmail, fromName, to]");
            return;
        }
        this.format();
        MandrillMessageStatus[] result = new MandrillMessageStatus[0];
        try {
            result = MANDRILL_API.messages().send(this.message, false);
        } catch (MandrillApiError | IOException mandrillApiError) {
            log.error("MandrillUtil.send --> mandrillApiError: ", mandrillApiError);
        }
        if (ArrayUtil.isEmpty(result)) {
            log.error("MandrillUtil.send --> result is empty");
        }
        if (StrUtil.equalsAny(result[0].getStatus(), REJECTED, INVALID)) {
            log.error("MandrillUtil.send --> result: {}", GsonUtil.toJson(result));
        }
    }

    private void format() {
        this.message.setSubject(this.subject);
        this.message.setFromEmail(this.fromEmail);
        this.message.setFromName(this.fromName);
        if (CollUtil.isNotEmpty(this.attachments)) {
            this.message.setAttachments(this.attachments);
        }
        if (CollUtil.isNotEmpty(this.images)) {
            this.message.setImages(this.images);
            this.isHtml = true;
        }
        if (this.isHtml) {
            this.message.setHtml(this.content);
            this.message.setInlineCss(true);
        } else {
            this.message.setText(this.content);
        }
        this.message.setTo(this.to);
        this.message.setPreserveRecipients(true);
    }

}
