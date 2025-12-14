package com.youlai.boot.platform.codegen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.platform.codegen.enums.JavaTypeEnum;
import com.youlai.boot.config.property.CodegenProperties;
import com.youlai.boot.platform.codegen.service.GenConfigService;
import com.youlai.boot.platform.codegen.service.GenFieldConfigService;
import com.youlai.boot.platform.codegen.service.CodegenService;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.platform.codegen.mapper.DatabaseMapper;
import com.youlai.boot.platform.codegen.model.entity.GenConfig;
import com.youlai.boot.platform.codegen.model.entity.GenFieldConfig;
import com.youlai.boot.platform.codegen.model.query.TablePageQuery;
import com.youlai.boot.platform.codegen.model.vo.CodegenPreviewVO;
import com.youlai.boot.platform.codegen.model.vo.TablePageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 데이터库서비스구현类
 *
 * @author Ray
 * @since 2.10.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CodegenServiceImpl implements CodegenService {

    private final DatabaseMapper databaseMapper;
    private final CodegenProperties codegenProperties;
    private final GenConfigService genConfigService;
    private final GenFieldConfigService genFieldConfigService;

    /**
     * 데이터表페이지 목록
     *
     * @param queryParams 조회参수
     * @return 페이지결과
     */
    public Page<TablePageVO> getTablePage(TablePageQuery queryParams) {
        Page<TablePageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        // 设置排除의表
        List<String> excludeTables = codegenProperties.getExcludeTables();
        queryParams.setExcludeTables(excludeTables);

        return databaseMapper.getTablePage(page, queryParams);
    }

    /**
     * 조회预览생성代码
     *
     * @param tableName 테이블명
     * @return 预览데이터
     */
    @Override
    public List<CodegenPreviewVO> getCodegenPreviewData(String tableName, String pageType) {

        List<CodegenPreviewVO> list = new ArrayList<>();

        GenConfig genConfig = genConfigService.getOne(new LambdaQueryWrapper<GenConfig>()
                .eq(GenConfig::getTableName, tableName)
        );
        if (genConfig == null) {
            throw new BusinessException("미找到表생성 설정");
        }

        List<GenFieldConfig> fieldConfigs = genFieldConfigService.list(new LambdaQueryWrapper<GenFieldConfig>()
                .eq(GenFieldConfig::getConfigId, genConfig.getId())
                .orderByAsc(GenFieldConfig::getFieldSort)

        );
        if (CollectionUtil.isEmpty(fieldConfigs)) {
            throw new BusinessException("미找到字段생성 설정");
        }

        // 遍历템플릿설정
        Map<String, CodegenProperties.TemplateConfig> templateConfigs = codegenProperties.getTemplateConfigs();
        for (Map.Entry<String, CodegenProperties.TemplateConfig> templateConfigEntry : templateConfigs.entrySet()) {
            CodegenPreviewVO previewVO = new CodegenPreviewVO();

            CodegenProperties.TemplateConfig templateConfig = templateConfigEntry.getValue();

            /* 1. 생성파일名 UserController */
            // User Role Menu Dept
            String entityName = genConfig.getEntityName();
            // Controller Service Mapper Entity
            String templateName = templateConfigEntry.getKey();
            // .java .ts .vue
            String extension = templateConfig.getExtension();

            // 파일名 UserController.java
            String fileName = getFileName(entityName, templateName, extension);
            previewVO.setFileName(fileName);

            /* 2. 생성파일경로 */
            // 包名：com.youlai.boot
            String packageName = genConfig.getPackageName();
            // 模块名：system
            String moduleName = genConfig.getModuleName();
            // 子包名：controller
            String subpackageName = templateConfig.getSubpackageName();
            // 组合成파일경로：src/main/java/com/youlai/boot/system/controller
            String filePath = getFilePath(templateName, moduleName, packageName, subpackageName, entityName);
            previewVO.setPath(filePath);

            /* 3. 생성파일내용 */
            // 을템플릿파일중의变量替换값具体의值 생성代码내용
            // 优先사용저장의 ui，没有则사용请求参수
            String finalType = StrUtil.blankToDefault(genConfig.getPageType(), pageType);
            String content = getCodeContent(templateConfig, genConfig, fieldConfigs, finalType);
            previewVO.setContent(content);

            list.add(previewVO);
        }
        return list;
    }

    /**
     * 생성파일名
     *
     * @param entityName   实体类名 UserController
     * @param templateName 템플릿名 Entity
     * @param extension    파일후缀 .java
     * @return 파일名
     */
    private String getFileName(String entityName, String templateName, String extension) {
        if ("Entity".equals(templateName)) {
            return entityName + extension;
        } else if ("MapperXml".equals(templateName)) {
            return entityName + "Mapper" + extension;
        } else if ("API".equals(templateName)) {
            // 생성 user-api.ts 命名
            return StrUtil.toSymbolCase(entityName, '-') + "-api" + extension;
        } else if ("VIEW".equals(templateName)) {
            return "index.vue";
        }
        return entityName + templateName + extension;
    }

    /**
     * 생성파일경로
     *
     * @param templateName   템플릿名 Entity
     * @param moduleName     模块名 system
     * @param packageName    包名 com.youlai
     * @param subPackageName 子包名 controller
     * @param entityName     实体类名 UserController
     * @return 파일경로 src/main/java/com/youlai/system/controller
     */
    private String getFilePath(String templateName, String moduleName, String packageName, String subPackageName, String entityName) {
        String path;
        if ("MapperXml".equals(templateName)) {
            path = (codegenProperties.getBackendAppName()
                    + File.separator
                    + "src" + File.separator + "main" + File.separator + "resources"
                    + File.separator + subPackageName
                    + File.separator + moduleName
            );
        } else if ("API".equals(templateName)) {
            // path = "src/api/system";
            path = (codegenProperties.getFrontendAppName()
                    + File.separator + "src"
                    + File.separator + subPackageName
                    + File.separator + moduleName
            );
        } else if ("VIEW".equals(templateName)) {
            // path = "src/views/system/user";
            path = (codegenProperties.getFrontendAppName()
                    + File.separator + "src"
                    + File.separator + subPackageName
                    + File.separator + moduleName
                    + File.separator + StrUtil.toSymbolCase(entityName, '-')
            );
        } else {
            path = (codegenProperties.getBackendAppName()
                    + File.separator
                    + "src" + File.separator + "main" + File.separator + "java"
                    + File.separator + packageName
                    + File.separator + moduleName
                    + File.separator + subPackageName
            );
        }

        // subPackageName = model.entity => model/entity
        path = path.replace(".", File.separator);

        return path;
    }

    /**
     * 생성代码내용
     *
     * @param templateConfig 템플릿설정
     * @param genConfig      생성 설정
     * @param fieldConfigs   필드 설정
     * @return 代码내용
     */
    private String getCodeContent(CodegenProperties.TemplateConfig templateConfig, GenConfig genConfig, List<GenFieldConfig> fieldConfigs, String pageType) {

        Map<String, Object> bindMap = new HashMap<>();

        String entityName = genConfig.getEntityName();

        bindMap.put("packageName", genConfig.getPackageName());
        bindMap.put("moduleName", genConfig.getModuleName());
        bindMap.put("subpackageName", templateConfig.getSubpackageName());
        bindMap.put("date", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm"));
        bindMap.put("entityName", entityName);
        bindMap.put("tableName", genConfig.getTableName());
        bindMap.put("author", genConfig.getAuthor());
        bindMap.put("lowerFirstEntityName", StrUtil.lowerFirst(entityName)); // UserTest → userTest
        bindMap.put("kebabCaseEntityName", StrUtil.toSymbolCase(entityName, '-')); // UserTest → user-test
        bindMap.put("businessName", genConfig.getBusinessName());
        bindMap.put("fieldConfigs", fieldConfigs);

        boolean hasLocalDateTime = false;
        boolean hasBigDecimal = false;
        boolean hasRequiredField = false;

        for (GenFieldConfig fieldConfig : fieldConfigs) {

            if ("LocalDateTime".equals(fieldConfig.getFieldType())) {
                hasLocalDateTime = true;
            }
            if ("BigDecimal".equals(fieldConfig.getFieldType())) {
                hasBigDecimal = true;
            }
            if (ObjectUtil.equals(fieldConfig.getIsRequired(), 1)) {
                hasRequiredField = true;
            }
            fieldConfig.setTsType(JavaTypeEnum.getTsTypeByJavaType(fieldConfig.getFieldType()));
        }

        bindMap.put("hasLocalDateTime", hasLocalDateTime);
        bindMap.put("hasBigDecimal", hasBigDecimal);
        bindMap.put("hasRequiredField", hasRequiredField);

        TemplateEngine templateEngine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        // 根据 ui 选择不同의前端页面템플릿：默认 index.vue.vm；封装版사용 index.curd.vue.vm
        String path = templateConfig.getTemplatePath();
        if ("curd".equalsIgnoreCase(pageType) && path.endsWith("index.vue.vm")) {
            path = path.replace("index.vue.vm", "index.curd.vue.vm");
        }
        Template template = templateEngine.getTemplate(path);

        return template.render(bindMap);
    }

    /**
     * 코드 다운로드
     *
     * @param tableNames 테이블명수组，支持多张表。
     * @return 压缩파일字节수组
     */
    @Override
    public byte[] downloadCode(String[] tableNames, String ui) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(outputStream)) {

            // 遍历每个테이블명，생성对应의代码并压缩到 zip 파일중
            for (String tableName : tableNames) {
                generateAndZipCode(tableName, zip, ui);
            }
            // 确保所有压缩데이터写入输出流，避免데이터残留에内存缓冲区引发의데이터不完整
            zip.finish();
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error while generating zip for code download", e);
            throw new RuntimeException("Failed to generate code zip file", e);
        }
    }

    /**
     * 根据테이블명생성代码并压缩到zip파일중
     *
     * @param tableName 테이블명
     * @param zip       压缩파일输出流
     */
    private void generateAndZipCode(String tableName, ZipOutputStream zip, String ui) {
        List<CodegenPreviewVO> codePreviewList = getCodegenPreviewData(tableName, ui);

        for (CodegenPreviewVO codePreview : codePreviewList) {
            String fileName = codePreview.getFileName();
            String content = codePreview.getContent();
            String path = codePreview.getPath();

            try {
                // 생성压缩条目
                ZipEntry zipEntry = new ZipEntry(path + File.separator + fileName);
                zip.putNextEntry(zipEntry);

                // 写入파일내용
                zip.write(content.getBytes(StandardCharsets.UTF_8));

                // 关闭현재压缩条目
                zip.closeEntry();

            } catch (IOException e) {
                log.error("Error while adding file {} to zip", fileName, e);
            }
        }
    }

}
