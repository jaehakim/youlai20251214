package com.youlai.boot.system.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description ="메뉴 뷰 객체")
@Data
public class MenuVO {

    @Schema(description = "메뉴 ID")
    private Long id;

    @Schema(description = "상위 메뉴 ID")
    private Long parentId;

    @Schema(description = "메뉴명")
    private String name;

    @Schema(description="메뉴 유형")
    private Integer type;

    @Schema(description = "라우트명")
    private String routeName;

    @Schema(description = "라우트 경로")
    private String routePath;

    @Schema(description = "컴포넌트 경로")
    private String component;

    @Schema(description = "메뉴 정렬(숫자가 작을수록 우선순위 높음)")
    private Integer sort;

    @Schema(description = "메뉴 표시 여부(1:표시;0:숨김)")
    private Integer visible;

    @Schema(description = "ICON")
    private String icon;

    @Schema(description = "리다이렉트 경로")
    private String redirect;

    @Schema(description="버튼 권한 식별자")
    private String perm;

    @Schema(description = "하위 메뉴")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<MenuVO> children;

}
