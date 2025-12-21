package com.youlai.boot.system.converter;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.Dict;
import com.youlai.boot.system.model.vo.DictPageVO;
import com.youlai.boot.system.model.form.DictForm;
import org.mapstruct.Mapper;

/**
 * 사전 객체 변환기
 *
 * @author Ray Hao
 * @since 2022/6/8
 */
@Mapper(componentModel = "spring")
public interface DictConverter {

    Page<DictPageVO> toPageVo(Page<Dict> page);

    DictForm toForm(Dict entity);

    Dict toEntity(DictForm entity);
}
