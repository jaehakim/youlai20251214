import { 참조, watch, onMounted, onUnmounted, getCurrentInstance } from "vue";
import { useStomp } from "./useStomp";
import { register웹소켓Instance } from "@/plugins/websocket";
import { AuthStorage } from "@/utils/auth";

/**
 * 에스레드사용자개量메시지结构
 */
interface OnlineCountMessage {
  count?: number;
  timestamp?: number;
}

/**
 * 全局단일例实例
 */
let globalInstance: ReturnType<typeof createOnlineCountComposable> | null = null;

/**
 * 创建에스레드사용자계배열合式함수（내부工厂함수）
 */
function createOnlineCountComposable() {
  // ==================== 상태 관리 ====================
  const onlineUserCount = 참조(0);
  const lastUpdateTime = 참조(0);

  // ==================== 웹소켓 客户端 ====================
  const stomp = useStomp({
    reconnectDelay: 15000,
    maxReconnectAttempts: 3,
    connectionTimeout: 10000,
    useExponentialBackoff: true,
    autoRe저장소Subscriptions: true, // 자동恢复구독
    debug: false,
  });

  // 에스레드사용자계개테마
  const ONLINE_COUNT_TOPIC = "/topic/online-count";

  // 구독 ID
  let subscriptionId: string | null = null;

  // 등록到全局实例管理器
  register웹소켓Instance("onlineCount", stomp);

  /**
   * 처리에스레드사용자개量메시지
   */
  const handleOnlineCountMessage = (message: any) => {
    try {
      const data = message.body;
      const jsonData = JSON.parse(data) as OnlineCountMessage;

      // 支持两种메시지格式
      // 1. 直接是숫자: 42
      // 2. 객체格式: { count: 42, timestamp: 1234567890 }
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
      console.log("[useOnlineCount] 已存에구독，점프거치");
      return;
    }

    // 구독에스레드사용자계개테마（useStomp 会처리重连후의구독恢复）
    subscriptionId = stomp.subscribe(ONLINE_COUNT_TOPIC, handleOnlineCountMessage);

    if (subscriptionId) {
      console.log(`[useOnlineCount] 已구독테마: ${ONLINE_COUNT_TOPIC}`);
    } else {
      console.log(`[useOnlineCount] 暂存구독설정，대기연결建立후자동구독`);
    }
  };

  /**
   * 초기화 웹소켓 연결并구독에스레드사용자테마
   */
  const initialize = () => {
    // 检查 웹소켓 엔드포인트是否설정
    const wsEndpoint = import.meta.env.VITE_APP_WS_ENDPOINT;
    if (!wsEndpoint) {
      console.log("[useOnlineCount] 미설정 웹소켓 엔드포인트，점프거치초기화");
      return;
    }

    // 检查令牌유효性
    const accessToken = AuthStorage.getAccessToken();
    if (!accessToken) {
      console.log("[useOnlineCount] 미检测到유효令牌，점프거치초기화");
      return;
    }

    console.log("[useOnlineCount] 초기화에스레드사용자계개서비스...");

    // 建立 웹소켓 연결
    stomp.connect();

    // 구독테마
    subscribeToOnlineCount();
  };

  /**
   * 닫기 웹소켓 연결并清理资源
   */
  const cleanup = () => {
    console.log("[useOnlineCount] 清理에스레드사용자계개서비스...");

    // 취소구독
    if (subscriptionId) {
      stomp.unsubscribe(subscriptionId);
      subscriptionId = null;
    }

    // 也可以通거치테마地址취소구독
    stomp.unsubscribeDestination(ONLINE_COUNT_TOPIC);

    // 断开연결
    stomp.disconnect();

    // 초기화상태
    onlineUserCount.value = 0;
    lastUpdateTime.value = 0;
  };

  // 리스닝연결상태变化
  watch(
    stomp.isConnected,
    (connected) => {
      if (connected) {
        console.log("[useOnlineCount] 웹소켓 已연결");
      } else {
        console.log("[useOnlineCount] 웹소켓 已断开");
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
 * 에스레드사용자계배열合式함수（단일例模式）
 *
 * 용도实시显示系统에스레드사용자개量
 *
 * @param options 설정옵션
 * @param options.autoInit 是否에컴포넌트마운트시자동초기화（기본값 true）
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

  // 조회或创建단일例实例
  if (!globalInstance) {
    globalInstance = createOnlineCountComposable();
  }

  // 오직에컴포넌트上下文내且 autoInit 로 true 시사용생명 주기훅
  const instance = getCurrentInstance();
  if (autoInit && instance) {
    onMounted(() => {
      // 오직有에미연결시才尝试초기화
      if (!globalInstance!.isConnected.value) {
        console.log("[useOnlineCount] 컴포넌트마운트，초기화 웹소켓 연결");
        globalInstance!.initialize();
      } else {
        console.log("[useOnlineCount] 웹소켓 已연결，점프거치초기화");
      }
    });

    // 注意：不에언마운트시닫기연결，保持全局연결
    onUnmounted(() => {
      console.log("[useOnlineCount] 컴포넌트언마운트（保持 웹소켓 연결）");
    });
  }

  return globalInstance;
}
