package com.youlai.boot.core.web;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

/**
 * 웹 응답 헬퍼 클래스
 * <p>
 * 필터, 핸들러 등 @RestControllerAdvice를 사용할 수 없는 시나리오에서 응답을 통합적으로 처리하기 위해 사용
 *
 * @author Ray.Hao
 * @since 2.0.0
 */
@Slf4j
public class WebResponseHelper {

    /**
     * 오류 응답 작성
     *
     * @param response   HttpServletResponse
     * @param resultCode 응답 결과 코드
     */
    public static void writeError(HttpServletResponse response, ResultCode resultCode) {
        writeError(response, resultCode, null);
    }

    /**
     * 오류 응답 작성 (사용자 정의 메시지 포함)
     *
     * @param response   HttpServletResponse
     * @param resultCode 응답 결과 코드
     * @param message    사용자 정의 메시지
     */
    public static void writeError(HttpServletResponse response, ResultCode resultCode, String message) {
        try {
            // HTTP 상태 코드 설정
            int httpStatus = mapHttpStatus(resultCode);
            response.setStatus(httpStatus);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            // 응답 객체 구성
            Result<?> result = message == null
                    ? Result.failed(resultCode)
                    : Result.failed(resultCode, message);

            // 응답 작성
            JakartaServletUtil.write(response,
                    JSONUtil.toJsonStr(result),
                    MediaType.APPLICATION_JSON_VALUE
            );

        } catch (Exception e) {
            log.error("오류 응답 작성 실패: resultCode={}, message={}", resultCode, message, e);
        }
    }

    /**
     * 비즈니스 결과 코드를 HTTP 상태 코드로 매핑
     *
     * @param resultCode 비즈니스 결과 코드
     * @return HTTP 상태 코드
     */
    private static int mapHttpStatus(ResultCode resultCode) {
        return switch (resultCode) {
            case ACCESS_UNAUTHORIZED,
                    ACCESS_TOKEN_INVALID,
                    REFRESH_TOKEN_INVALID -> HttpStatus.UNAUTHORIZED.value();
            default -> HttpStatus.BAD_REQUEST.value();
        };
    }
}

