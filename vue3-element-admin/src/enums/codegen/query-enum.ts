/**
 * 조회타입열거형
 */
export const QueryTypeEnum: Record<string, OptionType> = {
  /** 等于 */
  EQ: { value: 1, label: "=" },

  /** 模糊일치 */
  LIKE: { value: 2, label: "LIKE '%s%'" },

  /** 패키지含 */
  IN: { value: 3, label: "IN" },

  /** 범위 */
  BETWEEN: { value: 4, label: "BETWEEN" },

  /** 大于 */
  GT: { value: 5, label: ">" },

  /** 大于等于 */
  GE: { value: 6, label: ">=" },

  /** 小于 */
  LT: { value: 7, label: "<" },

  /** 小于等于 */
  LE: { value: 8, label: "<=" },

  /** 아님等于 */
  NE: { value: 9, label: "!=" },

  /** 左模糊일치 */
  LIKE_LEFT: { value: 10, label: "LIKE '%s'" },

  /** 右模糊일치 */
  LIKE_RIGHT: { value: 11, label: "LIKE 's%'" },
};
