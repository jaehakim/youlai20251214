package com.youlai.boot.auth.service;

import com.youlai.boot.auth.model.vo.CaptchaVO;
import com.youlai.boot.auth.model.dto.WxMiniAppPhoneLoginDTO;
import com.youlai.boot.security.model.AuthenticationToken;
import com.youlai.boot.auth.model.dto.WxMiniAppCodeLoginDTO;

/**
 * 인증 서비스 인터페이스
 *
 * @author Ray.Hao
 * @since 2.4.0
 */
public interface AuthService {

    /**
     * 로그인
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @return 로그인결과
     */
    AuthenticationToken login(String username, String password);

    /**
     * 로그아웃
     */
    void logout();

    /**
     * 인증코드 조회
     *
     * @return 인증코드
     */
    CaptchaVO getCaptcha();

    /**
     * 토큰 갱신
     *
     * @param refreshToken 토큰 갱신
     * @return 로그인결과
     */
    AuthenticationToken refreshToken(String refreshToken);

    /**
     * 위챗 미니 프로그램 로그인
     *
     * @param code 위챗 로그인code
     * @return 로그인결과
     */
    AuthenticationToken loginByWechat(String code);

    /**
     * 위챗미니 프로그램Code로그인
     *
     * @param loginDTO 로그인参수
     * @return 접근토큰
     */
    AuthenticationToken loginByWxMiniAppCode(WxMiniAppCodeLoginDTO loginDTO);

    /**
     * 위챗미니 프로그램휴대폰 번호로그인
     *
     * @param loginDTO 로그인参수
     * @return 접근토큰
     */
    AuthenticationToken loginByWxMiniAppPhone(WxMiniAppPhoneLoginDTO loginDTO);

    /**
     * 발송SMS인증코드
     *
     * @param mobile 휴대폰 번호
     */
    void sendSmsLoginCode(String mobile);

    /**
     * SMS 인증 로그인
     *
     * @param mobile 휴대폰 번호
     * @param code   인증코드
     * @return 로그인결과
     */
    AuthenticationToken loginBySms(String mobile, String code);
}
