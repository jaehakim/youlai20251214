package com.youlai.boot.platform.codegen.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youlai.boot.common.base.BaseEntity;
import com.youlai.boot.platform.codegen.enums.FormTypeEnum;
import com.youlai.boot.platform.codegen.enums.QueryTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 필드 생성 설정 엔티티
 *
 * @author Ray
 * @since 2.10.0
 */
@TableName(value = "gen_field_config")
@Getter
@Setter
public class GenFieldConfig extends BaseEntity {


    /**
     * 연관된 설정 ID
     */
    private Long configId;

    /**
     * 컬럼명
     */
    private String columnName;

    /**
     * 컬럼 유형
     */
    private String columnType;

    /**
     * 필드 길이
     */
    private Long maxLength;

    /**
     * 필드 이름
     */
    private String fieldName;

    /**
     * 필드 정렬
     */
    private Integer fieldSort;

    /**
     * 필드 유형
     */
    private String fieldType;

    /**
     * 필드 설명
     */
    private String fieldComment;

    /**
     * 폼 유형
     */
    private FormTypeEnum formType;

    /**
     * 조회 방식
     */
    private QueryTypeEnum queryType;

    /**
     * 목록에 표시 여부
     */
    private Integer isShowInList;

    /**
     * 폼에 표시 여부
     */
    private Integer isShowInForm;

    /**
     * 조회 조건에 표시 여부
     */
    private Integer isShowInQuery;

    /**
     * 필수 여부
     */
    private Integer isRequired;

    /**
     * TypeScript 유형
     */
    @TableField(exist = false)
    @JsonIgnore
    private String tsType;

    /**
     * 사전 유형
     */
    private String dictType;
}
