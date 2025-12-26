import { useApp스토어, useSettings스토어 } from "@/저장소";
import { defaultSettings } from "@/settings";

/**
 * 레이아웃 관련 공통 로직
 */
export function useLayout() {
  const app스토어 = useApp스토어();
  const settings스토어 = useSettings스토어();

  // 현재 레이아웃 모드 계산
  const currentLayout = computed(() => settings스토어.layout);

  // 사이드바 펼침 상태
  const isSidebarOpen = computed(() => app스토어.sidebar.opened);

  // 태그뷰 표시 여부
  const isShowTagsView = computed(() => settings스토어.showTagsView);

  // 설정 패널 표시 여부
  const isShowSettings = computed(() => defaultSettings.showSettings);

  // 로고 표시 여부
  const isShowLogo = computed(() => settings스토어.showAppLogo);

  // 모바일 기기 여부
  const isMobile = computed(() => app스토어.device === "mobile");

  // 레이아웃 CSS 클래스
  const layoutClass = computed(() => ({
    hideSidebar: !app스토어.sidebar.opened,
    openSidebar: app스토어.sidebar.opened,
    mobile: app스토어.device === "mobile",
    [`layout-${settings스토어.layout}`]: true,
  }));

  /**
   * 사이드바 펼침/접힘 상태 전환 처리
   */
  function toggleSidebar() {
    app스토어.toggleSidebar();
  }

  /**
   * 사이드바 닫기(모바일)
   */
  function closeSidebar() {
    app스토어.closeSideBar();
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
