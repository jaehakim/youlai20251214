import { 참조, watch, onMounted, onUnmounted, getCurrentInstance } from "vue";
import { useStomp } from "./useStomp";
import { register웹소켓Instance } from "@/plugins/websocket";
import { AuthStorage } from "@/utils/auth";

/**
 * 에스레드사용자개양메시지结构
 */
interface OnlineCountMessage {
  count?: number;
  timestamp?: number;
}

/**
 * 글로벌단일例实例
 */
let globalInstance: ReturnType<typeof createOnlineCountComposable> | null = null;

/**
 * 생성에스레드사용자계배열合式함수（내부工厂함수）
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
    autoRe저장소Subscriptions: true, // 자동복구구독
    debug: false,
  });

  // 에스레드사용자계개테마
  const ONLINE_COUNT_TOPIC = "/topic/online-count";

  // 구독 ID
  let subscriptionId: string | null = null;

  // 등록到글로벌实例관리기기
  register웹소켓Instance("onlineCount", stomp);

  /**
   * 처리에스레드사용자개양메시지
   */
  const handleOnlineCountMessage = (message: any) => {
    try {
      const data = message.body;
      const jsonData = JSON.parse(data) as OnlineCountMessage;

      // 지원两种메시지형식
      // 1. 直接예숫자: 42
      // 2. 객체형식: { count: 42, timestamp: 1234567890 }
      const count = typeof jsonData === "number" ? jsonData : jsonData.count;

      if (count !== undefined && !isNaN(count)) {
        onlineUserCount.value = count;
        lastUpdateTime.value = Date.now();
        console.log(`[useOnlineCount] 에스레드사용자개업데이트: ${count}`);
      } else {
        console.warn("[useOnlineCount] 收到无效의에스레드사용자개:", data);
      }
    } catch (error) {
      console.error("[useOnlineCount] 파싱에스레드사용자개실패:", error);
    }
  };

  /**
   * 구독에스레드사용자계개테마
   */
  const subscribeToOnlineCount = () => {
    if (subscriptionId) {
      console.log("[useOnlineCount] 이미存에구독，점프거치");
      return;
    }

    // 구독에스레드사용자계개테마（useStomp 회의처리재연결후의구독복구）
    subscriptionId = stomp.subscribe(ONLINE_COUNT_TOPIC, handleOnlineCountMessage);

    if (subscriptionId) {
      console.log(`[useOnlineCount] 이미구독테마: ${ONLINE_COUNT_TOPIC}`);
    } else {
      console.log(`[useOnlineCount] 暂存구독설정，대기연결구축후자동구독`);
    }
  };

  /**
   * 초기화 웹소켓 연결그리고구독에스레드사용자테마
   */
  const initialize = () => {
    // 확인 웹소켓 엔드포인트여부설정
    const wsEndpoint = import.meta.env.VITE_APP_WS_ENDPOINT;
    if (!wsEndpoint) {
      console.log("[useOnlineCount] 미설정 웹소켓 엔드포인트，점프거치초기화");
      return;
    }

    // 확인令牌유효性
    const accessToken = AuthStorage.getAccessToken();
    if (!accessToken) {
      console.log("[useOnlineCount] 미检测到유효令牌，점프거치초기화");
      return;
    }

    console.log("[useOnlineCount] 초기화에스레드사용자계개서비스...");

    // 구축 웹소켓 연결
    stomp.connect();

    // 구독테마
    subscribeToOnlineCount();
  };

  /**
   * 닫기 웹소켓 연결그리고정리资源
   */
  const cleanup = () => {
    console.log("[useOnlineCount] 정리에스레드사용자계개서비스...");

    // 취소구독
    if (subscriptionId) {
      stomp.unsubscribe(subscriptionId);
      subscriptionId = null;
    }

    // 也可以通거치테마주소취소구독
    stomp.unsubscribeDestination(ONLINE_COUNT_TOPIC);

    // 끊김연결
    stomp.disconnect();

    // 초기화상태
    onlineUserCount.value = 0;
    lastUpdateTime.value = 0;
  };

  // 리스닝연결상태변경
  watch(
    stomp.isConnected,
    (connected) => {
      if (connected) {
        console.log("[useOnlineCount] 웹소켓 이미연결");
      } else {
        console.log("[useOnlineCount] 웹소켓 이미끊김");
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

    // 别이름메서드（에후兼容）
    init웹소켓: initialize,
    close웹소켓: cleanup,
  };
}

/**
 * 에스레드사용자계배열合式함수（단일例모드）
 *
 * 용도实시표시시스템에스레드사용자개양
 *
 * @param options 설정옵션
 * @param options.autoInit 여부에컴포넌트마운트시자동초기화（기본값 true）
 *
 * @example
 * ```ts
 * // 에컴포넌트내사용
 * const { onlineUserCount, isConnected } = useOnlineCount();
 *
 * // 手动控制초기화
 * const { onlineUserCount, initialize, cleanup } = useOnlineCount({ autoInit: false });
 * onMounted(() => initialize());
 * onUnmounted(() => cleanup());
 * ```
 */
export function useOnlineCount(options: { autoInit?: boolean } = {}) {
  const { autoInit = true } = options;

  // 조회또는생성단일例实例
  if (!globalInstance) {
    globalInstance = createOnlineCountComposable();
  }

  // 오직에컴포넌트上下文내且 autoInit 로 true 시사용생명 주기훅
  const instance = getCurrentInstance();
  if (autoInit && instance) {
    onMounted(() => {
      // 오직있음에미연결시才尝试초기화
      if (!globalInstance!.isConnected.value) {
        console.log("[useOnlineCount] 컴포넌트마운트，초기화 웹소켓 연결");
        globalInstance!.initialize();
      } else {
        console.log("[useOnlineCount] 웹소켓 이미연결，점프거치초기화");
      }
    });

    // 주의：아님에언마운트시닫기연결，保持글로벌연결
    onUnmounted(() => {
      console.log("[useOnlineCount] 컴포넌트언마운트（保持 웹소켓 연결）");
    });
  }

  return globalInstance;
}
