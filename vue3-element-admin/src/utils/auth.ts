import { Storage } from "./storage";
import { AUTH_KEYS, ROLE_ROOT } from "@/constants";
import { useUserStoreHook } from "@/store/modules/user-store";
import router from "@/router";

// 로컬 자격 증명 및 선호도 읽기/쓰기 담당
export const AuthStorage = {
  getAccessToken(): string {
    const isRememberMe = Storage.get<boolean>(AUTH_KEYS.REMEMBER_ME, false);
    return isRememberMe
      ? Storage.get(AUTH_KEYS.ACCESS_TOKEN, "")
      : Storage.sessionGet(AUTH_KEYS.ACCESS_TOKEN, "");
  },

  getRefreshToken(): string {
    const isRememberMe = Storage.get<boolean>(AUTH_KEYS.REMEMBER_ME, false);
    return isRememberMe
      ? Storage.get(AUTH_KEYS.REFRESH_TOKEN, "")
      : Storage.sessionGet(AUTH_KEYS.REFRESH_TOKEN, "");
  },

  setTokens(accessToken: string, refreshToken: string, rememberMe: boolean): void {
    Storage.set(AUTH_KEYS.REMEMBER_ME, rememberMe);
    if (rememberMe) {
      Storage.set(AUTH_KEYS.ACCESS_TOKEN, accessToken);
      Storage.set(AUTH_KEYS.REFRESH_TOKEN, refreshToken);
    } else {
      Storage.sessionSet(AUTH_KEYS.ACCESS_TOKEN, accessToken);
      Storage.sessionSet(AUTH_KEYS.REFRESH_TOKEN, refreshToken);
      Storage.remove(AUTH_KEYS.ACCESS_TOKEN);
      Storage.remove(AUTH_KEYS.REFRESH_TOKEN);
    }
  },

  clearAuth(): void {
    Storage.remove(AUTH_KEYS.ACCESS_TOKEN);
    Storage.remove(AUTH_KEYS.REFRESH_TOKEN);
    Storage.sessionRemove(AUTH_KEYS.ACCESS_TOKEN);
    Storage.sessionRemove(AUTH_KEYS.REFRESH_TOKEN);
  },

  getRememberMe(): boolean {
    return Storage.get<boolean>(AUTH_KEYS.REMEMBER_ME, false);
  },
};

/**
 * 권한 판단
 */
export function hasPerm(value: string | string[], type: "button" | "role" = "button"): boolean {
  const { roles, perms } = useUserStoreHook().userInfo;

  if (!roles || !perms) {
    return false;
  }

  // 슈퍼 관리자는 모든 권한을 가집니다.
  if (type === "button" && roles.includes(ROLE_ROOT)) {
    return true;
  }

  const auths = type === "button" ? perms : roles;
  return typeof value === "string"
    ? auths.includes(value)
    : value.some((perm) => auths.includes(perm));
}

/**
 * 로그인 페이지로 리디렉션
 */
export async function redirectToLogin(message: string = "다시 로그인해주세요"): Promise<void> {
  ElNotification({
    title: "알림",
    message,
    type: "warning",
    duration: 3000,
  });

  await useUserStoreHook().resetAllState();

  try {
    // 로그인 페이지로 이동, 로그인 후 리디렉션을 위해 현재 라우트 유지
    const currentPath = router.currentRoute.value.fullPath;
    await router.push(`/login?redirect=${encodeURIComponent(currentPath)}`);
  } catch (error) {
    console.error("Redirect to login error:", error);
    // 라우트 리디렉션 실패 시에도 강제로 이동
    window.location.href = "/login";
  }
}
