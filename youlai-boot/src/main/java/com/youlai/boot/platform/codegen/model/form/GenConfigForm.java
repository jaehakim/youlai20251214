package com.youlai.boot.platform.codegen.model.form;

import com.youlai.boot.platform.codegen.enums.FormTypeEnum;
import com.youlai.boot.platform.codegen.enums.QueryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 코드 생성 설정 폼
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(description = "코드 생성 설정 폼")
@Data
public class GenConfigForm {

    @Schema(description = "기본 키",example = "1")
    private Long id;

    @Schema(description = "테이블명",example = "sys_user")
    private String tableName;

    @Schema(description = "비즈니스명",example = "사용자")
    private String businessName;

    @Schema(description = "모듈명",example = "system")
    private String moduleName;

    @Schema(description = "패키지명",example = "com.youlai")
    private String packageName;

    @Schema(description = "엔티티명",example = "User")
    private String entityName;

    @Schema(description = "작성자",example = "youlaitech")
    private String author;

    @Schema(description = "상위 메뉴 ID",example = "1")
    private Long parentMenuId;

    @Schema(description = "필드 설정 목록")
    private List<FieldConfig> fieldConfigs;

    @Schema(description = "백엔드 애플리케이션명")
    private String backendAppName;

    @Schema(description = "프론트엔드 애플리케이션명")
    private String frontendAppName;

    @Schema(description = "페이지 유형 classic|curd", example = "classic")
    private String pageType;

    @Schema(description = "제거할 테이블 접두사, 예: sys_", example = "sys_")
    private String removeTablePrefix;

    @Schema(description = "필드 설정")
    @Data
    public static class FieldConfig {

        @Schema(description = "기본 키")
        private Long id;

        @Schema(description = "컬럼명")
        private String columnName;

        @Schema(description = "컬럼 유형")
        private String columnType;

        @Schema(description = "필드명")
        private String fieldName;

        @Schema(description = "필드 정렬")
        private Integer fieldSort;

        @Schema(description = "필드 유형")
        private String fieldType;

        @Schema(description = "필드 설명")
        private String fieldComment;

        @Schema(description = "목록 표시 여부")
        private Integer isShowInList;

        @Schema(description = "폼 표시 여부")
        private Integer isShowInForm;

        @Schema(description = "조회 조건 표시 여부")
        private Integer isShowInQuery;

        @Schema(description = "필수 여부")
        private Integer isRequired;

        @Schema(description = "최대 길이")
        private Integer maxLength;

        @Schema(description = "폼 유형")
        private FormTypeEnum formType;

        @Schema(description = "조회 유형")
        private QueryTypeEnum queryType;

        @Schema(description = "사전 유형")
        private String dictType;

    }
}
