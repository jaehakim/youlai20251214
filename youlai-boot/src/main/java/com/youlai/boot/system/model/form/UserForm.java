package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * 사용자 폼 객체
 *
 * @author haoxr
 * @since 2022/4/12 11:04
 */
@Schema(description = "사용자 폼 객체")
@Data
public class UserForm {

    @Schema(description="사용자 ID")
    private Long id;

    @Schema(description="사용자명")
    @NotBlank(message = "사용자명은 비어있을 수 없습니다")
    private String username;

    @Schema(description="닉네임")
    @NotBlank(message = "닉네임은 비어있을 수 없습니다")
    private String nickname;


    @Schema(description="휴대폰 번호")
    @Pattern(regexp = "^$|^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "휴대폰 번호 형식이 올바르지 않습니다")
    private String mobile;

    @Schema(description="성별")
    private Integer gender;

    @Schema(description="사용자 프로필 이미지")
    private String avatar;

    @Schema(description="이메일")
    private String email;

    @Schema(description="사용자 상태(1:정상;0:비활성화)")
    @Range(min = 0, max = 1, message = "사용자 상태가 올바르지 않습니다")
    private Integer status;

    @Schema(description="부서 ID")
    private Long deptId;

    @Schema(description="역할 ID 집합")
    @NotEmpty(message = "사용자 역할은 비어있을 수 없습니다")
    private List<Long> roleIds;

    @Schema(description="위챗 openId")
    private String openId;

}
