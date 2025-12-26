package com.youlai.boot.platform.websocket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.platform.websocket.service.WebSocketService;
import com.youlai.boot.system.model.dto.DictEventDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 웹소켓 서비스 구현 클래스
 *
 * 핵심 기능:
 * - 사용자 온라인 상태 관리 (다중 기기 로그인 지원)
 * - 메시지 푸시 (브로드캐스트, 점대점)
 * - 사전 변경 알림
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    // ==================== 온라인 사용자 관리 ====================

    /**
     * 사용자 온라인 세션 매핑 테이블
     * Key: 사용자명
     * Value: 해당 사용자의 모든 세션 ID 집합 (다중 기기 로그인 지원)
     */
    private final Map<String, Set<String>> userSessionsMap = new ConcurrentHashMap<>();

    /**
     * 세션 상세 매핑 테이블
     * Key: 세션 ID
     * Value: 세션 상세 정보
     */
    private final Map<String, SessionInfo> sessionDetailsMap = new ConcurrentHashMap<>();

    // ==================== 의존성 주입 ====================
    
    private SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebSocketServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 지연 주입 SimpMessagingTemplate, 순환 의존성 방지
     */
    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        log.info("✓ 웹소켓 메시지 템플릿 초기화 완료");
    }

    // ==================== 사용자 온라인 상태 관리 ====================

    /**
     * 사용자 연결 이벤트 처리
     *
     * @param username  사용자명
     * @param sessionId 웹소켓 세션 ID
     */
    @Override
    public void userConnected(String username, String sessionId) {
        if (username == null || username.isEmpty()) {
            log.warn("사용자 연결 실패: 사용자명이 비어있음");
            return;
        }

        if (sessionId == null || sessionId.isEmpty()) {
            log.warn("사용자[{}] 연결 실패: 세션 ID가 비어있음", username);
            return;
        }

        // 사용자의 세션 집합에 세션 추가 (다중 기기 로그인 지원)
        userSessionsMap.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet())
                       .add(sessionId);

        // 세션 상세 정보 저장
        SessionInfo sessionInfo = new SessionInfo(username, sessionId, System.currentTimeMillis());
        sessionDetailsMap.put(sessionId, sessionInfo);

        int sessionCount = userSessionsMap.get(username).size();
        int totalOnlineUsers = userSessionsMap.size();

        log.info("✓ 사용자[{}] 세션[{}] 온라인 (해당 사용자 총 {} 개 세션, 시스템 전체 온라인 사용자 수: {})",
                username, sessionId, sessionCount, totalOnlineUsers);

        // 온라인 사용자 수 변경 브로드캐스트
        broadcastOnlineUserCount();
    }

    /**
     * 사용자 연결 해제 이벤트 처리
     *
     * @param username 사용자명
     */
    @Override
    public void userDisconnected(String username) {
        if (username == null || username.isEmpty()) {
            return;
        }

        // 해당 사용자의 모든 세션 조회
        Set<String> sessions = userSessionsMap.get(username);
        if (sessions == null || sessions.isEmpty()) {
            log.warn("사용자[{}] 오프라인: 세션 기록을 찾을 수 없음", username);
            return;
        }

        // 모든 세션 상세 정보 제거 (일반적으로 한 번에 하나의 세션만 끊지만, 여기서는 전체 정리)
        sessions.forEach(sessionDetailsMap::remove);

        // 사용자의 세션 기록 제거
        userSessionsMap.remove(username);

        int totalOnlineUsers = userSessionsMap.size();
        log.info("✓ 사용자[{}] 오프라인 (시스템 전체 온라인 사용자 수: {})", username, totalOnlineUsers);

        // 온라인 사용자 수 변경 브로드캐스트
        broadcastOnlineUserCount();
    }

    /**
     * 지정된 세션 제거 (단일 기기 오프라인)
     *
     * @param sessionId 세션 ID
     */
    public void removeSession(String sessionId) {
        SessionInfo sessionInfo = sessionDetailsMap.remove(sessionId);
        if (sessionInfo == null) {
            return;
        }

        String username = sessionInfo.getUsername();
        Set<String> sessions = userSessionsMap.get(username);

        if (sessions != null) {
            sessions.remove(sessionId);

            // 해당 사용자가 다른 세션이 없으면, 사용자 기록 제거
            if (sessions.isEmpty()) {
                userSessionsMap.remove(username);
                log.info("✓ 사용자[{}] 마지막 세션[{}] 오프라인", username, sessionId);
            } else {
                log.info("✓ 사용자[{}] 세션[{}] 오프라인 (남은 세션: {}개)",
                        username, sessionId, sessions.size());
            }

            // 온라인 사용자 수 변경 브로드캐스트
            broadcastOnlineUserCount();
        }
    }

    /**
     * 온라인 사용자 목록 조회
     *
     * @return 온라인 사용자 정보 목록
     */
    public List<OnlineUserDTO> getOnlineUsers() {
        return userSessionsMap.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    Set<String> sessions = entry.getValue();

                    // 해당 사용자의 가장 빠른 로그인 시간 조회
                    long earliestLoginTime = sessions.stream()
                            .map(sessionDetailsMap::get)
                            .filter(info -> info != null)
                            .mapToLong(SessionInfo::getConnectTime)
                            .min()
                            .orElse(System.currentTimeMillis());

                    return new OnlineUserDTO(username, sessions.size(), earliestLoginTime);
                })
                .collect(Collectors.toList());
    }

    /**
     * 온라인 사용자 수 조회
     *
     * @return 온라인 사용자 수 (세션 수가 아님)
     */
    public int getOnlineUserCount() {
        return userSessionsMap.size();
    }

    /**
     * 조회에스레드세션총수
     *
     * @return 모든에스레드세션의총수
     */
    public int getTotalSessionCount() {
        return sessionDetailsMap.size();
    }

    /**
     * 사용자 온라인 여부 확인
     *
     * @param username 사용자명
     * @return 온라인 여부
     */
    public boolean isUserOnline(String username) {
        Set<String> sessions = userSessionsMap.get(username);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * 조회지정된사용자의세션수수량
     *
     * @param username 사용자명
     * @return 세션수수량
     */
    public int getUserSessionCount(String username) {
        Set<String> sessions = userSessionsMap.get(username);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * 수동 트리거온라인 사용자수수량브로드캐스트
     * 
     * 용외부서비스（정시 작업처럼）호출
     */
    public void notifyOnlineUsersChange() {
        log.info("수동 트리거온라인 사용자수수량공지，현재온라인 사용자수：{}", getOnlineUserCount());
        broadcastOnlineUserCount();
    }

    /**
     * 브로드캐스트온라인 사용자수수량 변경（내부메서드）
     */
    private void broadcastOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("메시지템플릿아직미초기화，불가능발송온라인 사용자수수량");
            return;
        }

        try {
            int count = getOnlineUserCount();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("✓ 이미브로드캐스트온라인 사용자수수량: {}", count);
        } catch (Exception e) {
            log.error("브로드캐스트온라인 사용자수수량실패", e);
        }
    }

    // ==================== 메시지 푸시 기능 ====================

    /**
     * 모든 클라이언트에 사전 업데이트 이벤트 브로드캐스트
     *
     * @param dictCode 사전 코드
     */
    @Override
    public void broadcastDictChange(String dictCode) {
        if (dictCode == null || dictCode.isEmpty()) {
            log.warn("사전 코드가 비어있음, 브로드캐스트 건너뜀");
            return;
        }

        DictEventDTO event = new DictEventDTO(dictCode);
        sendDictChangeEvent(event);
    }

    /**
     * 발송사전변경이벤트
     *
     * @param event 사전이벤트
     */
    private void sendDictChangeEvent(DictEventDTO event) {
        if (messagingTemplate == null) {
            log.warn("메시지템플릿아직미초기화，불가능발송사전업데이트공지");
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(event);
            messagingTemplate.convertAndSend("/topic/dict", message);
            log.info("✓ 이미브로드캐스트사전변경공지: dictCode={}", event.getDictCode());
        } catch (JsonProcessingException e) {
            log.error("사전이벤트직렬화실패: dictCode={}", event.getDictCode(), e);
        } catch (Exception e) {
            log.error("발송사전변경공지실패: dictCode={}", event.getDictCode(), e);
        }
    }

    /**
     * 특정으로사용자발송공지메시지
     *
     * @param username 대상사용자명
     * @param message  메시지내용
     */
    @Override
    public void sendNotification(String username, Object message) {
        if (username == null || username.isEmpty()) {
            log.warn("사용자명값비어있음，불가능발송공지");
            return;
        }

        if (message == null) {
            log.warn("메시지내용값비어있음，불가능발송에게사용자[{}]", username);
            return;
        }

        if (messagingTemplate == null) {
            log.warn("메시지템플릿아직미초기화，불가능발송사용자메시지");
            return;
        }

        try {
            String messageJson = objectMapper.writeValueAsString(message);
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", messageJson);
            log.info("✓ 이미에사용자[{}]발송공지", username);
        } catch (JsonProcessingException e) {
            log.error("메시지직렬화실패: username={}", username, e);
        } catch (Exception e) {
            log.error("에사용자[{}]발송공지실패", username, e);
        }
    }

    /**
     * 브로드캐스트시스템메시지모두에게사용자
     *
     * @param message 메시지내용
     */
    public void broadcastSystemMessage(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("메시지내용값비어있음，불가능브로드캐스트");
            return;
        }

        if (messagingTemplate == null) {
            log.warn("메시지템플릿아직미초기화，불가능발송브로드캐스트메시지");
            return;
        }

        try {
            SystemMessage systemMessage = new SystemMessage(
                    "시스템공지",
                    message,
                    System.currentTimeMillis()
            );
            String messageJson = objectMapper.writeValueAsString(systemMessage);
            messagingTemplate.convertAndSend("/topic/public", messageJson);
            log.info("✓ 이미브로드캐스트시스템메시지: {}", message);
        } catch (JsonProcessingException e) {
            log.error("시스템메시지직렬화실패", e);
        } catch (Exception e) {
            log.error("브로드캐스트시스템메시지실패", e);
        }
    }

    // ==================== 내부 데이터 클래스 ====================

    /**
     * 세션 정보
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class SessionInfo {
        /** 사용자명 */
        private String username;
        /** 세션 ID */
        private String sessionId;
        /** 연결 타임스탬프 */
        private long connectTime;
    }

    /**
     * 온라인 사용자 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OnlineUserDTO {
        /** 사용자명 */
        private String username;
        /** 세션수수량 */
        private int sessionCount;
        /** 최초 로그인 시간 */
        private long loginTime;
    }

    /**
     * 시스템 메시지
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SystemMessage {
        /** 발송자 */
        private String sender;
        /** 메시지 내용 */
        private String content;
        /** 타임스탬프 */
        private long timestamp;
    }
}
