import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { AuthStorage } from "@/utils/auth";

export interface UseStompOptions {
  /** 웹소켓 주소，아님传시사용 VITE_APP_WS_ENDPOINT 环境변수 */
  brokerURL?: string;
  /** 용도鉴权의 token，아님传시사용 getAccessToken() 의반환값 */
  token?: string;
  /** 재연결지연，단일자리毫秒，기본값로 15000 */
  reconnectDelay?: number;
  /** 연결시간초과시사이，단일자리毫秒，기본값로 10000 */
  connectionTimeout?: number;
  /** 여부开启指개退避재연결정책 */
  useExponentialBackoff?: boolean;
  /** 最大재연결次개，기본값로 3 */
  maxReconnectAttempts?: number;
  /** 最大재연결지연，단일자리毫秒，기본값로 60000 */
  maxReconnectDelay?: number;
  /** 여부开启调试日志 */
  debug?: boolean;
  /** 여부에재연결시자동복구구독，기본값로 true */
  autoRe저장소Subscriptions?: boolean;
}

/**
 * 구독설정정보
 */
interface SubscriptionConfig {
  destination: string;
  callback: (message: IMessage) => void;
}

/**
 * 연결상태열거형
 */
enum ConnectionState {
  DISCONNECTED = "DISCONNECTED",
  CONNECTING = "CONNECTING",
  CONNECTED = "CONNECTED",
  RECONNECTING = "RECONNECTING",
}

/**
 * STOMP 웹소켓 연결관리조합式함수
 *
 * 核心기능：
 * - 자동연결관리（연결、끊김、재연결）
 * - 구독관리（구독、취소구독、자동복구）
 * - 心점프检测
 * - Token 자동새로고침
 *
 * @param options 설정옵션
 * @returns STOMP 클라이언트작업인터페이스
 */
export function useStomp(options: UseStompOptions = {}) {
  // ==================== 설정초기화 ====================
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

  // ==================== 定시기기관리 ====================
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  let connectionTimeoutTimer: ReturnType<typeof setTimeout> | null = null;

  // ==================== 구독관리 ====================
  // 活动구독：저장소当前 STOMP 구독객체
  const activeSubscriptions = new Map<string, StompSubscription>();
  // 구독설정등록테이블：용도자동복구구독
  const subscriptionRegistry = new Map<string, SubscriptionConfig>();

  // ==================== 클라이언트实例 ====================
  const stompClient = 참조<Client | null>(null);
  let isManualDisconnect = false;

  // ==================== 工具함수 ====================

  /**
   * 정리모든定시기기
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
   * 日志출력（지원调试모드控制）
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
   * 복구모든구독
   */
  const re저장소Subscriptions = () => {
    if (!config.autoRe저장소Subscriptions || subscriptionRegistry.size === 0) {
      return;
    }

    log(`시작복구 ${subscriptionRegistry.size} 개구독...`);

    for (const [destination, subscriptionConfig] of subscriptionRegistry.entries()) {
      try {
        performSubscribe(destination, subscriptionConfig.callback);
      } catch (error) {
        logError(`복구구독 ${destination} 실패:`, error);
      }
    }
  };

  /**
   * 초기화 STOMP 클라이언트
   */
  const initializeClient = () => {
    // 만약클라이언트이미存에且处于活动상태，直接돌아가기
    if (stompClient.value && (stompClient.value.active || stompClient.value.connected)) {
      log("STOMP 클라이언트이미存에且处于活动상태，점프거치초기화");
      return;
    }

    // 확인 웹소켓 엔드포인트여부설정
    if (!config.brokerURL.value) {
      logWarn("웹소켓 연결실패: 미설정 웹소켓 엔드포인트 URL");
      return;
    }

    // 每次연결前다시조회최신令牌
    const accessToken = AuthStorage.getAccessToken();
    if (!accessToken) {
      logWarn("웹소켓 연결실패：권한 부여令牌로비어있음，요청先로그인");
      return;
    }

    // 정리旧클라이언트
    if (stompClient.value) {
      try {
        stompClient.value.deactivate();
      } catch (error) {
        logWarn("정리旧클라이언트시出错:", error);
      }
      stompClient.value = null;
    }

    // 생성 STOMP 클라이언트
    stompClient.value = new Client({
      brokerURL: config.brokerURL.value,
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      debug: config.debug ? (msg) => console.log("[STOMP]", msg) : () => {},
      reconnectDelay: 0, // 비활성화内置재연결，사용사용자 정의재연결逻辑
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // ==================== 이벤트 리스닝기기 ====================

    // 연결성공
    stompClient.value.onConnect = () => {
      connectionState.value = ConnectionState.CONNECTED;
      reconnectAttempts.value = 0;
      clearAllTimers();

      log("✅ 웹소켓 연결이미구축");

      // 자동복구구독
      re저장소Subscriptions();
    };

    // 연결끊김
    stompClient.value.onDisconnect = () => {
      connectionState.value = ConnectionState.DISCONNECTED;
      log("❌ 웹소켓 연결이미끊김");

      // 정리비어있음活动구독（但보유구독설정용도복구）
      activeSubscriptions.clear();

      // 만약아님예手动끊김且미达到最大재연결次개，그러면尝试재연결
      if (!isManualDisconnect && reconnectAttempts.value < config.maxReconnectAttempts) {
        scheduleReconnect();
      }
    };

    // 웹소켓 닫기
    stompClient.value.on웹소켓Close = (event) => {
      connectionState.value = ConnectionState.DISCONNECTED;
      log(`웹소켓 이미닫기: code=${event?.code}, reason=${event?.reason}`);

      // 만약예手动끊김，아님재연결
      if (isManualDisconnect) {
        log("手动끊김연결，아님进행재연결");
        return;
      }

      // 对于예외닫기，尝试재연결
      if (
        event?.code &&
        [1000, 1006, 1008, 1011].includes(event.code) &&
        reconnectAttempts.value < config.maxReconnectAttempts
      ) {
        log("检测到연결예외닫기，로尝试재연결");
        scheduleReconnect();
      }
    };

    // STOMP 오류
    stompClient.value.onStompError = (frame) => {
      logError("STOMP 오류:", frame.headers, frame.body);
      connectionState.value = ConnectionState.DISCONNECTED;

      // 확인여부예권한 부여오류
      const isAuthError =
        frame.headers?.message?.includes("Unauthorized") ||
        frame.body?.includes("Unauthorized") ||
        frame.body?.includes("Token") ||
        frame.body?.includes("401");

      if (isAuthError) {
        logWarn("웹소켓 권한 부여오류，중지재연결");
        isManualDisconnect = true; // 권한 부여오류아님进행재연결
      }
    };
  };

  /**
   * 调度재연결任务
   */
  const scheduleReconnect = () => {
    // 만약正에연결또는手动끊김，아님재연결
    if (connectionState.value === ConnectionState.CONNECTING || isManualDisconnect) {
      return;
    }

    // 확인여부达到最大재연결次개
    if (reconnectAttempts.value >= config.maxReconnectAttempts) {
      logError(`이미达到最大재연결次개 (${config.maxReconnectAttempts})，중지재연결`);
      return;
    }

    reconnectAttempts.value++;
    connectionState.value = ConnectionState.RECONNECTING;

    // 계算재연결지연（지원指개退避）
    const delay = config.useExponentialBackoff
      ? Math.min(
          config.reconnectDelay * Math.pow(2, reconnectAttempts.value - 1),
          config.maxReconnectDelay
        )
      : config.reconnectDelay;

    log(`准备재연결 (${reconnectAttempts.value}/${config.maxReconnectAttempts})，지연 ${delay}ms`);

    // 정리除之前의재연결계시기기
    if (reconnectTimer) {
      clearTimeout(reconnectTimer);
    }

    // 설정재연결계시기기
    reconnectTimer = setTimeout(() => {
      if (connectionState.value !== ConnectionState.CONNECTED && !isManualDisconnect) {
        log(`시작第 ${reconnectAttempts.value} 次재연결...`);
        connect();
      }
    }, delay);
  };

  // 리스닝 brokerURL 의변경，자동다시초기화
  watch(config.brokerURL, (newURL, oldURL) => {
    if (newURL !== oldURL) {
      log(`웹소켓 엔드포인트이미更改: ${oldURL} -> ${newURL}`);

      // 끊김当前연결
      if (stompClient.value && stompClient.value.connected) {
        stompClient.value.deactivate();
      }

      // 다시초기화클라이언트
      initializeClient();
    }
  });

  // 초기화클라이언트
  initializeClient();

  // ==================== 公共인터페이스 ====================

  /**
   * 구축 웹소켓 연결
   */
  const connect = () => {
    // 초기화手动끊김标志
    isManualDisconnect = false;

    // 확인여부설정됨 웹소켓 엔드포인트
    if (!config.brokerURL.value) {
      logError("웹소켓 연결실패: 미설정 웹소켓 엔드포인트 URL");
      return;
    }

    // 방지중복연결
    if (connectionState.value === ConnectionState.CONNECTING) {
      log("웹소켓 正에연결내，점프거치중복연결요청");
      return;
    }

    // 만약클라이언트아님存에，先초기화
    if (!stompClient.value) {
      initializeClient();
    }

    if (!stompClient.value) {
      logError("STOMP 클라이언트초기화실패");
      return;
    }

    // 避免중복연결：확인여부이미연결
    if (stompClient.value.connected) {
      log("웹소켓 이미연결，점프거치중복연결");
      connectionState.value = ConnectionState.CONNECTED;
      return;
    }

    // 설정연결상태
    connectionState.value = ConnectionState.CONNECTING;

    // 설정연결시간초과
    if (connectionTimeoutTimer) {
      clearTimeout(connectionTimeoutTimer);
    }

    connectionTimeoutTimer = setTimeout(() => {
      if (connectionState.value === ConnectionState.CONNECTING) {
        logWarn("웹소켓 연결시간초과");
        connectionState.value = ConnectionState.DISCONNECTED;

        // 시간초과후尝试재연결
        if (!isManualDisconnect && reconnectAttempts.value < config.maxReconnectAttempts) {
          scheduleReconnect();
        }
      }
    }, config.connectionTimeout);

    try {
      stompClient.value.activate();
      log("正에구축 웹소켓 연결...");
    } catch (error) {
      logError("활성화 웹소켓 연결실패:", error);
      connectionState.value = ConnectionState.DISCONNECTED;
    }
  };

  /**
   * 실행구독작업（내부메서드）
   */
  const performSubscribe = (destination: string, callback: (message: IMessage) => void): string => {
    if (!stompClient.value || !stompClient.value.connected) {
      logWarn(`尝试구독 ${destination} 실패: 클라이언트미연결`);
      return "";
    }

    try {
      const subscription = stompClient.value.subscribe(destination, callback);
      const subscriptionId = subscription.id;
      activeSubscriptions.set(subscriptionId, subscription);
      log(`✓ 구독성공: ${destination} (ID: ${subscriptionId})`);
      return subscriptionId;
    } catch (error) {
      logError(`구독 ${destination} 실패:`, error);
      return "";
    }
  };

  /**
   * 구독指定테마
   *
   * @param destination 대상테마주소（예：/topic/message）
   * @param callback 接收到메시지시의콜백함수
   * @returns 구독 ID，용도후续취소구독
   */
  const subscribe = (destination: string, callback: (message: IMessage) => void): string => {
    // 저장구독설정到등록테이블，용도断스레드재연결후자동복구
    subscriptionRegistry.set(destination, { destination, callback });

    // 만약이미연결，立即구독
    if (stompClient.value?.connected) {
      return performSubscribe(destination, callback);
    }

    log(`暂存구독설정: ${destination}，로에연결구축후자동구독`);
    return "";
  };

  /**
   * 취소구독
   *
   * @param subscriptionId 구독 ID（由 subscribe 메서드돌아가기）
   */
  const unsubscribe = (subscriptionId: string) => {
    const subscription = activeSubscriptions.get(subscriptionId);
    if (subscription) {
      try {
        subscription.unsubscribe();
        activeSubscriptions.delete(subscriptionId);
        log(`✓ 이미취소구독: ${subscriptionId}`);
      } catch (error) {
        logWarn(`취소구독 ${subscriptionId} 시出错:`, error);
      }
    }
  };

  /**
   * 취소指定테마의구독（从등록테이블내移除）
   *
   * @param destination 테마주소
   */
  const unsubscribeDestination = (destination: string) => {
    // 从등록테이블내移除
    subscriptionRegistry.delete(destination);

    // 취소모든일치该테마의活动구독
    for (const [id, subscription] of activeSubscriptions.entries()) {
      // 주의：STOMP 의 subscription 객체없음直接暴露 destination，
      // 这里简化처리，实际사용시可能필요해야额外维护 id -> destination 의매핑
      try {
        subscription.unsubscribe();
        activeSubscriptions.delete(id);
      } catch (error) {
        logWarn(`취소구독 ${id} 시出错:`, error);
      }
    }

    log(`✓ 이미移除테마구독설정: ${destination}`);
  };

  /**
   * 끊김 웹소켓 연결
   *
   * @param clearSubscriptions 여부정리除구독등록테이블（기본값로 true）
   */
  const disconnect = (clearSubscriptions = true) => {
    // 설정手动끊김标志
    isManualDisconnect = true;

    // 정리除모든定시기기
    clearAllTimers();

    // 취소모든活动구독
    for (const [id, subscription] of activeSubscriptions.entries()) {
      try {
        subscription.unsubscribe();
      } catch (error) {
        logWarn(`취소구독 ${id} 시出错:`, error);
      }
    }
    activeSubscriptions.clear();

    // 선택：정리除구독등록테이블
    if (clearSubscriptions) {
      subscriptionRegistry.clear();
      log("이미정리除모든구독설정");
    }

    // 끊김연결
    if (stompClient.value) {
      try {
        if (stompClient.value.connected || stompClient.value.active) {
          stompClient.value.deactivate();
          log("✓ 웹소켓 연결이미주요动끊김");
        }
      } catch (error) {
        logError("끊김 웹소켓 연결시出错:", error);
      }
      stompClient.value = null;
    }

    connectionState.value = ConnectionState.DISCONNECTED;
    reconnectAttempts.value = 0;
  };

  // ==================== 돌아가기公共인터페이스 ====================
  return {
    // 상태
    connectionState: readonly(connectionState),
    isConnected,
    reconnectAttempts: readonly(reconnectAttempts),

    // 연결관리
    connect,
    disconnect,

    // 구독관리
    subscribe,
    unsubscribe,
    unsubscribeDestination,

    // 통계계정보
    getActiveSubscriptionCount: () => activeSubscriptions.size,
    getRegisteredSubscriptionCount: () => subscriptionRegistry.size,
  };
}
