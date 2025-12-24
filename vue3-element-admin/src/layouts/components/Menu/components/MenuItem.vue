<template>
  <div v-if="!item.meta || !item.meta.hidden">
    <!--【리프 노드】리프 노드 또는 유일한 자식 노드를 표시하고 부모 노드가 항상 표시하도록 구성하지 않음 -->
    <template
      v-if="
        // 항상 표시하도록 구성되지 않음, 유일한 자식 노드를 사용하여 부모 노드를 리프 노드로 표시
        (hasOneShowingChild(item.children, item) &&
          !item.meta?.alwaysShow &&
          (!onlyOneChild.children || onlyOneChild.noShowingChildren)) ||
        // 항상 표시하도록 구성되었더라도 자식 노드가 없으면 리프 노드로 표시
        (item.meta?.alwaysShow && !item.children)
      "
    >
      <AppLink
        v-if="onlyOneChild.meta"
        :to="{
          path: resolvePath(onlyOneChild.path),
          query: onlyOneChild.meta.params,
        }"
      >
        <el-menu-item
          :index="resolvePath(onlyOneChild.path)"
          :class="{ 'submenu-title-noDropdown': !isNest }"
        >
          <MenuItemContent
            v-if="onlyOneChild.meta"
            :icon="onlyOneChild.meta.icon || item.meta?.icon"
            :title="onlyOneChild.meta.title"
          />
        </el-menu-item>
      </AppLink>
    </template>

    <!--【비리프 노드】여러 자식 노드를 포함하는 부모 메뉴 또는 항상 표시되는 단일 자식 노드 표시 -->
    <el-sub-menu v-else :index="resolvePath(item.path)" :data-path="item.path" teleported>
      <template #title>
        <MenuItemContent v-if="item.meta" :icon="item.meta.icon" :title="item.meta.title" />
      </template>

      <MenuItem
        v-for="child in item.children"
        :key="child.path"
        :is-nest="true"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup lang="ts">
import MenuItemContent from "./MenuItemContent.vue";

defineOptions({
  name: "MenuItem",
  inheritAttrs: false,
});

import path from "path-browserify";
import { RouteRecordRaw } from "vue-router";

import { isExternal } from "@/utils";

const props = defineProps({
  /**
   * 현재 라우트 객체
   */
  item: {
    type: Object as PropType<RouteRecordRaw>,
    required: true,
  },

  /**
   * 부모 완전 경로
   */
  basePath: {
    type: String,
    required: true,
  },

  /**
   * 중첩 라우트 여부
   */
  isNest: {
    type: Boolean,
    default: false,
  },
});

// 표시 가능한 유일한 자식 노드
const onlyOneChild = ref();

/**
 * 가시 자식 노드가 하나만 있는지 확인
 *
 * @param children 자식 라우트 배열
 * @param parent 부모 라우트
 * @returns 가시 자식 노드가 하나만 있는지 여부
 */
function hasOneShowingChild(children: RouteRecordRaw[] = [], parent: RouteRecordRaw) {
  // 표시 가능한 자식 노드 필터링
  const showingChildren = children.filter((route: RouteRecordRaw) => {
    if (!route.meta?.hidden) {
      onlyOneChild.value = route;
      return true;
    }
    return false;
  });

  // 노드 하나만 있음
  if (showingChildren.length === 1) {
    return true;
  }

  // 자식 노드가 없을 때
  if (showingChildren.length === 0) {
    // 부모 노드를 유일한 표시 노드로 설정하고 자식 노드 없음으로 표시
    onlyOneChild.value = { ...parent, path: "", noShowingChildren: true };
    return true;
  }
  return false;
}

/**
 * 전체 경로 가져오기, 외부 링크 적응
 *
 * @param routePath 라우트 경로
 * @returns 절대 경로
 */
function resolvePath(routePath: string) {
  if (isExternal(routePath)) return routePath;
  if (isExternal(props.basePath)) return props.basePath;

  // 부모 경로 및 현재 경로 결합
  return path.resolve(props.basePath, routePath);
}
</script>

<style lang="scss">
.hideSidebar {
  .submenu-title-noDropdown {
    position: relative;

    & > span {
      display: inline-block;
      visibility: hidden;
      width: 0;
      height: 0;
      overflow: hidden;
    }
  }

  .el-sub-menu {
    overflow: hidden;

    & > .el-sub-menu__title {
      .sub-el-icon {
        margin-left: 19px;
      }

      .el-sub-menu__icon-arrow {
        display: none;
      }
    }
  }

  .el-menu--collapse {
    width: $sidebar-width-collapsed;

    .el-sub-menu {
      & > .el-sub-menu__title > span {
        display: inline-block;
        visibility: hidden;
        width: 0;
        height: 0;
        overflow: hidden;
      }
    }
  }
}

html.dark {
  .el-menu-item:hover {
    background-color: $menu-hover;
  }
}

html.sidebar-color-blue {
  .el-menu-item:hover {
    background-color: $menu-hover;
  }
}

// 부모 메뉴 활성화 상태 스타일 - 자식 메뉴 활성화시, 부모 메뉴가 활성화 상태 표시
.el-sub-menu {
  // 부모 메뉴가 활성 자식 메뉴를 포함할 때의 스타일
  &.has-active-child > .el-sub-menu__title {
    color: var(--el-color-primary) !important;
    background-color: var(--el-color-primary-light-9) !important;

    .menu-icon {
      color: var(--el-color-primary) !important;
    }
  }

  // 어두운 테마의 부모 메뉴 활성화 상태
  html.dark & {
    &.has-active-child > .el-sub-menu__title {
      color: var(--el-color-primary-light-3) !important;
      background-color: rgba(64, 128, 255, 0.15) !important;

      .menu-icon {
        color: var(--el-color-primary-light-3) !important;
      }
    }
  }

  // 진한 파란색 사이드바 색상 구성표의 부모 메뉴 활성화 상태
  html.sidebar-color-blue & {
    &.has-active-child > .el-sub-menu__title {
      color: var(--el-color-primary-light-3) !important;
      background-color: rgba(64, 128, 255, 0.2) !important;

      .menu-icon {
        color: var(--el-color-primary-light-3) !important;
      }
    }
  }
}
</style>
