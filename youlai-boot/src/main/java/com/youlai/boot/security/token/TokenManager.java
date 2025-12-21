package com.youlai.boot.security.token;


import com.youlai.boot.security.model.AuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 *  토큰 관리자
 *  <p>
 *  토큰 생성, 파싱, 검증, 갱신에 사용
 *
 * @author Ray.Hao
 * @since 2.16.0
 */
public interface TokenManager {

    /**
     * 인증 토큰 생성
     *
     * @param authentication 사용자 인증 정보
     * @return 인증 토큰 응답
     */
    AuthenticationToken generateToken(Authentication authentication);

    /**
     * 토큰 파싱하여 인증 정보 획득
     *
     * @param token  토큰
     * @return 사용자 인증 정보
     */
    Authentication parseToken(String token);

    /**
     * 토큰이 유효한지 검증
     *
     * @param token JWT 토큰
     * @return 유효 여부
     */
    boolean validateToken(String token);

    /**
     * 리프레시 토큰이 유효한지 검증
     *
     * @param refreshToken JWT 토큰
     * @return 유효 여부
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     *  토큰 갱신
     *
     * @param token 리프레시 토큰
     * @return 인증 토큰 응답
     */
    AuthenticationToken refreshToken(String token);

    /**
     * 토큰 무효화
     *
     * @param token 토큰
     */
    default void invalidateToken(String token) {
        // 기본 구현은 비어있거나 지원되지 않는 작업 예외를 던질 수 있음
        // throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * 지정된 사용자의 모든 세션 무효화
     *
     * @param userId 사용자 ID
     */
    default void invalidateUserSessions(Long userId) {
        // 기본 빈 구현, 구체적인 TokenManager가 사용자별 오프라인 지원 여부 결정
    }

}
