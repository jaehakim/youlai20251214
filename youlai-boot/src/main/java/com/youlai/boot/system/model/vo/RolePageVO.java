package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description ="역할 페이지 객체")
@Data
public class RolePageVO {

    @Schema(description="역할 ID")
    private Long id;

    @Schema(description="역할명")
    private String name;

    @Schema(description="역할 코드")
    private String code;

    @Schema(description="역할 상태")
    private Integer status;

    @Schema(description="정렬")
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
