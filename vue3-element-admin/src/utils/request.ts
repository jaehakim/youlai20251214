import axios, { type InternalAxiosRequestConfig, type AxiosResponse } from "axios";
import qs from "qs";
import { ApiCodeEnum } from "@/enums/api/code-enum";
import { AuthStorage, redirectToLogin } from "@/utils/auth";
import { useTokenRefresh } from "@/composables/auth/useTokenRefresh";
import { authConfig } from "@/settings";

// 토큰 새로 고침 합성 함수 초기화
const { refreshTokenAndRetry } = useTokenRefresh();

/**
 * HTTP 요청 인스턴스 생성
 */
const httpRequest = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 50000,
  headers: { "Content-Type": "application/json;charset=utf-8" },
  paramsSerializer: (params) => qs.stringify(params),
});

/**
 * 요청 인터셉터 - Authorization 헤더 추가
 */
httpRequest.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const accessToken = AuthStorage.getAccessToken();

    // Authorization이 no-auth로 설정되면 토큰을 전달하지 않습니다.
    if (config.headers.Authorization !== "no-auth" && accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    } else {
      delete config.headers.Authorization;
    }

    return config;
  },
  (error) => {
    console.error("Request interceptor error:", error);
    return Promise.reject(error);
  }
);

/**
 * 응답 인터셉터 - 응답 및 오류 통합 처리
 */
httpRequest.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    // 응답이 이진 데이터이면 response 객체를 직접 반환합니다. (파일 다운로드, Excel 내보내기, 이미지 표시 등용)
    if (response.config.responseType === "blob" || response.config.responseType === "arraybuffer") {
      return response;
    }

    const { code, data, msg } = response.data;

    // 요청 성공
    if (code === ApiCodeEnum.SUCCESS) {
      return data;
    }

    // 비즈니스 오류
    ElMessage.error(msg || "시스템 오류");
    return Promise.reject(new Error(msg || "Business Error"));
  },
  async (error) => {
    console.error("Response interceptor error:", error);

    const { config, response } = error;

    // 네트워크 오류 또는 서버 응답 없음
    if (!response) {
      ElMessage.error("네트워크 연결 실패, 네트워크 설정을 확인하세요.");
      return Promise.reject(error);
    }

    const { code, msg } = response.data as ApiResponse;

    switch (code) {
      case ApiCodeEnum.ACCESS_TOKEN_INVALID:
        // Access Token 만료됨
        if (authConfig.enableTokenRefresh) {
          // 토큰 새로 고침 활성화, 새로 고침 시도
          return refreshTokenAndRetry(config, httpRequest);
        } else {
          // 토큰 새로 고침 비활성화, 로그인 페이지로 직접 이동
          await redirectToLogin("로그인이 만료되었습니다. 다시 로그인하세요.");
          return Promise.reject(new Error(msg || "Access Token Invalid"));
        }

      case ApiCodeEnum.REFRESH_TOKEN_INVALID:
        // Refresh Token 만료됨, 로그인 페이지로 이동
        await redirectToLogin("로그인이 만료되었습니다. 다시 로그인하세요.");
        return Promise.reject(new Error(msg || "Refresh Token Invalid"));

      default:
        ElMessage.error(msg || "요청 실패");
        return Promise.reject(new Error(msg || "Request Error"));
    }
  }
);

export default httpRequest;
