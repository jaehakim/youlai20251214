package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 역할 엔티티
 *
 * @author Ray.Hao
 * @since 2024/6/23
 */
@TableName("sys_role")
@Getter
@Setter
public class Role extends BaseEntity {

    /**
     * 역할명
     */
    private String name;

    /**
     * 역할 코드
     */
    private String code;

    /**
     * 표시 순서
     */
    private Integer sort;

    /**
     * 역할 상태(1-정상 0-중지)
     */
    private Integer status;

    /**
     * 데이터 권한
     */
    private Integer dataScope;

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
}