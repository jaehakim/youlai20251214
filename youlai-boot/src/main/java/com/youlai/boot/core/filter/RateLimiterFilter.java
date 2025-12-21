package com.youlai.boot.core.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.common.util.IPUtils;
import com.youlai.boot.core.web.WebResponseHelper;
import com.youlai.boot.system.service.ConfigService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * IP 속도 제한 필터
 *
 * @author Theo
 * @since 2024/08/10 14:38
 */
@Slf4j
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ConfigService configService;

    private static final long DEFAULT_IP_LIMIT = 10L; // 기본 IP 속도 제한 임계값

    public RateLimiterFilter(RedisTemplate<String, Object> redisTemplate, ConfigService configService) {
        this.redisTemplate = redisTemplate;
        this.configService = configService;
    }

    /**
     * IP가 속도 제한을 트리거했는지 판단
     * 기본적으로 동일 IP는 초당 최대 10회 요청으로 제한되며, 시스템 설정을 통해 조정 가능
     * 시스템에 속도 제한 임계값이 설정되지 않은 경우, 기본적으로 속도 제한을 건너뜀
     *
     * @param ip IP 주소
     * @return 속도 제한 여부: true는 제한됨, false는 제한되지 않음
     */
    public boolean rateLimit(String ip) {
        // 속도 제한 Redis 키
        String key = StrUtil.format(RedisConstants.RateLimiter.IP, ip);

        // 요청 카운트 증가
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null || count == 1) {
            // 첫 번째 접근 시 만료 시간을 1초로 설정
            redisTemplate.expire(key, 1, TimeUnit.SECONDS);
        }

        // 시스템 설정된 속도 제한 임계값 가져오기
        Object systemConfig = configService.getSystemConfig(SystemConstants.SYSTEM_CONFIG_IP_QPS_LIMIT_KEY);
        if (systemConfig == null) {
            // 시스템에 속도 제한이 설정되지 않음, 속도 제한 로직 건너뛰기
            log.warn("시스템에 속도 제한 임계값이 설정되지 않아 속도 제한을 건너뜁니다");
            return false;
        }

        // 시스템 설정을 속도 제한 값으로 변환, 기본값은 10
        long limit = Convert.toLong(systemConfig, DEFAULT_IP_LIMIT);
        return count != null && count > limit;
    }

    /**
     * IP 속도 제한 로직 실행
     * IP 요청이 제한을 초과하면 속도 제한 응답을 직접 반환하고, 그렇지 않으면 필터 체인 계속 실행
     *
     * @param request     요청 본문
     * @param response    응답 본문
     * @param filterChain 필터 체인
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 요청의 IP 주소 가져오기
        String ip = IPUtils.getIpAddr(request);

        // 속도 제한 여부 판단
        if (rateLimit(ip)) {
            // 속도 제한 오류 정보 반환
            WebResponseHelper.writeError(response, ResultCode.REQUEST_CONCURRENCY_LIMIT_EXCEEDED);
            return;
        }

        // 속도 제한이 트리거되지 않음, 필터 체인 계속 실행
        filterChain.doFilter(request, response);
    }
}
