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
 * 사전 항목 인터페이스
 *
 * @author Ray Hao
 * @since 2023/3/4
 */
public interface DictItemService extends IService<DictItem> {

    /**
     * 사전 항목 페이지 목록
     *
     * @param queryParams 조회 파라미터
     * @return 사전 항목 페이지 목록
     */
    Page<DictItemPageVO> getDictItemPage(DictItemPageQuery queryParams);

    /**
     * 조회 사전 항목 목록
     *
     * @param dictCode 사전 코드
     * @return 사전 항목 목록
     */
    List<DictItemOptionVO> getDictItems(String dictCode);

    /**
     * 조회 사전 항목 폼
     *
     * @param itemId 사전 항목 ID
     * @return 사전 항목 폼
     */
    DictItemForm getDictItemForm(Long itemId);

    /**
     * 저장 사전 항목
     *
     * @param formData 사전 항목 폼
     * @return 성공 여부
     */
    boolean saveDictItem(DictItemForm formData);

    /**
     * 업데이트 사전 항목
     *
     * @param formData 사전 항목 폼
     * @return 성공 여부
     */
    boolean updateDictItem(DictItemForm formData);

    /**
     * 삭제 사전 항목
     *
     * @param ids 사전 항목 ID, 여러 개는 쉼표로 구분
     */
    void deleteDictItemByIds(String ids);

}
