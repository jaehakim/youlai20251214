<template>
  <div @click="openSearchModal">
    <div class="i-svg:search" />
    <el-dialog
      v-model="isModalVisible"
      width="30%"
      :append-to-body="true"
      :show-close="false"
      @close="closeSearchModal"
    >
      <template #header>
        <el-input
          ref="searchInputRef"
          v-model="searchKeyword"
          size="large"
          placeholder="메뉴 이름 키워드 검색 입력"
          clearable
          @keyup.enter="selectActiveResult"
          @input="updateSearchResults"
          @keydown.up.prevent="navigateResults('up')"
          @keydown.down.prevent="navigateResults('down')"
          @keydown.esc="closeSearchModal"
        >
          <template #prepend>
            <el-button icon="Search" />
          </template>
        </el-input>
      </template>

      <div class="search-result">
        <!-- 검색 기록 -->
        <template v-if="searchKeyword === '' && searchHistory.length > 0">
          <div class="search-history">
            <div class="search-history__title">
              검색 기록
              <el-button
                type="primary"
                text
                size="small"
                class="search-history__clear"
                @click="clearHistory"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <ul class="search-history__list">
              <li
                v-for="(item, index) in searchHistory"
                :key="index"
                class="search-history__item"
                @click="navigateToRoute(item)"
              >
                <div class="search-history__icon">
                  <el-icon><Clock /></el-icon>
                </div>
                <span class="search-history__name">{{ item.title }}</span>
                <div class="search-history__action">
                  <el-icon @click.stop="removeHistoryItem(index)"><Close /></el-icon>
                </div>
              </li>
            </ul>
          </div>
        </template>

        <!-- 검색 결과 -->
        <template v-else>
          <ul v-if="displayResults.length > 0">
            <li
              v-for="(item, index) in displayResults"
              :key="item.path"
              :class="[
                'search-result__item',
                {
                  'search-result__item--active': index === activeIndex,
                },
              ]"
              @click="navigateToRoute(item)"
            >
              <el-icon v-if="item.icon && item.icon.startsWith('el-icon')">
                <component :is="item.icon.replace('el-icon-', '')" />
              </el-icon>
              <div v-else-if="item.icon" :class="`i-svg:${item.icon}`" />
              <div v-else class="i-svg:menu" />
              <span class="ml-2">{{ item.title }}</span>
            </li>
          </ul>
        </template>

        <!-- 검색 기록 없음 표시 -->
        <div v-if="searchKeyword === '' && searchHistory.length === 0" class="no-history">
          <p class="no-history__text">검색 기록이 없습니다</p>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <div class="ctrl-k-hint">
            <span class="ctrl-k-text">Ctrl+K 빠른 열기</span>
          </div>
          <div class="shortcuts-group">
            <div class="key-box">
              <div class="key-btn">선택</div>
            </div>
            <div class="arrow-box">
              <div class="arrow-up-down">
                <div class="key-btn">
                  <div class="i-svg:up" />
                </div>
                <div class="key-btn ml-1">
                  <div class="i-svg:down" />
                </div>
              </div>
              <span class="key-text">전환</span>
            </div>
            <div class="key-box">
              <div class="key-btn esc-btn">ESC</div>
              <span class="key-text">닫기</span>
            </div>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import router from "@/router";
import { usePermissionStore } from "@/store";
import { isExternal } from "@/utils";
import { RouteRecordRaw, LocationQueryRaw } from "vue-router";
import { Clock, Close, Delete } from "@element-plus/icons-vue";

const HISTORY_KEY = "menu_search_history";
const MAX_HISTORY = 5;

const permissionStore = usePermissionStore();
const isModalVisible = ref(false);
const searchKeyword = ref("");
const searchInputRef = ref();
const excludedRoutes = ref(["/redirect", "/login", "/401", "/404"]);
const menuItems = ref<SearchItem[]>([]);
const searchResults = ref<SearchItem[]>([]);
const activeIndex = ref(-1);
const searchHistory = ref<SearchItem[]>([]);

interface SearchItem {
  title: string;
  path: string;
  name?: string;
  icon?: string;
  redirect?: string;
  params?: LocationQueryRaw;
}

// 로컬 저장소에서 검색 기록 로드
function loadSearchHistory() {
  const historyStr = localStorage.getItem(HISTORY_KEY);
  if (historyStr) {
    try {
      searchHistory.value = JSON.parse(historyStr);
    } catch {
      searchHistory.value = [];
    }
  }
}

// 검색 기록을 로컬 저장소에 저장
function saveSearchHistory() {
  localStorage.setItem(HISTORY_KEY, JSON.stringify(searchHistory.value));
}

// 검색 기록에 항목 추가
function addToHistory(item: SearchItem) {
  // 이미 존재하는지 확인
  const index = searchHistory.value.findIndex((i) => i.path === item.path);

  // 존재하면 제거
  if (index !== -1) {
    searchHistory.value.splice(index, 1);
  }

  // 기록의 시작 부분에 추가
  searchHistory.value.unshift(item);

  // 기록 수량 제한
  if (searchHistory.value.length > MAX_HISTORY) {
    searchHistory.value = searchHistory.value.slice(0, MAX_HISTORY);
  }

  // 로컬 저장소에 저장
  saveSearchHistory();
}

// 기록 항목 제거
function removeHistoryItem(index: number) {
  searchHistory.value.splice(index, 1);
  saveSearchHistory();
}

// 기록 비우기
function clearHistory() {
  searchHistory.value = [];
  localStorage.removeItem(HISTORY_KEY);
}

// 전역 바로 가기 키 등록
function handleKeyDown(e: KeyboardEvent) {
  // Ctrl+K 조합 키 여부 확인
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "k") {
    e.preventDefault(); // 기본 동작 방지
    openSearchModal();
  }
}

// 키보드 이벤트 리스너 추가
onMounted(() => {
  loadRoutes(permissionStore.routes);
  loadSearchHistory();
  document.addEventListener("keydown", handleKeyDown);
});

// 키보드 이벤트 리스너 제거
onBeforeUnmount(() => {
  document.removeEventListener("keydown", handleKeyDown);
});

// 검색 모달 열기
function openSearchModal() {
  searchKeyword.value = "";
  activeIndex.value = -1;
  isModalVisible.value = true;
  setTimeout(() => {
    searchInputRef.value.focus();
  }, 100);
}

// 검색 모달 닫기
function closeSearchModal() {
  isModalVisible.value = false;
}

// 검색 결과 업데이트
function updateSearchResults() {
  activeIndex.value = -1;
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    searchResults.value = menuItems.value.filter((item) =>
      item.title.toLowerCase().includes(keyword)
    );
  } else {
    searchResults.value = [];
  }
}

// 검색 결과 표시
const displayResults = computed(() => searchResults.value);

// 검색 실행
function selectActiveResult() {
  if (displayResults.value.length > 0 && activeIndex.value >= 0) {
    navigateToRoute(displayResults.value[activeIndex.value]);
  }
}

// 검색 결과 탐색
function navigateResults(direction: string) {
  if (displayResults.value.length === 0) return;

  if (direction === "up") {
    activeIndex.value =
      activeIndex.value <= 0 ? displayResults.value.length - 1 : activeIndex.value - 1;
  } else if (direction === "down") {
    activeIndex.value =
      activeIndex.value >= displayResults.value.length - 1 ? 0 : activeIndex.value + 1;
  }
}

// 다음으로 이동
function navigateToRoute(item: SearchItem) {
  closeSearchModal();
  // 기록에 추가
  addToHistory(item);

  if (isExternal(item.path)) {
    window.open(item.path, "_blank");
  } else {
    router.push({ path: item.path, query: item.params });
  }
}

function loadRoutes(routes: RouteRecordRaw[], parentPath = "") {
  routes.forEach((route) => {
    const path = route.path.startsWith("/")
      ? route.path
      : `${parentPath}${parentPath.endsWith("/") ? "" : "/"}${route.path}`;
    if (excludedRoutes.value.includes(route.path) || isExternal(route.path)) return;

    if (route.children) {
      loadRoutes(route.children, path);
    } else if (route.meta?.title) {
      const title = route.meta.title === "dashboard" ? "홈" : route.meta.title;
      menuItems.value.push({
        title,
        path,
        name: typeof route.name === "string" ? route.name : undefined,
        icon: route.meta.icon,
        redirect: typeof route.redirect === "string" ? route.redirect : undefined,
        params: route.meta.params
          ? JSON.parse(JSON.stringify(toRaw(route.meta.params)))
          : undefined,
      });
    }
  });
}
</script>

<style scoped lang="scss">
.search-result {
  max-height: 400px;
  overflow-y: auto;

  ul {
    padding: 0;
    margin: 0;
    list-style: none;
  }

  &__item {
    display: flex;
    align-items: center;
    padding: 10px;
    text-align: left;
    cursor: pointer;

    &--active {
      color: var(--el-color-primary);
      background-color: var(--el-menu-hover-bg-color);
    }
  }
}

/* 검색 기록 스타일 */
.search-history {
  &__title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 12px;
    font-size: 12px;
    line-height: 34px;
    color: var(--el-text-color-secondary);
  }

  &__clear {
    padding: 2px;
    font-size: 12px;

    &:hover {
      color: var(--el-color-danger);
    }
  }

  &__list {
    padding: 0;
    margin: 0;
  }

  &__icon {
    display: flex;
    align-items: center;
    margin-right: 10px;
    font-size: 16px;
    color: var(--el-text-color-secondary);
  }

  &__name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 14px;
    color: var(--el-text-color-primary);
    white-space: nowrap;
  }

  &__action {
    padding: 4px;
    color: var(--el-text-color-secondary);
    border-radius: 4px;
    opacity: 0;
    transition: opacity 0.2s;

    &:hover {
      color: var(--el-color-danger);
      background-color: var(--el-fill-color);
    }
  }

  &__item {
    display: flex;
    align-items: center;
    height: 40px;
    padding: 0 12px;
    cursor: pointer;

    &:hover {
      background-color: var(--el-fill-color-light);

      .search-history__action {
        opacity: 1;
      }
    }
  }
}

/* 검색 기록이 없을 때의 스타일 */
.no-history {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100px;

  &__text {
    font-size: 14px;
    color: var(--el-text-color-secondary);
  }
}

.dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.shortcuts-group {
  display: flex;
  gap: 15px;
  align-items: center;
}

.key-box {
  display: flex;
  gap: 5px;
  align-items: center;
}

.arrow-box {
  display: flex;
  gap: 5px;
  align-items: center;
}

.arrow-up-down {
  display: flex;
  gap: 2px;
  align-items: center;
}

.key-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 20px;
  padding: 0 4px;
  font-size: 12px;
  color: var(--el-text-color-regular);
  background-color: var(--el-fill-color-blank);
  border: 1px solid var(--el-border-color);
  border-radius: 3px;
  box-shadow:
    inset 0 -2px 0 0 var(--el-border-color),
    inset 0 0 1px 1px var(--el-color-white),
    0 1px 2px rgba(30, 35, 90, 0.2);

  &::before {
    position: absolute;
    top: 1px;
    right: 1px;
    left: 1px;
    height: 50%;
    pointer-events: none;
    content: "";
    background: linear-gradient(to bottom, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0));
    border-radius: 2px 2px 0 0;
  }
}

.esc-btn {
  font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
  font-size: 11px;
}

.key-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.ctrl-k-hint {
  display: flex;
  align-items: center;
}

.ctrl-k-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

// Element Plus 대화 상자 적응
:deep(.el-dialog__footer) {
  box-sizing: border-box;
  padding-top: 10px;
  text-align: right;
}

// 어두운 모드 적응
html.dark {
  .key-btn::before {
    background: linear-gradient(to bottom, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0));
  }
}
</style>
