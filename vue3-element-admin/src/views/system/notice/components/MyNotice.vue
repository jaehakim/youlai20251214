<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" @submit.prevent>
        <el-form-item label="공지 제목" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="키워드"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery()"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" @click="handleQuery()">
            <template #icon>
              <Search />
            </template>
            검색
          </el-button>
          <el-button @click="handleResetQuery()">
            <template #icon>
              <Refresh />
            </template>
            초기화
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="data-table">
      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="pageData"
        highlight-current-row
        border
        stripe
        class="data-table__content"
      >
        <el-table-column type="index" label="번호" width="60" />
        <el-table-column label="공지 제목" prop="title" min-width="200" />
        <el-table-column align="center" label="공지 유형" width="150">
          <template #default="scope">
            <DictLabel v-model="scope.row.noticeType" code="notice_type" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="발행자" prop="publisherName" width="100" />
        <el-table-column align="center" label="공지 등급" width="100">
          <template #default="scope">
            <DictLabel v-model="scope.row.noticeLevel" code="notice_level" />
          </template>
        </el-table-column>
        <el-table-column
          key="releaseTime"
          align="center"
          label="발행 시간"
          prop="publishTime"
          width="150"
        />

        <el-table-column align="center" label="발행자" prop="publisherName" width="150" />
        <el-table-column align="center" label="상태" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.isRead == 1" type="success">읽음</el-tag>
            <el-tag v-else type="info">읽지 않음</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" fixed="right" label="작업" width="80">
          <template #default="scope">
            <el-button type="primary" size="small" link @click="handleReadNotice(scope.row.id)">
              보기
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery()"
      />
    </el-card>

    <el-dialog
      v-model="noticeDialogVisible"
      :title="noticeDetail?.title ?? '알림상세'"
      width="800px"
      custom-class="notice-detail"
      :close-on-click-modal="false"
    >
      <div v-if="noticeDetail" class="notice-detail__wrapper">
        <div class="notice-detail__meta">
          <span>
            <el-icon><User /></el-icon>
            {{ noticeDetail.publisherName }}
          </span>
          <span class="ml-2">
            <el-icon><Timer /></el-icon>
            {{ noticeDetail.publishTime }}
          </span>
        </div>

        <div class="notice-detail__content">
          <div v-html="noticeDetail.content"></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "MyNotice",
  inheritAttrs: false,
});

import NoticeAPI, { NoticePageVO, NoticePageQuery, NoticeDetailVO } from "@/api/system/notice-api";

const queryFormRef = ref();
const pageData = ref<NoticePageVO[]>([]);

const loading = ref(false);
const total = ref(0);

const queryParams = reactive<NoticePageQuery>({
  pageNum: 1,
  pageSize: 10,
});

const noticeDialogVisible = ref(false);
const noticeDetail = ref<NoticeDetailVO | null>(null);

// 알림 공지사항 조회
function handleQuery() {
  loading.value = true;
  NoticeAPI.getMyNoticePage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 알림 공지사항 조회 초기화
function handleResetQuery() {
  queryFormRef.value!.resetFields();
  queryParams.pageNum = 1;
  handleQuery();
}

// 알림 공지사항 읽기
function handleReadNotice(id: string) {
  NoticeAPI.getDetail(id).then((data) => {
    noticeDialogVisible.value = true;
    noticeDetail.value = data;
  });
}

onMounted(() => {
  handleQuery();
});
</script>

<style lang="scss" scoped>
:deep(.el-dialog__header) {
  text-align: center;
}
.notice-detail {
  &__wrapper {
    padding: 0 20px;
  }

  &__meta {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  &__publisher {
    margin-right: 24px;

    i {
      margin-right: 4px;
    }
  }

  &__content {
    max-height: 60vh;
    padding-top: 16px;
    margin-bottom: 24px;
    overflow-y: auto;
    border-top: 1px solid var(--el-border-color);

    &::-webkit-scrollbar {
      width: 6px;
    }
  }
}
</style>
