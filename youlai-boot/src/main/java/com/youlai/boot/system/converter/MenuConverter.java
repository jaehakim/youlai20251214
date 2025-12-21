package com.youlai.boot.system.converter;

import com.youlai.boot.system.model.entity.Menu;
import com.youlai.boot.system.model.vo.MenuVO;
import com.youlai.boot.system.model.form.MenuForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 메뉴 객체 변환기
 *
 * @author Ray Hao
 * @since 2024/5/26
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {

    MenuVO toVo(Menu entity);

    @Mapping(target = "params", ignore = true)
    MenuForm toForm(Menu entity);

    @Mapping(target = "params", ignore = true)
    Menu toEntity(MenuForm menuForm);

}