<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" @submit.prevent>
        <el-form-item prop="keywords" label="키워드">
          <el-input
            v-model="queryParams.keywords"
            placeholder="로그 내용"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item prop="createTime" label="작업 시간">
          <el-date-picker
            v-model="queryParams.createTime"
            :editable="false"
            type="daterange"
            range-separator="~"
            start-placeholder="시작 시간"
            end-placeholder="종료 시간"
            value-format="YYYY-MM-DD"
            style="width: 250px"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" icon="search" @click="handleQuery">검색</el-button>
          <el-button icon="refresh" @click="handleResetQuery">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="data-table">
      <el-table
        v-loading="loading"
        :data="pageData"
        highlight-current-row
        border
        stripe
        height="calc(100vh - 84px - 200px)"
        class="data-table__content"
        @sort-change="handleSortChange"
      >
        <el-table-column label="작업 시간" prop="createTime" width="180" sortable="custom" />
        <el-table-column label="작업자" prop="operator" width="120" sortable="custom" />
        <el-table-column label="로그 모듈" prop="module" width="100" sortable="custom" />
        <el-table-column label="로그 내용" prop="content" min-width="200" />
        <el-table-column label="IP 주소" prop="ip" width="150" sortable="custom" />
        <el-table-column label="지역" prop="region" width="150" sortable="custom" />
        <el-table-column label="브라우저" prop="browser" width="150" sortable="custom" />
        <el-table-column
          label="운영체제"
          prop="os"
          width="200"
          show-overflow-tooltip
          sortable="custom"
        />
        <el-table-column label="실행 시간(ms)" prop="executionTime" width="150" sortable="custom" />
      </el-table>

    </el-card>

    <!-- Fixed Pagination -->
    <div v-if="total > 0" class="fixed-pagination">
      <pagination
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="fetchData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Log",
  inheritAttrs: false,
});

import LogAPI, { LogPageVO, LogPageQuery } from "@/api/system/log.api";
import { convertToCamelCase } from "@/utils/utils";

const queryFormRef = ref();

const loading = ref(false);
const total = ref(0);

const queryParams = reactive<LogPageQuery>({
  pageNum: 1,
  pageSize: 10,
  keywords: "",
  createTime: ["", ""],
  sortBy: "", // 정렬 필드
  sortDirection: "", // 정렬 방향
});

// 로그 테이블 데이터
const pageData = ref<LogPageVO[]>();

/** 데이터 가져오기 */
function fetchData() {
  loading.value = true;
  LogAPI.getPage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

/** 정렬 변경 처리 */
function handleSortChange({ prop, order }: { prop: string; order: string | null }) {
  console.log("정렬 변경:", { prop, order });

  if (order) {
    // prop을 camelCase로 변환 (create_time -> createTime)
    queryParams.sortBy = convertToCamelCase(prop);
    queryParams.sortDirection = order === "ascending" ? "asc" : "desc";
  } else {
    // 정렬 제거
    queryParams.sortBy = "";
    queryParams.sortDirection = "";
  }

  // 정렬 변경 시 첫 페이지로 이동
  queryParams.pageNum = 1;
  fetchData();
}

/** 조회(페이지 초기화 후 데이터 가져오기) */
function handleQuery() {
  queryParams.pageNum = 1;
  fetchData();
}

/** 조회 초기화 */
function handleResetQuery() {
  queryFormRef.value.resetFields();
  queryParams.pageNum = 1;
  queryParams.createTime = undefined;
  fetchData();
}

onMounted(() => {
  handleQuery();
});
</script>

<style scoped lang="scss">
.app-container {
  overflow: hidden;
}

.fixed-pagination {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100vw;
  background: var(--el-bg-color-overlay, #fff);
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.04);
  z-index: 100;
  padding: 0 !important;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination) {
  margin: 0 !important;
}

:deep(.el-table__header th) {
  background-color: #e0e0e0 !important;
  font-weight: bold !important;
}
</style>
