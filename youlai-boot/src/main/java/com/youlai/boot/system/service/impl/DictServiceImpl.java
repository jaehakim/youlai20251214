package com.youlai.boot.system.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.converter.DictConverter;
import com.youlai.boot.system.mapper.DictMapper;
import com.youlai.boot.system.model.entity.Dict;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.form.DictForm;
import com.youlai.boot.system.model.query.DictPageQuery;
import com.youlai.boot.system.model.vo.DictPageVO;
import com.youlai.boot.system.service.DictItemService;
import com.youlai.boot.system.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사전 비즈니스 구현 클래스
 *
 * @author haoxr
 * @since 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    private final DictItemService dictItemService;
    private final DictConverter dictConverter;

    /**
     * 사전 페이지목록
     *
     * @param queryParams 페이지조회객체
     */
    @Override
    public Page<DictPageVO> getDictPage(DictPageQuery queryParams) {
        // 조회파라미터수
        int pageNum = queryParams.getPageNum();
        int pageSize = queryParams.getPageSize();

        // 조회데이터
        return this.baseMapper.getDictPage(new Page<>(pageNum, pageSize), queryParams);
    }

    /**
     * 조회사전목록
     *
     * @return 사전목록
     */
    @Override
    public List<Option<String>> getDictList() {
        return this.list(new LambdaQueryWrapper<Dict>().eq(Dict::getStatus, 1))
                .stream()
                .map(item -> new Option<>(item.getDictCode(), item.getName()))
                .toList();
    }


    /**
     * 사전 추가
     *
     * @param dictForm 사전 폼 데이터
     */
    @Override
    public boolean saveDict(DictForm dictForm) {
        // 사전 저장
        Dict entity = dictConverter.toEntity(dictForm);

        // code 유일성 검증
        String dictCode = entity.getDictCode();

        long count = this.count(new LambdaQueryWrapper<Dict>()
                .eq(Dict::getDictCode, dictCode)
        );

        Assert.isTrue(count == 0, "사전 코드가 이미 존재합니다");

        return this.save(entity);
    }


    /**
     * 사전 폼 상세 조회
     *
     * @param id 사전 ID
     */
    @Override
    public DictForm getDictForm(Long id) {
        // 사전 조회
        Dict entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException("사전이 존재하지 않습니다");
        }
        return dictConverter.toForm(entity);
    }

    /**
     * 사전 수정
     *
     * @param id       사전 ID
     * @param dictForm 사전 폼
     */
    @Override
    @Transactional
    public boolean updateDict(Long id, DictForm dictForm) {
        // 사전 조회
        Dict entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException("사전이 존재하지 않습니다");
        }
        // code 유일성 검증
        String dictCode = dictForm.getDictCode();
        if (!entity.getDictCode().equals(dictCode)) {
            long count = this.count(new LambdaQueryWrapper<Dict>()
                    .eq(Dict::getDictCode, dictCode)
            );
            Assert.isTrue(count == 0, "사전 코드가 이미 존재합니다");
        }
        // 사전 업데이트
        Dict dict = dictConverter.toEntity(dictForm);
        dict.setId(id);
        boolean result = this.updateById(dict);
        if (result) {
            // 사전 데이터 업데이트
            List<DictItem> dictItemList = dictItemService.list(
                    new LambdaQueryWrapper<DictItem>()
                            .eq(DictItem::getDictCode, entity.getDictCode())
                            .select(DictItem::getId)
            );
            if (!dictItemList.isEmpty()){
                List<Long> dictItemIds = dictItemList.stream().map(DictItem::getId).toList();
                DictItem dictItem = new DictItem();
                dictItem.setDictCode(dict.getDictCode());
                dictItemService.update(dictItem,
                        new LambdaQueryWrapper<DictItem>()
                                .in(DictItem::getId, dictItemIds)
                );
            }
        }
        return result;
    }

    /**
     * 사전 삭제
     *
     * @param ids 사전 ID, 여러 개는 영문 쉼표(,)로 구분
     */
    @Transactional
    @Override
    public void deleteDictByIds(List<String> ids) {
        // 사전 삭제
        this.removeByIds(ids);

        // 사전 항목 삭제
        List<Dict> list = this.listByIds(ids);
        if (!list.isEmpty()) {
            List<String> dictCodes = list.stream().map(Dict::getDictCode).toList();
            dictItemService.remove(new LambdaQueryWrapper<DictItem>()
                    .in(DictItem::getDictCode, dictCodes)
            );
        }
    }

    /**
     * 사전 ID 목록으로 사전 코드 목록 조회
     *
     * @param ids 사전 ID 목록
     * @return 사전 코드 목록
     */
    @Override
    public List<String> getDictCodesByIds(List<String> ids) {
        List<Dict> dictList = this.listByIds(ids);
        return dictList.stream().map(Dict::getDictCode).toList();
    }

}




