package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.system.model.form.ConfigForm;
import com.youlai.boot.system.model.query.ConfigPageQuery;
import com.youlai.boot.system.model.vo.ConfigVO;
import com.youlai.boot.system.service.ConfigService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 시스템 설정프론트엔드 컨트롤러
 *
 * @author Theo
 * @since 2024-07-30 11:25
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "08.시스템 설정")
@RequestMapping("/api/v1/config")
public class ConfigController {

    private final ConfigService configService;

    @Operation(summary = "시스템 설정페이지 목록")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('sys:config:query')")
    @Log( value = "시스템 설정페이지 목록",module = LogModuleEnum.SETTING)
    public PageResult<ConfigVO> page(@ParameterObject ConfigPageQuery configPageQuery) {
        IPage<ConfigVO> result = configService.page(configPageQuery);
        return PageResult.success(result);
    }

    @Operation(summary = "추가시스템 설정")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('sys:config:add')")
    @Log( value = "추가시스템 설정",module = LogModuleEnum.SETTING)
    public Result<?> save(@RequestBody @Valid ConfigForm configForm) {
        return Result.judge(configService.save(configForm));
    }

    @Operation(summary = "시스템 설정 폼 데이터 조회")
    @GetMapping("/{id}/form")
    public Result<ConfigForm> getConfigForm(
            @Parameter(description = "시스템 설정ID") @PathVariable Long id
    ) {
        ConfigForm formData = configService.getConfigFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "새로고침시스템 설정캐시")
    @PutMapping("/refresh")
    @PreAuthorize("@ss.hasPerm('sys:config:refresh')")
    @Log( value = "새로고침시스템 설정캐시",module = LogModuleEnum.SETTING)
    public Result<ConfigForm> refreshCache() {
        return Result.judge(configService.refreshCache());
    }

    @Operation(summary = "수정시스템 설정")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('sys:config:update')")
    @Log( value = "수정시스템 설정",module = LogModuleEnum.SETTING)
    public Result<?> update(@Valid @PathVariable Long id, @RequestBody ConfigForm configForm) {
        return Result.judge(configService.edit(id, configForm));
    }

    @Operation(summary = "삭제시스템 설정")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPerm('sys:config:delete')")
    @Log( value = "삭제시스템 설정",module = LogModuleEnum.SETTING)
    public Result<?> delete(@PathVariable Long id) {
        return Result.judge(configService.delete(id));
    }

}
