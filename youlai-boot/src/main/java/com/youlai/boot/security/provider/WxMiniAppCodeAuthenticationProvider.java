package com.youlai.boot.security.provider;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.security.model.SysUserDetails;
import com.youlai.boot.security.model.UserAuthCredentials;
import com.youlai.boot.security.model.WxMiniAppCodeAuthenticationToken;
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
 * 위챗 미니프로그램 코드 인증 Provider
 *
 * @author YouLai Tech Team
 * @since 2.0.0
 */
@Slf4j
public class WxMiniAppCodeAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final WxMaService wxMaService;


    public WxMiniAppCodeAuthenticationProvider(UserService userService, WxMaService wxMaService) {
        this.userService = userService;
        this.wxMaService = wxMaService;
    }


    /**
     * 위챗 인증 로직, Spring Security 비밀번호 검증 프로세스 참고
     *
     * @param authentication 인증 객체
     * @return 인증된 Authentication 객체
     * @throws AuthenticationException 인증 예외
     * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String code = (String) authentication.getPrincipal();

        // 위챗 서버를 통해 code를 검증하고 사용자 세션 정보 획득
        WxMaJscode2SessionResult sessionInfo;
        try {
            sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        } catch (WxErrorException e) {
            throw new CredentialsExpiredException("위챗 로그인 코드가 유효하지 않거나 만료되었습니다. 다시 획득해주세요");
        }

        String openId = sessionInfo.getOpenid();
        if (StrUtil.isBlank(openId)) {
            throw new UsernameNotFoundException("위챗 OpenID를 가져올 수 없습니다. 나중에 다시 시도해주세요");
        }

        // 위챗 OpenID로 사용자 정보 조회
        UserAuthCredentials userAuthCredentials = userService.getAuthCredentialsByOpenId(openId);

        if (userAuthCredentials == null) {
            // 사용자가 존재하지 않으면 등록
            userService.registerOrBindWechatUser(openId);

            // 사용자 정보 재조회, 사용자 등록 성공 확인
            userAuthCredentials = userService.getAuthCredentialsByOpenId(openId);
            if (userAuthCredentials == null) {
                throw new UsernameNotFoundException("사용자 등록에 실패했습니다. 나중에 다시 시도해주세요");
            }
        }

        // 사용자 상태가 유효한지 확인
        if (ObjectUtil.notEqual(userAuthCredentials.getStatus(), 1)) {
            throw new DisabledException("사용자가 비활성화되었습니다");
        }

        // 인증된 사용자 세부 정보 구성
        SysUserDetails userDetails = new SysUserDetails(userAuthCredentials);

        // 인증된 토큰 생성
        return WxMiniAppCodeAuthenticationToken.authenticated(
                userDetails,
                userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WxMiniAppCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
} 