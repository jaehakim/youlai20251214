<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" @submit.prevent>
        <el-form-item label="키워드" prop="keywords">
          <el-input
            v-model="queryParams.keywords"
            placeholder="부서명"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item label="부서 상태" prop="status">
          <el-select v-model="queryParams.status" placeholder="전체" clearable style="width: 100px">
            <el-option :value="1" label="정상" />
            <el-option :value="0" label="비활성" />
          </el-select>
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button class="filter-item" type="primary" icon="search" @click="handleQuery">
            검색
          </el-button>
          <el-button icon="refresh" @click="handleResetQuery">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="data-table">
      <div class="data-table__toolbar">
        <div class="data-table__toolbar--actions">
          <el-button
            v-hasPerm="['sys:dept:add']"
            type="success"
            icon="plus"
            @click="handleOpenDialog()"
          >
            추가
          </el-button>
          <el-button
            v-hasPerm="['sys:dept:delete']"
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
        v-loading="loading"
        :data="deptList"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        class="data-table__content"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="name" label="부서명" min-width="200" />
        <el-table-column prop="code" label="부서 코드" width="200" />
        <el-table-column prop="status" label="상태" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status == 1" type="success">정상</el-tag>
            <el-tag v-else type="info">비활성</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="sort" label="정렬" width="100" />

        <el-table-column label="작업" fixed="right" align="left" width="200">
          <template #default="scope">
            <el-button
              v-hasPerm="['sys:dept:add']"
              type="primary"
              link
              size="small"
              icon="plus"
              @click.stop="handleOpenDialog(scope.row.id, undefined)"
            >
              추가
            </el-button>
            <el-button
              v-hasPerm="['sys:dept:edit']"
              type="primary"
              link
              size="small"
              icon="edit"
              @click.stop="handleOpenDialog(scope.row.parentId, scope.row.id)"
            >
              수정
            </el-button>
            <el-button
              v-hasPerm="['sys:dept:delete']"
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

    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="600px"
      :close-on-click-modal="false"
      @closed="handleCloseDialog"
    >
      <el-form ref="deptFormRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="상위 부서" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            placeholder="상위 부서 선택"
            :data="deptOptions"
            filterable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="부서명" prop="name">
          <el-input v-model="formData.name" placeholder="부서명을 입력하세요" />
        </el-form-item>
        <el-form-item label="부서 코드" prop="code">
          <el-input v-model="formData.code" placeholder="부서 코드를 입력하세요" />
        </el-form-item>
        <el-form-item label="표시 순서" prop="sort">
          <el-input-number
            v-model="formData.sort"
            controls-position="right"
            style="width: 100px"
            :min="0"
          />
        </el-form-item>
        <el-form-item label="부서 상태">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">정상</el-radio>
            <el-radio :value="0">비활성</el-radio>
          </el-radio-group>
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
  name: "Dept",
  inheritAttrs: false,
});

import DeptAPI, { DeptVO, DeptForm, DeptQuery } from "@/api/system/dept-api";

const queryFormRef = ref();
const deptFormRef = ref();

const loading = ref(false);
const selectIds = ref<number[]>([]);
const queryParams = reactive<DeptQuery>({});

const dialog = reactive({
  title: "",
  visible: false,
});

const deptList = ref<DeptVO[]>();
const deptOptions = ref<OptionType[]>();
const formData = reactive<DeptForm>({
  status: 1,
  parentId: "0",
  sort: 1,
});

const rules = reactive({
  parentId: [{ required: true, message: "상위 부서를 입력하세요", trigger: "change" }],
  name: [{ required: true, message: "부서명을 입력하세요", trigger: "blur" }],
  code: [{ required: true, message: "부서 코드를 입력하세요", trigger: "blur" }],
  sort: [{ required: true, message: "표시 순서를 입력하세요", trigger: "blur" }],
});

// 부서 조회
function handleQuery() {
  loading.value = true;
  DeptAPI.getList(queryParams).then((data) => {
    deptList.value = data;
    loading.value = false;
  });
}

// 쿼리 초기화
function handleResetQuery() {
  queryFormRef.value.resetFields();
  handleQuery();
}

// 선택 항목 변경 처리
function handleSelectionChange(selection: any) {
  selectIds.value = selection.map((item: any) => item.id);
}

/**
 * 부서 대화상자 열기
 *
 * @param parentId 상위 부서 ID
 * @param deptId 부서 ID
 */
async function handleOpenDialog(parentId?: string, deptId?: string) {
  // 부서 드롭다운 데이터 로드
  const data = await DeptAPI.getOptions();
  deptOptions.value = [
    {
      value: "0",
      label: "최상위 부서",
      children: data,
    },
  ];

  dialog.visible = true;
  if (deptId) {
    dialog.title = "부서 수정";
    DeptAPI.getFormData(deptId).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "부서 추가";
    formData.parentId = parentId || "0";
  }
}

// 부서 폼 제출
function handleSubmit() {
  deptFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const deptId = formData.id;
      if (deptId) {
        DeptAPI.update(deptId, formData)
          .then(() => {
            ElMessage.success("수정 성공");
            handleCloseDialog();
            handleQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        DeptAPI.create(formData)
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

// 부서 삭제
function handleDelete(deptId?: number) {
  const deptIds = [deptId || selectIds.value].join(",");

  if (!deptIds) {
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
      DeptAPI.deleteByIds(deptIds)
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

// 폼 초기화
function resetForm() {
  deptFormRef.value.resetFields();
  deptFormRef.value.clearValidate();

  formData.id = undefined;
  formData.parentId = "0";
  formData.status = 1;
  formData.sort = 1;
}

// 대화상자 닫기
function handleCloseDialog() {
  dialog.visible = false;
  resetForm();
}

onMounted(() => {
  handleQuery();
});
</script>
