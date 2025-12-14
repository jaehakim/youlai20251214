package com.youlai.boot.platform.ai.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * AI 命令记录实体（合并解析和执行记录）
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

    /** 原始命令 */
    private String originalCommand;

    // ==================== 解析相关字段 ====================

    /** AI 供应商（qwen/openai/deepseek等） */
    private String provider;

    /** AI 模型（qwen-plus/qwen-max/gpt-4-turbo等） */
    private String model;

    /** 解析是否成功 */
    private Boolean parseSuccess;

    /** 解析出的函数调用列表（JSON） */
    private String functionCalls;

    /** AI 的理解说明 */
    private String explanation;

    /** 置信度（0.00-1.00） */
    private BigDecimal confidence;

    /** 解析오류 정보 */
    private String parseErrorMessage;

    /** 输入 Token 数量 */
    private Integer inputTokens;

    /** 输出 Token 数量 */
    private Integer outputTokens;

    /** 总 Token 数量 */
    private Integer totalTokens;

    /** 解析耗时（毫秒） */
    private Long parseTime;

    // ==================== 执行相关字段 ====================

    /** 执行的함수 이름 */
    private String functionName;

    /** 函数参数（JSON） */
    private String functionArguments;

    /** 执行상태：pending, success, failed */
    private String executeStatus;

    /** 执行结果（JSON） */
    private String executeResult;

    /** 执行오류 정보 */
    private String executeErrorMessage;

    /** 영향받은 레코드 수 */
    private Integer affectedRows;

    /** 위험한 작업 여부 */
    private Boolean isDangerous;

    /** 是否需要确认 */
    private Boolean requiresConfirmation;

    /** 用户是否确认 */
    private Boolean userConfirmed;

    /** 幂等性令牌（防止重复执行） */
    private String idempotencyKey;

    /** 执行耗时（毫秒） */
    private Long executionTime;

    // ==================== 通用字段 ====================

    /** IP 地址 */
    private String ipAddress;

    /** 사용자 에이전트 */
    private String userAgent;

    /** 현재 페이지 라우트 */
    private String currentRoute;

    /** 비고 */
    private String remark;
}


