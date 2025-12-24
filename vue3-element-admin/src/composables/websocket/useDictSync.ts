import { useDictStoreHook } from "@/저장소/modules/dict-저장소";
import { useStomp } from "./useStomp";
import type { IMessage } from "@stomp/stompjs";

/**
 * 사전변경메시지结构
 */
export interface DictChangeMessage {
  /** 사전인코딩 */
  dictCode: string;
  /** 시사이戳 */
  timestamp: number;
}

/**
 * 사전메시지别이름（에후兼容）
 */
export type DictMessage = DictChangeMessage;

/**
 * 사전변경이벤트콜백함수타입
 */
export type DictChangeCallback = (message: DictChangeMessage) => void;

/**
 * 全局단일例实例
 */
let singletonInstance: ReturnType<typeof createDictSyncComposable> | null = null;

/**
 * 创建사전동기조합式함수（내부工厂함수）
 */
function createDictSyncComposable() {
  const dictStore = useDictStoreHook();

  // 사용优化후의 useStomp
  const stomp = useStomp({
    reconnectDelay: 20000,
    connectionTimeout: 15000,
    useExponentialBackoff: false,
    maxReconnectAttempts: 3,
    autoRe저장소Subscriptions: true, // 자동恢复구독
    debug: false,
  });

  // 사전테마地址
  const DICT_TOPIC = "/topic/dict";

  // 메시지콜백함수목록
  const messageCallbacks = 참조<DictChangeCallback[]>([]);

  // 구독 ID（용도취소구독）
  let subscriptionId: string | null = null;

  /**
   * 처리사전변경이벤트
   */
  const handleDictChangeMessage = (message: IMessage) => {
    if (!message.body) {
      return;
    }

    try {
      const data = JSON.parse(message.body) as DictChangeMessage;
      const { dictCode } = data;

      if (!dictCode) {
        console.warn("[DictSync] 收到无效의사전변경메시지：缺少 dictCode");
        return;
      }

      console.log(`[DictSync] 사전 "${dictCode}" 已업데이트，清除本地캐시`);

      // 清除캐시，대기按필요加载
      dictStore.removeDictItem(dictCode);

      // 실행모든등록의콜백함수
      messageCallbacks.value.forEach((callback) => {
        try {
          callback(data);
        } catch (error) {
          console.error("[DictSync] 콜백함수실행실패:", error);
        }
      });
    } catch (error) {
      console.error("[DictSync] 파싱사전변경메시지실패:", error);
    }
  };

  /**
   * 초기화 웹소켓 연결并구독사전테마
   */
  const initialize = () => {
    // 检查是否설정됨 웹소켓 엔드포인트
    const wsEndpoint = import.meta.env.VITE_APP_WS_ENDPOINT;
    if (!wsEndpoint) {
      console.log("[DictSync] 미설정 웹소켓 엔드포인트，점프거치사전동기功能");
      return;
    }

    console.log("[DictSync] 초기화사전동기서비스...");

    // 建立 웹소켓 연결
    stomp.connect();

    // 구독사전테마（useStomp 会자동처리重连후의구독恢复）
    subscriptionId = stomp.subscribe(DICT_TOPIC, handleDictChangeMessage);

    if (subscriptionId) {
      console.log(`[DictSync] 已구독사전테마: ${DICT_TOPIC}`);
    } else {
      console.log(`[DictSync] 暂存사전테마구독，대기연결建立후자동구독`);
    }
  };

  /**
   * 닫기 웹소켓 연결并清理资源
   */
  const cleanup = () => {
    console.log("[DictSync] 清理사전동기서비스...");

    // 취소구독（만약有의话）
    if (subscriptionId) {
      stomp.unsubscribe(subscriptionId);
      subscriptionId = null;
    }

    // 也可以通거치테마地址취소구독
    stomp.unsubscribeDestination(DICT_TOPIC);

    // 断开연결
    stomp.disconnect();

    // 清비어있음콜백목록
    messageCallbacks.value = [];
  };

  /**
   * 등록사전변경콜백함수
   *
   * @param callback 콜백함수
   * @returns 돌아가기하나개취소등록의함수
   */
  const onDictChange = (callback: DictChangeCallback) => {
    messageCallbacks.value.push(callback);

    // 돌아가기취소등록의함수
    return () => {
      const index = messageCallbacks.value.indexOf(callback);
      if (index !== -1) {
        messageCallbacks.value.splice(index, 1);
      }
    };
  };

  return {
    // 상태
    isConnected: stomp.isConnected,
    connectionState: stomp.connectionState,

    // 메서드
    initialize,
    cleanup,
    onDictChange,

    // 别이름메서드（에후兼容）
    init웹소켓: initialize,
    close웹소켓: cleanup,
    onDictMessage: onDictChange,

    // 용도测试및调试
    handleDictChangeMessage,
  };
}

/**
 * 사전동기조합式함수（단일例模式）
 *
 * 용도리스닝백엔드사전변경并자동동기到프론트엔드캐시
 *
 * @example
 * ```ts
 * const dictSync = useDictSync();
 *
 * // 초기화（에应用启动시호출）
 * dictSync.initialize();
 *
 * // 등록콜백
 * const unsubscribe = dictSync.onDictChange((message) => {
 *   console.log('사전已업데이트:', message.dictCode);
 * });
 *
 * // 취소등록
 * unsubscribe();
 *
 * // 清理（에应用退出시호출）
 * dictSync.cleanup();
 * ```
 */
export function useDictSync() {
  if (!singletonInstance) {
    singletonInstance = createDictSyncComposable();
  }
  return singletonInstance;
}
