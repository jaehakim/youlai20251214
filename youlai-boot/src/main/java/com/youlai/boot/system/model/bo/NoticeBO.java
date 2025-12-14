package com.youlai.boot.system.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 공지사항 비즈니스 객체
 *
 * @author Theo
 * @since 2024-09-01 10:31
 */
@Data
public class NoticeBO {

    /**
     * 공지 ID
     */
    private Long id;

    /**
     * 공지 제목
     */
    private String title;

    /**
     * 공지 유형
     */
    private Integer type;

    /**
     * 공지 유형 라벨
     */
    private String typeLabel;

    /**
     * 공지 내용
     */
    private String content;

    /**
     * 발행인 성명
     */
    private String publisherName;

    /**
     * 공지 레벨 (L: 낮음, M: 중간, H: 높음)
     */
    private String level;

    /**
     * 대상 유형 (1: 전체 2: 지정)
     */
    private Integer targetType;

    /**
     * 발행 상태 (0: 미발행, 1: 발행됨, -1: 철회됨)
     */
    private Integer publishStatus;

    /**
     * 생성 시간
     */
    private LocalDateTime createTime;

    /**
     * 발행 시간
     */
    private LocalDateTime publishTime;

    /**
     * 철회 시간
     */
    private LocalDateTime revokeTime;
}
