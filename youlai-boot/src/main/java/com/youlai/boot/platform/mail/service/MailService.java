package com.youlai.boot.platform.mail.service;

/**
 * 이메일 서비스 인터페이스
 *
 * @author Ray
 * @since 2024/8/17
 */
public interface MailService {


    /**
     * 간단한 텍스트 이메일 전송
     *
     * @param to      수신자 주소
     * @param subject 이메일 제목
     * @param text    이메일 내용
     */
    void sendMail(String to, String subject, String text) ;

    /**
     * 첨부 파일이 포함된 이메일 전송
     *
     * @param to      수신자 주소
     * @param subject 이메일 제목
     * @param text    이메일 내용
     * @param filePath 첨부 파일 경로
     */
    void sendMailWithAttachment(String to, String subject, String text, String filePath);

}
