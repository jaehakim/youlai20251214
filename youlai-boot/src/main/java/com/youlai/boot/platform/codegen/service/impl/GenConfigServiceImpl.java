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
 * 데이터库서비스구현类
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
     * 조회코드 생성설정
     *
     * @param tableName 테이블명 eg: sys_user
     * @return 코드 생성설정
     */
    @Override
    public GenConfigForm getGenConfigFormData(String tableName) {
        // 조회表생성 설정
        GenConfig genConfig = this.getOne(
                new LambdaQueryWrapper<>(GenConfig.class)
                        .eq(GenConfig::getTableName, tableName)
                        .last("LIMIT 1")
        );

        // 여부有코드 생성설정
        boolean hasGenConfig = genConfig != null;

        // 如果没有코드 생성설정，则根据表의元데이터생성默认설정
        if (genConfig == null) {
            TableMetaData tableMetadata = databaseMapper.getTableMetadata(tableName);
            Assert.isTrue(tableMetadata != null, "미找到表元데이터");

            genConfig = new GenConfig();
            genConfig.setTableName(tableName);

            // 表注释作값비즈니스이름，去掉表字 例如：사용자表 -> 사용자
            String tableComment = tableMetadata.getTableComment();
            if (StrUtil.isNotBlank(tableComment)) {
                genConfig.setBusinessName(tableComment.replace("表", "").trim());
            }
            //  根据테이블명생성实体类名，支持去除前缀 例如：sys_user -> SysUser
            String removePrefix = genConfig.getRemoveTablePrefix();
            String processedTable = tableName;
            if (StrUtil.isNotBlank(removePrefix) && StrUtil.startWith(tableName, removePrefix)) {
                processedTable = StrUtil.removePrefix(tableName, removePrefix);
            }
            genConfig.setEntityName(StrUtil.toCamelCase(StrUtil.upperFirst(StrUtil.toCamelCase(processedTable))));

            genConfig.setPackageName(YouLaiBootApplication.class.getPackageName());
            genConfig.setModuleName(codegenProperties.getDefaultConfig().getModuleName()); // 默认模块名
            genConfig.setAuthor(codegenProperties.getDefaultConfig().getAuthor());
        }

        // 根据表의列 + 이미经存에의字段생성 설정 得到 组合후의字段생성 설정
        List<GenFieldConfig> genFieldConfigs = new ArrayList<>();

        // 조회表의列
        List<ColumnMetaData> tableColumns = databaseMapper.getTableColumns(tableName);
        if (CollectionUtil.isNotEmpty(tableColumns)) {
            // 조회字段생성 설정
            List<GenFieldConfig> fieldConfigList = genFieldConfigService.list(
                    new LambdaQueryWrapper<GenFieldConfig>()
                            .eq(GenFieldConfig::getConfigId, genConfig.getId())
                            .orderByAsc(GenFieldConfig::getFieldSort)
            );
            Integer maxSort = fieldConfigList.stream()
                    .map(GenFieldConfig::getFieldSort)
                    .filter(Objects::nonNull) // 过滤掉空值
                    .max(Integer::compareTo)
                    .orElse(0);
            for (ColumnMetaData tableColumn : tableColumns) {
                // 根据列名조회字段생성 설정
                String columnName = tableColumn.getColumnName();
                GenFieldConfig fieldConfig = fieldConfigList.stream()
                        .filter(item -> StrUtil.equals(item.getColumnName(), columnName))
                        .findFirst()
                        .orElseGet(() -> createDefaultFieldConfig(tableColumn));
                if (fieldConfig.getFieldSort() == null) {
                    fieldConfig.setFieldSort(++maxSort);
                }
                // 根据列유형设置字段유형
                String fieldType = fieldConfig.getFieldType();
                if (StrUtil.isBlank(fieldType)) {
                    String javaType = JavaTypeEnum.getJavaTypeByColumnType(fieldConfig.getColumnType());
                    fieldConfig.setFieldType(javaType);
                }
                // 如果没有코드 생성설정，则默认展示에목록和폼
                if (!hasGenConfig) {
                    fieldConfig.setIsShowInList(1);
                    fieldConfig.setIsShowInForm(1);
                }
                genFieldConfigs.add(fieldConfig);
            }
        }
        // 对 genFieldConfigs 按照 fieldSort 정렬
        genFieldConfigs = genFieldConfigs.stream().sorted(Comparator.comparing(GenFieldConfig::getFieldSort)).toList();
        GenConfigForm genConfigForm = codegenConverter.toGenConfigForm(genConfig, genFieldConfigs);

        genConfigForm.setFrontendAppName(codegenProperties.getFrontendAppName());
        genConfigForm.setBackendAppName(codegenProperties.getBackendAppName());
        return genConfigForm;
    }


    /**
     * 생성默认필드 설정
     *
     * @param columnMetaData 表字段元데이터
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
     * 저장코드 생성설정
     *
     * @param formData 코드 생성설정폼
     */
    @Override
    public void saveGenConfig(GenConfigForm formData) {
        GenConfig genConfig = codegenConverter.toGenConfig(formData);
        this.saveOrUpdate(genConfig);

        // 如果选择上级메뉴且현재环境不是生产环境，则저장메뉴
        Long parentMenuId = formData.getParentMenuId();
        if (parentMenuId != null && !EnvEnum.PROD.getValue().equals(springProfilesActive)) {
            menuService.addMenuForCodegen(parentMenuId, genConfig);
        }

        List<GenFieldConfig> genFieldConfigs = codegenConverter.toGenFieldConfig(formData.getFieldConfigs());

        if (CollectionUtil.isEmpty(genFieldConfigs)) {
            throw new BusinessException("필드 설정不能값空");
        }
        genFieldConfigs.forEach(genFieldConfig -> {
            genFieldConfig.setConfigId(genConfig.getId());
        });
        genFieldConfigService.saveOrUpdateBatch(genFieldConfigs);
    }

    /**
     * 삭제코드 생성설정
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
