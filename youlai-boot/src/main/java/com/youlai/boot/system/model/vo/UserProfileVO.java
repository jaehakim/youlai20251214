package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 개인센터 사용자 정보
 *
 * @author Ray
 * @since 2024/8/13
 */
@Schema(description = "개인센터 사용자 정보")
@Data
public class UserProfileVO {

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

    @Schema(description = "부서명")
    private String deptName;

    @Schema(description = "역할명")
    private String roleNames;

    @Schema(description = "생성 시간")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

}
