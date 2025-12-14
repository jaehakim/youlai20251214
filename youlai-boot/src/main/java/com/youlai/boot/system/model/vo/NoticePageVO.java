package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 공지사항 뷰 객체
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Getter
@Setter
@Schema(description = "공지사항 뷰 객체")
public class NoticePageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "공지 ID")
    private Long id;

    @Schema(description = "공지 제목")
    private String title;

    @Schema(description = "공지 상태")
    private Integer publishStatus;

    @Schema(description = "공지 유형")
    private Integer type;

    @Schema(description = "발행자명")
    private String publisherName;

    @Schema(description = "공지 등급")
    private String level;

    @Schema(description = "발행 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishTime;

    @Schema(description = "읽음 여부")
    private Integer isRead;

    @Schema(description = "대상 유형")
    private Integer targetType;

    @Schema(description = "생성 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    @Schema(description = "회수 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime revokeTime;
}
