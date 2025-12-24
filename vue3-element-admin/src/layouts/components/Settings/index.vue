<template>
  <el-drawer
    v-model="drawerVisible"
    size="380"
    :title="t('settings.project')"
    :before-close="handleCloseDrawer"
    class="settings-drawer"
  >
    <div class="settings-content">
      <section class="config-section">
        <el-divider>{{ t("settings.theme") }}</el-divider>

        <div class="flex-center">
          <el-switch
            v-model="isDark"
            active-icon="Moon"
            inactive-icon="Sunny"
            class="theme-switch"
            @change="handleThemeChange"
          />
        </div>
      </section>

      <!-- 인터페이스 설정 -->
      <section class="config-section">
        <el-divider>{{ t("settings.interface") }}</el-divider>

        <div class="config-item flex-x-between">
          <span class="text-xs">{{ t("settings.themeColor") }}</span>
          <el-color-picker
            v-model="selectedThemeColor"
            :predefine="colorPresets"
            popper-class="theme-picker-dropdown"
          />
        </div>

        <div class="config-item flex-x-between">
          <span class="text-xs">{{ t("settings.showTagsView") }}</span>
          <el-switch v-model="settings스토어.showTagsView" />
        </div>

        <div class="config-item flex-x-between">
          <span class="text-xs">{{ t("settings.showAppLogo") }}</span>
          <el-switch v-model="settings스토어.showAppLogo" />
        </div>

        <div class="config-item flex-x-between">
          <span class="text-xs">{{ t("settings.showWatermark") }}</span>
          <el-switch v-model="settings스토어.showWatermark" />
        </div>
        <div v-if="!isDark" class="config-item flex-x-between">
          <span class="text-xs">{{ t("settings.sidebarColorScheme") }}</span>
          <el-radio-group v-model="sidebarColor" @change="changeSidebarColor">
            <el-radio :value="SidebarColor.CLASSIC_BLUE">
              {{ t("settings.classicBlue") }}
            </el-radio>
            <el-radio :value="SidebarColor.MINIMAL_WHITE">
              {{ t("settings.minimalWhite") }}
            </el-radio>
          </el-radio-group>
        </div>
      </section>

      <!-- 레이아웃 설정 -->
      <section class="config-section">
        <el-divider>{{ t("settings.navigation") }}</el-divider>

        <!-- 통합 레이아웃 선택기 -->
        <div class="layout-select">
          <div class="layout-grid">
            <el-tooltip
              v-for="item in layoutOptions"
              :key="item.value"
              :content="item.label"
              placement="bottom"
            >
              <div
                role="button"
                tabindex="0"
                :class="[
                  'layout-item',
                  item.className,
                  {
                    'is-active': settings스토어.layout === item.value,
                  },
                ]"
                @click="handleLayoutChange(item.value)"
                @keydown.enter.space="handleLayoutChange(item.value)"
              >
                <!-- 레이아웃 미리보기 아이콘 -->
                <div class="layout-preview">
                  <div v-if="item.value !== LayoutMode.LEFT" class="layout-header"></div>
                  <div v-if="item.value !== LayoutMode.TOP" class="layout-sidebar"></div>
                  <div class="layout-main"></div>
                </div>
                <!-- 레이아웃 이름 -->
                <div class="layout-name">{{ item.label }}</div>
                <!-- 선택 상태 표시기 -->
                <div v-if="settings스토어.layout === item.value" class="layout-check">
                  <el-icon><Check /></el-icon>
                </div>
              </div>
            </el-tooltip>
          </div>
        </div>
      </section>
    </div>

    <!-- 작업 버튼 영역 - 하단 고정 -->
    <template #footer>
      <div class="action-buttons">
        <el-tooltip
          content="설정 복사는 현재 설정의 코드를 생성하여 src/settings.ts의 defaultSettings 변수를 덮어씁니다"
          placement="top"
        >
          <el-button
            type="primary"
            size="default"
            :icon="copyIcon"
            :loading="copyLoading"
            @click="handleCopySettings"
          >
            {{ copyLoading ? "복사 중..." : t("settings.copyConfig") }}
          </el-button>
        </el-tooltip>
        <el-tooltip content="재설정하면 모든 설정이 기본값으로 복원됩니다" placement="top">
          <el-button
            type="warning"
            size="default"
            :icon="resetIcon"
            :loading="resetLoading"
            @click="handleResetSettings"
          >
            {{ resetLoading ? "재설정 중..." : t("settings.resetConfig") }}
          </el-button>
        </el-tooltip>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { DocumentCopy, RefreshLeft, Check } from "@element-plus/icons-vue";

const { t } = useI18n();
import { LayoutMode, SidebarColor, ThemeMode } from "@/enums";
import { useSettings스토어 } from "@/store";
import { themeColorPresets } from "@/settings";

// 버튼 아이콘
const copyIcon = markRaw(DocumentCopy);
const resetIcon = markRaw(RefreshLeft);

// 로드 상태
const copyLoading = ref(false);
const resetLoading = ref(false);

// 레이아웃 옵션 구성
interface LayoutOption {
  value: LayoutMode;
  label: string;
  className: string;
}

const layoutOptions: LayoutOption[] = [
  { value: LayoutMode.LEFT, label: t("settings.leftLayout"), className: "left" },
  { value: LayoutMode.TOP, label: t("settings.topLayout"), className: "top" },
  { value: LayoutMode.MIX, label: t("settings.mixLayout"), className: "mix" },
];

// 통합 색상 사전 설정 사용
const colorPresets = themeColorPresets;

const settings스토어 = useSettings스토어();

const isDark = ref<boolean>(settings스토어.theme === ThemeMode.DARK);
const sidebarColor = ref(settings스토어.sidebarColorScheme);

const selectedThemeColor = computed({
  get: () => settings스토어.themeColor,
  set: (value) => settings스토어.updateThemeColor(value),
});

const drawerVisible = computed({
  get: () => settings스토어.settingsVisible,
  set: (value) => (settings스토어.settingsVisible = value),
});

/**
 * 테마 전환 처리
 *
 * @param isDark 어두운 모드 활성화 여부
 */
const handleThemeChange = (isDark: string | number | boolean) => {
  settings스토어.updateTheme(isDark ? ThemeMode.DARK : ThemeMode.LIGHT);
};

/**
 * 사이드바 색상 변경
 *
 * @param val 색상 체계 이름
 */
const changeSidebarColor = (val: any) => {
  settings스토어.updateSidebarColorScheme(val);
};

/**
 * 레이아웃 전환
 *
 * @param layout - 레이아웃 모드
 */
const handleLayoutChange = (layout: LayoutMode) => {
  if (settings스토어.layout === layout) return;

  settings스토어.updateLayout(layout);
};

/**
 * 현재 설정 복사
 */
const handleCopySettings = async () => {
  try {
    copyLoading.value = true;

    // 설정 코드 생성
    const configCode = generateSettingsCode();

    // 클립보드에 복사
    await navigator.clipboard.writeText(configCode);

    // 성공 메시지 표시
    ElMessage.success({
      message: t("settings.copySuccess"),
      duration: 3000,
    });
  } catch {
    ElMessage.error("설정 복사 실패");
  } finally {
    copyLoading.value = false;
  }
};

/**
 * 기본 설정으로 재설정
 */
const handleResetSettings = async () => {
  resetLoading.value = true;

  try {
    settings스토어.resetSettings();

    // 로컬 상태 동기화 업데이트
    isDark.value = settings스토어.theme === ThemeMode.DARK;
    sidebarColor.value = settings스토어.sidebarColorScheme;

    ElMessage.success(t("settings.resetSuccess"));
  } catch {
    ElMessage.error("설정 재설정 실패");
  } finally {
    resetLoading.value = false;
  }
};

/**
 * 설정 코드 문자열 생성
 */
const generateSettingsCode = (): string => {
  const settings = {
    title: "pkg.name",
    version: "pkg.version",
    showSettings: true,
    showTagsView: settings스토어.showTagsView,
    showAppLogo: settings스토어.showAppLogo,
    layout: `LayoutMode.${settings스토어.layout.toUpperCase()}`,
    theme: `ThemeMode.${settings스토어.theme.toUpperCase()}`,
    size: "ComponentSize.DEFAULT",
    language: "LanguageEnum.ZH_CN",
    themeColor: `"${settings스토어.themeColor}"`,
    showWatermark: settings스토어.showWatermark,
    watermarkContent: "pkg.name",
    sidebarColorScheme: `SidebarColor.${settings스토어.sidebarColorScheme.toUpperCase().replace("-", "_")}`,
  };

  return `const defaultSettings: AppSettings = {
  title: ${settings.title},
  version: ${settings.version},
  showSettings: ${settings.showSettings},
  showTagsView: ${settings.showTagsView},
  showAppLogo: ${settings.showAppLogo},
  layout: ${settings.layout},
  theme: ${settings.theme},
  size: ${settings.size},
  language: ${settings.language},
  themeColor: ${settings.themeColor},
  showWatermark: ${settings.showWatermark},
  watermarkContent: ${settings.watermarkContent},
  sidebarColorScheme: ${settings.sidebarColorScheme},
};`;
};

/**
 * 드로어 닫기 전 콜백
 */
const handleCloseDrawer = () => {
  settings스토어.settingsVisible = false;
};
</script>

<style lang="scss" scoped>
/* 설정 드로어 스타일 */
.settings-drawer {
  :deep(.el-drawer__body) {
    position: relative;
    height: 100%;
    padding: 0;
    overflow: hidden;
  }
}

/* 설정 콘텐츠 영역 */
.settings-content {
  height: calc(100vh - 120px); /* 헤더 및 하단 버튼 높이 제외 */
  padding: 20px;
  overflow-y: auto;
}

/* 하단 작업 영역 스타일 */
.action-buttons {
  display: flex;

  & > .el-button {
    flex: 1;
    font-size: 14px;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      transform: translateY(-2px);
    }
  }
}

/* 테마 전환기 최적화 */
.theme-switch {
  transform: scale(1.2);
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.25);
  }
}

.config-section {
  margin-bottom: 24px;

  .config-item {
    padding: 12px 0;
    border-bottom: 1px solid var(--el-border-color-light);
    transition: all 0.3s ease;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      padding-right: 8px;
      padding-left: 8px;
      margin: 0 -8px;
      background-color: var(--el-fill-color-light);
      border-radius: 6px;
    }
  }
}

/* 레이아웃 선택기 스타일 최적화 */
.layout-select {
  padding: 16px 8px;

  .layout-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
    justify-items: center;
  }
}

.layout-item {
  position: relative;
  width: 70px;
  height: 80px;
  overflow: hidden;
  cursor: pointer;
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
  border: 2px solid var(--el-border-color-light);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    background: linear-gradient(145deg, #ffffff 0%, var(--el-color-primary-light-9) 100%);
    border-color: var(--el-color-primary-light-3);
    transform: translateY(-4px) scale(1.05);
  }

  &:active {
    transform: translateY(-2px) scale(1.02);
  }

  .layout-preview {
    position: relative;
    width: 100%;
    height: 50px;
    margin: 8px 0 4px 0;
  }

  .layout-header {
    position: absolute;
    top: 0;
    right: 4px;
    left: 4px;
    height: 8px;
    background: linear-gradient(
      90deg,
      var(--el-color-primary) 0%,
      var(--el-color-primary-light-3) 100%
    );
    border-radius: 2px;
  }

  .layout-sidebar {
    position: absolute;
    left: 4px;
    width: 12px;
    background: linear-gradient(
      180deg,
      var(--el-color-primary-dark-2) 0%,
      var(--el-color-primary) 100%
    );
    border-radius: 2px;
  }

  .layout-main {
    position: absolute;
    background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 2px;
  }

  .layout-name {
    position: absolute;
    right: 0;
    bottom: 6px;
    left: 0;
    font-size: 10px;
    font-weight: 500;
    color: var(--el-text-color-regular);
    text-align: center;
    transition: color 0.3s ease;
  }

  .layout-check {
    position: absolute;
    top: 4px;
    right: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 16px;
    height: 16px;
    font-size: 10px;
    color: white;
    background: var(--el-color-success);
    border-radius: 50%;
  }

  // 왼쪽 레이아웃
  &.left {
    .layout-sidebar {
      top: 4px;
      bottom: 4px;
    }
    .layout-main {
      top: 4px;
      right: 4px;
      bottom: 4px;
      left: 20px;
    }
  }

  // 상단 레이아웃
  &.top {
    .layout-header {
      height: 12px;
    }
    .layout-main {
      top: 16px;
      right: 4px;
      bottom: 4px;
      left: 4px;
    }
  }

  // 혼합 레이아웃
  &.mix {
    .layout-header {
      height: 10px;
    }
    .layout-sidebar {
      top: 14px;
      bottom: 4px;
    }
    .layout-main {
      top: 14px;
      right: 4px;
      bottom: 4px;
      left: 20px;
    }
  }

  &.is-active {
    background: linear-gradient(
      145deg,
      var(--el-color-primary-light-9) 0%,
      var(--el-color-primary-light-8) 100%
    );
    border-color: var(--el-color-primary);
    transform: translateY(-2px) scale(1.08);

    .layout-name {
      font-weight: 600;
      color: var(--el-color-primary);
    }
  }
}

/* 어두운 모드 적응 */
.dark {
  .action-footer {
    background: var(--el-bg-color);
    border-top-color: var(--el-border-color);
  }

  .action-card {
    background: var(--el-fill-color-extra-light);
  }

  .layout-item {
    background: linear-gradient(145deg, var(--el-bg-color) 0%, var(--el-bg-color-page) 100%);
    border-color: var(--el-border-color);

    &:hover {
      background: linear-gradient(
        145deg,
        var(--el-bg-color-page) 0%,
        var(--el-color-primary-light-9) 100%
      );
    }

    &.is-active {
      background: linear-gradient(
        145deg,
        var(--el-color-primary-light-9) 0%,
        var(--el-color-primary-light-8) 100%
      );
    }

    .layout-main {
      background: linear-gradient(135deg, var(--el-fill-color) 0%, var(--el-fill-color-light) 100%);
    }
  }
}

/* 설정 복사 대화 상자 스타일 */
:deep(.copy-config-dialog) {
  .el-message-box__content {
    max-height: 400px;
    overflow-y: auto;
  }
}
</style>
