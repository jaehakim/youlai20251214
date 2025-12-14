package com.youlai.boot.platform.codegen.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "테이블 뷰 객체")
@Data
public class TablePageVO {

    @Schema(description = "테이블명", example = "sys_user")
    private String tableName;

    @Schema(description = "테이블 설명",example = "사용자 테이블")
    private String tableComment;

    @Schema(description = "테이블 정렬 규칙",example = "utf8mb4_general_ci")
    private String tableCollation;

    @Schema(description = "스토리지 엔진",example = "InnoDB")
    private String engine;

    @Schema(description = "문자 집합",example = "utf8mb4")
    private String charset;

    @Schema(description = "생성 시간",example = "2023-08-08 08:08:08")
    private String createTime;

    @Schema(description="설정 완료 여부")
    private Integer isConfigured;

}
