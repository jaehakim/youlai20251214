package com.youlai.boot.system.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 방문량 통계 비즈니스 객체
 *
 * @author Ray.Hao
 * @since 2024/7/2
 */
@Getter
@Setter
public class VisitStatsBO {

    @Schema(description = "오늘 방문량 (PV)")
    private Integer todayCount;

    @Schema(description = "누적 방문량")
    private Integer totalCount;

    @Schema(description = "페이지 방문량 증가율")
    private BigDecimal growthRate;

}
