package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.UserNotice;
import com.youlai.boot.system.model.query.NoticePageQuery;
import com.youlai.boot.system.model.vo.UserNoticePageVO;
import com.youlai.boot.system.model.vo.NoticePageVO;

import java.util.List;

/**
 * 사용자 공지 상태 서비스
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
public interface UserNoticeService extends IService<UserNotice> {

    /**
     * 전체 읽음으로 표시
     *
     * @return 성공 여부
     */
    boolean readAll();

    /**
     * 페이지 조회 내 공지사항
     * @param page 페이지 객체
     * @param queryParams 조회 파라미터
     * @return 내 공지사항 페이지 목록
     */
    IPage<UserNoticePageVO> getMyNoticePage(Page<NoticePageVO> page, NoticePageQuery queryParams);
}
