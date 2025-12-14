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
 * WebSocket 테스트用例컨트롤러
 * <p>
 * 包含点对点/广播발송메시지
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
     * 广播발송메시지
     *
     * @param message 메시지내용
     */
    @MessageMapping("/sendToAll")
    @SendTo("/topic/notice")
    public String sendToAll(String message) {
        return "서비스端공지: " + message;
    }

    /**
     * 点对点발송메시지
     * <p>
     * 模拟 张三 给 李四 발송메시지场景
     *
     * @param principal 현재사용자
     * @param username  接收메시지의사용자
     * @param message   메시지내용
     */
    @MessageMapping("/sendToUser/{username}")
    public void sendToUser(Principal principal, @DestinationVariable String username, String message) {
        // 발송人
        String sender = principal.getName();
        // 接收人
        String receiver = username;

        log.info("발송人:{}; 接收人:{}", sender, receiver);
        // 발송메시지给지정된사용자，로 연결후경로 /user/{receiver}/queue/greeting
        messagingTemplate.convertAndSendToUser(receiver, "/queue/greeting", new ChatMessage(sender, message));
    }

}
