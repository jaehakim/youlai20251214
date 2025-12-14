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
     * 角色前缀，用于区分 authorities 角色和权限， ROLE_* 角色 、没有前缀的是权限
     */
    String ROLE_PREFIX = "ROLE_";
}
