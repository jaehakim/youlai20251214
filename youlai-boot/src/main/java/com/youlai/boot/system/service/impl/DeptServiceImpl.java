package com.youlai.boot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.system.converter.DeptConverter;
import com.youlai.boot.system.mapper.DeptMapper;
import com.youlai.boot.system.model.entity.Dept;
import com.youlai.boot.system.model.form.DeptForm;
import com.youlai.boot.system.model.query.DeptQuery;
import com.youlai.boot.system.model.vo.DeptVO;
import com.youlai.boot.common.constant.SystemConstants;
import com.youlai.boot.common.enums.StatusEnum;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 부서 비즈니스 구현 클래스
 *
 * @author Ray
 * @since 2021/08/22
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {


    private final DeptConverter deptConverter;

    /**
     * 부서 목록 조회
     */
    @Override
    public List<DeptVO> getDeptList(DeptQuery queryParams) {
        // 조회 파라미터
        String keywords = queryParams.getKeywords();
        Integer status = queryParams.getStatus();

        // 데이터 조회
        List<Dept> deptList = this.list(
                new LambdaQueryWrapper<Dept>()
                        .like(StrUtil.isNotBlank(keywords), Dept::getName, keywords)
                        .eq(status != null, Dept::getStatus, status)
                        .orderByAsc(Dept::getSort)
        );

        if (CollectionUtil.isEmpty(deptList)) {
            return Collections.EMPTY_LIST;
        }

        // 모든 부서 ID 조회
        Set<Long> deptIds = deptList.stream()
                .map(Dept::getId)
                .collect(Collectors.toSet());
        // 부모 노드 ID 조회
        Set<Long> parentIds = deptList.stream()
                .map(Dept::getParentId)
                .collect(Collectors.toSet());
        // 루트 노드 ID 조회(재귀의 시작점), 즉 부모 노드 ID 중 부서 ID에 포함되지 않는 노드, 여기서 최상위 부서 0을 루트 노드로 사용할 수 없음에 주의, 부서 필터링 시 0이 제외되기 때문
        List<Long> rootIds = CollectionUtil.subtractToList(parentIds, deptIds);

        // 재귀적으로 부서 트리 목록 생성
        return rootIds.stream()
                .flatMap(rootId -> recurDeptList(rootId, deptList).stream())
                .toList();
    }

    /**
     * 재귀적으로 부서 트리 목록 생성
     *
     * @param parentId 부모 ID
     * @param deptList 부서 목록
     * @return 부서 트리 목록
     */
    public List<DeptVO> recurDeptList(Long parentId, List<Dept> deptList) {
        return deptList.stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .map(dept -> {
                    DeptVO deptVO = deptConverter.toVo(dept);
                    List<DeptVO> children = recurDeptList(dept.getId(), deptList);
                    deptVO.setChildren(children);
                    return deptVO;
                }).toList();
    }

    /**
     * 부서 드롭다운 옵션
     *
     * @return 부서 드롭다운 목록 집합
     */
    @Override
    public List<Option<Long>> listDeptOptions() {

        List<Dept> deptList = this.list(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getStatus, StatusEnum.ENABLE.getValue())
                .select(Dept::getId, Dept::getParentId, Dept::getName)
                .orderByAsc(Dept::getSort)
        );
        if (CollectionUtil.isEmpty(deptList)) {
            return Collections.emptyList();
        }

        Set<Long> deptIds = deptList.stream()
                .map(Dept::getId)
                .collect(Collectors.toSet());

        Set<Long> parentIds = deptList.stream()
                .map(Dept::getParentId)
                .collect(Collectors.toSet());

        List<Long> rootIds = CollectionUtil.subtractToList(parentIds, deptIds);

        // 재귀적으로 부서 트리 목록 생성
        return rootIds.stream()
                .flatMap(rootId -> recurDeptTreeOptions(rootId, deptList).stream())
                .toList();
    }

    /**
     * 부서 추가
     *
     * @param formData 부서 폼
     * @return 부서 ID
     */
    @Override
    public Long saveDept(DeptForm formData) {
        // 부서명 존재 여부 검증
        String code = formData.getCode();
        long count = this.count(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getCode, code)
        );
        Assert.isTrue(count == 0, "부서번호가 이미 존재합니다");

        // form->entity
        Dept entity = deptConverter.toEntity(formData);

        // 부서 경로(tree_path) 생성, 형식: 부모 노드 tree_path + , + 부모 노드 ID, 부서 삭제 시 하위 부서 연쇄 삭제용
        String treePath = generateDeptTreePath(formData.getParentId());
        entity.setTreePath(treePath);

        entity.setCreateBy(SecurityUtils.getUserId());
        // 부서 저장 및 부서 ID 반환
        boolean result = this.save(entity);
        Assert.isTrue(result, "부서 저장 실패");

        return entity.getId();
    }


    /**
     * 부서 폼 조회
     *
     * @param deptId 부서 ID
     * @return 부서 폼 객체
     */
    @Override
    public DeptForm getDeptForm(Long deptId) {
        Dept entity = this.getById(deptId);
        return deptConverter.toForm(entity);
    }


    /**
     * 부서 업데이트
     *
     * @param deptId   부서 ID
     * @param formData 부서 폼
     * @return 부서 ID
     */
    @Override
    public Long updateDept(Long deptId, DeptForm formData) {
        // 부서명/부서번호 존재 여부 검증
        String code = formData.getCode();
        long count = this.count(new LambdaQueryWrapper<Dept>()
                .ne(Dept::getId, deptId)
                .eq(Dept::getCode, code)
        );
        Assert.isTrue(count == 0, "부서번호가 이미 존재합니다");


        // form->entity
        Dept entity = deptConverter.toEntity(formData);
        entity.setId(deptId);

        // 부서 경로(tree_path) 생성, 형식: 부모 노드 tree_path + , + 부모 노드 ID, 부서 삭제 시 하위 부서 연쇄 삭제용
        String treePath = generateDeptTreePath(formData.getParentId());
        entity.setTreePath(treePath);

        // 부서 저장 및 부서 ID 반환
        boolean result = this.updateById(entity);
        Assert.isTrue(result, "부서 업데이트 실패");

        return entity.getId();
    }

    /**
     * 재귀적으로 부서 테이블 계층 목록 생성
     *
     * @param parentId 부모 ID
     * @param deptList 부서 목록
     * @return 부서 테이블 계층 목록
     */
    public static List<Option<Long>> recurDeptTreeOptions(long parentId, List<Dept> deptList) {
        return CollectionUtil.emptyIfNull(deptList).stream()
                .filter(dept -> dept.getParentId().equals(parentId))
                .map(dept -> {
                    Option<Long> option = new Option<>(dept.getId(), dept.getName());
                    List<Option<Long>> children = recurDeptTreeOptions(dept.getId(), deptList);
                    if (CollectionUtil.isNotEmpty(children)) {
                        option.setChildren(children);
                    }
                    return option;
                })
                .collect(Collectors.toList());
    }


    /**
     * 부서 삭제
     *
     * @param ids 부서 ID, 여러 개는 영문 쉼표(,)로 연결된 문자열
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteByIds(String ids) {
        // 부서 및 하위 부서 삭제
        if (StrUtil.isNotBlank(ids)) {
            String[] menuIds = ids.split(",");
            for (String deptId : menuIds) {
                String patten = "%," + deptId + ",%";
                this.update(new LambdaUpdateWrapper<Dept>()
                        .eq(Dept::getId, deptId)
                        .or()
                        .apply("CONCAT (',',tree_path,',') LIKE {0}", patten)
                        .set(Dept::getIsDeleted, 1)
                        .set(Dept::getUpdateBy, SecurityUtils.getUserId())
                );
            }
        }
        return true;
    }


    /**
     * 부서 경로 생성
     *
     * @param parentId 부모 ID
     * @return 부모 노드 경로는 영문 쉼표(,)로 구분, 예: 1,2,3
     */
    private String generateDeptTreePath(Long parentId) {
        String treePath = null;
        if (SystemConstants.ROOT_NODE_ID.equals(parentId)) {
            treePath = String.valueOf(parentId);
        } else {
            Dept parent = this.getById(parentId);
            if (parent != null) {
                treePath = parent.getTreePath() + "," + parent.getId();
            }
        }
        return treePath;
    }
}
