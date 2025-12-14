package com.youlai.boot.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 사용자에线상태서비스
 * 负责维护사용자의에线상태和관련통계
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
public class UserOnlineService {

    // 온라인 사용자映射表，key값사용자명，value값사용자에线信息
    private final Map<String, UserOnlineInfo> onlineUsers = new ConcurrentHashMap<>();
    
    private SimpMessagingTemplate messagingTemplate;

    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 사용자上线
     *
     * @param username  사용자명
     * @param sessionId WebSocket세션ID（가选）
     */
    public void userConnected(String username, String sessionId) {
        // 생성세션ID（如果미提용）
        String actualSessionId = sessionId != null ? sessionId : "session-" + System.nanoTime();
        UserOnlineInfo info = new UserOnlineInfo(username, actualSessionId, System.currentTimeMillis());
        onlineUsers.put(username, info);
        log.info("사용자[{}]上线，현재온라인 사용자수：{}", username, onlineUsers.size());
        
        // 공지온라인 사용자상태变更
        notifyOnlineUsersChange();
    }

    /**
     * 사용자下线
     *
     * @param username 사용자명
     */
    public void userDisconnected(String username) {
        onlineUsers.remove(username);
        log.info("사용자[{}]下线，현재온라인 사용자수：{}", username, onlineUsers.size());
        
        // 공지온라인 사용자상태变更
        notifyOnlineUsersChange();
    }

    /**
     * 조회에线사용자 목록
     *
     * @return 온라인 사용자名목록
     */
    public List<UserOnlineDTO> getOnlineUsers() {
        return onlineUsers.values().stream()
                .map(info -> new UserOnlineDTO(info.getUsername(), info.getLoginTime()))
                .collect(Collectors.toList());
    }

    /**
     * 조회온라인 사용자수量
     *
     * @return 온라인 사용자수
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    /**
     * 检查사용자여부에线
     *
     * @param username 사용자명
     * @return 여부에线
     */
    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    /**
     * 공지所有客户端온라인 사용자变更
     */
    private void notifyOnlineUsersChange() {
        if (messagingTemplate == null) {
            log.warn("메시지템플릿尚미初始化，无法발송온라인 사용자수量");
            return;
        }
        
        // 발송简化版데이터（仅수量）
        sendOnlineUserCount();
    }
    
    /**
     * 발송온라인 사용자수量（简化版，不包含사용자 상세）
     */
    private void sendOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("메시지템플릿尚미初始化，无法발송온라인 사용자수量");
            return;
        }
        
        try {
            // 直接발송수量，更轻量
            int count = onlineUsers.size();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("이미발송온라인 사용자수量: {}", count);
        } catch (Exception e) {
            log.error("발송온라인 사용자수量실패", e);
        }
    }

    /**
     * 사용자에线信息
     */
    @Data
    private static class UserOnlineInfo {
        private final String username;
        private final String sessionId;
        private final long loginTime;
    }

    /**
     * 사용자에线DTO（용도返回给前端）
     */
    @Data
    public static class UserOnlineDTO {
        private final String username;
        private final long loginTime;
    }

    /**
     * 온라인 사용자变更事件
     */
    @Data
    private static class OnlineUsersChangeEvent {
        private String type;
        private int count;
        private List<UserOnlineDTO> users;
        private long timestamp;
    }
} 
