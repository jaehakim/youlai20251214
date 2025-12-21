package com.youlai.boot.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.UserRole;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {

    /**
     * 저장 사용자 역할
     *
     * @param userId
     * @param roleIds
     * @return
     */
    void saveUserRoles(Long userId, List<Long> roleIds);

    /**
     * 역할이 바인딩된 사용자 존재 여부 판단
     *
     * @param roleId 역할 ID
     * @return true: 이미 할당됨 false: 미할당
     */
    boolean hasAssignedUsers(Long roleId);
}
