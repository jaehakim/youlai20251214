import type { RouteRecordRaw } from "vue-router";
import NProgress from "@/utils/nprogress";
import router from "@/router";
import { usePermission스토어, useUser스토어 } from "@/저장소";

export function setupPermission() {
  const whiteList = ["/login"];

  router.beforeEach(async (to, from, next) => {
    NProgress.start();

    try {
      const isLoggedIn = useUser스토어().isLoggedIn();

      // 미로그인처리
      if (!isLoggedIn) {
        if (whiteList.includes(to.path)) {
          next();
        } else {
          next(`/login?redirect=${encodeURIComponent(to.fullPath)}`);
          NProgress.done();
        }
        return;
      }

      // 이미로그인로그인页重定에
      if (to.path === "/login") {
        next({ path: "/" });
        return;
      }

      const permission스토어 = usePermission스토어();
      const user스토어 = useUser스토어();

      // 动态라우팅生成
      if (!permission스토어.isRouteGenerated) {
        if (!user스토어.userInfo?.roles?.length) {
          await user스토어.getUserInfo();
        }

        const dynamicRoutes = await permission스토어.generateRoutes();
        dynamicRoutes.forEach((route: RouteRecordRaw) => {
          router.addRoute(route);
        });

        next({ ...to, replace: true });
        return;
      }

      // 라우팅404확인
      if (to.matched.length === 0) {
        next("/404");
        return;
      }

      // 动态标题설정
      const title = (to.params.title as string) || (to.query.title as string);
      if (title) {
        to.meta.title = title;
      }

      next();
    } catch (error) {
      // 오류처리：초기화상태그리고점프转로그인
      console.error("Route guard error:", error);
      await useUser스토어().resetAllState();
      next("/login");
      NProgress.done();
    }
  });

  router.afterEach(() => {
    NProgress.done();
  });
}
