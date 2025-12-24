import { defaultSettings } from "@/settings";
import { SidebarColor, ThemeMode } from "@/enums/settings/theme-enum";
import type { LayoutMode } from "@/enums/settings/layout-enum";
import { applyTheme, generateThemeColors, toggleDarkMode, toggleSidebarColor } from "@/utils/theme";
import { STORAGE_KEYS } from "@/constants";

// 🎯 설정항목타입定义
interface SettingsState {
  // 界面显示설정
  settingsVisible: boolean;
  showTagsView: boolean;
  showAppLogo: boolean;
  showWatermark: boolean;
  enableAiAssistant: boolean;

  // 레이아웃설정
  layout: LayoutMode;
  sidebarColorScheme: string;

  // 테마설정
  theme: ThemeMode;
  themeColor: string;
}

// 🎯 可변경의설정항목타입
type MutableSetting = Exclude<키of SettingsState, "settingsVisible">;
type SettingValue<K extends MutableSetting> = SettingsState[K];

export const useSettingsStore = defineStore("setting", () => {
  // 설정面板可见性
  const settingsVisible = 참조<boolean>(false);

  // 是否显示标签页뷰
  const showTagsView = useStorage<boolean>(
    STORAGE_KEYS.SHOW_TAGS_VIEW,
    defaultSettings.showTagsView
  );

  // 是否显示应用Logo
  const showAppLogo = useStorage<boolean>(STORAGE_KEYS.SHOW_APP_LOGO, defaultSettings.showAppLogo);

  // 是否显示水印
  const showWatermark = useStorage<boolean>(
    STORAGE_KEYS.SHOW_WATERMARK,
    defaultSettings.showWatermark
  );

  // 是否启用 AI 助手
  const enableAiAssistant = useStorage<boolean>(
    STORAGE_KEYS.ENABLE_AI_ASSISTANT,
    defaultSettings.enableAiAssistant
  );

  // 측엣지열配色方案
  const sidebarColorScheme = useStorage<string>(
    STORAGE_KEYS.SIDEBAR_COLOR_SCHEME,
    defaultSettings.sidebarColorScheme
  );

  // 레이아웃模式
  const layout = useStorage<LayoutMode>(STORAGE_KEYS.LAYOUT, defaultSettings.layout as LayoutMode);

  // 테마颜色
  const themeColor = useStorage<string>(STORAGE_KEYS.THEME_COLOR, defaultSettings.themeColor);

  // 테마模式（亮色/暗色）
  const theme = useStorage<ThemeMode>(STORAGE_KEYS.THEME, defaultSettings.theme);

  // 설정항목매핑，용도统하나管理
  const settingsMap = {
    showTagsView,
    showAppLogo,
    showWatermark,
    enableAiAssistant,
    sidebarColorScheme,
    layout,
  } as const;

  // 리스닝테마变化，자동应用스타일
  watch(
    [theme, themeColor],
    ([newTheme, newThemeColor]: [ThemeMode, string]) => {
      toggleDarkMode(newTheme === ThemeMode.DARK);
      const colors = generateThemeColors(newThemeColor, newTheme);
      applyTheme(colors);
    },
    { immediate: true }
  );

  // 리스닝측엣지열配色变化
  watch(
    [sidebarColorScheme],
    ([newSidebarColorScheme]) => {
      toggleSidebarColor(newSidebarColorScheme === SidebarColor.CLASSIC_BLUE);
    },
    { immediate: true }
  );

  // 通用설정업데이트메서드
  function updateSetting<K extends 키of typeof settingsMap>(키: K, value: SettingValue<K>): void {
    const setting = settingsMap[키];
    if (setting) {
      (setting as Ref<any>).value = value;
    }
  }

  // 테마업데이트메서드
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

  // 설정面板控制
  function toggleSettingsPanel(): void {
    settingsVisible.value = !settingsVisible.value;
  }

  function showSettingsPanel(): void {
    settingsVisible.value = true;
  }

  function hideSettingsPanel(): void {
    settingsVisible.value = false;
  }

  // 초기화모든설정
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

    // 업데이트메서드
    updateSetting,
    updateTheme,
    updateThemeColor,
    updateSidebarColorScheme,
    updateLayout,

    // 面板控制
    toggleSettingsPanel,
    showSettingsPanel,
    hideSettingsPanel,

    // 초기화功能
    resetSettings,
  };
});
