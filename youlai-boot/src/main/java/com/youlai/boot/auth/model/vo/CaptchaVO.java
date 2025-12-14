package com.youlai.boot.auth.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 인증코드 정보
 *
 * @author Ray。Hao
 * @since 2023/03/24
 */
@Schema(description = "인증코드 정보")
@Data
@Builder
public class CaptchaVO {

    @Schema(description = "인증코드 캐시 Key")
    private String captchaKey;

    @Schema(description = "인증코드 이미지 Base64 문자열")
    private String captchaBase64;

}
