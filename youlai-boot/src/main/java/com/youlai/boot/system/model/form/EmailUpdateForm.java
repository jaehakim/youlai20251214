package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 이메일 수정 폼
 *
 * @author Ray.Hao
 * @since 2024/8/19
 */
@Schema(description = "이메일 수정 폼")
@Data
public class EmailUpdateForm {

    @Schema(description = "이메일")
    @NotBlank(message = "이메일은 비어있을 수 없습니다")
    private String email;

    @Schema(description = "인증코드")
    @NotBlank(message = "인증코드는 비어있을 수 없습니다")
    private String code;

}
