import { useRoute } from "vue-router";
import { useApp스토어, usePermission스토어 } from "@/저장소";

/**
 * 레이아웃 메뉴 처리 로직
 */
export function useLayoutMenu() {
  const route = useRoute();
  const app스토어 = useApp스토어();
  const permission스토어 = usePermission스토어();

  // 상단 메뉴 활성화 경로
  const activeTopMenuPath = computed(() => app스토어.activeTopMenuPath);

  // 일반 라우팅(왼쪽 메뉴 또는 상단 메뉴)
  const routes = computed(() => permission스토어.routes);

  // 혼합 레이아웃 왼쪽 메뉴 라우팅
  const sideMenuRoutes = computed(() => permission스토어.mixLayoutSideMenus);

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
