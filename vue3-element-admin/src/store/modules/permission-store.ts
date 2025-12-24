import type { RouteRecordRaw } from "vue-router";
import { constantRoutes } from "@/router";
import { 저장소 } from "@/저장소";
import router from "@/router";

import MenuAPI, { type RouteVO } from "@/api/system/menu-api";
const modules = import.meta.glob("../../views/**/**.vue");
const Layout = () => import("../../layouts/index.vue");

export const usePermissionStore = defineStore("permission", () => {
  // 모든라우팅（静态라우팅 + 动态라우팅）
  const routes = 참조<RouteRecordRaw[]>([]);
  // 混合레이아웃의왼쪽메뉴단일라우팅
  const mixLayoutSideMenus = 참조<RouteRecordRaw[]>([]);
  // 动态라우팅是否已生成
  const isRouteGenerated = 참조(false);

  /** 生成动态라우팅 */
  async function generateRoutes(): Promise<RouteRecordRaw[]> {
    try {
      const data = await MenuAPI.getRoutes(); // 조회当前登录人의메뉴단일라우팅
      const dynamicRoutes = transformRoutes(data);

      routes.value = [...constantRoutes, ...dynamicRoutes];
      isRouteGenerated.value = true;

      return dynamicRoutes;
    } catch (error) {
      // 라우팅生成실패，초기화상태
      isRouteGenerated.value = false;
      throw error;
    }
  }

  /** 설정混合레이아웃왼쪽메뉴단일 */
  const setMixLayoutSideMenus = (parentPath: string) => {
    const parentMenu = routes.value.find((item) => item.path === parentPath);
    mixLayoutSideMenus.value = parentMenu?.children || [];
  };

  /** 초기화라우팅상태 */
  const resetRouter = () => {
    // 移除动态추가의라우팅
    const constantRouteNames = new Set(constantRoutes.map((route) => route.name).filter(Boolean));
    routes.value.forEach((route) => {
      if (route.name && !constantRouteNames.has(route.name)) {
        router.removeRoute(route.name);
      }
    });

    // 초기화모든상태
    routes.value = [...constantRoutes];
    mixLayoutSideMenus.value = [];
    isRouteGenerated.value = false;
  };

  return {
    routes,
    mixLayoutSideMenus,
    isRouteGenerated,
    generateRoutes,
    setMixLayoutSideMenus,
    resetRouter,
  };
});

/**
 * 변환백엔드라우팅데이터로Vue Router설정
 * 처리컴포넌트경로매핑및Layout层级嵌套
 */
const transformRoutes = (routes: RouteVO[], isTopLevel: boolean = true): RouteRecordRaw[] => {
  return routes.map((route) => {
    const { component, children, ...args } = route;

    // 처리컴포넌트：顶层或非Layout보유컴포넌트，내사이层Layout设로undefined
    const processedComponent = isTopLevel || component !== "Layout" ? component : undefined;

    const normalizedRoute = { ...args } as RouteRecordRaw;

    if (!processedComponent) {
      // 多级메뉴단일의父级메뉴단일，不필요해야컴포넌트
      normalizedRoute.component = undefined;
    } else {
      // 动态가져오기컴포넌트，Layout特殊처리，找不到컴포넌트시돌아가기404
      normalizedRoute.component =
        processedComponent === "Layout"
          ? Layout
          : modules[`../../views/${processedComponent}.vue`] ||
            modules[`../../views/error/404.vue`];
    }

    // 递归처리子라우팅
    if (children && children.length > 0) {
      normalizedRoute.children = transformRoutes(children, false);
    }

    return normalizedRoute;
  });
};

/** 非컴포넌트环境사용权限저장소 */
export function usePermissionStoreHook() {
  return usePermissionStore(저장소);
}
