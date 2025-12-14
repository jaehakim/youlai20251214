package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 시스템 설정 조회 객체
 *
 * @author Theo
 * @since 2024-7-29 11:38:00
 */
@Getter
@Setter
@Schema(description = "시스템 설정 페이징 조회")
public class ConfigPageQuery extends BasePageQuery {

    @Schema(description="키워드 (설정 항목명/설정 항목값)")
    private String keywords;
}
