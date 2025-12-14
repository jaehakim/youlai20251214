package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.form.DictItemForm;
import com.youlai.boot.system.model.query.DictItemPageQuery;
import com.youlai.boot.system.model.vo.DictItemOptionVO;
import com.youlai.boot.system.model.vo.DictItemPageVO;

import java.util.List;

/**
 * 사전 항목인터페이스
 *
 * @author Ray Hao
 * @since 2023/3/4
 */
public interface DictItemService extends IService<DictItem> {

    /**
     * 사전 항목페이지 목록
     *
     * @param queryParams 조회参수
     * @return 사전 항목페이지 목록
     */
    Page<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams);

    /**
     * 조회사전 항목목록
     *
     * @param dictCode 사전 코드
     * @return 사전 항목목록
     */
    List<DictItemOptionVO> getDictItems(String dictCode);

    /**
     * 조회사전 항목폼
     *
     * @param itemId 사전 항목ID
     * @return 사전 항목폼
     */
    DictItemForm getDictItemForm(Long itemId);

    /**
     * 저장사전 항목
     *
     * @param formData 사전 항목폼
     * @return 여부성공
     */
    boolean saveDictItem(DictItemForm formData);

    /**
     * 업데이트사전 항목
     *
     * @param formData 사전 항목폼
     * @return 여부성공
     */
    boolean updateDictItem(DictItemForm formData);

    /**
     * 삭제사전 항목
     *
     * @param ids 사전 항목ID,여러 개쉼표分隔
     */
    void deleteDictItemByIds(String ids);

}
