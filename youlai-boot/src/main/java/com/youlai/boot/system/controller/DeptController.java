package com.youlai.boot.system.controller;

import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.common.annotation.RepeatSubmit;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.system.model.form.DeptForm;
import com.youlai.boot.system.model.query.DeptQuery;
import com.youlai.boot.system.model.vo.DeptVO;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.system.service.DeptService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 부서컨트롤러
 *
 * @author Ray.Hao
 * @since 2020/11/6
 */
@Tag(name = "05.부서인터페이스")
@RestController
@RequestMapping("/api/v1/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @Operation(summary = "부서 목록")
    @GetMapping
    @Log( value = "부서 목록",module = LogModuleEnum.DEPT)
    public Result<List<DeptVO>> getDeptList(
             DeptQuery queryParams
    ) {
        List<DeptVO> list = deptService.getDeptList(queryParams);
        return Result.success(list);
    }

    @Operation(summary = "부서 드롭다운 목록")
    @GetMapping("/options")
    public Result<List<Option<Long>>> getDeptOptions() {
        List<Option<Long>> list = deptService.listDeptOptions();
        return Result.success(list);
    }

    @Operation(summary = "추가부서")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('sys:dept:add')")
    @RepeatSubmit
    public Result<?> saveDept(
            @Valid @RequestBody DeptForm formData
    ) {
        Long id = deptService.saveDept(formData);
        return Result.success(id);
    }

    @Operation(summary = "부서 폼 데이터 조회")
    @GetMapping("/{deptId}/form")
    public Result<DeptForm> getDeptForm(
            @Parameter(description ="부서ID") @PathVariable Long deptId
    ) {
        DeptForm deptForm = deptService.getDeptForm(deptId);
        return Result.success(deptForm);
    }

    @Operation(summary = "수정부서")
    @PutMapping(value = "/{deptId}")
    @PreAuthorize("@ss.hasPerm('sys:dept:edit')")
    public Result<?> updateDept(
            @PathVariable Long deptId,
            @Valid @RequestBody DeptForm formData
    ) {
        deptId = deptService.updateDept(deptId, formData);
        return Result.success(deptId);
    }

    @Operation(summary = "삭제부서")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('sys:dept:delete')")
    public Result<?> deleteDepartments(
            @Parameter(description ="부서ID，여러 개는영문쉼표(,)로 구분") @PathVariable("ids") String ids
    ) {
        boolean result = deptService.deleteByIds(ids);
        return Result.judge(result);
    }

}
