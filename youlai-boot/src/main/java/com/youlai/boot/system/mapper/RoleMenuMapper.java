package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.bo.RolePermsBO;
import com.youlai.boot.system.model.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * 역할 메뉴 접근 계층
 *
 * @author haoxr
 * @since 2022/6/4
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 역할이 가진 메뉴 ID 집합 가져오기
     *
     * @param roleId 역할 ID
     * @return 메뉴 ID 집합
     */
    List<Long> listMenuIdsByRoleId(Long roleId);

    /**
     * 권한과 권한을 가진 역할 목록 가져오기
     */
    List<RolePermsBO> getRolePermsList(String roleCode);


    /**
     * 역할 권한 집합 가져오기
     *
     * @param roles
     * @return
     */
    Set<String> listRolePerms(Set<String> roles);
}
