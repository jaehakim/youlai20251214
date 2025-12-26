package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.system.converter.DictItemConverter;
import com.youlai.boot.system.mapper.DictItemMapper;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.form.DictItemForm;
import com.youlai.boot.system.model.query.DictItemPageQuery;
import com.youlai.boot.system.model.vo.DictItemOptionVO;
import com.youlai.boot.system.model.vo.DictItemPageVO;
import com.youlai.boot.system.service.DictItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 사전 항목 구현 클래스
 *
 * @author Ray.Hao
 * @since 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {

    private final DictItemConverter dictItemConverter;

    /**
     * 조회사전 항목페이지 목록
     *
     * @param queryParams 조회파라미터수
     * @return 사전 항목페이지 목록
     */
    @Override
    public Page<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams) {
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();
        Page<DictItemPageVO> page = new Page<>(pageNum, pageSize);

        return this.baseMapper.getDictItemPage(page, queryParams);
    }


    /**
     * 사전 항목 목록 조회
     *
     * @param dictCode 사전 코드
     */
    @Override
    public List<DictItemOptionVO> getDictItems(String dictCode) {
        return this.list(
                        new LambdaQueryWrapper<DictItem>()
                                .eq(DictItem::getDictCode, dictCode)
                                .eq(DictItem::getStatus, 1)
                                .orderByAsc(DictItem::getSort)
                ).stream()
                .map(item -> {
                    DictItemOptionVO dictItemOptionVO = new DictItemOptionVO();
                    dictItemOptionVO.setLabel(item.getLabel());
                    dictItemOptionVO.setValue(item.getValue());
                    dictItemOptionVO.setTagType(item.getTagType());
                    return dictItemOptionVO;
                }).toList();
    }



    /**
     * 사전 항목 폼 조회
     *
     * @param itemId 사전 항목 ID
     * @return 사전 항목 폼
     */
    @Override
    public DictItemForm getDictItemForm( Long itemId) {
        DictItem entity = this.getById(itemId);
        return dictItemConverter.toForm(entity);
    }

    /**
     * 사전 항목 저장
     *
     * @param formData 사전 항목 폼
     * @return 성공 여부
     */
    @Override
    public boolean saveDictItem(DictItemForm formData) {
        DictItem entity = dictItemConverter.toEntity(formData);
        return this.save(entity);
    }

    /**
     * 사전 항목 업데이트
     *
     * @param formData 사전 항목 폼
     * @return 성공 여부
     */
    @Override
    public boolean updateDictItem(DictItemForm formData) {
        DictItem entity = dictItemConverter.toEntity(formData);
        return this.updateById(entity);
    }

    /**
     * 사전 항목 삭제
     *
     * @param ids 사전 항목 ID 집합
     */
    @Override
    public void deleteDictItemByIds(String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
          .map(Long::parseLong)
          .toList();
        this.removeByIds(idList);
    }

}




