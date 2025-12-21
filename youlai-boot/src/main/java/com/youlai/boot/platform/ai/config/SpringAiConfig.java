package com.youlai.boot.platform.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.youlai.boot.platform.ai.tools.UserTools;

/**
 * Spring AI 설정 클래스
 *
 * Spring AI 자동 설정 사용, 지원 모델:
 * - OpenAI
 * - 통의천문(DashScope, OpenAI 프로토콜 호환)
 * - DeepSeek(OpenAI 프로토콜 호환)
 * - 기타 OpenAI 프로토콜 호환 모델
 *
 * 설정 방법:
 * spring.ai.openai.api-key: xxx
 * spring.ai.openai.base-url: xxx
 * spring.ai.openai.chat.options.model: xxx
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.ai.openai.chat", name = "enabled", havingValue = "true", matchIfMissing = false)
public class SpringAiConfig {

    /**
     * ChatClient 생성 (Spring AI 핵심 클라이언트)
     * <p>
     * OpenAiChatModel은 Spring AI 자동 설정으로 생성됨
     * spring.ai.openai.* 설정을 기반으로 자동 초기화
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel, UserTools userTools) {
        log.info("✅ Spring AI ChatClient 초기화 성공");
        log.info("📋 현재 설정 - 모델: {}", chatModel.getDefaultOptions().getModel());
        // UserTools를 기본 도구로 등록하여 모든 호출에서 사용 가능
        return ChatClient.builder(chatModel)
                .defaultTools(userTools)
                .build();
    }
}

