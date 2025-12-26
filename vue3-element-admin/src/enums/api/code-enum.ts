/**
 * API 응답 코드 열거형
 */
export const enum ApiCodeEnum {
  /**
   * 성공
   */
  SUCCESS = "00000",
  /**
   * 오류
   */
  ERROR = "B0001",

  /**
   * 액세스 토큰 무효 또는 만료
   */
  ACCESS_TOKEN_INVALID = "A0230",

  /**
   * 리프레시 토큰 무효 또는 만료
   */
  REFRESH_TOKEN_INVALID = "A0231",
}
