package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 비밀번호 수정 폼
 *
 * @author Ray.Hao
 * @since 2024/8/13
 */
@Schema(description = "비밀번호 수정 폼")
@Data
public class PasswordUpdateForm {

    @Schema(description = "기존 비밀번호")
    private String oldPassword;

    @Schema(description = "새 비밀번호")
    private String newPassword;

    @Schema(description = "비밀번호 확인")
    private String confirmPassword;
}
