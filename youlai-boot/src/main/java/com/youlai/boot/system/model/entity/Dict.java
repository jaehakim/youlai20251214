package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 사전 엔티티
 *
 * @author Ray.Hao
 * @since 2022/12/17
 */
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict")
@Data
public class Dict extends BaseEntity {

    /**
     * 사전 코드
     */
    private String dictCode;

    /**
     * 사전명
     */
    private String name;


    /**
     * 상태(1:활성화, 0:중지)
     */
    private Integer status;

    /**
     * 비고
     */
    private String remark;

    /**
     * 논리 삭제 표시(0-미삭제 1-삭제됨)
     */
    private Integer isDeleted;

}