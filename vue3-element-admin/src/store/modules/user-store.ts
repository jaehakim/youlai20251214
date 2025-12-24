import { 저장소 } from "@/저장소";

import AuthAPI, { type LoginFormData } from "@/api/auth-api";
import UserAPI, { type UserInfo } from "@/api/system/user-api";

import { AuthStorage } from "@/utils/auth";
import { usePermissionStoreHook } from "@/저장소/modules/permission-저장소";
import { useDictStoreHook } from "@/저장소/modules/dict-저장소";
import { useTagsViewStore } from "@/저장소";
import { cleanup웹소켓 } from "@/plugins/websocket";

export const useUserStore = defineStore("user", () => {
  // 사용자정보
  const userInfo = 참조<UserInfo>({} as UserInfo);
  // 记住我상태
  const rememberMe = 참조(AuthStorage.getRememberMe());

  /**
   * 登录
   *
   * @param {LoginFormData}
   * @returns
   */
  function login(LoginFormData: LoginFormData) {
    return new Promise<void>((resolve, reject) => {
      AuthAPI.login(LoginFormData)
        .then((data) => {
          const { accessToken, 참조reshToken } = data;
          // 저장记住我상태및token
          rememberMe.value = LoginFormData.rememberMe;
          AuthStorage.setTokens(accessToken, 참조reshToken, rememberMe.value);
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  /**
   * 조회사용자정보
   *
   * @returns {UserInfo} 사용자정보
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
   * 登出
   */
  function logout() {
    return new Promise<void>((resolve, reject) => {
      AuthAPI.logout()
        .then(() => {
          // 초기화모든系统상태
          resetAllState();
          resolve();
        })
        .catch((error) => {
          reject(error);
        });
    });
  }

  /**
   * 초기화모든系统상태
   * 统하나처리모든清理工作，패키지括사용자凭证、라우팅、캐시等
   */
  function resetAllState() {
    // 1. 초기화사용자상태
    resetUserState();

    // 2. 초기화其他모듈상태
    // 초기화라우팅
    usePermissionStoreHook().resetRouter();
    // 清除사전캐시
    useDictStoreHook().clearDictCache();
    // 清除标签뷰
    useTagsViewStore().delAllViews();

    // 3. 清理 웹소켓 연결
    cleanup웹소켓();
    console.log("[UserStore] 웹소켓 connections cleaned up");

    return Promise.resolve();
  }

  /**
   * 초기화사용자상태
   * 오직처리사용자모듈内의상태
   */
  function resetUserState() {
    // 清除사용자凭证
    AuthStorage.clearAuth();
    // 초기화사용자정보
    userInfo.value = {} as UserInfo;
  }

  /**
   * 새로고침 token
   */
  function 참조reshToken() {
    const 참조reshToken = AuthStorage.getRefreshToken();

    if (!참조reshToken) {
      return Promise.reject(new Error("없음유효의새로고침令牌"));
    }

    return new Promise<void>((resolve, reject) => {
      AuthAPI.참조reshToken(참조reshToken)
        .then((data) => {
          const { accessToken, 참조reshToken: newRefreshToken } = data;
          // 업데이트令牌，保持当前记住我상태
          AuthStorage.setTokens(accessToken, newRefreshToken, AuthStorage.getRememberMe());
          resolve();
        })
        .catch((error) => {
          console.log(" 참조reshToken  새로고침실패", error);
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
    참조reshToken,
  };
});

/**
 * 에컴포넌트외부사용UserStore의훅함수
 * @see https://pinia.vuejs.org/core-concepts/outside-component-usage.html
 */
export function useUserStoreHook() {
  return useUserStore(저장소);
}
