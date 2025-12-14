package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 사전 항목 엔티티 객체
 *
 * @author Ray.Hao
 * @since 2022/12/17
 */
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict_item")
@Data
public class DictItem extends BaseEntity {

    /**
     * 사전 코드
     */
    private String dictCode;

    /**
     * 사전 항목명
     */
    private String label;

    /**
     * 사전 항목 값
     */
    private String value;

    /**
     * 정렬
     */
    private Integer sort;

    /**
     * 상태(1-정상, 0-비활성화)
     */
    private Integer status;

    /**
     * 비고
     */
    private String remark;

    /**
     * 태그 유형
     */
    private String tagType;
}