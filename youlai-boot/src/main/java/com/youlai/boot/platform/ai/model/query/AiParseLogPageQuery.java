package com.youlai.boot.platform.ai.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * AI 명령 파싱 로그 페이징 조회 객체
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Schema(description = "AI 명령 파싱 로그 페이징 조회 객체")
@Getter
@Setter
public class AiParseLogPageQuery extends BasePageQuery {

    @Schema(description = "키워드 (원본 명령/사용자명)")
    private String keywords;

    @Schema(description = "파싱 성공 여부 (0-실패, 1-성공)")
    private Boolean parseSuccess;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "AI 공급자 (qwen/openai/deepseek/gemini 등)")
    private String provider;

    @Schema(description = "AI 모델 (qwen-plus/qwen-max/gpt-4-turbo 등)")
    private String model;

    @Schema(description = "생성 시간 범위")
    private List<String> createTime;
}

