package com.youlai.boot.system.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 방문 추세 VO
 *
 * @author Ray.Hao
 * @since 2.3.0
 */
@Schema(description = "방문 추세 VO")
@Getter
@Setter
public class VisitTrendVO {

    @Schema(description = "날짜 목록")
    private List<String> dates;

    @Schema(description = "조회수(PV)")
    private List<Integer> pvList;

    @Schema(description = "IP 수")
    private List<Integer> ipList;

}
