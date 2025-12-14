package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 엔티티
 */
@TableName("sys_user")
@Getter
@Setter
public class User extends BaseEntity {

    /**
     * 사용자명
     */
    private String username;

    /**
     * 닉네임
     */
    private String nickname;

    /**
     * 성별(1-남성 2-여성 0-비공개)
     */
    private Integer gender;

    /**
     * 비밀번호
     */
    private String password;

    /**
     * 부서 ID
     */
    private Long deptId;

    /**
     * 사용자 아바타
     */
    private String avatar;

    /**
     * 연락처
     */
    private String mobile;

    /**
     * 상태(1-정상 0-비활성화)
     */
    private Integer status;

    /**
     * 사용자 이메일
     */
    private String email;

    /**
     * 생성자 ID
     */
    private Long createBy;

    /**
     * 수정자 ID
     */
    private Long updateBy;

    /**
     * 삭제 여부(0-아니오 1-예)
     */
    private Integer isDeleted;

    /**
     * 위챗 OpenID
     */
    private String openid;
}