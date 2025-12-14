package com.youlai.boot.system.model.query;

import cn.hutool.db.sql.Direction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.youlai.boot.common.base.BasePageQuery;
import com.youlai.boot.common.annotation.ValidField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 사용자 페이지 조회 객체
 *
 * @author haoxr
 * @since 2022/1/14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "사용자 페이지 조회 객체")
public class UserPageQuery extends BasePageQuery {

    @Schema(description = "키워드(사용자명/닉네임/휴대폰 번호)")
    private String keywords;

    @Schema(description = "사용자 상태")
    private Integer status;

    @Schema(description = "부서 ID")
    private Long deptId;

    @Schema(description = "역할 ID")
    private List<Long> roleIds;

    @Schema(description = "생성 시간 범위")
    private List<String> createTime;

    @Schema(description = "정렬 필드")
    @ValidField(allowedValues = {"create_time", "update_time"})
    private String field;

    @Schema(description = "정렬 방식(오름차순:ASC;내림차순:DESC)")
    private Direction direction;

    /**
     * 슈퍼 관리자 여부
     */
    @JsonIgnore
    @Schema(hidden = true)
    private Boolean isRoot;

}
