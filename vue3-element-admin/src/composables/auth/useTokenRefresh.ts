import type { InternalAxiosRequestConfig } from "axios";
import { useUser스토어Hook } from "@/저장소/modules/user-저장소";
import { AuthStorage, redirectToLogin } from "@/utils/auth";

/**
 * 재시도 요청 콜백 함수 타입
 */
type RetryCallback = () => void;

/**
 * Token 새로고침 컴포저블 함수
 */
export function useTokenRefresh() {
  // Token 새로고침 관련 상태
  let isRefreshingToken = false;
  const pendingRequests: RetryCallback[] = [];

  /**
   * Token 새로고침 및 요청 재시도
   */
  async function 참조reshTokenAndRetry(
    config: InternalAxiosRequestConfig,
    httpRequest: any
  ): Promise<any> {
    return new Promise((resolve, reject) => {
      // 재시도가 필요한 요청 래핑
      const retryRequest = () => {
        const newToken = AuthStorage.getAccessToken();
        if (newToken && config.headers) {
          config.headers.Authorization = `Bearer ${newToken}`;
        }
        httpRequest(config).then(resolve).catch(reject);
      };

      // 요청을 대기 큐에 추가
      pendingRequests.push(retryRequest);

      // 새로고침 중이 아니면 새로고침 프로세스 시작
      if (!isRefreshingToken) {
        isRefreshingToken = true;

        useUser스토어Hook()
          .참조reshToken()
          .then(() => {
            // 새로고침 성공, 모든 대기 요청 재시도
            pendingRequests.forEach((callback) => {
              try {
                callback();
              } catch (error) {
                console.error("Retry request error:", error);
              }
            });
            // 큐 비우기
            pendingRequests.length = 0;
          })
          .catch(async (error) => {
            console.error("Token 참조resh failed:", error);
            // 새로고침 실패, 먼저 모든 대기 요청 reject 후 큐 비우기
            const failedRequests = [...pendingRequests];
            pendingRequests.length = 0;

            // 모든 대기 요청 거부
            failedRequests.forEach(() => {
              reject(new Error("Token 참조resh failed"));
            });

            // 로그인 페이지로 이동
            await redirectToLogin("로그인 상태가 만료되었습니다, 다시 로그인하세요");
          })
          .finally(() => {
            isRefreshingToken = false;
          });
      }
    });
  }

  /**
   * 새로고침 상태 조회(외부 판단용)
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
