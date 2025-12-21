package com.youlai.boot.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 메일 설정 클래스, 메일 관련 설정 속성을 수신하고 저장하는 데 사용됩니다.
 *
 * @author Ray
 * @since 2024/8/17
 */
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class MailProperties {

    /**
     * 메일 서버 호스트명 또는 IP 주소.
     * 예: smtp.example.com
     */
    private String host;

    /**
     * 메일 서버 포트 번호.
     * 예: 587
     */
    private int port;

    /**
     * 메일 서버 연결에 사용되는 사용자 이름.
     * 예: your_email@example.com
     */
    private String username;

    /**
     * 메일 서버 연결에 사용되는 비밀번호.
     * 이 비밀번호는 안전하게 저장되어야 하며, 코드에 하드코딩해서는 안 됩니다.
     */
    private String password;

    /**
     * 메일 발신자 주소.
     */
    private String from;

    /**
     * 메일 서버의 기타 속성 설정.
     * 이러한 설정은 일반적으로 메일 전송 동작을 추가로 커스터마이징하는 데 사용됩니다.
     */
    private Properties properties = new Properties();

    /**
     * 내부 클래스, 메일 서버의 상세 설정을 캡슐화하는 데 사용됩니다.
     * SMTP 관련 설정 옵션을 포함합니다.
     */
    @Data
    public static class Properties {

        /**
         * SMTP 설정 옵션 클래스.
         * 인증, 암호화 등 SMTP 프로토콜 관련 설정을 포함합니다.
         */
        private Smtp smtp = new Smtp();

        @Data
        public static class Smtp {

            /**
             * SMTP 인증 활성화 여부.
             * `true`인 경우, 유효한 사용자 이름과 비밀번호를 제공하여 인증해야 합니다.
             */
            private boolean auth;

            /**
             * STARTTLS 암호화 설정 옵션.
             */
            private StartTls starttls = new StartTls();

            @Data
            public static class StartTls {

                /**
                 * STARTTLS 암호화 활성화 여부.
                 * `true`인 경우, 메일 전송 시 STARTTLS 프로토콜을 사용하여 암호화 전송합니다.
                 */
                private boolean enable;
            }
        }
    }
}
