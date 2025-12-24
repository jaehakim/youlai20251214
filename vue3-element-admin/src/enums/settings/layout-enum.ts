/**
 * 메뉴단일레이아웃열거형
 */
export const enum LayoutMode {
  /**
   * 왼쪽메뉴단일레이아웃
   */
  LEFT = "left",
  /**
   * 상단메뉴단일레이아웃
   */
  TOP = "top",

  /**
   * 混合메뉴단일레이아웃
   */
  MIX = "mix",
}

/**
 * 측엣지열상태열거형
 */
export const enum SidebarStatus {
  /**
   * 펼치기
   */
  OPENED = "opened",

  /**
   * 닫기
   */
  CLOSED = "closed",
}

/**
 * 컴포넌트尺寸열거형
 */
export const enum ComponentSize {
  /**
   * 기본값
   */
  DEFAULT = "default",

  /**
   * 大型
   */
  LARGE = "large",

  /**
   * 小型
   */
  SMALL = "small",
}
