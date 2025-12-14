package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 부서 엔티티 객체
 *
 * @author Ray.Hao
 * @since 2024/06/23
 */
@TableName("sys_dept")
@Getter
@Setter
public class Dept extends BaseEntity {

    /**
     * 부서명
     */
    private String name;

    /**
     * 부서 코드
     */
    private String code;

    /**
     * 상위 노드 id
     */
    private Long parentId;

    /**
     * 상위 노드 id 경로
     */
    private String treePath;

    /**
     * 표시 순서
     */
    private Integer sort;

    /**
     * 상태(1-정상 0-비활성화)
     */
    private Integer status;

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