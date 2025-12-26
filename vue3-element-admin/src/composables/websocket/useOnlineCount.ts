import { 참조, watch, onMounted, onUnmounted, getCurrentInstance } from "vue";
import { useStomp } from "./useStomp";
import { registerWebSocketInstance } from "@/plugins/websocket";
import { AuthStorage } from "@/utils/auth";

/**
 * 온라인 사용자 수 메시지 구조
 */
interface OnlineCountMessage {
  count?: number;
  timestamp?: number;
}

/**
 * 전역 싱글톤 인스턴스
 */
let globalInstance: ReturnType<typeof createOnlineCountComposable> | null = null;

/**
 * 온라인 사용자 수 컴포저블 생성(내부 팩토리 함수)
 */
function createOnlineCountComposable() {
  // ==================== 상태 관리 ====================
  const onlineUserCount = 참조(0);
  const lastUpdateTime = 참조(0);

  // ==================== 웹소켓 클라이언트 ====================
  const stomp = useStomp({
    reconnectDelay: 15000,
    maxReconnectAttempts: 3,
    connectionTimeout: 10000,
    useExponentialBackoff: true,
    autoRe저장소Subscriptions: true, // 자동 구독 복구
    debug: false,
  });

  // 온라인 사용자 수 토픽
  const ONLINE_COUNT_TOPIC = "/topic/online-count";

  // 구독 ID
  let subscriptionId: string | null = null;

  // 전역 인스턴스 관리자에 등록
  registerWebSocketInstance("onlineCount", stomp);

  /**
   * 온라인 사용자 수 메시지 처리
   */
  const handleOnlineCountMessage = (message: any) => {
    try {
      const data = message.body;
      const jsonData = JSON.parse(data) as OnlineCountMessage;

      // 두 가지 메시지 형식 지원
      // 1. 직접 숫자: 42
      // 2. 객체 형식: { count: 42, timestamp: 1234567890 }
      const count = typeof jsonData === "number" ? jsonData : jsonData.count;

      if (count !== undefined && !isNaN(count)) {
        onlineUserCount.value = count;
        lastUpdateTime.value = Date.now();
        console.log(`[useOnlineCount] 온라인 사용자 수 업데이트: ${count}`);
      } else {
        console.warn("[useOnlineCount] 유효하지 않은 온라인 사용자 수 수신:", data);
      }
    } catch (error) {
      console.error("[useOnlineCount] 온라인 사용자 수 파싱 실패:", error);
    }
  };

  /**
   * 온라인 사용자 수 토픽 구독
   */
  const subscribeToOnlineCount = () => {
    if (subscriptionId) {
      console.log("[useOnlineCount] 이미 구독 존재, 건너뜀");
      return;
    }

    // 온라인 사용자 수 토픽 구독(useStomp가 재연결 후 구독 복구 처리)
    subscriptionId = stomp.subscribe(ONLINE_COUNT_TOPIC, handleOnlineCountMessage);

    if (subscriptionId) {
      console.log(`[useOnlineCount] 토픽 구독됨: ${ONLINE_COUNT_TOPIC}`);
    } else {
      console.log(`[useOnlineCount] 구독 설정 임시 저장, 연결 수립 후 자동 구독 대기`);
    }
  };

  /**
   * 웹소켓 연결 초기화 및 온라인 사용자 토픽 구독
   */
  const initialize = () => {
    // 웹소켓 엔드포인트 설정 여부 확인
    const wsEndpoint = import.meta.env.VITE_APP_WS_ENDPOINT;
    if (!wsEndpoint) {
      console.log("[useOnlineCount] 웹소켓 엔드포인트 미설정, 초기화 건너뜀");
      return;
    }

    // 토큰 유효성 확인
    const accessToken = AuthStorage.getAccessToken();
    if (!accessToken) {
      console.log("[useOnlineCount] 유효한 토큰 미감지, 초기화 건너뜀");
      return;
    }

    console.log("[useOnlineCount] 온라인 사용자 수 서비스 초기화...");

    // 웹소켓 연결 수립
    stomp.connect();

    // 토픽 구독
    subscribeToOnlineCount();
  };

  /**
   * 웹소켓 연결 닫기 및 리소스 정리
   */
  const cleanup = () => {
    console.log("[useOnlineCount] 온라인 사용자 수 서비스 정리...");

    // 구독 취소
    if (subscriptionId) {
      stomp.unsubscribe(subscriptionId);
      subscriptionId = null;
    }

    // 토픽 주소로도 구독 취소 가능
    stomp.unsubscribeDestination(ONLINE_COUNT_TOPIC);

    // 연결 해제
    stomp.disconnect();

    // 상태 초기화
    onlineUserCount.value = 0;
    lastUpdateTime.value = 0;
  };

  // 연결 상태 변경 감시
  watch(
    stomp.isConnected,
    (connected) => {
      if (connected) {
        console.log("[useOnlineCount] 웹소켓 연결됨");
      } else {
        console.log("[useOnlineCount] 웹소켓 연결 해제됨");
      }
    },
    { immediate: false }
  );

  return {
    // 상태
    onlineUserCount: readonly(onlineUserCount),
    lastUpdateTime: readonly(lastUpdateTime),
    isConnected: stomp.isConnected,
    connectionState: stomp.connectionState,

    // 메서드
    initialize,
    cleanup,

    // 별칭 메서드(하위 호환용)
    initWebSocket: initialize,
    closeWebSocket: cleanup,
  };
}

/**
 * 온라인 사용자 수 컴포저블(싱글톤 모드)
 *
 * 시스템 온라인 사용자 수 실시간 표시용
 *
 * @param options 설정 옵션
 * @param options.autoInit 컴포넌트 마운트 시 자동 초기화 여부(기본값 true)
 *
 * @example
 * ```ts
 * // 컴포넌트 내 사용
 * const { onlineUserCount, isConnected } = useOnlineCount();
 *
 * // 수동 초기화 제어
 * const { onlineUserCount, initialize, cleanup } = useOnlineCount({ autoInit: false });
 * onMounted(() => initialize());
 * onUnmounted(() => cleanup());
 * ```
 */
export function useOnlineCount(options: { autoInit?: boolean } = {}) {
  const { autoInit = true } = options;

  // 싱글톤 인스턴스 조회 또는 생성
  if (!globalInstance) {
    globalInstance = createOnlineCountComposable();
  }

  // 컴포넌트 컨텍스트 내이고 autoInit이 true일 때만 라이프사이클 훅 사용
  const instance = getCurrentInstance();
  if (autoInit && instance) {
    onMounted(() => {
      // 미연결 상태일 때만 초기화 시도
      if (!globalInstance!.isConnected.value) {
        console.log("[useOnlineCount] 컴포넌트 마운트, 웹소켓 연결 초기화");
        globalInstance!.initialize();
      } else {
        console.log("[useOnlineCount] 웹소켓 이미 연결됨, 초기화 건너뜀");
      }
    });

    // 주의: 언마운트 시 연결 닫지 않음, 전역 연결 유지
    onUnmounted(() => {
      console.log("[useOnlineCount] 컴포넌트 언마운트(웹소켓 연결 유지)");
    });
  }

  return globalInstance;
}
