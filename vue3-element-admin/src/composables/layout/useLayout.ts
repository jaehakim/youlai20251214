import { useAppStore, useSettingsStore } from "@/저장소";
import { defaultSettings } from "@/settings";

/**
 * 레이아웃 관련의通用逻辑
 */
export function useLayout() {
  const appStore = useAppStore();
  const settingsStore = useSettingsStore();

  // 계算当前레이아웃模式
  const currentLayout = computed(() => settingsStore.layout);

  // 측엣지열펼치기상태
  const isSidebarOpen = computed(() => appStore.sidebar.opened);

  // 是否显示标签뷰
  const isShowTagsView = computed(() => settingsStore.showTagsView);

  // 是否显示설정面板
  const isShowSettings = computed(() => defaultSettings.showSettings);

  // 是否显示Logo
  const isShowLogo = computed(() => settingsStore.showAppLogo);

  // 是否모바일 기기
  const isMobile = computed(() => appStore.device === "mobile");

  // 레이아웃CSS类
  const layoutClass = computed(() => ({
    hideSidebar: !appStore.sidebar.opened,
    openSidebar: appStore.sidebar.opened,
    mobile: appStore.device === "mobile",
    [`layout-${settingsStore.layout}`]: true,
  }));

  /**
   * 처리切换측엣지열의펼치기/접기상태
   */
  function toggleSidebar() {
    appStore.toggleSidebar();
  }

  /**
   * 닫기측엣지열（모바일）
   */
  function closeSidebar() {
    appStore.closeSideBar();
  }

  return {
    currentLayout,
    isSidebarOpen,
    isShowTagsView,
    isShowSettings,
    isShowLogo,
    isMobile,
    layoutClass,
    toggleSidebar,
    closeSidebar,
  };
}
