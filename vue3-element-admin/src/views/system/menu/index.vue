<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="키워드" prop="keywords">
          <el-input
            v-model="queryParams.keywords"
            placeholder="메뉴명"
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
            v-hasPerm="['sys:menu:add']"
            type="success"
            icon="plus"
            @click="handleOpenDialog('0')"
          >
            추가
          </el-button>
        </div>
      </div>

      <el-table
        ref="dataTableRef"
        v-loading="loading"
        row-key="id"
        :data="menuTableData"
        :tree-props="{
          children: 'children',
          hasChildren: 'hasChildren',
        }"
        class="data-table__content"
        @row-click="handleRowClick"
      >
        <el-table-column label="메뉴명" min-width="200">
          <template #default="scope">
            <template v-if="scope.row.icon && scope.row.icon.startsWith('el-icon')">
              <el-icon style="vertical-align: -0.15em">
                <component :is="scope.row.icon.replace('el-icon-', '')" />
              </el-icon>
            </template>
            <template v-else-if="scope.row.icon">
              <div :class="`i-svg:${scope.row.icon}`" />
            </template>
            {{ scope.row.name }}
          </template>
        </el-table-column>

        <el-table-column label="유형" align="center" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.type === MenuTypeEnum.CATALOG" type="warning">디렉터리</el-tag>
            <el-tag v-if="scope.row.type === MenuTypeEnum.MENU" type="success">메뉴</el-tag>
            <el-tag v-if="scope.row.type === MenuTypeEnum.BUTTON" type="danger">버튼</el-tag>
            <el-tag v-if="scope.row.type === MenuTypeEnum.EXTLINK" type="info">외부링크</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="라우트명" align="left" width="150" prop="routeName" />
        <el-table-column label="라우트 경로" align="left" width="150" prop="routePath" />
        <el-table-column label="컴포넌트 경로" align="left" width="250" prop="component" />
        <el-table-column label="권한 식별자" align="center" width="200" prop="perm" />
        <el-table-column label="상태" align="center" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.visible === 1" type="success">표시</el-tag>
            <el-tag v-else type="info">숨김</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="정렬" align="center" width="80" prop="sort" />
        <el-table-column fixed="right" align="center" label="작업" width="220">
          <template #default="scope">
            <el-button
              v-if="scope.row.type == MenuTypeEnum.CATALOG || scope.row.type == MenuTypeEnum.MENU"
              v-hasPerm="['sys:menu:add']"
              type="primary"
              link
              size="small"
              icon="plus"
              @click.stop="handleOpenDialog(scope.row.id)"
            >
              추가
            </el-button>

            <el-button
              v-hasPerm="['sys:menu:edit']"
              type="primary"
              link
              size="small"
              icon="edit"
              @click.stop="handleOpenDialog(undefined, scope.row.id)"
            >
              수정
            </el-button>
            <el-button
              v-hasPerm="['sys:menu:delete']"
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

    <el-drawer
      v-model="dialog.visible"
      :title="dialog.title"
      :size="drawerSize"
      @close="handleCloseDialog"
    >
      <el-form ref="menuFormRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="상위 메뉴" prop="parentId">
          <el-tree-select
            v-model="formData.parentId"
            placeholder="상위 메뉴 선택"
            :data="menuOptions"
            filterable
            check-strictly
            :render-after-expand="false"
          />
        </el-form-item>

        <el-form-item label="메뉴명" prop="name">
          <el-input v-model="formData.name" placeholder="메뉴명을 입력하세요" />
        </el-form-item>

        <el-form-item label="메뉴 유형" prop="type">
          <el-radio-group v-model="formData.type" @change="handleMenuTypeChange">
            <el-radio :value="MenuTypeEnum.CATALOG">디렉터리</el-radio>
            <el-radio :value="MenuTypeEnum.MENU">메뉴</el-radio>
            <el-radio :value="MenuTypeEnum.BUTTON">버튼</el-radio>
            <el-radio :value="MenuTypeEnum.EXTLINK">외부링크</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="formData.type == MenuTypeEnum.EXTLINK" label="외부링크 주소" prop="path">
          <el-input v-model="formData.routePath" placeholder="외부링크 전체 경로를 입력하세요" />
        </el-form-item>

        <el-form-item v-if="formData.type == MenuTypeEnum.MENU" prop="routeName">
          <template #label>
            <div class="flex-y-center">
              라우트명
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  캐싱을 활성화하려면 페이지의 defineOptions에 있는 name이 여기와 일치해야 하며, 카멜케이스 사용을 권장합니다.
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-input v-model="formData.routeName" placeholder="User" />
        </el-form-item>

        <el-form-item
          v-if="formData.type == MenuTypeEnum.CATALOG || formData.type == MenuTypeEnum.MENU"
          prop="routePath"
        >
          <template #label>
            <div class="flex-y-center">
              라우트 경로
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  애플리케이션의 다른 페이지에 해당하는 URL 경로를 정의합니다. 디렉터리는 /로 시작해야 하며, 메뉴 항목은 필요하지 않습니다. 예: 시스템 관리 디렉터리
                  /system, 시스템 관리 하위의 사용자 관리 메뉴 user.
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>
          <el-input
            v-if="formData.type == MenuTypeEnum.CATALOG"
            v-model="formData.routePath"
            placeholder="system"
          />
          <el-input v-else v-model="formData.routePath" placeholder="user" />
        </el-form-item>

        <el-form-item v-if="formData.type == MenuTypeEnum.MENU" prop="component">
          <template #label>
            <div class="flex-y-center">
              컴포넌트 경로
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  컴포넌트 페이지의 전체 경로, src/views/를 기준으로 합니다. 예: system/user/index, 접미사 .vue는 생략합니다
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>

          <el-input v-model="formData.component" placeholder="system/user/index" style="width: 95%">
            <template v-if="formData.type == MenuTypeEnum.MENU" #prepend>src/views/</template>
            <template v-if="formData.type == MenuTypeEnum.MENU" #append>.vue</template>
          </el-input>
        </el-form-item>

        <el-form-item v-if="formData.type == MenuTypeEnum.MENU">
          <template #label>
            <div class="flex-y-center">
              라우트 파라미터
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  컴포넌트 페이지에서 `useRoute().query.파라미터명`을 사용하여 라우트 파라미터 값을 가져옵니다.
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>

          <div v-if="!formData.params || formData.params.length === 0">
            <el-button type="success" plain @click="formData.params = [{ key: '', value: '' }]">
              라우트 파라미터 추가
            </el-button>
          </div>

          <div v-else>
            <div v-for="(item, index) in formData.params" :key="index">
              <el-input v-model="item.key" placeholder="파라미터명" style="width: 100px" />

              <span class="mx-1">=</span>

              <el-input v-model="item.value" placeholder="파라미터 값" style="width: 100px" />

              <el-icon
                v-if="formData.params.indexOf(item) === formData.params.length - 1"
                class="ml-2 cursor-pointer color-[var(--el-color-success)]"
                style="vertical-align: -0.15em"
                @click="formData.params.push({ key: '', value: '' })"
              >
                <CirclePlusFilled />
              </el-icon>
              <el-icon
                class="ml-2 cursor-pointer color-[var(--el-color-danger)]"
                style="vertical-align: -0.15em"
                @click="formData.params.splice(formData.params.indexOf(item), 1)"
              >
                <DeleteFilled />
              </el-icon>
            </div>
          </div>
        </el-form-item>

        <el-form-item v-if="formData.type !== MenuTypeEnum.BUTTON" prop="visible" label="표시 상태">
          <el-radio-group v-model="formData.visible">
            <el-radio :value="1">표시</el-radio>
            <el-radio :value="0">숨김</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="formData.type === MenuTypeEnum.CATALOG || formData.type === MenuTypeEnum.MENU"
        >
          <template #label>
            <div class="flex-y-center">
              항상 표시
              <el-tooltip placement="bottom" effect="light">
                <template #content>
                  "예"를 선택하면 디렉터리나 메뉴 아래에 하위 노드가 하나만 있어도 상위 노드가 표시됩니다.
                  <br />
                  "아니오"를 선택하면 디렉터리나 메뉴 아래에 하위 노드가 하나만 있을 경우 해당 하위 노드만 표시되고 상위 노드는 숨겨집니다.
                  <br />
                  리프 노드인 경우 "아니오"를 선택하세요.
                </template>
                <el-icon class="ml-1 cursor-pointer">
                  <QuestionFilled />
                </el-icon>
              </el-tooltip>
            </div>
          </template>

          <el-radio-group v-model="formData.alwaysShow">
            <el-radio :value="1">예</el-radio>
            <el-radio :value="0">아니오</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="formData.type === MenuTypeEnum.MENU" label="페이지 캐싱">
          <el-radio-group v-model="formData.keepAlive">
            <el-radio :value="1">활성화</el-radio>
            <el-radio :value="0">비활성화</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="정렬" prop="sort">
          <el-input-number
            v-model="formData.sort"
            style="width: 100px"
            controls-position="right"
            :min="0"
          />
        </el-form-item>

        <!-- 권한 식별자 -->
        <el-form-item v-if="formData.type == MenuTypeEnum.BUTTON" label="권한 식별자" prop="perm">
          <el-input v-model="formData.perm" placeholder="sys:user:add" />
        </el-form-item>

        <el-form-item v-if="formData.type !== MenuTypeEnum.BUTTON" label="아이콘" prop="icon">
          <!-- 아이콘 선택기 -->
          <icon-select v-model="formData.icon" />
        </el-form-item>

        <el-form-item v-if="formData.type == MenuTypeEnum.CATALOG" label="리다이렉트 라우트">
          <el-input v-model="formData.redirect" placeholder="리다이렉트 라우트" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmit">확인</el-button>
          <el-button @click="handleCloseDialog">취소</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { useApp스토어 } from "@/store/modules/app-store";
import { DeviceEnum } from "@/enums/settings/device-enum";

import MenuAPI, { MenuQuery, MenuForm, MenuVO } from "@/api/system/menu-api";
import { MenuTypeEnum } from "@/enums/system/menu-enum";

defineOptions({
  name: "SysMenu",
  inheritAttrs: false,
});

const app스토어 = useApp스토어();

const queryFormRef = ref();
const menuFormRef = ref();

const loading = ref(false);
const dialog = reactive({
  title: "메뉴 추가",
  visible: false,
});

const drawerSize = computed(() => (app스토어.device === DeviceEnum.DESKTOP ? "600px" : "90%"));
// 쿼리 파라미터
const queryParams = reactive<MenuQuery>({});
// 메뉴 테이블 데이터
const menuTableData = ref<MenuVO[]>([]);
// 최상위 메뉴 드롭다운 옵션
const menuOptions = ref<OptionType[]>([]);
// 초기 메뉴 폼 데이터
const initialMenuFormData = ref<MenuForm>({
  id: undefined,
  parentId: "0",
  visible: 1,
  sort: 1,
  type: MenuTypeEnum.MENU, // 기본 메뉴
  alwaysShow: 0,
  keepAlive: 1,
  params: [],
});
// 메뉴 폼 데이터
const formData = ref({ ...initialMenuFormData.value });
// 폼 검증 규칙
const rules = reactive({
  parentId: [{ required: true, message: "상위 메뉴를 선택하세요", trigger: "blur" }],
  name: [{ required: true, message: "메뉴명을 입력하세요", trigger: "blur" }],
  type: [{ required: true, message: "메뉴 유형을 선택하세요", trigger: "blur" }],
  routeName: [{ required: true, message: "라우트명을 입력하세요", trigger: "blur" }],
  routePath: [{ required: true, message: "라우트 경로를 입력하세요", trigger: "blur" }],
  component: [{ required: true, message: "컴포넌트 경로를 입력하세요", trigger: "blur" }],
  visible: [{ required: true, message: "표시 상태를 선택하세요", trigger: "change" }],
});

// 선택된 테이블 행의 메뉴 ID
const selectedMenuId = ref<string | undefined>();

// 메뉴 조회
function handleQuery() {
  loading.value = true;
  MenuAPI.getList(queryParams)
    .then((data) => {
      menuTableData.value = data;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 쿼리 초기화
function handleResetQuery() {
  queryFormRef.value.resetFields();
  handleQuery();
}

// 행 클릭 이벤트
function handleRowClick(row: MenuVO) {
  selectedMenuId.value = row.id;
}

/**
 * 폼 대화상자 열기
 *
 * @param parentId 상위 메뉴 ID
 * @param menuId 메뉴 ID
 */
function handleOpenDialog(parentId?: string, menuId?: string) {
  MenuAPI.getOptions(true)
    .then((data) => {
      menuOptions.value = [{ value: "0", label: "최상위 메뉴", children: data }];
    })
    .then(() => {
      dialog.visible = true;
      if (menuId) {
        dialog.title = "메뉴 수정";
        MenuAPI.getFormData(menuId).then((data) => {
          initialMenuFormData.value = { ...data };
          formData.value = data;
        });
      } else {
        dialog.title = "메뉴 추가";
        formData.value.parentId = parentId?.toString();
      }
    });
}

// 메뉴 유형 변경
function handleMenuTypeChange() {
  // 메뉴 유형이 변경된 경우
  if (formData.value.type !== initialMenuFormData.value.type) {
    if (formData.value.type === MenuTypeEnum.MENU) {
      // 디렉터리에서 메뉴로 전환할 때 컴포넌트 경로를 비움
      if (initialMenuFormData.value.type === MenuTypeEnum.CATALOG) {
        formData.value.component = "";
      } else {
        // 기타 경우, 기존 컴포넌트 경로를 유지
        formData.value.routePath = initialMenuFormData.value.routePath;
        formData.value.component = initialMenuFormData.value.component;
      }
    }
  }
}

/**
 * 폼 제출
 */
function handleSubmit() {
  menuFormRef.value.validate((isValid: boolean) => {
    if (isValid) {
      const menuId = formData.value.id;
      if (menuId) {
        //수정 시 상위 메뉴는 현재 메뉴가 될 수 없음
        if (formData.value.parentId == menuId) {
          ElMessage.error("상위 메뉴는 현재 메뉴가 될 수 없습니다");
          return;
        }
        MenuAPI.update(menuId, formData.value).then(() => {
          ElMessage.success("수정 성공");
          handleCloseDialog();
          handleQuery();
        });
      } else {
        MenuAPI.create(formData.value).then(() => {
          ElMessage.success("추가 성공");
          handleCloseDialog();
          handleQuery();
        });
      }
    }
  });
}

// 메뉴 삭제
function handleDelete(menuId: string) {
  if (!menuId) {
    ElMessage.warning("삭제할 항목을 선택하세요");
    return false;
  }

  ElMessageBox.confirm("선택한 데이터 항목을 삭제하시겠습니까?", "경고", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    type: "warning",
  }).then(
    () => {
      loading.value = true;
      MenuAPI.deleteById(menuId)
        .then(() => {
          ElMessage.success("삭제 성공");
          handleQuery();
        })
        .finally(() => {
          loading.value = false;
        });
    },
    () => {
      ElMessage.info("삭제가 취소되었습니다");
    }
  );
}

function resetForm() {
  menuFormRef.value.resetFields();
  menuFormRef.value.clearValidate();
  formData.value = {
    id: undefined,
    parentId: "0",
    visible: 1,
    sort: 1,
    type: MenuTypeEnum.MENU, // 기본 메뉴
    alwaysShow: 0,
    keepAlive: 1,
    params: [],
  };
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
