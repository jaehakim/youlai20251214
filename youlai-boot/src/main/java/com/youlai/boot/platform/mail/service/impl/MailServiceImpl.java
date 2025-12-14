package com.youlai.boot.platform.mail.service.impl;

import com.youlai.boot.config.property.MailProperties;
import com.youlai.boot.platform.mail.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 이메일서비스구현类
 *
 * @author Ray
 * @since 2024/8/17
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    private final MailProperties mailProperties;

    /**
     * 발송简单텍스트이메일
     *
     * @param to      收件人주소
     * @param subject 이메일主题
     * @param text    이메일내용
     */
    @Override
    public void sendMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getFrom());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 전송실패{}", e.getMessage());
        }
    }

    /**
     * 발송带附件의이메일
     *
     * @param to       收件人주소
     * @param subject  이메일主题
     * @param text     이메일내용
     * @param filePath 附件경로
     */
    @Override
    public void sendMailWithAttachment(String to, String subject, String text, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);  // true表示支持HTML내용

            FileSystemResource file = new FileSystemResource(new File(filePath));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("이메일 전송실패{}", e.getMessage());
        }
    }
}
