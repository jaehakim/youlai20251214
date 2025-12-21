package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.system.model.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 사용자 역할 접근 계층
 *
 * @author haoxr
 * @since 2022/1/15
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 역할에 바인딩된 사용자 수 가져오기
     *
     * @param roleId 역할 ID
     */
    int countUsersForRole(Long roleId);
}
