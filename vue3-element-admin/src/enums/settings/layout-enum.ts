/**
 * 메뉴 레이아웃 열거형
 */
export const enum LayoutMode {
  /**
   * 왼쪽 메뉴 레이아웃
   */
  LEFT = "left",
  /**
   * 상단 메뉴 레이아웃
   */
  TOP = "top",

  /**
   * 혼합 메뉴 레이아웃
   */
  MIX = "mix",
}

/**
 * 사이드바 상태 열거형
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
 * 컴포넌트 크기 열거형
 */
export const enum ComponentSize {
  /**
   * 기본값
   */
  DEFAULT = "default",

  /**
   * 대형
   */
  LARGE = "large",

  /**
   * 소형
   */
  SMALL = "small",
}
