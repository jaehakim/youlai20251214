package com.youlai.boot.platform.codegen.model.bo;

import lombok.Data;


/**
 * 데이터 테이블 메타데이터
 *
 * @author Ray
 * @since 2.10.0
 */
@Data
public class TableMetaData {

    /**
     * 테이블명
     */
    private String tableName;

    /**
     * 테이블 설명
     */
    private String tableComment;

    /**
     * 정렬 규칙
     */
    private String tableCollation;

    /**
     * 스토리지 엔진
     */
    private String engine;

    /**
     * 문자 집합
     */
    private String charset;

    /**
     * 생성 시간
     */
    private String createTime;

}
