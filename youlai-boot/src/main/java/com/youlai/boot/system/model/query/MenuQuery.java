package com.youlai.boot.system.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 메뉴 조회 객체
 *
 * @author haoxr
 * @since 2022/10/28
 */
@Schema(description ="메뉴 조회 객체")
@Data
public class MenuQuery {

    @Schema(description="키워드(메뉴명)")
    private String keywords;

    @Schema(description="상태(1->표시;0->숨김)")
    private Integer status;

}
