package com.youlai.boot.platform.codegen.model.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 데이터 테이블 페이징 조회 객체
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(description = "데이터 테이블 페이징 조회 객체")
@Getter
@Setter
public class TablePageQuery extends BasePageQuery {

    @Schema(description="키워드 (테이블명)")
    private String keywords;

    /**
     * 제외할 테이블명
     */
    @JsonIgnore
    private List<String> excludeTables;

}
