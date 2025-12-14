package com.youlai.boot.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.system.converter.NoticeConverter;
import com.youlai.boot.system.enums.NoticePublishStatusEnum;
import com.youlai.boot.system.enums.NoticeTargetEnum;
import com.youlai.boot.system.mapper.NoticeMapper;
import com.youlai.boot.system.model.bo.NoticeBO;
import com.youlai.boot.system.model.dto.NoticeDTO;
import com.youlai.boot.system.model.entity.Notice;
import com.youlai.boot.system.model.entity.UserNotice;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.form.NoticeForm;
import com.youlai.boot.system.model.query.NoticePageQuery;
import com.youlai.boot.system.model.vo.NoticePageVO;
import com.youlai.boot.system.model.vo.UserNoticePageVO;
import com.youlai.boot.system.model.vo.NoticeDetailVO;
import com.youlai.boot.system.service.NoticeService;
import com.youlai.boot.system.service.UserNoticeService;
import com.youlai.boot.system.service.UserOnlineService;
import com.youlai.boot.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 공지사항서비스구현类
 *
 * @author Theo
 * @since 2024-08-27 10:31
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeConverter noticeConverter;
    private final UserNoticeService userNoticeService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserOnlineService userOnlineService;

    /**
     * 조회공지사항페이지 목록
     *
     * @param queryParams 조회参수
     * @return {@link IPage< NoticePageVO >} 공지사항페이지 목록
     */
    @Override
    public IPage<NoticePageVO> getNoticePage(NoticePageQuery queryParams) {
        Page<NoticeBO> noticePage = this.baseMapper.getNoticePage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
        return noticeConverter.toPageVo(noticePage);
    }

    /**
     * 공지사항 폼 데이터 조회
     *
     * @param id 공지사항ID
     * @return {@link NoticeForm} 공지사항폼객체
     */
    @Override
    public NoticeForm getNoticeFormData(Long id) {
        Notice entity = this.getById(id);
        return noticeConverter.toForm(entity);
    }

    /**
     * 추가공지사항
     *
     * @param formData 공지사항폼객체
     * @return {@link Boolean} 여부추가성공
     */
    @Override
    public boolean saveNotice(NoticeForm formData) {

        if (NoticeTargetEnum.SPECIFIED.getValue().equals(formData.getTargetType())) {
            List<String> targetUserIdList = formData.getTargetUserIds();
            if (CollectionUtil.isEmpty(targetUserIdList)) {
                throw new BusinessException("推送지정된사용자不能값空");
            }
        }
        Notice entity = noticeConverter.toEntity(formData);
        entity.setCreateBy(SecurityUtils.getUserId());
        return this.save(entity);
    }

    /**
     * 업데이트공지사항
     *
     * @param id       공지사항ID
     * @param formData 공지사항폼객체
     * @return {@link Boolean} 여부업데이트성공
     */
    @Override
    public boolean updateNotice(Long id, NoticeForm formData) {
        if (NoticeTargetEnum.SPECIFIED.getValue().equals(formData.getTargetType())) {
            List<String> targetUserIdList = formData.getTargetUserIds();
            if (CollectionUtil.isEmpty(targetUserIdList)) {
                throw new BusinessException("推送지정된사용자不能값空");
            }
        }

        Notice entity = noticeConverter.toEntity(formData);
        return this.updateById(entity);
    }

    /**
     * 삭제공지사항
     *
     * @param ids 공지사항ID，여러 개는영문쉼표(,)로 구분
     * @return {@link Boolean} 여부삭제성공
     */
    @Override
    @Transactional
    public boolean deleteNotices(String ids) {
        if (StrUtil.isBlank(ids)) {
            throw new BusinessException("삭제의공지사항데이터값空");
        }

        // 逻辑삭제
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .toList();
        boolean isRemoved = this.removeByIds(idList);
        if (isRemoved) {
            // 삭제공지사항의同时，需要삭제공지사항对应의사용자공지상태
            userNoticeService.remove(new LambdaQueryWrapper<UserNotice>().in(UserNotice::getNoticeId, idList));
        }
        return isRemoved;
    }

    /**
     * 발행공지사항
     *
     * @param id 공지사항ID
     * @return 여부발행성공
     */
    @Override
    @Transactional
    public boolean publishNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException("공지사항不存에");
        }

        if (NoticePublishStatusEnum.PUBLISHED.getValue().equals(notice.getPublishStatus())) {
            throw new BusinessException("공지사항이미발행");
        }

        Integer targetType = notice.getTargetType();
        String targetUserIds = notice.getTargetUserIds();
        if (NoticeTargetEnum.SPECIFIED.getValue().equals(targetType)
                && StrUtil.isBlank(targetUserIds)) {
            throw new BusinessException("推送지정된사용자不能값空");
        }

        notice.setPublishStatus(NoticePublishStatusEnum.PUBLISHED.getValue());
        notice.setPublisherId(SecurityUtils.getUserId());
        notice.setPublishTime(LocalDateTime.now());
        boolean publishResult = this.updateById(notice);

        if (publishResult) {
            // 발행공지사항의同时，삭제该通告之前의사용자공지데이터，因값가能是重새발행
            userNoticeService.remove(
                    new LambdaQueryWrapper<UserNotice>().eq(UserNotice::getNoticeId, id)
            );

            // 添加새의사용자공지데이터
            List<String> targetUserIdList = null;
            if (NoticeTargetEnum.SPECIFIED.getValue().equals(targetType)) {
                targetUserIdList = Arrays.asList(targetUserIds.split(","));
            }

            List<User> targetUserList = userService.list(
                    new LambdaQueryWrapper<User>()
                            // 如果是지정된사용자，则筛选出지정된사용자
                            .in(
                                    NoticeTargetEnum.SPECIFIED.getValue().equals(targetType),
                                    User::getId,
                                    targetUserIdList
                            )
            );

            List<UserNotice> userNoticeList = targetUserList.stream().map(user -> {
                UserNotice userNotice = new UserNotice();
                userNotice.setNoticeId(id);
                userNotice.setUserId(user.getId());
                userNotice.setIsRead(0);
                return userNotice;
            }).toList();

            if (CollectionUtil.isNotEmpty(userNoticeList)) {
                userNoticeService.saveBatch(userNoticeList);
            }

            Set<String> receivers = targetUserList.stream().map(User::getUsername).collect(Collectors.toSet());

            Set<String> allOnlineUsers = userOnlineService.getOnlineUsers().stream()
              .map(UserOnlineService.UserOnlineDTO::getUsername)
              .collect(Collectors.toSet());

            // 找出온라인 사용자의공지接收者
            Set<String> onlineReceivers = new HashSet<>(CollectionUtil.intersection(receivers, allOnlineUsers));

            NoticeDTO noticeDTO = new NoticeDTO();
            noticeDTO.setId(id);
            noticeDTO.setTitle(notice.getTitle());
            noticeDTO.setType(notice.getType());
            noticeDTO.setPublishTime(notice.getPublishTime());

            onlineReceivers.forEach(receiver -> messagingTemplate.convertAndSendToUser(receiver, "/queue/message", noticeDTO));
        }
        return publishResult;
    }

    /**
     * 회수공지사항
     *
     * @param id 공지사항ID
     * @return 여부회수성공
     */
    @Override
    @Transactional
    public boolean revokeNotice(Long id) {
        Notice notice = this.getById(id);
        if (notice == null) {
            throw new BusinessException("공지사항不存에");
        }

        if (!NoticePublishStatusEnum.PUBLISHED.getValue().equals(notice.getPublishStatus())) {
            throw new BusinessException("공지사항미발행또는이미회수");
        }

        notice.setPublishStatus(NoticePublishStatusEnum.REVOKED.getValue());
        notice.setRevokeTime(LocalDateTime.now());
        notice.setUpdateBy(SecurityUtils.getUserId());

        boolean revokeResult = this.updateById(notice);

        if (revokeResult) {
            // 회수공지사항의同时，需要삭제공지사항对应의사용자공지상태
            userNoticeService.remove(new LambdaQueryWrapper<UserNotice>()
                    .eq(UserNotice::getNoticeId, id)
            );
        }
        return revokeResult;
    }

    /**
     *
     * @param id 공지사항ID
     * @return NoticeDetailVO 공지사항상세
     */
    @Override
    public NoticeDetailVO getNoticeDetail(Long id) {
        NoticeBO noticeBO = this.baseMapper.getNoticeDetail(id);
        // 업데이트사용자공지사항의읽기상태
        Long userId = SecurityUtils.getUserId();
        userNoticeService.update(new LambdaUpdateWrapper<UserNotice>()
                .eq(UserNotice::getNoticeId, id)
                .eq(UserNotice::getUserId, userId)
                .eq(UserNotice::getIsRead, 0)
                .set(UserNotice::getIsRead, 1)
        );
        return noticeConverter.toDetailVO(noticeBO);
    }

    /**
     * 조회현재로그인사용자의공지사항목록
     *
     * @param queryParams 조회参수
     * @return 공지사항페이지 목록
     */
    @Override
    public IPage<UserNoticePageVO> getMyNoticePage(NoticePageQuery queryParams) {
        queryParams.setUserId(SecurityUtils.getUserId());
        return userNoticeService.getMyNoticePage(
                new Page<>(queryParams.getPageNum(), queryParams.getPageSize()),
                queryParams
        );
    }

}
