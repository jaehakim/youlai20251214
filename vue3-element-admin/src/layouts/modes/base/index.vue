<template>
  <div class="layout" :class="layoutClass">
    <!-- 모바일遮罩层 - 当侧边열열기시표시 -->
    <div v-if="isMobile && isSidebarOpen" class="layout__overlay" @click="closeSidebar" />

    <!-- 布局내용슬롯 - 各种布局模式의具体내용 -->
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
