package com.youlai.boot.system.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * 현재 로그인 사용자 객체
 *
 * @author haoxr
 * @since 2022/1/14
 */
@Schema(description ="현재 로그인 사용자 객체")
@Data
public class CurrentUserDTO {

    @Schema(description="사용자 ID")
    private Long userId;

    @Schema(description="사용자명")
    private String username;

    @Schema(description="사용자 닉네임")
    private String nickname;

    @Schema(description="프로필 이미지 주소")
    private String avatar;

    @Schema(description="사용자 역할 코드 집합")
    private Set<String> roles;

    @Schema(description="사용자 권한 식별자 집합")
    private Set<String> perms;

}
