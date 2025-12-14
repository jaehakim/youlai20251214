package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.system.model.entity.Dict;
import com.youlai.boot.system.model.form.DictForm;
import com.youlai.boot.system.model.query.DictPageQuery;
import com.youlai.boot.system.model.vo.DictItemOptionVO;
import com.youlai.boot.system.model.vo.DictPageVO;

import java.util.List;

/**
 * 사전비즈니스인터페이스
 *
 * @author haoxr
 * @since 2022/10/12
 */
public interface DictService extends IService<Dict> {

    /**
     * 조회사전 페이지목록
     *
     * @param queryParams 페이지조회객체
     * @return 사전 페이지목록
     */
    Page<DictPageVO> getDictPage(DictPageQuery queryParams);

    /**
     * 조회사전목록
     *
     * @return 사전목록
     */
    List<Option<String>> getDictList();

    /**
     * 사전 폼 데이터 조회
     *
     * @param id 사전ID
     * @return 사전폼
     */
    DictForm getDictForm(Long id);

    /**
     * 추가사전
     *
     * @param dictForm 사전폼
     * @return 여부성공
     */
    boolean saveDict(DictForm dictForm);

    /**
     * 수정사전
     *
     * @param id     사전ID
     * @param dictForm 사전폼
     * @return 여부성공
     */
    boolean updateDict(Long id, DictForm dictForm);

    /**
     * 삭제사전
     *
     * @param ids 사전ID集合
     */
    void deleteDictByIds(List<String> ids);

    /**
     * 根据사전ID목록조회사전 코드목록
     *
     * @param ids 사전ID목록
     * @return 사전 코드목록
     */
    List<String> getDictCodesByIds(List<String> ids);
}
