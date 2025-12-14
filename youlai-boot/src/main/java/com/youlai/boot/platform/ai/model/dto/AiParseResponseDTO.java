package com.youlai.boot.platform.ai.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * AI 파싱 응답 DTO
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiParseResponseDTO {

    /**
     * 파싱 로그 ID (실행 기록 연결용)
     */
    private Long parseLogId;

    /**
     * 파싱 성공 여부
     */
    private Boolean success;

    /**
     * 파싱된 함수 호출 목록
     */
    private List<AiFunctionCallDTO> functionCalls;

    /**
     * AI의 이해 및 설명
     */
    private String explanation;

    /**
     * 신뢰도 (0-1)
     */
    private Double confidence;

    /**
     * 오류 정보
     */
    private String error;

    /**
     * 원본 LLM 응답 (디버깅용)
     */
    private String rawResponse;
}

