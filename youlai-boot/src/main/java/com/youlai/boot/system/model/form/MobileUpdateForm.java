package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 휴대폰 번호 수정 폼
 *
 * @author Ray.Hao
 * @since 2024/8/19
 */
@Schema(description = "휴대폰 번호 수정 폼")
@Data
public class MobileUpdateForm {

    @Schema(description = "휴대폰 번호")
    @NotBlank(message = "휴대폰 번호는 비어있을 수 없습니다")
    private String mobile;

    @Schema(description = "인증코드")
    @NotBlank(message = "인증코드는 비어있을 수 없습니다")
    private String code;

}
