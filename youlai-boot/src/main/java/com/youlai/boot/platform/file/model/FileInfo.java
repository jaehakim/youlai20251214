package com.youlai.boot.platform.file.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 파일 정보 객체
 *
 * @author Ray.Hao
 * @since 1.0.0
 */
@Schema(description = "파일 객체")
@Data
public class FileInfo {

    @Schema(description = "파일 이름")
    private String name;

    @Schema(description = "파일 URL")
    private String url;

}
