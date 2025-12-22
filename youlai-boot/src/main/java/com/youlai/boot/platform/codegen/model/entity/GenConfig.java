package com.youlai.boot.platform.codegen.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 코드 생성 기본 설정
 *
 * @author Ray
 * @since 2.10.0
 */
@TableName(value = "gen_config")
@Getter
@Setter
public class GenConfig extends BaseEntity {

    /**
     * 테이블명
     */
    private String tableName;

    /**
     * 패키지명
     */
    private String packageName;

    /**
     * 모듈명
     */
    private String moduleName;

    /**
     * 엔티티 클래스명
     */
    private String entityName;

    /**
     * 비즈니스명
     */
    private String businessName;

    /**
     * 상위 메뉴 ID
     */
    private Long parentMenuId;

    /**
     * 작성자
     */
    private String author;

    /**
     * 페이지 유형 classic|curd
     */
    private String pageType;

    /**
     * 제거할 테이블 접두사, 예: sys_
     */
    private String removeTablePrefix;
}