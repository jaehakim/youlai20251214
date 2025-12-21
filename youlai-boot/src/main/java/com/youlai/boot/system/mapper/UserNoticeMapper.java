package com.youlai.boot.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.system.model.entity.UserNotice;
import com.youlai.boot.system.model.query.NoticePageQuery;
import com.youlai.boot.system.model.vo.NoticePageVO;
import com.youlai.boot.system.model.vo.UserNoticePageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 사용자 공지 상태 Mapper 인터페이스
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
@Mapper
public interface UserNoticeMapper extends BaseMapper<UserNotice> {
    /**
     * 내 공지사항 페이지로 가져오기
     * @param page 페이지 객체
     * @param queryParams 쿼리 매개변수
     * @return 공지사항 페이지 목록
     */
    IPage<UserNoticePageVO> getMyNoticePage(Page<NoticePageVO> page, @Param("queryParams") NoticePageQuery queryParams);
}
