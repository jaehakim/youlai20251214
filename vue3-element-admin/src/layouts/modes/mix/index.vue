<template>
  <BaseLayout>
    <!-- 상단메뉴열 -->
    <div class="layout__header">
      <div class="layout__header-content">
        <!-- Logo영역 -->
        <div v-if="isShowLogo" class="layout__header-logo">
          <AppLogo :collapse="isLogoCollapsed" />
        </div>

        <!-- 상단메뉴영역 -->
        <div class="layout__header-menu">
          <MixTopMenu />
        </div>

        <!-- 右侧작업영역 -->
        <div class="layout__header-actions">
          <Navbar액션s />
        </div>
      </div>
    </div>

    <!-- 주요내용영역컨테이너 -->
    <div class="layout__container">
      <!-- 왼쪽메뉴열 -->
      <div class="layout__sidebar--left" :class="{ 'layout__sidebar--collapsed': !isSidebarOpen }">
        <el-scrollbar>
          <el-menu
            :default-active="activeLeftMenuPath"
            :collapse="!isSidebarOpen"
            :collapse-transition="false"
            :unique-opened="false"
            :background-color="variables['menu-background']"
            :text-color="variables['menu-text']"
            :active-text-color="variables['menu-active-text']"
          >
            <MenuItem
              v-for="item in sideMenuRoutes"
              :key="item.path"
              :item="item"
              :base-path="resolvePath(item.path)"
            />
          </el-menu>
        </el-scrollbar>
        <!-- 侧边열切换버튼 -->
        <div class="layout__sidebar-toggle">
          <Hamburger :is-active="isSidebarOpen" @toggle-click="toggleSidebar" />
        </div>
      </div>

      <!-- 주요내용영역 -->
      <div :class="{ hasTagsView: isShowTagsView }" class="layout__main">
        <TagsView v-if="isShowTagsView" />
        <AppMain />
      </div>
    </div>
  </BaseLayout>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { useWindowSize } from "@vueuse/core";
import { useLayout, useLayoutMenu } from "@/composables";
import BaseLayout from "../base/index.vue";
import AppLogo from "../../components/AppLogo/index.vue";
import MixTopMenu from "../../components/Menu/MixTopMenu.vue";
import Navbar액션s from "../../components/NavBar/components/Navbar액션s.vue";
import TagsView from "../../components/TagsView/index.vue";
import AppMain from "../../components/AppMain/index.vue";
import MenuItem from "../../components/Menu/components/MenuItem.vue";
import Hamburger from "@/components/Hamburger/index.vue";
import variables from "@/styles/variables.module.scss";
import { isExternal } from "@/utils/index";
import { useApp스토어, usePermission스토어 } from "@/store";

const route = useRoute();

// 레이아웃 관련파라미터
const { isShowTagsView, isShowLogo, isSidebarOpen, toggleSidebar } = useLayout();

// 메뉴관련
const { sideMenuRoutes, activeTopMenuPath } = useLayoutMenu();

// 반응형 창 크기
const { width } = useWindowSize();

// 소형 스크린 기기에서만（모바일 기기）일 때만 접기Logo（오직표시아이콘，숨기기텍스트）
const isLogoCollapsed = computed(() => width.value < 768);

// 当前활성화의메뉴
const activeLeftMenuPath = computed(() => {
  const { meta, path } = route;
  // 만약설정됨activeMenu，그러면사용
  if ((meta?.activeMenu as unknown as string) && typeof meta.activeMenu === "string") {
    return meta.activeMenu as unknown as string;
  }
  return path;
});

/**
 * 파싱경로 - 혼합모드下，왼쪽메뉴예从최상위메뉴下의子메뉴시작의
 * 所以需해야拼接최상위메뉴경로
 */
function resolvePath(routePath: string) {
  if (isExternal(routePath)) {
    return routePath;
  }

  if (routePath.startsWith("/")) {
    return activeTopMenuPath.value + routePath;
  }
  return `${activeTopMenuPath.value}/${routePath}`;
}

// 감시라우팅변경，보장왼쪽메뉴能随TagsView切换而正确활성화
watch(
  () => route.path,
  (newPath: string) => {
    // 조회최상위경로
    const topMenuPath =
      newPath.split("/").filter(Boolean).length > 1 ? newPath.match(/^\/[^/]+/)?.[0] || "/" : "/";

    // 만약当前경로属于当前활성화의상단메뉴
    if (newPath.startsWith(activeTopMenuPath.value)) {
      // no-op
    }
    // 만약경로改变됨최상위메뉴，보장상단메뉴및왼쪽메뉴都업데이트
    else if (topMenuPath !== activeTopMenuPath.value) {
      const app스토어 = useApp스토어();
      const permission스토어 = usePermission스토어();

      app스토어.activeTopMenu(topMenuPath);
      permission스토어.setMixLayoutSideMenus(topMenuPath);
    }
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.layout {
  &__header {
    position: sticky;
    top: 0;
    z-index: 999;
    width: 100%;
    height: $navbar-height;
    background-color: var(--menu-background);
    border-bottom: 1px solid var(--el-border-color-lighter);

    &-content {
      display: flex;
      align-items: center;
      height: 100%;
      padding: 0;
    }

    &-logo {
      display: flex;
      flex-shrink: 0;
      align-items: center;
      justify-content: center;
      height: 100%;
    }

    &-menu {
      display: flex;
      flex: 1;
      align-items: center;
      min-width: 0;
      height: 100%;
      overflow: hidden;

      :deep(.el-menu) {
        height: 100%;
        background-color: transparent;
        border: none;
      }

      :deep(.el-menu--horizontal) {
        display: flex;
        align-items: center;
        height: 100%;

        .el-menu-item {
          height: 100%;
          line-height: $navbar-height;
          border-bottom: none;

          &.is-active {
            background-color: rgba(255, 255, 255, 0.12);
            border-bottom: 2px solid var(--el-color-primary);
          }
        }
      }
    }

    &-actions {
      display: flex;
      flex-shrink: 0;
      align-items: center;
      height: 100%;
      padding: 0 16px;
    }
  }

  &__container {
    display: flex;
    height: calc(100vh - $navbar-height);
    padding-top: 0;

    .layout__sidebar--left {
      position: relative;
      width: $sidebar-width;
      height: 100%;
      background-color: var(--menu-background);
      transition: width 0.28s;

      &.layout__sidebar--collapsed {
        width: $sidebar-width-collapsed !important;
      }

      :deep(.el-scrollbar) {
        height: calc(100vh - $navbar-height - 50px);
      }

      :deep(.el-menu) {
        height: 100%;
        border: none;
      }

      .layout__sidebar-toggle {
        position: absolute;
        bottom: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 100%;
        height: 50px;
        line-height: 50px;
        background-color: var(--menu-background);
        box-shadow: 0 0 6px -2px var(--el-color-primary);
      }
    }

    .layout__main {
      flex: 1;
      min-width: 0;
      height: 100%;
      margin-left: 0;
      overflow-y: auto;
    }
  }
}

/* 모바일스타일 */
:deep(.mobile) {
  .layout__container {
    .layout__sidebar--left {
      position: fixed;
      top: $navbar-height;
      bottom: 0;
      left: 0;
      z-index: 1000;
      transition: transform 0.28s;
    }
  }

  &.hideSidebar {
    .layout__sidebar--left {
      width: $sidebar-width !important;
      transform: translateX(-$sidebar-width);
    }
  }
}
:deep(.hasTagsView) {
  .app-main {
    height: calc(100vh - $navbar-height - $tags-view-height) !important;
  }
}
</style>
