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
 * 공지사항 서비스
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 공지사항 페이지 목록
     *
     * @return 공지사항 페이지 목록
     */
    IPage<NoticePageVO> getNoticePage(NoticePageQuery queryParams);

    /**
     * 공지사항 폼 데이터 조회
     *
     * @param id 공지사항 ID
     * @return 공지사항 폼 객체
     */
    NoticeForm getNoticeFormData(Long id);

    /**
     * 추가 공지사항
     *
     * @param formData 공지사항 폼 객체
     * @return 추가 성공 여부
     */
    boolean saveNotice(NoticeForm formData);

    /**
     * 수정 공지사항
     *
     * @param id       공지사항 ID
     * @param formData 공지사항 폼 객체
     * @return 수정 성공 여부
     */
    boolean updateNotice(Long id, NoticeForm formData);

    /**
     * 삭제 공지사항
     *
     * @param ids 공지사항 ID, 여러 개는 영문 쉼표(,)로 구분
     * @return 삭제 성공 여부
     */
    boolean deleteNotices(String ids);

    /**
     * 발행 공지사항
     *
     * @param id 공지사항 ID
     * @return 발행 성공 여부
     */
    boolean publishNotice(Long id);

    /**
     * 회수 공지사항
     *
     * @param id 공지사항 ID
     * @return 회수 성공 여부
     */
    boolean revokeNotice(Long id);

    /**
     * 공지사항 상세 읽기 조회
     *
     * @param id 공지사항 ID
     * @return 공지사항 상세
     */
    NoticeDetailVO getNoticeDetail(Long id);

    /**
     * 내 공지사항 페이지 목록 조회
     *
     * @param queryParams 조회 파라미터
     * @return 공지사항 페이지 목록
     */
    IPage<UserNoticePageVO> getMyNoticePage(NoticePageQuery queryParams);
}
