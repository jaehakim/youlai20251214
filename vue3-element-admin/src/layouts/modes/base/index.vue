<template>
  <div class="layout" :class="layoutClass">
    <!-- 모바일 오버레이 레이어 - 사이드바 열릴 때 표시 -->
    <div v-if="isMobile && isSidebarOpen" class="layout__overlay" @click="closeSidebar" />

    <!-- 레이아웃 콘텐츠 슬롯 - 다양한 레이아웃 모드의 구체적인 콘텐츠 -->
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { useLayout, useDeviceDetection } from "@/composables";

/// Layout-related functionality and state management
const { layoutClass, isSidebarOpen, closeSidebar } = useLayout();

/// Device detection for responsive layout
const { isMobile } = useDeviceDetection();
</script>

<style lang="scss" scoped>
.layout {
  width: 100%;
  height: 100%;

  &__overlay {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 999;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.3);
  }
}
</style>
