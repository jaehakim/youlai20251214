package com.youlai.boot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.Config;
import com.youlai.boot.system.model.vo.ConfigVO;
import com.youlai.boot.system.model.form.ConfigForm;
import org.mapstruct.Mapper;

/**
 * 시스템 설정 객체 변환기
 *
 * @author Theo
 * @since 2024-7-29 11:42:49
 */
@Mapper(componentModel = "spring")
public interface ConfigConverter {

    Page<ConfigVO> toPageVo(Page<Config> page);

    Config toEntity(ConfigForm configForm);

    ConfigForm toForm(Config entity);
}
