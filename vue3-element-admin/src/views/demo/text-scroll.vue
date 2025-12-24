<template>
  <div class="app-container">
    <!-- 기본 사용 방법 -->
    <TextScroll text="이것은 기본 스크롤 공지입니다. 기본적으로 왼쪽으로 스크롤합니다." typewriter />

    <!-- 다양한 타입 사용 -->
    <TextScroll type="success" text="이것은 성공 타입 스크롤 공지입니다" typewriter />

    <TextScroll type="warning" text="이것은 경고 타입 스크롤 공지입니다" />

    <TextScroll type="danger" text="이것은 위험 타입 스크롤 공지입니다" />

    <TextScroll type="info" text="이것은 정보 타입 스크롤 공지입니다" />

    <!-- 사용자 지정 속도 및 방향 -->
    <TextScroll text="이것은 느린 속도로 오른쪽으로 스크롤하는 공지입니다" :speed="30" direction="right" show-close />
  </div>
</template>

<script setup lang="ts">
import TextScroll from "@/components/TextScroll/index.vue";
</script>

<style lang="scss" scoped>
.app-container {
  :deep(.text-scroll-container) {
    margin-bottom: 20px;
  }
}
</style>
