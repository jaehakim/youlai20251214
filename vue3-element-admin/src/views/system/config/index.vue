<!-- 시스템 설정 -->
<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="키워드" prop="keywords">
          <el-input
            v-model="queryParams.keywords"
            placeholder="설정 키\설정 이름을 입력하세요"
            clearable
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
          <el-button
            v-hasPerm="['sys:config:add']"
            type="success"
            icon="plus"
            @click="handleOpenDialog()"
          >
            추가
          </el-button>
          <el-button
            v-hasPerm="['sys:config:refresh']"
            color="#626aef"
            icon="RefreshLeft"
            @click="handleRefreshCache"
          >
            캐시 새로고침
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
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="index" label="번호" width="60" />
        <el-table-column key="configName" label="설정 이름" prop="configName" min-width="100" />
        <el-table-column key="configKey" label="설정 키" prop="configKey" min-width="100" />
        <el-table-column key="configValue" label="설정 값" prop="configValue" min-width="100" />
        <el-table-column key="remark" label="설명" prop="remark" min-width="100" />
        <el-table-column fixed="right" label="작업" width="220">
          <template #default="scope">
            <el-button
              v-hasPerm="['sys:config:update']"
              type="primary"
              size="small"
              link
              icon="edit"
              @click="handleOpenDialog(scope.row.id)"
            >
              편집
            </el-button>
            <el-button
              v-hasPerm="['sys:config:delete']"
              type="danger"
              size="small"
              link
              icon="delete"
              @click="handleDelete(scope.row.id)"
            >
              삭제
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="fetchData"
      />
    </el-card>

    <!-- 시스템 설정 폼 다이얼로그 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="handleCloseDialog"
    >
      <el-form
        ref="dataFormRef"
        :model="formData"
        :rules="rules"
        label-suffix=":"
        label-width="100px"
      >
        <el-form-item label="설정 이름" prop="configName">
          <el-input v-model="formData.configName" placeholder="설정 이름을 입력하세요" :maxlength="50" />
        </el-form-item>
        <el-form-item label="설정 키" prop="configKey">
          <el-input v-model="formData.configKey" placeholder="설정 키를 입력하세요" :maxlength="50" />
        </el-form-item>
        <el-form-item label="설정 값" prop="configValue">
          <el-input v-model="formData.configValue" placeholder="설정 값을 입력하세요" :maxlength="100" />
        </el-form-item>
        <el-form-item label="설명" prop="remark">
          <el-input
            v-model="formData.remark"
            :rows="4"
            :maxlength="100"
            show-word-limit
            type="textarea"
            placeholder="설명을 입력하세요"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">확인</el-button>
          <el-button @click="handleCloseDialog">취소</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Config",
  inheritAttrs: false,
});

import ConfigAPI, { ConfigPageVO, ConfigForm, ConfigPageQuery } from "@/api/system/config-api";
import { ElMessage, ElMessageBox } from "element-plus";
import { useDebounceFn } from "@vueuse/core";

const queryFormRef = ref();
const dataFormRef = ref();

const loading = ref(false);
const selectIds = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<ConfigPageQuery>({
  pageNum: 1,
  pageSize: 10,
  keywords: "",
});

// 시스템 설정 테이블 데이터
const pageData = ref<ConfigPageVO[]>([]);

const dialog = reactive({
  title: "",
  visible: false,
});

const formData = reactive<ConfigForm>({
  id: undefined,
  configName: "",
  configKey: "",
  configValue: "",
  remark: "",
});

const rules = reactive({
  configName: [{ required: true, message: "시스템 설정 이름을 입력하세요", trigger: "blur" }],
  configKey: [{ required: true, message: "시스템 설정 코드를 입력하세요", trigger: "blur" }],
  configValue: [{ required: true, message: "시스템 설정 값을 입력하세요", trigger: "blur" }],
});

// 데이터 가져오기
function fetchData() {
  loading.value = true;
  ConfigAPI.getPage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 조회(페이지 초기화 후 데이터 가져오기)
function handleQuery() {
  queryParams.pageNum = 1;
  fetchData();
}

// 조회 초기화
function handleResetQuery() {
  queryFormRef.value.resetFields();
  queryParams.pageNum = 1;
  fetchData();
}

// 행 체크박스 선택 변화
function handleSelectionChange(selection: any) {
  selectIds.value = selection.map((item: any) => item.id);
}

// 시스템 설정 다이얼로그 열기
function handleOpenDialog(id?: string) {
  dialog.visible = true;
  if (id) {
    dialog.title = "시스템 설정 수정";
    ConfigAPI.getFormData(id).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "시스템 설정 추가";
    formData.id = undefined;
  }
}

// 캐시 새로고침(디바운스)
const handleRefreshCache = useDebounceFn(() => {
  ConfigAPI.refreshCache().then(() => {
    ElMessage.success("새로고침 성공");
  });
}, 1000);

// 시스템 설정 양식 제출
function handleSubmit() {
  dataFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const id = formData.id;
      if (id) {
        ConfigAPI.update(id, formData)
          .then(() => {
            ElMessage.success("수정 성공");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        ConfigAPI.create(formData)
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
}

// 시스템 설정 팝업 닫기
function handleCloseDialog() {
  dialog.visible = false;
  resetForm();
}

// 시스템 설정 삭제
function handleDelete(id: string) {
  ElMessageBox.confirm("해당 설정을 삭제하시겠습니까?", "경고", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    type: "warning",
  }).then(() => {
    loading.value = true;
    ConfigAPI.deleteById(id)
      .then(() => {
        ElMessage.success("삭제 성공");
        handleResetQuery();
      })
      .finally(() => (loading.value = false));
  });
}

onMounted(() => {
  handleQuery();
});
</script>
