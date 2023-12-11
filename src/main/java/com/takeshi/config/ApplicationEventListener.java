package com.takeshi.config;

import cn.hutool.core.net.NetUtil;
import cn.hutool.http.useragent.Platform;
import com.takeshi.constants.TakeshiConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Locale;

/**
 * 应用程序事件监听器
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@Component
public class ApplicationEventListener {

    @Value("${spring.application.name:}")
    private String applicationName;

    @Value("${java.version}")
    private String javaVersion;

    @Value("${server.port:8080}")
    private Integer serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 上下文刷新事件
     */
    @EventListener(ContextRefreshedEvent.class)
    public void handleContextRefreshed() {
        Platform.mobilePlatforms.add(4, TakeshiConstants.ANDROID_TABLET);
        Platform.platforms.addAll(0, Platform.mobilePlatforms);
        log.info("ApplicationEventListener.handleContextRefreshed --> Add the Android tablet platform type to the Platform static variable of Hutool.");
    }

    /**
     * 应用程序就绪事件
     */
    @EventListener(ApplicationReadyEvent.class)
    public void handleApplicationReady() {
        log.info("""

                         ________    _______   ________
                        |\\_____  \\  /  ___  \\ |\\   ____\\
                         \\|___/  /|/__/|_/  /|\\ \\  \\___|_
                             /  / /|__|//  / / \\ \\_____  \\
                            /  / /     /  /_/__ \\|____|\\  \\
                           /__/ /     |\\________\\ ____\\_\\  \\
                           |__|/       \\|_______||\\_________\\
                                                 \\|_________|
                        Application {} Successfully started using Java {} with PID {}. Default language: {}. Default region: {}. Default TimeZone: {}
                        Swagger Api Url: http://{}:{}{}/doc.html""",
                applicationName, javaVersion, ProcessHandle.current().pid(), Locale.getDefault().getLanguage(),
                Locale.getDefault().getCountry(), ZoneId.systemDefault(), NetUtil.getLocalhostStr(),
                serverPort, contextPath);
    }

}
