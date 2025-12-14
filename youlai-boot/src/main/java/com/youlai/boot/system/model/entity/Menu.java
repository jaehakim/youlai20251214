package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 메뉴 엔티티
 *
 * @author Ray.Hao
 * @since 2023/3/6
 */
@TableName("sys_menu")
@Getter
@Setter
public class Menu {
    /**
     * 메뉴 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 상위 메뉴 ID
     */
    private Long parentId;

    /**
     * 메뉴명
     */
    private String name;

    /**
     * 메뉴 유형(1-메뉴, 2-디렉토리, 3-외부링크, 4-버튼권한)
     */
    private Integer type;

    /**
     * 라우트명(Vue Router에 정의된 라우트명)
     */
    private String routeName;

    /**
     * 라우트 경로(Vue Router에 정의된 URL 경로)
     */
    private String routePath;

    /**
     * 컴포넌트 경로(vue 페이지 전체 경로, .vue 확장자 생략)
     */
    private String component;

    /**
     * 권한 식별자
     */
    private String perm;

    /**
     * 표시 상태(1:표시, 0:숨김)
     */
    private Integer visible;

    /**
     * 정렬
     */
    private Integer sort;

    /**
     * 메뉴 아이콘
     */
    private String icon;

    /**
     * 리다이렉트 경로
     */
    private String redirect;

    /**
     * 상위 노드 경로, 쉼표(,)로 구분
     */
    private String treePath;

    /**
     * [메뉴] 페이지 캐시 활성화 여부(1:활성화, 0:비활성화)
     */
    private Integer keepAlive;

    /**
     * [디렉토리] 하위 라우트가 하나일 때 항상 표시 여부(1:예 0:아니오)
     */
    private Integer alwaysShow;

    /**
     * 라우트 파라미터
     */
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String params;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 수정 시간
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}