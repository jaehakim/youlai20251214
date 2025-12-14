package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "부서 뷰 객체")
@Data
public class DeptVO {

    @Schema(description = "부서 ID")
    private Long id;

    @Schema(description = "상위 부서 ID")
    private Long parentId;

    @Schema(description = "부서명")
    private String name;

    @Schema(description = "부서 코드")
    private String code;

    @Schema(description = "정렬")
    private Integer sort;

    @Schema(description = "상태(1:활성화；0:비활성화)")
    private Integer status;

    @Schema(description = "하위 부서")
    private List<DeptVO> children;

    @Schema(description = "생성 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;
    @Schema(description = "수정 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updateTime;

}
