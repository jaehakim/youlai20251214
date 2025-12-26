import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { AuthStorage } from "@/utils/auth";

export interface UseStompOptions {
  /** 웹소켓 주소, 미전달 시 VITE_APP_WS_ENDPOINT 환경변수 사용 */
  brokerURL?: string;
  /** 인증용 token, 미전달 시 getAccessToken()의 반환값 사용 */
  token?: string;
  /** 재연결 지연, 단위 밀리초, 기본값 15000 */
  reconnectDelay?: number;
  /** 연결 타임아웃 시간, 단위 밀리초, 기본값 10000 */
  connectionTimeout?: number;
  /** 지수 백오프 재연결 정책 활성화 여부 */
  useExponentialBackoff?: boolean;
  /** 최대 재연결 횟수, 기본값 3 */
  maxReconnectAttempts?: number;
  /** 최대 재연결 지연, 단위 밀리초, 기본값 60000 */
  maxReconnectDelay?: number;
  /** 디버그 로그 활성화 여부 */
  debug?: boolean;
  /** 재연결 시 자동 구독 복구 여부, 기본값 true */
  autoRe저장소Subscriptions?: boolean;
}

/**
 * 구독 설정 정보
 */
interface SubscriptionConfig {
  destination: string;
  callback: (message: IMessage) => void;
}

/**
 * 연결 상태 열거형
 */
enum ConnectionState {
  DISCONNECTED = "DISCONNECTED",
  CONNECTING = "CONNECTING",
  CONNECTED = "CONNECTED",
  RECONNECTING = "RECONNECTING",
}

/**
 * STOMP 웹소켓 연결 관리 컴포저블 함수
 *
 * 핵심 기능:
 * - 자동 연결 관리(연결, 연결 해제, 재연결)
 * - 구독 관리(구독, 구독 취소, 자동 복구)
 * - 하트비트 감지
 * - Token 자동 갱신
 *
 * @param options 설정 옵션
 * @returns STOMP 클라이언트 작업 인터페이스
 */
export function useStomp(options: UseStompOptions = {}) {
  // ==================== 설정 초기화 ====================
  const defaultBrokerURL = import.meta.env.VITE_APP_WS_ENDPOINT || "";

  const config = {
    brokerURL: 참조(options.brokerURL ?? defaultBrokerURL),
    reconnectDelay: options.reconnectDelay ?? 15000,
    connectionTimeout: options.connectionTimeout ?? 10000,
    useExponentialBackoff: options.useExponentialBackoff ?? false,
    maxReconnectAttempts: options.maxReconnectAttempts ?? 3,
    maxReconnectDelay: options.maxReconnectDelay ?? 60000,
    autoRe저장소Subscriptions: options.autoRe저장소Subscriptions ?? true,
    debug: options.debug ?? false,
  };

  // ==================== 상태 관리 ====================
  const connectionState = 참조<ConnectionState>(ConnectionState.DISCONNECTED);
  const isConnected = computed(() => connectionState.value === ConnectionState.CONNECTED);
  const reconnectAttempts = 참조(0);

  // ==================== 타이머 관리 ====================
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  let connectionTimeoutTimer: ReturnType<typeof setTimeout> | null = null;

  // ==================== 구독 관리 ====================
  // 활성 구독: 현재 STOMP 구독 객체 저장
  const activeSubscriptions = new Map<string, StompSubscription>();
  // 구독 설정 레지스트리: 자동 구독 복구용
  const subscriptionRegistry = new Map<string, SubscriptionConfig>();

  // ==================== 클라이언트 인스턴스 ====================
  const stompClient = 참조<Client | null>(null);
  let isManualDisconnect = false;

  // ==================== 유틸리티 함수 ====================

  /**
   * 모든 타이머 정리
   */
  const clearAllTimers = () => {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer);
      reconnectTimer = null;
    }
    if (connectionTimeoutTimer) {
      clearTimeout(connectionTimeoutTimer);
      connectionTimeoutTimer = null;
    }
  };

  /**
   * 로그 출력(디버그 모드 제어 지원)
   */
  const log = (...args: any[]) => {
    if (config.debug) {
      console.log("[useStomp]", ...args);
    }
  };

  const logWarn = (...args: any[]) => {
    console.warn("[useStomp]", ...args);
  };

  const logError = (...args: any[]) => {
    console.error("[useStomp]", ...args);
  };

  /**
   * 모든 구독 복구
   */
  const re저장소Subscriptions = () => {
    if (!config.autoRe저장소Subscriptions || subscriptionRegistry.size === 0) {
      return;
    }

    log(`${subscriptionRegistry.size}개 구독 복구 시작...`);

    for (const [destination, subscriptionConfig] of subscriptionRegistry.entries()) {
      try {
        performSubscribe(destination, subscriptionConfig.callback);
      } catch (error) {
        logError(`구독 복구 실패 ${destination}:`, error);
      }
    }
  };

  /**
   * STOMP 클라이언트 초기화
   */
  const initializeClient = () => {
    // 클라이언트가 이미 존재하고 활성 상태이면 바로 반환
    if (stompClient.value && (stompClient.value.active || stompClient.value.connected)) {
      log("STOMP 클라이언트가 이미 존재하고 활성 상태입니다, 초기화 건너뜀");
      return;
    }

    // 웹소켓 엔드포인트 설정 여부 확인
    if (!config.brokerURL.value) {
      logWarn("웹소켓 연결 실패: 웹소켓 엔드포인트 URL이 설정되지 않음");
      return;
    }

    // 매 연결 전 최신 토큰 조회
    const accessToken = AuthStorage.getAccessToken();
    if (!accessToken) {
      logWarn("웹소켓 연결 실패: 인증 토큰이 비어있음, 먼저 로그인하세요");
      return;
    }

    // 이전 클라이언트 정리
    if (stompClient.value) {
      try {
        stompClient.value.deactivate();
      } catch (error) {
        logWarn("이전 클라이언트 정리 중 오류:", error);
      }
      stompClient.value = null;
    }

    // STOMP 클라이언트 생성
    stompClient.value = new Client({
      brokerURL: config.brokerURL.value,
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      debug: config.debug ? (msg) => console.log("[STOMP]", msg) : () => {},
      reconnectDelay: 0, // 내장 재연결 비활성화, 커스텀 재연결 로직 사용
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // ==================== 이벤트 리스너 ====================

    // 연결 성공
    stompClient.value.onConnect = () => {
      connectionState.value = ConnectionState.CONNECTED;
      reconnectAttempts.value = 0;
      clearAllTimers();

      log("웹소켓 연결이 수립되었습니다");

      // 자동 구독 복구
      re저장소Subscriptions();
    };

    // 연결 해제
    stompClient.value.onDisconnect = () => {
      connectionState.value = ConnectionState.DISCONNECTED;
      log("웹소켓 연결이 해제되었습니다");

      // 활성 구독 정리(구독 설정은 복구용으로 유지)
      activeSubscriptions.clear();

      // 수동 연결 해제가 아니고 최대 재연결 횟수에 도달하지 않았으면 재연결 시도
      if (!isManualDisconnect && reconnectAttempts.value < config.maxReconnectAttempts) {
        scheduleReconnect();
      }
    };

    // 웹소켓 닫힘
    stompClient.value.on웹소켓Close = (event) => {
      connectionState.value = ConnectionState.DISCONNECTED;
      log(`웹소켓이 닫힘: code=${event?.code}, reason=${event?.reason}`);

      // 수동 연결 해제인 경우 재연결하지 않음
      if (isManualDisconnect) {
        log("수동 연결 해제, 재연결하지 않음");
        return;
      }

      // 비정상 닫힘인 경우 재연결 시도
      if (
        event?.code &&
        [1000, 1006, 1008, 1011].includes(event.code) &&
        reconnectAttempts.value < config.maxReconnectAttempts
      ) {
        log("연결 비정상 닫힘 감지, 재연결 시도");
        scheduleReconnect();
      }
    };

    // STOMP 오류
    stompClient.value.onStompError = (frame) => {
      logError("STOMP 오류:", frame.headers, frame.body);
      connectionState.value = ConnectionState.DISCONNECTED;

      // 인증 오류 여부 확인
      const isAuthError =
        frame.headers?.message?.includes("Unauthorized") ||
        frame.body?.includes("Unauthorized") ||
        frame.body?.includes("Token") ||
        frame.body?.includes("401");

      if (isAuthError) {
        logWarn("웹소켓 인증 오류, 재연결 중지");
        isManualDisconnect = true; // 인증 오류 시 재연결하지 않음
      }
    };
  };

  /**
   * 재연결 작업 스케줄링
   */
  const scheduleReconnect = () => {
    // 연결 중이거나 수동 연결 해제인 경우 재연결하지 않음
    if (connectionState.value === ConnectionState.CONNECTING || isManualDisconnect) {
      return;
    }

    // 최대 재연결 횟수 도달 여부 확인
    if (reconnectAttempts.value >= config.maxReconnectAttempts) {
      logError(`최대 재연결 횟수 도달 (${config.maxReconnectAttempts}), 재연결 중지`);
      return;
    }

    reconnectAttempts.value++;
    connectionState.value = ConnectionState.RECONNECTING;

    // 재연결 지연 계산(지수 백오프 지원)
    const delay = config.useExponentialBackoff
      ? Math.min(
          config.reconnectDelay * Math.pow(2, reconnectAttempts.value - 1),
          config.maxReconnectDelay
        )
      : config.reconnectDelay;

    log(`재연결 준비 (${reconnectAttempts.value}/${config.maxReconnectAttempts}), 지연 ${delay}ms`);

    // 이전 재연결 타이머 정리
    if (reconnectTimer) {
      clearTimeout(reconnectTimer);
    }

    // 재연결 타이머 설정
    reconnectTimer = setTimeout(() => {
      if (connectionState.value !== ConnectionState.CONNECTED && !isManualDisconnect) {
        log(`${reconnectAttempts.value}번째 재연결 시작...`);
        connect();
      }
    }, delay);
  };

  // brokerURL 변경 감지, 자동 재초기화
  watch(config.brokerURL, (newURL, oldURL) => {
    if (newURL !== oldURL) {
      log(`웹소켓 엔드포인트 변경됨: ${oldURL} -> ${newURL}`);

      // 현재 연결 해제
      if (stompClient.value && stompClient.value.connected) {
        stompClient.value.deactivate();
      }

      // 클라이언트 재초기화
      initializeClient();
    }
  });

  // 클라이언트 초기화
  initializeClient();

  // ==================== 공개 인터페이스 ====================

  /**
   * 웹소켓 연결 수립
   */
  const connect = () => {
    // 수동 연결 해제 플래그 초기화
    isManualDisconnect = false;

    // 웹소켓 엔드포인트 설정 여부 확인
    if (!config.brokerURL.value) {
      logError("웹소켓 연결 실패: 웹소켓 엔드포인트 URL이 설정되지 않음");
      return;
    }

    // 중복 연결 방지
    if (connectionState.value === ConnectionState.CONNECTING) {
      log("웹소켓 연결 중, 중복 연결 요청 건너뜀");
      return;
    }

    // 클라이언트가 없으면 먼저 초기화
    if (!stompClient.value) {
      initializeClient();
    }

    if (!stompClient.value) {
      logError("STOMP 클라이언트 초기화 실패");
      return;
    }

    // 중복 연결 방지: 이미 연결되어 있는지 확인
    if (stompClient.value.connected) {
      log("웹소켓 이미 연결됨, 중복 연결 건너뜀");
      connectionState.value = ConnectionState.CONNECTED;
      return;
    }

    // 연결 상태 설정
    connectionState.value = ConnectionState.CONNECTING;

    // 연결 타임아웃 설정
    if (connectionTimeoutTimer) {
      clearTimeout(connectionTimeoutTimer);
    }

    connectionTimeoutTimer = setTimeout(() => {
      if (connectionState.value === ConnectionState.CONNECTING) {
        logWarn("웹소켓 연결 타임아웃");
        connectionState.value = ConnectionState.DISCONNECTED;

        // 타임아웃 후 재연결 시도
        if (!isManualDisconnect && reconnectAttempts.value < config.maxReconnectAttempts) {
          scheduleReconnect();
        }
      }
    }, config.connectionTimeout);

    try {
      stompClient.value.activate();
      log("웹소켓 연결 수립 중...");
    } catch (error) {
      logError("웹소켓 연결 활성화 실패:", error);
      connectionState.value = ConnectionState.DISCONNECTED;
    }
  };

  /**
   * 구독 작업 실행(내부 메서드)
   */
  const performSubscribe = (destination: string, callback: (message: IMessage) => void): string => {
    if (!stompClient.value || !stompClient.value.connected) {
      logWarn(`구독 시도 실패 ${destination}: 클라이언트가 연결되지 않음`);
      return "";
    }

    try {
      const subscription = stompClient.value.subscribe(destination, callback);
      const subscriptionId = subscription.id;
      activeSubscriptions.set(subscriptionId, subscription);
      log(`구독 성공: ${destination} (ID: ${subscriptionId})`);
      return subscriptionId;
    } catch (error) {
      logError(`구독 실패 ${destination}:`, error);
      return "";
    }
  };

  /**
   * 지정 토픽 구독
   *
   * @param destination 대상 토픽 주소(예: /topic/message)
   * @param callback 메시지 수신 시 콜백 함수
   * @returns 구독 ID, 이후 구독 취소에 사용
   */
  const subscribe = (destination: string, callback: (message: IMessage) => void): string => {
    // 구독 설정을 레지스트리에 저장, 연결 해제 후 재연결 시 자동 복구용
    subscriptionRegistry.set(destination, { destination, callback });

    // 이미 연결된 경우 즉시 구독
    if (stompClient.value?.connected) {
      return performSubscribe(destination, callback);
    }

    log(`구독 설정 임시 저장: ${destination}, 연결 수립 후 자동 구독됨`);
    return "";
  };

  /**
   * 구독 취소
   *
   * @param subscriptionId 구독 ID(subscribe 메서드에서 반환)
   */
  const unsubscribe = (subscriptionId: string) => {
    const subscription = activeSubscriptions.get(subscriptionId);
    if (subscription) {
      try {
        subscription.unsubscribe();
        activeSubscriptions.delete(subscriptionId);
        log(`구독 취소됨: ${subscriptionId}`);
      } catch (error) {
        logWarn(`구독 취소 중 오류 ${subscriptionId}:`, error);
      }
    }
  };

  /**
   * 지정 토픽의 구독 취소(레지스트리에서 제거)
   *
   * @param destination 토픽 주소
   */
  const unsubscribeDestination = (destination: string) => {
    // 레지스트리에서 제거
    subscriptionRegistry.delete(destination);

    // 해당 토픽의 모든 활성 구독 취소
    for (const [id, subscription] of activeSubscriptions.entries()) {
      // 주의: STOMP의 subscription 객체는 destination을 직접 노출하지 않음,
      // 여기서는 간소화 처리, 실제 사용 시 id -> destination 매핑 추가 관리 필요할 수 있음
      try {
        subscription.unsubscribe();
        activeSubscriptions.delete(id);
      } catch (error) {
        logWarn(`구독 취소 중 오류 ${id}:`, error);
      }
    }

    log(`토픽 구독 설정 제거됨: ${destination}`);
  };

  /**
   * 웹소켓 연결 해제
   *
   * @param clearSubscriptions 구독 레지스트리 정리 여부(기본값 true)
   */
  const disconnect = (clearSubscriptions = true) => {
    // 수동 연결 해제 플래그 설정
    isManualDisconnect = true;

    // 모든 타이머 정리
    clearAllTimers();

    // 모든 활성 구독 취소
    for (const [id, subscription] of activeSubscriptions.entries()) {
      try {
        subscription.unsubscribe();
      } catch (error) {
        logWarn(`구독 취소 중 오류 ${id}:`, error);
      }
    }
    activeSubscriptions.clear();

    // 선택: 구독 레지스트리 정리
    if (clearSubscriptions) {
      subscriptionRegistry.clear();
      log("모든 구독 설정이 정리됨");
    }

    // 연결 해제
    if (stompClient.value) {
      try {
        if (stompClient.value.connected || stompClient.value.active) {
          stompClient.value.deactivate();
          log("웹소켓 연결이 수동으로 해제됨");
        }
      } catch (error) {
        logError("웹소켓 연결 해제 중 오류:", error);
      }
      stompClient.value = null;
    }

    connectionState.value = ConnectionState.DISCONNECTED;
    reconnectAttempts.value = 0;
  };

  // ==================== 공개 인터페이스 반환 ====================
  return {
    // 상태
    connectionState: readonly(connectionState),
    isConnected,
    reconnectAttempts: readonly(reconnectAttempts),

    // 연결 관리
    connect,
    disconnect,

    // 구독 관리
    subscribe,
    unsubscribe,
    unsubscribeDestination,

    // 통계 정보
    getActiveSubscriptionCount: () => activeSubscriptions.size,
    getRegisteredSubscriptionCount: () => subscriptionRegistry.size,
  };
}
