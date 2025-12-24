import { useRoute } from "vue-router";
import { useAppStore, usePermissionStore } from "@/저장소";

/**
 * 레이아웃메뉴단일처리逻辑
 */
export function useLayoutMenu() {
  const route = useRoute();
  const appStore = useAppStore();
  const permissionStore = usePermissionStore();

  // 상단메뉴단일激活경로
  const activeTopMenuPath = computed(() => appStore.activeTopMenuPath);

  // 常规라우팅（왼쪽메뉴단일或상단메뉴단일）
  const routes = computed(() => permissionStore.routes);

  // 混合레이아웃왼쪽메뉴단일라우팅
  const sideMenuRoutes = computed(() => permissionStore.mixLayoutSideMenus);

  // 当前激活의메뉴단일
  const activeMenu = computed(() => {
    const { meta, path } = route;

    // 만약설정됨activeMenu，그러면사용
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
