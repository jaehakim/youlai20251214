<template>
  <div class="navbar">
    <div class="flex-y-center">
      <!-- 메뉴 축소 버튼 -->
      <Hamburger :is-active="isSidebarOpened" @toggle-click="toggleSideBar" />
      <!-- 브레드크럼 네비게이션 -->
      <Breadcrumb />
    </div>
    <!-- 네비게이션 바 작업 영역 -->
    <div class="navbar__actions">
      <Navbar액션s />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from "@/store";
import Hamburger from "@/components/Hamburger/index.vue";
import Breadcrumb from "@/components/Breadcrumb/index.vue";
import Navbar액션s from "./components/Navbar액션s.vue";

const appStore = useAppStore();

// 사이드바 전개 상태
const isSidebarOpened = computed(() => appStore.sidebar.opened);

// 사이드바 전개/축소 상태 토글
function toggleSideBar() {
  console.log("🔄 Hamburger clicked! Current state:", isSidebarOpened.value);
  console.log("🔄 Device type:", appStore.device);
  appStore.toggleSidebar();
  console.log("🔄 New state:", appStore.sidebar.opened);
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $navbar-height;

  &__actions {
    display: flex;
    align-items: center;
    height: 100%;
  }
}
</style>
