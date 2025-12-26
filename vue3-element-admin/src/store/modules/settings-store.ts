import { defaultSettings } from "@/settings";
import { SidebarColor, ThemeMode } from "@/enums/settings/theme-enum";
import type { LayoutMode } from "@/enums/settings/layout-enum";
import { applyTheme, generateThemeColors, toggleDarkMode, toggleSidebarColor } from "@/utils/theme";
import { STORAGE_KEYS } from "@/constants";

// 설정 항목 타입 정의
interface SettingsState {
  // 인터페이스 표시 설정
  settingsVisible: boolean;
  showTagsView: boolean;
  showAppLogo: boolean;
  showWatermark: boolean;
  enableAiAssistant: boolean;

  // 레이아웃 설정
  layout: LayoutMode;
  sidebarColorScheme: string;

  // 테마 설정
  theme: ThemeMode;
  themeColor: string;
}

// 변경 가능한 설정 항목 타입
type MutableSetting = Exclude<keyof SettingsState, "settingsVisible">;
type SettingValue<K extends MutableSetting> = SettingsState[K];

export const useSettingsStore = defineStore("setting", () => {
  // 설정 패널 표시 여부
  const settingsVisible = ref<boolean>(false);

  // 태그 뷰 표시 여부
  const showTagsView = useStorage<boolean>(
    STORAGE_KEYS.SHOW_TAGS_VIEW,
    defaultSettings.showTagsView
  );

  // 앱 로고 표시 여부
  const showAppLogo = useStorage<boolean>(STORAGE_KEYS.SHOW_APP_LOGO, defaultSettings.showAppLogo);

  // 워터마크 표시 여부
  const showWatermark = useStorage<boolean>(
    STORAGE_KEYS.SHOW_WATERMARK,
    defaultSettings.showWatermark
  );

  // AI 도우미 활성화 여부
  const enableAiAssistant = useStorage<boolean>(
    STORAGE_KEYS.ENABLE_AI_ASSISTANT,
    defaultSettings.enableAiAssistant
  );

  // 사이드바 색상 구성
  const sidebarColorScheme = useStorage<string>(
    STORAGE_KEYS.SIDEBAR_COLOR_SCHEME,
    defaultSettings.sidebarColorScheme
  );

  // 레이아웃 모드
  const layout = useStorage<LayoutMode>(STORAGE_KEYS.LAYOUT, defaultSettings.layout as LayoutMode);

  // 테마 색상
  const themeColor = useStorage<string>(STORAGE_KEYS.THEME_COLOR, defaultSettings.themeColor);

  // 테마 모드 (라이트/다크)
  const theme = useStorage<ThemeMode>(STORAGE_KEYS.THEME, defaultSettings.theme);

  // 설정 항목 매핑, 통합 관리용
  const settingsMap = {
    showTagsView,
    showAppLogo,
    showWatermark,
    enableAiAssistant,
    sidebarColorScheme,
    layout,
  } as const;

  // 테마 변경 감시, 자동 스타일 적용
  watch(
    [theme, themeColor],
    ([newTheme, newThemeColor]: [ThemeMode, string]) => {
      toggleDarkMode(newTheme === ThemeMode.DARK);
      const colors = generateThemeColors(newThemeColor, newTheme);
      applyTheme(colors);
    },
    { immediate: true }
  );

  // 사이드바 색상 변경 감시
  watch(
    [sidebarColorScheme],
    ([newSidebarColorScheme]) => {
      toggleSidebarColor(newSidebarColorScheme === SidebarColor.CLASSIC_BLUE);
    },
    { immediate: true }
  );

  // 범용 설정 업데이트 메서드
  function updateSetting<K extends keyof typeof settingsMap>(key: K, value: SettingValue<K>): void {
    const setting = settingsMap[key];
    if (setting) {
      (setting as Ref<any>).value = value;
    }
  }

  // 테마 업데이트 메서드
  function updateTheme(newTheme: ThemeMode): void {
    theme.value = newTheme;
  }

  function updateThemeColor(newColor: string): void {
    themeColor.value = newColor;
  }

  function updateSidebarColorScheme(newScheme: string): void {
    sidebarColorScheme.value = newScheme;
  }

  function updateLayout(newLayout: LayoutMode): void {
    layout.value = newLayout;
  }

  // 설정 패널 제어
  function toggleSettingsPanel(): void {
    settingsVisible.value = !settingsVisible.value;
  }

  function showSettingsPanel(): void {
    settingsVisible.value = true;
  }

  function hideSettingsPanel(): void {
    settingsVisible.value = false;
  }

  // 모든 설정 초기화
  function resetSettings(): void {
    showTagsView.value = defaultSettings.showTagsView;
    showAppLogo.value = defaultSettings.showAppLogo;
    showWatermark.value = defaultSettings.showWatermark;
    enableAiAssistant.value = defaultSettings.enableAiAssistant;
    sidebarColorScheme.value = defaultSettings.sidebarColorScheme;
    layout.value = defaultSettings.layout as LayoutMode;
    themeColor.value = defaultSettings.themeColor;
    theme.value = defaultSettings.theme;
  }

  return {
    // 상태
    settingsVisible,
    showTagsView,
    showAppLogo,
    showWatermark,
    enableAiAssistant,
    sidebarColorScheme,
    layout,
    themeColor,
    theme,

    // 업데이트 메서드
    updateSetting,
    updateTheme,
    updateThemeColor,
    updateSidebarColorScheme,
    updateLayout,

    // 패널 제어
    toggleSettingsPanel,
    showSettingsPanel,
    hideSettingsPanel,

    // 초기화 기능
    resetSettings,
  };
});
