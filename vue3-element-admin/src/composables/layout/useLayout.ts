import { useAppStore, useSettingsStore } from "@/store";
import { defaultSettings } from "@/settings";

/**
 * 레이아웃 관련 공통 로직
 */
export function useLayout() {
  const appStore = useAppStore();
  const settingsStore = useSettingsStore();

  // 현재 레이아웃 모드 계산
  const currentLayout = computed(() => settingsStore.layout);

  // 사이드바 펼침 상태
  const isSidebarOpen = computed(() => appStore.sidebar.opened);

  // 태그뷰 표시 여부
  const isShowTagsView = computed(() => settingsStore.showTagsView);

  // 설정 패널 표시 여부
  const isShowSettings = computed(() => defaultSettings.showSettings);

  // 로고 표시 여부
  const isShowLogo = computed(() => settingsStore.showAppLogo);

  // 모바일 기기 여부
  const isMobile = computed(() => appStore.device === "mobile");

  // 레이아웃 CSS 클래스
  const layoutClass = computed(() => ({
    hideSidebar: !appStore.sidebar.opened,
    openSidebar: appStore.sidebar.opened,
    mobile: appStore.device === "mobile",
    [`layout-${settingsStore.layout}`]: true,
  }));

  /**
   * 사이드바 펼침/접힘 상태 전환 처리
   */
  function toggleSidebar() {
    appStore.toggleSidebar();
  }

  /**
   * 사이드바 닫기(모바일)
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
