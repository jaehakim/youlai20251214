package com.youlai.boot.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 인증 코드 속성 설정
 *
 * @author haoxr
 * @since 2023/11/24
 */
@Component
@ConfigurationProperties(prefix = "captcha")
@Data
public class CaptchaProperties {

    /**
     * 인증 코드 타입  circle-원형 간섭 인증 코드|gif-Gif 인증 코드|line-간섭선 인증 코드|shear-왜곡 간섭 인증 코드
     */
    private String type;

    /**
     * 인증 코드 이미지 너비
     */
    private int width;
    /**
     * 인증 코드 이미지 높이
     */
    private int height;

    /**
     * 간섭선 수
     */
    private int interfereCount;

    /**
     * 텍스트 투명도
     */
    private Float textAlpha;

    /**
     * 인증 코드 만료 시간, 단위: 초
     */
    private Long expireSeconds;

    /**
     * 인증 코드 문자 설정
     */
    private CodeProperties code;

    /**
     * 인증 코드 폰트
     */
    private FontProperties font;

    /**
     * 인증 코드 문자 설정
     */
    @Data
    public static class CodeProperties {
        /**
         * 인증 코드 문자 타입 math-산술|random-랜덤 문자열
         */
        private String type;
        /**
         * 인증 코드 문자 길이, type=산술일 때, 연산 자릿수를 나타냄(1:일의 자리 2:십의 자리); type=랜덤 문자일 때, 문자 개수를 나타냄
         */
        private int length;
    }

    /**
     * 인증 코드 폰트 설정
     */
    @Data
    public static class FontProperties {
        /**
         * 폰트 이름
         */
        private String name;
        /**
         * 폰트 스타일  0-보통|1-볼드|2-이탤릭
         */
        private int weight;
        /**
         * 폰트 크기
         */
        private int size;
    }


}
