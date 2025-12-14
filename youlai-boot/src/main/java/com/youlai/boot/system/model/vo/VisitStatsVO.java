package com.youlai.boot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 방문량 통계 뷰 객체
 *
 * @author Ray.Hao
 * @since 2024/7/2
 */
@Schema(description = "방문량 통계 뷰 객체")
@Getter
@Setter
public class VisitStatsVO {

    @Schema(description = "오늘 순방문자수 (UV)")
    private Integer todayUvCount;

    @Schema(description = "누적 순방문자수 (UV)")
    private Integer totalUvCount;

    @Schema(description = "순방문자 증가율")
    private BigDecimal uvGrowthRate;

    @Schema(description = "오늘 페이지 조회수 (PV)")
    private Integer todayPvCount;

    @Schema(description = "누적 페이지 조회수 (PV)")
    private Integer totalPvCount;

    @Schema(description = "페이지 조회수 증가율")
    private BigDecimal pvGrowthRate;

}
