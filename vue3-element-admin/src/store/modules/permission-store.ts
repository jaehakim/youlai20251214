import type { RouteRecordRaw } from "vue-router";
import { constantRoutes } from "@/router";
import { store } from "@/store";
import router from "@/router";

import MenuAPI, { type RouteVO } from "@/api/system/menu-api";
const modules = import.meta.glob("../../views/**/**.vue");
const Layout = () => import("../../layouts/index.vue");

export const usePermissionStore = defineStore("permission", () => {
  // 모든 라우트 (정적 라우트 + 동적 라우트)
  const routes = ref<RouteRecordRaw[]>([]);
  // 혼합 레이아웃의 왼쪽 메뉴 라우트
  const mixLayoutSideMenus = ref<RouteRecordRaw[]>([]);
  // 동적 라우트 생성 여부
  const isRouteGenerated = ref(false);

  /** 동적 라우트 생성 */
  async function generateRoutes(): Promise<RouteRecordRaw[]> {
    try {
      const data = await MenuAPI.getRoutes(); // 현재 로그인한 사용자의 메뉴 라우트 조회
      const dynamicRoutes = transformRoutes(data);

      routes.value = [...constantRoutes, ...dynamicRoutes];
      isRouteGenerated.value = true;

      return dynamicRoutes;
    } catch (error) {
      // 라우트 생성 실패, 상태 초기화
      isRouteGenerated.value = false;
      throw error;
    }
  }

  /** 혼합 레이아웃 왼쪽 메뉴 설정 */
  const setMixLayoutSideMenus = (parentPath: string) => {
    const parentMenu = routes.value.find((item) => item.path === parentPath);
    mixLayoutSideMenus.value = parentMenu?.children || [];
  };

  /** 라우트 상태 초기화 */
  const resetRouter = () => {
    // 동적으로 추가된 라우트 제거
    const constantRouteNames = new Set(constantRoutes.map((route) => route.name).filter(Boolean));
    routes.value.forEach((route) => {
      if (route.name && !constantRouteNames.has(route.name)) {
        router.removeRoute(route.name);
      }
    });

    // 모든 상태 초기화
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
 * 백엔드 라우트 데이터를 Vue Router 설정으로 변환
 * 컴포넌트 경로 매핑 및 Layout 계층 중첩 처리
 */
const transformRoutes = (routes: RouteVO[], isTopLevel: boolean = true): RouteRecordRaw[] => {
  return routes.map((route) => {
    const { component, children, ...args } = route;

    // 컴포넌트 처리: 최상위 또는 비 Layout 컴포넌트 유지, 중간 레벨 Layout은 undefined로 설정
    const processedComponent = isTopLevel || component !== "Layout" ? component : undefined;

    const normalizedRoute = { ...args } as RouteRecordRaw;

    if (!processedComponent) {
      // 다중 레벨 메뉴의 상위 메뉴, 컴포넌트 필요 없음
      normalizedRoute.component = undefined;
    } else {
      // 컴포넌트 동적 임포트, Layout 특수 처리, 컴포넌트를 찾지 못하면 404 반환
      normalizedRoute.component =
        processedComponent === "Layout"
          ? Layout
          : modules[`../../views/${processedComponent}.vue`] ||
            modules[`../../views/error/404.vue`];
    }

    // 자식 라우트 재귀 처리
    if (children && children.length > 0) {
      normalizedRoute.children = transformRoutes(children, false);
    }

    return normalizedRoute;
  });
};

/** 컴포넌트 외부에서 권한 스토어 사용 */
export function usePermissionStoreHook() {
  return usePermissionStore(store);
}
