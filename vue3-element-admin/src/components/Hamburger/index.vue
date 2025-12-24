<template>
  <div class="hamburger-wrapper" @click="toggleClick">
    <div :class="['i-svg:collapse', { hamburger: true, 'is-active': isActive }, hamburgerClass]" />
  </div>
</template>

<script setup lang="ts">
import { useSettings스토어 } from "@/store";
import { ThemeMode, SidebarColor } from "@/enums/settings/theme-enum";
import { LayoutMode } from "@/enums/settings/layout-enum";

defineProps({
  isActive: { type: Boolean, required: true },
});

const emit = defineEmits(["toggleClick"]);

const settings스토어 = useSettings스토어();
const layout = computed(() => settings스토어.layout);

const hamburgerClass = computed(() => {
  // 어두운 테마인 경우
  if (settings스토어.theme === ThemeMode.DARK) {
    return "hamburger--white";
  }

  // 혼합 레이아웃 && 사이드바 색 구성 방식이 클래식 파란색인 경우
  if (
    layout.value === LayoutMode.MIX &&
    settings스토어.sidebarColorScheme === SidebarColor.CLASSIC_BLUE
  ) {
    return "hamburger--white";
  }

  // 기본값 빈 문자열 반환
  return "";
});

function toggleClick() {
  emit("toggleClick");
}
</script>

<style scoped lang="scss">
.hamburger-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 15px;
  cursor: pointer;

  .hamburger {
    vertical-align: middle;
    transform: scaleX(-1);
    transition: transform 0.3s ease;

    &--white {
      color: #fff;
    }

    &.is-active {
      transform: scaleX(1);
    }
  }
}
</style>
