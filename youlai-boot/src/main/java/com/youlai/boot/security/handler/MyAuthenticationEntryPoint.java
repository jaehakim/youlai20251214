package com.youlai.boot.security.handler;

import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.core.web.WebResponseHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Spring Security 인증 실패 응답 통합 처리
 *
 * @author Ray.Hao
 * @since 2.0.0
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증 실패 처리 진입 메서드
     *
     * @param request 예외를 발생시킨 요청 객체 (요청 헤더, 파라미터 등 획득에 사용 가능)
     * @param response 응답 객체 (오류 정보 작성에 사용)
     * @param authException 인증 예외 객체 (구체적인 실패 원인 포함)
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (authException instanceof BadCredentialsException) {
            // 사용자명 또는 비밀번호 오류
            WebResponseHelper.writeError(response, ResultCode.USER_PASSWORD_ERROR);
        } else if(authException instanceof InsufficientAuthenticationException){
            // 요청 헤더 Authorization 누락, 토큰 형식 오류, 토큰 만료, 서명 검증 실패
            WebResponseHelper.writeError(response, ResultCode.ACCESS_TOKEN_INVALID);
        } else {
            // 기타 명확하게 처리되지 않은 인증 예외 (예: 계정 잠금, 계정 비활성화 등)
            WebResponseHelper.writeError(response, ResultCode.USER_LOGIN_EXCEPTION, authException.getMessage());
        }
    }
}




