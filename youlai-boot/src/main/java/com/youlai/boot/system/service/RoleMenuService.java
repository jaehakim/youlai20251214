package com.youlai.boot.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.RoleMenu;

import java.util.List;
import java.util.Set;

/**
 * 역할메뉴비즈니스인터페이스
 *
 * @author haoxr
 * @since 2.5.0
 */
public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * 조회역할拥有의메뉴ID集合
     *
     * @param roleId 역할ID
     * @return 메뉴ID集合
     */
    List<Long> listMenuIdsByRoleId(Long roleId);


    /**
     * 새로고침권한캐시(所有역할)
     */
    void refreshRolePermsCache();

    /**
     * 새로고침권한캐시(지정된역할)
     *
     * @param roleCode 역할코드
     */
    void refreshRolePermsCache(String roleCode);

    /**
     * 새로고침권한캐시(수정역할코드时调用)
     *
     * @param oldRoleCode 旧역할코드
     * @param newRoleCode 새역할코드
     */
    void refreshRolePermsCache(String oldRoleCode, String newRoleCode);

    /**
     * 조회역할 권한集合
     *
     * @param roles 역할코드集合
     * @return 권한集合
     */
    Set<String> getRolePermsByRoleCodes(Set<String> roles);
}
