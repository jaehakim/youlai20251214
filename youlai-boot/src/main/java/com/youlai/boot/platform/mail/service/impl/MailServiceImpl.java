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
 * 이메일 서비스 구현 클래스
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
     * 간단한 텍스트 이메일 전송
     *
     * @param to      수신자 주소
     * @param subject 이메일 제목
     * @param text    이메일 내용
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
            log.error("이메일 전송 실패: {}", e.getMessage());
        }
    }

    /**
     * 첨부 파일이 포함된 이메일 전송
     *
     * @param to       수신자 주소
     * @param subject  이메일 제목
     * @param text     이메일 내용
     * @param filePath 첨부 파일 경로
     */
    @Override
    public void sendMailWithAttachment(String to, String subject, String text, String filePath) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailProperties.getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);  // true는 HTML 내용 지원을 의미함

            FileSystemResource file = new FileSystemResource(new File(filePath));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("이메일 전송 실패: {}", e.getMessage());
        }
    }
}
