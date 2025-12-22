package com.youlai.boot.platform.websocket.service;

/**
 * WebSocket 서비스 인터페이스
 * <p>
 * WebSocket 연결 관리와 관련된 기능을 제공합니다:
 * - 사용자 연결/해제 이벤트 처리
 * - 사전 데이터 변경 알림
 * - 시스템 메시지 푸시
 * </p>
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
public interface WebSocketService {

    /**
     * 사용자 연결 이벤트 처리
     *
     * @param username  사용자명
     * @param sessionId WebSocket 세션 ID
     */
    void userConnected(String username, String sessionId);

    /**
     * 사용자 연결 해제 이벤트 처리
     *
     * @param username 사용자명
     */
    void userDisconnected(String username);

    /**
     * 사전 데이터 변경 알림 브로드캐스트
     *
     * @param dictCode 사전 코드
     */
    void broadcastDictChange(String dictCode);

    /**
     * 특정 사용자에게 시스템 알림 전송
     *
     * @param username 대상 사용자명
     * @param message  알림 메시지 내용
     */
    void sendNotification(String username, Object message);
} 
