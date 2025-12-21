package com.youlai.boot.common.constant;

/**
 * 보안 모듈 상수
 *
 * @author Ray.Hao
 * @since 2023/11/24
 */
public interface SecurityConstants {

    /**
     * 로그인 경로
     */
    String LOGIN_PATH = "/api/v1/auth/login";

    /**
     * JWT Token 접두사
     */
    String BEARER_TOKEN_PREFIX  = "Bearer ";

    /**
     * 역할 접두사, authorities에서 역할과 권한을 구분하기 위해 사용, ROLE_* 는 역할, 접두사 없는 것은 권한
     */
    String ROLE_PREFIX = "ROLE_";
}
