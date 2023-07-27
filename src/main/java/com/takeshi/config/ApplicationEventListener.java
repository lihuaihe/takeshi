package com.takeshi.config;

import cn.hutool.core.net.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
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

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${java.version}")
    private String javaVersion;

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 应用程序就绪事件
     *
     * @param event event
     */
    @EventListener
    public void applicationReadyEvent(ApplicationReadyEvent event) {
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
