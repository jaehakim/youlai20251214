package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 사용자 페이지 뷰 객체
 *
 * @author haoxr
 * @since 2022/1/15 9:41
 */
@Schema(description ="사용자 페이지 객체")
@Data
public class UserPageVO {

    @Schema(description="사용자 ID")
    private Long id;

    @Schema(description="사용자명")
    private String username;

    @Schema(description="사용자 닉네임")
    private String nickname;

    @Schema(description="휴대폰 번호")
    private String mobile;

    @Schema(description="성별")
    private Integer gender;

    @Schema(description="사용자 프로필 이미지 주소")
    private String avatar;

    @Schema(description="사용자 이메일")
    private String email;

    @Schema(description="사용자 상태(1:활성화;0:비활성화)")
    private Integer status;

    @Schema(description="부서명")
    private String deptName;

    @Schema(description="역할명, 여러 개는 쉼표(,)로 구분")
    private String roleNames;

    @Schema(description="생성 시간")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
    private LocalDateTime createTime;

}
