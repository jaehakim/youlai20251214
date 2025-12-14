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
 * 사용자공지상태서비스类
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
public interface UserNoticeService extends IService<UserNotice> {

    /**
     * 全部标记값읽음
     *
     * @return 여부성공
     */
    boolean readAll();

    /**
     * 페이지조회내공지사항
     * @param page 페이지객체
     * @param queryParams 조회参수
     * @return 내공지사항페이지 목록
     */
    IPage<UserNoticePageVO> getMyNoticePage(Page<NoticePageVO> page, NoticePageQuery queryParams);
}
