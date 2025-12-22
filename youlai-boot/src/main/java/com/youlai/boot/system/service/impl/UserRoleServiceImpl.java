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
   * 사용자 역할 저장
   *
   * @param userId 사용자 ID
   * @param roleIds 선택한 역할 ID 집합
   * @return
   */
  @Override
  public void saveUserRoles(Long userId, List<Long> roleIds) {
    if (userId == null || CollectionUtil.isEmpty(roleIds)) {
      return ;
    }

    // 기존 역할 조회
    List<Long> userRoleIds = this.list(new LambdaQueryWrapper<UserRole>()
        .select(UserRole::getRoleId)
        .eq(UserRole::getUserId, userId))
      .parallelStream()
      .map(UserRole::getRoleId)
      .toList();

    // Set을 사용하여 비교 효율 향상
    Set<Long> oldRoles = new HashSet<>(userRoleIds);
    Set<Long> newRoles = new HashSet<>(roleIds);

    // 변경 집합 계산
    Set<Long> addedRoles = new HashSet<>(newRoles);
    addedRoles.removeAll(oldRoles);

    Set<Long> removedRoles = new HashSet<>(oldRoles);
    removedRoles.removeAll(newRoles);

    boolean rolesChanged = !addedRoles.isEmpty() || !removedRoles.isEmpty();

    // 추가된 역할 일괄 저장
    if (!addedRoles.isEmpty()) {
      this.saveBatch(addedRoles.stream()
        .map(roleId -> new UserRole(userId, roleId))
        .collect(Collectors.toList()));
    }

    // 폐기된 역할 삭제
    if (!removedRoles.isEmpty()) {
      this.remove(new LambdaQueryWrapper<UserRole>()
        .eq(UserRole::getUserId, userId)
        .in(UserRole::getRoleId, removedRoles));
    }

    // 권한 변경 시 사용자의 로그인 상태 무효화
    if (rolesChanged) {
      tokenManager.invalidateUserSessions(userId);
    }
  }

  /**
   * 역할에 바인딩된 사용자 존재 여부 판단
   *
   * @param roleId 역할 ID
   * @return true: 이미 할당됨, false: 미할당
   */
  @Override
  public boolean hasAssignedUsers(Long roleId) {
    int count = this.baseMapper.countUsersForRole(roleId);
    return count > 0;
  }
}
