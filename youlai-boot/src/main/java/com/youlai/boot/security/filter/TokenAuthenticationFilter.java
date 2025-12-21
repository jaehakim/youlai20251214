package com.youlai.boot.security.filter;

import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.core.web.WebResponseHelper;
import com.youlai.boot.security.token.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 토큰 인증 검증 필터
 *
 * @author wangtao
 * @since 2025/3/6 16:50
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /**
     * 토큰 관리자
     */
    private final TokenManager tokenManager;

    public TokenAuthenticationFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    /**
     * 토큰 검증, 서명 검증 및 만료 여부 확인 포함
     * 토큰이 유효하면 토큰을 Authentication 객체로 파싱하고 Spring Security 컨텍스트에 설정
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (StrUtil.isNotBlank(authorizationHeader)
                    && authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {

                // Bearer 접두사를 제거하여 원본 토큰 획득
                String rawToken = authorizationHeader.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());

                // 토큰 유효성 검사 수행 (암호화 서명 검증 및 만료 시간 검증 포함)
                boolean isValidToken = tokenManager.validateToken(rawToken);
                if (!isValidToken) {
                    WebResponseHelper.writeError(response, ResultCode.ACCESS_TOKEN_INVALID);
                    return;
                }

                // 토큰을 Spring Security 컨텍스트 인증 객체로 파싱
                Authentication authentication = tokenManager.parseToken(rawToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // 보안 컨텍스트 클리어 보장 (컨텍스트 잔류 방지)
            SecurityContextHolder.clearContext();
            WebResponseHelper.writeError(response, ResultCode.ACCESS_TOKEN_INVALID);
            return;
        }

        // 후속 필터 체인 실행 계속
        filterChain.doFilter(request, response);
    }
}
