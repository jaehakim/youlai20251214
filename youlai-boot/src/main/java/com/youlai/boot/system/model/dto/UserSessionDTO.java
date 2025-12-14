package com.youlai.boot.system.model.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 사용자 세션 DTO
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Data
public class UserSessionDTO {

    /**
     * 사용자명
     */
    private String username;

    /**
     * 사용자 세션 ID 집합
     */
    private Set<String> sessionIds;

    /**
     * 마지막 활동 시간
     */
    private long lastActiveTime;

    public UserSessionDTO(String username) {
        this.username = username;
        this.sessionIds = new HashSet<>();
        this.lastActiveTime = System.currentTimeMillis();
    }
} 
