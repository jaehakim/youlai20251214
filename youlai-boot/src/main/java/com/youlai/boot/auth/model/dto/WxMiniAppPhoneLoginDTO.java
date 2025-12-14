package com.youlai.boot.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 위챗 미니 프로그램 휴대폰 번호 로그인 요청 파라미터
 *
 * @author Ray.Hao
 * @since 2.0.0
 */
@Schema(description = "위챗 미니 프로그램 휴대폰 번호 로그인 요청 파라미터")
@Data
public class WxMiniAppPhoneLoginDTO {

    @Schema(description = "위챗 미니 프로그램 로그인 시 획득한 code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "code는 비어있을 수 없습니다")
    private String code;

    @Schema(description = "민감한 데이터를 포함한 전체 사용자 정보의 암호화 데이터")
    private String encryptedData;

    @Schema(description = "암호화 알고리즘의 초기 벡터")
    private String iv;

} 