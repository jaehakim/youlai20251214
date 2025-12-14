package com.youlai.boot.platform.ai.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 명령 실행 응답 DTO
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiExecuteResponseDTO {

    /**
     * 실행 성공 여부
     */
    private Boolean success;

    /**
     * 실행 결과 데이터
     */
    private Object data;

    /**
     * 실행 결과 설명
     */
    private String message;

    /**
     * 영향받은 레코드 수
     */
    private Integer affectedRows;

    /**
     * 오류 정보
     */
    private String error;

    /**
     * 레코드 ID (추적용)
     */
    private Long recordId;

    /**
     * 사용자 확인 필요
     */
    private Boolean requiresConfirmation;

    /**
     * 확인 프롬프트 메시지
     */
    private String confirmationPrompt;
}



