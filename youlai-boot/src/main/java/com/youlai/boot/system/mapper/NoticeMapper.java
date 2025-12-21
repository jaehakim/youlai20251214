package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.bo.NoticeBO;
import com.youlai.boot.system.model.entity.Notice;
import com.youlai.boot.system.model.query.NoticePageQuery;
import com.youlai.boot.system.model.vo.NoticePageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 공지사항 Mapper 인터페이스
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    /**
     * 공지사항 페이지 데이터 가져오기
     *
     * @param page 페이지 객체
     * @param queryParams 쿼리 매개변수
     * @return 공지사항 페이지 데이터
     */
    Page<NoticeBO> getNoticePage(Page<NoticePageVO> page, NoticePageQuery queryParams);

    /**
     * 읽을 때 공지사항 상세정보 가져오기
     *
     * @param id 공지사항 ID
     * @return 공지사항 상세정보
     */
    NoticeBO getNoticeDetail(@Param("id") Long id);
}
