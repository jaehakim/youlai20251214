package com.youlai.boot.platform.codegen.converter;

import com.youlai.boot.platform.codegen.model.entity.GenConfig;
import com.youlai.boot.platform.codegen.model.entity.GenFieldConfig;
import com.youlai.boot.platform.codegen.model.form.GenConfigForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 코드 생성 설정 변환기
 *
 * @author Ray
 * @since 2.10.0
 */
@Mapper(componentModel = "spring")
public interface CodegenConverter {

    @Mapping(source = "genConfig.tableName", target = "tableName")
    @Mapping(source = "genConfig.businessName", target = "businessName")
    @Mapping(source = "genConfig.moduleName", target = "moduleName")
    @Mapping(source = "genConfig.packageName", target = "packageName")
    @Mapping(source = "genConfig.entityName", target = "entityName")
    @Mapping(source = "genConfig.author", target = "author")
    @Mapping(source = "genConfig.pageType", target = "pageType")
    @Mapping(source = "genConfig.removeTablePrefix", target = "removeTablePrefix")
    @Mapping(source = "fieldConfigs", target = "fieldConfigs")
    GenConfigForm toGenConfigForm(GenConfig genConfig, List<GenFieldConfig> fieldConfigs);

    List<GenConfigForm.FieldConfig> toGenFieldConfigForm(List<GenFieldConfig> fieldConfigs);

    GenConfigForm.FieldConfig toGenFieldConfigForm(GenFieldConfig genFieldConfig);

    GenConfig toGenConfig(GenConfigForm formData);

    List<GenFieldConfig> toGenFieldConfig(List<GenConfigForm.FieldConfig> fieldConfigs);

    GenFieldConfig toGenFieldConfig(GenConfigForm.FieldConfig fieldConfig);

}