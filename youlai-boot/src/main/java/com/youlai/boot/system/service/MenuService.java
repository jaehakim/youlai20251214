package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.platform.codegen.model.entity.GenConfig;
import com.youlai.boot.system.model.form.MenuForm;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.model.entity.Menu;
import com.youlai.boot.system.model.query.MenuQuery;
import com.youlai.boot.system.model.vo.MenuVO;
import com.youlai.boot.system.model.vo.RouteVO;

import java.util.List;
import java.util.Set;

/**
 * 메뉴 비즈니스 인터페이스
 *
 * @author haoxr
 * @since 2020/11/06
 */
public interface MenuService extends IService<Menu> {

    /**
     * 조회 메뉴 테이블 목록
     */
    List<MenuVO> listMenus(MenuQuery queryParams);

    /**
     * 조회 메뉴 드롭다운 목록
     *
     * @param onlyParent 부모 메뉴만 조회 여부
     */
    List<Option<Long>> listMenuOptions(boolean onlyParent);

    /**
     * 추가 메뉴
     *
     * @param menuForm  메뉴 폼 객체
     */
    boolean saveMenu(MenuForm menuForm);

    /**
     * 조회 현재 사용자의 메뉴 라우트 목록
     */
    List<RouteVO> listCurrentUserRoutes();

    /**
     * 조회 현재 사용자의 메뉴 라우트 목록(지정된 데이터 소스)
     *
     * @param datasource 데이터 소스 이름, 예: master(주 DB), naiveui(NaiveUI DB), template(템플릿 DB)
     */
    List<RouteVO> listCurrentUserRoutes(String datasource);

    /**
     * 메뉴 표시 상태 수정
     *
     * @param menuId 메뉴 ID
     * @param visible 표시 여부(1-표시 0-숨김)
     */
    boolean updateMenuVisible(Long menuId, Integer visible);

    /**
     * 조회 메뉴 폼 데이터
     *
     * @param id 메뉴 ID
     */
    MenuForm getMenuForm(Long id);

    /**
     * 삭제 메뉴
     *
     * @param id 메뉴 ID
     */
    boolean deleteMenu(Long id);

    /**
     * 코드 생성 시 메뉴 추가
     *
     * @param parentMenuId 부모 메뉴 ID
     * @param genConfig   엔티티명
     */
    void addMenuForCodegen(Long parentMenuId, GenConfig genConfig);
}
