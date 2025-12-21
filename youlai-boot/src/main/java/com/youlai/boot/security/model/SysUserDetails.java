package com.youlai.boot.security.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.youlai.boot.common.constant.SecurityConstants;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Spring Security 사용자 인증 객체
 * <p>
 * 사용자의 기본 정보와 권한 정보를 캡슐화하여 Spring Security의 사용자 인증 및 권한 부여에 사용됩니다.
 * {@link UserDetails} 인터페이스를 구현하여 사용자의 핵심 정보를 제공합니다.
 *
 * @author Ray.Hao
 * @version 3.0.0
 */
@Data
@NoArgsConstructor
public class SysUserDetails implements UserDetails {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 비밀번호
     */
    private String password;

    /**
     * 계정 활성화 여부(true:활성화 false:비활성화)
     */
    private Boolean enabled;

    /**
     * 부서 ID
     */
    private Long deptId;

    /**
     * 데이터 권한 범위
     */
    private Integer dataScope;

    /**
     * 사용자 역할 권한 집합
     */
    private Collection<SimpleGrantedAuthority> authorities;

    /**
     * 생성자: 사용자 인증 정보를 기반으로 사용자 세부 정보 객체 초기화
     *
     * @param user 사용자 인증 정보 객체 {@link UserAuthCredentials}
     */
    public SysUserDetails(UserAuthCredentials user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = ObjectUtil.equal(user.getStatus(), 1);
        this.deptId = user.getDeptId();
        this.dataScope = user.getDataScope();

        // 역할 권한 집합 초기화
        this.authorities = CollectionUtil.isNotEmpty(user.getRoles())
                ? user.getRoles().stream()
                // 역할명에 "ROLE_" 접두사 추가, 역할(ROLE_ADMIN)과 권한(user:add) 구분용
                .map(role -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + role))
                .collect(Collectors.toSet())
                : Collections.emptySet();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
