package com.youlai.boot.security.provider;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.security.model.SysUserDetails;
import com.youlai.boot.security.model.UserAuthCredentials;
import com.youlai.boot.security.model.WxMiniAppPhoneAuthenticationToken;
import com.youlai.boot.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 위챗 미니프로그램 휴대폰 번호 인증 Provider
 *
 * @author YouLai Tech Team
 * @since 2.0.0
 */
@Slf4j
public class WxMiniAppPhoneAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final WxMaService wxMaService;

    public WxMiniAppPhoneAuthenticationProvider(UserService userService, WxMaService wxMaService) {
        this.userService = userService;
        this.wxMaService = wxMaService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WxMiniAppPhoneAuthenticationToken authenticationToken = (WxMiniAppPhoneAuthenticationToken) authentication;
        String code = (String) authenticationToken.getPrincipal();
        String encryptedData = authenticationToken.getEncryptedData();
        String iv = authenticationToken.getIv();

        // 1. code를 통해 session_key 획득
        WxMaJscode2SessionResult sessionInfo;
        try {
            sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            log.error("위챗 session_key 획득 실패", e);
            throw new CredentialsExpiredException("위챗 로그인 코드가 유효하지 않거나 만료되었습니다");
        }

        String sessionKey = sessionInfo.getSessionKey();
        String openId = sessionInfo.getOpenid();

        if (StrUtil.isBlank(sessionKey) || StrUtil.isBlank(openId)) {
            throw new CredentialsExpiredException("위챗 세션 정보 획득 실패");
        }

        // 2. 휴대폰 번호 정보 복호화
        WxMaPhoneNumberInfo phoneNumberInfo;
        try {
            if (StrUtil.isNotBlank(encryptedData) && StrUtil.isNotBlank(iv)) {
                phoneNumberInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
            } else {
                throw new IllegalArgumentException("휴대폰 번호 암호화 데이터 누락");
            }
        } catch (Exception e) {
            log.error("위챗 휴대폰 번호 복호화 실패", e);
            throw new CredentialsExpiredException("휴대폰 번호 정보 복호화 실패");
        }

        if (phoneNumberInfo == null || StrUtil.isBlank(phoneNumberInfo.getPhoneNumber())) {
            throw new CredentialsExpiredException("휴대폰 번호 획득 실패");
        }

        String phoneNumber = phoneNumberInfo.getPhoneNumber();

        // 3. 휴대폰 번호로 사용자 조회, 존재하지 않으면 신규 사용자 생성
        UserAuthCredentials userAuthCredentials = userService.getAuthCredentialsByMobile(phoneNumber);

        if (userAuthCredentials == null) {
            // 사용자가 존재하지 않으면 신규 사용자 등록
            boolean registered = userService.registerUserByMobileAndOpenId(phoneNumber, openId);
            if (!registered) {
                throw new UsernameNotFoundException("사용자 등록 실패");
            }
            // 사용자 정보 재획득
            userAuthCredentials = userService.getAuthCredentialsByMobile(phoneNumber);
        } else {
            // 사용자가 존재하면 openId 바인딩 (미바인딩 시)
            userService.bindUserOpenId(userAuthCredentials.getUserId(), openId);
        }

        // 4. 사용자 상태 확인
        if (ObjectUtil.notEqual(userAuthCredentials.getStatus(), 1)) {
            throw new DisabledException("사용자가 비활성화되었습니다");
        }

        // 5. 인증된 사용자 세부 정보 구성
        SysUserDetails userDetails = new SysUserDetails(userAuthCredentials);

        // 6. 인증된 토큰 생성
        return WxMiniAppPhoneAuthenticationToken.authenticated(
                userDetails,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WxMiniAppPhoneAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 