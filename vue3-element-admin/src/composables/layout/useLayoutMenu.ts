import { useRoute } from "vue-router";
import { useAppStore, usePermissionStore } from "@/store";

/**
 * 레이아웃 메뉴 처리 로직
 */
export function useLayoutMenu() {
  const route = useRoute();
  const appStore = useAppStore();
  const permissionStore = usePermissionStore();

  // 상단 메뉴 활성화 경로
  const activeTopMenuPath = computed(() => appStore.activeTopMenuPath);

  // 일반 라우팅(왼쪽 메뉴 또는 상단 메뉴)
  const routes = computed(() => permissionStore.routes);

  // 혼합 레이아웃 왼쪽 메뉴 라우팅
  const sideMenuRoutes = computed(() => permissionStore.mixLayoutSideMenus);

  // 현재 활성화된 메뉴
  const activeMenu = computed(() => {
    const { meta, path } = route;

    // activeMenu가 설정되어 있으면 사용
    if (meta?.activeMenu) {
      return meta.activeMenu;
    }

    return path;
  });

  return {
    routes,
    sideMenuRoutes,
    activeMenu,
    activeTopMenuPath,
  };
}
