package com.youlai.boot.security.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 인증 토큰 응답 객체
 *
 * @author Ray.Hao
 * @since 0.0.1
 */
@Schema(description = "인증 토큰 응답 객체")
@Data
@Builder
public class AuthenticationToken {

    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType;

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레시 토큰")
    private String refreshToken;

    @Schema(description = "만료 시간(단위: 초)")
    private Integer expiresIn;

}
