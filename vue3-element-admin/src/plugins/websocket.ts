import { useDictSync } from "@/composables";
import { AuthStorage } from "@/utils/auth";
// 스토어 또는 userStore를 직접 가져오지 않음

// 글로벌 웹소켓 인스턴스 관리
const websocketInstances = new Map<string, any>();

// 중복 초기화 방지를 위한 상태 플래그
let isInitialized = false;
let dictWebSocketInstance: ReturnType<typeof useDictSync> | null = null;

/**
 * 웹소켓 인스턴스 등록
 */
export function registerWebSocketInstance(key: string, instance: any) {
  websocketInstances.set(key, instance);
  console.log(`[WebSocketPlugin] Registered WebSocket instance: ${key}`);
}

/**
 * 웹소켓 인스턴스 조회
 */
export function getWebSocketInstance(key: string) {
  return websocketInstances.get(key);
}

/**
 * 웹소켓 서비스 초기화
 */
export function setupWebSocket() {
  console.log("[WebSocketPlugin] 웹소켓 서비스 초기화 시작...");

  // 이미 초기화되었는지 확인
  if (isInitialized) {
    console.log("[WebSocketPlugin] 웹소켓 서비스 이미 초기화됨, 중복 초기화 건너뜀");
    return;
  }

  // 환경 변수 설정 여부 확인
  const wsEndpoint = import.meta.env.VITE_APP_WS_ENDPOINT;
  if (!wsEndpoint) {
    console.log("[WebSocketPlugin] 웹소켓 엔드포인트 미설정, 웹소켓 초기화 건너뜀");
    return;
  }

  // 이미 로그인했는지 확인 (액세스 토큰 존재 여부 기반)
  if (!AuthStorage.getAccessToken()) {
    console.warn(
      "[WebSocketPlugin] 액세스 토큰을 찾을 수 없음, 웹소켓 초기화 건너뜀. 사용자 로그인 후 자동 재연결됨."
    );
    return;
  }

  try {
    // 지연 초기화, 애플리케이션 완전 시작 보장
    setTimeout(() => {
      // 인스턴스 참조 저장
      dictWebSocketInstance = useDictSync();
      registerWebSocketInstance("dictSync", dictWebSocketInstance);

      // 사전 웹소켓 서비스 초기화
      dictWebSocketInstance.initWebSocket();
      console.log("[WebSocketPlugin] 사전 웹소켓 초기화 완료");

      // 온라인 사용자 수 웹소켓 초기화
      import("@/composables").then(({ useOnlineCount }) => {
        const onlineCountInstance = useOnlineCount({ autoInit: false });
        onlineCountInstance.initWebSocket();
        console.log("[WebSocketPlugin] 온라인 사용자 수 웹소켓 초기화 완료");
      });

      // 창 닫기 전 웹소켓 연결 해제
      window.addEventListener("beforeunload", handleWindowClose);

      console.log("[WebSocketPlugin] 웹소켓 서비스 초기화 완료");
      isInitialized = true;
    }, 1000); // 1초 지연 초기화
  } catch (error) {
    console.error("[WebSocketPlugin] 웹소켓 서비스 초기화 실패:", error);
  }
}

/**
 * 창 닫기 처리
 */
function handleWindowClose() {
  console.log("[WebSocketPlugin] 창 닫기 예정, 웹소켓 연결 해제");
  cleanupWebSocket();
}

/**
 * 웹소켓 연결 정리
 */
export function cleanupWebSocket() {
  // 사전 웹소켓 정리
  if (dictWebSocketInstance) {
    try {
      dictWebSocketInstance.closeWebSocket();
      console.log("[WebSocketPlugin] 사전 웹소켓 연결 해제됨");
    } catch (error) {
      console.error("[WebSocketPlugin] 사전 웹소켓 연결 해제 실패:", error);
    }
  }

  // 모든 등록된 웹소켓 인스턴스 정리
  websocketInstances.forEach((instance, key) => {
    try {
      if (instance && typeof instance.disconnect === "function") {
        instance.disconnect();
        console.log(`[WebSocketPlugin] ${key} 웹소켓 연결 해제됨`);
      } else if (instance && typeof instance.closeWebSocket === "function") {
        instance.closeWebSocket();
        console.log(`[WebSocketPlugin] ${key} 웹소켓 연결 해제됨`);
      }
    } catch (error) {
      console.error(`[WebSocketPlugin] ${key} 웹소켓 연결 해제 실패:`, error);
    }
  });

  // 인스턴스 맵 비우기
  websocketInstances.clear();

  // 이벤트 리스너 제거
  window.removeEventListener("beforeunload", handleWindowClose);

  // 상태 초기화
  dictWebSocketInstance = null;
  isInitialized = false;
}

/**
 * 웹소켓 재초기화 (로그인 후 재연결용)
 */
export function reinitializeWebSocket() {
  // 먼저 기존 연결 정리
  cleanupWebSocket();

  // 지연 후 재초기화
  setTimeout(() => {
    setupWebSocket();
  }, 500);
}
