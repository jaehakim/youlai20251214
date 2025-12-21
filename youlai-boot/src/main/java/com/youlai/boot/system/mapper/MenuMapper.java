package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * 메뉴 접근 계층
 *
 * @author Ray
 * @since 2022/1/24
 */

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 메뉴 라우트 목록 가져오기
     *
     * @param roleCodes 역할 코드 집합
     */
    List<Menu> getMenusByRoleCodes(Set<String> roleCodes);

}
