package com.youlai.boot.system.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 사전 항목 페이지 객체
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
@Schema(description = "사전 항목 페이지 객체")
@Getter
@Setter
public class DictItemPageVO {

    @Schema(description = "사전 항목 ID")
    private Long id;

    @Schema(description = "사전 코드")
    private String dictCode;

    @Schema(description = "사전 레이블")
    private String label;

    @Schema(description = "사전값")
    private String value;

    @Schema(description = "정렬")
    private Integer sort;

    @Schema(description = "상태（1:활성화，0:비활성화）")
    private Integer status;

}
