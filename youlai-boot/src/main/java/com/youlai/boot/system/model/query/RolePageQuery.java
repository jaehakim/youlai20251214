package com.youlai.boot.system.model.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 역할 페이지 조회 객체
 *
 * @author Ray
 * @since 2022/6/3
 */
@Schema(description = "역할 페이지 조회 객체")
@Getter
@Setter
public class RolePageQuery extends BasePageQuery {

    @Schema(description="키워드(역할명/역할 코드)")
    private String keywords;

    @Schema(description="시작 날짜")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    @Schema(description="종료 날짜")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;
}
