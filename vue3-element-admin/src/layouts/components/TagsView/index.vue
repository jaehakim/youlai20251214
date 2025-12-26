<template>
  <div class="tags-container">
    <!-- 가로 스크롤 컨테이너 -->
    <el-scrollbar
      ref="scrollbarRef"
      class="scroll-container"
      :view-style="{ height: '100%' }"
      @wheel="handleScroll"
    >
      <div h-full flex-y-center gap-8px>
        <el-tag
          v-for="tag in visitedViews"
          :key="tag.fullPath"
          h-26px
          cursor-pointer
          :closable="!tag.affix"
          :effect="tagsView스토어.isActive(tag) ? 'dark' : 'light'"
          :type="tagsView스토어.isActive(tag) ? 'primary' : 'info'"
          @click.middle="handleMiddleClick(tag)"
          @contextmenu.prevent="(event: MouseEvent) => openContextMenu(tag, event)"
          @close="closeSelectedTag(tag)"
          @click="
            router.push({
              path: tag.fullPath,
              query: tag.query,
            })
          "
        >
          {{ translateRouteTitle(tag.title) }}
        </el-tag>
      </div>
    </el-scrollbar>

    <!-- 태그 우클릭 메뉴 -->
    <Teleport to="body">
      <ul
        v-show="contextMenu.visible"
        class="contextmenu"
        :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
      >
        <li @click="refreshSelectedTag(selectedTag)">
          <div class="i-svg:refresh" />
          새로고침
        </li>
        <li v-if="!selectedTag?.affix" @click="closeSelectedTag(selectedTag)">
          <div class="i-svg:close" />
          닫기
        </li>
        <li @click="closeOtherTags">
          <div class="i-svg:close_other" />
          다른 태그 닫기
        </li>
        <li v-if="!isFirstView" @click="closeLeftTags">
          <div class="i-svg:close_left" />
          왼쪽 닫기
        </li>
        <li v-if="!isLastView" @click="closeRightTags">
          <div class="i-svg:close_right" />
          오른쪽 닫기
        </li>
        <li @click="closeAllTags(selectedTag)">
          <div class="i-svg:close_all" />
          모두 닫기
        </li>
      </ul>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter, type RouteRecordRaw } from "vue-router";
import { resolve } from "path-browserify";
import { translateRouteTitle } from "@/utils/i18n";
import { usePermission스토어, useTagsView스토어 } from "@/store";

interface ContextMenu {
  visible: boolean;
  x: number;
  y: number;
}

const router = useRouter();
const route = useRoute();

// 상태 관리
const permission스토어 = usePermission스토어();
const tagsView스토어 = useTagsView스토어();

const { visitedViews } = storeToRefs(tagsView스토어);

// 현재 선택된 태그
const selectedTag = ref<TagView | null>(null);

// 우클릭 메뉴 상태
const contextMenu = reactive<ContextMenu>({
  visible: false,
  x: 0,
  y: 0,
});

// 스크롤바 참조
const scrollbarRef = ref();

// 라우트 매핑 캐시, 조회 성능 향상
const routePathMap = computed(() => {
  const map = new Map<string, TagView>();
  visitedViews.value.forEach((tag) => {
    map.set(tag.path, tag);
  });
  return map;
});

// 첫 번째 태그 여부 판단
const isFirstView = computed(() => {
  if (!selectedTag.value) return false;
  return (
    selectedTag.value.path === "/dashboard" ||
    selectedTag.value.fullPath === visitedViews.value[1]?.fullPath
  );
});

// 마지막 태그 여부 판단
const isLastView = computed(() => {
  if (!selectedTag.value) return false;
  return selectedTag.value.fullPath === visitedViews.value[visitedViews.value.length - 1]?.fullPath;
});

/**
 * 고정 태그 재귀 추출
 */
const extractAffixTags = (routes: RouteRecordRaw[], basePath = "/"): TagView[] => {
  const affixTags: TagView[] = [];

  const traverse = (routeList: RouteRecordRaw[], currentBasePath: string) => {
    routeList.forEach((route) => {
      const fullPath = resolve(currentBasePath, route.path);

      // 고정 태그인 경우 목록에 추가
      if (route.meta?.affix) {
        affixTags.push({
          path: fullPath,
          fullPath,
          name: String(route.name || ""),
          title: route.meta.title || "no-name",
          affix: true,
          keepAlive: route.meta.keepAlive || false,
        });
      }

      // 자식 라우트 재귀 처리
      if (route.children?.length) {
        traverse(route.children, fullPath);
      }
    });
  };

  traverse(routes, basePath);
  return affixTags;
};

/**
 * 고정 태그 초기화
 */
const initAffixTags = () => {
  const affixTags = extractAffixTags(permission스토어.routes);

  affixTags.forEach((tag) => {
    if (tag.name) {
      tagsView스토어.addVisitedView(tag);
    }
  });
};

/**
 * 현재 라우트 태그 추가
 */
const addCurrentTag = () => {
  if (!route.meta?.title) return;

  tagsView스토어.addView({
    name: route.name as string,
    title: route.meta.title,
    path: route.path,
    fullPath: route.fullPath,
    affix: route.meta.affix || false,
    keepAlive: route.meta.keepAlive || false,
    query: route.query,
  });
};

/**
 * 현재 태그 업데이트
 */
const updateCurrentTag = () => {
  nextTick(() => {
    const currentTag = routePathMap.value.get(route.path);

    if (currentTag && currentTag.fullPath !== route.fullPath) {
      tagsView스토어.updateVisitedView({
        name: route.name as string,
        title: route.meta?.title || "",
        path: route.path,
        fullPath: route.fullPath,
        affix: route.meta?.affix || false,
        keepAlive: route.meta?.keepAlive || false,
        query: route.query,
      });
    }
  });
};

/**
 * 중간 키 클릭 처리
 */
const handleMiddleClick = (tag: TagView) => {
  if (!tag.affix) {
    closeSelectedTag(tag);
  }
};

/**
 * 우클릭 메뉴 열기
 */
const openContextMenu = (tag: TagView, event: MouseEvent) => {
  contextMenu.x = event.clientX;
  contextMenu.y = event.clientY;
  contextMenu.visible = true;

  selectedTag.value = tag;
};

/**
 * 우클릭 메뉴 닫기
 */
const closeContextMenu = () => {
  contextMenu.visible = false;
};

/**
 * 휠 이벤트 처리
 */
const handleScroll = (event: WheelEvent) => {
  closeContextMenu();

  const scrollWrapper = scrollbarRef.value?.wrapRef;
  if (!scrollWrapper) return;

  const hasHorizontalScroll = scrollWrapper.scrollWidth > scrollWrapper.clientWidth;
  if (!hasHorizontalScroll) return;

  const deltaY = event.deltaY || -(event as any).wheelDelta || 0;
  const newScrollLeft = scrollWrapper.scrollLeft + deltaY;

  scrollbarRef.value.setScrollLeft(newScrollLeft);
};

/**
 * 태그 새로고침
 */
const refreshSelectedTag = (tag: TagView | null) => {
  if (!tag) return;

  tagsView스토어.delCachedView(tag);
  nextTick(() => {
    router.replace("/redirect" + tag.fullPath);
  });
};

/**
 * 태그 닫기
 */
const closeSelectedTag = (tag: TagView | null) => {
  if (!tag) return;

  tagsView스토어.delView(tag).then((result: any) => {
    if (tagsView스토어.isActive(tag)) {
      tagsView스토어.toLastView(result.visitedViews, tag);
    }
  });
};

/**
 * 왼쪽 태그 닫기
 */
const closeLeftTags = () => {
  if (!selectedTag.value) return;

  tagsView스토어.delLeftViews(selectedTag.value).then((result: any) => {
    const hasCurrentRoute = result.visitedViews.some((item: TagView) => item.path === route.path);

    if (!hasCurrentRoute) {
      tagsView스토어.toLastView(result.visitedViews);
    }
  });
};

/**
 * 오른쪽 태그 닫기
 */
const closeRightTags = () => {
  if (!selectedTag.value) return;

  tagsView스토어.delRightViews(selectedTag.value).then((result: any) => {
    const hasCurrentRoute = result.visitedViews.some((item: TagView) => item.path === route.path);

    if (!hasCurrentRoute) {
      tagsView스토어.toLastView(result.visitedViews);
    }
  });
};

/**
 * 다른 태그 닫기
 */
const closeOtherTags = () => {
  if (!selectedTag.value) return;

  router.push(selectedTag.value);
  tagsView스토어.delOtherViews(selectedTag.value).then(() => {
    updateCurrentTag();
  });
};

/**
 * 모든 태그 닫기
 */
const closeAllTags = (tag: TagView | null) => {
  tagsView스토어.delAllViews().then((result: any) => {
    tagsView스토어.toLastView(result.visitedViews, tag || undefined);
  });
};

// 우클릭 메뉴 관리
const useContextMenuManager = () => {
  const handleOutsideClick = () => {
    closeContextMenu();
  };

  watchEffect(() => {
    if (contextMenu.visible) {
      document.addEventListener("click", handleOutsideClick);
    } else {
      document.removeEventListener("click", handleOutsideClick);
    }
  });

  // 컴포넌트 언마운트 시 정리
  onBeforeUnmount(() => {
    document.removeEventListener("click", handleOutsideClick);
  });
};

// 라우트 변화 감시
watch(
  route,
  () => {
    addCurrentTag();
    updateCurrentTag();
  },
  { immediate: true }
);

// 초기화
onMounted(() => {
  initAffixTags();
});

// 우클릭 메뉴 관리 활성화
useContextMenuManager();
</script>

<style lang="scss" scoped>
.tags-container {
  width: 100%;
  height: $tags-view-height;
  padding: 0 15px;
  border-top: 1px solid var(--el-border-color-light);

  .scroll-container {
    white-space: nowrap;
  }
}
.contextmenu {
  position: absolute;
  z-index: 3000;
  padding: 5px 0;
  margin: 0;
  font-size: 12px;
  font-weight: 400;
  color: var(--el-text-color-primary);
  list-style-type: none;
  background: var(--el-bg-color);
  border-radius: 4px;
  box-shadow: var(--el-box-shadow-light);

  li {
    display: flex;
    gap: 8px;
    align-items: center;
    padding: 7px 16px;
    margin: 0;
    cursor: pointer;
    transition: background-color 0.2s;

    &:hover {
      background: var(--el-fill-color-light);
    }
  }
}
</style>
