package com.youlai.boot.common.constant;

/**
 * Redis 상수
 *
 * @author Theo
 * @since 2024-7-29 11:46:08
 */
public interface RedisConstants {

    /**
     * 속도 제한 관련 키
     */
    interface RateLimiter {
        String IP = "rate_limiter:ip:{}"; // IP限流（示例：rate_limiter:ip:192.168.1.1）
    }

    /**
     * 분산 잠금 관련 키
     */
    interface Lock {
        String RESUBMIT = "lock:resubmit:{}:{}"; // 防重复提交（示例：lock:resubmit:userIdentifier:requestIdentifier）
    }

    /**
     * 인증 모듈
     */
    interface Auth {
        // 액세스 토큰에 해당하는 사용자 정보 저장 (accessToken -> OnlineUser）
        String ACCESS_TOKEN_USER = "auth:token:access:{}";
        // 리프레시 토큰에 해당하는 사용자 정보 저장 (refreshToken -> OnlineUser）
        String REFRESH_TOKEN_USER = "auth:token:refresh:{}";
        // 사용자와 액세스 토큰의 매핑 (userId -> accessToken）
        String USER_ACCESS_TOKEN = "auth:user:access:{}";
        // 사용자와 리프레시 토큰의 매핑 (userId -> refreshToken
        String USER_REFRESH_TOKEN = "auth:user:refresh:{}";
        // 블랙리스트 Token (로그아웃 또는 계정 삭제 시 사용)
        String BLACKLIST_TOKEN = "auth:token:blacklist:{}";
        // 사용자 보안 버전 번호 (사용자별 이전 JWT 무효화 시 사용)
        String USER_SECURITY_VERSION = "auth:user:security_version:{}";
    }

    /**
     * 인증 코드 모듈
     */
    interface Captcha {
        String IMAGE_CODE = "captcha:image:{}";              // 이미지 인증 코드
        String SMS_LOGIN_CODE = "captcha:sms_login:{}";      // 로그인 SMS 인증 코드
        String SMS_REGISTER_CODE = "captcha:sms_register:{}";// 회원가입 SMS 인증 코드
        String MOBILE_CODE = "captcha:mobile:{}";            // 휴대폰 번호 등록/변경 인증 코드
        String EMAIL_CODE = "captcha:email:{}";              // 이메일 인증 코드
    }

    /**
     * 시스템 모듈
     */
    interface System {
        String CONFIG = "system:config";                 // 시스템 설정
        String ROLE_PERMS = "system:role:perms"; // 시스템 역할 및 권한 매핑
    }

}
