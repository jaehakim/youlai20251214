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
 * 사용자 온라인 상태 서비스
 * 사용자의 온라인 상태 및 관련 통계 유지 담당
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
public class UserOnlineService {

    // 온라인 사용자 매핑 테이블, key는 사용자명, value는 사용자 온라인 정보
    private final Map<String, UserOnlineInfo> onlineUsers = new ConcurrentHashMap<>();

    private SimpMessagingTemplate messagingTemplate;

    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 사용자 접속
     *
     * @param username  사용자명
     * @param sessionId WebSocket 세션 ID(선택사항)
     */
    public void userConnected(String username, String sessionId) {
        // 세션 ID 생성(미제공 시)
        String actualSessionId = sessionId != null ? sessionId : "session-" + System.nanoTime();
        UserOnlineInfo info = new UserOnlineInfo(username, actualSessionId, System.currentTimeMillis());
        onlineUsers.put(username, info);
        log.info("사용자 [{}] 접속, 현재 온라인 사용자 수: {}", username, onlineUsers.size());

        // 온라인 사용자 상태 변경 알림
        notifyOnlineUsersChange();
    }

    /**
     * 사용자 접속 해제
     *
     * @param username 사용자명
     */
    public void userDisconnected(String username) {
        onlineUsers.remove(username);
        log.info("사용자 [{}] 접속 해제, 현재 온라인 사용자 수: {}", username, onlineUsers.size());

        // 온라인 사용자 상태 변경 알림
        notifyOnlineUsersChange();
    }

    /**
     * 조회 온라인 사용자 목록
     *
     * @return 온라인 사용자명 목록
     */
    public List<UserOnlineDTO> getOnlineUsers() {
        return onlineUsers.values().stream()
                .map(info -> new UserOnlineDTO(info.getUsername(), info.getLoginTime()))
                .collect(Collectors.toList());
    }

    /**
     * 조회 온라인 사용자 수량
     *
     * @return 온라인 사용자 수
     */
    public int getOnlineUserCount() {
        return onlineUsers.size();
    }

    /**
     * 사용자 온라인 여부 확인
     *
     * @param username 사용자명
     * @return 온라인 여부
     */
    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    /**
     * 모든 클라이언트에 온라인 사용자 변경 알림
     */
    private void notifyOnlineUsersChange() {
        if (messagingTemplate == null) {
            log.warn("메시지 템플릿이 아직 초기화되지 않아 온라인 사용자 수량을 전송할 수 없습니다");
            return;
        }

        // 간소화된 데이터 전송(수량만)
        sendOnlineUserCount();
    }

    /**
     * 온라인 사용자 수량 전송(간소화 버전, 사용자 상세 미포함)
     */
    private void sendOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("메시지 템플릿이 아직 초기화되지 않아 온라인 사용자 수량을 전송할 수 없습니다");
            return;
        }

        try {
            // 수량 직접 전송, 더 가벼움
            int count = onlineUsers.size();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("온라인 사용자 수량 전송 완료: {}", count);
        } catch (Exception e) {
            log.error("온라인 사용자 수량 전송 실패", e);
        }
    }

    /**
     * 사용자 온라인 정보
     */
    @Data
    private static class UserOnlineInfo {
        private final String username;
        private final String sessionId;
        private final long loginTime;
    }

    /**
     * 사용자 온라인 DTO(프론트엔드 반환용)
     */
    @Data
    public static class UserOnlineDTO {
        private final String username;
        private final long loginTime;
    }

    /**
     * 온라인 사용자 변경 이벤트
     */
    @Data
    private static class OnlineUsersChangeEvent {
        private String type;
        private int count;
        private List<UserOnlineDTO> users;
        private long timestamp;
    }
}
