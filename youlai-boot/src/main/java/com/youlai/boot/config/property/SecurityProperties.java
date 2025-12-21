package com.youlai.boot.config.property;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 보안 모듈 설정 속성 클래스
 *
 * <p>application.yml의 security 접두사가 붙은 보안 관련 설정을 매핑합니다</p>
 *
 * @author Ray.Hao
 * @since 2024/4/18
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 세션 관리 설정
     */
    private SessionConfig session;

    /**
     * 보안 화이트리스트 경로 (보안 필터를 완전히 우회)
     * <p>예시: /api/v1/auth/login/**, /ws/**
     */
    @NotEmpty
    private String[] ignoreUrls;

    /**
     * 비보안 엔드포인트 경로 (익명 접근 허용 API)
     * <p>예시: /doc.html, /v3/api-docs/**
     */
    @NotEmpty
    private String[] unsecuredUrls;

    /**
     * 세션 설정 중첩 클래스
     */
    @Data
    public static class SessionConfig {
        /**
         * 인증 전략 타입
         * <ul>
         *   <li>jwt - JWT 기반 무상태 인증</li>
         *   <li>redis-token - Redis 기반 상태 유지 인증</li>
         * </ul>
         */
        @NotNull
        private String type;

        /**
         * 액세스 토큰 유효 기간 (단위: 초)
         * <p>기본값: 3600 (1시간)</p>
         * <p>-1은 만료되지 않음을 의미</p>
         */
        @Min(-1)
        private Integer accessTokenTimeToLive = 3600;

        /**
         * 리프레시 토큰 유효 기간 (단위: 초)
         * <p>기본값: 604800 (7일)</p>
         * <p>-1은 만료되지 않음을 의미</p>
         */
        @Min(-1)
        private Integer refreshTokenTimeToLive = 604800;

        /**
         * JWT 설정 항목
         */
        private JwtConfig jwt;

        /**
         * Redis 토큰 설정 항목
         */
        private RedisTokenConfig redisToken;
    }

    /**
     * JWT 설정 중첩 클래스
     */
    @Data
    public static class JwtConfig {
        /**
         * JWT 서명 비밀 키
         * <p>HS256 알고리즘은 최소 32자를 요구합니다</p>
         * <p>예시: SecretKey012345678901234567890123456789</p>
         */
        @NotNull
        private String secretKey;
    }

    /**
     * Redis 토큰 설정 중첩 클래스
     */
    @Data
    public static class RedisTokenConfig {
        /**
         * 다중 기기 동시 로그인 허용 여부
         * <p>true - 동일 계정 다중 기기 로그인 허용 (기본값)</p>
         * <p>false - 새 로그인 시 기존 토큰 무효화</p>
         */
        private Boolean allowMultiLogin = true;
    }
}
