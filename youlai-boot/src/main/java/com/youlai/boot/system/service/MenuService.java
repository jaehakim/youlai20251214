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
 * 메뉴비즈니스인터페이스
 * 
 * @author haoxr
 * @since 2020/11/06
 */
public interface MenuService extends IService<Menu> {

    /**
     * 조회메뉴表格목록
     */
    List<MenuVO> listMenus(MenuQuery queryParams);

    /**
     * 조회메뉴 드롭다운 목록
     *
     * @param onlyParent 부모 메뉴만 조회 여부
     */
    List<Option<Long>> listMenuOptions(boolean onlyParent);

    /**
     * 추가메뉴
     *
     * @param menuForm  메뉴폼객체
     */
    boolean saveMenu(MenuForm menuForm);

    /**
     * 조회현재사용자의메뉴라우트 목록
     */
    List<RouteVO> listCurrentUserRoutes();

    /**
     * 조회현재사용자의메뉴라우트 목록（지정된데이터源）
     *
     * @param datasource 데이터源이름，如：master(主库)、naiveui(NaiveUI데이터库)、template(템플릿데이터库)
     */
    List<RouteVO> listCurrentUserRoutes(String datasource);

    /**
     * 메뉴 표시 상태 수정
     * 
     * @param menuId 메뉴ID
     * @param visible 여부표시(1-표시 0-숨김)
     */
    boolean updateMenuVisible(Long menuId, Integer visible);

    /**
     * 조회메뉴 폼 데이터
     *
     * @param id 메뉴ID
     */
    MenuForm getMenuForm(Long id);

    /**
     * 삭제메뉴
     *
     * @param id 메뉴ID
     */
    boolean deleteMenu(Long id);

    /**
     * 코드 생성时添加메뉴
     *
     * @param parentMenuId 父메뉴ID
     * @param genConfig   实体名
     */
    void addMenuForCodegen(Long parentMenuId, GenConfig genConfig);
}
