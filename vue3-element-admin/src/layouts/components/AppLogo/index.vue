<template>
  <div class="logo">
    <transition enter-active-class="animate__animated animate__fadeInLeft">
      <router-link :key="+collapse" class="wh-full flex-center" to="/">
        <img :src="logo" class="w20px h20px" />
        <span v-if="!collapse" class="title">
          {{ defaultSettings.title }}
        </span>
      </router-link>
    </transition>
  </div>
</template>

<script lang="ts" setup>
import { defaultSettings } from "@/settings";
import logo from "@/assets/logo.png";

defineProps({
  collapse: {
    type: Boolean,
    required: true,
  },
});
</script>

<style lang="scss" scoped>
.logo {
  width: 100%;
  height: $navbar-height;
  background-color: $sidebar-logo-background;

  .title {
    flex-shrink: 0;
    margin-left: 10px;
    font-size: 14px;
    font-weight: bold;
    color: $sidebar-logo-text-color;
  }
}
</style>

<style lang="scss">
// 상단 레이아웃 및 혼합 레이아웃의 특별 처리
.layout-top,
.layout-mix {
  .logo {
    background-color: transparent !important;

    .title {
      color: var(--menu-text);
    }
  }
}

// 넓은 화면: openSidebar 상태에서 전체 Logo + 텍스트 표시
.openSidebar {
  &.layout-top .layout__header-left .logo,
  &.layout-mix .layout__header-logo .logo {
    width: $sidebar-width; // 210px, logo + 텍스트 표시
  }
}

// 좁은 화면: hideSidebar 상태에서 Logo 아이콘만 표시
.hideSidebar {
  &.layout-top .layout__header-left .logo,
  &.layout-mix .layout__header-logo .logo {
    width: $sidebar-width-collapsed; // 54px, logo만 표시
  }

  // 텍스트 숨기기, 아이콘만 표시
  .logo .title {
    display: none;
  }
}
</style>
