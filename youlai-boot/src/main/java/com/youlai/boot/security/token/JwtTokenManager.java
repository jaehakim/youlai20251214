package com.youlai.boot.security.token;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.youlai.boot.common.constant.JwtClaimConstants;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.config.property.SecurityProperties;
import com.youlai.boot.security.model.AuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import com.youlai.boot.security.model.SysUserDetails;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * JWT 토큰 관리자
 * <p>
 * JWT 토큰 생성, 파싱, 검증, 갱신에 사용
 *
 * @author Ray.Hao
 * @since 2024/11/15
 */
@ConditionalOnProperty(value = "security.session.type", havingValue = "jwt")
@Service
public class JwtTokenManager implements TokenManager {

    private final SecurityProperties securityProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final byte[] secretKey;

    public JwtTokenManager(SecurityProperties securityProperties, RedisTemplate<String, Object> redisTemplate) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
        this.secretKey = securityProperties.getSession().getJwt().getSecretKey().getBytes();
    }

    /**
     * 토큰 생성
     *
     * @param authentication 인증 정보
     * @return 토큰 응답 객체
     */
    @Override
    public AuthenticationToken generateToken(Authentication authentication) {
        int accessTokenTimeToLive = securityProperties.getSession().getAccessTokenTimeToLive();
        int refreshTokenTimeToLive = securityProperties.getSession().getRefreshTokenTimeToLive();

        String accessToken = generateToken(authentication, accessTokenTimeToLive);
        String refreshToken = generateToken(authentication, refreshTokenTimeToLive, true);

        return AuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenTimeToLive)
                .build();
    }

    /**
     * 토큰 파싱
     *
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    @Override
    public Authentication parseToken(String token) {

        JWT jwt = JWTUtil.parseToken(token);
        JSONObject payloads = jwt.getPayloads();
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(payloads.getLong(JwtClaimConstants.USER_ID)); // 사용자 ID
        userDetails.setDeptId(payloads.getLong(JwtClaimConstants.DEPT_ID)); // 부서 ID
        userDetails.setDataScope(payloads.getInt(JwtClaimConstants.DATA_SCOPE)); // 데이터 권한 범위

        userDetails.setUsername(payloads.getStr(JWTPayload.SUBJECT)); // 사용자명
        // 역할 집합
        Set<SimpleGrantedAuthority> authorities = payloads.getJSONArray(JwtClaimConstants.AUTHORITIES)
                .stream()
                .map(authority -> new SimpleGrantedAuthority(Convert.toStr(authority)))
                .collect(Collectors.toSet());

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    /**
     * 토큰 검증
     *
     * @param token JWT 토큰
     * @return 유효 여부
     */
    @Override
    public boolean validateToken(String token) {
        return validateToken(token, false);
    }

    /**
     * 리프레시 토큰 검증
     *
     * @param refreshToken JWT 토큰
     * @return 검증 결과
     */
    @Override
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, true);
    }

    /**
     * 토큰 검증
     *
     * @param token                JWT 토큰
     * @param validateRefreshToken 리프레시 토큰 검증 여부
     * @return 유효 여부
     */
    private boolean validateToken(String token, boolean validateRefreshToken) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            // 토큰이 유효한지 확인 (서명 검증 + 만료 여부)
            boolean isValid = jwt.setKey(secretKey).validate(0);

            if (isValid) {
                JSONObject payloads = jwt.getPayloads();
                // 1. 리프레시 토큰 타입 검증 (리프레시 토큰 검증 시나리오에서만 활성화)
                String jti = payloads.getStr(JWTPayload.JWT_ID);
                if (validateRefreshToken) {
                    // 리프레시 토큰은 토큰 타입 검증 필요
                    boolean isRefreshToken = payloads.getBool(JwtClaimConstants.TOKEN_TYPE);
                    if (!isRefreshToken) {
                        return false;
                    }
                }
                // 2. 보안 버전 번호 검증 (사용자 차원에서 이전 토큰 무효화용)
                Long userId = payloads.getLong(JwtClaimConstants.USER_ID);
                if (userId != null) {
                    // 구 버전 토큰은 SECURITY_VERSION 선언이 없을 수 있으며, 0 버전으로 간주
                    Integer tokenVersionRaw = payloads.getInt(JwtClaimConstants.SECURITY_VERSION);
                    int tokenVersion = tokenVersionRaw != null ? tokenVersionRaw : 0;

                    String versionKey = StrUtil.format(RedisConstants.Auth.USER_SECURITY_VERSION, userId);
                    Integer currentVersionRaw = (Integer) redisTemplate.opsForValue().get(versionKey);
                    int currentVersion = currentVersionRaw != null ? currentVersionRaw : 0;

                    // 현재 버전 번호가 토큰에 포함된 버전 번호보다 새로우면 해당 토큰은 무효로 간주
                    if (tokenVersion < currentVersion) {
                        return false;
                    }
                }

                // 3. 블랙리스트에 있는지 판단, 있으면 false를 반환하여 토큰 무효 표시
                if (Boolean.TRUE.equals(redisTemplate.hasKey(StrUtil.format(RedisConstants.Auth.BLACKLIST_TOKEN, jti)))) {
                    return false;
                }
            }
            return isValid;
        } catch (Exception gitignore) {
            // 토큰 검증
        }
        return false;
    }

    /**
     * 토큰을 블랙리스트에 추가
     *
     * @param token JWT 토큰
     */
    @Override
    public void invalidateToken(String token) {
        if (StringUtils.isBlank(token)) {
            return;
        }

        if (token.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());
        }
        JWT jwt = JWTUtil.parseToken(token);
        JSONObject payloads = jwt.getPayloads();
        Integer expirationAt = payloads.getInt(JWTPayload.EXPIRES_AT);
        // 블랙리스트 토큰 키
        String blacklistTokenKey = StrUtil.format(RedisConstants.Auth.BLACKLIST_TOKEN, payloads.getStr(JWTPayload.JWT_ID));

        if (expirationAt != null) {
            int currentTimeSeconds = Convert.toInt(System.currentTimeMillis() / 1000);
            if (expirationAt < currentTimeSeconds) {
                // 토큰이 이미 만료됨, 직접 반환
                return;
            }
            // 토큰 남은 시간 계산 후 블랙리스트에 추가 (값은 단순 불리언 플래그 사용)
            int expirationIn = expirationAt - currentTimeSeconds;
            redisTemplate.opsForValue().set(blacklistTokenKey, Boolean.TRUE, expirationIn, TimeUnit.SECONDS);
        } else {
            // 영구적인 토큰은 영구적으로 블랙리스트에 추가
            redisTemplate.opsForValue().set(blacklistTokenKey, Boolean.TRUE);
        }
        ;
    }

    /**
     * 지정된 사용자의 모든 세션 무효화
     * <p>
     * 사용자의 보안 버전 번호를 높여서 구 버전 번호를 포함한 토큰이 후속 검증 시 모두 무효화되도록 함
     */
    @Override
    public void invalidateUserSessions(Long userId) {
        if (userId == null) {
            return;
        }

        String versionKey = StrUtil.format(RedisConstants.Auth.USER_SECURITY_VERSION, userId);
        // 버전 번호 증가
        redisTemplate.opsForValue().increment(versionKey);

    }

    /**
     * 토큰 갱신
     *
     * @param refreshToken 리프레시 토큰
     * @return 토큰 응답 객체
     */
    @Override
    public AuthenticationToken refreshToken(String refreshToken) {
        boolean isValid = validateRefreshToken(refreshToken);
        if (!isValid) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_INVALID);
        }
        Authentication authentication = parseToken(refreshToken);
        int accessTokenExpiration = securityProperties.getSession().getAccessTokenTimeToLive();
        String newAccessToken = generateToken(authentication, accessTokenExpiration);
        return AuthenticationToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .build();
    }

    /**
     * JWT 토큰 생성
     *
     * @param authentication 인증 정보
     * @param ttl            만료 시간
     * @return JWT 토큰
     */
    private String generateToken(Authentication authentication, int ttl) {
        return generateToken(authentication, ttl, false);
    }


    /**
     * JWT 토큰 생성
     *
     * @param authentication 인증 정보
     * @param ttl            만료 시간
     * @param isRefreshToken 리프레시 토큰 타입 여부
     * @return JWT 토큰
     */
    private String generateToken(Authentication authentication, int ttl, boolean isRefreshToken) {
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        Map<String, Object> payload = new HashMap<>();
        payload.put(JwtClaimConstants.USER_ID, userDetails.getUserId()); // 사용자 ID
        payload.put(JwtClaimConstants.DEPT_ID, userDetails.getDeptId()); // 부서 ID
        payload.put(JwtClaimConstants.DATA_SCOPE, userDetails.getDataScope()); // 데이터 권한 범위

        // claims에 역할 정보 추가
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        payload.put(JwtClaimConstants.AUTHORITIES, roles);

        Date now = new Date();
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JwtClaimConstants.TOKEN_TYPE, false);
        if (isRefreshToken) {
            payload.put(JwtClaimConstants.TOKEN_TYPE, true);
        }

        // 보안 버전 번호 설정: 존재하지 않을 때 기본값 0
        String versionKey = StrUtil.format(RedisConstants.Auth.USER_SECURITY_VERSION, userDetails.getUserId());
        Integer currentVersion = (Integer) redisTemplate.opsForValue().get(versionKey);
        int securityVersion = currentVersion != null ? currentVersion : 0;
        payload.put(JwtClaimConstants.SECURITY_VERSION, securityVersion);

        // 만료 시간 설정, -1은 영구적
        if (ttl != -1) {
            Date expiresAt = DateUtil.offsetSecond(now, ttl);
            payload.put(JWTPayload.EXPIRES_AT, expiresAt);
        }
        payload.put(JWTPayload.SUBJECT, authentication.getName());
        payload.put(JWTPayload.JWT_ID, IdUtil.simpleUUID());

        return JWTUtil.createToken(payload, secretKey);
    }

}
