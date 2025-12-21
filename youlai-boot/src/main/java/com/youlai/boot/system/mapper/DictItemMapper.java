package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.DictItem;
import com.youlai.boot.system.model.query.DictItemPageQuery;
import com.youlai.boot.system.model.vo.DictItemPageVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 사전 항목 매핑 계층
 *
 * @author Ray Hao
 * @since 2.9.0
 */
@Mapper
public interface DictItemMapper extends BaseMapper<DictItem> {

    /**
     * 사전 항목 페이지 목록
     */
    Page<DictItemPageVO> getDictItemPage(Page<DictItemPageVO> page, DictItemPageQuery queryParams);
}




