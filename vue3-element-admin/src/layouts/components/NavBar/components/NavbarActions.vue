<template>
  <div :class="['navbar-actions', navbar액션sClass]">
    <!-- 데스크톱 도구 항목 -->
    <template v-if="isDesktop">
      <!-- 검색 -->
      <div class="navbar-actions__item">
        <MenuSearch />
      </div>

      <!-- 전체 화면 -->
      <div class="navbar-actions__item">
        <Fullscreen />
      </div>

      <!-- 레이아웃 크기 -->
      <div class="navbar-actions__item">
        <SizeSelect />
      </div>

      <!-- 언어 선택 -->
      <div class="navbar-actions__item">
        <LangSelect />
      </div>

      <!-- 알림 -->
      <div class="navbar-actions__item">
        <Notification />
      </div>
    </template>

    <!-- 사용자 메뉴 -->
    <div class="navbar-actions__item">
      <el-dropdown trigger="click">
        <div class="user-profile">
          <img class="user-profile__avatar" :src="userStore.userInfo.avatar" />
          <span class="user-profile__name">{{ userStore.userInfo.username }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleProfileClick">
              {{ t("navbar.profile") }}
            </el-dropdown-item>
            <el-dropdown-item divided @click="logout">
              {{ t("navbar.logout") }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 시스템 설정 -->
    <div
      v-if="defaultSettings.showSettings"
      class="navbar-actions__item"
      @click="handleSettingsClick"
    >
      <div class="i-svg:setting" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import { useRoute, useRouter } from "vue-router";
import { defaultSettings } from "@/settings";
import { DeviceEnum } from "@/enums/settings/device-enum";
import { useAppStore, useSettingsStore, useUserStore } from "@/store";
import { SidebarColor, ThemeMode } from "@/enums/settings/theme-enum";
import { LayoutMode } from "@/enums";

// 자식 컴포넌트 가져오기
import MenuSearch from "@/components/MenuSearch/index.vue";
import Fullscreen from "@/components/Fullscreen/index.vue";
import SizeSelect from "@/components/SizeSelect/index.vue";
import LangSelect from "@/components/LangSelect/index.vue";
import Notification from "@/components/Notification/index.vue";

const { t } = useI18n();
const appStore = useAppStore();
const setting스토어 = useSettingsStore();
const userStore = useUserStore();

const route = useRoute();
const router = useRouter();

// 데스크톱 장치 여부
const isDesktop = computed(() => appStore.device === DeviceEnum.DESKTOP);

/**
 * 개인 센터 페이지 열기
 */
function handleProfileClick() {
  router.push({ name: "Profile" });
}

// 테마 및 사이드바 색상 구성표에 따라 스타일 클래스 선택
const navbar액션sClass = computed(() => {
  const { theme, sidebarColorScheme, layout } = setting스토어;

  // 어두운 테마에서 모든 레이아웃이 흰색 텍스트를 사용합니다
  if (theme === ThemeMode.DARK) {
    return "navbar-actions--white-text";
  }

  // 밝은 테마에서
  if (theme === ThemeMode.LIGHT) {
    // 상단 레이아웃과 혼합 레이아웃의 상단 영역:
    // - 사이드바가 클래식 파란색이면 흰색 텍스트 사용
    // - 사이드바가 미니멀 흰색이면 진한 텍스트 사용
    if (layout === LayoutMode.TOP || layout === LayoutMode.MIX) {
      if (sidebarColorScheme === SidebarColor.CLASSIC_BLUE) {
        return "navbar-actions--white-text";
      } else {
        return "navbar-actions--dark-text";
      }
    }
  }

  return "navbar-actions--dark-text";
});

/**
 * 로그아웃
 */
function logout() {
  ElMessageBox.confirm("로그아웃하고 시스템을 종료하시겠습니까?", "알림", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    type: "warning",
    lockScroll: false,
  }).then(() => {
    userStore.logout().then(() => {
      router.push(`/login?redirect=${route.fullPath}`);
    });
  });
}

/**
 * 시스템 설정 페이지 열기
 */
function handleSettingsClick() {
  setting스토어.settingsVisible = true;
}
</script>

<style lang="scss" scoped>
.navbar-actions {
  display: flex;
  align-items: center;
  height: 100%;

  &__item {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 44px; /* 최소 클릭 영역을 44px로 늘립니다. 인간-컴퓨터 상호작용 표준을 준수합니다 */
    height: 100%;
    min-height: 44px;
    padding: 0 8px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;

    // 하위 요소가 중앙에 있는지 확인합니다
    > * {
      display: flex;
      align-items: center;
      justify-content: center;
    }

    // Element Plus 컴포넌트가 정상적으로 작동하는지 확인합니다
    :deep(.el-dropdown),
    :deep(.el-tooltip) {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 100%;
    }

    // 아이콘 스타일
    :deep([class^="i-svg:"]) {
      font-size: 18px;
      line-height: 1;
      color: var(--el-text-color-regular);
      transition: color 0.3s;
    }

    &:hover {
      background: rgba(0, 0, 0, 0.04);

      :deep([class^="i-svg:"]) {
        color: var(--el-color-primary);
      }
    }
  }

  .user-profile {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    padding: 0 8px;

    &__avatar {
      flex-shrink: 0;
      width: 28px;
      height: 28px;
      border-radius: 50%;
    }

    &__name {
      margin-left: 8px;
      color: var(--el-text-color-regular);
      white-space: nowrap;
      transition: color 0.3s;
    }
  }
}

// 흰색 텍스트 스타일（어두운 배경용: 어두운 테마, 상단 레이아웃, 혼합 레이아웃）
.navbar-actions--white-text {
  .navbar-actions__item {
    :deep([class^="i-svg:"]) {
      color: rgba(255, 255, 255, 0.85);
    }

    &:hover {
      background: rgba(255, 255, 255, 0.1);

      :deep([class^="i-svg:"]) {
        color: #fff;
      }
    }
  }

  .user-profile__name {
    color: rgba(255, 255, 255, 0.85);
  }
}

// 진한 텍스트 스타일（밝은 배경용: 밝은 테마의 왼쪽 레이아웃）
.navbar-actions--dark-text {
  .navbar-actions__item {
    :deep([class^="i-svg:"]) {
      color: var(--el-text-color-regular) !important;
    }

    &:hover {
      background: rgba(0, 0, 0, 0.04);

      :deep([class^="i-svg:"]) {
        color: var(--el-color-primary) !important;
      }
    }
  }

  .user-profile__name {
    color: var(--el-text-color-regular) !important;
  }
}

// 드롭다운 메뉴의 아이콘이 영향을 받지 않는지 확인합니다
:deep(.el-dropdown-menu) {
  [class^="i-svg:"] {
    color: var(--el-text-color-regular) !important;

    &:hover {
      color: var(--el-color-primary) !important;
    }
  }
}
</style>
