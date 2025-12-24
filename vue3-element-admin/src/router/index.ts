import type { App } from "vue";
import { createRouter, createWebHashHistory, type RouteRecordRaw } from "vue-router";

export const Layout = () => import("@/layouts/index.vue");

// 静态라우팅
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: "/redirect",
    component: Layout,
    meta: { hidden: true },
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue"),
      },
    ],
  },

  {
    path: "/login",
    component: () => import("@/views/login/index.vue"),
    meta: { hidden: true },
  },

  {
    path: "/",
    name: "/",
    component: Layout,
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        component: () => import("@/views/dashboard/index.vue"),
        // 용도 keep-alive 功能，필요해야与 SFC 내자동推导或显式声明의컴포넌트이름칭하나致
        // 参考文档: https://cn.vuejs.org/guide/built-ins/keep-alive.html#include-exclude
        name: "Dashboard",
        meta: {
          title: "dashboard",
          icon: "homepage",
          affix: true,
          keepAlive: true,
        },
      },
      {
        path: "401",
        component: () => import("@/views/error/401.vue"),
        meta: { hidden: true },
      },
      {
        path: "404",
        component: () => import("@/views/error/404.vue"),
        meta: { hidden: true },
      },
      {
        path: "profile",
        name: "Profile",
        component: () => import("@/views/profile/index.vue"),
        meta: { title: "개人내心", icon: "user", hidden: true },
      },
      {
        path: "my-notice",
        name: "MyNotice",
        component: () => import("@/views/system/notice/components/MyNotice.vue"),
        meta: { title: "我의通知", icon: "user", hidden: true },
      },
      {
        path: "/detail/:id(\\d+)",
        name: "DemoDetail",
        component: () => import("@/views/demo/detail.vue"),
        meta: { title: "상세页캐시", icon: "user", hidden: true, keepAlive: true },
      },
    ],
  },
];

/**
 * 创建라우팅
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes,
  // 새로고침시，滚动条位置还原
  scrollBehavior: () => ({ left: 0, top: 0 }),
});

// 全局등록 router
export function setupRouter(app: App<Element>) {
  app.use(router);
}

export default router;
