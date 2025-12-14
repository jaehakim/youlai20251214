package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 메뉴 라우트 뷰 객체
 *
 * @author haoxr
 * @since 2020/11/28
 */
@Schema(description = "라우트 객체")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteVO {

    @Schema(description = "라우트 경로", example = "user")
    private String path;

    @Schema(description = "컴포넌트 경로", example = "system/user/index")
    private String component;

    @Schema(description = "리다이렉트 링크", example = "https://www.youlai.tech")
    private String redirect;

    @Schema(description = "라우트명")
    private String name;

    @Schema(description = "라우트 속성")
    private Meta meta;

    @Schema(description = "라우트 속성 유형")
    @Data
    public static class Meta {

        @Schema(description = "라우트 제목")
        private String title;

        @Schema(description = "ICON")
        private String icon;

        @Schema(description = "숨김 여부(true-숨김 false-표시)", example = "true")
        private Boolean hidden;

        @Schema(description = "【메뉴】페이지 캐시 활성화 여부", example = "true")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean keepAlive;

        @Schema(description = "【디렉토리】하위 라우트가 하나만 있어도 항상 표시 여부", example = "true")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean alwaysShow;

        @Schema(description = "라우트 파라미터")
        private Map<String,String> params;
    }

    @Schema(description = "하위 라우트 목록")
    private List<RouteVO> children;
}
