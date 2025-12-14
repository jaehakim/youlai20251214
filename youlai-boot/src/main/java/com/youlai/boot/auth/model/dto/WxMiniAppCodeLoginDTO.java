package com.youlai.boot.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 위챗 미니 프로그램 Code 로그인 요청 파라미터
 *
 * @author 有来技术团队
 * @since 2.0.0
 */
@Schema(description = "위챗 미니 프로그램 Code 로그인 요청 파라미터")
@Data
public class WxMiniAppCodeLoginDTO {

    @Schema(description = "위챗 미니 프로그램 로그인 시 획득한 code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "code는 비어있을 수 없습니다")
    private String code;

} 