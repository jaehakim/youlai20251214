/**
 * 프로젝트 상수 통합 관리
 * 스토리지 키 명명 규칙: {prefix}:{namespace}:{key}
 */

export const APP_PREFIX = "vea";

export const STORAGE_KEYS = {
  // 사용자 인증 관련
  ACCESS_TOKEN: `${APP_PREFIX}:auth:access_token`, // JWT 액세스 토큰
  REFRESH_TOKEN: `${APP_PREFIX}:auth:refresh_token`, // JWT 리프레시 토큰
  REMEMBER_ME: `${APP_PREFIX}:auth:remember_me`, // 로그인 상태 기억

  // 시스템 핵심 관련
  DICT_CACHE: `${APP_PREFIX}:system:dict_cache`, // 사전 데이터 캐시

  // UI 설정 관련
  SHOW_TAGS_VIEW: `${APP_PREFIX}:ui:show_tags_view`, // 태그 뷰 표시
  SHOW_APP_LOGO: `${APP_PREFIX}:ui:show_app_logo`, // 앱 로고 표시
  SHOW_WATERMARK: `${APP_PREFIX}:ui:show_watermark`, // 워터마크 표시
  ENABLE_AI_ASSISTANT: `${APP_PREFIX}:ui:enable_ai_assistant`, // AI 도우미 활성화
  LAYOUT: `${APP_PREFIX}:ui:layout`, // 레이아웃 모드
  SIDEBAR_COLOR_SCHEME: `${APP_PREFIX}:ui:sidebar_color_scheme`, // 사이드바 배색 방안
  THEME: `${APP_PREFIX}:ui:theme`, // 테마 모드
  THEME_COLOR: `${APP_PREFIX}:ui:theme_color`, // 테마 색상

  // 앱 상태 관련
  DEVICE: `${APP_PREFIX}:app:device`, // 기기 타입
  SIZE: `${APP_PREFIX}:app:size`, // 화면 크기
  LANGUAGE: `${APP_PREFIX}:app:language`, // 앱 언어
  SIDEBAR_STATUS: `${APP_PREFIX}:app:sidebar_status`, // 사이드바 상태
  ACTIVE_TOP_MENU_PATH: `${APP_PREFIX}:app:active_top_menu_path`, // 현재 활성화된 상단 메뉴 경로
} as const;

export const ROLE_ROOT = "ROOT"; // 슈퍼 관리자 역할

// 그룹별 키 컬렉션 (일괄 작업 편의)
export const AUTH_KEYS = {
  ACCESS_TOKEN: STORAGE_KEYS.ACCESS_TOKEN,
  REFRESH_TOKEN: STORAGE_KEYS.REFRESH_TOKEN,
  REMEMBER_ME: STORAGE_KEYS.REMEMBER_ME,
} as const;

export const SYSTEM_KEYS = {
  DICT_CACHE: STORAGE_KEYS.DICT_CACHE,
} as const;

export const SETTINGS_KEYS = {
  SHOW_TAGS_VIEW: STORAGE_KEYS.SHOW_TAGS_VIEW,
  SHOW_APP_LOGO: STORAGE_KEYS.SHOW_APP_LOGO,
  SHOW_WATERMARK: STORAGE_KEYS.SHOW_WATERMARK,
  ENABLE_AI_ASSISTANT: STORAGE_KEYS.ENABLE_AI_ASSISTANT,
  SIDEBAR_COLOR_SCHEME: STORAGE_KEYS.SIDEBAR_COLOR_SCHEME,
  LAYOUT: STORAGE_KEYS.LAYOUT,
  THEME_COLOR: STORAGE_KEYS.THEME_COLOR,
  THEME: STORAGE_KEYS.THEME,
} as const;

export const APP_KEYS = {
  DEVICE: STORAGE_KEYS.DEVICE,
  SIZE: STORAGE_KEYS.SIZE,
  LANGUAGE: STORAGE_KEYS.LANGUAGE,
  SIDEBAR_STATUS: STORAGE_KEYS.SIDEBAR_STATUS,
  ACTIVE_TOP_MENU_PATH: STORAGE_KEYS.ACTIVE_TOP_MENU_PATH,
} as const;

export const ALL_STORAGE_KEYS = {
  ...AUTH_KEYS,
  ...SYSTEM_KEYS,
  ...SETTINGS_KEYS,
  ...APP_KEYS,
} as const;

export type StorageKey = (typeof STORAGE_KEYS)[keyof typeof STORAGE_KEYS];
