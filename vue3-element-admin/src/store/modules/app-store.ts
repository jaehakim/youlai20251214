import { defaultSettings } from "@/settings";

// 가져오기 Element Plus 내英文语言패키지
import zhCn from "element-plus/es/locale/lang/zh-cn";
import en from "element-plus/es/locale/lang/en";
import { 저장소 } from "@/저장소";
import { DeviceEnum } from "@/enums/settings/device-enum";
import { SidebarStatus } from "@/enums/settings/layout-enum";
import { STORAGE_KEYS } from "@/constants";

export const useAppStore = defineStore("app", () => {
  // 设备타입
  const device = useStorage(STORAGE_KEYS.DEVICE, DeviceEnum.DESKTOP);
  // 레이아웃크기
  const size = useStorage(STORAGE_KEYS.SIZE, defaultSettings.size);
  // 语言
  const language = useStorage(STORAGE_KEYS.LANGUAGE, defaultSettings.language);
  // 측엣지열상태
  const sidebarStatus = useStorage(STORAGE_KEYS.SIDEBAR_STATUS, SidebarStatus.CLOSED);
  const sidebar = reactive({
    opened: sidebarStatus.value === SidebarStatus.OPENED,
    withoutAnimation: false,
  });

  // 상단메뉴단일激活경로
  const activeTopMenuPath = useStorage(STORAGE_KEYS.ACTIVE_TOP_MENU_PATH, "");

  /**
   * 에 따라语言标识读취对应의语言패키지
   */
  const locale = computed(() => {
    if (language?.value == "en") {
      return en;
    } else {
      return zhCn;
    }
  });

  // 切换측엣지열
  function toggleSidebar() {
    sidebar.opened = !sidebar.opened;
    sidebarStatus.value = sidebar.opened ? SidebarStatus.OPENED : SidebarStatus.CLOSED;
  }

  // 닫기측엣지열
  function closeSideBar() {
    sidebar.opened = false;
    sidebarStatus.value = SidebarStatus.CLOSED;
  }

  // 열기측엣지열
  function openSideBar() {
    sidebar.opened = true;
    sidebarStatus.value = SidebarStatus.OPENED;
  }

  // 切换设备
  function toggleDevice(val: string) {
    device.value = val;
  }

  /**
   * 改变레이아웃크기
   *
   * @param val 레이아웃크기 default | small | large
   */
  function changeSize(val: string) {
    size.value = val;
  }
  /**
   * 切换语言
   *
   * @param val
   */
  function changeLanguage(val: string) {
    language.value = val;
  }
  /**
   * 混合模式상단切换
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
 * 용도에컴포넌트외부（如에Pinia Store 내）사용 Pinia 提供의 저장소 实例。
 * 官方文档解释됨如何에컴포넌트외부사용 Pinia Store：
 * https://pinia.vuejs.org/core-concepts/outside-component-usage.html#using-a-저장소-outside-of-a-component
 */
export function useAppStoreHook() {
  return useAppStore(저장소);
}
