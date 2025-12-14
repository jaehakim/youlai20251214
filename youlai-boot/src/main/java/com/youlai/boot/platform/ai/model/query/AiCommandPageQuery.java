package com.youlai.boot.platform.ai.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * AI 명령 기록 페이징 조회 객체
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Schema(description = "AI 명령 기록 페이징 조회 객체")
@Getter
@Setter
public class AiCommandPageQuery extends BasePageQuery {

    @Schema(description = "키워드 (원본 명령/함수 이름/사용자명)")
    private String keywords;

    @Schema(description = "실행 상태(pending-대기 중, success-성공, failed-실패)")
    private String executeStatus;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "위험한 작업 여부")
    private Boolean isDangerous;

    @Schema(description = "생성 시간 범위")
    private List<String> createTime;

    @Schema(description = "함수 이름")
    private String functionName;
}

