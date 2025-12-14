package com.youlai.boot.system.model.form;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * 사전 폼 객체
 *
 * @author Ray Hao
 * @since 2.9.0
 */
@Schema(description = "사전")
@Data
public class DictForm {

    @Schema(description = "사전 ID",example = "1")
    private Long id;

    @Schema(description = "사전명",example = "성별")
    private String name;

    @Schema(description = "사전 코드", example ="gender")
    @NotBlank(message = "사전 코드는 비어있을 수 없습니다")
    private String dictCode;

    @Schema(description = "비고")
    private String remark;

    @Schema(description = "사전 상태 (1-활성화, 0-비활성화)", example = "1")
    @Range(min = 0, max = 1, message = "사전 상태가 올바르지 않습니다")
    private Integer status;

}
