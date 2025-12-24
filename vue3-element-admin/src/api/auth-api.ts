import request from "@/utils/request";

const AUTH_BASE_URL = "/api/v1/auth";

const AuthAPI = {
  /** 로그인 인터페이스*/
  login(data: LoginFormData) {
    const formData = new FormData();
    formData.append("username", data.username);
    formData.append("password", data.password);
    formData.append("captchaKey", data.captchaKey);
    formData.append("captchaCode", data.captchaCode);
    return request<any, LoginResult>({
      url: `${AUTH_BASE_URL}/login`,
      method: "post",
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /** 토큰 새로 고침 인터페이스*/
  refreshToken(refreshToken: string) {
    return request<any, LoginResult>({
      url: `${AUTH_BASE_URL}/refresh-token`,
      method: "post",
      params: { refreshToken },
      headers: {
        Authorization: "no-auth",
      },
    });
  },

  /** 로그아웃 인터페이스 */
  logout() {
    return request({
      url: `${AUTH_BASE_URL}/logout`,
      method: "delete",
    });
  },

  /** 캡차 조회 인터페이스*/
  getCaptcha() {
    return request<any, CaptchaInfo>({
      url: `${AUTH_BASE_URL}/captcha`,
      method: "get",
    });
  },
};

export default AuthAPI;

/** 로그인 폼 데이터 */
export interface LoginFormData {
  /** 사용자명 */
  username: string;
  /** 비밀번호 */
  password: string;
  /** 캡차 캐시 키 */
  captchaKey: string;
  /** 캡차 코드 */
  captchaCode: string;
  /** 로그인 상태 유지 */
  rememberMe: boolean;
}

/** 로그인 응답 */
export interface LoginResult {
  /** 액세스 토큰 */
  accessToken: string;
  /** 리프레시 토큰 */
  refreshToken: string;
  /** 토큰 타입 */
  tokenType: string;
  /** 만료 시간(초) */
  expiresIn: number;
}

/** 캡차 정보 */
export interface CaptchaInfo {
  /** 캡차 캐시 키 */
  captchaKey: string;
  /** 캡차 이미지 Base64 문자열 */
  captchaBase64: string;
}
