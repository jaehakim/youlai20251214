<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-suffix=":" @submit.prevent>
        <el-form-item label="제목" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="제목"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery()"
          />
        </el-form-item>

        <el-form-item label="발행 상태" prop="publishStatus">
          <el-select
            v-model="queryParams.publishStatus"
            clearable
            placeholder="전체"
            style="width: 100px"
          >
            <el-option :value="0" label="미발행" />
            <el-option :value="1" label="발행됨" />
            <el-option :value="-1" label="철회됨" />
          </el-select>
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" icon="search" @click="handleQuery()">검색</el-button>
          <el-button icon="refresh" @click="handleResetQuery()">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="data-table">
      <div class="data-table__toolbar">
        <div class="data-table__toolbar--actions">
          <el-button
            v-hasPerm="['sys:notice:add']"
            type="success"
            icon="plus"
            @click="handleOpenDialog()"
          >
            공지사항 추가
          </el-button>
          <el-button
            v-hasPerm="['sys:notice:delete']"
            type="danger"
            :disabled="selectIds.length === 0"
            icon="delete"
            @click="handleDelete()"
          >
            삭제
          </el-button>
        </div>
      </div>

      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="pageData"
        highlight-current-row
        class="data-table__content"
        border
        stripe
        height="calc(100vh - 84px - 236px)"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="번호" width="60" />
        <el-table-column label="공지 제목" prop="title" min-width="200" />
        <el-table-column align="center" label="공지 유형" width="150">
          <template #default="scope">
            <DictLabel v-model="scope.row.type" :code="'notice_type'" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="발행인" prop="publisherName" width="150" />
        <el-table-column align="center" label="공지 등급" width="100">
          <template #default="scope">
            <DictLabel v-model="scope.row.level" code="notice_level" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="공지 대상 유형" prop="targetType" min-width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.targetType == 1" type="warning">전체</el-tag>
            <el-tag v-if="scope.row.targetType == 2" type="success">지정</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" label="발행 상태" min-width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.publishStatus == 0" type="info">미발행</el-tag>
            <el-tag v-if="scope.row.publishStatus == 1" type="success">발행됨</el-tag>
            <el-tag v-if="scope.row.publishStatus == -1" type="warning">철회됨</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="작업 시간" width="250">
          <template #default="scope">
            <div class="flex-x-start">
              <span>생성 시간:</span>
              <span>{{ scope.row.createTime || "-" }}</span>
            </div>

            <div v-if="scope.row.publishStatus === 1" class="flex-x-start">
              <span>발행 시간:</span>
              <span>{{ scope.row.publishTime || "-" }}</span>
            </div>
            <div v-else-if="scope.row.publishStatus === -1" class="flex-x-start">
              <span>철회 시간:</span>
              <span>{{ scope.row.revokeTime || "-" }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column align="center" fixed="right" label="작업" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" link @click="openDetailDialog(scope.row.id)">
              보기
            </el-button>
            <el-button
              v-if="scope.row.publishStatus != 1"
              v-hasPerm="['sys:notice:publish']"
              type="primary"
              size="small"
              link
              @click="handlePublish(scope.row.id)"
            >
              발행
            </el-button>
            <el-button
              v-if="scope.row.publishStatus == 1"
              v-hasPerm="['sys:notice:revoke']"
              type="primary"
              size="small"
              link
              @click="handleRevoke(scope.row.id)"
            >
              철회
            </el-button>
            <el-button
              v-if="scope.row.publishStatus != 1"
              v-hasPerm="['sys:notice:edit']"
              type="primary"
              size="small"
              link
              @click="handleOpenDialog(scope.row.id)"
            >
              편집
            </el-button>
            <el-button
              v-if="scope.row.publishStatus != 1"
              v-hasPerm="['sys:notice:delete']"
              type="danger"
              size="small"
              link
              @click="handleDelete(scope.row.id)"
            >
              삭제
            </el-button>
          </template>
        </el-table-column>
      </el-table>

    </el-card>

    <!-- Fixed Pagination -->
    <div v-if="total > 0" class="fixed-pagination">
      <pagination
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="fetchData()"
      />
    </div>

    <!-- 공지사항 폼 다이얼로그 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      top="3vh"
      width="80%"
      :close-on-click-modal="false"
      @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="공지 제목" prop="title">
          <el-input v-model="formData.title" placeholder="공지 제목" clearable />
        </el-form-item>

        <el-form-item label="공지 유형" prop="type">
          <Dict v-model="formData.type" code="notice_type" />
        </el-form-item>
        <el-form-item label="공지 등급" prop="level">
          <Dict v-model="formData.level" code="notice_level" />
        </el-form-item>
        <el-form-item label="대상 유형" prop="targetType">
          <el-radio-group v-model="formData.targetType">
            <el-radio :value="1">전체</el-radio>
            <el-radio :value="2">지정</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="formData.targetType == 2" label="지정 사용자" prop="targetUserIds">
          <el-select v-model="formData.targetUserIds" multiple search placeholder="지정 사용자를 선택하세요">
            <el-option
              v-for="item in userOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="공지 내용" prop="content">
          <WangEditor v-model="formData.content" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit()">확인</el-button>
          <el-button @click="handleCloseDialog()">취소</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 공지사항 상세정보 -->
    <el-dialog
      v-model="detailDialog.visible"
      :show-close="false"
      width="50%"
      append-to-body
      :close-on-click-modal="false"
      @close="closeDetailDialog"
    >
      <template #header>
        <div class="flex-x-between">
          <span>공지사항 상세정보</span>
          <div class="dialog-toolbar">
            <el-button circle @click="closeDetailDialog">
              <template #icon>
                <Close />
              </template>
            </el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="1">
        <el-descriptions-item label="제목:">
          {{ currentNotice.title }}
        </el-descriptions-item>
        <el-descriptions-item label="발행 상태:">
          <el-tag v-if="currentNotice.publishStatus == 0" type="info">미발행</el-tag>
          <el-tag v-else-if="currentNotice.publishStatus == 1" type="success">발행됨</el-tag>
          <el-tag v-else-if="currentNotice.publishStatus == -1" type="warning">철회됨</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="발행인:">
          {{ currentNotice.publisherName }}
        </el-descriptions-item>
        <el-descriptions-item label="발행 시간:">
          {{ currentNotice.publishTime }}
        </el-descriptions-item>
        <el-descriptions-item label="공지 내용:">
          <div class="notice-content" v-html="currentNotice.content" />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Notice",
  inheritAttrs: false,
});

import NoticeAPI, {
  NoticePageVO,
  NoticeForm,
  NoticePageQuery,
  NoticeDetailVO,
} from "@/api/system/notice-api";
import UserAPI from "@/api/system/user-api";

const queryFormRef = ref();
const dataFormRef = ref();

const loading = ref(false);
const selectIds = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<NoticePageQuery>({
  pageNum: 1,
  pageSize: 10,
});

const userOptions = ref<OptionType[]>([]);
// 공지사항 테이블 데이터
const pageData = ref<NoticePageVO[]>([]);

// 팝업
const dialog = reactive({
  title: "",
  visible: false,
});

// 공지사항 폼 데이터
const formData = reactive<NoticeForm>({
  level: "L", // 기본 우선순위: 낮음
  targetType: 1, // 기본 대상 유형: 전체
});

// 공지사항 폼 검증 규칙
const rules = reactive({
  title: [{ required: true, message: "공지 제목을 입력하세요", trigger: "blur" }],
  content: [
    {
      required: true,
      message: "공지 내용을 입력하세요",
      trigger: "blur",
      validator: (rule: any, value: string, callback: any) => {
        if (!value.replace(/<[^>]+>/g, "").trim()) {
          callback(new Error("공지 내용을 입력하세요"));
        } else {
          callback();
        }
      },
    },
  ],
  type: [{ required: true, message: "공지 유형을 선택하세요", trigger: "change" }],
});

const detailDialog = reactive({
  visible: false,
});
const currentNotice = ref<NoticeDetailVO>({});

// 공지사항 조회
function handleQuery() {
  queryParams.pageNum = 1;
  fetchData();
}

// 요청 인터페이스 발송
function fetchData() {
  loading.value = true;
  NoticeAPI.getPage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 조회 초기화
function handleResetQuery() {
  queryFormRef.value!.resetFields();
  queryParams.pageNum = 1;
  handleQuery();
}

// 행 체크박스 선택 변화
function handleSelectionChange(selection: any) {
  selectIds.value = selection.map((item: any) => item.id);
}

// 공지사항 다이얼로그 열기
function handleOpenDialog(id?: string) {
  UserAPI.getOptions().then((data) => {
    userOptions.value = data;
  });

  dialog.visible = true;
  if (id) {
    dialog.title = "공지사항 수정";
    NoticeAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    Object.assign(formData, { level: 0, targetType: 0 });
    dialog.title = "공지사항 추가";
  }
}

// 공지사항 발행
function handlePublish(id: string) {
  NoticeAPI.publish(id).then(() => {
    ElMessage.success("발행 성공");
    handleQuery();
  });
}

// 공지사항 철회
function handleRevoke(id: string) {
  NoticeAPI.revoke(id).then(() => {
    ElMessage.success("철회 성공");
    handleQuery();
  });
}

// 공지사항 양식 제출
function handleSubmit() {
  dataFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const id = formData.id;
      if (id) {
        NoticeAPI.update(id, formData)
          .then(() => {
            ElMessage.success("수정 성공");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        NoticeAPI.create(formData)
          .then(() => {
            ElMessage.success("추가 성공");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      }
    }
  });
}

// 양식 초기화
function resetForm() {
  dataFormRef.value.resetFields();
  dataFormRef.value.clearValidate();
  formData.id = undefined;
  formData.targetType = 1;
}

// 공지사항 팝업 닫기
function handleCloseDialog() {
  dialog.visible = false;
  resetForm();
}

// 공지사항 삭제
function handleDelete(id?: number) {
  const deleteIds = [id || selectIds.value].join(",");
  if (!deleteIds) {
    ElMessage.warning("삭제할 항목을 선택하세요");
    return;
  }

  ElMessageBox.confirm("선택한 데이터 항목을 삭제하시겠습니까?", "경고", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    type: "warning",
  }).then(
    () => {
      loading.value = true;
      NoticeAPI.deleteByIds(deleteIds)
        .then(() => {
          ElMessage.success("삭제 성공");
          handleResetQuery();
        })
        .finally(() => (loading.value = false));
    },
    () => {
      ElMessage.info("삭제가 취소되었습니다");
    }
  );
}

const closeDetailDialog = () => {
  detailDialog.visible = false;
};

const openDetailDialog = async (id: string) => {
  const noticeDetail = await NoticeAPI.getDetail(id);
  currentNotice.value = noticeDetail;
  detailDialog.visible = true;
};

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
