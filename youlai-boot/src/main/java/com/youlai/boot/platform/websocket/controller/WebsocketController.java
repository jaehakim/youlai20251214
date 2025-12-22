package com.youlai.boot.platform.websocket.controller;

import com.youlai.boot.platform.websocket.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * WebSocket 테스트 컨트롤러
 * <p>
 * 점대점/브로드캐스트 메시지 전송 기능 포함
 *
 * @author Ray.Hao
 * @since 2.3.0
 */
@RestController
@RequestMapping("/websocket")
@RequiredArgsConstructor
@Slf4j
public class WebsocketController {

    private final SimpMessagingTemplate messagingTemplate;


    /**
     * 브로드캐스트 메시지 전송
     *
     * @param message 메시지 내용
     */
    @MessageMapping("/sendToAll")
    @SendTo("/topic/notice")
    public String sendToAll(String message) {
        return "서버 알림: " + message;
    }

    /**
     * 점대점 메시지 전송
     * <p>
     * 사용자 A가 사용자 B에게 메시지를 전송하는 시나리오 시뮬레이션
     *
     * @param principal 현재 사용자
     * @param username  메시지를 수신할 사용자
     * @param message   메시지 내용
     */
    @MessageMapping("/sendToUser/{username}")
    public void sendToUser(Principal principal, @DestinationVariable String username, String message) {
        // 발송자
        String sender = principal.getName();
        // 수신자
        String receiver = username;

        log.info("발송자: {}; 수신자: {}", sender, receiver);
        // 지정된 사용자에게 메시지 전송, 최종 경로: /user/{receiver}/queue/greeting
        messagingTemplate.convertAndSendToUser(receiver, "/queue/greeting", new ChatMessage(sender, message));
    }

}
