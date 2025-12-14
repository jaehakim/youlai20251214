package com.youlai.boot.platform.codegen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.config.property.CodegenProperties;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.platform.codegen.service.CodegenService;
import com.youlai.boot.platform.codegen.model.form.GenConfigForm;
import com.youlai.boot.platform.codegen.model.query.TablePageQuery;
import com.youlai.boot.platform.codegen.model.vo.CodegenPreviewVO;
import com.youlai.boot.platform.codegen.model.vo.TablePageVO;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.platform.codegen.service.GenConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 코드 생성器컨트롤러
 *
 * @author Ray
 * @since 2.10.0
 */
@Tag(name = "11.코드 생성")
@RestController
@RequestMapping("/api/v1/codegen")
@RequiredArgsConstructor
@Slf4j
public class CodegenController {

    private final CodegenService codegenService;
    private final GenConfigService genConfigService;
    private final CodegenProperties codegenProperties;

    @Operation(summary = "데이터 테이블 페이지 목록 조회")
    @GetMapping("/table/page")
    @Log(value = "코드 생성페이지 목록", module = LogModuleEnum.OTHER)
    public PageResult<TablePageVO> getTablePage(
            TablePageQuery queryParams
    ) {
        Page<TablePageVO> result = codegenService.getTablePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "조회코드 생성설정")
    @GetMapping("/{tableName}/config")
    public Result<GenConfigForm> getGenConfigFormData(
            @Parameter(description = "테이블명", example = "sys_user") @PathVariable String tableName
    ) {
        GenConfigForm formData = genConfigService.getGenConfigFormData(tableName);
        return Result.success(formData);
    }

    @Operation(summary = "저장코드 생성설정")
    @PostMapping("/{tableName}/config")
    @Log(value = "생성代码", module = LogModuleEnum.OTHER)
    public Result<?> saveGenConfig(@RequestBody GenConfigForm formData) {
        genConfigService.saveGenConfig(formData);
        return Result.success();
    }

    @Operation(summary = "삭제코드 생성설정")
    @DeleteMapping("/{tableName}/config")
    public Result<?> deleteGenConfig(
            @Parameter(description = "테이블명", example = "sys_user") @PathVariable String tableName
    ) {
        genConfigService.deleteGenConfig(tableName);
        return Result.success();
    }

    @Operation(summary = "조회预览생성代码")
    @GetMapping("/{tableName}/preview")
    @Log(value = "预览생성代码", module = LogModuleEnum.OTHER)
    public Result<List<CodegenPreviewVO>> getTablePreviewData(@PathVariable String tableName,
                                                              @RequestParam(value = "pageType", required = false, defaultValue = "classic") String pageType) {
        List<CodegenPreviewVO> list = codegenService.getCodegenPreviewData(tableName, pageType);
        return Result.success(list);
    }

    @Operation(summary = "코드 다운로드")
    @GetMapping("/{tableName}/download")
    @Log(value = "코드 다운로드", module = LogModuleEnum.OTHER)
    public void downloadZip(HttpServletResponse response, @PathVariable String tableName,
                            @RequestParam(value = "pageType", required = false, defaultValue = "classic") String pageType) {
        String[] tableNames = tableName.split(",");
        byte[] data = codegenService.downloadCode(tableNames, pageType);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(codegenProperties.getDownloadFileName(), StandardCharsets.UTF_8));
        response.setContentType("application/octet-stream; charset=UTF-8");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            log.error("Error while writing the zip file to response", e);
            throw new RuntimeException("Failed to write the zip file to response", e);
        }
    }
}
