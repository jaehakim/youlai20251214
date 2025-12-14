package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 공지사항 페이지 조회 객체
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description ="공지사항 조회 객체")
public class NoticePageQuery extends BasePageQuery {

    @Schema(description = "공지 제목")
    private String title;

    @Schema(description = "발행 상태(0-미발행 1-발행됨 -1-회수됨)")
    private Integer publishStatus;

    @Schema(description = "발행 시간(시작-종료)")
    private List<String> publishTime;

    @Schema(description = "조회자 ID")
    private Long userId;

    @Schema(description = "읽음 여부(0-읽지 않음 1-읽음)")
    private Integer isRead;

}
