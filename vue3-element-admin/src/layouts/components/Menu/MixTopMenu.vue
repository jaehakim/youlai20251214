<!-- 혼합 레이아웃 상단 메뉴 -->
<template>
  <el-menu
    mode="horizontal"
    :default-active="activeTopMenuPath"
    :background-color="
      theme === 'dark' || sidebarColorScheme === SidebarColor.CLASSIC_BLUE
        ? variables['menu-background']
        : undefined
    "
    :text-color="
      theme === 'dark' || sidebarColorScheme === SidebarColor.CLASSIC_BLUE
        ? variables['menu-text']
        : undefined
    "
    :active-text-color="
      theme === 'dark' || sidebarColorScheme === SidebarColor.CLASSIC_BLUE
        ? variables['menu-active-text']
        : undefined
    "
    @select="handleMenuSelect"
  >
    <el-menu-item v-for="menuItem in processedTopMenus" :key="menuItem.path" :index="menuItem.path">
      <MenuItemContent
        v-if="menuItem.meta"
        :icon="menuItem.meta.icon"
        :title="menuItem.meta.title"
      />
    </el-menu-item>
  </el-menu>
</template>

<script lang="ts" setup>
import MenuItemContent from "./components/MenuItemContent.vue";

defineOptions({
  name: "MixTopMenu",
});

import { LocationQueryRaw, RouteRecordRaw } from "vue-router";
import { usePermission스토어, useApp스토어, useSettings스토어 } from "@/store";
import variables from "@/styles/variables.module.scss";
import { SidebarColor } from "@/enums/settings/theme-enum";

const router = useRouter();
const app스토어 = useApp스토어();
const permission스토어 = usePermission스토어();
const settings스토어 = useSettings스토어();

// 테마 가져오기
const theme = computed(() => settings스토어.theme);

// 밝은 테마의 사이드바 색상 구성표 가져오기
const sidebarColorScheme = computed(() => settings스토어.sidebarColorScheme);

// 상단 메뉴 목록
const topMenus = ref<RouteRecordRaw[]>([]);

// 처리된 상단 메뉴 목록 - 유일한 자식 메뉴 제목 지능형 표시
const processedTopMenus = computed(() => {
  return topMenus.value.map((route) => {
    // 라우트가 alwaysShow = true로 설정되었거나 자식 메뉴가 없으면 원본 라우트를 직접 반환
    if (route.meta?.alwaysShow || !route.children || route.children.length === 0) {
      return route;
    }

    // 숨겨지지 않은 자식 메뉴 필터링
    const visibleChildren = route.children.filter((child) => !child.meta?.hidden);

    // 숨겨지지 않은 자식 메뉴가 하나뿐이면 자식 메뉴 정보 표시
    if (visibleChildren.length === 1) {
      const onlyChild = visibleChildren[0];
      return {
        ...route,
        meta: {
          ...route.meta,
          title: onlyChild.meta?.title || route.meta?.title,
          icon: onlyChild.meta?.icon || route.meta?.icon,
        },
      };
    }

    // 기타 경우 원본 라우트 반환
    return route;
  });
});

/**
 * 메뉴 클릭 이벤트 처리, 상단 메뉴 전환 및 해당 왼쪽 메뉴 로드
 * @param routePath 클릭한 메뉴 경로
 */
const handleMenuSelect = (routePath: string) => {
  updateMenuState(routePath);
};

/**
 * 메뉴 상태 업데이트 - 클릭 및 라우트 변화 상황을 동시에 처리
 * @param topMenuPath 상위 메뉴 경로
 * @param skipNavigation 네비게이션 스킵 여부（라우트 변화시 true, 메뉴 클릭시 false）
 */
const updateMenuState = (topMenuPath: string, skipNavigation = false) => {
  // 다르면 업데이트, 중복 작업 방지
  if (topMenuPath !== app스토어.activeTopMenuPath) {
    app스토어.activeTopMenu(topMenuPath); // 활성화된 상단 메뉴 설정
    permission스토어.setMixLayoutSideMenus(topMenuPath); // 혼합 레이아웃 왼쪽 메뉴 설정
  }

  // 메뉴 클릭 및 상태가 변경된 경우에만 네비게이션 수행
  if (!skipNavigation) {
    navigateToFirstLeftMenu(permission스토어.mixLayoutSideMenus); // 왼쪽 첫 번째 메뉴로 이동
  }
};

/**
 * 왼쪽 첫 번째 접근 가능한 메뉴로 이동
 * @param menus 왼쪽 메뉴 목록
 */
const navigateToFirstLeftMenu = (menus: RouteRecordRaw[]) => {
  if (menus.length === 0) return;

  const [firstMenu] = menus;

  // 첫 번째 메뉴에 하위 메뉴가 있으면, 첫 번째 하위 메뉴로 재귀적으로 이동
  if (firstMenu.children && firstMenu.children.length > 0) {
    navigateToFirstLeftMenu(firstMenu.children as RouteRecordRaw[]);
  } else if (firstMenu.name) {
    router.push({
      name: firstMenu.name,
      query:
        typeof firstMenu.meta?.params === "object"
          ? (firstMenu.meta.params as LocationQueryRaw)
          : undefined,
    });
  }
};

// 현재 라우트 경로의 상단 메뉴 경로 가져오기
const activeTopMenuPath = computed(() => app스토어.activeTopMenuPath);

onMounted(() => {
  topMenus.value = permission스토어.routes.filter((item) => !item.meta || !item.meta.hidden);
  // 상단 메뉴 초기화
  const currentTopMenuPath =
    useRoute().path.split("/").filter(Boolean).length > 1
      ? useRoute().path.match(/^\/[^/]+/)?.[0] || "/"
      : "/";
  app스토어.activeTopMenu(currentTopMenuPath); // 활성화된 상단 메뉴 설정
  permission스토어.setMixLayoutSideMenus(currentTopMenuPath); // 혼합 레이아웃 왼쪽 메뉴 설정
});

// 라우트 변화 감시, 상단 메뉴 및 왼쪽 메뉴의 활성화 상태 동시 업데이트
watch(
  () => router.currentRoute.value.path,
  (newPath) => {
    if (newPath) {
      // 상위 경로 추출
      const topMenuPath =
        newPath.split("/").filter(Boolean).length > 1 ? newPath.match(/^\/[^/]+/)?.[0] || "/" : "/";

      // 공개 방법을 사용하여 메뉴 상태 업데이트, 네비게이션 스킵（라우트가 이미 변경되었으므로）
      updateMenuState(topMenuPath, true);
    }
  }
);
</script>

<style lang="scss" scoped>
.el-menu {
  width: 100%;
  height: 100%;

  &--horizontal {
    height: $navbar-height !important;

    // 메뉴 항목이 수직 중앙에 있는지 확인
    :deep(.el-menu-item) {
      height: 100%;
      line-height: $navbar-height;
    }

    // 기본 하단 테두리 제거
    &:after {
      display: none;
    }
  }
}
</style>
