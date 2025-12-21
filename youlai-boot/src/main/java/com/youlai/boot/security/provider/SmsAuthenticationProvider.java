package com.youlai.boot.security.provider;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.security.exception.CaptchaValidationException;
import com.youlai.boot.security.model.SmsAuthenticationToken;
import com.youlai.boot.security.model.SysUserDetails;
import com.youlai.boot.security.model.UserAuthCredentials;
import com.youlai.boot.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * SMS 인증 코드 인증 Provider
 *
 * @author Ray.Hao
 * @since 2.17.0
 */
@Slf4j
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    private final RedisTemplate<String, Object> redisTemplate;


    public SmsAuthenticationProvider(UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * SMS 인증 코드 인증 로직, Spring Security 비밀번호 검증 프로세스 참고
     *
     * @param authentication 인증 객체
     * @return 인증된 Authentication 객체
     * @throws AuthenticationException 인증 예외
     * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String mobile = (String) authentication.getPrincipal();
        String inputVerifyCode = (String) authentication.getCredentials();

        // 휴대폰 번호로 사용자 정보 획득
        UserAuthCredentials userAuthCredentials = userService.getAuthCredentialsByMobile(mobile);

        if (userAuthCredentials == null) {
            throw new UsernameNotFoundException("사용자가 존재하지 않습니다");
        }

        // 사용자 상태가 유효한지 확인
        if (ObjectUtil.notEqual(userAuthCredentials.getStatus(), 1)) {
            throw new DisabledException("사용자가 비활성화되었습니다");
        }

        // SMS 인증 코드를 발송한 휴대폰 번호가 현재 로그인 사용자와 일치하는지 검증
        String cacheKey = StrUtil.format(RedisConstants.Captcha.SMS_LOGIN_CODE, mobile);
        String cachedVerifyCode = (String) redisTemplate.opsForValue().get(cacheKey);

        if (!StrUtil.equals(inputVerifyCode, cachedVerifyCode)) {
            throw new CaptchaValidationException("인증 코드가 올바르지 않습니다");
        } else {
            // 검증 성공 후 인증 코드 삭제
            redisTemplate.delete(cacheKey);
        }

        // 인증된 사용자 세부 정보 구성
        SysUserDetails userDetails = new SysUserDetails(userAuthCredentials);

        // 인증된 SmsAuthenticationToken 생성
        return SmsAuthenticationToken.authenticated(
                userDetails,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
