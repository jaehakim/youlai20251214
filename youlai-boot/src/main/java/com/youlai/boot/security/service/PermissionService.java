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
 * SpringSecurity 권한검증
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
     * 判断현재로그인사용자여부拥有操作권한
     *
     * @param requiredPerm 所需권한
     * @return 여부有권한
     */
    public boolean hasPerm(String requiredPerm) {

        if (StrUtil.isBlank(requiredPerm)) {
            return false;
        }
        // 超级관리员放行
        if (SecurityUtils.isRoot()) {
            return true;
        }

        // 조회현재로그인사용자의역할코드集合
        Set<String> roleCodes = SecurityUtils.getRoles();
        if (CollectionUtil.isEmpty(roleCodes)) {
            return false;
        }

        // 조회현재로그인사용자의所有역할의권한목록
        Set<String> rolePerms = this.getRolePermsFormCache(roleCodes);
        if (CollectionUtil.isEmpty(rolePerms)) {
            return false;
        }
        // 判断현재로그인사용자의所有역할의권한목록중여부包含所需권한
        boolean hasPermission = rolePerms.stream()
                .anyMatch(rolePerm ->
                        // 匹配권한，支持通配符(* 等)
                        PatternMatchUtils.simpleMatch(rolePerm, requiredPerm)
                );

        if (!hasPermission) {
            log.error("사용자无操作권한：{}",requiredPerm);
        }
        return hasPermission;
    }


    /**
     * 从캐시중조회역할 권한목록
     *
     * @param roleCodes 역할코드集合
     * @return 역할 권한목록
     */
    public Set<String> getRolePermsFormCache(Set<String> roleCodes) {
        // 检查输入여부값空
        if (CollectionUtil.isEmpty(roleCodes)) {
            return Collections.emptySet();
        }

        Set<String> perms = new HashSet<>();
        // 从캐시중원次性조회所有역할의권한
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
