package com.youlai.boot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.security.token.TokenManager;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.system.mapper.UserRoleMapper;
import com.youlai.boot.system.model.entity.UserRole;
import com.youlai.boot.system.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {


  private final TokenManager tokenManager;

  /**
   * 저장사용자역할
   *
   * @param userId 사용자ID
   * @param roleIds 选择의역할ID集合
   * @return
   */
  @Override
  public void saveUserRoles(Long userId, List<Long> roleIds) {
    if (userId == null || CollectionUtil.isEmpty(roleIds)) {
      return ;
    }

    // 조회现有역할
    List<Long> userRoleIds = this.list(new LambdaQueryWrapper<UserRole>()
        .select(UserRole::getRoleId)
        .eq(UserRole::getUserId, userId))
      .parallelStream()
      .map(UserRole::getRoleId)
      .toList();

    // 사용Set提升对比效率
    Set<Long> oldRoles = new HashSet<>(userRoleIds);
    Set<Long> newRoles = new HashSet<>(roleIds);

    // 计算变更集
    Set<Long> addedRoles = new HashSet<>(newRoles);
    addedRoles.removeAll(oldRoles);

    Set<Long> removedRoles = new HashSet<>(oldRoles);
    removedRoles.removeAll(newRoles);

    boolean rolesChanged = !addedRoles.isEmpty() || !removedRoles.isEmpty();

    // 批量저장추가역할
    if (!addedRoles.isEmpty()) {
      this.saveBatch(addedRoles.stream()
        .map(roleId -> new UserRole(userId, roleId))
        .collect(Collectors.toList()));
    }

    // 삭제废弃역할
    if (!removedRoles.isEmpty()) {
      this.remove(new LambdaQueryWrapper<UserRole>()
        .eq(UserRole::getUserId, userId)
        .in(UserRole::getRoleId, removedRoles));
    }

    // 当권한变更时제거被사용자 수정의로그인态
    if (rolesChanged) {
      tokenManager.invalidateUserSessions(userId);
    }
  }

  /**
   * 判断역할여부存에바인딩의사용자
   *
   * @param roleId 역할ID
   * @return true：이미分配 false：미分配
   */
  @Override
  public boolean hasAssignedUsers(Long roleId) {
    int count = this.baseMapper.countUsersForRole(roleId);
    return count > 0;
  }
}
