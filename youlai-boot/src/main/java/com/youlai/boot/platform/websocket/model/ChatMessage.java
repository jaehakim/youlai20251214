package com.youlai.boot.platform.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 시스템 메시지 모델
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    /**
     * 발송자
     */
    private String sender;

    /**
     * 메시지 내용
     */
    private String content;

}
