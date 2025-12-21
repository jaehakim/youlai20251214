package com.youlai.boot.core.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.HttpMethod;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.common.util.IPUtils;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.system.model.entity.Log;
import com.youlai.boot.system.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 로그 Aspect
 *
 * @author Ray.Hao
 * @since 2024/6/25
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    private final LogService logService;
    private final HttpServletRequest request;
    private final CacheManager cacheManager;

    /**
     * Pointcut
     */
    @Pointcut("@annotation(com.youlai.boot.common.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * 요청 처리 완료 후 실행
     *
     * @param joinPoint Pointcut
     */
    @Around("logPointcut() && @annotation(logAnnotation)")
    public Object doAround(ProceedingJoinPoint joinPoint, com.youlai.boot.common.annotation.Log logAnnotation) throws Throwable {
        // 메서드 실행 전에 사용자 ID를 가져옴, 메서드 실행 중 컨텍스트가 지워져서 사용자 ID를 가져올 수 없는 것을 방지
        Long userId = SecurityUtils.getUserId();
        
        TimeInterval timer = DateUtil.timer();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long executionTime = timer.interval(); // 실행 시간
            this.saveLog(joinPoint, exception, result, logAnnotation, executionTime, userId);
        }
        return result;
    }


    /**
     * 로그 저장
     *
     * @param joinPoint     Pointcut
     * @param e             예외
     * @param jsonResult    응답 결과
     * @param logAnnotation 로그 어노테이션
     * @param userId        사용자 ID
     */
    private void saveLog(final JoinPoint joinPoint, final Exception e, Object jsonResult, com.youlai.boot.common.annotation.Log logAnnotation, long executionTime, Long userId) {
        String requestURI = request.getRequestURI();
        // 로그 레코드 생성
        Log log = new Log();
        log.setExecutionTime(executionTime);
        if (logAnnotation == null && e != null) {
            log.setModule(LogModuleEnum.EXCEPTION);
            log.setContent("시스템 예외 발생");
            this.setRequestParameters(joinPoint, log);
            log.setResponseContent(JSONUtil.toJsonStr(e.getStackTrace()));
        } else {
            log.setModule(logAnnotation.module());
            log.setContent(logAnnotation.value());
            // 요청 파라미터
            if (logAnnotation.params()) {
                this.setRequestParameters(joinPoint, log);
            }
            // 응답 결과
            if (logAnnotation.result() && jsonResult != null) {
                log.setResponseContent(JSONUtil.toJsonStr(jsonResult));
            }
        }
        log.setRequestUri(requestURI);
        log.setCreateBy(userId);
        String ipAddr = IPUtils.getIpAddr(request);
        if (StrUtil.isNotBlank(ipAddr)) {
            log.setIp(ipAddr);
            String region = IPUtils.getRegion(ipAddr);
            // 중국|0|사천성|성도시|전신 - 성과 시 파싱
            if (StrUtil.isNotBlank(region)) {
                String[] regionArray = region.split("\\|");
                if (regionArray.length > 2) {
                    log.setProvince(regionArray[2]);
                    log.setCity(regionArray[3]);
                }
            }
        }


        // 브라우저 및 터미널 시스템 정보 가져오기
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = resolveUserAgent(userAgentString);
        if (Objects.nonNull(userAgent)) {
            // 시스템 정보
            log.setOs(userAgent.getOs().getName());
            // 브라우저 정보
            log.setBrowser(userAgent.getBrowser().getName());
            log.setBrowserVersion(userAgent.getBrowser().getVersion(userAgentString));
        }
        // 메서드명 가져오기
        String methodName = joinPoint.getSignature().getName();
        log.setMethod(methodName);
        // 로그를 데이터베이스에 저장
        logService.save(log);
    }

    /**
     * 로그 객체에 요청 파라미터 설정
     *
     * @param joinPoint Pointcut
     * @param log       작업 로그
     */
    private void setRequestParameters(JoinPoint joinPoint, Log log) {
        String requestMethod = request.getMethod();
        log.setRequestMethod(requestMethod);
        if (HttpMethod.GET.name().equalsIgnoreCase(requestMethod) || HttpMethod.PUT.name().equalsIgnoreCase(requestMethod) || HttpMethod.POST.name().equalsIgnoreCase(requestMethod)) {
            String params = convertArgumentsToString(joinPoint.getArgs());
            log.setRequestParams(StrUtil.sub(params, 0, 65535));
        } else {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                Map<?, ?> paramsMap = (Map<?, ?>) attributes.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                log.setRequestParams(StrUtil.sub(paramsMap.toString(), 0, 65535));
            } else {
                log.setRequestParams("");
            }
        }
    }

    /**
     * 파라미터 배열을 문자열로 변환
     *
     * @param paramsArray 파라미터 배열
     * @return 파라미터 문자열
     */
    private String convertArgumentsToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null) {
            for (Object param : paramsArray) {
                if (!shouldFilterObject(param)) {
                    params.append(JSONUtil.toJsonStr(param)).append(" ");
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 필터링이 필요한 객체인지 판단
     *
     * @param obj 객체 정보
     * @return 필터링이 필요한 객체이면 true 반환, 그렇지 않으면 false 반환
     */
    private boolean shouldFilterObject(Object obj) {
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            return MultipartFile.class.isAssignableFrom(clazz.getComponentType());
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection<?>) obj;
            return collection.stream().anyMatch(item -> item instanceof MultipartFile);
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map<?, ?> map = (Map<?, ?>) obj;
            return map.values().stream().anyMatch(value -> value instanceof MultipartFile);
        }
        return obj instanceof MultipartFile || obj instanceof HttpServletRequest || obj instanceof HttpServletResponse;
    }


    /**
     * UserAgent 파싱
     *
     * @param userAgentString UserAgent 문자열
     * @return UserAgent
     */
    public UserAgent resolveUserAgent(String userAgentString) {
        if (StrUtil.isBlank(userAgentString)) {
            return null;
        }
        // userAgentString을 MD5로 암호화하여 너무 길어지는 것을 방지
        String userAgentStringMD5 = DigestUtil.md5Hex(userAgentString);
        // 캐시 히트 여부 확인
        UserAgent userAgent = Objects.requireNonNull(cacheManager.getCache("userAgent")).get(userAgentStringMD5, UserAgent.class);
        if (userAgent != null) {
            return userAgent;
        }
        userAgent = UserAgentUtil.parse(userAgentString);
        Objects.requireNonNull(cacheManager.getCache("userAgent")).put(userAgentStringMD5, userAgent);
        return userAgent;
    }

}
