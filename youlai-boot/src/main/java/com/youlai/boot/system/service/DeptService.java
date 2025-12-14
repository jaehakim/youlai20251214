package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.Dept;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.model.form.DeptForm;
import com.youlai.boot.system.model.query.DeptQuery;
import com.youlai.boot.system.model.vo.DeptVO;

import java.util.List;

/**
 * 부서비즈니스인터페이스
 *
 * @author haoxr
 * @since 2021/8/22
 */
public interface DeptService extends IService<Dept> {
    /**
     * 부서 목록
     *
     * @return 부서 목록
     */
    List<DeptVO> getDeptList(DeptQuery queryParams);

    /**
     * 부서树形드롭다운 옵션
     *
     * @return 부서树形드롭다운 옵션
     */
    List<Option<Long>> listDeptOptions();

    /**
     * 추가부서
     *
     * @param formData 부서폼
     * @return 부서ID
     */
    Long saveDept(DeptForm formData);

    /**
     * 수정부서
     *
     * @param deptId  부서ID
     * @param formData 부서폼
     * @return 부서ID
     */
    Long updateDept(Long deptId, DeptForm formData);

    /**
     * 삭제부서
     *
     * @param ids 부서ID，여러 개는영문쉼표,로 연결字符串
     * @return 여부성공
     */
    boolean deleteByIds(String ids);

    /**
     * 조회부서상세
     *
     * @param deptId 부서ID
     * @return 부서상세
     */
    DeptForm getDeptForm(Long deptId);
}
