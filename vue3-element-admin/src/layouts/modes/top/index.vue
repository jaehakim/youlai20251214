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

// 소형 스크린 기기에서만(모바일 기기)일 때만 Logo 접기(아이콘만 표시, 텍스트 숨기기)
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
      min-width: 0; // flex 축소 허용
      height: 100%;

      // Logo 스타일은 AppLogo 컴포넌트의 글로벌 스타일이 제어
      :deep(.logo) {
        flex-shrink: 0; // Logo 압축 방지
        height: $navbar-height;
      }
    }

    &-right {
      display: flex;
      flex-shrink: 0; // 작업 버튼 압축 방지
      align-items: center;
      height: 100%;
      padding-left: 12px;
    }

    // 메뉴스타일
    :deep(.el-menu--horizontal) {
      flex: 1;
      min-width: 0; // 메뉴 축소 허용
      height: $navbar-height;
      overflow: hidden; // 메뉴 넘침 방지
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

        // 부모 메뉴 활성화 상태 - 수평 레이아웃 전용
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

      // 하위 메뉴 팝업 위치 수정
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

// TagsView가 있을 때의 스타일 조정
.hasTagsView {
  :deep(.app-main) {
    height: calc(100vh - $navbar-height - $tags-view-height) !important;
  }
}
</style>
