import { useDictSync } from "@/composables";
import { AuthStorage } from "@/utils/auth";
// 不直接가져오기 저장소 或 userStore

// 全局 웹소켓 实例管理
const websocketInstances = new Map<string, any>();

// 용도방지重复초기화의상태标记
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
  console.log("[웹소켓Plugin] 开始초기화웹소켓서비스...");

  // 检查是否已经초기화
  if (isInitialized) {
    console.log("[웹소켓Plugin] 웹소켓서비스已经초기화,점프거치重复초기화");
    return;
  }

  // 检查环境변수是否설정
  const wsEndpoint = import.meta.env.VITE_APP_WS_ENDPOINT;
  if (!wsEndpoint) {
    console.log("[웹소켓Plugin] 미설정웹소켓엔드포인트,점프거치웹소켓초기화");
    return;
  }

  // 检查是否已登录（基于是否存에访问令牌）
  if (!AuthStorage.getAccessToken()) {
    console.warn(
      "[웹소켓Plugin] 미找到访问令牌，웹소켓초기화已점프거치。사용자登录후로자동重新연결。"
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
      console.log("[웹소켓Plugin] 사전웹소켓초기화完成");

      // 초기화에스레드사용자계개웹소켓
      import("@/composables").then(({ useOnlineCount }) => {
        const onlineCountInstance = useOnlineCount({ autoInit: false });
        onlineCountInstance.init웹소켓();
        console.log("[웹소켓Plugin] 에스레드사용자계개웹소켓초기화完成");
      });

      // 에창닫기前断开웹소켓연결
      window.addEventListener("beforeunload", handleWindowClose);

      console.log("[웹소켓Plugin] 웹소켓서비스초기화完成");
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
  console.log("[웹소켓Plugin] 창即로닫기，断开웹소켓연결");
  cleanup웹소켓();
}

/**
 * 清理웹소켓연결
 */
export function cleanup웹소켓() {
  // 清理사전 웹소켓
  if (dict웹소켓Instance) {
    try {
      dict웹소켓Instance.close웹소켓();
      console.log("[웹소켓Plugin] 사전웹소켓연결已断开");
    } catch (error) {
      console.error("[웹소켓Plugin] 断开사전웹소켓연결실패:", error);
    }
  }

  // 清理모든등록의 웹소켓 实例
  websocketInstances.forEach((instance, 키) => {
    try {
      if (instance && typeof instance.disconnect === "function") {
        instance.disconnect();
        console.log(`[웹소켓Plugin] ${키} 웹소켓연결已断开`);
      } else if (instance && typeof instance.close웹소켓 === "function") {
        instance.close웹소켓();
        console.log(`[웹소켓Plugin] ${키} 웹소켓연결已断开`);
      }
    } catch (error) {
      console.error(`[웹소켓Plugin] 断开 ${키} 웹소켓연결실패:`, error);
    }
  });

  // 清비어있음实例매핑
  websocketInstances.clear();

  // 移除이벤트 리스닝器
  window.removeEventListener("beforeunload", handleWindowClose);

  // 초기화상태
  dict웹소켓Instance = null;
  isInitialized = false;
}

/**
 * 重新초기화웹소켓（용도登录후重连）
 */
export function reinitialize웹소켓() {
  // 先清理现有연결
  cleanup웹소켓();

  // 지연후重新초기화
  setTimeout(() => {
    setup웹소켓();
  }, 500);
}
