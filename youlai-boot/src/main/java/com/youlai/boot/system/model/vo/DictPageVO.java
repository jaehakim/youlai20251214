package com.youlai.boot.system.model.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * 사전 페이지 VO
 *
 * @author Ray
 * @since 0.0.1
 */
@Schema(description = "사전 페이지 객체")
@Getter
@Setter
public class DictPageVO {

    @Schema(description = "사전 ID")
    private Long id;

    @Schema(description = "사전명")
    private String name;

    @Schema(description = "사전 코드")
    private String dictCode;

    @Schema(description = "사전 상태（1-활성화，0-비활성화）")
    private Integer status;

}
