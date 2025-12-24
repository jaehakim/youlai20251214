<!-- 사전 항목 -->
<template>
  <div class="app-container">
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="키워드" prop="keywords">
          <el-input
            v-model="queryParams.keywords"
            placeholder="사전 레이블/사전 값"
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

    <el-card shadow="never" class="data-table">
      <div class="data-table__toolbar">
        <div class="data-table__toolbar--actions">
          <el-button type="success" icon="plus" @click="handleOpenDialog()">추가</el-button>
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
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="사전 항목 레이블" prop="label" />
        <el-table-column label="사전 항목 값" prop="value" />
        <el-table-column label="정렬" prop="sort" />
        <el-table-column label="상태">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? "활성화" : "비활성화" }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column fixed="right" label="작업" align="center" width="220">
          <template #default="scope">
            <el-button
              type="primary"
              link
              size="small"
              icon="edit"
              @click.stop="handleOpenDialog(scope.row)"
            >
              편집
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

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="fetchData"
      />
    </el-card>

    <!--사전 항목 다이얼로그-->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="600px"
      @close="handleCloseDialog"
    >
      <el-form ref="dataFormRef" :model="formData" :rules="computedRules" label-width="100px">
        <el-form-item label="사전 항목 레이블" prop="label">
          <el-input v-model="formData.label" placeholder="사전 레이블을 입력하세요" />
        </el-form-item>
        <el-form-item label="사전 항목 값" prop="value">
          <el-input v-model="formData.value" placeholder="사전 값을 입력하세요" />
        </el-form-item>
        <el-form-item label="상태">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">활성화</el-radio>
            <el-radio :value="0">비활성화</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="정렬">
          <el-input-number v-model="formData.sort" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <template #label>
            <div class="flex-y-center">
              레이블 유형
              <el-tooltip>
                <template #content>표시 스타일, 비어있으면 '텍스트'로 표시됩니다</template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-select
            v-model="formData.tagType"
            placeholder="레이블 유형을 선택하세요"
            clearable
            @clear="formData.tagType = ''"
          >
            <template #label="{ value }">
              <el-tag v-if="value" :type="value">
                {{ formData.label ? formData.label : "사전 레이블" }}
              </el-tag>
            </template>
            <!-- <el-option label="기본 텍스트" value="" /> -->
            <el-option v-for="type in tagType" :key="type" :label="type" :value="type">
              <div flex-y-center gap-10px>
                <el-tag :type="type">{{ formData.label ?? "사전 레이블" }}</el-tag>
                <span>{{ type }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmitClick">확 인</el-button>
          <el-button @click="handleCloseDialog">취 소</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { TagProps } from "element-plus";
import DictAPI, { DictItemPageQuery, DictItemPageVO, DictItemForm } from "@/api/system/dict-api";

const route = useRoute();

const dictCode = ref(route.query.dictCode as string);

const queryFormRef = ref();
const dataFormRef = ref();

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<DictItemPageQuery>({
  pageNum: 1,
  pageSize: 10,
});

const tableData = ref<DictItemPageVO[]>();

const dialog = reactive({
  title: "",
  visible: false,
});

const formData = reactive<DictItemForm>({});

// 레이블 유형
const tagType: TagProps["type"][] = ["primary", "success", "info", "warning", "danger"];

const computedRules = computed(() => {
  const rules: Partial<Record<string, any>> = {
    value: [{ required: true, message: "사전 값을 입력하세요", trigger: "blur" }],
    label: [{ required: true, message: "사전 레이블을 입력하세요", trigger: "blur" }],
  };

  return rules;
});

// 데이터 가져오기
function fetchData() {
  loading.value = true;
  DictAPI.getDictItemPage(dictCode.value, queryParams)
    .then((data) => {
      tableData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 쿼리 (페이지 번호 초기화 후 데이터 가져오기)
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

// 다이얼로그 열기
function handleOpenDialog(row?: DictItemPageVO) {
  dialog.visible = true;
  dialog.title = row ? "사전 항목 편집" : "사전 항목 추가";

  if (row?.id) {
    DictAPI.getDictItemFormData(dictCode.value, row.id).then((data) => {
      Object.assign(formData, data);
    });
  }
}

// 양식 제출
function handleSubmitClick() {
  dataFormRef.value.validate((isValid: boolean) => {
    if (isValid) {
      loading.value = true;
      const id = formData.id;

      formData.dictCode = dictCode.value;
      if (id) {
        DictAPI.updateDictItem(dictCode.value, id, formData)
          .then(() => {
            ElMessage.success("수정 완료");
            handleCloseDialog();
            handleQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        DictAPI.createDictItem(dictCode.value, formData)
          .then(() => {
            ElMessage.success("추가 완료");
            handleCloseDialog();
            handleQuery();
          })
          .finally(() => (loading.value = false));
      }
    }
  });
}

// 다이얼로그 닫기
function handleCloseDialog() {
  dataFormRef.value.resetFields();
  dataFormRef.value.clearValidate();

  formData.id = undefined;
  formData.sort = 1;
  formData.status = 1;
  formData.tagType = "";

  dialog.visible = false;
}
/**
 * 사전 삭제
 *
 * @param id 사전 ID
 */
function handleDelete(id?: number) {
  const itemIds = [id || ids.value].join(",");

  if (!itemIds) {
    ElMessage.warning("삭제할 항목을 선택하세요");

    return;
  }
  ElMessageBox.confirm("선택한 데이터 항목을 삭제하시겠습니까?", "경고", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    type: "warning",
  }).then(
    () => {
      DictAPI.deleteDictItems(dictCode.value, itemIds).then(() => {
        ElMessage.success("삭제 완료");
        handleResetQuery();
      });
    },
    () => {
      ElMessage.info("삭제 취소됨");
    }
  );
}

onMounted(() => {
  handleQuery();
});
</script>
