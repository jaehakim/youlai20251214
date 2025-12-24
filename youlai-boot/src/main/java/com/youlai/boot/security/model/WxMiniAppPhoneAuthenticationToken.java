package com.youlai.boot.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

/**
 * 위챗 미니프로그램 휴대폰 번호 인증 토큰
 *
 * @author YouLai Tech Team
 * @since 2.0.0
 */
public class WxMiniAppPhoneAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 622L;

    private final Object principal; // code
    private String encryptedData;
    private String iv;

    /**
     * 위챗 미니프로그램 휴대폰 번호 인증 토큰 (미인증)
     *
     * @param code 위챗 로그인 코드
     * @param encryptedData 암호화된 데이터
     * @param iv 초기 벡터
     */
    public WxMiniAppPhoneAuthenticationToken(String code, String encryptedData, String iv) {
        super(null);
        this.principal = code;
        this.encryptedData = encryptedData;
        this.iv = iv;
        this.setAuthenticated(false);
    }

    /**
     * 위챗 미니프로그램 휴대폰 번호 인증 토큰 (인증됨)
     *
     * @param principal 사용자 정보
     * @param authorities 권한 정보
     */
    public WxMiniAppPhoneAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    /**
     * 인증 통과
     *
     * @param principal 사용자 정보
     * @param authorities 권한 정보
     * @return 인증 통과된 토큰
     */
    public static WxMiniAppPhoneAuthenticationToken authenticated(Object principal, Collection<? extends GrantedAuthority> authorities) {
        return new WxMiniAppPhoneAuthenticationToken(principal, authorities);
    }

    @Override
    public Object getCredentials() {
        // 위챗 미니프로그램 휴대폰 번호 인증은 비밀번호가 필요하지 않음
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * 암호화된 데이터 획득
     *
     * @return 암호화된 데이터
     */
    public String getEncryptedData() {
        return encryptedData;
    }

    /**
     * 초기 벡터 획득
     *
     * @return 초기 벡터
     */
    public String getIv() {
        return iv;
    }
} 
