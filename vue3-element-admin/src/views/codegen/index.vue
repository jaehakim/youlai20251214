<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true" @submit.prevent>
        <el-form-item prop="keywords" label="키워드">
          <el-input
            v-model="queryParams.keywords"
            placeholder="테이블명"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" @click="handleQuery">
            <template #icon>
              <Search />
            </template>
            검색
          </el-button>
          <el-button @click="handleResetQuery">
            <template #icon>
              <Refresh />
            </template>
            초기화
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="hover" class="table-card">
      <el-table
        ref="dataTableRef"
        v-loading="loading"
        :data="pageData"
        highlight-current-row
        border
        stripe
        class="data-table__content"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="테이블명" prop="tableName" min-width="100" />
        <el-table-column label="설명" prop="tableComment" width="150" />

        <el-table-column label="저장 엔진" align="center" prop="engine" />

        <el-table-column label="정렬 규칙" align="center" prop="tableCollation" />
        <el-table-column label="생성 시간" align="center" prop="createTime" />

        <el-table-column fixed="right" label="작업" width="200">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleOpenDialog(scope.row.tableName)"
            >
              <template #icon>
                <MagicStick />
              </template>
              코드 생성
            </el-button>

            <el-button
              v-if="scope.row.isConfigured === 1"
              type="danger"
              size="small"
              link
              @click="handleResetConfig(scope.row.tableName)"
            >
              <template #icon>
                <RefreshLeft />
              </template>
              구성 초기화
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="handleQuery"
      />
    </el-card>

    <el-drawer
      v-model="dialog.visible"
      :title="dialog.title"
      size="80%"
      @close="dialog.visible = false"
    >
      <el-steps :active="active" align-center finish-status="success" simple>
        <el-step title="기본 구성" />
        <el-step title="필드 구성" />
        <el-step title="미리 보기 생성" />
      </el-steps>

      <div class="mt-5">
        <el-form
          v-show="active == 0"
          :model="genConfigFormData"
          :label-width="100"
          :rules="genConfigFormRules"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item label="테이블명" prop="tableName">
                <el-input v-model="genConfigFormData.tableName" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="비즈니스명" prop="businessName">
                <el-input v-model="genConfigFormData.businessName" placeholder="사용자" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="주 패키지명" prop="packageName">
                <el-input v-model="genConfigFormData.packageName" placeholder="com.youlai.boot" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="모듈명" prop="moduleName">
                <el-input v-model="genConfigFormData.moduleName" placeholder="system" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="엔티티명" prop="entityName">
                <el-input v-model="genConfigFormData.entityName" placeholder="User" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="저자">
                <el-input v-model="genConfigFormData.author" placeholder="youlai" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item label="테이블 접두사 제거">
                <el-input v-model="genConfigFormData.removeTablePrefix" placeholder="예: sys_" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="페이지 유형">
                <el-radio-group v-model="genConfigFormData.pageType">
                  <el-radio-button label="classic">일반</el-radio-button>
                  <el-radio-button label="curd">캡슐화(CURD)</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="12">
              <el-form-item>
                <template #label>
                  <div class="flex-y-between">
                    <span>상위 메뉴</span>
                    <el-tooltip effect="dark">
                      <template #content>
                        상위 메뉴를 선택하면, 코드 생성 후 자동으로 해당 메뉴가 생성됩니다.
                        <br />
                        주의 1: 메뉴 생성 후 역할에 권한을 할당해야 하며, 그렇지 않으면 메뉴가 표시되지 않습니다.
                        <br />
                        주의 2: 데모 환경에서는 기본적으로 메뉴가 생성되지 않습니다. 생성하려면 로컬에 데이터베이스를 배포하세요.
                      </template>
                      <el-icon class="cursor-pointer">
                        <QuestionFilled />
                      </el-icon>
                    </el-tooltip>
                  </div>
                </template>

                <el-tree-select
                  v-model="genConfigFormData.parentMenuId"
                  placeholder="상위 메뉴 선택"
                  :data="menuOptions"
                  check-strictly
                  :render-after-expand="false"
                  filterable
                  clearable
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <div v-show="active == 1" class="elTableCustom">
          <div class="mb-2 flex-y-center gap-2">
            <el-tag size="small" type="info">일괄 설정</el-tag>
            <el-space size="small">
              <el-dropdown>
                <el-button size="small" type="primary" plain>조회</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="bulkSet('isShowInQuery', 1)">전체 선택</el-dropdown-item>
                    <el-dropdown-item @click="bulkSet('isShowInQuery', 0)">전체 선택 해제</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-dropdown>
                <el-button size="small" type="success" plain>목록</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="bulkSet('isShowInList', 1)">전체 선택</el-dropdown-item>
                    <el-dropdown-item @click="bulkSet('isShowInList', 0)">전체 선택 해제</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-dropdown>
                <el-button size="small" type="warning" plain>양식</el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="bulkSet('isShowInForm', 1)">전체 선택</el-dropdown-item>
                    <el-dropdown-item @click="bulkSet('isShowInForm', 0)">전체 선택 해제</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-space>
          </div>
          <el-table
            v-loading="loading"
            row-key="id"
            :element-loading-text="loadingText"
            highlight--currentrow
            :data="genConfigFormData.fieldConfigs"
          >
            <el-table-column width="55" align="center">
              <el-icon class="cursor-move sortable-handle">
                <Rank />
              </el-icon>
            </el-table-column>

            <el-table-column label="컬럼명" width="110">
              <template #default="scope">
                {{ scope.row.columnName }}
              </template>
            </el-table-column>

            <el-table-column label="열유형" width="80">
              <template #default="scope">
                {{ scope.row.columnType }}
              </template>
            </el-table-column>
            <el-table-column label="필드명" width="120">
              <template #default="scope">
                <el-input v-model="scope.row.fieldName" />
              </template>
            </el-table-column>
            <el-table-column label="필드 유형" width="80">
              <template #default="scope">
                {{ scope.row.fieldType }}
              </template>
            </el-table-column>

            <el-table-column label="필드 설명" min-width="100">
              <template #default="scope">
                <el-input v-model="scope.row.fieldComment" />
              </template>
            </el-table-column>

            <el-table-column label="최대 길이" width="80">
              <template #default="scope">
                <el-input v-model="scope.row.maxLength" />
              </template>
            </el-table-column>

            <el-table-column width="70" label="조회">
              <template #default="scope">
                <el-checkbox v-model="scope.row.isShowInQuery" :true-value="1" :false-value="0" />
              </template>
            </el-table-column>

            <el-table-column width="70" label="목록">
              <template #default="scope">
                <el-checkbox v-model="scope.row.isShowInList" :true-value="1" :false-value="0" />
              </template>
            </el-table-column>

            <el-table-column width="70" label="양식">
              <template #default="scope">
                <el-checkbox v-model="scope.row.isShowInForm" :true-value="1" :false-value="0" />
              </template>
            </el-table-column>

            <el-table-column label="필수" width="70">
              <template #default="scope">
                <el-checkbox
                  v-if="scope.row.isShowInForm == 1"
                  v-model="scope.row.isRequired"
                  :true-value="1"
                  :false-value="0"
                />
                <span v-else>-</span>
              </template>
            </el-table-column>

            <el-table-column label="조회 방식" min-width="120">
              <template #default="scope">
                <el-select
                  v-if="scope.row.isShowInQuery === 1"
                  v-model="scope.row.queryType"
                  placeholder="선택해주세요"
                >
                  <el-option
                    v-for="(item, key) in queryTypeOptions"
                    :key="key"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <span v-else>-</span>
              </template>
            </el-table-column>

            <el-table-column label="폼 유형" min-width="120">
              <template #default="scope">
                <el-select
                  v-if="scope.row.isShowInQuery === 1 || scope.row.isShowInForm === 1"
                  v-model="scope.row.formType"
                  placeholder="선택해주세요"
                >
                  <el-option
                    v-for="(item, key) in formTypeOptions"
                    :key="key"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <span v-else>-</span>
              </template>
            </el-table-column>

            <el-table-column label="사전 유형" min-width="100">
              <template #default="scope">
                <el-select
                  v-if="scope.row.formType === FormTypeEnum.SELECT.value"
                  v-model="scope.row.dictType"
                  placeholder="선택해주세요"
                  clearable
                >
                  <el-option
                    v-for="item in dictOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-row v-show="active == 2">
          <el-col :span="24" class="mb-2">
            <div class="flex-y-center gap-3">
              <span class="text-sm color-#909399">미리보기 범위</span>
              <el-radio-group v-model="previewScope" size="small">
                <el-radio-button label="all">모두</el-radio-button>
                <el-radio-button label="frontend">프론트엔드</el-radio-button>
                <el-radio-button label="backend">백엔드</el-radio-button>
              </el-radio-group>
              <span class="ml-3 text-sm color-#909399">유형</span>
              <el-checkbox-group v-model="previewTypes" size="small">
                <el-checkbox-button v-for="t in previewTypeOptions" :key="t" :label="t">
                  {{ t }}
                </el-checkbox-button>
              </el-checkbox-group>
            </div>
          </el-col>
          <el-col :span="6">
            <el-scrollbar max-height="72vh">
              <el-tree
                :data="filteredTreeData"
                default-expand-all
                highlight-current
                @node-click="handleFileTreeNodeClick"
              >
                <template #default="{ data }">
                  <div :class="`i-svg:${getFileTreeNodeIcon(data.label)}`" />
                  <span class="ml-1">{{ data.label }}</span>
                </template>
              </el-tree>
            </el-scrollbar>
          </el-col>
          <el-col :span="18">
            <el-scrollbar max-height="72vh">
              <div class="absolute z-36 right-5 top-2">
                <el-link type="primary" @click="handleCopyCode">
                  <el-icon>
                    <CopyDocument />
                  </el-icon>
                  원클릭 복사
                </el-link>
              </div>

              <Codemirror
                ref="cmRef"
                v-model:value="code"
                :options="cmOptions"
                border
                :readonly="true"
                height="100%"
                width="100%"
              />
            </el-scrollbar>
          </el-col>
        </el-row>
      </div>

      <template #footer>
        <el-button v-if="active !== 0" type="success" @click="handlePrevClick">
          <el-icon>
            <Back />
          </el-icon>
          {{ prevBtnText }}
        </el-button>
        <el-button type="primary" @click="handleNextClick">
          {{ nextBtnText }}
          <el-icon v-if="active !== 2">
            <Right />
          </el-icon>
          <el-icon v-else>
            <Download />
          </el-icon>
        </el-button>
        <el-button
          v-if="active === 2"
          :disabled="!supportsFSAccess"
          type="primary"
          plain
          @click="openWriteDialog"
        >
          <template #icon>
            <el-icon><FolderOpened /></el-icon>
          </template>
          로컬에 저장
        </el-button>
      </template>
    </el-drawer>
    <!-- 로컬에 저장 다이얼로그 -->
    <el-dialog v-model="writeDialog.visible" title="로컬에 저장" width="820px" :close-on-click-modal="false">
      <div class="space-y-3">
        <el-alert
          v-if="!supportsFSAccess"
          title="현재 브라우저는 File System Access API를 지원하지 않습니다. Chrome/Edge 최신 버전 사용을 권장합니다."
          type="warning"
          show-icon
          :closable="false"
        />

        <el-form :label-width="110">
          <el-form-item label="프론트엔드 루트 디렉토리">
            <div class="flex-y-center gap-2">
              <el-input v-model="frontendDirPath" placeholder="프론트엔드 루트 디렉토리를 선택해주세요" readonly />
              <el-button :disabled="!supportsFSAccess" @click="pickFrontendDir">선택</el-button>
            </div>
          </el-form-item>
          <el-form-item label="백엔드 루트 디렉토리">
            <div class="flex-y-center gap-2">
              <el-input v-model="backendDirPath" placeholder="백엔드 루트 디렉토리를 선택해주세요" readonly />
              <el-button :disabled="!supportsFSAccess" @click="pickBackendDir">선택</el-button>
            </div>
          </el-form-item>
          <el-form-item label="쓰기 범위">
            <el-radio-group v-model="writeScope">
              <el-radio-button label="all">모두</el-radio-button>
              <el-radio-button label="frontend">프론트엔드만</el-radio-button>
              <el-radio-button label="backend">백엔드만</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="덮어쓰기 정책">
            <el-radio-group v-model="overwriteMode">
              <el-radio-button label="overwrite">덮어쓰기</el-radio-button>
              <el-radio-button label="skip">기존 항목 건너뛰기</el-radio-button>
              <el-radio-button label="ifChanged">변경된 경우만 덮어쓰기</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-form>

        <div v-if="writeProgress.total > 0" class="mt-2">
          <el-progress :text-inside="true" :stroke-width="16" :percentage="writeProgress.percent" />
          <div class="mt-1 text-sm color-#909399">
            {{ writeProgress.done }}/{{ writeProgress.total }} {{ writeProgress.current }}
          </div>
        </div>
      </div>

      <template #footer>
        <el-button @click="writeDialog.visible = false">취소</el-button>
        <el-button
          type="primary"
          :disabled="!canWriteToLocal || writeRunning"
          @click="confirmWrite"
        >
          쓰기
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Codegen",
});

import Sortable from "sortablejs";
import "codemirror/mode/javascript/javascript.js";
import Codemirror from "codemirror-editor-vue3";
import type { CmComponentRef } from "codemirror-editor-vue3";
import type { EditorConfiguration } from "codemirror";

import { FormTypeEnum } from "@/enums/codegen/form-enum";
import { QueryTypeEnum } from "@/enums/codegen/query-enum";

import GeneratorAPI, {
  TablePageVO,
  GenConfigForm,
  TablePageQuery,
  FieldConfig,
} from "@/api/codegen-api";
import { ElLoading } from "element-plus";

import DictAPI from "@/api/system/dict-api";
import MenuAPI from "@/api/system/menu-api";

interface TreeNode {
  label: string;
  content?: string;
  children?: TreeNode[];
}
const treeData = ref<TreeNode[]>([]);
const previewScope = ref<"all" | "frontend" | "backend">("all");
const previewTypeOptions = ["ts", "vue", "java", "xml"];
const previewTypes = ref<string[]>([...previewTypeOptions]);

const filteredTreeData = computed<TreeNode[]>(() => {
  if (!treeData.value.length) return [];
  // 원본 트리 기반 scope/types로 리프 노드 필터링
  const match = (label: string, parentPath: string[]): boolean => {
    // scope 필터: 경로에 따라 초기 판단
    const pathStr = parentPath.join("/");
    if (previewScope.value !== "all") {
      const isBackend = /(^|\/)src\/main\//.test(pathStr) || /(^|\/)java\//.test(pathStr);
      const scopeOfNode = isBackend ? "backend" : "frontend";
      if (scopeOfNode !== previewScope.value) return false;
    }
    // 유형 필터: 확장자에 따라 필터링
    const ext = label.split(".").pop() || "";
    return previewTypes.value.includes(ext);
  };

  const cloneFilter = (node: TreeNode, parents: string[] = []): TreeNode | null => {
    if (!node.children || node.children.length === 0) {
      return match(node.label, parents) ? { ...node } : null;
    }
    const nextParents = [...parents, node.label];
    const children = (node.children || [])
      .map((c) => cloneFilter(c, nextParents))
      .filter(Boolean) as TreeNode[];
    if (!children.length) return null;
    return { label: node.label, children };
  };

  const filtered = treeData.value.map((n) => cloneFilter(n)).filter(Boolean) as TreeNode[];
  return filtered;
});

const queryFormRef = ref();
const queryParams = reactive<TablePageQuery>({
  pageNum: 1,
  pageSize: 10,
});

const loading = ref(false);
const loadingText = ref("loading...");

const pageData = ref<TablePageVO[]>([]);
const total = ref(0);

const formTypeOptions: Record<string, OptionType> = FormTypeEnum;
const queryTypeOptions: Record<string, OptionType> = QueryTypeEnum;
const dictOptions = ref<OptionType[]>();
const menuOptions = ref<OptionType[]>([]);
const genConfigFormData = ref<GenConfigForm>({
  fieldConfigs: [],
  pageType: "classic",
});

const genConfigFormRules = {
  tableName: [{ required: true, message: "테이블명을 입력해주세요", trigger: "blur" }],
  businessName: [{ required: true, message: "비즈니스명을 입력해주세요", trigger: "blur" }],
  packageName: [{ required: true, message: "주 패키지명을 입력해주세요", trigger: "blur" }],
  moduleName: [{ required: true, message: "모듈명을 입력해주세요", trigger: "blur" }],
  entityName: [{ required: true, message: "엔티티명을 입력해주세요", trigger: "blur" }],
};

const dialog = reactive({
  visible: false,
  title: "",
});

// 페이지 스타일에 따른 백엔드 지속성 필드 genConfigFormData.ui
watch(
  () => genConfigFormData.value.removeTablePrefix,
  (prefix) => {
    const table = genConfigFormData.value.tableName;
    if (!table) return;
    const p = prefix || "";
    const base = table.startsWith(p) ? table.slice(p.length) : table;
    // 언더스코어로 구분된 테이블명을 파스칼 케이스로 변환
    const camel = base
      .split("_")
      .filter(Boolean)
      .map((s) => s.charAt(0).toUpperCase() + s.slice(1))
      .join("");
    genConfigFormData.value.entityName = camel;
  }
);

const { copy, copied } = useClipboard();
const code = ref();
const cmRef = ref<CmComponentRef>();
const cmOptions: EditorConfiguration = {
  mode: "text/javascript",
};

const prevBtnText = ref("");
const nextBtnText = ref("다음 단계, 필드 설정");
const active = ref(0);
const currentTableName = ref("");
const sortFlag = ref<object>();

// ================= 로컬 디스크 쓰기(선택 사항) =================
const supportsFSAccess = typeof (window as any).showDirectoryPicker === "function";
const outputMode = ref<"zip" | "local">("zip");
const frontendDirHandle = ref<any>(null);
const backendDirHandle = ref<any>(null);
const frontendDirName = ref("");
const backendDirName = ref("");
// 미리보기의 원본 파일 목록(디스크 쓰기용)
const lastPreviewFiles = ref<{ path: string; fileName: string; content: string }[]>([]);
const needFrontend = computed(() =>
  lastPreviewFiles.value.some((f) => resolveRootForPath(f.path) === "frontend")
);
const needBackend = computed(() =>
  lastPreviewFiles.value.some((f) => resolveRootForPath(f.path) === "backend")
);
const canWriteToLocal = computed(() => {
  if (!lastPreviewFiles.value.length) return false;
  const frontOk = needFrontend.value ? !!frontendDirHandle.value : true;
  const backOk = needBackend.value ? !!backendDirHandle.value : true;
  return frontOk && backOk;
});

// 조회 전체 선택 여부
const isCheckAllQuery = ref(false);
// 목록 전체 선택 여부
const isCheckAllList = ref(false);
// 양식 전체 선택 여부
const isCheckAllForm = ref(false);

watch(active, (val) => {
  if (val === 0) {
    nextBtnText.value = "다음 단계, 필드 설정";
  } else if (val === 1) {
    prevBtnText.value = "이전 단계, 기본 설정";
    nextBtnText.value = "다음 단계, 생성 확인";
  } else if (val === 2) {
    prevBtnText.value = "이전 단계, 필드 설정";
    nextBtnText.value = "코드 다운로드";
  }
});

watch(copied, () => {
  if (copied.value) {
    ElMessage.success("복사 성공");
  }
});

watch(
  () => genConfigFormData.value.fieldConfigs as FieldConfig[],
  (newVal: FieldConfig[]) => {
    newVal.forEach((fieldConfig) => {
      if (
        fieldConfig.fieldType &&
        fieldConfig.fieldType.includes("Date") &&
        fieldConfig.isShowInQuery === 1
      ) {
        fieldConfig.queryType = QueryTypeEnum.BETWEEN.value as number;
      }
    });
  },
  { deep: true, immediate: true }
);

const initSort = () => {
  if (sortFlag.value) {
    return;
  }
  const table = document.querySelector(".elTableCustom .el-table__body-wrapper tbody");
  sortFlag.value = Sortable.create(<HTMLElement>table, {
    group: "shared",
    animation: 150,
    ghostClass: "sortable-ghost", // 드래그 스타일
    handle: ".sortable-handle", // 드래그 영역
    easing: "cubic-bezier(1, 0, 0, 1)",

    // 드래그 종료 이벤트
    onEnd: (item: any) => {
      setNodeSort(item.oldIndex, item.newIndex);
    },
  });
};

const setNodeSort = (oldIndex: number, newIndex: number) => {
  // arr을 사용하여 테이블 데이터 그룹 복사
  const arr = Object.assign([], genConfigFormData.value.fieldConfigs);
  const currentRow = arr.splice(oldIndex, 1)[0];
  arr.splice(newIndex, 0, currentRow);
  arr.forEach((item: FieldConfig, index) => {
    item.fieldSort = index + 1;
  });
  genConfigFormData.value.fieldConfigs = [];
  nextTick(async () => {
    genConfigFormData.value.fieldConfigs = arr;
  });
};

/** 이전 단계 */
function handlePrevClick() {
  if (active.value === 2) {
    // 여기서 데이터를 다시 조회합니다. 처음 코드 생성 후 이전 단계를 클릭하면 데이터가 재구성되지 않고, 다시 다음 단계를 클릭하면 데이터가 다시 삽입되어 인덱스 중복 오류가 발생할 수 있습니다.
    genConfigFormData.value = {
      fieldConfigs: [],
    };
    nextTick(() => {
      loading.value = true;
      GeneratorAPI.getGenConfig(currentTableName.value)
        .then((data) => {
          genConfigFormData.value = data;
        })
        .finally(() => {
          loading.value = false;
        });
    });
    initSort();
  }
  if (active.value-- <= 0) active.value = 0;
}

/** 다음 단계 */
function handleNextClick() {
  if (active.value === 0) {
    // 여기서 기본 설정 검증 필요
    const { tableName, packageName, businessName, moduleName, entityName } =
      genConfigFormData.value;
    if (!tableName || !packageName || !businessName || !moduleName || !entityName) {
      ElMessage.error("테이블명, 비즈니스명, 패키지명, 모듈명, 엔티티명은 비워둘 수 없습니다");
      return;
    }
    initSort();
  }
  if (active.value === 1) {
    // 생성 설정 저장
    const tableName = genConfigFormData.value.tableName;
    if (!tableName) {
      ElMessage.error("테이블명은 비워둘 수 없습니다");
      return;
    }
    loading.value = true;
    loadingText.value = "코드 생성 중입니다. 잠시만 기다려주세요...";
    GeneratorAPI.saveGenConfig(tableName, genConfigFormData.value)
      .then(() => {
        handlePreview(tableName);
      })
      .then(() => {
        if (active.value++ >= 2) active.value = 2;
      })
      .finally(() => {
        loading.value = false;
        loadingText.value = "loading...";
      });
  } else {
    if (active.value++ >= 2) {
      active.value = 2;
    }
    if (active.value === 2) {
      const tableName = genConfigFormData.value.tableName;
      if (!tableName) {
        ElMessage.error("테이블명은 비워둘 수 없습니다");
        return;
      }
      if (outputMode.value === "zip" || !supportsFSAccess) {
        GeneratorAPI.download(tableName, (genConfigFormData.value.pageType as any) || "classic");
      }
    }
  }
}

/** 검색 */
function handleQuery() {
  loading.value = true;
  GeneratorAPI.getTablePage(queryParams)
    .then((data) => {
      pageData.value = data.list;
      total.value = data.total;
    })
    .finally(() => {
      loading.value = false;
    });
}

/** 조회 초기화 */
function handleResetQuery() {
  queryFormRef.value.resetFields();
  queryParams.pageNum = 1;
  handleQuery();
}

/** 팝업 열기 */
async function handleOpenDialog(tableName: string) {
  dialog.visible = true;
  active.value = 0;

  menuOptions.value = await MenuAPI.getOptions(true);

  currentTableName.value = tableName;
  // 사전 데이터 조회
  DictAPI.getList().then((data) => {
    dictOptions.value = data;
    loading.value = true;
    GeneratorAPI.getGenConfig(tableName)
      .then((data) => {
        dialog.title = `${tableName} 코드 생성`;
        genConfigFormData.value = data;

        checkAllSelected("isShowInQuery", isCheckAllQuery);
        checkAllSelected("isShowInList", isCheckAllList);
        checkAllSelected("isShowInForm", isCheckAllForm);

        // 이미 설정을 거쳤다면 미리보기 페이지로 직접 이동
        if (genConfigFormData.value.id) {
          active.value = 2;
          handlePreview(tableName);
        } else {
          // 설정을 거치지 않았다면 기본 설정 페이지로 이동
          active.value = 0;
        }
      })
      .finally(() => {
        loading.value = false;
      });
  });
}

/** 설정 초기화 */
function handleResetConfig(tableName: string) {
  ElMessageBox.confirm("설정을 초기화하시겠습니까?", "알림", {
    type: "warning",
  }).then(() => {
    GeneratorAPI.resetGenConfig(tableName).then(() => {
      ElMessage.success("초기화 성공");
      handleQuery();
    });
  });
}

type FieldConfigKey = "isShowInQuery" | "isShowInList" | "isShowInForm";

/** 전체 선택 */
// 단일 열 전체 선택 스위치가 제거되고 상단 "일괄 설정" 입구로 변경되었습니다. 메서드를 유지하면 미사용 경고가 발생하므로 삭제합니다.

function bulkSet(key: FieldConfigKey, value: 0 | 1) {
  const list = genConfigFormData.value?.fieldConfigs || [];
  list.forEach((row: any) => {
    // 기존 필드만 변경하여 반응형 유지
    row[key] = value;
  });
}

const checkAllSelected = (key: keyof FieldConfig, isCheckAllRef: any) => {
  const fieldConfigs = genConfigFormData.value?.fieldConfigs || [];
  isCheckAllRef.value = fieldConfigs.every((row: FieldConfig) => row[key] === 1);
};

/** 생성 미리보기 조회 */
function handlePreview(tableName: string) {
  treeData.value = [];
  GeneratorAPI.getPreviewData(tableName, (genConfigFormData.value.pageType as any) || "classic")
    .then((data) => {
      dialog.title = `코드 생성 ${tableName}`;
      // 트리 구조 완성 코드 그룹화
      const tree = buildTree(data);
      // 디스크 쓰기용 원본 데이터 캐시
      lastPreviewFiles.value = data || [];
      // 루트 노드 "프론트/백엔드 코드"를 제거하고 해당 children을 1차 디렉토리로 직접 표시
      treeData.value = tree?.children ? [...tree.children] : [];

      // 기본값으로 첫 번째 리프 노드를 선택하고 code 값 설정
      const firstLeafNode = findFirstLeafNode(tree);
      if (firstLeafNode) {
        code.value = firstLeafNode.content || "";
      }
    })
    .catch(() => {
      active.value = 0;
    });
}

/**
 * 트리 구조 재귀적 빌드
 *
 * @param data - 데이터 배열
 * @returns 트리 구조 루트 노드
 */
function buildTree(data: { path: string; fileName: string; content: string }[]): TreeNode {
  // 루트 노드 동적 조회
  const root: TreeNode = { label: "프론트/백엔드 코드", children: [] };

  data.forEach((item) => {
    // 경로를 배열로 분리
    const separator = item.path.includes("/") ? "/" : "\\";
    const parts = item.path.split(separator);

    // 특수 경로 정의
    const specialPaths = [
      "src" + separator + "main",
      "java",
      genConfigFormData.value.backendAppName,
      genConfigFormData.value.frontendAppName,
      (genConfigFormData.value.packageName + "." + genConfigFormData.value.moduleName).replace(
        /\./g,
        separator
      ),
    ];

    // 경로의 특수 부분을 확인하고 병합
    const mergedParts: string[] = [];
    let buffer: string[] = [];

    parts.forEach((part) => {
      buffer.push(part);
      const currentPath = buffer.join(separator);
      if (specialPaths.includes(currentPath)) {
        mergedParts.push(currentPath);
        buffer = [];
      }
    });

    // mergedParts 경로의 구분자 \를 /로 치환
    mergedParts.forEach((part, index) => {
      mergedParts[index] = part.replace(/\\/g, "/");
    });

    if (buffer.length > 0) {
      mergedParts.push(...buffer);
    }

    let currentNode = root;

    mergedParts.forEach((part) => {
      // 현재 부분의 자식 노드를 찾거나 생성
      let node = currentNode.children?.find((child) => child.label === part);
      if (!node) {
        node = { label: part, children: [] };
        currentNode.children?.push(node);
      }
      currentNode = node;
    });

    // 파일 노드 추가
    currentNode.children?.push({
      label: item.fileName,
      content: item?.content,
    });
  });

  return root;
}

/**
 * 첫 번째 리프 노드 재귀적 찾기
 * @param node - 트리 노드
 * @returns 첫 번째 리프 노드
 */
function findFirstLeafNode(node: TreeNode): TreeNode | null {
  if (!node.children || node.children.length === 0) {
    return node;
  }
  for (const child of node.children) {
    const leafNode = findFirstLeafNode(child);
    if (leafNode) {
      return leafNode;
    }
  }
  return null;
}

/** 파일 트리 노드 클릭 */
function handleFileTreeNodeClick(data: TreeNode) {
  if (!data.children || data.children.length === 0) {
    code.value = data.content || "";
  }
}

/** 파일 트리 노드 아이콘 조회 */
function getFileTreeNodeIcon(label: string) {
  if (label.endsWith(".java")) {
    return "java";
  }
  if (label.endsWith(".html")) {
    return "html";
  }
  if (label.endsWith(".vue")) {
    return "vue";
  }
  if (label.endsWith(".ts")) {
    return "typescript";
  }
  if (label.endsWith(".xml")) {
    return "xml";
  }
  return "file";
}

/** 원클릭 복사 */
const handleCopyCode = () => {
  if (code.value) {
    copy(code.value);
  }
};

// =============== 디렉토리 선택 및 쓰기 ===============
const pickFrontendDir = async () => {
  try {
    // @ts-ignore
    frontendDirHandle.value = await (window as any).showDirectoryPicker();
    frontendDirName.value = frontendDirHandle.value?.name || "";
    ElMessage.success("프론트엔드 디렉토리 선택 성공");
  } catch {
    // 사용자 취소 또는 브라우저 미지원
  }
};

const pickBackendDir = async () => {
  try {
    // @ts-ignore
    backendDirHandle.value = await (window as any).showDirectoryPicker();
    backendDirName.value = backendDirHandle.value?.name || "";
    ElMessage.success("백엔드 디렉토리 선택 성공");
  } catch {
    // 사용자 취소 또는 브라우저 미지원
  }
};

async function ensureDir(root: any, path: string[], force = true) {
  let current = root;
  for (const segment of path) {
    try {
      // @ts-ignore
      current = await current.getDirectoryHandle(segment, { create: true });
    } catch (err: any) {
      // 동일 이름 파일이 디렉토리 생성을 차단하면 강제 삭제 후 재생성 시도
      if (force && err?.name === "TypeMismatchError") {
        try {
          // @ts-ignore
          await current.removeEntry(segment, { recursive: true });
          // @ts-ignore
          current = await current.getDirectoryHandle(segment, { create: true });
        } catch {
          throw err;
        }
      } else {
        throw err;
      }
    }
  }
  return current;
}

async function writeFile(dirHandle: any, filePath: string, content: string) {
  const normalized = filePath.replace(/\\/g, "/");
  const parts = normalized.split("/").filter(Boolean);
  const fileName = parts.pop()!;
  const folderSegments = parts;
  const targetDir = await ensureDir(dirHandle, folderSegments, true);
  // @ts-ignore
  let fileHandle;
  try {
    // @ts-ignore
    fileHandle = await targetDir.getFileHandle(fileName, { create: true });
  } catch (err: any) {
    if (err?.name === "TypeMismatchError") {
      // 동일 이름 디렉토리가 존재하면 삭제 후 파일 재생성 시도
      try {
        // @ts-ignore
        await targetDir.removeEntry(fileName, { recursive: true });
        // @ts-ignore
        fileHandle = await targetDir.getFileHandle(fileName, { create: true });
      } catch {
        throw err;
      }
    } else {
      throw err;
    }
  }
  // @ts-ignore
  const writable = await fileHandle.createWritable();
  await writable.write(content ?? "");
  await writable.close();
}

async function pathExists(dirHandle: any, filePath: string): Promise<boolean> {
  try {
    const normalized = filePath.replace(/\\/g, "/");
    const parts = normalized.split("/").filter(Boolean);
    const fileName = parts.pop()!;
    const targetDir = await ensureDir(dirHandle, parts, false);
    // @ts-ignore
    await targetDir.getFileHandle(fileName, { create: false });
    return true;
  } catch {
    return false;
  }
}

async function isSameFile(dirHandle: any, filePath: string, content: string): Promise<boolean> {
  try {
    const normalized = filePath.replace(/\\/g, "/");
    const parts = normalized.split("/").filter(Boolean);
    const fileName = parts.pop()!;
    const targetDir = await ensureDir(dirHandle, parts, false);
    // @ts-ignore
    const fileHandle = await targetDir.getFileHandle(fileName, { create: false });
    // @ts-ignore
    const file = await fileHandle.getFile();
    const text = await file.text();
    return text === (content ?? "");
  } catch {
    return false;
  }
}

// 템플릿의 path를 프론트엔드/백엔드 루트 디렉토리로 매핑
function resolveRootForPath(p: string) {
  const normalized = p.replace(/\\/g, "/");
  const frontApp = genConfigFormData.value.frontendAppName;
  const backApp = genConfigFormData.value.backendAppName;
  if (
    (backApp && normalized.startsWith(`${backApp}/`)) ||
    normalized.includes("/src/main/") ||
    normalized.startsWith("src/main/") ||
    normalized.startsWith("java/")
  ) {
    return "backend" as const;
  }
  if ((frontApp && normalized.startsWith(`${frontApp}/`)) || normalized.startsWith("src/")) {
    return "frontend" as const;
  }
  // 기본값 프론트엔드
  return "frontend" as const;
}

function stripProjectRoot(p: string) {
  const normalized = p.replace(/\\/g, "/");
  const frontApp = genConfigFormData.value.frontendAppName;
  const backApp = genConfigFormData.value.backendAppName;
  let rel = normalized;
  if (frontApp && normalized.startsWith(`${frontApp}/`)) {
    rel = normalized.slice(frontApp.length + 1);
  } else if (backApp && normalized.startsWith(`${backApp}/`)) {
    rel = normalized.slice(backApp.length + 1);
  } else {
    const idx = normalized.indexOf("/src/");
    if (idx > -1) {
      rel = normalized.slice(idx + 1); // 보유 'src/...'
    } else if (normalized.startsWith("src/")) {
      rel = normalized;
    }
  }
  return rel;
}

const writeGeneratedCode = async () => {
  if (!supportsFSAccess) {
    ElMessage.warning("현재 브라우저는 로컬 쓰기를 지원하지 않습니다. ZIP 다운로드를 선택해주세요.");
    return;
  }
  if (
    (needFrontend.value && !frontendDirHandle.value) ||
    (needBackend.value && !backendDirHandle.value)
  ) {
    ElMessage.warning("먼저 필요한 프론트엔드/백엔드 디렉토리를 선택해주세요");
    return;
  }
  if (!lastPreviewFiles.value.length) {
    ElMessage.warning("먼저 미리보기를 생성해주세요");
    return;
  }
  loading.value = true;
  const loadingSvc = ElLoading.service({
    lock: true,
    text: "코드 쓰기 중...",
  });
  writeRunning.value = true;
  let frontCount = 0;
  let backCount = 0;
  const failed: string[] = [];
  const files = lastPreviewFiles.value.filter((f) => {
    const root = resolveRootForPath(f.path);
    return writeScope.value === "all" || root === writeScope.value;
  });
  writeProgress.total = files.length;
  writeProgress.done = 0;
  writeProgress.percent = 0;
  writeProgress.current = "";

  const concurrency = 4;
  const queue = files.slice();
  const workers: Promise<void>[] = [];

  async function worker() {
    while (queue.length) {
      const item = queue.shift()!;
      try {
        const root = resolveRootForPath(item.path);
        const relativePath = stripProjectRoot(`${item.path}/${item.fileName}`);
        writeProgress.current = relativePath;
        if (overwriteMode.value === "ifChanged") {
          // 간단한 차이 확인: 기존 파일 내용과 쓸 내용이 동일하면 건너뛰기
          // @ts-ignore
          const targetRoot = root === "frontend" ? frontendDirHandle.value : backendDirHandle.value;
          const existsSame = await isSameFile(targetRoot, relativePath, item.content || "");
          if (existsSame) {
            // 성공으로 처리하되 쓰기하지 않음
            writeProgress.done++;
            writeProgress.percent = Math.round((writeProgress.done / writeProgress.total) * 100);
            continue;
          }
        }
        if (overwriteMode.value === "skip") {
          // @ts-ignore
          const targetRoot = root === "frontend" ? frontendDirHandle.value : backendDirHandle.value;
          const exists = await pathExists(targetRoot, relativePath);
          if (exists) {
            writeProgress.done++;
            writeProgress.percent = Math.round((writeProgress.done / writeProgress.total) * 100);
            continue;
          }
        }
        if (root === "frontend") {
          await writeFile(frontendDirHandle.value, relativePath, item.content || "");
          frontCount++;
        } else {
          await writeFile(backendDirHandle.value, relativePath, item.content || "");
          backCount++;
        }
      } catch (err) {
        console.error("쓰기 실패:", item.path, err);
        failed.push(item.path);
      } finally {
        writeProgress.done++;
        writeProgress.percent = Math.round((writeProgress.done / writeProgress.total) * 100);
      }
    }
  }

  for (let i = 0; i < concurrency; i++) {
    workers.push(worker());
  }
  await Promise.all(workers);
  loading.value = false;
  loadingSvc.close();
  writeRunning.value = false;
  if (failed.length) {
    ElMessage.warning(
      `일부 파일 쓰기 실패: ${failed.length}개, 성공: 프론트엔드 ${frontCount}개/백엔드 ${backCount}개. 콘솔을 열어 상세 정보를 확인하세요.`
    );
  } else {
    ElMessage.success(`쓰기 완료: 프론트엔드 ${frontCount}개 파일, 백엔드 ${backCount}개 파일`);
  }
};

const writeDialog = reactive({ visible: false });
const frontendDirPath = ref("");
const backendDirPath = ref("");
const writeScope = ref<"all" | "frontend" | "backend">("all");
const overwriteMode = ref<"overwrite" | "skip" | "ifChanged">("overwrite");
const writeProgress = reactive({ total: 0, done: 0, percent: 0, current: "" });
const writeRunning = ref(false);

// 알림 텍스트가 취소되어 표시되지 않으며, 논리적 의미가 크지 않으므로 제거합니다.

function openWriteDialog() {
  writeDialog.visible = true;
}

// 경로 동기 표시
watch(frontendDirName, (v) => (frontendDirPath.value = v));
watch(backendDirName, (v) => (backendDirPath.value = v));

async function confirmWrite() {
  await writeGeneratedCode();
  writeDialog.visible = false;
}

/** 컴포넌트 마운트 후 실행 */
onMounted(() => {
  handleQuery();
  cmRef.value?.destroy();
});
</script>
