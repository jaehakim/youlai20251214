package com.youlai.boot.platform.ai.model.dto;

import lombok.Data;

/**
 * AI 명령 실행 요청 DTO
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
public class AiExecuteRequestDTO {

    /**
     * 연관된 파싱 로그 ID
     */
    private String parseLogId;

    /**
     * 원본 명령 (감사용)
     */
    private String originalCommand;

    /**
     * 실행할 함수 호출
     */
    private AiFunctionCallDTO functionCall;

    /**
     * 확인 모드：auto=자동 실행, manual=사용자 확인 필요
     */
    private String confirmMode;

    /**
     * 사용자 확인 플래그
     */
    private Boolean userConfirmed;

    /**
     * 멱등성 토큰 (중복 실행 방지)
     */
    private String idempotencyKey;

    /**
     * 현재 페이지 라우트
     */
    private String currentRoute;
}



