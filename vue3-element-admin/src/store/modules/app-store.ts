import { defaultSettings } from "@/settings";

// Element Plus 영문 언어 패키지 가져오기
import zhCn from "element-plus/es/locale/lang/zh-cn";
import en from "element-plus/es/locale/lang/en";
import ko from "element-plus/es/locale/lang/ko";
import { store } from "@/store";
import { DeviceEnum } from "@/enums/settings/device-enum";
import { SidebarStatus } from "@/enums/settings/layout-enum";
import { STORAGE_KEYS } from "@/constants";

export const useAppStore = defineStore("app", () => {
  // 디바이스 유형
  const device = useStorage(STORAGE_KEYS.DEVICE, DeviceEnum.DESKTOP);
  // 레이아웃 크기
  const size = useStorage(STORAGE_KEYS.SIZE, defaultSettings.size);
  // 언어
  const language = useStorage(STORAGE_KEYS.LANGUAGE, defaultSettings.language);
  // 사이드바 상태
  const sidebarStatus = useStorage(STORAGE_KEYS.SIDEBAR_STATUS, SidebarStatus.CLOSED);
  const sidebar = reactive({
    opened: sidebarStatus.value === SidebarStatus.OPENED,
    withoutAnimation: false,
  });

  // 상단 메뉴 활성화 경로
  const activeTopMenuPath = useStorage(STORAGE_KEYS.ACTIVE_TOP_MENU_PATH, "");

  /**
   * 언어 식별자에 따라 해당 언어 패키지 로드
   */
  const locale = computed(() => {
    if (language?.value == "en") {
      return en;
    } else if (language?.value == "ko") {
      return ko;
    } else {
      return zhCn;
    }
  });

  // 사이드바 전환
  function toggleSidebar() {
    sidebar.opened = !sidebar.opened;
    sidebarStatus.value = sidebar.opened ? SidebarStatus.OPENED : SidebarStatus.CLOSED;
  }

  // 사이드바 닫기
  function closeSideBar() {
    sidebar.opened = false;
    sidebarStatus.value = SidebarStatus.CLOSED;
  }

  // 사이드바 열기
  function openSideBar() {
    sidebar.opened = true;
    sidebarStatus.value = SidebarStatus.OPENED;
  }

  // 디바이스 전환
  function toggleDevice(val: string) {
    device.value = val;
  }

  /**
   * 레이아웃 크기 변경
   *
   * @param val 레이아웃 크기 default | small | large
   */
  function changeSize(val: string) {
    size.value = val;
  }
  /**
   * 언어 전환
   *
   * @param val
   */
  function changeLanguage(val: string) {
    language.value = val;
  }
  /**
   * 혼합 모드 상단 전환
   */
  function activeTopMenu(val: string) {
    activeTopMenuPath.value = val;
  }
  return {
    device,
    sidebar,
    language,
    locale,
    size,
    activeTopMenu,
    toggleDevice,
    changeSize,
    changeLanguage,
    toggleSidebar,
    closeSideBar,
    openSideBar,
    activeTopMenuPath,
  };
});

/**
 * 컴포넌트 외부(예: Pinia 스토어 내)에서 Pinia가 제공하는 store 인스턴스를 사용하기 위한 함수
 * 공식 문서에서 컴포넌트 외부에서 Pinia 스토어를 사용하는 방법 설명:
 * https://pinia.vuejs.org/core-concepts/outside-component-usage.html#using-a-store-outside-of-a-component
 */
export function useAppStoreHook() {
  return useAppStore(store);
}
