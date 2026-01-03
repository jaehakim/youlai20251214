<!-- 사전 -->
<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" @submit.prevent>
        <el-form-item label="키워드" prop="keywords">
          <el-input
            v-model="queryParams.keywords"
            placeholder="사전명/코드"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" icon="search" @click="handleQuery">검색</el-button>
          <el-button icon="refresh" @click="handleResetQuery">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="data-table">
      <div class="data-table__toolbar">
        <div class="data-table__toolbar--actions">
          <el-button type="success" icon="plus" @click="handleAddClick()">추가</el-button>
          <el-button
            type="danger"
            :disabled="ids.length === 0"
            icon="delete"
            @click="handleDelete()"
          >
            삭제
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        highlight-current-row
        :data="tableData"
        border
        stripe
        height="calc(100vh - 84px - 236px)"
        class="data-table__content"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="사전명" prop="name" />
        <el-table-column label="사전 코드" prop="dictCode" />
        <el-table-column label="상태" prop="status">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? "활성" : "비활성" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="작업" align="center" width="220">
          <template #default="scope">
            <el-button type="primary" link size="small" @click.stop="handleOpenDictData(scope.row)">
              <template #icon>
                <Collection />
              </template>
              사전 데이터
            </el-button>

            <el-button
              type="primary"
              link
              size="small"
              icon="edit"
              @click.stop="handleEditClick(scope.row.id)"
            >
              수정
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              icon="delete"
              @click.stop="handleDelete(scope.row.id)"
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
        @pagination="fetchData"
      />
    </div>

    <!--사전 대화상자-->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      :close-on-click-modal="false"
      @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="computedRules" label-width="80px">
        <el-form-item label="사전명" prop="name">
          <el-input v-model="formData.name" placeholder="사전명을 입력하세요" />
        </el-form-item>

        <el-form-item label="사전 코드" prop="dictCode">
          <el-input v-model="formData.dictCode" placeholder="사전 코드를 입력하세요" />
        </el-form-item>

        <el-form-item label="상태">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">활성</el-radio>
            <el-radio :value="0">비활성</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="비고">
          <el-input v-model="formData.remark" type="textarea" placeholder="비고를 입력하세요" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmitClick">확인</el-button>
          <el-button @click="handleCloseDialog">취소</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Dict",
  inherititems: false,
});

import DictAPI, { DictPageQuery, DictPageVO, DictForm } from "@/api/system/dict-api";

import router from "@/router";

const queryFormRef = ref();
const dataFormRef = ref();

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<DictPageQuery>({
  pageNum: 1,
  pageSize: 10,
});

const tableData = ref<DictPageVO[]>();

const dialog = reactive({
  title: "",
  visible: false,
});

const formData = reactive<DictForm>({});

const computedRules = computed(() => {
  const rules: Partial<Record<string, any>> = {
    name: [{ required: true, message: "사전명을 입력하세요", trigger: "blur" }],
    dictCode: [{ required: true, message: "사전 코드를 입력하세요", trigger: "blur" }],
  };
  return rules;
});

// 데이터 가져오기
function fetchData() {
  loading.value = true;
  DictAPI.getPage(queryParams)
    .then((data) => {
      tableData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 조회 (페이지 번호 초기화 후 데이터 가져오기)
function handleQuery() {
  queryParams.pageNum = 1;
  fetchData();
}

// 쿼리 초기화
function handleResetQuery() {
  queryFormRef.value.resetFields();
  queryParams.pageNum = 1;
  fetchData();
}

// 행 선택
function handleSelectionChange(selection: any) {
  ids.value = selection.map((item: any) => item.id);
}

// 사전 추가
function handleAddClick() {
  dialog.visible = true;
  dialog.title = "사전 추가";
}

/**
 * 사전 수정
 *
 * @param id 사전 ID
 */
function handleEditClick(id: string) {
  dialog.visible = true;
  dialog.title = "사전 수정";
  DictAPI.getFormData(id).then((data) => {
    Object.assign(formData, data);
  });
}

// 사전 폼 제출
function handleSubmitClick() {
  dataFormRef.value.validate((isValid: boolean) => {
    if (isValid) {
      loading.value = true;
      const id = formData.id;
      if (id) {
        DictAPI.update(id, formData)
          .then(() => {
            ElMessage.success("수정 성공");
            handleCloseDialog();
            handleQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        DictAPI.create(formData)
          .then(() => {
            ElMessage.success("추가 성공");
            handleCloseDialog();
            handleQuery();
          })
          .finally(() => (loading.value = false));
      }
    }
  });
}

// 사전 대화상자 닫기
function handleCloseDialog() {
  dialog.visible = false;

  dataFormRef.value.resetFields();
  dataFormRef.value.clearValidate();

  formData.id = undefined;
}
/**
 * 사전 삭제
 *
 * @param id 사전 ID
 */
function handleDelete(id?: number) {
  const attrGroupIds = [id || ids.value].join(",");
  if (!attrGroupIds) {
    ElMessage.warning("삭제할 항목을 선택하세요");
    return;
  }
  ElMessageBox.confirm("선택한 데이터 항목을 삭제하시겠습니까?", "경고", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    type: "warning",
  }).then(
    () => {
      DictAPI.deleteByIds(attrGroupIds).then(() => {
        ElMessage.success("삭제 성공");
        handleResetQuery();
      });
    },
    () => {
      ElMessage.info("삭제가 취소되었습니다");
    }
  );
}

// 사전 항목 열기
function handleOpenDictData(row: DictPageVO) {
  router.push({
    path: "/system/dict-item",
    query: { dictCode: row.dictCode, title: "【" + row.name + "】사전 데이터" },
  });
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
