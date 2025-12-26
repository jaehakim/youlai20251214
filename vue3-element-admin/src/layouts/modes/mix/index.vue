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

        <!-- 우측 작업 영역 -->
        <div class="layout__header-actions">
          <NavbarActions />
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
        <!-- 사이드바 전환 버튼 -->
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
import NavbarActions from "../../components/NavBar/components/NavbarActions.vue";
import TagsView from "../../components/TagsView/index.vue";
import AppMain from "../../components/AppMain/index.vue";
import MenuItem from "../../components/Menu/components/MenuItem.vue";
import Hamburger from "@/components/Hamburger/index.vue";
import variables from "@/styles/variables.module.scss";
import { isExternal } from "@/utils/index";
import { useAppStore, usePermissionStore } from "@/store";

const route = useRoute();

// 레이아웃 관련파라미터
const { isShowTagsView, isShowLogo, isSidebarOpen, toggleSidebar } = useLayout();

// 메뉴관련
const { sideMenuRoutes, activeTopMenuPath } = useLayoutMenu();

// 반응형 창 크기
const { width } = useWindowSize();

// 소형 스크린 기기에서만(모바일 기기)일 때만 Logo 접기(아이콘만 표시, 텍스트 숨기기)
const isLogoCollapsed = computed(() => width.value < 768);

// 현재 활성화된 메뉴
const activeLeftMenuPath = computed(() => {
  const { meta, path } = route;
  // activeMenu가 설정되어 있으면 사용
  if ((meta?.activeMenu as unknown as string) && typeof meta.activeMenu === "string") {
    return meta.activeMenu as unknown as string;
  }
  return path;
});

/**
 * 경로 파싱 - 혼합 모드에서 왼쪽 메뉴는 최상위 메뉴 아래의 하위 메뉴부터 시작
 * 따라서 최상위 메뉴 경로를 결합해야 함
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

// 라우팅 변경 감시, 왼쪽 메뉴가 TagsView 전환에 따라 올바르게 활성화되도록 보장
watch(
  () => route.path,
  (newPath: string) => {
    // 최상위 경로 조회
    const topMenuPath =
      newPath.split("/").filter(Boolean).length > 1 ? newPath.match(/^\/[^/]+/)?.[0] || "/" : "/";

    // 현재 경로가 현재 활성화된 상단 메뉴에 속하면
    if (newPath.startsWith(activeTopMenuPath.value)) {
      // no-op
    }
    // 경로가 최상위 메뉴로 변경되면, 상단 메뉴 및 왼쪽 메뉴 모두 업데이트 보장
    else if (topMenuPath !== activeTopMenuPath.value) {
      const appStore = useAppStore();
      const permissionStore = usePermissionStore();

      appStore.activeTopMenu(topMenuPath);
      permissionStore.setMixLayoutSideMenus(topMenuPath);
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
