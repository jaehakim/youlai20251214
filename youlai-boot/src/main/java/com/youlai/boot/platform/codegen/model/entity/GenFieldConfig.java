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
 * 字段生成설정实体
 *
 * @author Ray
 * @since 2.10.0
 */
@TableName(value = "gen_field_config")
@Getter
@Setter
public class GenFieldConfig extends BaseEntity {


    /**
     * 关联的설정ID
     */
    private Long configId;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 列유형
     */
    private String columnType;

    /**
     * 字段长度
     */
    private Long maxLength;

    /**
     * 字段이름
     */
    private String fieldName;

    /**
     * 字段排序
     */
    private Integer fieldSort;

    /**
     * 字段유형
     */
    private String fieldType;

    /**
     * 字段설명
     */
    private String fieldComment;

    /**
     * 表单유형
     */
    private FormTypeEnum formType;

    /**
     * 查询方式
     */
    private QueryTypeEnum queryType;

    /**
     * 是否在列表显示
     */
    private Integer isShowInList;

    /**
     * 是否在表单显示
     */
    private Integer isShowInForm;

    /**
     * 是否在查询条件显示
     */
    private Integer isShowInQuery;

    /**
     * 是否必填
     */
    private Integer isRequired;

    /**
     * TypeScript유형
     */
    @TableField(exist = false)
    @JsonIgnore
    private String tsType;

    /**
     * 字典유형
     */
    private String dictType;
}
