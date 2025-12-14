package com.youlai.boot.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.system.mapper.UserNoticeMapper;
import com.youlai.boot.system.model.entity.UserNotice;
import com.youlai.boot.system.model.query.NoticePageQuery;
import com.youlai.boot.system.model.vo.NoticePageVO;
import com.youlai.boot.system.model.vo.UserNoticePageVO;
import com.youlai.boot.system.service.UserNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 사용자공지상태서비스구현类
 *
 * @author youlaitech
 * @since 2024-08-28 16:56
 */
@Service
@RequiredArgsConstructor
public class UserNoticeServiceImpl extends ServiceImpl<UserNoticeMapper, UserNotice> implements UserNoticeService {

    private final UserNoticeMapper userNoticeMapper;

    /**
     * 全部标记값읽음
     *
     * @return 여부성공
     */
    @Override
    public boolean readAll() {
        Long userId = SecurityUtils.getUserId();
        return this.update(new LambdaUpdateWrapper<UserNotice>()
                .eq(UserNotice::getUserId, userId)
                .eq(UserNotice::getIsRead, 0)
                .set(UserNotice::getIsRead, 1)
        );
    }

    /**
     * 내공지사항페이지 목록
     *
     * @param page        페이지객체
     * @param queryParams 조회参수
     * @return 공지사항페이지 목록
     */
    @Override
    public IPage<UserNoticePageVO> getMyNoticePage(Page<NoticePageVO> page, NoticePageQuery queryParams) {
        return this.getBaseMapper().getMyNoticePage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
    }


}
