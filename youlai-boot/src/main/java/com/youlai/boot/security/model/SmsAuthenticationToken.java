package com.youlai.boot.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

/**
 * SMS 인증 코드 인증 토큰
 *
 * @author Ray.Hao
 * @since 2.20.0
 */
public class SmsAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 621L;

    /**
     * 인증 정보 (휴대폰 번호)
     */
    private final Object principal;

    /**
     * 자격 증명 정보 (SMS 인증 코드)
     */
    private final Object credentials;

    /**
     * SMS 인증 코드 인증 토큰 (미인증)
     *
     * @param principal 위챗 사용자 정보
     */
    public SmsAuthenticationToken(Object principal, Object credentials) {
        // 권한 정보가 없을 때 null로 설정
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        // 기본값 미인증
        this.setAuthenticated(false);
    }

    /**
     * SMS 인증 코드 인증 토큰 (인증됨)
     *
     * @param principal   사용자 정보
     * @param authorities 권한 정보
     */
    public SmsAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null;
        // 인증 통과
        super.setAuthenticated(true);
    }


    /**
     * 인증 통과
     *
     * @param principal   사용자 정보
     * @param authorities 권한 정보
     * @return SmsAuthenticationToken
     */
    public static SmsAuthenticationToken authenticated(Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new SmsAuthenticationToken(principal, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
