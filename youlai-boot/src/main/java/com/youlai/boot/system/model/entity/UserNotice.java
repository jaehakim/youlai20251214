package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자 알림 공지 엔티티 객체
 *
 * @author Kylin
 * @since 2024-08-28 16:56
 */
@Getter
@Setter
@TableName("sys_user_notice")
public class UserNotice extends BaseEntity {

    /**
     * 기본 키 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 공용 알림 id
     */
    private Long noticeId;
    /**
     * 사용자 id
     */
    private Long userId;
    /**
     * 읽기 상태, 0-미읽음, 1-읽음
     */
    private Integer isRead;
    /**
     * 사용자 읽은 시간
     */
    private LocalDateTime readTime;

    /**
     * 논리 삭제 표시(0-미삭제 1-삭제됨)
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
