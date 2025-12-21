package com.youlai.boot.platform.ai.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI 명령 기록 엔티티 (파싱 및 실행 기록 통합)
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_command_record")
public class AiCommandRecord extends BaseEntity {

    /** 사용자 ID */
    private Long userId;

    /** 사용자명 */
    private String username;

    /** 원본 명령 */
    private String originalCommand;

    // ==================== 파싱 관련 필드 ====================

    /** AI 공급자 (qwen/openai/deepseek 등) */
    private String provider;

    /** AI 모델 (qwen-plus/qwen-max/gpt-4-turbo 등) */
    private String model;

    /** 파싱 성공 여부 */
    private Boolean parseSuccess;

    /** 파싱된 함수 호출 목록 (JSON) */
    private String functionCalls;

    /** AI의 이해 설명 */
    private String explanation;

    /** 신뢰도 (0.00-1.00) */
    private BigDecimal confidence;

    /** 解析오류 정보 */
    private String parseErrorMessage;

    /** 입력 토큰 수 */
    private Integer inputTokens;

    /** 출력 토큰 수 */
    private Integer outputTokens;

    /** 총 토큰 수 */
    private Integer totalTokens;

    /** 파싱 소요 시간 (밀리초) */
    private Long parseTime;

    // ==================== 실행 관련 필드 ====================

    /** 执行的함수 이름 */
    private String functionName;

    /** 함수 파라미터 (JSON) */
    private String functionArguments;

    /** 실행 상태: pending, success, failed */
    private String executeStatus;

    /** 실행 결과 (JSON) */
    private String executeResult;

    /** 执行오류 정보 */
    private String executeErrorMessage;

    /** 영향받은 레코드 수 */
    private Integer affectedRows;

    /** 위험한 작업 여부 */
    private Boolean isDangerous;

    /** 확인 필요 여부 */
    private Boolean requiresConfirmation;

    /** 사용자 확인 여부 */
    private Boolean userConfirmed;

    /** 멱등성 토큰 (중복 실행 방지) */
    private String idempotencyKey;

    /** 실행 소요 시간 (밀리초) */
    private Long executionTime;

    // ==================== 공통 필드 ====================

    /** IP 주소 */
    private String ipAddress;

    /** 사용자 에이전트 */
    private String userAgent;

    /** 현재 페이지 라우트 */
    private String currentRoute;

    /** 비고 */
    private String remark;
}


