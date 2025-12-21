package com.youlai.boot.security.filter;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.core.web.WebResponseHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * 그래픽 캡차 검증 필터
 *
 * @author haoxr
 * @since 2022/10/1
 */
public class CaptchaValidationFilter extends OncePerRequestFilter {

    private static final RequestMatcher LOGIN_PATH_REQUEST_MATCHER = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST,SecurityConstants.LOGIN_PATH);

    public static final String CAPTCHA_CODE_PARAM_NAME = "captchaCode";
    public static final String CAPTCHA_KEY_PARAM_NAME = "captchaKey";

    private final RedisTemplate<String, Object> redisTemplate;

    private final CodeGenerator codeGenerator;

    public CaptchaValidationFilter(RedisTemplate<String, Object> redisTemplate, CodeGenerator codeGenerator) {
        this.redisTemplate = redisTemplate;
        this.codeGenerator = codeGenerator;
    }


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 로그인 인터페이스의 캡차 검증
        if (LOGIN_PATH_REQUEST_MATCHER.matches(request)) {
            // 요청의 캡차 코드
            String captchaCode = request.getParameter(CAPTCHA_CODE_PARAM_NAME);
            // TODO 캡차가 없는 버전과 호환 (운영 환경에서는 이 판단을 제거하세요)
            if (StrUtil.isBlank(captchaCode)) {
                chain.doFilter(request, response);
                return;
            }
            // 캐시의 캡차 코드
            String verifyCodeKey = request.getParameter(CAPTCHA_KEY_PARAM_NAME);
            String cacheVerifyCode = (String) redisTemplate.opsForValue().get(
                    StrUtil.format(RedisConstants.Captcha.IMAGE_CODE, verifyCodeKey)
            );
            if (cacheVerifyCode == null) {
                WebResponseHelper.writeError(response, ResultCode.USER_VERIFICATION_CODE_EXPIRED);
            } else {
                // 캡차 코드 비교
                if (codeGenerator.verify(cacheVerifyCode, captchaCode)) {
                    chain.doFilter(request, response);
                } else {
                    WebResponseHelper.writeError(response, ResultCode.USER_VERIFICATION_CODE_ERROR);
                }
            }
        } else {
            // 비로그인 인터페이스는 통과
            chain.doFilter(request, response);
        }
    }

}
