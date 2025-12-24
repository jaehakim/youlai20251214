/**
 * 항목目常量统하나管理
 * 存储키命이름规范：{p참조ix}:{namespace}:{키}
 */

export const APP_PREFIX = "vea";

export const STORAGE_KEYS = {
  // 사용자认证관련
  ACCESS_TOKEN: `${APP_PREFIX}:auth:access_token`, // JWT访问令牌
  REFRESH_TOKEN: `${APP_PREFIX}:auth:참조resh_token`, // JWT새로고침令牌
  REMEMBER_ME: `${APP_PREFIX}:auth:remember_me`, // 记住登录상태

  // 系统核心관련
  DICT_CACHE: `${APP_PREFIX}:system:dict_cache`, // 사전데이터캐시

  // UI설정관련
  SHOW_TAGS_VIEW: `${APP_PREFIX}:ui:show_tags_view`, // 显示标签页뷰
  SHOW_APP_LOGO: `${APP_PREFIX}:ui:show_app_logo`, // 显示应用Logo
  SHOW_WATERMARK: `${APP_PREFIX}:ui:show_watermark`, // 显示水印
  ENABLE_AI_ASSISTANT: `${APP_PREFIX}:ui:enable_ai_assistant`, // 启用 AI 助手
  LAYOUT: `${APP_PREFIX}:ui:layout`, // 레이아웃模式
  SIDEBAR_COLOR_SCHEME: `${APP_PREFIX}:ui:sidebar_color_scheme`, // 측엣지열配色方案
  THEME: `${APP_PREFIX}:ui:theme`, // 테마模式
  THEME_COLOR: `${APP_PREFIX}:ui:theme_color`, // 테마色

  // 应用상태관련
  DEVICE: `${APP_PREFIX}:app:device`, // 设备타입
  SIZE: `${APP_PREFIX}:app:size`, // 屏幕尺寸
  LANGUAGE: `${APP_PREFIX}:app:language`, // 应用语言
  SIDEBAR_STATUS: `${APP_PREFIX}:app:sidebar_status`, // 측엣지열상태
  ACTIVE_TOP_MENU_PATH: `${APP_PREFIX}:app:active_top_menu_path`, // 当前激活의상단메뉴단일경로
} as const;

export const ROLE_ROOT = "ROOT"; // 超级管理员역할

// 分그룹키컬렉션（便于일괄量작업）
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

export type StorageKey = (typeof STORAGE_KEYS)[키of typeof STORAGE_KEYS];
