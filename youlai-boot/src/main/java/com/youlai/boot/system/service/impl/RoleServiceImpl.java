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
 * 역할비즈니스구현类
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
     * 역할 페이지목록
     *
     * @param queryParams 역할조회参수
     * @return {@link Page< RolePageVO >} – 역할 페이지목록
     */
    @Override
    public Page<RolePageVO> getRolePage(RolePageQuery queryParams) {
        // 조회参수
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        String keywords = queryParams.getKeywords();

        // 조회데이터
        Page<Role> rolePage = this.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<Role>()
                        .and(StrUtil.isNotBlank(keywords),
                                wrapper ->
                                        wrapper.like(Role::getName, keywords)
                                                .or()
                                                .like(Role::getCode, keywords)
                        )
                        .ne(!SecurityUtils.isRoot(), Role::getCode, SystemConstants.ROOT_ROLE_CODE) // 非超级관리员不표시超级관리员역할
                        .orderByAsc(Role::getSort).orderByDesc(Role::getCreateTime).orderByDesc(Role::getUpdateTime)
        );

        // 实体转换
        return roleConverter.toPageVo(rolePage);
    }

    /**
     * 역할 드롭다운 목록
     *
     * @return {@link List<Option>} – 역할 드롭다운 목록
     */
    @Override
    public List<Option<Long>> listRoleOptions() {
        // 조회데이터
        List<Role> roleList = this.list(new LambdaQueryWrapper<Role>()
                .ne(!SecurityUtils.isRoot(), Role::getCode, SystemConstants.ROOT_ROLE_CODE)
                .select(Role::getId, Role::getName)
                .orderByAsc(Role::getSort)
        );

        // 实体转换
        return roleConverter.toOptions(roleList);
    }

    /**
     * 저장역할
     *
     * @param roleForm 역할폼데이터
     * @return {@link Boolean}
     */
    @Override
    public boolean saveRole(RoleForm roleForm) {

        Long roleId = roleForm.getId();

        // 编辑역할时，判断역할여부存에
        Role oldRole = null;
        if (roleId != null) {
            oldRole = this.getById(roleId);
            Assert.isTrue(oldRole != null, "역할不存에");
        }

        String roleCode = roleForm.getCode();
        long count = this.count(new LambdaQueryWrapper<Role>()
                .ne(roleId != null, Role::getId, roleId)
                .and(wrapper ->
                        wrapper.eq(Role::getCode, roleCode).or().eq(Role::getName, roleForm.getName())
                ));
        Assert.isTrue(count == 0, "역할이름또는역할코드이미存에，请수정후重试！");

        // 实体转换
        Role role = roleConverter.toEntity(roleForm);

        boolean result = this.saveOrUpdate(role);
        if (result) {
            // 判断역할코드또는상태여부수정，수정则새로고침권한캐시
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
     * @param roleId 역할ID
     * @return {@link RoleForm} – 역할폼데이터
     */
    @Override
    public RoleForm getRoleForm(Long roleId) {
        Role entity = this.getById(roleId);
        return roleConverter.toForm(entity);
    }

    /**
     * 수정역할상태
     *
     * @param roleId 역할ID
     * @param status 역할상태(1:활성화；0:비활성화)
     * @return {@link Boolean}
     */
    @Override
    public boolean updateRoleStatus(Long roleId, Integer status) {

        Role role = this.getById(roleId);
        if (role == null) {
            throw new BusinessException("역할不存에");
        }

        role.setStatus(status);
        boolean result = this.updateById(role);
        if (result) {
            // 새로고침역할의권한캐시
            roleMenuService.refreshRolePermsCache(role.getCode());
        }
        return result;
    }

    /**
     * 일괄 삭제역할
     *
     * @param ids 역할ID，여러 개사용영문쉼표(,)로 구분
     */
    @Override
    public void deleteRoles(String ids) {
        Assert.isTrue(StrUtil.isNotBlank(ids), "삭제의역할ID不能값空");
        List<Long> roleIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();

        for (Long roleId : roleIds) {
            Role role = this.getById(roleId);
            Assert.isTrue(role != null, "역할不存에");

            // 判断역할여부被사용자关联
            boolean isRoleAssigned = userRoleService.hasAssignedUsers(roleId);
            Assert.isTrue(!isRoleAssigned, "역할【{}】이미分配사용자，请先解除关联후삭제", role.getName());

            boolean deleteResult = this.removeById(roleId);
            if (deleteResult) {
                // 삭제성공，새로고침권한캐시
                roleMenuService.refreshRolePermsCache(role.getCode());
            }
        }
    }

    /**
     * 역할의 메뉴 ID 집합 조회
     *
     * @param roleId 역할ID
     * @return 메뉴ID集合(包括버튼 권한ID)
     */
    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuService.listMenuIdsByRoleId(roleId);
    }

    /**
     * 수정역할의资源권한
     *
     * @param roleId  역할ID
     * @param menuIds 메뉴ID集合
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new RuntimeException("역할不存에");
        }
        // 삭제역할메뉴
        roleMenuService.remove(
                new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleId)
        );
        // 추가역할메뉴
        if (CollectionUtil.isNotEmpty(menuIds)) {
            List<RoleMenu> roleMenus = menuIds
                    .stream()
                    .map(menuId -> new RoleMenu(roleId, menuId))
                    .toList();
            roleMenuService.saveBatch(roleMenus);
        }

        // 새로고침역할의권한캐시
        roleMenuService.refreshRolePermsCache(role.getCode());
    }

    /**
     * 조회最大范围의데이터권한
     *
     * @param roles 역할코드集合
     * @return {@link Integer} – 데이터권한范围
     */
    @Override
    public Integer getMaximumDataScope(Set<String> roles) {
        Integer dataScope = this.baseMapper.getMaximumDataScope(roles);
        return dataScope;
    }

}
