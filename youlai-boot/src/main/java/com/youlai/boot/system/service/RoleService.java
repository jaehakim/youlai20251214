package com.youlai.boot.system.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.Role;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.model.form.RoleForm;
import com.youlai.boot.system.model.query.RolePageQuery;
import com.youlai.boot.system.model.vo.RolePageVO;

import java.util.List;
import java.util.Set;

/**
 * 역할비즈니스인터페이스层
 *
 * @author haoxr
 * @since 2022/6/3
 */
public interface RoleService extends IService<Role> {

    /**
     * 역할 페이지목록
     *
     * @param queryParams
     * @return
     */
    Page<RolePageVO> getRolePage(RolePageQuery queryParams);


    /**
     * 역할 드롭다운 목록
     *
     * @return
     */
    List<Option<Long>> listRoleOptions();

    /**
     *
     * @param roleForm
     * @return
     */
    boolean saveRole(RoleForm roleForm);

    /**
     * 역할 폼 데이터 조회
     *
     * @param roleId 역할ID
     * @return  {@link RoleForm} – 역할폼데이터
     */
    RoleForm getRoleForm(Long roleId);

    /**
     * 수정역할상태
     *
     * @param roleId 역할ID
     * @param status 역할상태(1:활성화；0:비활성화)
     * @return {@link Boolean}
     */
    boolean updateRoleStatus(Long roleId, Integer status);

    /**
     * 일괄 삭제역할
     *
     * @param ids 역할ID，여러 개사용영문쉼표(,)로 구분
     */
    void deleteRoles(String ids);

    /**
     * 역할의 메뉴 ID 집합 조회
     *
     * @param roleId 역할ID
     * @return 메뉴ID集合(包括버튼 권한ID)
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 수정역할의资源권한
     *
     * @param roleId 역할ID
     * @param menuIds 메뉴ID集合
     */
    void assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 조회最大范围의데이터권한
     *
     * @param roles
     * @return
     */
    Integer getMaximumDataScope(Set<String> roles);


}
