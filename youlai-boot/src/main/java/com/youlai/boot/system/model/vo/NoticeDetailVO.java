package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 공지사항 상세 VO
 *
 * @author Theo
 * @since 2024-9-8 01:25:06
 */
@Data
public class NoticeDetailVO {

    @Schema(description = "공지 ID")
    private Long id;

    @Schema(description = "공지 제목")
    private String title;

    @Schema(description = "공지 내용")
    private String content;

    @Schema(description = "공지 유형")
    private Integer type;

    @Schema(description = "발행자")
    private String publisherName;

    @Schema(description = "우선순위(L-낮음 M-보통 H-높음)")
    private String level;

    @Schema(description = "발행 상태(0-미발행 1-발행완료 2-회수) 중복 필드, 발행 여부 판단 용이")
    private Integer publishStatus;

    @Schema(description = "발행 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}
