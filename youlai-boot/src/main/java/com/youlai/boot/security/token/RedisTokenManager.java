package com.youlai.boot.security.token;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.config.property.SecurityProperties;
import com.youlai.boot.security.model.AuthenticationToken;
import com.youlai.boot.security.model.OnlineUser;
import com.youlai.boot.security.model.SysUserDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 토큰 관리자
 * <p>
 * Redis 토큰 생성, 파싱, 검증, 갱신에 사용
 *
 * @author Ray.Hao
 * @since 2024/11/15
 */
@ConditionalOnProperty(value = "security.session.type", havingValue = "redis-token")
@Service
public class RedisTokenManager implements TokenManager {

    private final SecurityProperties securityProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisTokenManager(SecurityProperties securityProperties, RedisTemplate<String, Object> redisTemplate) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 토큰 생성
     *
     * @param authentication 사용자 인증 정보
     * @return 생성된 AuthenticationToken 객체
     */
    @Override
    public AuthenticationToken generateToken(Authentication authentication) {
        SysUserDetails user = (SysUserDetails) authentication.getPrincipal();
        String accessToken = IdUtil.fastSimpleUUID();
        String refreshToken = IdUtil.fastSimpleUUID();

        // 사용자 온라인 정보 구성
        OnlineUser onlineUser = new OnlineUser(
                user.getUserId(),
                user.getUsername(),
                user.getDeptId(),
                user.getDataScope(),
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet())
        );

        // 액세스 토큰, 리프레시 토큰 및 리프레시 토큰 매핑 저장
        storeTokensInRedis(accessToken, refreshToken, onlineUser);

        // 단일 기기 로그인 제어
        handleSingleDeviceLogin(user.getUserId(), accessToken);

        return AuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(securityProperties.getSession().getAccessTokenTimeToLive())
                .build();
    }

    /**
     * 토큰으로 사용자 정보 파싱
     *
     * @param token Redis 토큰
     * @return 구성된 Authentication 객체
     */
    @Override
    public Authentication parseToken(String token) {
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(formatTokenKey(token));
        if (onlineUser == null) return null;

        // 사용자 권한 집합 구성
        Set<SimpleGrantedAuthority> authorities = null;

        Set<String> roles = onlineUser.getRoles();
        if (CollectionUtil.isNotEmpty(roles)) {
            authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        // 사용자 세부 정보 객체 구성
        SysUserDetails userDetails = buildUserDetails(onlineUser, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    /**
     * 토큰이 유효한지 검증
     *
     * @param token 액세스 토큰
     * @return 유효 여부
     */
    @Override
    public boolean validateToken(String token) {
        return redisTemplate.hasKey(formatTokenKey(token));
    }

    /**
     * RefreshToken이 유효한지 검증
     *
     * @param refreshToken 액세스 토큰
     * @return 유효 여부
     */
    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return redisTemplate.hasKey(formatRefreshTokenKey(refreshToken));
    }

    /**
     * 토큰 갱신
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로 생성된 AuthenticationToken 객체
     */
    @Override
    public AuthenticationToken refreshToken(String refreshToken) {
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue()
                .get(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken));
        if (onlineUser == null) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_INVALID);
        }
        Object oldAccessTokenValue = redisTemplate.opsForValue().get(StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId()));
        // 구 액세스 토큰 기록 삭제
        Optional.of(oldAccessTokenValue)
                .map(String.class::cast)
                .ifPresent(oldAccessToken -> redisTemplate.delete(formatTokenKey(oldAccessToken)));

        // 신규 액세스 토큰 생성 및 저장
        String newAccessToken = IdUtil.fastSimpleUUID();
        storeAccessToken(newAccessToken, onlineUser);

        int accessTtl = securityProperties.getSession().getAccessTokenTimeToLive();
        return AuthenticationToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTtl)
                .build();
    }

    /**
     * 액세스 토큰 무효화
     *
     * @param token 액세스 토큰
     */
    @Override
    public void invalidateToken(String token) {
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(formatTokenKey(token));
        if (onlineUser != null) {
            Long userId = onlineUser.getUserId();
            invalidateUserSessions(userId);
        }
    }

    /**
     * 지정된 사용자의 모든 세션 무효화
     *
     * @param userId 사용자 ID
     */
    @Override
    public void invalidateUserSessions(Long userId) {
        if (userId == null) {
            return;
        }

        // 1. 액세스 토큰 관련 삭제
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
        Object accessTokenValue = redisTemplate.opsForValue().get(userAccessKey);
        Optional.of(accessTokenValue)
                .map(String.class::cast)
                .ifPresent(accessToken -> redisTemplate.delete(formatTokenKey(accessToken)));
        // 액세스 토큰 매핑 존재 여부와 관계없이 userAccessKey 삭제 시도
        redisTemplate.delete(userAccessKey);

        // 2. 리프레시 토큰 관련 삭제
        String userRefreshKey = StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, userId);
        Object refreshTokenValue = redisTemplate.opsForValue().get(userRefreshKey);
        Optional.of(refreshTokenValue)
                .map(String.class::cast)
                .ifPresent(refreshToken ->
                        redisTemplate.delete(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken))
                );
        // userRefreshKey 자체도 정리
        redisTemplate.delete(userRefreshKey);
    }

    /**
     * 액세스 토큰과 리프레시 토큰을 Redis에 저장
     *
     * @param accessToken  액세스 토큰
     * @param refreshToken 리프레시 토큰
     * @param onlineUser   온라인 사용자 정보
     */
    private void storeTokensInRedis(String accessToken, String refreshToken, OnlineUser onlineUser) {
        // 액세스 토큰 -> 사용자 정보
        setRedisValue(formatTokenKey(accessToken), onlineUser, securityProperties.getSession().getAccessTokenTimeToLive());

        // 리프레시 토큰 -> 사용자 정보
        String refreshTokenKey = StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
        setRedisValue(refreshTokenKey, onlineUser, securityProperties.getSession().getRefreshTokenTimeToLive());

        // 사용자 ID -> 리프레시 토큰
        setRedisValue(StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, onlineUser.getUserId()),
                refreshToken,
                securityProperties.getSession().getRefreshTokenTimeToLive());
    }

    /**
     * 단일 기기 로그인 제어 처리
     *
     * @param userId      사용자 ID
     * @param accessToken 새로 생성된 액세스 토큰
     */
    private void handleSingleDeviceLogin(Long userId, String accessToken) {
        Boolean allowMultiLogin = securityProperties.getSession().getRedisToken().getAllowMultiLogin();
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
        // 단일 기기 로그인 제어, 구 액세스 토큰 삭제
        if (!allowMultiLogin) {
            Object oldAccessTokenValue = redisTemplate.opsForValue().get(userAccessKey);
            Optional.of(oldAccessTokenValue)
                    .map(String.class::cast)
                    .ifPresent(oldAccessToken -> redisTemplate.delete(formatTokenKey(oldAccessToken)));
        }
        // 액세스 토큰 매핑 저장 (사용자 ID -> 액세스 토큰), 단일 기기 로그인 제어 시 구 액세스 토큰 삭제 및 리프레시 토큰 삭제 시 구 토큰 삭제용
        setRedisValue(userAccessKey, accessToken, securityProperties.getSession().getAccessTokenTimeToLive());
    }

    /**
     * 새로운 액세스 토큰 저장
     *
     * @param newAccessToken 새 액세스 토큰
     * @param onlineUser     온라인 사용자 정보
     */
    private void storeAccessToken(String newAccessToken, OnlineUser onlineUser) {
        setRedisValue(StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, newAccessToken), onlineUser, securityProperties.getSession().getAccessTokenTimeToLive());
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId());
        setRedisValue(userAccessKey, newAccessToken, securityProperties.getSession().getAccessTokenTimeToLive());
    }

    /**
     * 사용자 세부 정보 객체 구성
     *
     * @param onlineUser  온라인 사용자 정보
     * @param authorities 권한 집합
     * @return SysUserDetails 사용자 세부 정보
     */
    private SysUserDetails buildUserDetails(OnlineUser onlineUser, Set<SimpleGrantedAuthority> authorities) {
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(onlineUser.getUserId());
        userDetails.setUsername(onlineUser.getUsername());
        userDetails.setDeptId(onlineUser.getDeptId());
        userDetails.setDataScope(onlineUser.getDataScope());
        userDetails.setAuthorities(authorities);
        return userDetails;
    }

    /**
     * 액세스 토큰의 Redis 키 포맷
     *
     * @param token 액세스 토큰
     * @return 포맷된 Redis 키
     */
    private String formatTokenKey(String token) {
        return StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, token);
    }

    /**
     * 리프레시 토큰의 Redis 키 포맷
     *
     * @param refreshToken 액세스 토큰
     * @return 포맷된 Redis 키
     */
    private String formatRefreshTokenKey(String refreshToken) {
        return StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
    }

    /**
     * 값을 Redis에 저장
     *
     * @param key   키
     * @param value 값
     * @param ttl   만료 시간(초), -1은 영구적
     */
    private void setRedisValue(String key, Object value, int ttl) {
        if (ttl != -1) {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value); // ttl=-1일 때 영구적
        }
    }
}
