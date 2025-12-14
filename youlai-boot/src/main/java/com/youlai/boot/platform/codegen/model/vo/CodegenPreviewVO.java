package com.youlai.boot.platform.codegen.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "코드 생성 미리보기 VO")
@Data
public class CodegenPreviewVO {

    @Schema(description = "생성 파일 경로")
    private String path;

    @Schema(description = "생성 파일명",example = "SysUser.java" )
    private String fileName;

    @Schema(description = "생성 파일 내용")
    private String content;

}
