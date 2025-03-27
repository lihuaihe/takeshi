package com.takeshi.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
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
 * implementation 'com.mandrillapp.wrapper.lutung:lutung:+'
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class MandrillUtil {

    private volatile String fromEmail;

    private volatile String fromName;

    private volatile MandrillApi mandrillApi;

    private MandrillMessage message;

    private boolean preserveRecipients = true;

    /**
     * 收件人列表
     */
    private final List<MandrillMessage.Recipient> to = new ArrayList<>();

    /**
     * 附件列表
     */
    private final List<MandrillMessage.MessageContent> attachments = new ArrayList<>();

    /**
     * 嵌入的图像列表
     */
    private final List<MandrillMessage.MessageContent> images = new ArrayList<>();

    /**
     * 获取MandrillApi
     *
     * @return MandrillApi
     */
    public MandrillApi getMandrillApi() {
        if (ObjUtil.isNull(mandrillApi)) {
            synchronized (MandrillUtil.class) {
                if (ObjUtil.isNull(mandrillApi)) {
                    MandrillCredentials mandrill = SpringUtil.getBean(MandrillCredentials.class);
                    JsonNode jsonNode = AwsSecretsManagerUtil.getSecret();
                    String apiKey = StrUtil.blankToDefault(jsonNode.path(mandrill.getApiKeySecrets()).asText(), mandrill.getApiKey());
                    Assert.notBlank(apiKey, "'apiKey' is null; please provide Mandrill API key");
                    fromEmail = StrUtil.blankToDefault(jsonNode.path(mandrill.getFromEmailSecrets()).asText(), mandrill.getFromEmail());
                    fromName = StrUtil.blankToDefault(jsonNode.path(mandrill.getFromNameSecrets()).asText(), mandrill.getFromName());
                    mandrillApi = new MandrillApi(apiKey);
                    log.info("MandrillUtil.getMandrillApi --> Mandrill Initialization successful");
                }
            }
        }
        return mandrillApi;
    }

    private MandrillUtil() {
    }

    /**
     * 创建一个MandrillMessage对象
     *
     * @return MandrillUtil
     */
    private MandrillUtil message() {
        this.message = new MandrillMessage();
        return this;
    }

    /**
     * 创建一个MandrillMessage对象
     *
     * @param subject 主题
     * @param email   收件人邮箱
     * @param name    收件人名称
     * @return MandrillUtil
     */
    private MandrillUtil message(String subject, String email, String name) {
        MandrillMessage mandrillMessage = new MandrillMessage();
        mandrillMessage.setSubject(subject);
        this.message = mandrillMessage;
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(email);
        recipient.setName(name);
        this.to.add(recipient);
        return this;
    }

    /**
     * 创建一个可以发送邮件的对象
     *
     * @return MandrillUtil
     */
    public static MandrillUtil of() {
        return new MandrillUtil().message();
    }

    /**
     * 创建一个可以发送邮件的对象
     *
     * @param subject 主题
     * @param toEmail 收件人邮箱
     * @return MandrillUtil
     */
    public static MandrillUtil of(String subject, String toEmail) {
        return new MandrillUtil().message(subject, toEmail, null);
    }

    /**
     * 创建一个可以发送邮件的对象
     *
     * @param subject 主题
     * @param toEmail 收件人邮箱
     * @param toName  收件人名称
     * @return MandrillUtil
     */
    public static MandrillUtil of(String subject, String toEmail, String toName) {
        return new MandrillUtil().message(subject, toEmail, toName);
    }

    /**
     * 设置邮件主题
     *
     * @param subject 主题
     * @return this
     */
    public MandrillUtil subject(String subject) {
        this.message.setSubject(subject);
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
        this.message.setFromEmail(fromEmail);
        this.message.setFromName(fromName);
        return this;
    }

    /**
     * 设置正文
     * <br>
     * 正文是普通文本
     *
     * @param text 正文内容
     * @return this
     */
    public MandrillUtil text(String text) {
        this.message.setText(text);
        return this;
    }

    /**
     * 设置正文
     * <br/>
     * 正文是HTML
     *
     * @param html 正文内容
     * @return this
     */
    public MandrillUtil html(String html) {
        this.message.setHtml(html);
        this.message.setInlineCss(true);
        return this;
    }

    /**
     * 通过HTML模板与绑定参数融合后返回字符串，设置HTML正文，需要将模版文件放在默认模版路径resources/templates目录下
     *
     * @param templateName 模板文件名
     * @param bindingMap   绑定的参数，此Map中的参数会替换模板中的变量
     * @return this
     */
    public MandrillUtil html(String templateName, Map<?, ?> bindingMap) {
        return this.html(TakeshiUtil.getTemplateEngine().getTemplate(templateName).render(bindingMap));
    }

    /**
     * 是否在每封电子邮件的“收件人”标头中公开所有收件人
     *
     * @param preserveRecipients preserveRecipients
     * @return this
     */
    public MandrillUtil preserveRecipients(boolean preserveRecipients) {
        this.preserveRecipients = preserveRecipients;
        return this;
    }

    /**
     * 设置收件人信息
     *
     * @param toEmail 收件人邮箱
     * @param toName  收件人名称
     * @return this
     */
    public MandrillUtil addRecipient(String toEmail, String toName) {
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(toEmail);
        recipient.setName(toName);
        this.to.add(recipient);
        return this;
    }

    /**
     * 设置收件人信息
     *
     * @param toEmail 收件人邮箱
     * @param toName  收件人名称
     * @param type    收件人类型，可以通过调用{@link MandrillMessage.Recipient.Type}设置是否类型
     * @return this
     */
    public MandrillUtil addRecipient(String toEmail, String toName, MandrillMessage.Recipient.Type type) {
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(toEmail);
        recipient.setName(toName);
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
     * 设置附件
     *
     * @param inputStream 附件文件流
     * @param fileName    附件的名称
     * @return this
     */
    @SneakyThrows
    public MandrillUtil addAttachment(InputStream inputStream, String fileName) {
        try (TikaInputStream tikaInputStream = TikaInputStream.get(inputStream)) {
            MandrillMessage.MessageContent messageContent = new MandrillMessage.MessageContent();
            messageContent.setType(TakeshiUtil.getTika().detect(tikaInputStream));
            messageContent.setName(fileName);
            messageContent.setContent(Base64.encode(tikaInputStream));
            this.attachments.add(messageContent);
            return this;
        }
    }

    /**
     * 设置附件
     *
     * @param bytes    附件文件字节数组
     * @param fileName 附件的名称
     * @return this
     */
    public MandrillUtil addAttachment(byte[] bytes, String fileName) {
        MandrillMessage.MessageContent messageContent = new MandrillMessage.MessageContent();
        messageContent.setType(TakeshiUtil.getTika().detect(bytes));
        messageContent.setName(fileName);
        messageContent.setContent(Base64.encode(bytes));
        this.attachments.add(messageContent);
        return this;
    }

    /**
     * 使用cid引用设置嵌入的图像
     * <br/>
     * 正文中需要使用 img 标签 src="cid:名称"
     * <br/>
     * 例如：src="cid:favicon.ico"，name就必须是logo.png
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
     * 使用cid引用设置嵌入的图像，读取完毕后关闭流
     * <br/>
     * 正文中需要使用 img 标签 src="cid:名称"
     * <br/>
     * src中的名称要与下面代码中的 setName 中的值一值
     * <br/>
     * 例如：src="cid:favicon.ico"，imageName就必须是logo.png
     *
     * @param inputStream 嵌入的图像文件流
     * @param imageName   cid的名称
     * @return this
     */
    @SneakyThrows
    public MandrillUtil addImages(InputStream inputStream, String imageName) {
        try (TikaInputStream tikaInputStream = TikaInputStream.get(inputStream)) {
            MandrillMessage.MessageContent messageContent = new MandrillMessage.MessageContent();
            messageContent.setType(TakeshiUtil.getTika().detect(tikaInputStream));
            messageContent.setName(imageName);
            messageContent.setContent(Base64.encode(tikaInputStream));
            this.images.add(messageContent);
            return this;
        }
    }

    /**
     * 使用cid引用设置嵌入的图像，读取完毕后关闭流
     * <br/>
     * 正文中需要使用 img 标签 src="cid:名称"
     * <br/>
     * src中的名称要与下面代码中的 setName 中的值一值
     * <br/>
     * 例如：src="cid:favicon.ico"，imageName就必须是logo.png
     *
     * @param bytes     嵌入的图像文件字节数组
     * @param imageName cid的名称
     * @return this
     */
    public MandrillUtil addImages(byte[] bytes, String imageName) {
        MandrillMessage.MessageContent messageContent = new MandrillMessage.MessageContent();
        messageContent.setType(TakeshiUtil.getTika().detect(bytes));
        messageContent.setName(imageName);
        messageContent.setContent(Base64.encode(bytes));
        this.images.add(messageContent);
        return this;
    }

    /**
     * 发送邮件，会抛出异常
     */
    public void sendThrow() {
        try {
            if (CollUtil.isNotEmpty(this.attachments)) {
                this.message.setAttachments(this.attachments);
            }
            if (CollUtil.isNotEmpty(this.images)) {
                this.message.setImages(this.images);
            }
            if (CollUtil.isNotEmpty(this.to)) {
                this.message.setTo(this.to);
            }
            this.message.setPreserveRecipients(this.preserveRecipients);
            MandrillApi mandrillApi = this.getMandrillApi();
            if (StrUtil.isAllBlank(this.message.getFromEmail(), this.message.getFromName())) {
                this.message.setFromEmail(this.fromEmail);
                this.message.setFromName(this.fromName);
            }
            MandrillMessageStatus[] result = mandrillApi.messages().send(this.message, false);
            if (ArrayUtil.isEmpty(result)) {
                throw new TakeshiException("MandrillUtil sendThrow result is empty");
            }
            if (StrUtil.equalsAny(result[0].getStatus(), "rejected", "invalid")) {
                throw new TakeshiException(GsonUtil.toJson(result));
            }
            log.info("MandrillUtil.sendThrow --> result: {}", GsonUtil.toJson(result));
        } catch (MandrillApiError | IOException e) {
            throw new TakeshiException(e.getMessage());
        }
    }

    /**
     * 发送邮件，不会抛出异常
     */
    public void send() {
        try {
            this.sendThrow();
        } catch (Exception e) {
            log.error("MandrillUtil.send --> e: ", e);
        }
    }

}
