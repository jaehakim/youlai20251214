package com.youlai.boot.system.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 사전 항목 키값 객체
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
@Schema(description = "사전 항목 옵션 객체")
@Getter
@Setter
public class DictItemOptionVO {

    @Schema(description = "사전 항목값")
    private String value;

    @Schema(description = "사전 항목 레이블")
    private String label;

    @Schema(description = "레이블 유형")
    private String tagType;

}
