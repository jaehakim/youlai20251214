package com.youlai.boot.security.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.security.model.SysUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Spring Security 유틸리티 클래스
 *
 * @author Ray
 * @since 2021/1/10
 */
public class SecurityUtils {

    /**
     * 현재 로그인한 사용자 정보 획득
     *
     * @return Optional<SysUserDetails>
     */
    public static Optional<SysUserDetails> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof SysUserDetails) {
                return Optional.of((SysUserDetails) principal);
            }
        }
        return Optional.empty();
    }


    /**
     * 사용자 ID 획득
     *
     * @return Long
     */
    public static Long getUserId() {
        return getUser().map(SysUserDetails::getUserId).orElse(null);
    }


    /**
     * 사용자 계정 획득
     *
     * @return String 사용자 계정
     */
    public static String getUsername() {
        return getUser().map(SysUserDetails::getUsername).orElse(null);
    }


    /**
     * 부서 ID 획득
     *
     * @return Long
     */
    public static Long getDeptId() {
        return getUser().map(SysUserDetails::getDeptId).orElse(null);
    }

    /**
     * 데이터 권한 범위 획득
     *
     * @return Integer
     */
    public static Integer getDataScope() {
        return getUser().map(SysUserDetails::getDataScope).orElse(null);
    }


    /**
     * 역할 집합 획득
     *
     * @return 역할 집합
     */
    public static Set<String> getRoles() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .filter(CollectionUtil::isNotEmpty)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                // 역할 필터링, authorities의 역할은 모두 ROLE_로 시작
                .filter(authority -> authority.startsWith(SecurityConstants.ROLE_PREFIX))
                .map(authority -> StrUtil.removePrefix(authority, SecurityConstants.ROLE_PREFIX))
                .collect(Collectors.toSet());
    }

    /**
     * 최고 관리자 여부
     * <p>
     * 최고 관리자는 모든 권한 판단 무시
     */
    public static boolean isRoot() {
        Set<String> roles = getRoles();
        return roles.contains(SystemConstants.ROOT_ROLE_CODE);
    }

    /**
     * 요청에서 토큰 획득
     *
     * @return 토큰 문자열
     */
    public static String getTokenFromRequest() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if(Objects.isNull(servletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }


}
