package com.youlai.boot.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youlai.boot.system.model.entity.Notice;
import com.youlai.boot.system.model.form.NoticeForm;
import com.youlai.boot.system.model.query.NoticePageQuery;
import com.youlai.boot.system.model.vo.NoticePageVO;
import com.youlai.boot.system.model.vo.UserNoticePageVO;
import com.youlai.boot.system.model.vo.NoticeDetailVO;

/**
 * 공지사항서비스类
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 공지사항페이지 목록
     *
     * @return 공지사항페이지 목록
     */
    IPage<NoticePageVO> getNoticePage(NoticePageQuery queryParams);

    /**
     * 공지사항 폼 데이터 조회
     *
     * @param id 공지사항ID
     * @return 공지사항폼객체
     */
    NoticeForm getNoticeFormData(Long id);

    /**
     * 추가공지사항
     *
     * @param formData 공지사항폼객체
     * @return 여부추가성공
     */
    boolean saveNotice(NoticeForm formData);

    /**
     * 수정공지사항
     *
     * @param id       공지사항ID
     * @param formData 공지사항폼객체
     * @return 여부수정성공
     */
    boolean updateNotice(Long id, NoticeForm formData);

    /**
     * 삭제공지사항
     *
     * @param ids 공지사항ID，여러 개는영문쉼표(,)로 구분
     * @return 여부삭제성공
     */
    boolean deleteNotices(String ids);

    /**
     * 발행공지사항
     *
     * @param id 공지사항ID
     * @return 여부발행성공
     */
    boolean publishNotice(Long id);

    /**
     * 회수공지사항
     *
     * @param id 공지사항ID
     * @return 여부회수성공
     */
    boolean revokeNotice(Long id);

    /**
     * 공지사항 상세 읽기 조회
     *
     * @param id 공지사항ID
     * @return 공지사항상세
     */
    NoticeDetailVO getNoticeDetail(Long id);

    /**
     * 내 공지사항 페이지 목록 조회
     *
     * @param queryParams 조회参수
     * @return 공지사항페이지 목록
     */
    IPage<UserNoticePageVO> getMyNoticePage(NoticePageQuery queryParams);
}
