package com.youlai.boot.auth.controller;

import com.youlai.boot.auth.model.vo.CaptchaVO;
import com.youlai.boot.auth.model.dto.WxMiniAppPhoneLoginDTO;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.auth.service.AuthService;
import com.youlai.boot.auth.model.dto.WxMiniAppCodeLoginDTO;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.security.model.AuthenticationToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * 인증 컨트롤러
 *
 * @author Ray.Hao
 * @since 2022/10/16
 */
@Tag(name = "01.인증센터")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "인증코드 조회")
    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        CaptchaVO captcha = authService.getCaptcha();
        return Result.success(captcha);
    }

    @Operation(summary = "계정 비밀번호 로그인")
    @PostMapping("/login")
    @Log(value = "로그인", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> login(
            @Parameter(description = "사용자명", example = "admin") @RequestParam String username,
            @Parameter(description = "비밀번호", example = "123456") @RequestParam String password
    ) {
        AuthenticationToken authenticationToken = authService.login(username, password);
        return Result.success(authenticationToken);
    }

    @Operation(summary = "SMS 인증 로그인")
    @PostMapping("/login/sms")
    @Log(value = "SMS 인증 로그인", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> loginBySms(
            @Parameter(description = "휴대폰 번호", example = "18812345678") @RequestParam String mobile,
            @Parameter(description = "인증코드", example = "1234") @RequestParam String code
    ) {
        AuthenticationToken loginResult = authService.loginBySms(mobile, code);
        return Result.success(loginResult);
    }

    @Operation(summary = "로그인 SMS 인증코드 전송")
    @PostMapping("/sms/code")
    public Result<Void> sendLoginVerifyCode(
            @Parameter(description = "휴대폰 번호", example = "18812345678") @RequestParam String mobile
    ) {
        authService.sendSmsLoginCode(mobile);
        return Result.success();
    }

    @Operation(summary = "위챗 인증 로그인(Web)")
    @PostMapping("/login/wechat")
    @Log(value = "위챗 로그인", module = LogModuleEnum.LOGIN)
    public Result<AuthenticationToken> loginByWechat(
            @Parameter(description = "위챗 인증코드", example = "code") @RequestParam String code
    ) {
        AuthenticationToken loginResult = authService.loginByWechat(code);
        return Result.success(loginResult);
    }

    @Operation(summary = "위챗 미니 프로그램 로그인(Code)")
    @PostMapping("/wx/miniapp/code-login")
    public Result<AuthenticationToken> loginByWxMiniAppCode(@RequestBody @Valid WxMiniAppCodeLoginDTO loginDTO) {
        AuthenticationToken token = authService.loginByWxMiniAppCode(loginDTO);
        return Result.success(token);
    }

    @Operation(summary = "위챗 미니 프로그램 로그인(휴대폰 번호)")
    @PostMapping("/wx/miniapp/phone-login")
    public Result<AuthenticationToken> loginByWxMiniAppPhone(@RequestBody @Valid WxMiniAppPhoneLoginDTO loginDTO) {
        AuthenticationToken token = authService.loginByWxMiniAppPhone(loginDTO);
        return Result.success(token);
    }


    @Operation(summary = "로그아웃")
    @DeleteMapping("/logout")
    @Log(value = "로그아웃", module = LogModuleEnum.LOGIN)
    public Result<?> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refresh-token")
    public Result<?> refreshToken(
            @Parameter(description = "갱신 토큰", example = "xxx.xxx.xxx") @RequestParam String refreshToken
    ) {
        AuthenticationToken authenticationToken = authService.refreshToken(refreshToken);
        return Result.success(authenticationToken);
    }

}
