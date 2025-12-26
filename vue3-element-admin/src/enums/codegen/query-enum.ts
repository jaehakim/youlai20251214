/**
 * 조회 타입 열거형
 */
export const QueryTypeEnum: Record<string, OptionType> = {
  /** 같음 */
  EQ: { value: 1, label: "=" },

  /** 부분 일치 */
  LIKE: { value: 2, label: "LIKE '%s%'" },

  /** 포함 */
  IN: { value: 3, label: "IN" },

  /** 범위 */
  BETWEEN: { value: 4, label: "BETWEEN" },

  /** 보다 큼 */
  GT: { value: 5, label: ">" },

  /** 크거나 같음 */
  GE: { value: 6, label: ">=" },

  /** 보다 작음 */
  LT: { value: 7, label: "<" },

  /** 작거나 같음 */
  LE: { value: 8, label: "<=" },

  /** 같지 않음 */
  NE: { value: 9, label: "!=" },

  /** 왼쪽 부분 일치 */
  LIKE_LEFT: { value: 10, label: "LIKE '%s'" },

  /** 오른쪽 부분 일치 */
  LIKE_RIGHT: { value: 11, label: "LIKE 's%'" },
};
