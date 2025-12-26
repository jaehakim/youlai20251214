<!-- 사용자 관리 -->
<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 부서 트리 -->
      <el-col :lg="4" :xs="24" class="mb-[12px]">
        <DeptTree v-model="queryParams.deptId" @node-click="handleQuery" />
      </el-col>

      <!-- 사용자 목록 -->
      <el-col :lg="20" :xs="24">
        <!-- 검색 영역 -->
        <div class="search-container">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="auto">
            <el-form-item label="키워드" prop="keywords">
              <el-input
                v-model="queryParams.keywords"
                placeholder="사용자명/닉네임/휴대폰 번호"
                clearable
                @keyup.enter="handleQuery"
              />
            </el-form-item>

            <el-form-item label="상태" prop="status">
              <el-select
                v-model="queryParams.status"
                placeholder="전체"
                clearable
                style="width: 100px"
              >
                <el-option label="정상" :value="1" />
                <el-option label="비활성" :value="0" />
              </el-select>
            </el-form-item>

            <el-form-item label="생성 시간">
              <el-date-picker
                v-model="queryParams.createTime"
                :editable="false"
                type="daterange"
                range-separator="~"
                start-placeholder="시작 시간"
                end-placeholder="종료 시간"
                value-format="YYYY-MM-DD"
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
                v-hasPerm="['sys:user:add']"
                type="success"
                icon="plus"
                @click="handleOpenDialog()"
              >
                추가
              </el-button>
              <el-button
                v-hasPerm="'sys:user:delete'"
                type="danger"
                icon="delete"
                :disabled="!hasSelection"
                @click="handleDelete()"
              >
                삭제
              </el-button>
            </div>
            <div class="data-table__toolbar--tools">
              <el-button
                v-hasPerm="'sys:user:import'"
                icon="upload"
                @click="handleOpenImportDialog"
              >
                가져오기
              </el-button>

              <el-button v-hasPerm="'sys:user:export'" icon="download" @click="handleExport">
                내보내기
              </el-button>
            </div>
          </div>

          <el-table
            v-loading="loading"
            :data="pageData"
            border
            stripe
            highlight-current-row
            class="data-table__content"
            row-key="id"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="사용자명" prop="username" />
            <el-table-column label="닉네임" width="150" align="center" prop="nickname" />
            <el-table-column label="성별" width="100" align="center">
              <template #default="scope">
                <DictLabel v-model="scope.row.gender" code="gender" />
              </template>
            </el-table-column>
            <el-table-column label="부서" width="120" align="center" prop="deptName" />
            <el-table-column label="휴대폰 번호" align="center" prop="mobile" width="120" />
            <el-table-column label="이메일" align="center" prop="email" width="160" />
            <el-table-column label="상태" align="center" prop="status" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.status == 1 ? 'success' : 'info'">
                  {{ scope.row.status == 1 ? "정상" : "비활성" }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="생성 시간" align="center" prop="createTime" width="150" />
            <el-table-column label="작업" fixed="right" width="220">
              <template #default="scope">
                <el-button
                  v-hasPerm="'sys:user:reset-password'"
                  type="primary"
                  icon="RefreshLeft"
                  size="small"
                  link
                  @click="handleResetPassword(scope.row)"
                >
                  비밀번호 재설정
                </el-button>
                <el-button
                  v-hasPerm="'sys:user:edit'"
                  type="primary"
                  icon="edit"
                  link
                  size="small"
                  @click="handleOpenDialog(scope.row.id)"
                >
                  수정
                </el-button>
                <el-button
                  v-hasPerm="'sys:user:delete'"
                  type="danger"
                  icon="delete"
                  link
                  size="small"
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
            @pagination="fetchUserList"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 사용자 양식 -->
    <el-drawer
      v-model="dialog.visible"
      :title="dialog.title"
      append-to-body
      :size="drawerSize"
      @close="handleCloseDialog"
    >
      <el-form ref="userFormRef" :model="formData" :rules="rules" label-width="80px">
        <el-form-item label="사용자명" prop="username">
          <el-input
            v-model="formData.username"
            :readonly="!!formData.id"
            placeholder="사용자명을 입력하세요"
          />
        </el-form-item>

        <el-form-item label="닉네임" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="닉네임을 입력하세요" />
        </el-form-item>

        <el-form-item label="소속 부서" prop="deptId">
          <el-tree-select
            v-model="formData.deptId"
            placeholder="소속 부서를 선택하세요"
            :data="deptOptions"
            filterable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>

        <el-form-item label="성별" prop="gender">
          <Dict v-model="formData.gender" code="gender" />
        </el-form-item>

        <el-form-item label="역할" prop="roleIds">
          <el-select v-model="formData.roleIds" multiple placeholder="선택하세요">
            <el-option
              v-for="item in roleOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="휴대폰 번호" prop="mobile">
          <el-input v-model="formData.mobile" placeholder="휴대폰 번호를 입력하세요" maxlength="11" />
        </el-form-item>

        <el-form-item label="이메일" prop="email">
          <el-input v-model="formData.email" placeholder="이메일을 입력하세요" maxlength="50" />
        </el-form-item>

        <el-form-item label="상태" prop="status">
          <el-switch
            v-model="formData.status"
            inline-prompt
            active-text="정상"
            inactive-text="비활성"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">확인</el-button>
          <el-button @click="handleCloseDialog">취소</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 사용자 가져오기 -->
    <UserImport v-model="importDialogVisible" @import-success="handleQuery()" />
  </div>
</template>

<script setup lang="ts">
// ==================== 1. Vue 핵심 API ====================
import { computed, onMounted, reactive, ref } from "vue";
import { useDebounceFn } from "@vueuse/core";

// ==================== 2. Element Plus ====================
import { ElMessage, ElMessageBox } from "element-plus";

// ==================== 3. 타입 정의 ====================
import type { UserForm, UserPageQuery, UserPageVO } from "@/api/system/user-api";
// ==================== 4. API 서비스 ====================
import UserAPI from "@/api/system/user-api";
import DeptAPI from "@/api/system/dept-api";
import RoleAPI from "@/api/system/role-api";

// ==================== 5. 스토어 ====================
import { useAppStore } from "@/store/modules/app-store";
import { useUserStore } from "@/store";

// ==================== 6. Enums ====================
import { DeviceEnum } from "@/enums/settings/device-enum";

// ==================== 7. Composables ====================
import { useAiAction, useTableSelection } from "@/composables";

// ==================== 8. 컴포넌트 ====================
import DeptTree from "./components/DeptTree.vue";
import UserImport from "./components/UserImport.vue";

// ==================== 컴포넌트 설정 ====================
defineOptions({
  name: "SystemUser",
  inheritAttrs: false,
});

// ==================== 스토어 인스턴스 ====================
const appStore = useAppStore();
const userStore = useUserStore();

// ==================== 반응형 상태 ====================

// DOM 참조
const queryFormRef = ref();
const userFormRef = ref();

// 목록 조회 매개변수
const queryParams = reactive<UserPageQuery>({
  pageNum: 1,
  pageSize: 10,
});

// 목록 데이터
const pageData = ref<UserPageVO[]>();
const total = ref(0);
const loading = ref(false);

// 팝업 상태
const dialog = reactive({
  visible: false,
  title: "사용자 추가",
});

// 양식 데이터
const formData = reactive<UserForm>({
  status: 1,
});

// 드롭다운 옵션 데이터
const deptOptions = ref<OptionType[]>();
const roleOptions = ref<OptionType[]>();

// 가져오기 팝업
const importDialogVisible = ref(false);

// ==================== 계산된 속성 ====================

/**
 * 드로어 크기(반응형)
 */
const drawerSize = computed(() => (appStore.device === DeviceEnum.DESKTOP ? "600px" : "90%"));

// ==================== 양식 검증 규칙 ====================

const rules = reactive({
  username: [
    {
      required: true,
      message: "사용자명은 필수입니다",
      trigger: "blur",
    },
  ],
  nickname: [
    {
      required: true,
      message: "닉네임은 필수입니다",
      trigger: "blur",
    },
  ],
  deptId: [
    {
      required: true,
      message: "소속 부서는 필수입니다",
      trigger: "blur",
    },
  ],
  roleIds: [
    {
      required: true,
      message: "역할은 필수입니다",
      trigger: "blur",
    },
  ],
  email: [
    {
      pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/,
      message: "올바른 이메일 주소를 입력하세요",
      trigger: "blur",
    },
  ],
  mobile: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "올바른 휴대폰 번호를 입력하세요",
      trigger: "blur",
    },
  ],
});

// ==================== 데이터 로드 ====================

/**
 * 사용자 목록 데이터 조회
 */
async function fetchUserList(): Promise<void> {
  loading.value = true;
  try {
    const data = await UserAPI.getPage(queryParams);
    pageData.value = data.list;
    total.value = data.total;
  } catch (error) {
    ElMessage.error("사용자 목록 조회 실패");
    console.error("사용자 목록 조회 실패:", error);
  } finally {
    loading.value = false;
  }
}

// ==================== 테이블 선택 ====================
const { selectedIds, hasSelection, handleSelectionChange } = useTableSelection<UserPageVO>();

// ==================== 조회 작업 ====================

/**
 * 사용자 목록 조회
 */
function handleQuery(): Promise<void> {
  queryParams.pageNum = 1;
  return fetchUserList();
}

/**
 * 조회 조건 초기화
 */
function handleResetQuery(): void {
  queryFormRef.value.resetFields();
  queryParams.deptId = undefined;
  queryParams.createTime = undefined;
  handleQuery();
}
// ==================== 사용자 작업 ====================

/**
 * 사용자 비밀번호 초기화
 * @param row 사용자 데이터
 */
function handleResetPassword(row: UserPageVO): void {
  ElMessageBox.prompt(`사용자【${row.username}】의 새 비밀번호를 입력하세요`, "비밀번호 재설정", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    inputPattern: /.{6,}/,
    inputErrorMessage: "비밀번호는 최소 6자 이상이어야 합니다",
  })
    .then(({ value }) => {
      return UserAPI.resetPassword(row.id, value);
    })
    .then(() => {
      ElMessage.success("비밀번호 재설정 성공");
    })
    .catch((error) => {
      if (error !== "cancel") {
        ElMessage.error("비밀번호 재설정 실패");
      }
    });
}

// ==================== 팝업 작업 ====================

/**
 * 사용자 양식 팝업 열기
 * @param id 사용자ID(편집 시 전달)
 */
async function handleOpenDialog(id?: string): Promise<void> {
  dialog.visible = true;

  // 드롭다운 옵션 데이터 병렬 로드
  try {
    [roleOptions.value, deptOptions.value] = await Promise.all([
      RoleAPI.getOptions(),
      DeptAPI.getOptions(),
    ]);
  } catch (error) {
    ElMessage.error("옵션 데이터 로드 실패");
    console.error("옵션 데이터 로드 실패:", error);
  }

  // 편집: 사용자 데이터 로드
  if (id) {
    dialog.title = "사용자 수정";
    try {
      const data = await UserAPI.getFormData(id);
      Object.assign(formData, data);
    } catch (error) {
      ElMessage.error("사용자 데이터 로드 실패");
      console.error("사용자 데이터 로드 실패:", error);
    }
  } else {
    // 추가: 기본값 설정
    dialog.title = "사용자 추가";
  }
}

/**
 * 사용자 양식 팝업 닫기
 */
function handleCloseDialog(): void {
  dialog.visible = false;
  userFormRef.value.resetFields();
  userFormRef.value.clearValidate();

  // 양식 데이터 초기화
  formData.id = undefined;
  formData.status = 1;
}

/**
 * 사용자 양식 제출(디바운스)
 */
const handleSubmit = useDebounceFn(async () => {
  const valid = await userFormRef.value.validate().catch(() => false);
  if (!valid) return;

  const userId = formData.id;
  loading.value = true;

  try {
    if (userId) {
      await UserAPI.update(userId, formData);
      ElMessage.success("사용자 수정 성공");
    } else {
      await UserAPI.create(formData);
      ElMessage.success("사용자 추가 성공");
    }
    handleCloseDialog();
    handleResetQuery();
  } catch (error) {
    ElMessage.error(userId ? "사용자 수정 실패" : "사용자 추가 실패");
    console.error("사용자 양식 제출 실패:", error);
  } finally {
    loading.value = false;
  }
}, 1000);

/**
 * 사용자 삭제
 * @param id 사용자ID(단일 삭제 시 전달)
 */
function handleDelete(id?: string): void {
  const userIds = id ? id : selectedIds.value.join(",");

  if (!userIds) {
    ElMessage.warning("삭제할 항목을 선택하세요");
    return;
  }

  // 안전 검사: 현재 로그인한 사용자 삭제 방지
  const currentUserId = userStore.userInfo?.userId;
  if (currentUserId) {
    const isCurrentUserInList = id
      ? id === currentUserId
      : selectedIds.value.some((selectedId) => String(selectedId) === currentUserId);

    if (isCurrentUserInList) {
      ElMessage.error("현재 로그인한 사용자는 삭제할 수 없습니다");
      return;
    }
  }

  ElMessageBox.confirm("선택한 사용자를 삭제하시겠습니까?", "경고", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    type: "warning",
  })
    .then(async () => {
      loading.value = true;
      try {
        await UserAPI.deleteByIds(userIds);
        ElMessage.success("삭제 성공");
        handleResetQuery();
      } catch (error) {
        ElMessage.error("삭제 실패");
        console.error("사용자 삭제 실패:", error);
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 사용자가 작업 취소, 처리 불필요
    });
}

// ==================== 가져오기/내보내기 ====================

/**
 * 가져오기 팝업 열기
 */
function handleOpenImportDialog(): void {
  importDialogVisible.value = true;
}

/**
 * 사용자 목록 내보내기
 */
async function handleExport(): Promise<void> {
  try {
    const response = await UserAPI.export(queryParams);
    const fileData = response.data;
    const contentDisposition = response.headers["content-disposition"];
    const fileName = decodeURI(contentDisposition.split(";")[1].split("=")[1]);
    const fileType =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";

    // 다운로드 링크 생성
    const blob = new Blob([fileData], { type: fileType });
    const downloadUrl = window.URL.createObjectURL(blob);
    const downloadLink = document.createElement("a");
    downloadLink.href = downloadUrl;
    downloadLink.download = fileName;

    // 다운로드 실행
    document.body.appendChild(downloadLink);
    downloadLink.click();

    // 정리
    document.body.removeChild(downloadLink);
    window.URL.revokeObjectURL(downloadUrl);

    ElMessage.success("내보내기 성공");
  } catch (error) {
    ElMessage.error("내보내기 실패");
    console.error("사용자 목록 내보내기 실패:", error);
  }
}

// ==================== AI 어시스턴트 관련 ====================
useAiAction({
  actionHandlers: {
    /**
     * AI 사용자 닉네임 변경
     * 설정 객체 방식: 자동으로 확인, 실행, 피드백 처리
     */
    updateUserNickname: {
      needConfirm: true,
      callBackendApi: true, // 자동으로 백엔드 API 호출
      confirmMessage: (args: any) =>
        `AI 어시스턴트가 다음 작업을 수행합니다:<br/>
        <strong>사용자 수정:</strong> ${args.username}<br/>
        <strong>새 닉네임:</strong> ${args.nickname}<br/><br/>
        실행하시겠습니까?`,
      successMessage: (args: any) => `사용자 ${args.username}의 닉네임을 ${args.nickname}(으)로 수정했습니다`,
      execute: async () => {
        // callBackendApi=true일 때 execute는 비어있을 수 있음
        // Composable이 자동으로 백엔드 API 호출
      },
    },

    /**
     * AI 사용자 조회
     * 설정 객체 방식: 조회 작업은 확인 불필요
     */
    queryUser: {
      needConfirm: false, // 조회 작업은 확인 불필요
      successMessage: (args: any) => `검색 완료: ${args.keywords}`,
      execute: async (args: any) => {
        queryParams.keywords = args.keywords;
        await handleQuery();
      },
    },
  },
  onRefresh: fetchUserList,
  onAutoSearch: (keywords: string) => {
    queryParams.keywords = keywords;
    setTimeout(() => {
      handleQuery();
      ElMessage.success(`AI 어시스턴트가 자동으로 검색했습니다: ${keywords}`);
    }, 300);
  },
});

// ==================== 생명 주기 ====================

/**
 * 컴포넌트 마운트 시 데이터 초기화
 *
 * 주의: URL에 AI 매개변수(예: 검색 키워드)가 있으면
 * useAiAction이 nextTick에서 다시 검색을 실행합니다. 이는 예상된 동작입니다.
 */
onMounted(() => {
  handleQuery();
});
</script>
