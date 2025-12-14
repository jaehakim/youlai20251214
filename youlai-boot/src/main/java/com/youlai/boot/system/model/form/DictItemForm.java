package com.youlai.boot.system.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 사전 항목 폼 객체
 *
 * @author Ray Hao
 * @since 2.9.0
 */
@Schema(description = "사전 항목 폼")
@Data
public class DictItemForm {

    @Schema(description = "사전 항목 ID")
    private Long id;

    @Schema(description = "사전 코드")
    private String dictCode;

    @Schema(description = "사전 항목값")
    private String value;

    @Schema(description = "사전 항목 라벨")
    private String label;

    @Schema(description = "정렬")
    private Integer sort;

    @Schema(description = "상태 (0: 비활성화, 1: 활성화)")
    private Integer status;

    @Schema(description = "사전 유형 (표시 스타일용)")
    private String tagType;

}
