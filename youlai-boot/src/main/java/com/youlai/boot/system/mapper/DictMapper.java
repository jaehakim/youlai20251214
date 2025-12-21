package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.Dict;
import com.youlai.boot.system.model.query.DictPageQuery;
import com.youlai.boot.system.model.vo.DictPageVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 사전 접근 계층
 *
 * @author Ray Hao
 * @since 2.9.0
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 사전 페이지 목록
     *
     * @param page 페이지 매개변수
     * @param queryParams 쿼리 매개변수
     * @return 사전 페이지 목록
     */
    Page<DictPageVO> getDictPage(Page<DictPageVO> page, DictPageQuery queryParams);

}




