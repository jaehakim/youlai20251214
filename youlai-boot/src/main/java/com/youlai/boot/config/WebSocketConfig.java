package com.youlai.boot.config;

import cn.hutool.core.util.StrUtil;
import com.youlai.boot.security.model.SysUserDetails;
import com.youlai.boot.security.token.TokenManager;
import com.youlai.boot.platform.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 설정 클래스
 *
 * 핵심 기능:
 * - WebSocket 엔드포인트 설정
 * - 메시지 브로커 설정
 * - 연결 인증 및 권한 부여 구현
 * - 사용자 세션 생명주기 관리
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@EnableWebSocketMessageBroker
@Configuration
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String WS_ENDPOINT = "/ws";
    private static final String APP_DESTINATION_PREFIX = "/app";
    private static final String USER_DESTINATION_PREFIX = "/user";
    private static final String[] BROKER_DESTINATIONS = {"/topic", "/queue"};

    private final TokenManager tokenManager;
    private final WebSocketService webSocketService;

    public WebSocketConfig(TokenManager tokenManager, @Lazy WebSocketService webSocketService) {
        this.tokenManager = tokenManager;
        this.webSocketService = webSocketService;
        log.info("✓ WebSocket 설정 로드 완료");
    }

    /**
     * STOMP 엔드포인트 등록
     *
     * 클라이언트는 이 엔드포인트를 통해 WebSocket 연결을 설정합니다
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint(WS_ENDPOINT)
                .setAllowedOriginPatterns("*"); // 크로스 도메인 허용 (운영 환경에서는 구체적인 도메인 설정 권장)

        log.info("✓ STOMP 엔드포인트 등록 완료: {}", WS_ENDPOINT);
    }

    /**
     * 메시지 브로커 설정
     *
     * - /app 접두사: 클라이언트가 서버로 메시지를 전송할 때 사용하는 접두사
     * - /topic 접두사: 브로드캐스트 메시지에 사용
     * - /queue 접두사: 1:1 메시지에 사용
     * - /user 접두사: 서버가 특정 사용자에게 메시지를 전송할 때 사용하는 접두사
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 메시지를 전송하는 요청 접두사
        registry.setApplicationDestinationPrefixes(APP_DESTINATION_PREFIX);

        // 간단한 메시지 브로커 활성화, /topic과 /queue 접두사를 가진 메시지 처리
        registry.enableSimpleBroker(BROKER_DESTINATIONS);

        // 서버가 클라이언트에게 알릴 때 사용하는 접두사
        registry.setUserDestinationPrefix(USER_DESTINATION_PREFIX);

        log.info("✓ 메시지 브로커 설정 완료: app={}, broker={}, user={}",
                APP_DESTINATION_PREFIX, BROKER_DESTINATIONS, USER_DESTINATION_PREFIX);
    }

    /**
     * 클라이언트 인바운드 채널 인터셉터 설정
     *
     * 핵심 기능:
     * 1. 연결 설정 시: JWT Token을 파싱하고 사용자 신원 바인딩
     * 2. 연결 종료 시: 사용자 오프라인 알림 트리거
     * 3. 보안 방어: 유효하지 않은 연결 요청 차단
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // 방어적 체크: accessor가 null이 아님을 보장
                if (accessor == null) {
                    log.warn("⚠ 비정상 메시지 수신: StompHeaderAccessor를 가져올 수 없음");
                    return ChannelInterceptor.super.preSend(message, channel);
                }

                StompCommand command = accessor.getCommand();
                if (command == null) {
                    return ChannelInterceptor.super.preSend(message, channel);
                }

                try {
                    switch (command) {
                        case CONNECT:
                            handleConnect(accessor);
                            break;

                        case DISCONNECT:
                            handleDisconnect(accessor);
                            break;

                        case SUBSCRIBE:
                            handleSubscribe(accessor);
                            break;

                        default:
                            // 기타 명령은 특별한 처리 불필요
                            break;
                    }
                } catch (AuthenticationException ex) {
                    // 인증 실패 시 강제로 연결 종료
                    log.error("❌ 연결 인증 실패: {}", ex.getMessage());
                    throw ex;
                } catch (Exception ex) {
                    // 기타 알 수 없는 예외 포착
                    log.error("❌ WebSocket 메시지 처리 예외", ex);
                    throw new MessagingException("메시지 처리 실패: " + ex.getMessage());
                }

                return ChannelInterceptor.super.preSend(message, channel);
            }
        });

        log.info("✓ 클라이언트 인바운드 채널 인터셉터 설정 완료");
    }

    /**
     * 클라이언트 연결 요청 처리
     *
     * 보안 검증 흐름:
     * 1. Authorization 헤더 추출
     * 2. Bearer Token 형식 검증
     * 3. JWT 유효성 파싱 및 검증
     * 4. 현재 세션에 사용자 신원 바인딩
     * 5. 사용자 온라인 상태 기록
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

        // 보안 체크: Authorization 헤더가 존재하고 형식이 올바른지 확인
        if (StrUtil.isBlank(authorization)) {
            log.warn("⚠ 불법 연결 요청: Authorization 헤더 누락");
            throw new AuthenticationCredentialsNotFoundException("Authorization 헤더 누락");
        }

        if (!authorization.startsWith("Bearer ")) {
            log.warn("⚠ 불법 연결 요청: Authorization 헤더 형식 오류");
            throw new BadCredentialsException("Authorization 헤더 형식 오류");
        }

        // JWT Token 추출 ("Bearer " 접두사 제거)
        String token = authorization.substring(7);

        if (StrUtil.isBlank(token)) {
            log.warn("⚠ 불법 연결 요청: Token이 비어있음");
            throw new BadCredentialsException("Token이 비어있음");
        }

        // Token 파싱 및 검증
        Authentication authentication;
        try {
            authentication = tokenManager.parseToken(token);
        } catch (Exception ex) {
            log.error("❌ Token 파싱 실패", ex);
            throw new BadCredentialsException("Token 유효하지 않음: " + ex.getMessage());
        }

        // 파싱 결과 검증
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("⚠ Token 파싱 실패: 인증 객체가 유효하지 않음");
            throw new BadCredentialsException("Token 파싱 실패");
        }

        // 사용자 상세 정보 가져오기
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SysUserDetails)) {
            log.error("❌ 유효하지 않은 사용자 자격 증명 타입: {}", principal.getClass().getName());
            throw new BadCredentialsException("사용자 자격 증명 타입 오류");
        }

        SysUserDetails userDetails = (SysUserDetails) principal;
        String username = userDetails.getUsername();

        if (StrUtil.isBlank(username)) {
            log.warn("⚠ 사용자 이름이 비어있음");
            throw new BadCredentialsException("사용자 이름이 비어있음");
        }

        // 현재 세션에 사용자 신원 바인딩 (중요: @SendToUser 등 어노테이션에 사용)
        accessor.setUser(authentication);

        // 세션 ID 가져오기
        String sessionId = accessor.getSessionId();
        if (sessionId == null) {
            log.warn("⚠ 세션 ID가 비어있음, 임시 ID 사용");
            sessionId = "temp-" + System.nanoTime();
        }

        // 사용자 온라인 상태 기록
        try {
            webSocketService.userConnected(username, sessionId);
            log.info("✓ WebSocket 연결 설정 성공: 사용자[{}], 세션[{}]", username, sessionId);
        } catch (Exception ex) {
            log.error("❌ 사용자 온라인 상태 기록 실패: 사용자[{}], 세션[{}]", username, sessionId, ex);
            // 예외를 던지지 않고 연결 계속 진행
        }
    }

    /**
     * 클라이언트 연결 끊김 이벤트 처리
     *
     * 주의사항:
     * - 성공적으로 인증이 설정된 연결만 오프라인 이벤트 트리거
     * - 인증 실패한 연결이 더티 데이터를 생성하는 것 방지
     */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();

        // 방어적 체크: 인증된 연결만 처리
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("미인증 연결 끊김, 처리 건너뜀");
            return;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SysUserDetails)) {
            log.warn("⚠ 연결 끊김 시 사용자 자격 증명 타입 이상");
            return;
        }

        SysUserDetails userDetails = (SysUserDetails) principal;
        String username = userDetails.getUsername();

        if (StrUtil.isNotBlank(username)) {
            try {
                webSocketService.userDisconnected(username);
                log.info("✓ WebSocket 연결 끊김: 사용자[{}]", username);
            } catch (Exception ex) {
                log.error("❌ 사용자 오프라인 상태 기록 실패: 사용자[{}]", username, ex);
            }
        }
    }

    /**
     * 클라이언트 구독 이벤트 처리 (선택 사항)
     *
     * 구독 정보를 기록하거나 구독 수준의 권한 제어를 구현하는 데 사용
     */
    private void handleSubscribe(StompHeaderAccessor accessor) {
        Authentication authentication = (Authentication) accessor.getUser();

        if (authentication != null && authentication.isAuthenticated()) {
            String destination = accessor.getDestination();
            String username = authentication.getName();

            log.debug("사용자[{}] 주제 구독: {}", username, destination);

            // TODO: 여기서 구독 수준의 권한 제어를 구현할 수 있습니다
            // 예: 사용자가 특정 주제를 구독할 권한이 있는지 확인
        }
    }
}
