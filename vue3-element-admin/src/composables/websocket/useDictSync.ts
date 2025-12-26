import { useDictStoreHook } from "@/store/modules/dict-store";
import { useStomp } from "./useStomp";
import type { IMessage } from "@stomp/stompjs";

/**
 * 사전 변경 메시지 구조
 */
export interface DictChangeMessage {
  /** 사전 코드 */
  dictCode: string;
  /** 타임스탬프 */
  timestamp: number;
}

/**
 * 사전 메시지 별칭(하위 호환용)
 */
export type DictMessage = DictChangeMessage;

/**
 * 사전 변경 이벤트 콜백 함수 타입
 */
export type DictChangeCallback = (message: DictChangeMessage) => void;

/**
 * 전역 싱글톤 인스턴스
 */
let singletonInstance: ReturnType<typeof createDictSyncComposable> | null = null;

/**
 * 사전 동기화 컴포저블 생성(내부 팩토리 함수)
 */
function createDictSyncComposable() {
  const dictStore = useDictStoreHook();

  // 최적화된 useStomp 사용
  const stomp = useStomp({
    reconnectDelay: 20000,
    connectionTimeout: 15000,
    useExponentialBackoff: false,
    maxReconnectAttempts: 3,
    autoRe저장소Subscriptions: true, // 자동 구독 복구
    debug: false,
  });

  // 사전 토픽 주소
  const DICT_TOPIC = "/topic/dict";

  // 메시지 콜백 함수 목록
  const messageCallbacks = 참조<DictChangeCallback[]>([]);

  // 구독 ID(구독 취소용)
  let subscriptionId: string | null = null;

  /**
   * 사전 변경 이벤트 처리
   */
  const handleDictChangeMessage = (message: IMessage) => {
    if (!message.body) {
      return;
    }

    try {
      const data = JSON.parse(message.body) as DictChangeMessage;
      const { dictCode } = data;

      if (!dictCode) {
        console.warn("[DictSync] 유효하지 않은 사전 변경 메시지 수신: dictCode 누락");
        return;
      }

      console.log(`[DictSync] 사전 "${dictCode}" 업데이트됨, 로컬 캐시 정리`);

      // 캐시 정리, 필요시 로드 대기
      dictStore.removeDictItem(dictCode);

      // 모든 등록된 콜백 함수 실행
      messageCallbacks.value.forEach((callback) => {
        try {
          callback(data);
        } catch (error) {
          console.error("[DictSync] 콜백 함수 실행 실패:", error);
        }
      });
    } catch (error) {
      console.error("[DictSync] 사전 변경 메시지 파싱 실패:", error);
    }
  };

  /**
   * 웹소켓 연결 초기화 및 사전 토픽 구독
   */
  const initialize = () => {
    // 웹소켓 엔드포인트 설정 여부 확인
    const wsEndpoint = import.meta.env.VITE_APP_WS_ENDPOINT;
    if (!wsEndpoint) {
      console.log("[DictSync] 웹소켓 엔드포인트 미설정, 사전 동기화 기능 건너뜀");
      return;
    }

    console.log("[DictSync] 사전 동기화 서비스 초기화...");

    // 웹소켓 연결 수립
    stomp.connect();

    // 사전 토픽 구독(useStomp가 재연결 후 구독 복구 자동 처리)
    subscriptionId = stomp.subscribe(DICT_TOPIC, handleDictChangeMessage);

    if (subscriptionId) {
      console.log(`[DictSync] 사전 토픽 구독됨: ${DICT_TOPIC}`);
    } else {
      console.log(`[DictSync] 사전 토픽 구독 임시 저장, 연결 수립 후 자동 구독 대기`);
    }
  };

  /**
   * 웹소켓 연결 닫기 및 리소스 정리
   */
  const cleanup = () => {
    console.log("[DictSync] 사전 동기화 서비스 정리...");

    // 구독 취소(있는 경우)
    if (subscriptionId) {
      stomp.unsubscribe(subscriptionId);
      subscriptionId = null;
    }

    // 토픽 주소로도 구독 취소 가능
    stomp.unsubscribeDestination(DICT_TOPIC);

    // 연결 해제
    stomp.disconnect();

    // 콜백 목록 비우기
    messageCallbacks.value = [];
  };

  /**
   * 사전 변경 콜백 함수 등록
   *
   * @param callback 콜백 함수
   * @returns 등록 취소 함수 반환
   */
  const onDictChange = (callback: DictChangeCallback) => {
    messageCallbacks.value.push(callback);

    // 등록 취소 함수 반환
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

    // 별칭 메서드(하위 호환용)
    initWebSocket: initialize,
    closeWebSocket: cleanup,
    onDictMessage: onDictChange,

    // 테스트 및 디버그용
    handleDictChangeMessage,
  };
}

/**
 * 사전 동기화 컴포저블(싱글톤 모드)
 *
 * 백엔드 사전 변경 감시 및 프론트엔드 캐시 자동 동기화용
 *
 * @example
 * ```ts
 * const dictSync = useDictSync();
 *
 * // 초기화(앱 시작 시 호출)
 * dictSync.initialize();
 *
 * // 콜백 등록
 * const unsubscribe = dictSync.onDictChange((message) => {
 *   console.log('사전 업데이트됨:', message.dictCode);
 * });
 *
 * // 등록 취소
 * unsubscribe();
 *
 * // 정리(앱 종료 시 호출)
 * dictSync.cleanup();
 * ```
 */
export function useDictSync() {
  if (!singletonInstance) {
    singletonInstance = createDictSyncComposable();
  }
  return singletonInstance;
}
