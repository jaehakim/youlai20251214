<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item prop="keywords" label="키워드">
          <el-input
            v-model="queryParams.keywords"
            placeholder="로그 내용"
            clearable
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
            style="width: 200px"
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
        class="data-table__content"
      >
        <el-table-column label="작업 시간" prop="createTime" width="180" />
        <el-table-column label="작업자" prop="operator" width="120" />
        <el-table-column label="로그 모듈" prop="module" width="100" />
        <el-table-column label="로그 내용" prop="content" min-width="200" />
        <el-table-column label="IP 주소" prop="ip" width="150" />
        <el-table-column label="지역" prop="region" width="150" />
        <el-table-column label="브라우저" prop="browser" width="150" />
        <el-table-column label="끝단 시스템" prop="os" width="200" show-overflow-tooltip />
        <el-table-column label="실행 시간(ms)" prop="executionTime" width="150" />
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="fetchData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Log",
  inheritAttrs: false,
});

import LogAPI, { LogPageVO, LogPageQuery } from "@/api/system/log-api";

const queryFormRef = ref();

const loading = ref(false);
const total = ref(0);

const queryParams = reactive<LogPageQuery>({
  pageNum: 1,
  pageSize: 10,
  keywords: "",
  createTime: ["", ""],
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
