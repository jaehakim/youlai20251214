import { useDictSync } from "@/composables";
import { AuthStorage } from "@/utils/auth";
// 아님直接가져오기 저장소 또는 user스토어

// 글로벌 웹소켓 实例관리
const websocketInstances = new Map<string, any>();

// 용도방지중복초기화의상태标记
let isInitialized = false;
let dict웹소켓Instance: ReturnType<typeof useDictSync> | null = null;

/**
 * 등록 웹소켓 实例
 */
export function register웹소켓Instance(키: string, instance: any) {
  websocketInstances.set(키, instance);
  console.log(`[웹소켓Plugin] Registered 웹소켓 instance: ${키}`);
}

/**
 * 조회 웹소켓 实例
 */
export function get웹소켓Instance(키: string) {
  return websocketInstances.get(키);
}

/**
 * 초기화웹소켓서비스
 */
export function setup웹소켓() {
  console.log("[웹소켓Plugin] 시작초기화웹소켓서비스...");

  // 확인여부이미经초기화
  if (isInitialized) {
    console.log("[웹소켓Plugin] 웹소켓서비스이미经초기화,점프거치중복초기화");
    return;
  }

  // 확인环境변수여부설정
  const wsEndpoint = import.meta.env.VITE_APP_WS_ENDPOINT;
  if (!wsEndpoint) {
    console.log("[웹소켓Plugin] 미설정웹소켓엔드포인트,점프거치웹소켓초기화");
    return;
  }

  // 확인여부이미로그인（基于여부存에访问令牌）
  if (!AuthStorage.getAccessToken()) {
    console.warn(
      "[웹소켓Plugin] 미找到访问令牌，웹소켓초기화이미점프거치。사용자로그인후로자동다시연결。"
    );
    return;
  }

  try {
    // 지연초기화，보장应用完全启动
    setTimeout(() => {
      // 저장实例引用
      dict웹소켓Instance = useDictSync();
      register웹소켓Instance("dictSync", dict웹소켓Instance);

      // 초기화사전웹소켓서비스
      dict웹소켓Instance.init웹소켓();
      console.log("[웹소켓Plugin] 사전웹소켓초기화완료");

      // 초기화에스레드사용자계개웹소켓
      import("@/composables").then(({ useOnlineCount }) => {
        const onlineCountInstance = useOnlineCount({ autoInit: false });
        onlineCountInstance.init웹소켓();
        console.log("[웹소켓Plugin] 에스레드사용자계개웹소켓초기화완료");
      });

      // 에창닫기前끊김웹소켓연결
      window.addEventListener("beforeunload", handleWindowClose);

      console.log("[웹소켓Plugin] 웹소켓서비스초기화완료");
      isInitialized = true;
    }, 1000); // 지연1秒초기화
  } catch (error) {
    console.error("[웹소켓Plugin] 초기화웹소켓서비스실패:", error);
  }
}

/**
 * 처리창닫기
 */
function handleWindowClose() {
  console.log("[웹소켓Plugin] 창即로닫기，끊김웹소켓연결");
  cleanup웹소켓();
}

/**
 * 정리웹소켓연결
 */
export function cleanup웹소켓() {
  // 정리사전 웹소켓
  if (dict웹소켓Instance) {
    try {
      dict웹소켓Instance.close웹소켓();
      console.log("[웹소켓Plugin] 사전웹소켓연결이미끊김");
    } catch (error) {
      console.error("[웹소켓Plugin] 끊김사전웹소켓연결실패:", error);
    }
  }

  // 정리모든등록의 웹소켓 实例
  websocketInstances.forEach((instance, 키) => {
    try {
      if (instance && typeof instance.disconnect === "function") {
        instance.disconnect();
        console.log(`[웹소켓Plugin] ${키} 웹소켓연결이미끊김`);
      } else if (instance && typeof instance.close웹소켓 === "function") {
        instance.close웹소켓();
        console.log(`[웹소켓Plugin] ${키} 웹소켓연결이미끊김`);
      }
    } catch (error) {
      console.error(`[웹소켓Plugin] 끊김 ${키} 웹소켓연결실패:`, error);
    }
  });

  // 정리비어있음实例매핑
  websocketInstances.clear();

  // 移除이벤트 리스닝기기
  window.removeEventListener("beforeunload", handleWindowClose);

  // 초기화상태
  dict웹소켓Instance = null;
  isInitialized = false;
}

/**
 * 다시초기화웹소켓（용도로그인후재연결）
 */
export function reinitialize웹소켓() {
  // 先정리现있음연결
  cleanup웹소켓();

  // 지연후다시초기화
  setTimeout(() => {
    setup웹소켓();
  }, 500);
}
