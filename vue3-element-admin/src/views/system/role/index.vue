<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item prop="keywords" label="키워드">
          <el-input
            v-model="queryParams.keywords"
            placeholder="역할명"
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
        ref="dataTableRef"
        v-loading="loading"
        :data="roleList"
        highlight-current-row
        border
        class="data-table__content"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="역할명" prop="name" min-width="100" />
        <el-table-column label="역할 코드" prop="code" width="150" />

        <el-table-column label="상태" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">정상</el-tag>
            <el-tag v-else type="info">비활성</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="정렬" align="center" width="80" prop="sort" />

        <el-table-column fixed="right" label="작업" width="220">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              icon="position"
              @click="handleOpenAssignPermDialog(scope.row)"
            >
              권한 할당
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              icon="edit"
              @click="handleOpenDialog(scope.row.id)"
            >
              수정
            </el-button>
            <el-button
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

    <!-- 역할 양식 팝업 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="500px"
      @close="handleCloseDialog"
    >
      <el-form ref="roleFormRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="역할명" prop="name">
          <el-input v-model="formData.name" placeholder="역할명을 입력하세요" />
        </el-form-item>

        <el-form-item label="역할 코드" prop="code">
          <el-input v-model="formData.code" placeholder="역할 코드를 입력하세요" />
        </el-form-item>

        <el-form-item label="데이터 권한" prop="dataScope">
          <el-select v-model="formData.dataScope">
            <el-option :key="1" label="전체 데이터" :value="1" />
            <el-option :key="2" label="부서 및 하위 부서 데이터" :value="2" />
            <el-option :key="3" label="본 부서 데이터" :value="3" />
            <el-option :key="4" label="본인 데이터" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="상태" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">정상</el-radio>
            <el-radio :value="0">중지</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="정렬" prop="sort">
          <el-input-number
            v-model="formData.sort"
            controls-position="right"
            :min="0"
            style="width: 100px"
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

    <!-- 권한 할당 팝업 -->
    <el-drawer
      v-model="assignPermDialogVisible"
      :title="'【' + checkedRole.name + '】권한 할당'"
      :size="drawerSize"
    >
      <div class="flex-x-between">
        <el-input v-model="permKeywords" clearable class="w-[150px]" placeholder="메뉴 권한명">
          <template #prefix>
            <Search />
          </template>
        </el-input>

        <div class="flex-center ml-5">
          <el-button type="primary" size="small" plain @click="togglePermTree">
            <template #icon>
              <Switch />
            </template>
            {{ isExpanded ? "축소" : "확장" }}
          </el-button>
          <el-checkbox
            v-model="parentChildLinked"
            class="ml-5"
            @change="handleparentChildLinkedChange"
          >
            부모-자식 연동
          </el-checkbox>

          <el-tooltip placement="bottom">
            <template #content>
              메뉴 권한만 선택하고 하위 메뉴나 버튼 권한을 선택하지 않으려면 부모-자식 연동을 끄세요
            </template>
            <el-icon class="ml-1 color-[--el-color-primary] inline-block cursor-pointer">
              <QuestionFilled />
            </el-icon>
          </el-tooltip>
        </div>
      </div>

      <el-tree
        ref="permTreeRef"
        node-key="value"
        show-checkbox
        :data="menuPermOptions"
        :filter-node-method="handlePermFilter"
        :default-expand-all="true"
        :check-strictly="!parentChildLinked"
        class="mt-5"
      >
        <template #default="{ data }">
          {{ data.label }}
        </template>
      </el-tree>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleAssignPermSubmit">확인</el-button>
          <el-button @click="assignPermDialogVisible = false">취소</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { useAppStore } from "@/store/modules/app-store";
import { DeviceEnum } from "@/enums/settings/device-enum";

import RoleAPI, { RolePageVO, RoleForm, RolePageQuery } from "@/api/system/role-api";
import MenuAPI from "@/api/system/menu-api";

defineOptions({
  name: "Role",
  inheritAttrs: false,
});

const appStore = useAppStore();

const queryFormRef = ref();
const roleFormRef = ref();
const permTreeRef = ref();

const loading = ref(false);
const ids = ref<number[]>([]);
const total = ref(0);

const queryParams = reactive<RolePageQuery>({
  pageNum: 1,
  pageSize: 10,
});

// 역할 테이블 데이터
const roleList = ref<RolePageVO[]>();
// 메뉴 권한 드롭다운
const menuPermOptions = ref<OptionType[]>([]);

// 팝업
const dialog = reactive({
  title: "",
  visible: false,
});

const drawerSize = computed(() => (appStore.device === DeviceEnum.DESKTOP ? "600px" : "90%"));

// 역할 양식
const formData = reactive<RoleForm>({
  sort: 1,
  status: 1,
});

const rules = reactive({
  name: [{ required: true, message: "역할명을 입력하세요", trigger: "blur" }],
  code: [{ required: true, message: "역할 코드를 입력하세요", trigger: "blur" }],
  dataScope: [{ required: true, message: "데이터 권한을 선택하세요", trigger: "blur" }],
  status: [{ required: true, message: "상태를 선택하세요", trigger: "blur" }],
});

// 선택된 역할
interface CheckedRole {
  id?: string;
  name?: string;
}
const checkedRole = ref<CheckedRole>({});
const assignPermDialogVisible = ref(false);

const permKeywords = ref("");
const isExpanded = ref(true);

const parentChildLinked = ref(true);

// 데이터 가져오기
function fetchData() {
  loading.value = true;
  RoleAPI.getPage(queryParams)
    .then((data) => {
      roleList.value = data.list;
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

// 행 체크박스 선택
function handleSelectionChange(selection: any) {
  ids.value = selection.map((item: any) => item.id);
}

// 역할 팝업 열기
function handleOpenDialog(roleId?: string) {
  dialog.visible = true;
  if (roleId) {
    dialog.title = "역할 수정";
    RoleAPI.getFormData(roleId).then((data) => {
      Object.assign(formData, data);
    });
  } else {
    dialog.title = "역할 추가";
  }
}

// 역할 양식 제출
function handleSubmit() {
  roleFormRef.value.validate((valid: any) => {
    if (valid) {
      loading.value = true;
      const roleId = formData.id;
      if (roleId) {
        RoleAPI.update(roleId, formData)
          .then(() => {
            ElMessage.success("수정 성공");
            handleCloseDialog();
            handleResetQuery();
          })
          .finally(() => (loading.value = false));
      } else {
        RoleAPI.create(formData)
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

// 팝업 닫기
function handleCloseDialog() {
  dialog.visible = false;

  roleFormRef.value.resetFields();
  roleFormRef.value.clearValidate();

  formData.id = undefined;
  formData.sort = 1;
  formData.status = 1;
}

// 역할 삭제
function handleDelete(roleId?: number) {
  const roleIds = [roleId || ids.value].join(",");
  if (!roleIds) {
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
      RoleAPI.deleteByIds(roleIds)
        .then(() => {
          ElMessage.success("삭제 성공");
          handleResetQuery();
        })
        .finally(() => (loading.value = false));
    },
    () => {
      ElMessage.info("삭제 취소됨");
    }
  );
}

// 메뉴 권한 할당 팝업 열기
async function handleOpenAssignPermDialog(row: RolePageVO) {
  const roleId = row.id;
  if (roleId) {
    assignPermDialogVisible.value = true;
    loading.value = true;

    checkedRole.value.id = roleId;
    checkedRole.value.name = row.name;

    // 모든 메뉴 가져오기
    menuPermOptions.value = await MenuAPI.getOptions();

    // 역할이 이미 소유한 메뉴 표시
    RoleAPI.getRoleMenuIds(roleId)
      .then((data) => {
        const checkedMenuIds = data;
        checkedMenuIds.forEach((menuId) => permTreeRef.value!.setChecked(menuId, true, false));
      })
      .finally(() => {
        loading.value = false;
      });
  }
}

// 메뉴 권한 할당 제출
function handleAssignPermSubmit() {
  const roleId = checkedRole.value.id;
  if (roleId) {
    const checkedMenuIds: number[] = permTreeRef
      .value!.getCheckedNodes(false, true)
      .map((node: any) => node.value);

    loading.value = true;
    RoleAPI.updateRoleMenus(roleId, checkedMenuIds)
      .then(() => {
        ElMessage.success("권한 할당 성공");
        assignPermDialogVisible.value = false;
        handleResetQuery();
      })
      .finally(() => {
        loading.value = false;
      });
  }
}

// 메뉴 권한 트리 확장/축소
function togglePermTree() {
  isExpanded.value = !isExpanded.value;
  if (permTreeRef.value) {
    Object.values(permTreeRef.value.store.nodesMap).forEach((node: any) => {
      if (isExpanded.value) {
        node.expand();
      } else {
        node.collapse();
      }
    });
  }
}

// 권한 필터링
watch(permKeywords, (val) => {
  permTreeRef.value!.filter(val);
});

function handlePermFilter(
  value: string,
  data: {
    [key: string]: any;
  }
) {
  if (!value) return true;
  return data.label.includes(value);
}

// 부모-자식 메뉴 노드 연동 여부
function handleparentChildLinkedChange(val: any) {
  parentChildLinked.value = val;
}

onMounted(() => {
  handleQuery();
});
</script>
