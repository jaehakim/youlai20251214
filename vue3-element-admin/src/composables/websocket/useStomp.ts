import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { AuthStorage } from "@/utils/auth";

export interface UseStompOptions {
  /** 웹소켓 地址，不传시사용 VITE_APP_WS_ENDPOINT 环境변수 */
  brokerURL?: string;
  /** 용도鉴权의 token，不传시사용 getAccessToken() 의반환값 */
  token?: string;
  /** 重连지연，단일位毫秒，기본값로 15000 */
  reconnectDelay?: number;
  /** 연결시간초과시사이，단일位毫秒，기본값로 10000 */
  connectionTimeout?: number;
  /** 是否开启指개退避重连정책 */
  useExponentialBackoff?: boolean;
  /** 最大重连次개，기본값로 3 */
  maxReconnectAttempts?: number;
  /** 最大重连지연，단일位毫秒，기본값로 60000 */
  maxReconnectDelay?: number;
  /** 是否开启调试日志 */
  debug?: boolean;
  /** 是否에重连시자동恢复구독，기본값로 true */
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
 * STOMP 웹소켓 연결管理조합式함수
 *
 * 核心功能：
 * - 자동연결管理（연결、断开、重连）
 * - 구독管理（구독、취소구독、자동恢复）
 * - 心점프检测
 * - Token 자동새로고침
 *
 * @param options 설정옵션
 * @returns STOMP 客户端작업인터페이스
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

  // ==================== 定시器管理 ====================
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  let connectionTimeoutTimer: ReturnType<typeof setTimeout> | null = null;

  // ==================== 구독管理 ====================
  // 活动구독：存储当前 STOMP 구독객체
  const activeSubscriptions = new Map<string, StompSubscription>();
  // 구독설정등록表：용도자동恢复구독
  const subscriptionRegistry = new Map<string, SubscriptionConfig>();

  // ==================== 客户端实例 ====================
  const stompClient = 참조<Client | null>(null);
  let isManualDisconnect = false;

  // ==================== 工具함수 ====================

  /**
   * 清理모든定시器
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
   * 日志输出（支持调试模式控制）
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
   * 恢复모든구독
   */
  const re저장소Subscriptions = () => {
    if (!config.autoRe저장소Subscriptions || subscriptionRegistry.size === 0) {
      return;
    }

    log(`开始恢复 ${subscriptionRegistry.size} 개구독...`);

    for (const [destination, subscriptionConfig] of subscriptionRegistry.entries()) {
      try {
        performSubscribe(destination, subscriptionConfig.callback);
      } catch (error) {
        logError(`恢复구독 ${destination} 실패:`, error);
      }
    }
  };

  /**
   * 초기화 STOMP 客户端
   */
  const initializeClient = () => {
    // 만약客户端已存에且处于活动상태，直接돌아가기
    if (stompClient.value && (stompClient.value.active || stompClient.value.connected)) {
      log("STOMP 客户端已存에且处于活动상태，점프거치초기화");
      return;
    }

    // 检查 웹소켓 엔드포인트是否설정
    if (!config.brokerURL.value) {
      logWarn("웹소켓 연결실패: 미설정 웹소켓 엔드포인트 URL");
      return;
    }

    // 每次연결前重新조회最新令牌
    const accessToken = AuthStorage.getAccessToken();
    if (!accessToken) {
      logWarn("웹소켓 연결실패：授权令牌로비어있음，요청先登录");
      return;
    }

    // 清理旧客户端
    if (stompClient.value) {
      try {
        stompClient.value.deactivate();
      } catch (error) {
        logWarn("清理旧客户端시出错:", error);
      }
      stompClient.value = null;
    }

    // 创建 STOMP 客户端
    stompClient.value = new Client({
      brokerURL: config.brokerURL.value,
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      debug: config.debug ? (msg) => console.log("[STOMP]", msg) : () => {},
      reconnectDelay: 0, // 비활성화内置重连，사용사용자 정의重连逻辑
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    // ==================== 이벤트 리스닝器 ====================

    // 연결성공
    stompClient.value.onConnect = () => {
      connectionState.value = ConnectionState.CONNECTED;
      reconnectAttempts.value = 0;
      clearAllTimers();

      log("✅ 웹소켓 연결已建立");

      // 자동恢复구독
      re저장소Subscriptions();
    };

    // 연결断开
    stompClient.value.onDisconnect = () => {
      connectionState.value = ConnectionState.DISCONNECTED;
      log("❌ 웹소켓 연결已断开");

      // 清비어있음活动구독（但보유구독설정용도恢复）
      activeSubscriptions.clear();

      // 만약不是手动断开且미达到最大重连次개，그러면尝试重连
      if (!isManualDisconnect && reconnectAttempts.value < config.maxReconnectAttempts) {
        scheduleReconnect();
      }
    };

    // 웹소켓 닫기
    stompClient.value.on웹소켓Close = (event) => {
      connectionState.value = ConnectionState.DISCONNECTED;
      log(`웹소켓 已닫기: code=${event?.code}, reason=${event?.reason}`);

      // 만약是手动断开，不重连
      if (isManualDisconnect) {
        log("手动断开연결，不进行重连");
        return;
      }

      // 对于예외닫기，尝试重连
      if (
        event?.code &&
        [1000, 1006, 1008, 1011].includes(event.code) &&
        reconnectAttempts.value < config.maxReconnectAttempts
      ) {
        log("检测到연결예외닫기，로尝试重连");
        scheduleReconnect();
      }
    };

    // STOMP 오류
    stompClient.value.onStompError = (frame) => {
      logError("STOMP 오류:", frame.headers, frame.body);
      connectionState.value = ConnectionState.DISCONNECTED;

      // 检查是否是授权오류
      const isAuthError =
        frame.headers?.message?.includes("Unauthorized") ||
        frame.body?.includes("Unauthorized") ||
        frame.body?.includes("Token") ||
        frame.body?.includes("401");

      if (isAuthError) {
        logWarn("웹소켓 授权오류，停止重连");
        isManualDisconnect = true; // 授权오류不进行重连
      }
    };
  };

  /**
   * 调度重连任务
   */
  const scheduleReconnect = () => {
    // 만약正에연결或手动断开，不重连
    if (connectionState.value === ConnectionState.CONNECTING || isManualDisconnect) {
      return;
    }

    // 检查是否达到最大重连次개
    if (reconnectAttempts.value >= config.maxReconnectAttempts) {
      logError(`已达到最大重连次개 (${config.maxReconnectAttempts})，停止重连`);
      return;
    }

    reconnectAttempts.value++;
    connectionState.value = ConnectionState.RECONNECTING;

    // 계算重连지연（支持指개退避）
    const delay = config.useExponentialBackoff
      ? Math.min(
          config.reconnectDelay * Math.pow(2, reconnectAttempts.value - 1),
          config.maxReconnectDelay
        )
      : config.reconnectDelay;

    log(`准备重连 (${reconnectAttempts.value}/${config.maxReconnectAttempts})，지연 ${delay}ms`);

    // 清除之前의重连계시器
    if (reconnectTimer) {
      clearTimeout(reconnectTimer);
    }

    // 설정重连계시器
    reconnectTimer = setTimeout(() => {
      if (connectionState.value !== ConnectionState.CONNECTED && !isManualDisconnect) {
        log(`开始第 ${reconnectAttempts.value} 次重连...`);
        connect();
      }
    }, delay);
  };

  // 리스닝 brokerURL 의变化，자동重新초기화
  watch(config.brokerURL, (newURL, oldURL) => {
    if (newURL !== oldURL) {
      log(`웹소켓 엔드포인트已更改: ${oldURL} -> ${newURL}`);

      // 断开当前연결
      if (stompClient.value && stompClient.value.connected) {
        stompClient.value.deactivate();
      }

      // 重新초기화客户端
      initializeClient();
    }
  });

  // 초기화客户端
  initializeClient();

  // ==================== 公共인터페이스 ====================

  /**
   * 建立 웹소켓 연결
   */
  const connect = () => {
    // 초기화手动断开标志
    isManualDisconnect = false;

    // 检查是否설정됨 웹소켓 엔드포인트
    if (!config.brokerURL.value) {
      logError("웹소켓 연결실패: 미설정 웹소켓 엔드포인트 URL");
      return;
    }

    // 방지重复연결
    if (connectionState.value === ConnectionState.CONNECTING) {
      log("웹소켓 正에연결내，점프거치重复연결요청");
      return;
    }

    // 만약客户端不存에，先초기화
    if (!stompClient.value) {
      initializeClient();
    }

    if (!stompClient.value) {
      logError("STOMP 客户端초기화실패");
      return;
    }

    // 避免重复연결：检查是否已연결
    if (stompClient.value.connected) {
      log("웹소켓 已연결，점프거치重复연결");
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

        // 시간초과후尝试重连
        if (!isManualDisconnect && reconnectAttempts.value < config.maxReconnectAttempts) {
          scheduleReconnect();
        }
      }
    }, config.connectionTimeout);

    try {
      stompClient.value.activate();
      log("正에建立 웹소켓 연결...");
    } catch (error) {
      logError("激活 웹소켓 연결실패:", error);
      connectionState.value = ConnectionState.DISCONNECTED;
    }
  };

  /**
   * 실행구독작업（내부메서드）
   */
  const performSubscribe = (destination: string, callback: (message: IMessage) => void): string => {
    if (!stompClient.value || !stompClient.value.connected) {
      logWarn(`尝试구독 ${destination} 실패: 客户端미연결`);
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
   * @param destination 대상테마地址（如：/topic/message）
   * @param callback 接收到메시지시의콜백함수
   * @returns 구독 ID，용도후续취소구독
   */
  const subscribe = (destination: string, callback: (message: IMessage) => void): string => {
    // 저장구독설정到등록表，용도断스레드重连후자동恢复
    subscriptionRegistry.set(destination, { destination, callback });

    // 만약已연결，立即구독
    if (stompClient.value?.connected) {
      return performSubscribe(destination, callback);
    }

    log(`暂存구독설정: ${destination}，로에연결建立후자동구독`);
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
        log(`✓ 已취소구독: ${subscriptionId}`);
      } catch (error) {
        logWarn(`취소구독 ${subscriptionId} 시出错:`, error);
      }
    }
  };

  /**
   * 취소指定테마의구독（从등록表내移除）
   *
   * @param destination 테마地址
   */
  const unsubscribeDestination = (destination: string) => {
    // 从등록表내移除
    subscriptionRegistry.delete(destination);

    // 취소모든일치该테마의活动구독
    for (const [id, subscription] of activeSubscriptions.entries()) {
      // 注意：STOMP 의 subscription 객체없음直接暴露 destination，
      // 这里简化처리，实际사용시可能필요해야额外维护 id -> destination 의매핑
      try {
        subscription.unsubscribe();
        activeSubscriptions.delete(id);
      } catch (error) {
        logWarn(`취소구독 ${id} 시出错:`, error);
      }
    }

    log(`✓ 已移除테마구독설정: ${destination}`);
  };

  /**
   * 断开 웹소켓 연결
   *
   * @param clearSubscriptions 是否清除구독등록表（기본값로 true）
   */
  const disconnect = (clearSubscriptions = true) => {
    // 설정手动断开标志
    isManualDisconnect = true;

    // 清除모든定시器
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

    // 可选：清除구독등록表
    if (clearSubscriptions) {
      subscriptionRegistry.clear();
      log("已清除모든구독설정");
    }

    // 断开연결
    if (stompClient.value) {
      try {
        if (stompClient.value.connected || stompClient.value.active) {
          stompClient.value.deactivate();
          log("✓ 웹소켓 연결已주요动断开");
        }
      } catch (error) {
        logError("断开 웹소켓 연결시出错:", error);
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

    // 연결管理
    connect,
    disconnect,

    // 구독管理
    subscribe,
    unsubscribe,
    unsubscribeDestination,

    // 统계정보
    getActiveSubscriptionCount: () => activeSubscriptions.size,
    getRegisteredSubscriptionCount: () => subscriptionRegistry.size,
  };
}
