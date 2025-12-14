package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 알림 공지 엔티티 객체
 *
 * @author Kylin
 * @since 2024-08-27 10:31
 */
@Getter
@Setter
@TableName("sys_notice")
public class Notice extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 알림 제목
     */
    private String title;
    /**
     * 알림 내용
     */
    private String content;
    /**
     * 알림 유형
     */
    private Integer type;

    /**
     * 발행자
     */
    private Long publisherId;

    /**
     * 알림 등급(L: 낮음, M: 중간, H: 높음)
     */
    private String level;

    /**
     * 대상 유형(1: 전체, 2: 지정)
     */
    private Integer targetType;

    /**
     * 대상 사용자 ID 목록
     */
    private String targetUserIds;

    /**
     * 발행 상태(0: 미발행, 1: 발행됨, -1: 철회됨)
     */
    private Integer publishStatus;

    /**
     * 발행 시간
     */
    private LocalDateTime publishTime;

    /**
     * 철회 시간
     */
    private LocalDateTime revokeTime;

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
