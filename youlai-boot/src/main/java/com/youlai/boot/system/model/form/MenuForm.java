package com.youlai.boot.system.model.form;

import com.youlai.boot.common.model.KeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * 메뉴 폼 객체
 *
 * @author Ray.Hao
 * @since 2024/06/23
 */
@Schema(description = "메뉴 폼 객체")
@Data
public class MenuForm {

    @Schema(description = "메뉴 ID")
    private Long id;

    @Schema(description = "상위 메뉴 ID")
    private Long parentId;

    @Schema(description = "메뉴명")
    private String name;

    @Schema(description = "메뉴 유형 (1-메뉴 2-디렉토리 3-외부링크 4-버튼)")
    private Integer type;

    @Schema(description = "라우트명")
    private String routeName;

    @Schema(description = "라우트 경로")
    private String routePath;

    @Schema(description = "컴포넌트 경로 (vue 페이지 전체 경로, .vue 확장자 생략)")
    private String component;

    @Schema(description = "권한 식별자")
    private String perm;

    @Schema(description = "표시 상태 (1:표시; 0:숨김)")
    @Range(max = 1, min = 0, message = "표시 상태가 올바르지 않습니다")
    private Integer visible;

    @Schema(description = "정렬 (숫자가 작을수록 앞에 위치)")
    private Integer sort;

    @Schema(description = "메뉴 아이콘")
    private String icon;

    @Schema(description = "리다이렉트 경로")
    private String redirect;

    @Schema(description = "[메뉴] 페이지 캐시 활성화 여부", example = "1")
    private Integer keepAlive;

    @Schema(description = "[디렉토리] 하나의 자식 라우트만 있을 때도 항상 표시", example = "1")
    private Integer alwaysShow;

    @Schema(description = "라우트 파라미터")
    private List<KeyValue> params;

}
