import { useRoute } from "vue-router";
import { useApp스토어, usePermission스토어 } from "@/저장소";

/**
 * 레이아웃메뉴단일처리逻辑
 */
export function useLayoutMenu() {
  const route = useRoute();
  const app스토어 = useApp스토어();
  const permission스토어 = usePermission스토어();

  // 상단메뉴단일활성화경로
  const activeTopMenuPath = computed(() => app스토어.activeTopMenuPath);

  // 常规라우팅（왼쪽메뉴단일또는상단메뉴단일）
  const routes = computed(() => permission스토어.routes);

  // 혼합레이아웃왼쪽메뉴단일라우팅
  const sideMenuRoutes = computed(() => permission스토어.mixLayoutSideMenus);

  // 当前활성화의메뉴단일
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
