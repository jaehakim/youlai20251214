package com.youlai.boot.system.controller;

import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.common.annotation.RepeatSubmit;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.system.model.form.MenuForm;
import com.youlai.boot.system.model.query.MenuQuery;
import com.youlai.boot.system.model.vo.MenuVO;
import com.youlai.boot.system.model.vo.RouteVO;
import com.youlai.boot.system.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 메뉴컨트롤러
 *
 * @author Ray.Hao
 * @since 2020/11/06
 */
@Tag(name = "04.메뉴인터페이스")
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 목록")
    @GetMapping
    @Log(value = "메뉴 목록", module = LogModuleEnum.MENU)
    public Result<List<MenuVO>> getMenus(MenuQuery queryParams) {
        List<MenuVO> menuList = menuService.listMenus(queryParams);
        return Result.success(menuList);
    }

    @Operation(summary = "메뉴 드롭다운 목록")
    @GetMapping("/options")
    public Result<List<Option<Long>>> getMenuOptions(
            @Parameter(description = "부모 메뉴만 조회 여부")
            @RequestParam(required = false, defaultValue = "false") boolean onlyParent
    ) {
        List<Option<Long>> menus = menuService.listMenuOptions(onlyParent);
        return Result.success(menus);
    }

    @Operation(summary = "현재 사용자 메뉴 라우트 목록")
    @GetMapping("/routes")
    public Result<List<RouteVO>> getCurrentUserRoutes() {
        List<RouteVO> routeList = menuService.listCurrentUserRoutes();
        return Result.success(routeList);
    }

    @Operation(summary = "메뉴 폼 데이터")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('sys:menu:edit')")
    public Result<MenuForm> getMenuForm(
            @Parameter(description = "메뉴ID") @PathVariable Long id
    ) {
        MenuForm menu = menuService.getMenuForm(id);
        return Result.success(menu);
    }

    @Operation(summary = "추가메뉴")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('sys:menu:add')")
    @RepeatSubmit
    public Result<?> addMenu(@RequestBody MenuForm menuForm) {
        boolean result = menuService.saveMenu(menuForm);
        return Result.judge(result);
    }

    @Operation(summary = "수정메뉴")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('sys:menu:edit')")
    public Result<?> updateMenu(
            @RequestBody MenuForm menuForm
    ) {
        boolean result = menuService.saveMenu(menuForm);
        return Result.judge(result);
    }

    @Operation(summary = "삭제메뉴")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPerm('sys:menu:delete')")
    public Result<?> deleteMenu(
            @Parameter(description = "메뉴 ID, 여러 개는 영문(,)로 구분") @PathVariable("id") Long id
    ) {
        boolean result = menuService.deleteMenu(id);
        return Result.judge(result);
    }

    @Operation(summary = "메뉴 표시 상태 수정")
    @PatchMapping("/{menuId}")
    @PreAuthorize("@ss.hasPerm('sys:menu:edit')")
    public Result<?> updateMenuVisible(
            @Parameter(description = "메뉴ID") @PathVariable Long menuId,
            @Parameter(description = "표시 상태(1:표시;0:숨김)") Integer visible

    ) {
        boolean result = menuService.updateMenuVisible(menuId, visible);
        return Result.judge(result);
    }

}

