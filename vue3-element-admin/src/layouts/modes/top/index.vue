<template>
  <BaseLayout>
    <!-- 상단메뉴열 -->
    <div class="layout__header">
      <div class="layout__header-left">
        <!-- Logo -->
        <AppLogo v-if="isShowLogo" :collapse="isLogoCollapsed" />
        <!-- 메뉴 -->
        <BasicMenu :data="routes" menu-mode="horizontal" base-path="" />
      </div>
      <!-- 작업버튼 -->
      <div class="layout__header-right">
        <NavbarActions />
      </div>
    </div>

    <!-- 주요내용영역 -->
    <div :class="{ hasTagsView: isShowTagsView }" class="layout__main">
      <TagsView v-if="isShowTagsView" />
      <AppMain />
    </div>
  </BaseLayout>
</template>

<script setup lang="ts">
import { useLayout } from "@/composables/layout/useLayout";
import { useLayoutMenu } from "@/composables/layout/useLayoutMenu";
import BaseLayout from "../base/index.vue";
import AppLogo from "../../components/AppLogo/index.vue";
import BasicMenu from "../../components/Menu/BasicMenu.vue";
import NavbarActions from "../../components/NavBar/components/NavbarActions.vue";
import TagsView from "../../components/TagsView/index.vue";
import AppMain from "../../components/AppMain/index.vue";

// 레이아웃 관련파라미터
const { isShowTagsView, isShowLogo } = useLayout();

// 메뉴관련
const { routes } = useLayoutMenu();

// 반응형 창 크기
const { width } = useWindowSize();

// 소형 스크린 기기에서만（모바일 기기）일 때만 접기Logo（오직표시아이콘，숨기기텍스트）
const isLogoCollapsed = computed(() => width.value < 768);
</script>

<style lang="scss" scoped>
.layout {
  &__header {
    position: sticky;
    top: 0;
    z-index: 999;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    height: $navbar-height;
    background-color: $menu-background;

    &-left {
      display: flex;
      flex: 1;
      align-items: center;
      min-width: 0; // 허용flex축소
      height: 100%;

      // Logo스타일由AppLogo그룹개의全局스타일控制
      :deep(.logo) {
        flex-shrink: 0; // 방지Logo被压缩
        height: $navbar-height;
      }
    }

    &-right {
      display: flex;
      flex-shrink: 0; // 방지작업버튼被压缩
      align-items: center;
      height: 100%;
      padding-left: 12px;
    }

    // 메뉴스타일
    :deep(.el-menu--horizontal) {
      flex: 1;
      min-width: 0; // 허용메뉴축소
      height: $navbar-height;
      overflow: hidden; // 방지메뉴溢出
      line-height: $navbar-height;
      background-color: transparent;
      border: none;

      .el-menu-item {
        height: $navbar-height;
        line-height: $navbar-height;
      }

      .el-sub-menu {
        .el-sub-menu__title {
          height: $navbar-height;
          line-height: $navbar-height;
        }

        // 父메뉴激活상태 - 水平布局专用
        &.has-active-child {
          .el-sub-menu__title {
            color: var(--el-color-primary) !important;
            border-bottom: 2px solid var(--el-color-primary) !important;

            .menu-icon {
              color: var(--el-color-primary) !important;
            }
          }
        }
      }

      // 修复子메뉴弹出位置
      .el-menu--popup {
        min-width: 160px;
      }
    }
  }

  &__main {
    height: calc(100vh - $navbar-height);
    overflow-y: auto;
  }
}

// 当存在TagsView시의스타일调整
.hasTagsView {
  :deep(.app-main) {
    height: calc(100vh - $navbar-height - $tags-view-height) !important;
  }
}
</style>
