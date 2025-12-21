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
 * 역할 비즈니스 인터페이스
 *
 * @author haoxr
 * @since 2022/6/3
 */
public interface RoleService extends IService<Role> {

    /**
     * 역할 페이지 목록
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
     * @param roleId 역할 ID
     * @return  {@link RoleForm} – 역할 폼 데이터
     */
    RoleForm getRoleForm(Long roleId);

    /**
     * 수정 역할 상태
     *
     * @param roleId 역할 ID
     * @param status 역할 상태(1:활성화; 0:비활성화)
     * @return {@link Boolean}
     */
    boolean updateRoleStatus(Long roleId, Integer status);

    /**
     * 일괄 삭제 역할
     *
     * @param ids 역할 ID, 여러 개는 영문 쉼표(,)로 구분
     */
    void deleteRoles(String ids);

    /**
     * 역할의 메뉴 ID 집합 조회
     *
     * @param roleId 역할 ID
     * @return 메뉴 ID 집합(버튼 권한 ID 포함)
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 수정 역할의 리소스 권한
     *
     * @param roleId 역할 ID
     * @param menuIds 메뉴 ID 집합
     */
    void assignMenusToRole(Long roleId, List<Long> menuIds);

    /**
     * 조회 최대 범위의 데이터 권한
     *
     * @param roles
     * @return
     */
    Integer getMaximumDataScope(Set<String> roles);


}
