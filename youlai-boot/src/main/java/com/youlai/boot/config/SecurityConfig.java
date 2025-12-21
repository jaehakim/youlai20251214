package com.youlai.boot.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.ArrayUtil;
import com.youlai.boot.config.property.SecurityProperties;
import com.youlai.boot.core.filter.RateLimiterFilter;
import com.youlai.boot.security.filter.CaptchaValidationFilter;
import com.youlai.boot.security.filter.TokenAuthenticationFilter;
import com.youlai.boot.security.handler.MyAccessDeniedHandler;
import com.youlai.boot.security.handler.MyAuthenticationEntryPoint;
import com.youlai.boot.security.provider.SmsAuthenticationProvider;
import com.youlai.boot.security.provider.WxMiniAppCodeAuthenticationProvider;
import com.youlai.boot.security.provider.WxMiniAppPhoneAuthenticationProvider;
import com.youlai.boot.security.token.TokenManager;
import com.youlai.boot.security.service.SysUserDetailsService;
import com.youlai.boot.system.service.ConfigService;
import com.youlai.boot.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 *
 * @author Ray.Hao
 * @since 2023/2/17
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private final TokenManager tokenManager;
    private final WxMaService wxMaService;
    private final UserService userService;
    private final SysUserDetailsService userDetailsService;

    private final CodeGenerator codeGenerator;
    private final ConfigService configService;
    private final SecurityProperties securityProperties;

    /**
     * 보안 필터 체인 SecurityFilterChain 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(requestMatcherRegistry -> {
                            // 로그인 없이 접근 가능한 공개 인터페이스 설정
                            String[] ignoreUrls = securityProperties.getIgnoreUrls();
                            if (ArrayUtil.isNotEmpty(ignoreUrls)) {
                                requestMatcherRegistry.requestMatchers(ignoreUrls).permitAll();
                            }
                            // 기타 모든 요청은 로그인 후 접근
                            requestMatcherRegistry.anyRequest().authenticated();
                        }
                )
                .exceptionHandling(configurer ->
                        configurer
                                .authenticationEntryPoint(new MyAuthenticationEntryPoint()) // 미인증 예외 처리기
                                .accessDeniedHandler(new MyAccessDeniedHandler()) // 권한 없음 예외 처리기
                )

                // 기본 Spring Security 기능 비활성화, 프론트엔드-백엔드 분리 아키텍처에 적합
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 무상태 인증, Session 사용 안 함
                )
                .csrf(AbstractHttpConfigurer::disable)      // CSRF 방어 비활성화, 프론트엔드-백엔드 분리에서는 이 방어 메커니즘 불필요
                .formLogin(AbstractHttpConfigurer::disable) // 기본 폼 로그인 기능 비활성화, 프론트엔드-백엔드 분리는 Token 인증 방식 사용
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증 비활성화, 팝업 로그인 방지
                // X-Frame-Options 응답 헤더 비활성화, 페이지를 iframe에 포함 가능
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // 속도 제한 필터
                .addFilterBefore(new RateLimiterFilter(redisTemplate, configService), UsernamePasswordAuthenticationFilter.class)
                // 인증 코드 검증 필터
                .addFilterBefore(new CaptchaValidationFilter(redisTemplate, codeGenerator), UsernamePasswordAuthenticationFilter.class)
                // 검증 및 파싱 필터
                .addFilterBefore(new TokenAuthenticationFilter(tokenManager), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 웹 보안 커스터마이저를 설정하여 특정 요청 경로의 보안 검사를 무시합니다.
     * <p>
     * 이 설정은 어떤 요청 경로가 Spring Security 필터 체인을 거치지 않을지 지정하는 데 사용됩니다. 일반적으로 정적 리소스 파일에 사용됩니다.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            String[] unsecuredUrls = securityProperties.getUnsecuredUrls();
            if (ArrayUtil.isNotEmpty(unsecuredUrls)) {
                web.ignoring().requestMatchers(unsecuredUrls);
            }
        };
    }

    /**
     * 기본 비밀번호 인증 Provider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    /**
     * 위챗 미니프로그램 Code 인증 Provider
     */
    @Bean
    public WxMiniAppCodeAuthenticationProvider wxMiniAppCodeAuthenticationProvider() {
        return new WxMiniAppCodeAuthenticationProvider(userService, wxMaService);
    }

    /**
     * 위챗 미니프로그램 전화번호 인증 Provider
     */
    @Bean
    public WxMiniAppPhoneAuthenticationProvider wxMiniAppPhoneAuthenticationProvider() {
        return new WxMiniAppPhoneAuthenticationProvider(userService, wxMaService);
    }

    /**
     * SMS 인증 코드 인증 Provider
     */
    @Bean
    public SmsAuthenticationProvider smsAuthenticationProvider() {
        return new SmsAuthenticationProvider(userService, redisTemplate);
    }

    /**
     * 인증 관리자
     */
    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider daoAuthenticationProvider,
            WxMiniAppCodeAuthenticationProvider wxMiniAppCodeAuthenticationProvider,
            WxMiniAppPhoneAuthenticationProvider wxMiniAppPhoneAuthenticationProvider,
            SmsAuthenticationProvider smsAuthenticationProvider
    ) {
        return new ProviderManager(
                daoAuthenticationProvider,
                wxMiniAppCodeAuthenticationProvider,
                wxMiniAppPhoneAuthenticationProvider,
                smsAuthenticationProvider
        );
    }
}
