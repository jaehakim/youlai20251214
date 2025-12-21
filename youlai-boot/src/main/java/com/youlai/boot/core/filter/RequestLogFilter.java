package com.youlai.boot.core.filter;

import com.youlai.boot.common.util.IPUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * 요청 로그 출력 필터
 *
 * @author haoxr
 * @since 2023/03/03
 */
@Configuration
@Slf4j
public class RequestLogFilter extends CommonsRequestLoggingFilter {

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        // 로그 출력 레벨 설정, 기본값은 debug
        return this.logger.isInfoEnabled();
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        String requestURI = request.getRequestURI();
        String ip = IPUtils.getIpAddr(request);
        log.info("request,ip:{}, uri: {}", ip, requestURI);
        super.beforeRequest(request, message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        super.afterRequest(request, message);
    }

}
