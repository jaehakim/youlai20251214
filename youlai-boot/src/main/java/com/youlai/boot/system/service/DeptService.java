package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.Dept;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.model.form.DeptForm;
import com.youlai.boot.system.model.query.DeptQuery;
import com.youlai.boot.system.model.vo.DeptVO;

import java.util.List;

/**
 * 부서 비즈니스 인터페이스
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
     * 부서 트리형 드롭다운 옵션
     *
     * @return 부서 트리형 드롭다운 옵션
     */
    List<Option<Long>> listDeptOptions();

    /**
     * 추가 부서
     *
     * @param formData 부서 폼
     * @return 부서 ID
     */
    Long saveDept(DeptForm formData);

    /**
     * 수정 부서
     *
     * @param deptId  부서 ID
     * @param formData 부서 폼
     * @return 부서 ID
     */
    Long updateDept(Long deptId, DeptForm formData);

    /**
     * 삭제 부서
     *
     * @param ids 부서 ID, 여러 개는 영문 쉼표(,)로 연결
     * @return 성공 여부
     */
    boolean deleteByIds(String ids);

    /**
     * 조회 부서 상세
     *
     * @param deptId 부서 ID
     * @return 부서 상세
     */
    DeptForm getDeptForm(Long deptId);
}
