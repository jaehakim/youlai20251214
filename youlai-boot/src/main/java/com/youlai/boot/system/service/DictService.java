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
 * 사전 비즈니스 인터페이스
 *
 * @author haoxr
 * @since 2022/10/12
 */
public interface DictService extends IService<Dict> {

    /**
     * 조회 사전 페이지 목록
     *
     * @param queryParams 페이지 조회 객체
     * @return 사전 페이지 목록
     */
    Page<DictPageVO> getDictPage(DictPageQuery queryParams);

    /**
     * 조회 사전 목록
     *
     * @return 사전 목록
     */
    List<Option<String>> getDictList();

    /**
     * 사전 폼 데이터 조회
     *
     * @param id 사전 ID
     * @return 사전 폼
     */
    DictForm getDictForm(Long id);

    /**
     * 추가 사전
     *
     * @param dictForm 사전 폼
     * @return 성공 여부
     */
    boolean saveDict(DictForm dictForm);

    /**
     * 수정 사전
     *
     * @param id     사전 ID
     * @param dictForm 사전 폼
     * @return 성공 여부
     */
    boolean updateDict(Long id, DictForm dictForm);

    /**
     * 삭제 사전
     *
     * @param ids 사전 ID 집합
     */
    void deleteDictByIds(List<String> ids);

    /**
     * 사전 ID 목록으로 사전 코드 목록 조회
     *
     * @param ids 사전 ID 목록
     * @return 사전 코드 목록
     */
    List<String> getDictCodesByIds(List<String> ids);
}
