package com.youlai.boot.core.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.common.constant.SecurityConstants;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.common.annotation.RepeatSubmit;
import com.youlai.boot.common.util.IPUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * 중복 제출 방지 Aspect
 *
 * @author Ray.Hao
 * @since 2.3.0
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RepeatSubmitAspect {

    private final RedissonClient redissonClient;

    /**
     * 중복 제출 방지 Pointcut
     */
    @Pointcut("@annotation(repeatSubmit)")
    public void repeatSubmitPointCut(RepeatSubmit repeatSubmit) {
    }

    /**
     * Around Advice: 중복 제출 방지 로직 처리
     */
    @Around(value = "repeatSubmitPointCut(repeatSubmit)", argNames = "pjp,repeatSubmit")
    public Object handleRepeatSubmit(ProceedingJoinPoint pjp, RepeatSubmit repeatSubmit) throws Throwable {
        String lockKey = buildLockKey();

        int expire = repeatSubmit.expire();
        RLock lock = redissonClient.getLock(lockKey);

        boolean locked = lock.tryLock(0, expire, TimeUnit.SECONDS);
        if (!locked) {
            throw new BusinessException(ResultCode.USER_DUPLICATE_REQUEST);
        }
        return pjp.proceed();
    }

    /**
     * 중복 제출 방지 락의 key 생성
     * @return 락의 key
     */
    private String buildLockKey() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 사용자 고유 식별자
        String userIdentifier = getUserIdentifier(request);
        // 요청 고유 식별자 = 요청 메서드 + 요청 경로 + 요청 파라미터 (엄격한 방법)
        String requestIdentifier = StrUtil.join(":", request.getMethod(), request.getRequestURI());
        return StrUtil.format(RedisConstants.Lock.RESUBMIT, userIdentifier, requestIdentifier);
    }

    /**
     * 사용자 고유 식별자 가져오기
     * 1. 요청 헤더에서 Token을 가져와 SHA-256으로 암호화하여 사용자 고유 식별자로 사용
     * 2. Token이 비어 있으면 IP를 사용자 고유 식별자로 사용
     *
     * @param request 요청 객체
     * @return 사용자 고유 식별자
     */
    private String getUserIdentifier(HttpServletRequest request) {
        // 사용자 신원 고유 식별자
        String userIdentifier;
        // 요청 헤더에서 Token 가져오기
        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isNotBlank(tokenHeader) && tokenHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {
            String rawToken = tokenHeader.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());  // Bearer를 제거한 Token
            userIdentifier = DigestUtil.sha256Hex(rawToken); // SHA-256으로 Token을 암호화하여 사용자 고유 식별자로 사용
        } else {
            userIdentifier = IPUtils.getIpAddr(request); // IP를 사용자 고유 식별자로 사용
        }
        return userIdentifier;
    }


}

