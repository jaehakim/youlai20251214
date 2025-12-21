package com.youlai.boot.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket 메시지 서비스
 *
 * @author Ray
 * @since 3.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 사전 이벤트 유형
     */
    public enum DictEventType {
        /**
         * 사전 업데이트
         */
        DICT_UPDATED,

        /**
         * 사전 삭제
         */
        DICT_DELETED
    }

    /**
     * 사전 이벤트 메시지
     */
    public static class DictEvent {
        /**
         * 이벤트 유형
         */
        private String type;

        /**
         * 사전 코드
         */
        private String dictCode;

        /**
         * 타임스탬프
         */
        private long timestamp;

        public DictEvent(DictEventType type, String dictCode) {
            this.type = type.name();
            this.dictCode = dictCode;
            this.timestamp = System.currentTimeMillis();
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDictCode() {
            return dictCode;
        }

        public void setDictCode(String dictCode) {
            this.dictCode = dictCode;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    /**
     * 모든 클라이언트에 사전 업데이트 이벤트 전송
     *
     * @param dictCode 사전 코드
     */
    public void sendDictUpdatedEvent(String dictCode) {
        DictEvent event = new DictEvent(DictEventType.DICT_UPDATED, dictCode);
        sendDictEvent(event);
    }

    /**
     * 모든 클라이언트에 사전 삭제 이벤트 전송
     *
     * @param dictCode 사전 코드
     */
    public void sendDictDeletedEvent(String dictCode) {
        DictEvent event = new DictEvent(DictEventType.DICT_DELETED, dictCode);
        sendDictEvent(event);
    }

    /**
     * 사전 이벤트 메시지 전송
     *
     * @param event 사전 이벤트
     */
    private void sendDictEvent(DictEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            messagingTemplate.convertAndSend("/topic/dict", message);
            log.info("Sent dict event to clients: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Failed to send dict event", e);
        }
    }
}
