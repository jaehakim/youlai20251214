package com.youlai.boot.system.model.query;


import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description ="사전 항목 페이지 조회 객체")
public class DictItemPageQuery extends BasePageQuery {

    @Schema(description="키워드(사전 항목값/사전 항목명)")
    private String keywords;

    @Schema(description="사전 코드")
    private String dictCode;

}
