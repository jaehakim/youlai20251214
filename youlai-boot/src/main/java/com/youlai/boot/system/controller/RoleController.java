package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.common.annotation.RepeatSubmit;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.system.model.form.RoleForm;
import com.youlai.boot.system.model.query.RolePageQuery;
import com.youlai.boot.system.model.vo.RolePageVO;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.system.service.RoleService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * 역할컨트롤러
 *
 * @author Ray.Hao
 * @since 2022/10/16
 */
@Tag(name = "03.역할인터페이스")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "역할 페이지목록")
    @GetMapping("/page")
    @Log(value = "역할 페이지목록", module = LogModuleEnum.ROLE)
    public PageResult<RolePageVO> getRolePage(
            RolePageQuery queryParams
    ) {
        Page<RolePageVO> result = roleService.getRolePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "역할 드롭다운 목록")
    @GetMapping("/options")
    public Result<List<Option<Long>>> listRoleOptions() {
        List<Option<Long>> list = roleService.listRoleOptions();
        return Result.success(list);
    }

    @Operation(summary = "추가역할")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('sys:role:add')")
    @RepeatSubmit
    public Result<?> addRole(@Valid @RequestBody RoleForm roleForm) {
        boolean result = roleService.saveRole(roleForm);
        return Result.judge(result);
    }

    @Operation(summary = "역할 폼 데이터 조회")
    @GetMapping("/{roleId}/form")
    @PreAuthorize("@ss.hasPerm('sys:role:edit')")
    public Result<RoleForm> getRoleForm(
            @Parameter(description = "역할ID") @PathVariable Long roleId
    ) {
        RoleForm roleForm = roleService.getRoleForm(roleId);
        return Result.success(roleForm);
    }

    @Operation(summary = "수정역할")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('sys:role:edit')")
    public Result<?> updateRole(@Valid @RequestBody RoleForm roleForm) {
        boolean result = roleService.saveRole(roleForm);
        return Result.judge(result);
    }

    @Operation(summary = "삭제역할")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('sys:role:delete')")
    public Result<Void> deleteRoles(
            @Parameter(description = "삭제역할，여러 개는영문쉼표(,)로 연결") @PathVariable String ids
    ) {
        roleService.deleteRoles(ids);
        return Result.success();
    }

    @Operation(summary = "수정역할상태")
    @PutMapping(value = "/{roleId}/status")
    @PreAuthorize("@ss.hasPerm('sys:role:edit')")
    public Result<?> updateRoleStatus(
            @Parameter(description = "역할ID") @PathVariable Long roleId,
            @Parameter(description = "상태(1:활성화;0:비활성화)") @RequestParam Integer status
    ) {
        boolean result = roleService.updateRoleStatus(roleId, status);
        return Result.judge(result);
    }

    @Operation(summary = "역할의 메뉴 ID 집합 조회")
    @GetMapping("/{roleId}/menuIds")
    public Result<List<Long>> getRoleMenuIds(
            @Parameter(description = "역할ID") @PathVariable Long roleId
    ) {
        List<Long> menuIds = roleService.getRoleMenuIds(roleId);
        return Result.success(menuIds);
    }

    @Operation(summary = "역할메뉴 할당권한")
    @PutMapping("/{roleId}/menus")
    public Result<Void> assignMenusToRole(
            @PathVariable Long roleId,
            @RequestBody List<Long> menuIds
    ) {
        roleService.assignMenusToRole(roleId, menuIds);
        return Result.success();
    }
}
