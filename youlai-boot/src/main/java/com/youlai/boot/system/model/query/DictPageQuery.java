package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description ="사전 페이지 조회 객체")
public class DictPageQuery extends BasePageQuery {

    @Schema(description="키워드(사전명)")
    private String keywords;

}
