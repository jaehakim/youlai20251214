package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Schema(description = "부서 폼 객체")
@Getter
@Setter
public class DeptForm {

    @Schema(description="부서 ID", example = "1001")
    private Long id;

    @Schema(description="부서명", example = "연구개발부")
    private String name;

    @Schema(description="부서 코드", example = "RD001")
    private String code;

    @Schema(description="상위 부서 ID", example = "1000")
    @NotNull(message = "상위 부서 ID는 비어있을 수 없습니다")
    private Long parentId;

    @Schema(description="상태 (1:활성화; 0:비활성화)", example = "1")
    @Range(min = 0, max = 1, message = "상태값이 올바르지 않습니다")
    private Integer status;

    @Schema(description="정렬 (숫자가 작을수록 앞에 위치)", example = "1")
    private Integer sort;

}
