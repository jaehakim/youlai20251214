package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youlai.boot.common.enums.LogModuleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 시스템 로그 페이지 VO
 *
 * @author Ray
 * @since 2.10.0
 */
@Data
@Schema(description = "시스템 로그 페이지 VO")
public class LogPageVO implements Serializable {

    @Schema(description = "기본키")
    private Long id;

    @Schema(description = "로그 모듈")
    private LogModuleEnum module;

    @Schema(description = "로그 내용")
    private String content;

    @Schema(description = "요청 경로")
    private String requestUri;

    @Schema(description = "요청 방식")
    private String method;

    @Schema(description = "IP 주소")
    private String ip;

    @Schema(description = "지역")
    private String region;

    @Schema(description = "브라우저")
    private String browser;

    @Schema(description = "운영체제")
    private String os;

    @Schema(description = "실행 시간(밀리초)")
    private Long executionTime;

    @Schema(description = "생성자 ID")
    private Long createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "작업자")
    private String operator;
}