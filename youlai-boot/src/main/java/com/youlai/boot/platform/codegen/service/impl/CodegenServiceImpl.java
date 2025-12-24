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
 * 데이터베이스 서비스 구현 클래스
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
     * 데이터 테이블페이지 목록
     *
     * @param queryParams 조회파라미터수
     * @return 페이지 결과
     */
    public Page<TablePageVO> getTablePage(TablePageQuery queryParams) {
        Page<TablePageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        // 제외할 테이블 설정
        List<String> excludeTables = codegenProperties.getExcludeTables();
        queryParams.setExcludeTables(excludeTables);

        return databaseMapper.getTablePage(page, queryParams);
    }

    /**
     * 생성 코드 미리보기 조회
     *
     * @param tableName 테이블명
     * @return 미리보기 데이터
     */
    @Override
    public List<CodegenPreviewVO> getCodegenPreviewData(String tableName, String pageType) {

        List<CodegenPreviewVO> list = new ArrayList<>();

        GenConfig genConfig = genConfigService.getOne(new LambdaQueryWrapper<GenConfig>()
                .eq(GenConfig::getTableName, tableName)
        );
        if (genConfig == null) {
            throw new BusinessException("테이블 생성 설정을 찾을 수 없습니다");
        }

        List<GenFieldConfig> fieldConfigs = genFieldConfigService.list(new LambdaQueryWrapper<GenFieldConfig>()
                .eq(GenFieldConfig::getConfigId, genConfig.getId())
                .orderByAsc(GenFieldConfig::getFieldSort)

        );
        if (CollectionUtil.isEmpty(fieldConfigs)) {
            throw new BusinessException("필드 생성 설정을 찾을 수 없습니다");
        }

        // 템플릿 설정 순회
        Map<String, CodegenProperties.TemplateConfig> templateConfigs = codegenProperties.getTemplateConfigs();
        for (Map.Entry<String, CodegenProperties.TemplateConfig> templateConfigEntry : templateConfigs.entrySet()) {
            CodegenPreviewVO previewVO = new CodegenPreviewVO();

            CodegenProperties.TemplateConfig templateConfig = templateConfigEntry.getValue();

            /* 1. 파일명 생성 UserController */
            // User Role Menu Dept
            String entityName = genConfig.getEntityName();
            // Controller Service Mapper Entity
            String templateName = templateConfigEntry.getKey();
            // .java .ts .vue
            String extension = templateConfig.getExtension();

            // 파일명 UserController.java
            String fileName = getFileName(entityName, templateName, extension);
            previewVO.setFileName(fileName);

            /* 2. 파일 경로 생성 */
            // 패키지명: com.youlai.boot
            String packageName = genConfig.getPackageName();
            // 모듈명: system
            String moduleName = genConfig.getModuleName();
            // 서브패키지명: controller
            String subpackageName = templateConfig.getSubpackageName();
            // 파일 경로 조합: src/main/java/com/youlai/boot/system/controller
            String filePath = getFilePath(templateName, moduleName, packageName, subpackageName, entityName);
            previewVO.setPath(filePath);

            /* 3. 파일 내용 생성 */
            // 템플릿 파일의 변수를 구체적인 값으로 치환하여 코드 내용 생성
            // 우선사용저장의 ui，없음그러면사용요청 파라미터수
            String finalType = StrUtil.blankToDefault(genConfig.getPageType(), pageType);
            String content = getCodeContent(templateConfig, genConfig, fieldConfigs, finalType);
            previewVO.setContent(content);

            list.add(previewVO);
        }
        return list;
    }

    /**
     * 파일명 생성
     *
     * @param entityName   엔티티클래스이름 UserController
     * @param templateName 템플릿이름 Entity
     * @param extension    파일 확장자 .java
     * @return 파일명
     */
    private String getFileName(String entityName, String templateName, String extension) {
        if ("Entity".equals(templateName)) {
            return entityName + extension;
        } else if ("MapperXml".equals(templateName)) {
            return entityName + "Mapper" + extension;
        } else if ("API".equals(templateName)) {
            // user-api.ts 네이밍 생성
            return StrUtil.toSymbolCase(entityName, '-') + "-api" + extension;
        } else if ("VIEW".equals(templateName)) {
            return "index.vue";
        }
        return entityName + templateName + extension;
    }

    /**
     * 파일 경로 생성
     *
     * @param templateName   템플릿이름 Entity
     * @param moduleName     모듈명 system
     * @param packageName    패키지명 com.youlai
     * @param subPackageName 서브패키지명 controller
     * @param entityName     엔티티클래스이름 UserController
     * @return 파일 경로 src/main/java/com/youlai/system/controller
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
     * 코드 내용 생성
     *
     * @param templateConfig 템플릿설정
     * @param genConfig      생성 설정
     * @param fieldConfigs   필드 설정
     * @return 코드 내용
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
        // UI에 따라 다른 프론트엔드 페이지 템플릿 선택: 기본 index.vue.vm; 캡슐화 버전은 index.curd.vue.vm 사용
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
     * @param tableNames 테이블명수그룹，여러 개 지원테이블。
     * @return 압축 파일 바이트 배열
     */
    @Override
    public byte[] downloadCode(String[] tableNames, String ui) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(outputStream)) {

            // 각 테이블명을 순회하여 해당 코드를 생성하고 zip 파일로 압축
            for (String tableName : tableNames) {
                generateAndZipCode(tableName, zip, ui);
            }
            // 모든 압축 데이터가 출력 스트림에 기록되도록 보장, 메모리 버퍼에 데이터가 남아 데이터 불완전 문제 발생 방지
            zip.finish();
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error while generating zip for code download", e);
            throw new RuntimeException("Failed to generate code zip file", e);
        }
    }

    /**
     * 테이블명에 따라 코드를 생성하고 zip 파일로 압축
     *
     * @param tableName 테이블명
     * @param zip       압축 파일 출력 스트림
     */
    private void generateAndZipCode(String tableName, ZipOutputStream zip, String ui) {
        List<CodegenPreviewVO> codePreviewList = getCodegenPreviewData(tableName, ui);

        for (CodegenPreviewVO codePreview : codePreviewList) {
            String fileName = codePreview.getFileName();
            String content = codePreview.getContent();
            String path = codePreview.getPath();

            try {
                // 압축 엔트리 생성
                ZipEntry zipEntry = new ZipEntry(path + File.separator + fileName);
                zip.putNextEntry(zipEntry);

                // 파일 내용 작성
                zip.write(content.getBytes(StandardCharsets.UTF_8));

                // 현재 압축 엔트리 닫기
                zip.closeEntry();

            } catch (IOException e) {
                log.error("Error while adding file {} to zip", fileName, e);
            }
        }
    }

}
