package com.youlai.boot.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 기본 페이징 요청 객체
 *
 * @author haoxr
 * @since 2021/2/28
 */
@Data
@Schema
public class BasePageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "페이지 번호", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum = 1;

    @Schema(description = "페이지당 레코드 수", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private int pageSize = 10;


}
