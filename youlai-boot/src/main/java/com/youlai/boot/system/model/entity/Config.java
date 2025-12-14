package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

/**
 * 시스템 설정 객체
 *
 * @author Theo
 * @since 2024-07-29 11:17:26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "시스템 설정")
@TableName("sys_config")
public class Config extends BaseEntity {

    /**
     * 설정명
     */
    private String configName;

    /**
     * 설정 키
     */
    private String configKey;

    /**
     * 설정 값
     */
    private String configValue;

    /**
     * 설명, 비고
     */
    private String remark;

    /**
     * 생성자 ID
     */
    private Long createBy;

    /**
     * 수정자 ID
     */
    private Long updateBy;

    /**
     * 논리 삭제 표시(0-미삭제 1-삭제됨)
     */
    private Integer isDeleted;

}
