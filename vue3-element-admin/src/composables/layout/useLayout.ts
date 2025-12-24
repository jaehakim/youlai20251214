import { useApp스토어, useSettings스토어 } from "@/저장소";
import { defaultSettings } from "@/settings";

/**
 * 레이아웃 관련의通用逻辑
 */
export function useLayout() {
  const app스토어 = useApp스토어();
  const settings스토어 = useSettings스토어();

  // 계算当前레이아웃모드
  const currentLayout = computed(() => settings스토어.layout);

  // 측엣지열펼치기상태
  const isSidebarOpen = computed(() => app스토어.sidebar.opened);

  // 여부표시태그뷰
  const isShowTagsView = computed(() => settings스토어.showTagsView);

  // 여부표시설정패널
  const isShowSettings = computed(() => defaultSettings.showSettings);

  // 여부표시Logo
  const isShowLogo = computed(() => settings스토어.showAppLogo);

  // 여부모바일 기기
  const isMobile = computed(() => app스토어.device === "mobile");

  // 레이아웃CSS클래스
  const layoutClass = computed(() => ({
    hideSidebar: !app스토어.sidebar.opened,
    openSidebar: app스토어.sidebar.opened,
    mobile: app스토어.device === "mobile",
    [`layout-${settings스토어.layout}`]: true,
  }));

  /**
   * 처리切换측엣지열의펼치기/접기상태
   */
  function toggleSidebar() {
    app스토어.toggleSidebar();
  }

  /**
   * 닫기측엣지열（모바일）
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
