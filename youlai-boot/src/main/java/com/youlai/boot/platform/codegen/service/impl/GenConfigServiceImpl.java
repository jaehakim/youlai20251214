package com.youlai.boot.platform.codegen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.YouLaiBootApplication;
import com.youlai.boot.common.enums.EnvEnum;
import com.youlai.boot.platform.codegen.enums.FormTypeEnum;
import com.youlai.boot.platform.codegen.enums.JavaTypeEnum;
import com.youlai.boot.platform.codegen.enums.QueryTypeEnum;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.config.property.CodegenProperties;
import com.youlai.boot.platform.codegen.converter.CodegenConverter;
import com.youlai.boot.platform.codegen.mapper.DatabaseMapper;
import com.youlai.boot.platform.codegen.mapper.GenConfigMapper;
import com.youlai.boot.platform.codegen.model.bo.ColumnMetaData;
import com.youlai.boot.platform.codegen.model.bo.TableMetaData;
import com.youlai.boot.platform.codegen.model.entity.GenConfig;
import com.youlai.boot.platform.codegen.model.entity.GenFieldConfig;
import com.youlai.boot.platform.codegen.model.form.GenConfigForm;
import com.youlai.boot.platform.codegen.service.GenConfigService;
import com.youlai.boot.platform.codegen.service.GenFieldConfigService;
import com.youlai.boot.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 데이터베이스 서비스 구현 클래스
 *
 * @author Ray
 * @since 2.10.0
 */
@Service
@RequiredArgsConstructor
public class GenConfigServiceImpl extends ServiceImpl<GenConfigMapper, GenConfig> implements GenConfigService {

    private final DatabaseMapper databaseMapper;
    private final CodegenProperties codegenProperties;
    private final GenFieldConfigService genFieldConfigService;
    private final CodegenConverter codegenConverter;

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    private final MenuService menuService;

    /**
     * 코드 생성 설정 조회
     *
     * @param tableName 테이블명 eg: sys_user
     * @return 코드 생성 설정
     */
    @Override
    public GenConfigForm getGenConfigFormData(String tableName) {
        // 테이블 생성 설정 조회
        GenConfig genConfig = this.getOne(
                new LambdaQueryWrapper<>(GenConfig.class)
                        .eq(GenConfig::getTableName, tableName)
                        .last("LIMIT 1")
        );

        // 코드 생성 설정 존재 여부
        boolean hasGenConfig = genConfig != null;

        // 코드 생성 설정이 없으면 테이블의 메타데이터를 기반으로 기본 설정 생성
        if (genConfig == null) {
            TableMetaData tableMetadata = databaseMapper.getTableMetadata(tableName);
            Assert.isTrue(tableMetadata != null, "테이블 메타데이터를 찾을 수 없습니다");

            genConfig = new GenConfig();
            genConfig.setTableName(tableName);

            // 테이블 주석을 비즈니스 이름으로 사용, '表' 제거 예: 사용자表 -> 사용자
            String tableComment = tableMetadata.getTableComment();
            if (StrUtil.isNotBlank(tableComment)) {
                genConfig.setBusinessName(tableComment.replace("表", "").trim());
            }
            // 테이블명으로 엔티티 클래스명 생성, 접두사 제거 지원 예: sys_user -> SysUser
            String removePrefix = genConfig.getRemoveTablePrefix();
            String processedTable = tableName;
            if (StrUtil.isNotBlank(removePrefix) && StrUtil.startWith(tableName, removePrefix)) {
                processedTable = StrUtil.removePrefix(tableName, removePrefix);
            }
            genConfig.setEntityName(StrUtil.toCamelCase(StrUtil.upperFirst(StrUtil.toCamelCase(processedTable))));

            genConfig.setPackageName(YouLaiBootApplication.class.getPackageName());
            genConfig.setModuleName(codegenProperties.getDefaultConfig().getModuleName()); // 기본 모듈명
            genConfig.setAuthor(codegenProperties.getDefaultConfig().getAuthor());
        }

        // 테이블의 컬럼 + 기존에 저장된 필드 생성 설정을 조합하여 최종 필드 생성 설정 생성
        List<GenFieldConfig> genFieldConfigs = new ArrayList<>();

        // 테이블의 컬럼 조회
        List<ColumnMetaData> tableColumns = databaseMapper.getTableColumns(tableName);
        if (CollectionUtil.isNotEmpty(tableColumns)) {
            // 필드 생성 설정 조회
            List<GenFieldConfig> fieldConfigList = genFieldConfigService.list(
                    new LambdaQueryWrapper<GenFieldConfig>()
                            .eq(GenFieldConfig::getConfigId, genConfig.getId())
                            .orderByAsc(GenFieldConfig::getFieldSort)
            );
            Integer maxSort = fieldConfigList.stream()
                    .map(GenFieldConfig::getFieldSort)
                    .filter(Objects::nonNull) // null 값 필터링
                    .max(Integer::compareTo)
                    .orElse(0);
            for (ColumnMetaData tableColumn : tableColumns) {
                // 컬럼명으로 필드 생성 설정 조회
                String columnName = tableColumn.getColumnName();
                GenFieldConfig fieldConfig = fieldConfigList.stream()
                        .filter(item -> StrUtil.equals(item.getColumnName(), columnName))
                        .findFirst()
                        .orElseGet(() -> createDefaultFieldConfig(tableColumn));
                if (fieldConfig.getFieldSort() == null) {
                    fieldConfig.setFieldSort(++maxSort);
                }
                // 컬럼 유형에 따라 필드 유형 설정
                String fieldType = fieldConfig.getFieldType();
                if (StrUtil.isBlank(fieldType)) {
                    String javaType = JavaTypeEnum.getJavaTypeByColumnType(fieldConfig.getColumnType());
                    fieldConfig.setFieldType(javaType);
                }
                // 코드 생성 설정이 없으면 기본적으로 목록과 폼에 표시
                if (!hasGenConfig) {
                    fieldConfig.setIsShowInList(1);
                    fieldConfig.setIsShowInForm(1);
                }
                genFieldConfigs.add(fieldConfig);
            }
        }
        // genFieldConfigs를 fieldSort 기준으로 정렬
        genFieldConfigs = genFieldConfigs.stream().sorted(Comparator.comparing(GenFieldConfig::getFieldSort)).toList();
        GenConfigForm genConfigForm = codegenConverter.toGenConfigForm(genConfig, genFieldConfigs);

        genConfigForm.setFrontendAppName(codegenProperties.getFrontendAppName());
        genConfigForm.setBackendAppName(codegenProperties.getBackendAppName());
        return genConfigForm;
    }


    /**
     * 기본 필드 설정 생성
     *
     * @param columnMetaData 테이블 필드 메타데이터
     * @return
     */
    private GenFieldConfig createDefaultFieldConfig(ColumnMetaData columnMetaData) {
        GenFieldConfig fieldConfig = new GenFieldConfig();
        fieldConfig.setColumnName(columnMetaData.getColumnName());
        fieldConfig.setColumnType(columnMetaData.getDataType());
        fieldConfig.setFieldComment(columnMetaData.getColumnComment());
        fieldConfig.setFieldName(StrUtil.toCamelCase(columnMetaData.getColumnName()));
        fieldConfig.setIsRequired("YES".equals(columnMetaData.getIsNullable()) ? 0 : 1);

        if (fieldConfig.getColumnType().equals("date")) {
            fieldConfig.setFormType(FormTypeEnum.DATE);
        } else if (fieldConfig.getColumnType().equals("datetime")) {
            fieldConfig.setFormType(FormTypeEnum.DATE_TIME);
        } else {
            fieldConfig.setFormType(FormTypeEnum.INPUT);
        }

        fieldConfig.setQueryType(QueryTypeEnum.EQ);
        fieldConfig.setMaxLength(columnMetaData.getCharacterMaximumLength());
        return fieldConfig;
    }

    /**
     * 코드 생성 설정 저장
     *
     * @param formData 코드 생성 설정 폼
     */
    @Override
    public void saveGenConfig(GenConfigForm formData) {
        GenConfig genConfig = codegenConverter.toGenConfig(formData);
        this.saveOrUpdate(genConfig);

        // 상위 메뉴를 선택하고 현재 환경이 프로덕션이 아니면 메뉴 저장
        Long parentMenuId = formData.getParentMenuId();
        if (parentMenuId != null && !EnvEnum.PROD.getValue().equals(springProfilesActive)) {
            menuService.addMenuForCodegen(parentMenuId, genConfig);
        }

        List<GenFieldConfig> genFieldConfigs = codegenConverter.toGenFieldConfig(formData.getFieldConfigs());

        if (CollectionUtil.isEmpty(genFieldConfigs)) {
            throw new BusinessException("필드 설정은 비어있을 수 없습니다");
        }
        genFieldConfigs.forEach(genFieldConfig -> {
            genFieldConfig.setConfigId(genConfig.getId());
        });
        genFieldConfigService.saveOrUpdateBatch(genFieldConfigs);
    }

    /**
     * 코드 생성 설정 삭제
     *
     * @param tableName 테이블명
     */
    @Override
    public void deleteGenConfig(String tableName) {
        GenConfig genConfig = this.getOne(new LambdaQueryWrapper<GenConfig>()
                .eq(GenConfig::getTableName, tableName));

        boolean result = this.remove(new LambdaQueryWrapper<GenConfig>()
                .eq(GenConfig::getTableName, tableName)
        );
        if (result) {
            genFieldConfigService.remove(new LambdaQueryWrapper<GenFieldConfig>()
                    .eq(GenFieldConfig::getConfigId, genConfig.getId())
            );
        }
    }



}
