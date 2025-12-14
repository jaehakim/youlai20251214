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
 * 부서 비즈니스구현类
 *
 * @author Ray
 * @since 2021/08/22
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {


    private final DeptConverter deptConverter;

    /**
     * 조회부서 목록
     */
    @Override
    public List<DeptVO> getDeptList(DeptQuery queryParams) {
        // 조회参수
        String keywords = queryParams.getKeywords();
        Integer status = queryParams.getStatus();

        // 조회데이터
        List<Dept> deptList = this.list(
                new LambdaQueryWrapper<Dept>()
                        .like(StrUtil.isNotBlank(keywords), Dept::getName, keywords)
                        .eq(status != null, Dept::getStatus, status)
                        .orderByAsc(Dept::getSort)
        );

        if (CollectionUtil.isEmpty(deptList)) {
            return Collections.EMPTY_LIST;
        }

        // 조회所有부서ID
        Set<Long> deptIds = deptList.stream()
                .map(Dept::getId)
                .collect(Collectors.toSet());
        // 조회父节点ID
        Set<Long> parentIds = deptList.stream()
                .map(Dept::getParentId)
                .collect(Collectors.toSet());
        // 조회根节点ID（递归의起点），即父节点ID중不包含에부서ID중의节点，注意这里不能拿顶级부서 O 作값根节点，因값부서筛选의时候 O 会被过滤掉
        List<Long> rootIds = CollectionUtil.subtractToList(parentIds, deptIds);

        // 递归생성부서树形목록
        return rootIds.stream()
                .flatMap(rootId -> recurDeptList(rootId, deptList).stream())
                .toList();
    }

    /**
     * 递归생성부서树形목록
     *
     * @param parentId 父ID
     * @param deptList 부서 목록
     * @return 부서树形목록
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
     * @return 부서下拉List集合
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

        // 递归생성부서树形목록
        return rootIds.stream()
                .flatMap(rootId -> recurDeptTreeOptions(rootId, deptList).stream())
                .toList();
    }

    /**
     * 추가부서
     *
     * @param formData 부서폼
     * @return 부서ID
     */
    @Override
    public Long saveDept(DeptForm formData) {
        // 검증부서이름여부存에
        String code = formData.getCode();
        long count = this.count(new LambdaQueryWrapper<Dept>()
                .eq(Dept::getCode, code)
        );
        Assert.isTrue(count == 0, "부서번호이미存에");

        // form->entity
        Dept entity = deptConverter.toEntity(formData);

        // 생성부서경로(tree_path)，格式：父节点tree_path + , + 父节点ID，용도삭제부서时级联삭제子부서
        String treePath = generateDeptTreePath(formData.getParentId());
        entity.setTreePath(treePath);

        entity.setCreateBy(SecurityUtils.getUserId());
        // 저장부서并返回부서ID
        boolean result = this.save(entity);
        Assert.isTrue(result, "부서저장실패");

        return entity.getId();
    }


    /**
     * 조회부서폼
     *
     * @param deptId 부서ID
     * @return 부서폼객체
     */
    @Override
    public DeptForm getDeptForm(Long deptId) {
        Dept entity = this.getById(deptId);
        return deptConverter.toForm(entity);
    }


    /**
     * 업데이트부서
     *
     * @param deptId   부서ID
     * @param formData 부서폼
     * @return 부서ID
     */
    @Override
    public Long updateDept(Long deptId, DeptForm formData) {
        // 검증부서이름/부서번호여부存에
        String code = formData.getCode();
        long count = this.count(new LambdaQueryWrapper<Dept>()
                .ne(Dept::getId, deptId)
                .eq(Dept::getCode, code)
        );
        Assert.isTrue(count == 0, "부서번호이미存에");


        // form->entity
        Dept entity = deptConverter.toEntity(formData);
        entity.setId(deptId);

        // 생성부서경로(tree_path)，格式：父节点tree_path + , + 父节点ID，용도삭제부서时级联삭제子부서
        String treePath = generateDeptTreePath(formData.getParentId());
        entity.setTreePath(treePath);

        // 저장부서并返回부서ID
        boolean result = this.updateById(entity);
        Assert.isTrue(result, "부서업데이트실패");

        return entity.getId();
    }

    /**
     * 递归생성부서表格层级목록
     *
     * @param parentId 父ID
     * @param deptList 부서 목록
     * @return 부서表格层级목록
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
     * 삭제부서
     *
     * @param ids 부서ID，여러 개는영문쉼표,로 연결字符串
     * @return 여부삭제성공
     */
    @Override
    public boolean deleteByIds(String ids) {
        // 삭제부서及子부서
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
     * 부서경로생성
     *
     * @param parentId 父ID
     * @return 父节点경로는영문쉼표(, )로 구분，eg: 1,2,3
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
