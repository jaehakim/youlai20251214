package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 사용자 공지 VO
 *
 * @author Theo
 * @since 2024-08-28 16:56
 */
@Data
@Schema(description = "사용자 공지 VO")
public class UserNoticePageVO {

    @Schema(description = "공지 ID")
    private Long id;

    @Schema(description = "공지 제목")
    private String title;

    @Schema(description = "공지 유형")
    private Integer noticeType;

    @Schema(description = "공지 등급")
    private String noticeLevel;

    @Schema(description = "발행자명")
    private String publisherName;

    @Schema(description = "발행 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishTime;

    @Schema(description = "읽음 여부")
    private Integer isRead;

}
