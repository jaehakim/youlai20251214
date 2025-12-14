package com.youlai.boot.system.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 공지 전송 객체
 *
 * @author Theo
 * @since 2024-9-2 14:32:58
 */
@Data
public class NoticeDTO {

    @Schema(description = "공지 ID")
    private Long id;

    @Schema(description = "공지 유형")
    private Integer type;

    @Schema(description = "공지 제목")
    private String title;

    @Schema(description = "공지 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime publishTime;


}
