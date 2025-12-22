package com.youlai.boot.security.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.common.constant.RedisConstants;
import com.youlai.boot.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import java.util.*;

/**
 * SpringSecurity 권한 검증
 *
 * @author haoxr
 * @since 2022/2/22
 */
@Component("ss")
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 현재 로그인한 사용자가 작업 권한을 가지고 있는지 판단
     *
     * @param requiredPerm 필요한 권한
     * @return 권한 보유 여부
     */
    public boolean hasPerm(String requiredPerm) {

        if (StrUtil.isBlank(requiredPerm)) {
            return false;
        }
        // 최고 관리자는 통과
        if (SecurityUtils.isRoot()) {
            return true;
        }

        // 현재 로그인한 사용자의 역할 코드 집합 조회
        Set<String> roleCodes = SecurityUtils.getRoles();
        if (CollectionUtil.isEmpty(roleCodes)) {
            return false;
        }

        // 현재 로그인한 사용자의 모든 역할의 권한 목록 조회
        Set<String> rolePerms = this.getRolePermsFormCache(roleCodes);
        if (CollectionUtil.isEmpty(rolePerms)) {
            return false;
        }
        // 현재 로그인한 사용자의 모든 역할의 권한 목록에 필요한 권한이 포함되어 있는지 판단
        boolean hasPermission = rolePerms.stream()
                .anyMatch(rolePerm ->
                        // 권한 매칭, 와일드카드(* 등) 지원
                        PatternMatchUtils.simpleMatch(rolePerm, requiredPerm)
                );

        if (!hasPermission) {
            log.error("사용자에게 작업 권한이 없음：{}",requiredPerm);
        }
        return hasPermission;
    }


    /**
     * 캐시에서 역할 권한 목록 조회
     *
     * @param roleCodes 역할 코드 집합
     * @return 역할 권한 목록
     */
    public Set<String> getRolePermsFormCache(Set<String> roleCodes) {
        // 입력값이 비어있는지 확인
        if (CollectionUtil.isEmpty(roleCodes)) {
            return Collections.emptySet();
        }

        Set<String> perms = new HashSet<>();
        // 캐시에서 한 번에 모든 역할의 권한 조회
        Collection<Object> roleCodesAsObjects = new ArrayList<>(roleCodes);
        List<Object> rolePermsList = redisTemplate.opsForHash().multiGet(RedisConstants.System.ROLE_PERMS, roleCodesAsObjects);

        for (Object rolePermsObj : rolePermsList) {
            if (rolePermsObj instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> rolePerms = (Set<String>) rolePermsObj;
                perms.addAll(rolePerms);
            }
        }

        return perms;
    }

}
