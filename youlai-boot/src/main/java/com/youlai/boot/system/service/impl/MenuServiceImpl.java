package com.youlai.boot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.platform.codegen.model.entity.GenConfig;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.system.converter.MenuConverter;
import com.youlai.boot.system.mapper.MenuMapper;
import com.youlai.boot.system.model.entity.Menu;
import com.youlai.boot.system.model.form.MenuForm;
import com.youlai.boot.system.model.query.MenuQuery;
import com.youlai.boot.system.model.vo.MenuVO;
import com.youlai.boot.system.model.vo.RouteVO;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.system.enums.MenuTypeEnum;
import com.youlai.boot.common.enums.StatusEnum;
import com.youlai.boot.common.model.KeyValue;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.service.MenuService;
import com.youlai.boot.system.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 메뉴 서비스 구현 클래스
 *
 * @author Ray.Hao
 * @since 2020/11/06
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final MenuConverter menuConverter;

    private final RoleMenuService roleMenuService;


    /**
     * 메뉴 목록
     *
     * @param queryParams {@link MenuQuery}
     */
    @Override
    public List<MenuVO> listMenus(MenuQuery queryParams) {
        List<Menu> menus = this.list(new LambdaQueryWrapper<Menu>()
                .like(StrUtil.isNotBlank(queryParams.getKeywords()), Menu::getName, queryParams.getKeywords())
                .orderByAsc(Menu::getSort)
        );
        // 모든 메뉴 ID 조회
        Set<Long> menuIds = menus.stream()
                .map(Menu::getId)
                .collect(Collectors.toSet());

        // 모든 부모 ID 조회
        Set<Long> parentIds = menus.stream()
                .map(Menu::getParentId)
                .collect(Collectors.toSet());

        // 루트 노드 ID 조회 (재귀의 시작점), 즉 부모 노드 ID 중 메뉴 ID에 포함되지 않는 노드, 주의: 최상위 메뉴 0을 루트 노드로 사용할 수 없음, 메뉴 필터링 시 0이 제외되기 때문
        List<Long> rootIds = parentIds.stream()
                .filter(id -> !menuIds.contains(id))
                .toList();

        // 재귀 함수를 사용하여 메뉴 트리 구성
        return rootIds.stream()
                .flatMap(rootId -> buildMenuTree(rootId, menus).stream())
                .collect(Collectors.toList());
    }

    /**
     * 재귀적으로 메뉴 목록 생성
     *
     * @param parentId 부모 ID
     * @param menuList 메뉴 목록
     * @return 메뉴 목록
     */
    private List<MenuVO> buildMenuTree(Long parentId, List<Menu> menuList) {
        return CollectionUtil.emptyIfNull(menuList)
                .stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(entity -> {
                    MenuVO menuVO = menuConverter.toVo(entity);
                    List<MenuVO> children = buildMenuTree(entity.getId(), menuList);
                    menuVO.setChildren(children);
                    return menuVO;
                }).toList();
    }

    /**
     * 메뉴 드롭다운 데이터
     *
     * @param onlyParent 부모 메뉴만 조회 여부, true인 경우 버튼 제외
     */
    @Override
    public List<Option<Long>> listMenuOptions(boolean onlyParent) {
        List<Menu> menuList = this.list(new LambdaQueryWrapper<Menu>()
                .in(onlyParent, Menu::getType, MenuTypeEnum.CATALOG.getValue(), MenuTypeEnum.MENU.getValue())
                .orderByAsc(Menu::getSort)
        );
        return buildMenuOptions(SystemConstants.ROOT_NODE_ID, menuList);
    }

    /**
     * 재귀적으로 메뉴 드롭다운 계층 목록 생성
     *
     * @param parentId 부모 ID
     * @param menuList 메뉴 목록
     * @return 메뉴 드롭다운 목록
     */
    private List<Option<Long>> buildMenuOptions(Long parentId, List<Menu> menuList) {
        List<Option<Long>> menuOptions = new ArrayList<>();

        for (Menu menu : menuList) {
            if (menu.getParentId().equals(parentId)) {
                Option<Long> option = new Option<>(menu.getId(), menu.getName());
                List<Option<Long>> subMenuOptions = buildMenuOptions(menu.getId(), menuList);
                if (!subMenuOptions.isEmpty()) {
                    option.setChildren(subMenuOptions);
                }
                menuOptions.add(option);
            }
        }

        return menuOptions;
    }

    /**
     * 현재 사용자의 메뉴 라우트 목록 조회
     */
    @Override
    public List<RouteVO> listCurrentUserRoutes() {
        Set<String> roleCodes = SecurityUtils.getRoles();

        if (CollectionUtil.isEmpty(roleCodes)) {
            return Collections.emptyList();
        }

        List<Menu> menuList;
        if (SecurityUtils.isRoot()) {
            // 슈퍼 관리자는 모든 메뉴 조회
            menuList = this.list(new LambdaQueryWrapper<Menu>()
                    .ne(Menu::getType, MenuTypeEnum.BUTTON.getValue())
                    .orderByAsc(Menu::getSort)
            );
        } else {
            menuList = this.baseMapper.getMenusByRoleCodes(roleCodes);
        }
        return buildRoutes(SystemConstants.ROOT_NODE_ID, menuList);
    }

    /**
     * 현재 사용자의 메뉴 라우트 목록 조회 (지정된 데이터 소스)
     *
     * @param datasource 데이터 소스 이름
     *                   - master: 메인 데이터베이스 메뉴 데이터
     *                   - naiveui: NaiveUI 프로젝트 메뉴 데이터
     *                   - template: 템플릿 프로젝트 메뉴 데이터
     */
    @Override
    public List<RouteVO> listCurrentUserRoutes(String datasource) {
        return listCurrentUserRoutes();
    }


    /**
     * 재귀적으로 메뉴 라우트 계층 목록 생성
     *
     * @param parentId 부모 ID
     * @param menuList 메뉴 목록
     * @return 라우트 계층 목록
     */
    private List<RouteVO> buildRoutes(Long parentId, List<Menu> menuList) {
        List<RouteVO> routeList = new ArrayList<>();

        for (Menu menu : menuList) {
            if (menu.getParentId().equals(parentId)) {
                RouteVO routeVO = toRouteVo(menu);
                List<RouteVO> children = buildRoutes(menu.getId(), menuList);
                if (!children.isEmpty()) {
                    routeVO.setChildren(children);
                }
                routeList.add(routeVO);
            }
        }

        return routeList;
    }

    /**
     * Menu 엔티티로 RouteVO 생성
     */
    private RouteVO toRouteVo(Menu menu) {
        RouteVO routeVO = new RouteVO();
        // 라우트 이름 조회
        String routeName = menu.getRouteName();
        if (StrUtil.isBlank(routeName)) {
            // 라우트 name은 카멜케이스 필요, 첫 글자는 대문자
            routeName = StringUtils.capitalize(StrUtil.toCamelCase(menu.getRoutePath(), '-'));
        }
        // name으로 라우트 이동: this.$router.push({name:xxx})
        routeVO.setName(routeName);

        // path로 라우트 이동: this.$router.push({path:xxx})
        routeVO.setPath(menu.getRoutePath());
        routeVO.setRedirect(menu.getRedirect());
        routeVO.setComponent(menu.getComponent());

        RouteVO.Meta meta = new RouteVO.Meta();
        meta.setTitle(menu.getName());
        meta.setIcon(menu.getIcon());
        meta.setHidden(StatusEnum.DISABLE.getValue().equals(menu.getVisible()));
        // 【메뉴】페이지 캐시 활성화 여부
        if (MenuTypeEnum.MENU.getValue().equals(menu.getType())
                && ObjectUtil.equals(menu.getKeepAlive(), 1)) {
            meta.setKeepAlive(true);
        }
        meta.setAlwaysShow(ObjectUtil.equals(menu.getAlwaysShow(), 1));

        String paramsJson = menu.getParams();
        // JSON 문자열을 Map<String, String>로 변환
        if (StrUtil.isNotBlank(paramsJson)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> paramMap = objectMapper.readValue(paramsJson, new TypeReference<>() {
                });
                meta.setParams(paramMap);
            } catch (Exception e) {
                throw new RuntimeException("파라미터 파싱 실패", e);
            }
        }
        routeVO.setMeta(meta);
        return routeVO;
    }

    /**
     * 메뉴 추가/수정
     */
    @Override
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public boolean saveMenu(MenuForm menuForm) {

        Integer menuType = menuForm.getType();

        if (MenuTypeEnum.CATALOG.getValue().equals(menuType)) {  // 디렉토리인 경우
            String path = menuForm.getRoutePath();
            if (menuForm.getParentId() == 0 && !path.startsWith("/")) {
                menuForm.setRoutePath("/" + path); // 최상위 디렉토리는 /로 시작 필요
            }
            menuForm.setComponent("Layout");
        } else if (MenuTypeEnum.EXTLINK.getValue().equals(menuType)) {
            // 외부 링크 메뉴 컴포넌트를 null로 설정
            menuForm.setComponent(null);
        }
        if (Objects.equals(menuForm.getParentId(), menuForm.getId())) {
            throw new RuntimeException("부모 메뉴는 현재 메뉴가 될 수 없습니다");
        }
        Menu entity = menuConverter.toEntity(menuForm);
        String treePath = generateMenuTreePath(menuForm.getParentId());
        entity.setTreePath(treePath);

        List<KeyValue> params = menuForm.getParams();
        // 라우트 파라미터 [{key:"id",value:"1"}, {key:"name",value:"장삼"}]를 [{"id":"1"},{"name":"장삼"}]로 변환
        if (CollectionUtil.isNotEmpty(params)) {
            entity.setParams(JSONUtil.toJsonStr(params.stream()
                    .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue))));
        } else {
            entity.setParams(null);
        }
        // 메뉴 타입 추가 시 라우트 이름은 고유해야 함
        if (MenuTypeEnum.MENU.getValue().equals(menuType)) {
            Assert.isFalse(this.exists(new LambdaQueryWrapper<Menu>()
                    .eq(Menu::getRouteName, entity.getRouteName())
                    .ne(menuForm.getId() != null, Menu::getId, menuForm.getId())
            ), "라우트 이름이 이미 존재합니다");
        } else {
            // 다른 타입인 경우 라우트 이름을 null로 설정
            entity.setRouteName(null);
        }

        boolean result = this.saveOrUpdate(entity);
        if (result) {
            // 편집 시 역할 권한 캐시 새로고침
            if (menuForm.getId() != null) {
                roleMenuService.refreshRolePermsCache();
            }
        }
        // 메뉴 수정 시 하위 메뉴가 있으면 하위 메뉴의 트리 경로 업데이트
        updateChildrenTreePath(entity.getId(), treePath);
        return result;
    }

    /**
     * 하위 메뉴 트리 경로 업데이트
     *
     * @param id       현재 메뉴 ID
     * @param treePath 현재 메뉴 트리 경로
     */
    private void updateChildrenTreePath(Long id, String treePath) {
        List<Menu> children = this.list(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        if (CollectionUtil.isNotEmpty(children)) {
            // 하위 메뉴의 트리 경로는 부모 메뉴의 트리 경로에 부모 메뉴 ID를 더한 값
            String childTreePath = treePath + "," + id;
            this.update(new LambdaUpdateWrapper<Menu>()
                    .eq(Menu::getParentId, id)
                    .set(Menu::getTreePath, childTreePath)
            );
            for (Menu child : children) {
                // 재귀적으로 하위 메뉴 업데이트
                updateChildrenTreePath(child.getId(), childTreePath);
            }
        }
    }

    /**
     * 메뉴 경로 생성
     *
     * @param parentId 부모 ID
     * @return 부모 노드 경로는 영문 쉼표(,)로 구분, 예: 1,2,3
     */
    private String generateMenuTreePath(Long parentId) {
        if (SystemConstants.ROOT_NODE_ID.equals(parentId)) {
            return String.valueOf(parentId);
        } else {
            Menu parent = this.getById(parentId);
            return parent != null ? parent.getTreePath() + "," + parent.getId() : null;
        }
    }


    /**
     * 메뉴 표시 상태 수정
     *
     * @param menuId  메뉴 ID
     * @param visible 표시 여부 (1->표시, 2->숨김)
     * @return 수정 성공 여부
     */
    @Override
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public boolean updateMenuVisible(Long menuId, Integer visible) {
        return this.update(new LambdaUpdateWrapper<Menu>()
                .eq(Menu::getId, menuId)
                .set(Menu::getVisible, visible)
        );
    }

    /**
     * 메뉴 폼 데이터 조회
     *
     * @param id 메뉴 ID
     * @return 메뉴 폼 데이터
     */
    @Override
    public MenuForm getMenuForm(Long id) {
        Menu entity = this.getById(id);
        Assert.isTrue(entity != null, "메뉴가 존재하지 않습니다");
        MenuForm formData = menuConverter.toForm(entity);
        // 라우트 파라미터 문자열 {"id":"1","name":"장삼"}를 [{key:"id", value:"1"}, {key:"name", value:"장삼"}]로 변환
        String params = entity.getParams();
        if (StrUtil.isNotBlank(params)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // JSON 문자열을 Map<String, String>로 파싱
                Map<String, String> paramMap = objectMapper.readValue(params, new TypeReference<>() {
                });

                // List<KeyValue> 형식으로 변환: [{key:"id", value:"1"}, {key:"name", value:"장삼"}]
                List<KeyValue> transformedList = paramMap.entrySet().stream()
                        .map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
                        .toList();

                // 변환된 목록을 MenuForm에 저장
                formData.setParams(transformedList);
            } catch (Exception e) {
                throw new RuntimeException("파라미터 파싱 실패", e);
            }
        }

        return formData;
    }

    /**
     * 메뉴 삭제
     *
     * @param id 메뉴 ID
     * @return 삭제 성공 여부
     */
    @Override
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public boolean deleteMenu(Long id) {
        boolean result = this.remove(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getId, id)
                .or()
                .apply("CONCAT (',',tree_path,',') LIKE CONCAT('%,',{0},',%')", id));


        // 역할 권한 캐시 새로고침
        if (result) {
            roleMenuService.refreshRolePermsCache();
        }
        return result;

    }

    /**
     * 코드 생성 시 메뉴 추가
     *
     * @param parentMenuId 부모 메뉴 ID
     * @param genConfig    엔티티 정보
     */
    @Override
    public void addMenuForCodegen(Long parentMenuId, GenConfig genConfig) {
        Menu parentMenu = this.getById(parentMenuId);
        Assert.notNull(parentMenu, "상위 메뉴가 존재하지 않습니다");

        String entityName = genConfig.getEntityName();

        long count = this.count(new LambdaQueryWrapper<Menu>().eq(Menu::getRouteName, entityName));
        if (count > 0) {
            return;
        }

        // 부모 메뉴의 하위 메뉴 중 최대 정렬 값 조회
        Menu maxSortMenu = this.getOne(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, parentMenuId)
                .orderByDesc(Menu::getSort)
                .last("limit 1")
        );
        int sort = 1;
        if (maxSortMenu != null) {
            sort = maxSortMenu.getSort() + 1;
        }

        Menu menu = new Menu();
        menu.setParentId(parentMenuId);
        menu.setName(genConfig.getBusinessName());

        menu.setRouteName(entityName);
        menu.setRoutePath(StrUtil.toSymbolCase(entityName, '-'));
        menu.setComponent(genConfig.getModuleName() + "/" + StrUtil.toSymbolCase(entityName, '-') + "/index");
        menu.setType(MenuTypeEnum.MENU.getValue());
        menu.setSort(sort);
        menu.setVisible(1);
        boolean result = this.save(menu);

        if (result) {
            // treePath 생성
            String treePath = generateMenuTreePath(parentMenuId);
            menu.setTreePath(treePath);
            this.updateById(menu);

            // CRUD 버튼 권한 생성
            String permPrefix = genConfig.getModuleName() + ":" + genConfig.getTableName().replace("_", "-") + ":";
            String[] actions = {"조회", "추가", "편집", "삭제"};
            String[] perms = {"query", "add", "edit", "delete"};

            for (int i = 0; i < actions.length; i++) {
                Menu button = new Menu();
                button.setParentId(menu.getId());
                button.setType(MenuTypeEnum.BUTTON.getValue());
                button.setName(actions[i]);
                button.setPerm(permPrefix + perms[i]);
                button.setSort(i + 1);
                this.save(button);

                // treePath 생성
                button.setTreePath(treePath + "," + button.getId());
                this.updateById(button);
            }
        }
    }

}
