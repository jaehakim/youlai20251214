package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 개인 센터 사용자 정보
 *
 * @author Ray.Hao
 * @since 2024/8/13
 */
@Schema(description = "개인 센터 사용자 정보")
@Data
public class UserProfileForm {

    @Schema(description = "사용자 ID")
    private Long id;

    @Schema(description = "사용자명")
    private String username;

    @Schema(description = "사용자 닉네임")
    private String nickname;

    @Schema(description = "프로필 이미지 URL")
    private String avatar;

    @Schema(description = "성별")
    private Integer gender;

    @Schema(description = "휴대폰 번호")
    private String mobile;

    @Schema(description = "이메일")
    private String email;


}
