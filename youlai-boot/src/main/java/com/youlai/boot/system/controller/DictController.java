package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.system.model.form.DictItemForm;
import com.youlai.boot.system.model.query.DictItemPageQuery;
import com.youlai.boot.system.model.query.DictPageQuery;
import com.youlai.boot.system.model.vo.DictItemOptionVO;
import com.youlai.boot.system.model.vo.DictItemPageVO;
import com.youlai.boot.system.model.vo.DictPageVO;
import com.youlai.boot.common.annotation.RepeatSubmit;
import com.youlai.boot.system.model.form.DictForm;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.system.service.DictItemService;
import com.youlai.boot.system.service.DictService;
import com.youlai.boot.platform.websocket.service.WebSocketService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 사전컨트롤러
 *
 * @author Ray.Hao
 * @since 2.9.0
 */
@Tag(name = "06.사전인터페이스")
@RestController
@SuppressWarnings("SpellCheckingInspection")
@RequestMapping("/api/v1/dicts")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;
    private final DictItemService dictItemService;
    private final WebSocketService webSocketService;

    //---------------------------------------------------
    // 사전 관련 인터페이스
    //---------------------------------------------------
    @Operation(summary = "사전 페이지목록")
    @GetMapping("/page")
    @Log( value = "사전 페이지목록",module = LogModuleEnum.DICT)
    public PageResult<DictPageVO> getDictPage(
            DictPageQuery queryParams
    ) {
        Page<DictPageVO> result = dictService.getDictPage(queryParams);
        return PageResult.success(result);
    }


    @Operation(summary = "사전목록")
    @GetMapping
    public Result<List<Option<String>>> getDictList() {
        List<Option<String>> list = dictService.getDictList();
        return Result.success(list);
    }

    @Operation(summary = "사전 폼 데이터 조회")
    @GetMapping("/{id}/form")
    public Result<DictForm> getDictForm(
            @Parameter(description = "사전ID") @PathVariable Long id
    ) {
        DictForm formData = dictService.getDictForm(id);
        return Result.success(formData);
    }

    @Operation(summary = "추가사전")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('sys:dict:add')")
    @RepeatSubmit
    public Result<?> saveDict(@Valid @RequestBody DictForm formData) {
        boolean result = dictService.saveDict(formData);
        // 발송사전업데이트공지
        if (result) {
            webSocketService.broadcastDictChange(formData.getDictCode());
        }
        return Result.judge(result);
    }

    @Operation(summary = "수정사전")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPerm('sys:dict:edit')")
    public Result<?> updateDict(
            @PathVariable Long id,
            @RequestBody DictForm dictForm
    ) {
        boolean status = dictService.updateDict(id, dictForm);
        // 발송사전업데이트공지
        if (status && dictForm.getDictCode() != null) {
          webSocketService.broadcastDictChange(dictForm.getDictCode());
        }
        return Result.judge(status);
    }

    @Operation(summary = "삭제사전")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('sys:dict:delete')")
    public Result<?> deleteDictionaries(
            @Parameter(description = "사전ID，여러 개는영문쉼표(,)로 연결") @PathVariable String ids
    ) {
        // 조회사전 코드목록，용도발송삭제공지
        List<String> dictCodes = dictService.getDictCodesByIds(Arrays.stream(ids.split(",")).toList());
        
        dictService.deleteDictByIds(Arrays.stream(ids.split(",")).toList());
        
        // 발송사전삭제공지
        for (String dictCode : dictCodes) {
          webSocketService.broadcastDictChange(dictCode);
        }
        
        return Result.success();
    }


    //---------------------------------------------------
    // 사전 항목 관련 인터페이스
    //---------------------------------------------------
    @Operation(summary = "사전 항목페이지 목록")
    @GetMapping("/{dictCode}/items/page")
    public PageResult<DictItemPageVO> getDictItemPage(
            @PathVariable String dictCode,
            DictItemPageQuery queryParams
    ) {
        queryParams.setDictCode(dictCode);
        Page<DictItemPageVO> result = dictItemService.getDictItemPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "사전 항목목록")
    @GetMapping("/{dictCode}/items")
    public Result<List<DictItemOptionVO>> getDictItems(
            @Parameter(description = "사전 코드") @PathVariable String dictCode
    ) {
        List<DictItemOptionVO> list = dictItemService.getDictItems(dictCode);
        return Result.success(list);
    }

    @Operation(summary = "추가사전 항목")
    @PostMapping("/{dictCode}/items")
    @PreAuthorize("@ss.hasPerm('sys:dict-item:add')")
    @RepeatSubmit
    public Result<Void> saveDictItem(
            @PathVariable String dictCode,
            @Valid @RequestBody DictItemForm formData
    ) {
        formData.setDictCode(dictCode);
        boolean result = dictItemService.saveDictItem(formData);
        
        // 발송사전업데이트공지
        if (result) {
          webSocketService.broadcastDictChange(dictCode);
        }
        
        return Result.judge(result);
    }

    @Operation(summary = "사전 항목 폼 데이터")
    @GetMapping("/{dictCode}/items/{itemId}/form")
    public Result<DictItemForm> getDictItemForm(
            @PathVariable String dictCode,
            @Parameter(description = "사전 항목ID") @PathVariable Long itemId
    ) {
        DictItemForm formData = dictItemService.getDictItemForm(itemId);
        return Result.success(formData);
    }

    @Operation(summary = "수정사전 항목")
    @PutMapping("/{dictCode}/items/{itemId}")
    @PreAuthorize("@ss.hasPerm('sys:dict-item:edit')")
    @RepeatSubmit
    public Result<?> updateDictItem(
            @PathVariable String dictCode,
            @PathVariable Long itemId,
            @RequestBody DictItemForm formData
    ) {
        formData.setId(itemId);
        formData.setDictCode(dictCode);
        boolean status = dictItemService.updateDictItem(formData);
        
        // 발송사전업데이트공지
        if (status) {
            webSocketService.broadcastDictChange(dictCode);
        }
        
        return Result.judge(status);
    }

    @Operation(summary = "삭제사전 항목")
    @DeleteMapping("/{dictCode}/items/{itemIds}")
    @PreAuthorize("@ss.hasPerm('sys:dict-item:delete')")
    public Result<Void> deleteDictItems(
            @PathVariable String dictCode,
            @Parameter(description = "사전ID，여러 개는영문쉼표(,)로 연결") @PathVariable String itemIds
    ) {
        dictItemService.deleteDictItemByIds(itemIds);
        
        // 발송사전업데이트공지
        webSocketService.broadcastDictChange(dictCode);
        
        return Result.success();
    }

}
