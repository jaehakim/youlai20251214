package com.youlai.boot.platform.websocket.service;

/**
 * WebSocket서비스인터페이스
 * <p>
 * 提용与WebSocket연결 관리관련의功能，包括：
 * - 사용자连接/断开事件处理
 * - 사전 데이터变更공지
 * - 시스템메시지 푸시
 * </p>
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
public interface WebSocketService {

    /**
     * 处理사용자连接事件
     *
     * @param username  사용자명
     * @param sessionId WebSocket세션ID
     */
    void userConnected(String username, String sessionId);

    /**
     * 处理사용자断开连接事件
     *
     * @param username 사용자명
     */
    void userDisconnected(String username);

    /**
     * 广播사전 데이터变更공지
     *
     * @param dictCode 사전 코드
     */
    void broadcastDictChange(String dictCode);

    /**
     * 발송시스템공지给特定사용자
     *
     * @param username 目标사용자명
     * @param message  공지메시지내용
     */
    void sendNotification(String username, Object message);
} 
