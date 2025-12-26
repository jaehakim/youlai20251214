import { store } from "@/store";

import AuthAPI, { type LoginFormData } from "@/api/auth-api";
import UserAPI, { type UserInfo } from "@/api/system/user-api";

import { AuthStorage } from "@/utils/auth";
import { usePermissionStoreHook } from "@/store/modules/permission-store";
import { useDictStoreHook } from "@/store/modules/dict-store";
import { useTagsViewStore } from "@/store";
import { cleanupWebSocket } from "@/plugins/websocket";

export const useUserStore = defineStore("user", () => {
  // 사용자 정보
  const userInfo = ref<UserInfo>({} as UserInfo);
  // 로그인 기억 상태
  const rememberMe = ref(AuthStorage.getRememberMe());

  /**
   * 로그인
   *
   * @param {LoginFormData}
   * @returns
   */
  function login(LoginFormData: LoginFormData) {
    return new Promise<void>((resolve, reject) => {
      AuthAPI.login(LoginFormData)
        .then((data) => {
          const { accessToken, refreshToken } = data;
          // 로그인 기억 상태 및 토큰 저장
          rememberMe.value = LoginFormData.rememberMe;
          AuthStorage.setTokens(accessToken, refreshToken, rememberMe.value);
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  /**
   * 사용자 정보 조회
   *
   * @returns {UserInfo} 사용자 정보
   */
  function getUserInfo() {
    return new Promise<UserInfo>((resolve, reject) => {
      UserAPI.getInfo()
        .then((data) => {
          if (!data) {
            reject("Verification failed, please Login again.");
            return;
          }
          Object.assign(userInfo.value, { ...data });
          resolve(data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  /**
   * 로그아웃
   */
  function logout() {
    return new Promise<void>((resolve, reject) => {
      AuthAPI.logout()
        .then(() => {
          // 모든 시스템 상태 초기화
          resetAllState();
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  /**
   * 모든 시스템 상태 초기화
   * 사용자 자격 증명, 라우팅, 캐시 등 모든 정리 작업을 통합 처리
   */
  function resetAllState() {
    // 1. 사용자 상태 초기화
    resetUserState();

    // 2. 다른 모듈 상태 초기화
    // 라우팅 초기화
    usePermissionStoreHook().resetRouter();
    // 사전 캐시 정리
    useDictStoreHook().clearDictCache();
    // 태그 뷰 정리
    useTagsViewStore().delAllViews();

    // 3. 웹소켓 연결 정리
    cleanupWebSocket();
    console.log("[UserStore] 웹소켓 connections cleaned up");

    return Promise.resolve();
  }

  /**
   * 사용자 상태 초기화
   * 사용자 모듈 내의 상태만 처리
   */
  function resetUserState() {
    // 사용자 자격 증명 정리
    AuthStorage.clearAuth();
    // 사용자 정보 초기화
    userInfo.value = {} as UserInfo;
  }

  /**
   * 토큰 새로고침
   */
  function refreshToken() {
    const refreshTokenValue = AuthStorage.getRefreshToken();

    if (!refreshTokenValue) {
      return Promise.reject(new Error("유효한 새로고침 토큰이 없습니다"));
    }

    return new Promise<void>((resolve, reject) => {
      AuthAPI.refreshToken(refreshTokenValue)
        .then((data) => {
          const { accessToken, refreshToken: newRefreshToken } = data;
          // 토큰 업데이트, 현재 로그인 기억 상태 유지
          AuthStorage.setTokens(accessToken, newRefreshToken, AuthStorage.getRememberMe());
          resolve();
        })
        .catch((error) => {
          console.log("refreshToken 새로고침 실패", error);
          reject(error);
        });
    });
  }

  return {
    userInfo,
    rememberMe,
    isLoggedIn: () => !!AuthStorage.getAccessToken(),
    getUserInfo,
    login,
    logout,
    resetAllState,
    resetUserState,
    refreshToken,
  };
});

/**
 * 컴포넌트 외부에서 UserStore를 사용하기 위한 훅 함수
 * @see https://pinia.vuejs.org/core-concepts/outside-component-usage.html
 */
export function useUserStoreHook() {
  return useUserStore(store);
}
