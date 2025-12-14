package com.youlai.boot.platform.mail.service;

/**
 * 이메일서비스인터페이스层
 *
 * @author Ray
 * @since 2024/8/17
 */
public interface MailService {


    /**
     * 발송简单텍스트이메일
     *
     * @param to      收件人주소
     * @param subject 이메일主题
     * @param text    이메일내용
     */
    void sendMail(String to, String subject, String text) ;

    /**
     * 발송带附件의이메일
     *
     * @param to      收件人주소
     * @param subject 이메일主题
     * @param text    이메일내용
     * @param filePath 附件경로
     */
    void sendMailWithAttachment(String to, String subject, String text, String filePath);

}
