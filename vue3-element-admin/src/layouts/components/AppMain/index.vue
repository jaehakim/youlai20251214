<template>
  <section class="app-main" :style="{ height: appMainHeight }">
    <router-view>
      <template #default="{ Component, route }">
        <transition enter-active-class="animate__animated animate__fadeIn" mode="out-in">
          <keep-alive :include="cachedViews">
            <component :is="currentComponent(Component, route)" :key="route.fullPath" />
          </keep-alive>
        </transition>
      </template>
    </router-view>

    <!-- 맨 위로 돌아가기 버튼 -->
    <el-backtop target=".app-main">
      <div class="i-svg:backtop w-6 h-6" />
    </el-backtop>
  </section>
</template>

<script setup lang="ts">
import { type RouteLocationNormalized } from "vue-router";
import { useSettings스토어, useTagsView스토어 } from "@/store";
import variables from "@/styles/variables.module.scss";
import Error404 from "@/views/error/404.vue";

const { cachedViews } = toRefs(useTagsView스토어());

// 현재 구성 요소
const wrapperMap = new Map<string, Component>();
const currentComponent = (component: Component, route: RouteLocationNormalized) => {
  if (!component) return;

  const { fullPath: componentName } = route; // 라우트 경로를 구성 요소 이름으로 사용
  let wrapper = wrapperMap.get(componentName);

  if (!wrapper) {
    wrapper = {
      name: componentName,
      render: () => {
        try {
          return h(component);
        } catch (error) {
          console.error(`Error rendering component for route: ${componentName}`, error);
          return h(Error404);
        }
      },
    };
    wrapperMap.set(componentName, wrapper);
  }

  // 구성 요소 수 제한 추가
  if (wrapperMap.size > 100) {
    const firstKey = wrapperMap.keys().next().value;
    if (firstKey) {
      wrapperMap.delete(firstKey);
    }
  }

  return h(wrapper);
};

const appMainHeight = computed(() => {
  if (useSettings스토어().showTagsView) {
    return `calc(100vh - ${variables["navbar-height"]} - ${variables["tags-view-height"]})`;
  } else {
    return `calc(100vh - ${variables["navbar-height"]})`;
  }
});
</script>

<style lang="scss" scoped>
.app-main {
  position: relative;
  overflow-y: auto;
  background-color: var(--el-bg-color-page);

  /* 레이아웃 전환 애니메이션 최적화 */
  &.animate__animated {
    animation-duration: 0.4s;
    animation-fill-mode: forwards;
  }

  &.animate__fadeOut {
    animation-timing-function: ease-in;
  }

  &.animate__fadeIn {
    animation-timing-function: ease-out;
  }
}
</style>
