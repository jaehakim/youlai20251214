package com.youlai.boot.platform.websocket.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youlai.boot.system.model.dto.DictEventDTO;
import com.youlai.boot.platform.websocket.service.WebSocketService;
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
 * WebSocket 서비스구현类
 * 
 * 核心功能：
 * - 사용자에线상태관리（支持多设备로그인）
 * - 메시지 푸시（广播、点对点）
 * - 사전变更공지
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    // ==================== 온라인 사용자관리 ====================
    
    /**
     * 사용자에线세션映射表
     * Key: 사용자명
     * Value: 该사용자의所有세션 ID 集合（支持多设备로그인）
     */
    private final Map<String, Set<String>> userSessionsMap = new ConcurrentHashMap<>();

    /**
     * 세션상세映射表
     * Key: 세션 ID
     * Value: 세션详细信息
     */
    private final Map<String, SessionInfo> sessionDetailsMap = new ConcurrentHashMap<>();

    // ==================== 依赖注入 ====================
    
    private SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebSocketServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 延迟注入 SimpMessagingTemplate，避免循环依赖
     */
    @Autowired(required = false)
    public void setMessagingTemplate(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        log.info("✓ WebSocket 메시지템플릿이미初始化");
    }

    // ==================== 사용자에线상태관리 ====================

    /**
     * 处理사용자连接事件
     *
     * @param username  사용자명
     * @param sessionId WebSocket 세션 ID
     */
    @Override
    public void userConnected(String username, String sessionId) {
        if (username == null || username.isEmpty()) {
            log.warn("사용자连接실패：사용자명값空");
            return;
        }

        if (sessionId == null || sessionId.isEmpty()) {
            log.warn("사용자[{}]连接실패：세션 ID 값空", username);
            return;
        }

        // 添加세션到사용자의세션集合중（支持多设备로그인）
        userSessionsMap.computeIfAbsent(username, k -> ConcurrentHashMap.newKeySet())
                       .add(sessionId);

        // 저장세션상세
        SessionInfo sessionInfo = new SessionInfo(username, sessionId, System.currentTimeMillis());
        sessionDetailsMap.put(sessionId, sessionInfo);

        int sessionCount = userSessionsMap.get(username).size();
        int totalOnlineUsers = userSessionsMap.size();

        log.info("✓ 사용자[{}]세션[{}]上线（该사용자共 {} 个세션，시스템总온라인 사용자수：{}）",
                username, sessionId, sessionCount, totalOnlineUsers);

        // 广播온라인 사용자수变更
        broadcastOnlineUserCount();
    }

    /**
     * 处理사용자断开连接事件
     *
     * @param username 사용자명
     */
    @Override
    public void userDisconnected(String username) {
        if (username == null || username.isEmpty()) {
            return;
        }

        // 조회该사용자의所有세션
        Set<String> sessions = userSessionsMap.get(username);
        if (sessions == null || sessions.isEmpty()) {
            log.warn("사용자[{}]下线：미找到세션기록", username);
            return;
        }

        // 移除所有세션상세（通常원次만断开원个세션，但这里做全量清理）
        sessions.forEach(sessionDetailsMap::remove);

        // 移除사용자의세션기록
        userSessionsMap.remove(username);

        int totalOnlineUsers = userSessionsMap.size();
        log.info("✓ 사용자[{}]下线（시스템总온라인 사용자수：{}）", username, totalOnlineUsers);

        // 广播온라인 사용자수变更
        broadcastOnlineUserCount();
    }

    /**
     * 移除지정된세션（单个设备下线）
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

            // 如果该사용자没有其他세션，移除사용자기록
            if (sessions.isEmpty()) {
                userSessionsMap.remove(username);
                log.info("✓ 사용자[{}]最후원个세션[{}]下线", username, sessionId);
            } else {
                log.info("✓ 사용자[{}]세션[{}]下线（还剩 {} 个세션）", 
                        username, sessionId, sessions.size());
            }

            // 广播온라인 사용자수变更
            broadcastOnlineUserCount();
        }
    }

    /**
     * 조회에线사용자 목록
     *
     * @return 에线사용자 정보목록
     */
    public List<OnlineUserDTO> getOnlineUsers() {
        return userSessionsMap.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    Set<String> sessions = entry.getValue();

                    // 조회该사용자最早의로그인시간
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
     * 조회온라인 사용자수量
     *
     * @return 온라인 사용자수（不是세션수）
     */
    public int getOnlineUserCount() {
        return userSessionsMap.size();
    }

    /**
     * 조회에线세션总수
     *
     * @return 所有에线세션의总수
     */
    public int getTotalSessionCount() {
        return sessionDetailsMap.size();
    }

    /**
     * 检查사용자여부에线
     *
     * @param username 사용자명
     * @return 여부에线
     */
    public boolean isUserOnline(String username) {
        Set<String> sessions = userSessionsMap.get(username);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * 조회지정된사용자의세션수量
     *
     * @param username 사용자명
     * @return 세션수量
     */
    public int getUserSessionCount(String username) {
        Set<String> sessions = userSessionsMap.get(username);
        return sessions != null ? sessions.size() : 0;
    }

    /**
     * 手动触发온라인 사용자수量广播
     * 
     * 용外部서비스（如定时任务）调用
     */
    public void notifyOnlineUsersChange() {
        log.info("手动触发온라인 사용자수量공지，현재온라인 사용자수：{}", getOnlineUserCount());
        broadcastOnlineUserCount();
    }

    /**
     * 广播온라인 사용자수量变更（内部方法）
     */
    private void broadcastOnlineUserCount() {
        if (messagingTemplate == null) {
            log.warn("메시지템플릿尚미初始化，无法발송온라인 사용자수量");
            return;
        }

        try {
            int count = getOnlineUserCount();
            messagingTemplate.convertAndSend("/topic/online-count", count);
            log.debug("✓ 이미广播온라인 사용자수量: {}", count);
        } catch (Exception e) {
            log.error("广播온라인 사용자수量실패", e);
        }
    }

    // ==================== 메시지 푸시功能 ====================

    /**
     * 向所有客户端广播사전업데이트事件
     *
     * @param dictCode 사전 코드
     */
    @Override
    public void broadcastDictChange(String dictCode) {
        if (dictCode == null || dictCode.isEmpty()) {
            log.warn("사전 코드값空，跳过广播");
            return;
        }

        DictEventDTO event = new DictEventDTO(dictCode);
        sendDictChangeEvent(event);
    }

    /**
     * 발송사전变更事件
     *
     * @param event 사전事件
     */
    private void sendDictChangeEvent(DictEventDTO event) {
        if (messagingTemplate == null) {
            log.warn("메시지템플릿尚미初始化，无法발송사전업데이트공지");
            return;
        }

        try {
            String message = objectMapper.writeValueAsString(event);
            messagingTemplate.convertAndSend("/topic/dict", message);
            log.info("✓ 이미广播사전变更공지: dictCode={}", event.getDictCode());
        } catch (JsonProcessingException e) {
            log.error("사전事件序列化실패: dictCode={}", event.getDictCode(), e);
        } catch (Exception e) {
            log.error("발송사전变更공지실패: dictCode={}", event.getDictCode(), e);
        }
    }

    /**
     * 向特定사용자발송공지메시지
     *
     * @param username 目标사용자명
     * @param message  메시지내용
     */
    @Override
    public void sendNotification(String username, Object message) {
        if (username == null || username.isEmpty()) {
            log.warn("사용자명값空，无法발송공지");
            return;
        }

        if (message == null) {
            log.warn("메시지내용값空，无法발송给사용자[{}]", username);
            return;
        }

        if (messagingTemplate == null) {
            log.warn("메시지템플릿尚미初始化，无法발송사용자메시지");
            return;
        }

        try {
            String messageJson = objectMapper.writeValueAsString(message);
            messagingTemplate.convertAndSendToUser(username, "/queue/messages", messageJson);
            log.info("✓ 이미向사용자[{}]발송공지", username);
        } catch (JsonProcessingException e) {
            log.error("메시지序列化실패: username={}", username, e);
        } catch (Exception e) {
            log.error("向사용자[{}]발송공지실패", username, e);
        }
    }

    /**
     * 广播시스템메시지给所有사용자
     *
     * @param message 메시지내용
     */
    public void broadcastSystemMessage(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("메시지내용값空，无法广播");
            return;
        }

        if (messagingTemplate == null) {
            log.warn("메시지템플릿尚미初始化，无法발송广播메시지");
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
            log.info("✓ 이미广播시스템메시지: {}", message);
        } catch (JsonProcessingException e) {
            log.error("시스템메시지序列化실패", e);
        } catch (Exception e) {
            log.error("广播시스템메시지실패", e);
        }
    }

    // ==================== 内部데이터类 ====================

    /**
     * 세션信息
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
        /** 세션수量 */
        private int sessionCount;
        /** 최초 로그인 시간 */
        private long loginTime;
    }

    /**
     * 시스템메시지
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SystemMessage {
        /** 발송者 */
        private String sender;
        /** 메시지내용 */
        private String content;
        /** 타임스탬프 */
        private long timestamp;
    }
}
