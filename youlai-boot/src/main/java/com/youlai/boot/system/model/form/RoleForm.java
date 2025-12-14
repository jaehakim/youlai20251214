package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

@Schema(description = "역할 폼 객체")
@Data
public class RoleForm {

    @Schema(description="역할 ID")
    private Long id;

    @Schema(description="역할명")
    @NotBlank(message = "역할명은 비어있을 수 없습니다")
    private String name;

    @Schema(description="역할 코드")
    @NotBlank(message = "역할 코드는 비어있을 수 없습니다")
    private String code;

    @Schema(description="정렬")
    private Integer sort;

    @Schema(description="역할 상태 (1-정상; 0-중지)")
    @Range(max = 1, min = 0, message = "역할 상태가 올바르지 않습니다")
    private Integer status;

    @Schema(description="데이터 권한")
    private Integer dataScope;

}
