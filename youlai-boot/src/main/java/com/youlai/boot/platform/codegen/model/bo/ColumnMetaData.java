package com.youlai.boot.platform.codegen.model.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "데이터 테이블 컬럼 VO")
@Data
public class ColumnMetaData {

        /**
         * 컬럼명
         */
        private String columnName;

        /**
         * 컬럼 유형
         */
        private String dataType;

        /**
         * 컬럼 설명
         */
        private String columnComment;

        /**
         * 컬럼 길이
         */
        private Long characterMaximumLength;

        /**
         * 기본 키 여부 (1-예 0-아니오)
         */
        private Integer isPrimaryKey;

        /**
         * Null 허용 여부 (1-예 0-아니오)
         */
        private String isNullable;

        /**
         * 문자 집합
         */
        private String characterSetName;

        /**
         * 정렬 규칙
         */
        private String collationName;

}
