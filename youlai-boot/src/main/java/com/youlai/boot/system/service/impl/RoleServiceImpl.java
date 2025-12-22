package com.youlai.boot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.system.converter.RoleConverter;
import com.youlai.boot.system.mapper.RoleMapper;
import com.youlai.boot.system.model.entity.Role;
import com.youlai.boot.system.model.entity.RoleMenu;
import com.youlai.boot.system.model.form.RoleForm;
import com.youlai.boot.system.model.query.RolePageQuery;
import com.youlai.boot.system.model.vo.RolePageVO;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.system.service.RoleMenuService;
import com.youlai.boot.system.service.RoleService;
import com.youlai.boot.system.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 역할 비즈니스 구현 클래스
 *
 * @author haoxr
 * @since 2022/6/3
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMenuService roleMenuService;
    private final UserRoleService userRoleService;
    private final RoleConverter roleConverter;

    /**
     * 역할 페이지 목록
     *
     * @param queryParams 역할 조회 파라미터
     * @return {@link Page< RolePageVO >} – 역할 페이지 목록
     */
    @Override
    public Page<RolePageVO> getRolePage(RolePageQuery queryParams) {
        // 조회 파라미터
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();

        // 데이터 조회
        Page<Role> rolePage = this.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Role>()
                        .and(StrUtil.isNotBlank(keywords),
                                wrapper ->
                                        wrapper.like(Role::getName, keywords)
                                                .or()
                                                .like(Role::getCode, keywords)
                        )
                        .ne(!SecurityUtils.isRoot(), Role::getCode, SystemConstants.ROOT_ROLE_CODE) // 슈퍼 관리자가 아닌 경우 슈퍼 관리자 역할 표시 안함
                        .orderByAsc(Role::getSort).orderByDesc(Role::getCreateTime).orderByDesc(Role::getUpdateTime)
        );

        // 엔티티 변환
        return roleConverter.toPageVo(rolePage);
    }

    /**
     * 역할 드롭다운 목록
     *
     * @return {@link List<Option>} – 역할 드롭다운 목록
     */
    @Override
    public List<Option<Long>> listRoleOptions() {
        // 데이터 조회
        List<Role> roleList = this.list(new LambdaQueryWrapper<Role>()
                .ne(!SecurityUtils.isRoot(), Role::getCode, SystemConstants.ROOT_ROLE_CODE)
                .select(Role::getId, Role::getName)
                .orderByAsc(Role::getSort)
        );

        // 엔티티 변환
        return roleConverter.toOptions(roleList);
    }

    /**
     * 역할 저장
     *
     * @param roleForm 역할 폼 데이터
     * @return {@link Boolean}
     */
    @Override
    public boolean saveRole(RoleForm roleForm) {

        Long roleId = roleForm.getId();

        // 역할 편집 시 역할 존재 여부 확인
        Role oldRole = null;
        if (roleId != null) {
            oldRole = this.getById(roleId);
            Assert.isTrue(oldRole != null, "역할이 존재하지 않습니다");
        }

        String roleCode = roleForm.getCode();
        long count = this.count(new LambdaQueryWrapper<Role>()
                .ne(roleId != null, Role::getId, roleId)
                .and(wrapper ->
                        wrapper.eq(Role::getCode, roleCode).or().eq(Role::getName, roleForm.getName())
                ));
        Assert.isTrue(count == 0, "역할명 또는 역할 코드가 이미 존재합니다. 수정 후 다시 시도해주세요!");

        // 엔티티 변환
        Role role = roleConverter.toEntity(roleForm);

        boolean result = this.saveOrUpdate(role);
        if (result) {
            // 역할 코드 또는 상태 수정 여부 확인, 수정 시 권한 캐시 새로고침
            if (oldRole != null
                    && (
                    !StrUtil.equals(oldRole.getCode(), roleCode) ||
                            !ObjectUtil.equals(oldRole.getStatus(), roleForm.getStatus())
            )) {
                roleMenuService.refreshRolePermsCache(oldRole.getCode(), roleCode);
            }
        }
        return result;
    }

    /**
     * 역할 폼 데이터 조회
     *
     * @param roleId 역할 ID
     * @return {@link RoleForm} – 역할 폼 데이터
     */
    @Override
    public RoleForm getRoleForm(Long roleId) {
        Role entity = this.getById(roleId);
        return roleConverter.toForm(entity);
    }

    /**
     * 역할 상태 수정
     *
     * @param roleId 역할 ID
     * @param status 역할 상태(1:활성화; 0:비활성화)
     * @return {@link Boolean}
     */
    @Override
    public boolean updateRoleStatus(Long roleId, Integer status) {

        Role role = this.getById(roleId);
        if (role == null) {
            throw new BusinessException("역할이 존재하지 않습니다");
        }

        role.setStatus(status);
        boolean result = this.updateById(role);
        if (result) {
            // 역할 권한 캐시 새로고침
            roleMenuService.refreshRolePermsCache(role.getCode());
        }
        return result;
    }

    /**
     * 역할 일괄 삭제
     *
     * @param ids 역할 ID, 여러 개는 영문 쉼표(,)로 구분
     */
    @Override
    public void deleteRoles(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "삭제할 역할 ID가 비어있습니다");
        List<Long> roleIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();

        for (Long roleId : roleIds) {
            Role role = this.getById(roleId);
            Assert.isTrue(role != null, "역할이 존재하지 않습니다");

            // 역할이 사용자에게 할당되었는지 확인
            boolean isRoleAssigned = userRoleService.hasAssignedUsers(roleId);
            Assert.isTrue(!isRoleAssigned, "역할【{}】이 이미 사용자에게 할당되어 있습니다. 먼저 연결을 해제한 후 삭제해주세요", role.getName());

            boolean deleteResult = this.removeById(roleId);
            if (deleteResult) {
                // 삭제 성공 시 권한 캐시 새로고침
                roleMenuService.refreshRolePermsCache(role.getCode());
            }
        }
    }

    /**
     * 역할의 메뉴 ID 집합 조회
     *
     * @param roleId 역할 ID
     * @return 메뉴 ID 집합(버튼 권한 ID 포함)
     */
    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuService.listMenuIdsByRoleId(roleId);
    }

    /**
     * 역할의 리소스 권한 수정
     *
     * @param roleId  역할 ID
     * @param menuIds 메뉴 ID 집합
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new RuntimeException("역할이 존재하지 않습니다");
        }
        // 역할 메뉴 삭제
        roleMenuService.remove(
                new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleId)
        );
        // 역할 메뉴 추가
        if (CollectionUtil.isNotEmpty(menuIds)) {
            List<RoleMenu> roleMenus = menuIds
                    .stream()
                    .map(menuId -> new RoleMenu(roleId, menuId))
                    .toList();
            roleMenuService.saveBatch(roleMenus);
        }

        // 역할 권한 캐시 새로고침
        roleMenuService.refreshRolePermsCache(role.getCode());
    }

    /**
     * 최대 범위의 데이터 권한 조회
     *
     * @param roles 역할 코드 집합
     * @return {@link Integer} – 데이터 권한 범위
     */
    @Override
    public Integer getMaximumDataScope(Set<String> roles) {
        Integer dataScope = this.baseMapper.getMaximumDataScope(roles);
        return dataScope;
    }

}
