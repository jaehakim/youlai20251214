package com.youlai.boot.system.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 사용자 영속화 객체
 *
 * @author haoxr
 * @since 2022/6/10
 */
@Data
public class UserBO {

    /**
     * 사용자 ID
     */
    private Long id;

    /**
     * 계정명
     */
    private String username;

    /**
     * 닉네임
     */
    private String nickname;

    /**
     * 휴대폰 번호
     */
    private String mobile;

    /**
     * 성별 (1->남성; 2->여성)
     */
    private Integer gender;

    /**
     * 프로필 이미지 URL
     */
    private String avatar;

    /**
     * 이메일
     */
    private String email;

    /**
     * 상태: 1->활성화; 0->비활성화
     */
    private Integer status;

    /**
     * 부서명
     */
    private String deptName;

    /**
     * 역할명, 여러 개인 경우 쉼표(,)로 구분
     */
    private String roleNames;

    /**
     * 생성 시간
     */
    private LocalDateTime createTime;
}
