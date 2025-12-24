<template>
  <el-config-provider :locale="locale" :size="size">
    <!-- 워터마크 활성화 -->
    <el-watermark
      :font="{ color: fontColor }"
      :content="showWatermark ? defaultSettings.watermarkContent : ''"
      :z-index="9999"
      class="wh-full"
    >
      <router-view />

      <!-- AI 어시스턴트 -->
      <AiAssistant v-if="enableAiAssistant" />
    </el-watermark>
  </el-config-provider>
</template>

<script setup lang="ts">
import { useApp스토어, useSettings스토어, useUser스토어 } from "@/store";
import { defaultSettings } from "@/settings";
import { ThemeMode, ComponentSize } from "@/enums";
import AiAssistant from "@/components/AiAssistant/index.vue";

const app스토어 = useApp스토어();
const settings스토어 = useSettings스토어();
const user스토어 = useUser스토어();

const locale = computed(() => app스토어.locale);
const size = computed(() => app스토어.size as ComponentSize);
const showWatermark = computed(() => settings스토어.showWatermark);

// AI 어시스턴트가 활성화되고 사용자가 로그인한 경우에만 표시합니다
// userInfo를 반응형 종속성으로 사용하여 사용자가 로그아웃할 때 자동으로 업데이트됩니다
const enableAiAssistant = computed(() => {
  const isEnabled = settings스토어.enableAiAssistant;
  const isLoggedIn = user스토어.userInfo && Object.keys(user스토어.userInfo).length > 0;
  return isEnabled && isLoggedIn;
});

// 밝은/어두운 테마 워터마크 글꼴 색상 적응
const fontColor = computed(() => {
  return settings스토어.theme === ThemeMode.DARK ? "rgba(255, 255, 255, .15)" : "rgba(0, 0, 0, .15)";
});
</script>
