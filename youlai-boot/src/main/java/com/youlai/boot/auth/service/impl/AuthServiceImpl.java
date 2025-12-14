package com.youlai.boot.auth.service.impl;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.auth.model.dto.WxMiniAppCodeLoginDTO;
import com.youlai.boot.auth.model.dto.WxMiniAppPhoneLoginDTO;
import com.youlai.boot.auth.model.vo.CaptchaVO;
import com.youlai.boot.auth.service.AuthService;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.common.enums.CaptchaTypeEnum;
import com.youlai.boot.config.property.CaptchaProperties;
import com.youlai.boot.platform.sms.enums.SmsTypeEnum;
import com.youlai.boot.platform.sms.service.SmsService;
import com.youlai.boot.security.model.AuthenticationToken;
import com.youlai.boot.security.model.SmsAuthenticationToken;
import com.youlai.boot.security.model.WxMiniAppCodeAuthenticationToken;
import com.youlai.boot.security.model.WxMiniAppPhoneAuthenticationToken;
import com.youlai.boot.security.token.TokenManager;
import com.youlai.boot.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 인증 서비스 구현 클래스
 *
 * @author Ray.Hao
 * @since 2.4.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    private final Font captchaFont;
    private final CaptchaProperties captchaProperties;
    private final CodeGenerator codeGenerator;

    private final SmsService smsService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 사용자명비밀번호로그인
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @return 접근토큰
     */
    @Override
    public AuthenticationToken login(String username, String password) {
        // 1. 생성용도비밀번호 인증 토큰（인증 안 됨）
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username.trim(), password);

        // 2. 인증 실행（인증 중）
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증성공 후생성 JWT 토큰，및 저장 Security 컨텍스트，로그인 로그용 AOP 사용（인증 완료）
        AuthenticationToken authenticationTokenResponse =
                tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authenticationTokenResponse;
    }

    /**
     * 위챗원클릭 인증 로그인
     *
     * @param code 위챗 로그인code
     * @return 접근토큰
     */
    @Override
    public AuthenticationToken loginByWechat(String code) {
        // 1. 사용자 생성 위챗 인증 토큰（인증 안 됨）
        WxMiniAppCodeAuthenticationToken authenticationToken = new WxMiniAppCodeAuthenticationToken(code);

        // 2. 인증 실행（인증 중）
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증성공 후생성 JWT 토큰，및 저장 Security 컨텍스트，로그인 로그용 AOP 사용（인증 완료）
        AuthenticationToken token = tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return token;
    }

    /**
     * 로그인 SMS 인증코드 전송
     *
     * @param mobile 휴대폰 번호
     */
    @Override
    public void sendSmsLoginCode(String mobile) {

        // 랜덤생성4자리인증코드
        // String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        // TODO 위해편의테스트，인증코드固定값 1234，실제개발중에SMS 서비스 설정 후，가는사용上面의랜덤인증코드
        String code = "1234";

        // 발송SMS인증코드
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("code", code);
        try {
            smsService.sendSms(mobile, SmsTypeEnum.LOGIN, templateParams);
        } catch (Exception e) {
            log.error("발송SMS인증코드실패", e);
        }
        // 인증코드를 캐시에 저장Redis，로그인 검증용
        redisTemplate.opsForValue().set(StrUtil.format(RedisConstants.Captcha.SMS_LOGIN_CODE, mobile), code, 5, TimeUnit.MINUTES);
    }

    /**
     * SMS 인증 로그인
     *
     * @param mobile 휴대폰 번호
     * @param code   인증코드
     * @return 접근토큰
     */
    @Override
    public AuthenticationToken loginBySms(String mobile, String code) {
        // 1. 사용자 생성 SMS 인증코드 인증 토큰（인증 안 됨）
        SmsAuthenticationToken smsAuthenticationToken = new SmsAuthenticationToken(mobile, code);

        // 2. 인증 실행（인증 중）
        Authentication authentication = authenticationManager.authenticate(smsAuthenticationToken);

        // 3. 인증성공 후생성 JWT 토큰，및 저장 Security 컨텍스트，로그인 로그용 AOP 사용（인증 완료）
        AuthenticationToken authenticationToken = tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authenticationToken;
    }

    /**
     * 로그아웃로그인
     */
    @Override
    public void logout() {
        String token = SecurityUtils.getTokenFromRequest();
        if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX )) {
            token = token.substring(SecurityConstants.BEARER_TOKEN_PREFIX .length());
            // 을JWT토큰추가黑名单
            tokenManager.invalidateToken(token);
            // 제거Security컨텍스트
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 인증코드 조회
     *
     * @return 인증코드
     */
    @Override
    public CaptchaVO getCaptcha() {

        String captchaType = captchaProperties.getType();
        int width = captchaProperties.getWidth();
        int height = captchaProperties.getHeight();
        int interfereCount = captchaProperties.getInterfereCount();
        int codeLength = captchaProperties.getCode().getLength();

        AbstractCaptcha captcha;
        if (CaptchaTypeEnum.CIRCLE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createCircleCaptcha(width, height, codeLength, interfereCount);
        } else if (CaptchaTypeEnum.GIF.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createGifCaptcha(width, height, codeLength);
        } else if (CaptchaTypeEnum.LINE.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createLineCaptcha(width, height, codeLength, interfereCount);
        } else if (CaptchaTypeEnum.SHEAR.name().equalsIgnoreCase(captchaType)) {
            captcha = CaptchaUtil.createShearCaptcha(width, height, codeLength, interfereCount);
        } else {
            throw new IllegalArgumentException("Invalid captcha type: " + captchaType);
        }
        captcha.setGenerator(codeGenerator);
        captcha.setTextAlpha(captchaProperties.getTextAlpha());
        captcha.setFont(captchaFont);

        String captchaCode = captcha.getCode();
        String imageBase64Data = captcha.getImageBase64Data();

        // 인증코드 텍스트를 캐시에 저장Redis，로그인 검증용
        String captchaKey = IdUtil.fastSimpleUUID();
        redisTemplate.opsForValue().set(
                StrUtil.format(RedisConstants.Captcha.IMAGE_CODE, captchaKey),
                captchaCode,
                captchaProperties.getExpireSeconds(),
                TimeUnit.SECONDS
        );

        return CaptchaVO.builder()
                .captchaKey(captchaKey)
                .captchaBase64(imageBase64Data)
                .build();
    }

    /**
     * 새로고침token
     *
     * @param refreshToken 토큰 갱신
     * @return 새로운 접근 토큰
     */
    @Override
    public AuthenticationToken refreshToken(String refreshToken) {
        return tokenManager.refreshToken(refreshToken);
    }

    /**
     * 위챗미니 프로그램Code로그인
     *
     * @param loginDTO 로그인参수
     * @return 접근토큰
     */
    @Override
    public AuthenticationToken loginByWxMiniAppCode(WxMiniAppCodeLoginDTO loginDTO) {
        // 1. 위챗 미니 프로그램 인증 토큰 생성（인증 안 됨）
        WxMiniAppCodeAuthenticationToken authenticationToken = new WxMiniAppCodeAuthenticationToken(loginDTO.getCode());

        // 2. 인증 실행（인증 중）
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증성공 후생성 JWT 토큰，및 저장 Security 컨텍스트，로그인 로그용 AOP 사용（인증 완료）
        AuthenticationToken token = tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return token;
    }

    /**
     * 위챗미니 프로그램휴대폰 번호로그인
     *
     * @param loginDTO 로그인参수
     * @return 접근토큰
     */
    @Override
    public AuthenticationToken loginByWxMiniAppPhone(WxMiniAppPhoneLoginDTO loginDTO) {
        // 위챗 미니 프로그램 휴대폰 번호 인증 생성Token
        WxMiniAppPhoneAuthenticationToken authenticationToken = new WxMiniAppPhoneAuthenticationToken(
                loginDTO.getCode(),
                loginDTO.getEncryptedData(),
                loginDTO.getIv()
        );

        // 인증 실행
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 인증성공 후생성JWT토큰，및 저장Security컨텍스트
        AuthenticationToken token = tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return token;
    }

}
