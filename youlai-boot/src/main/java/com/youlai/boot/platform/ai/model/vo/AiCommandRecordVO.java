package com.youlai.boot.platform.ai.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI 명령 기록 VO (파싱 및 실행 기록 통합)
 */
@Data
@Schema(description = "AI 명령 기록 VO")
public class AiCommandRecordVO implements Serializable {

    @Schema(description = "기본 키 ID")
    private String id;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "사용자명")
    private String username;

    @Schema(description = "원본 명령")
    private String originalCommand;

    // ==================== 파싱 관련 필드 ====================

    @Schema(description = "AI 공급자")
    private String provider;

    @Schema(description = "AI 모델")
    private String model;

    @Schema(description = "파싱 성공 여부")
    private Boolean parseSuccess;

    @Schema(description = "파싱된 함수 호출 목록 (JSON)")
    private String functionCalls;

    @Schema(description = "AI의 이해 설명")
    private String explanation;

    @Schema(description = "신뢰도")
    private BigDecimal confidence;

    @Schema(description = "파싱 오류 정보")
    private String parseErrorMessage;

    @Schema(description = "입력 토큰 수량")
    private Integer inputTokens;

    @Schema(description = "출력 토큰 수량")
    private Integer outputTokens;

    @Schema(description = "총 토큰 수량")
    private Integer totalTokens;

    @Schema(description = "파싱 소요 시간 (밀리초)")
    private Long parseTime;

    // ==================== 실행 관련 필드 ====================

    @Schema(description = "실행된 함수 이름")
    private String functionName;

    @Schema(description = "함수 파라미터 (JSON)")
    private String functionArguments;

    @Schema(description = "실행 상태")
    private String executeStatus;

    @Schema(description = "실행 결과 (JSON)")
    private String executeResult;

    @Schema(description = "실행 오류 정보")
    private String executeErrorMessage;

    @Schema(description = "영향받은 레코드 수")
    private Integer affectedRows;

    @Schema(description = "위험한 작업 여부")
    private Boolean isDangerous;

    @Schema(description = "확인 필요 여부")
    private Boolean requiresConfirmation;

    @Schema(description = "사용자 확인 여부")
    private Boolean userConfirmed;

    @Schema(description = "실행 소요 시간 (밀리초)")
    private Long executionTime;

    // ==================== 공통 필드 ====================

    @Schema(description = "IP 주소")
    private String ipAddress;

    @Schema(description = "사용자 에이전트")
    private String userAgent;

    @Schema(description = "현재 페이지 라우트")
    private String currentRoute;

    @Schema(description = "생성 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "수정 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "비고")
    private String remark;
}


