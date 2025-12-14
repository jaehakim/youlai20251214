package com.youlai.boot.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.UserRole;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {

    /**
     * 저장사용자역할
     *
     * @param userId
     * @param roleIds
     * @return
     */
    void saveUserRoles(Long userId, List<Long> roleIds);

    /**
     * 判断역할여부存에바인딩의사용자
     *
     * @param roleId 역할ID
     * @return true：이미分配 false：미分配
     */
    boolean hasAssignedUsers(Long roleId);
}
