package com.youlai.boot.system.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 공지사항 폼 객체
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Getter
@Setter
@Schema(description = "공지사항 폼 객체")
public class NoticeForm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "공지 ID")
    private Long id;

    @Schema(description = "공지 제목")
    @NotBlank(message = "공지 제목은 비어있을 수 없습니다")
    @Size(max=50, message="공지 제목 길이는 50자를 초과할 수 없습니다")
    private String title;

    @Schema(description = "공지 내용")
    @NotBlank(message = "공지 내용은 비어있을 수 없습니다")
    @Size(max=65535, message="공지 내용 길이는 65535자를 초과할 수 없습니다")
    private String content;

    @Schema(description = "공지 유형")
    private Integer noticeType;

    @Schema(description = "우선순위 (L-낮음 M-중간 H-높음)")
    private String noticeLevel;

    @Schema(description = "대상 유형 (1-전체 2-지정)")
    @Range(min = 1, max = 2, message = "대상 유형 범위는 [1,2]입니다")
    private Integer targetType;

    @Schema(description = "수신자 ID 집합")
    private List<String> targetUserIds;

}
