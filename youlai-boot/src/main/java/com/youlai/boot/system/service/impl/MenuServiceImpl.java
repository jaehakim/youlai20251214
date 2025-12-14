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
 * 메뉴서비스구현类
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
        // 조회所有메뉴ID
        Set<Long> menuIds = menus.stream()
                .map(Menu::getId)
                .collect(Collectors.toSet());

        // 조회所有부모ID
        Set<Long> parentIds = menus.stream()
                .map(Menu::getParentId)
                .collect(Collectors.toSet());

        // 조회根节点ID（递归의起点），即父节点ID중不包含에부서ID중의节点，注意这里不能拿顶级메뉴 O 作값根节点，因값메뉴筛选의时候 O 会被过滤掉
        List<Long> rootIds = parentIds.stream()
                .filter(id -> !menuIds.contains(id))
                .toList();

        // 사용递归函수来构建메뉴树
        return rootIds.stream()
                .flatMap(rootId -> buildMenuTree(rootId, menus).stream())
                .collect(Collectors.toList());
    }

    /**
     * 递归생성메뉴 목록
     *
     * @param parentId 부모ID
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
     * 메뉴下拉데이터
     *
     * @param onlyParent 부모 메뉴만 조회 여부 如果값true，排除按钮
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
     * 递归생성메뉴下拉层级목록
     *
     * @param parentId 부모ID
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
     * 조회현재사용자의메뉴라우트 목록
     */
    @Override
    public List<RouteVO> listCurrentUserRoutes() {
        Set<String> roleCodes = SecurityUtils.getRoles();

        if (CollectionUtil.isEmpty(roleCodes)) {
            return Collections.emptyList();
        }

        List<Menu> menuList;
        if (SecurityUtils.isRoot()) {
            // 超级관리员조회所有메뉴
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
     * 조회현재사용자의메뉴라우트 목록（지정된데이터源）
     * 
     * @param datasource 데이터源이름
     *                   - master: 主库메뉴데이터
     *                   - naiveui: NaiveUI项目메뉴데이터  
     *                   - template: 템플릿项目메뉴데이터
     */
    @Override
    public List<RouteVO> listCurrentUserRoutes(String datasource) {
        return listCurrentUserRoutes();
    }


    /**
     * 递归생성메뉴路由层级목록
     *
     * @param parentId 부모ID
     * @param menuList 메뉴 목록
     * @return 路由层级목록
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
     * 根据RouteBO생성RouteVO
     */
    private RouteVO toRouteVo(Menu menu) {
        RouteVO routeVO = new RouteVO();
        // 조회路由이름
        String routeName = menu.getRouteName();
        if (StrUtil.isBlank(routeName)) {
            // 路由 name 需要驼峰，首字母大写
            routeName = StringUtils.capitalize(StrUtil.toCamelCase(menu.getRoutePath(), '-'));
        }
        // 根据name路由跳转 this.$router.push({name:xxx})
        routeVO.setName(routeName);

        // 根据path路由跳转 this.$router.push({path:xxx})
        routeVO.setPath(menu.getRoutePath());
        routeVO.setRedirect(menu.getRedirect());
        routeVO.setComponent(menu.getComponent());

        RouteVO.Meta meta = new RouteVO.Meta();
        meta.setTitle(menu.getName());
        meta.setIcon(menu.getIcon());
        meta.setHidden(StatusEnum.DISABLE.getValue().equals(menu.getVisible()));
        // 【메뉴】여부开启页面캐시
        if (MenuTypeEnum.MENU.getValue().equals(menu.getType())
                && ObjectUtil.equals(menu.getKeepAlive(), 1)) {
            meta.setKeepAlive(true);
        }
        meta.setAlwaysShow(ObjectUtil.equals(menu.getAlwaysShow(), 1));

        String paramsJson = menu.getParams();
        // 을 JSON 字符串转换값 Map<String, String>
        if (StrUtil.isNotBlank(paramsJson)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> paramMap = objectMapper.readValue(paramsJson, new TypeReference<>() {
                });
                meta.setParams(paramMap);
            } catch (Exception e) {
                throw new RuntimeException("解析参수실패", e);
            }
        }
        routeVO.setMeta(meta);
        return routeVO;
    }

    /**
     * 추가/수정메뉴
     */
    @Override
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public boolean saveMenu(MenuForm menuForm) {

        Integer menuType = menuForm.getType();

        if (MenuTypeEnum.CATALOG.getValue().equals(menuType)) {  // 如果是目录
            String path = menuForm.getRoutePath();
            if (menuForm.getParentId() == 0 && !path.startsWith("/")) {
                menuForm.setRoutePath("/" + path); // 원级目录需는 / 开头
            }
            menuForm.setComponent("Layout");
        } else if (MenuTypeEnum.EXTLINK.getValue().equals(menuType)) {
            // 外链메뉴컴포넌트设置값 null
            menuForm.setComponent(null);
        }
        if (Objects.equals(menuForm.getParentId(), menuForm.getId())) {
            throw new RuntimeException("부모메뉴不能값현재메뉴");
        }
        Menu entity = menuConverter.toEntity(menuForm);
        String treePath = generateMenuTreePath(menuForm.getParentId());
        entity.setTreePath(treePath);

        List<KeyValue> params = menuForm.getParams();
        // 路由参수 [{key:"id",value:"1"}，{key:"name",value:"张三"}] 转换값 [{"id":"1"},{"name":"张三"}]
        if (CollectionUtil.isNotEmpty(params)) {
            entity.setParams(JSONUtil.toJsonStr(params.stream()
                    .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue))));
        } else {
            entity.setParams(null);
        }
        // 추가유형값메뉴时候 路由이름唯원
        if (MenuTypeEnum.MENU.getValue().equals(menuType)) {
            Assert.isFalse(this.exists(new LambdaQueryWrapper<Menu>()
                    .eq(Menu::getRouteName, entity.getRouteName())
                    .ne(menuForm.getId() != null, Menu::getId, menuForm.getId())
            ), "路由이름이미存에");
        } else {
            // 其他유형时 给路由이름赋值값空
            entity.setRouteName(null);
        }

        boolean result = this.saveOrUpdate(entity);
        if (result) {
            // 编辑새로고침역할 권한캐시
            if (menuForm.getId() != null) {
                roleMenuService.refreshRolePermsCache();
            }
        }
        // 수정메뉴如果有子메뉴，则업데이트子메뉴의树경로
        updateChildrenTreePath(entity.getId(), treePath);
        return result;
    }

    /**
     * 업데이트子메뉴树경로
     *
     * @param id       현재메뉴ID
     * @param treePath 현재메뉴树경로
     */
    private void updateChildrenTreePath(Long id, String treePath) {
        List<Menu> children = this.list(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, id));
        if (CollectionUtil.isNotEmpty(children)) {
            // 子메뉴의树경로等于父메뉴의树경로加上父메뉴ID
            String childTreePath = treePath + "," + id;
            this.update(new LambdaUpdateWrapper<Menu>()
                    .eq(Menu::getParentId, id)
                    .set(Menu::getTreePath, childTreePath)
            );
            for (Menu child : children) {
                // 递归업데이트子메뉴
                updateChildrenTreePath(child.getId(), childTreePath);
            }
        }
    }

    /**
     * 부서경로생성
     *
     * @param parentId 父ID
     * @return 父节点경로는영문쉼표(, )로 구분，eg: 1,2,3
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
     * @param menuId  메뉴ID
     * @param visible 여부표시(1->표시；2->숨김)
     * @return 여부수정성공
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
     * 조회메뉴 폼 데이터
     *
     * @param id 메뉴ID
     * @return 메뉴 폼 데이터
     */
    @Override
    public MenuForm getMenuForm(Long id) {
        Menu entity = this.getById(id);
        Assert.isTrue(entity != null, "메뉴不存에");
        MenuForm formData = menuConverter.toForm(entity);
        // 路由参수字符串 {"id":"1","name":"张三"} 转换값 [{key:"id", value:"1"}, {key:"name", value:"张三"}]
        String params = entity.getParams();
        if (StrUtil.isNotBlank(params)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 解析 JSON 字符串값 Map<String, String>
                Map<String, String> paramMap = objectMapper.readValue(params, new TypeReference<>() {
                });

                // 转换값 List<KeyValue> 格式 [{key:"id", value:"1"}, {key:"name", value:"张三"}]
                List<KeyValue> transformedList = paramMap.entrySet().stream()
                        .map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
                        .toList();

                // 을转换후의목록저장 MenuForm
                formData.setParams(transformedList);
            } catch (Exception e) {
                throw new RuntimeException("解析参수실패", e);
            }
        }

        return formData;
    }

    /**
     * 삭제메뉴
     *
     * @param id 메뉴ID
     * @return 여부삭제성공
     */
    @Override
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public boolean deleteMenu(Long id) {
        boolean result = this.remove(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getId, id)
                .or()
                .apply("CONCAT (',',tree_path,',') LIKE CONCAT('%,',{0},',%')", id));


        // 새로고침역할 권한캐시
        if (result) {
            roleMenuService.refreshRolePermsCache();
        }
        return result;

    }

    /**
     * 코드 생성时添加메뉴
     *
     * @param parentMenuId 父메뉴ID
     * @param genConfig    实体이름
     */
    @Override
    public void addMenuForCodegen(Long parentMenuId, GenConfig genConfig) {
        Menu parentMenu = this.getById(parentMenuId);
        Assert.notNull(parentMenu, "上级메뉴不存에");

        String entityName = genConfig.getEntityName();

        long count = this.count(new LambdaQueryWrapper<Menu>().eq(Menu::getRouteName, entityName));
        if (count > 0) {
            return;
        }

        // 조회부모메뉴子메뉴最带의정렬
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
            // 생성treePath
            String treePath = generateMenuTreePath(parentMenuId);
            menu.setTreePath(treePath);
            this.updateById(menu);

            // 생성CURD버튼 권한
            String permPrefix = genConfig.getModuleName() + ":" + genConfig.getTableName().replace("_", "-") + ":";
            String[] actions = {"조회", "추가", "编辑", "삭제"};
            String[] perms = {"query", "add", "edit", "delete"};

            for (int i = 0; i < actions.length; i++) {
                Menu button = new Menu();
                button.setParentId(menu.getId());
                button.setType(MenuTypeEnum.BUTTON.getValue());
                button.setName(actions[i]);
                button.setPerm(permPrefix + perms[i]);
                button.setSort(i + 1);
                this.save(button);

                // 생성treePath
                button.setTreePath(treePath + "," + button.getId());
                this.updateById(button);
            }
        }
    }

}
