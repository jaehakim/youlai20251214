package com.youlai.boot.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

/**
 * 위챗 미니프로그램 코드 인증 토큰
 *
 * @author YouLai Tech Team
 * @since 2.0.0
 */
public class WxMiniAppCodeAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 621L;
    private final Object principal;

    /**
     * 위챗 미니프로그램 코드 인증 토큰 (미인증)
     *
     * @param principal 위챗 코드
     */
    public WxMiniAppCodeAuthenticationToken(Object principal) {
        // 권한 정보가 없을 때 null로 설정
        super(null);
        this.principal = principal;
        // 기본값 미인증
        this.setAuthenticated(false);
    }


    /**
     * 위챗 미니프로그램 코드 인증 토큰 (인증됨)
     *
     * @param principal   위챗 사용자 정보
     * @param authorities 권한 정보
     */
    public WxMiniAppCodeAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        // 인증 통과
        super.setAuthenticated(true);
    }


    /**
     * 인증 통과
     *
     * @param principal   위챗 사용자 정보
     * @param authorities 권한 정보
     * @return 인증된 토큰
     */
    public static WxMiniAppCodeAuthenticationToken authenticated(Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new WxMiniAppCodeAuthenticationToken(principal, authorities);
    }

    @Override
    public Object getCredentials() {
        // 위챗 인증은 비밀번호가 필요하지 않음
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
} 