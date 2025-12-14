package com.youlai.boot.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 부서 조회 객체
 *
 * @author haoxr
 * @since 2022/6/11
 */
@Schema(description ="부서 페이지 조회 객체")
@Data
public class DeptQuery {

    @Schema(description="키워드(부서명)")
    private String keywords;

    @Schema(description="상태(1->정상;0->비활성화)")
    private Integer status;

}
