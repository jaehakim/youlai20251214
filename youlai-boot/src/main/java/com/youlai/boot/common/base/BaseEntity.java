package com.youlai.boot.common.base;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 기본 엔티티 클래스
 *
 * <p>엔티티 클래스의 기본 클래스, 생성 시간, 업데이트 시간, 논리적 삭제 플래그 등 엔티티 클래스의 공통 속성을 포함</p>
 *
 * @author Ray
 * @since 2024/6/23
 */
@Data
public class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 기본키 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 생성 시간
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 업데이트 시간
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
