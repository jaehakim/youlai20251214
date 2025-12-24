/**
 * API응답码열거형
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
   * 访问令牌无效또는거치期
   */
  ACCESS_TOKEN_INVALID = "A0230",

  /**
   * 새로고침令牌无效또는거치期
   */
  REFRESH_TOKEN_INVALID = "A0231",
}
