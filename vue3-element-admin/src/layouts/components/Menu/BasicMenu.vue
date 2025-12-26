<!-- 메뉴 구성 요소 -->
<template>
  <el-menu
    ref="menuRef"
    :default-active="activeMenuPath"
    :collapse="!app스토어.sidebar.opened"
    :background-color="menuThemeProps.backgroundColor"
    :text-color="menuThemeProps.textColor"
    :active-text-color="menuThemeProps.activeTextColor"
    :popper-effect="theme"
    :unique-opened="false"
    :collapse-transition="false"
    :mode="menuMode"
    @open="onMenuOpen"
    @close="onMenuClose"
  >
    <!-- 메뉴 항목 -->
    <MenuItem
      v-for="route in data"
      :key="route.path"
      :item="route"
      :base-path="resolveFullPath(route.path)"
    />
  </el-menu>
</template>

<script lang="ts" setup>
import { useRoute } from "vue-router";
import path from "path-browserify";
import type { MenuInstance } from "element-plus";
import type { RouteRecordRaw } from "vue-router";
import { SidebarColor } from "@/enums/settings/theme-enum";
import { useSettings스토어, useApp스토어 } from "@/store";
import { isExternal } from "@/utils/index";
import MenuItem from "./components/MenuItem.vue";
import variables from "@/styles/variables.module.scss";

const props = defineProps({
  data: {
    type: Array as PropType<RouteRecordRaw[]>,
    default: () => [],
  },
  basePath: {
    type: String,
    required: true,
    example: "/system",
  },
  menuMode: {
    type: String as PropType<"vertical" | "horizontal">,
    default: "vertical",
    validator: (value: string) => ["vertical", "horizontal"].includes(value),
  },
});

const menuRef = ref<MenuInstance>();
const settings스토어 = useSettings스토어();
const app스토어 = useApp스토어();
const currentRoute = useRoute();

// 확장된 메뉴 항목 인덱스 저장
const expandedMenuIndexes = ref<string[]>([]);

// 테마 가져오기
const theme = computed(() => settings스토어.theme);

// 밝은 테마의 사이드바 색상 구성표 가져오기
const sidebarColorScheme = computed(() => settings스토어.sidebarColorScheme);

// 메뉴 테마 속성
const menuThemeProps = computed(() => {
  const isDarkOrClassicBlue =
    theme.value === "dark" || sidebarColorScheme.value === SidebarColor.CLASSIC_BLUE;

  return {
    backgroundColor: isDarkOrClassicBlue ? variables["menu-background"] : undefined,
    textColor: isDarkOrClassicBlue ? variables["menu-text"] : undefined,
    activeTextColor: isDarkOrClassicBlue ? variables["menu-active-text"] : undefined,
  };
});

// 현재 활성화된 메뉴 항목 계산
const activeMenuPath = computed((): string => {
  const { meta, path } = currentRoute;

  // 라우트 메타에 activeMenu가 설정된 경우 사용 (상세 페이지와 같은 특별한 경우 처리)
  if (meta?.activeMenu && typeof meta.activeMenu === "string") {
    return meta.activeMenu;
  }

  // 그렇지 않으면 현재 라우트 경로 사용
  return path;
});

/**
 * 전체 경로 가져오기
 *
 * @param routePath 현재 라우트의 상대 경로  /user
 * @returns 전체 절대 경로 D://vue3-element-admin/system/user
 */
function resolveFullPath(routePath: string) {
  if (isExternal(routePath)) {
    return routePath;
  }
  if (isExternal(props.basePath)) {
    return props.basePath;
  }

  // basePath가 비어있으면（상단 레이아웃）, routePath를 직접 반환합니다
  if (!props.basePath || props.basePath === "") {
    return routePath;
  }

  // 경로 구문 분석, 전체 절대 경로 생성
  return path.resolve(props.basePath, routePath);
}

/**
 * 메뉴 열기
 *
 * @param index 현재 확장된 메뉴 항목 인덱스
 */
const onMenuOpen = (index: string) => {
  expandedMenuIndexes.value.push(index);
};

/**
 * 메뉴 닫기
 *
 * @param index 현재 축소된 메뉴 항목 인덱스
 */
const onMenuClose = (index: string) => {
  expandedMenuIndexes.value = expandedMenuIndexes.value.filter((item) => item !== index);
};

/**
 * 확장된 메뉴 항목 변화 감시, 부모 메뉴 스타일 업데이트
 */
watch(
  () => expandedMenuIndexes.value,
  () => {
    updateParentMenuStyles();
  }
);

/**
 * 메뉴 모드 변화 감시: 메뉴 모드가 수평 모드로 전환될 때, 모든 확장된 메뉴 항목을 닫고,
 * 수평 모드에서 메뉴 항목 표시 오류를 피합니다.
 */
watch(
  () => props.menuMode,
  (newMode) => {
    if (newMode === "horizontal" && menuRef.value) {
      expandedMenuIndexes.value.forEach((item) => menuRef.value!.close(item));
    }
  }
);

/**
 * 활성 메뉴 변화 감시, 활성 하위 메뉴를 포함하는 부모 메뉴에 스타일 클래스 추가
 */
watch(
  () => activeMenuPath.value,
  () => {
    nextTick(() => {
      updateParentMenuStyles();
    });
  },
  { immediate: true }
);

/**
 * 라우트 변화 감시, 메뉴가 TagsView 전환에 따라 올바르게 활성화되도록 보장
 */
watch(
  () => currentRoute.path,
  () => {
    nextTick(() => {
      updateParentMenuStyles();
    });
  }
);

/**
 * 부모 메뉴 스타일 업데이트 - 활성 하위 메뉴를 포함하는 부모 메뉴에 has-active-child 클래스 추가
 */
function updateParentMenuStyles() {
  if (!menuRef.value?.$el) return;

  nextTick(() => {
    try {
      const menuEl = menuRef.value?.$el as HTMLElement;
      if (!menuEl) return;

      // 모든 기존 has-active-child 클래스 제거
      const allSubMenus = menuEl.querySelectorAll(".el-sub-menu");
      allSubMenus.forEach((subMenu) => {
        subMenu.classList.remove("has-active-child");
      });

      // 현재 활성화된 메뉴 항목 찾기
      const activeMenuItem = menuEl.querySelector(".el-menu-item.is-active");

      if (activeMenuItem) {
        // 부모 el-sub-menu 요소 상향 검색
        let parent = activeMenuItem.parentElement;
        while (parent && parent !== menuEl) {
          if (parent.classList.contains("el-sub-menu")) {
            parent.classList.add("has-active-child");
          }
          parent = parent.parentElement;
        }
      } else {
        // 수평 모드에서는 특별한 처리가 필요할 수 있습니다
        if (props.menuMode === "horizontal") {
          // 수평 메뉴의 경우, 경로 매칭을 사용하여 부모 메뉴 찾기
          const currentPath = activeMenuPath.value;

          // 모든 부모 메뉴 항목 찾기, 현재 경로를 포함하는 항목 확인
          allSubMenus.forEach((subMenu) => {
            const subMenuEl = subMenu as HTMLElement;
            const subMenuPath =
              subMenuEl.getAttribute("data-path") ||
              subMenuEl.querySelector(".el-sub-menu__title")?.getAttribute("data-path");

            // 현재 경로를 포함하는 부모 메뉴를 찾으면, 활성화 클래스 추가
            if (subMenuPath && currentPath.startsWith(subMenuPath)) {
              subMenuEl.classList.add("has-active-child");
            }
          });
        }
      }
    } catch (error) {
      console.error("Error updating parent menu styles:", error);
    }
  });
}

/**
 * 컴포넌트 마운트 후 부모 메뉴 스타일 즉시 업데이트
 */
onMounted(() => {
  // 컴포넌트 마운트 후 스타일 업데이트, 비동기 작업에 의존하지 않음
  updateParentMenuStyles();
});
</script>
