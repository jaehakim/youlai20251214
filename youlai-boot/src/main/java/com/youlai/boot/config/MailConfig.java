package com.youlai.boot.config;

import com.youlai.boot.config.property.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * MailConfig 설정 클래스, JavaMailSender를 수동으로 설정하고 주입하는 데 사용됩니다.
 * MailProperties 클래스에서 설정된 메일 관련 속성을 읽어 JavaMailSender를 초기화합니다.
 * <p>
 * 수동 주입을 하는 이유는 application-dev.yml 또는 기타 비 application.yml 설정 파일을 사용할 때
 * IDEA에서 JavaMailSender bean을 찾을 수 없다는 경고를 방지하기 위함입니다.
 *
 * @author Ray
 * @since 2024/8/17
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {

    private final MailProperties mailProperties;

    public MailConfig(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
    }

    /**
     * JavaMailSender bean을 생성하고 설정합니다.
     *
     * @return 설정된 JavaMailSender 인스턴스
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", mailProperties.getProperties().getSmtp().isAuth());
        properties.put("mail.smtp.starttls.enable", mailProperties.getProperties().getSmtp().getStarttls().isEnable());

        return mailSender;
    }
}
