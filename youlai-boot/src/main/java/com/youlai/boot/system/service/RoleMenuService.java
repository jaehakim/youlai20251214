package com.youlai.boot.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.RoleMenu;

import java.util.List;
import java.util.Set;

/**
 * 역할 메뉴 비즈니스 인터페이스
 *
 * @author haoxr
 * @since 2.5.0
 */
public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * 조회 역할이 보유한 메뉴 ID 집합
     *
     * @param roleId 역할 ID
     * @return 메뉴 ID 집합
     */
    List<Long> listMenuIdsByRoleId(Long roleId);


    /**
     * 새로고침 권한 캐시(모든 역할)
     */
    void refreshRolePermsCache();

    /**
     * 새로고침 권한 캐시(지정된 역할)
     *
     * @param roleCode 역할 코드
     */
    void refreshRolePermsCache(String roleCode);

    /**
     * 새로고침 권한 캐시(역할 코드 수정 시 호출)
     *
     * @param oldRoleCode 이전 역할 코드
     * @param newRoleCode 새 역할 코드
     */
    void refreshRolePermsCache(String oldRoleCode, String newRoleCode);

    /**
     * 조회 역할 권한 집합
     *
     * @param roles 역할 코드 집합
     * @return 권한 집합
     */
    Set<String> getRolePermsByRoleCodes(Set<String> roles);
}
