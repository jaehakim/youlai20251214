import type { InternalAxiosRequestConfig } from "axios";
import { useUser스토어Hook } from "@/저장소/modules/user-저장소";
import { AuthStorage, redirectToLogin } from "@/utils/auth";

/**
 * 재시도요청의콜백함수타입
 */
type RetryCallback = () => void;

/**
 * Token새로고침조합式함수
 */
export function useTokenRefresh() {
  // Token 새로고침관련상태
  let isRefreshingToken = false;
  const pendingRequests: RetryCallback[] = [];

  /**
   * 새로고침 Token 그리고재시도요청
   */
  async function 참조reshTokenAndRetry(
    config: InternalAxiosRequestConfig,
    httpRequest: any
  ): Promise<any> {
    return new Promise((resolve, reject) => {
      // 封装필요해야재시도의요청
      const retryRequest = () => {
        const newToken = AuthStorage.getAccessToken();
        if (newToken && config.headers) {
          config.headers.Authorization = `Bearer ${newToken}`;
        }
        httpRequest(config).then(resolve).catch(reject);
      };

      // 로요청加입대기큐
      pendingRequests.push(retryRequest);

      // 만약없음正에새로고침，그러면시작새로고침流程
      if (!isRefreshingToken) {
        isRefreshingToken = true;

        useUser스토어Hook()
          .참조reshToken()
          .then(() => {
            // 새로고침성공，재시도모든대기의요청
            pendingRequests.forEach((callback) => {
              try {
                callback();
              } catch (error) {
                console.error("Retry request error:", error);
              }
            });
            // 정리비어있음큐
            pendingRequests.length = 0;
          })
          .catch(async (error) => {
            console.error("Token 참조resh failed:", error);
            // 새로고침실패，先 reject 모든대기의요청，再정리비어있음큐
            const failedRequests = [...pendingRequests];
            pendingRequests.length = 0;

            // 거부모든대기의요청
            failedRequests.forEach(() => {
              reject(new Error("Token 참조resh failed"));
            });

            // 점프转로그인页
            await redirectToLogin("로그인상태이미무효，요청다시로그인");
          })
          .finally(() => {
            isRefreshingToken = false;
          });
      }
    });
  }

  /**
   * 조회새로고침상태（용도외부判断）
   */
  function getRefreshStatus() {
    return {
      isRefreshing: isRefreshingToken,
      pendingCount: pendingRequests.length,
    };
  }

  return {
    참조reshTokenAndRetry,
    getRefreshStatus,
  };
}
