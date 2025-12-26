<template>
  <div
    class="rounded bg-[var(--el-bg-color)] border border-[var(--el-border-color)] p-5 h-full md:flex flex-1 flex-col md:overflow-auto"
  >
    <!-- 테이블 도구 모음 -->
    <div class="flex flex-col md:flex-row justify-between gap-y-2.5 mb-2.5">
      <!-- 왼쪽 도구 모음 -->
      <div class="toolbar-left flex gap-y-2.5 gap-x-2 md:gap-x-3 flex-wrap">
        <template v-for="(btn, index) in toolbarLeftBtn" :key="index">
          <el-button
            v-hasPerm="btn.perm ?? '*:*:*'"
            v-bind="btn.attrs"
            :disabled="btn.name === 'delete' && removeIds.length === 0"
            @click="handleToolbar(btn.name)"
          >
            {{ btn.text }}
          </el-button>
        </template>
      </div>
      <!-- 오른쪽 도구 모음 -->
      <div class="toolbar-right flex gap-y-2.5 gap-x-2 md:gap-x-3 flex-wrap">
        <template v-for="(btn, index) in toolbarRightBtn" :key="index">
          <el-popover v-if="btn.name === 'filter'" placement="bottom" trigger="click">
            <template #reference>
              <el-button v-bind="btn.attrs"></el-button>
            </template>
            <el-scrollbar max-height="350px">
              <template v-for="col in cols" :key="col.prop">
                <el-checkbox v-if="col.prop" v-model="col.show" :label="col.label" />
              </template>
            </el-scrollbar>
          </el-popover>
          <el-button
            v-else
            v-hasPerm="btn.perm ?? '*:*:*'"
            v-bind="btn.attrs"
            @click="handleToolbar(btn.name)"
          ></el-button>
        </template>
      </div>
    </div>

    <!-- 목록 -->
    <el-table
      ref="tableRef"
      v-loading="loading"
      v-bind="contentConfig.table"
      :data="pageData"
      :row-key="pk"
      class="flex-1"
      @selection-change="handleSelectionChange"
      @filter-change="handleFilterChange"
    >
      <template v-for="col in cols" :key="col.prop">
        <el-table-column v-if="col.show" v-bind="col">
          <template #default="scope">
            <!-- 이미지 표시 -->
            <template v-if="col.templet === 'image'">
              <template v-if="col.prop">
                <template v-if="Array.isArray(scope.row[col.prop])">
                  <template v-for="(item, index) in scope.row[col.prop]" :key="item">
                    <el-image
                      :src="item"
                      :preview-src-list="scope.row[col.prop]"
                      :initial-index="index"
                      :preview-teleported="true"
                      :style="`width: ${col.imageWidth ?? 40}px; height: ${col.imageHeight ?? 40}px`"
                    />
                  </template>
                </template>
                <template v-else>
                  <el-image
                    :src="scope.row[col.prop]"
                    :preview-src-list="[scope.row[col.prop]]"
                    :preview-teleported="true"
                    :style="`width: ${col.imageWidth ?? 40}px; height: ${col.imageHeight ?? 40}px`"
                  />
                </template>
              </template>
            </template>
            <!-- 행의 selectList 속성을 기반으로 해당 목록 값 반환 -->
            <template v-else-if="col.templet === 'list'">
              <template v-if="col.prop">
                {{ (col.selectList ?? {})[scope.row[col.prop]] }}
              </template>
            </template>
            <!-- 링크 형식화 및 표시 -->
            <template v-else-if="col.templet === 'url'">
              <template v-if="col.prop">
                <el-link type="primary" :href="scope.row[col.prop]" target="_blank">
                  {{ scope.row[col.prop] }}
                </el-link>
              </template>
            </template>
            <!-- 전환 컴포넌트 생성 -->
            <template v-else-if="col.templet === 'switch'">
              <template v-if="col.prop">
                <!-- pageData.length>0: el-switch 컴포넌트가 테이블 초기화 시 변경 이벤트를 한 번 트리거하는 문제 해결 -->
                <el-switch
                  v-model="scope.row[col.prop]"
                  :active-value="col.activeValue ?? 1"
                  :inactive-value="col.inactiveValue ?? 0"
                  :inline-prompt="true"
                  :active-text="col.activeText ?? ''"
                  :inactive-text="col.inactiveText ?? ''"
                  :validate-event="false"
                  :disabled="!hasButtonPerm(col.prop)"
                  @change="
                    pageData.length > 0 && handleModify(col.prop, scope.row[col.prop], scope.row)
                  "
                />
              </template>
            </template>
            <!-- 입력 상자 컴포넌트 생성 -->
            <template v-else-if="col.templet === 'input'">
              <template v-if="col.prop">
                <el-input
                  v-model="scope.row[col.prop]"
                  :type="col.inputType ?? 'text'"
                  :disabled="!hasButtonPerm(col.prop)"
                  @blur="handleModify(col.prop, scope.row[col.prop], scope.row)"
                />
              </template>
            </template>
            <!-- 가격으로 포맷 -->
            <template v-else-if="col.templet === 'price'">
              <template v-if="col.prop">
                {{ `${col.priceFormat ?? "￥"}${scope.row[col.prop]}` }}
              </template>
            </template>
            <!-- 백분율로 포맷 -->
            <template v-else-if="col.templet === 'percent'">
              <template v-if="col.prop">{{ scope.row[col.prop] }}%</template>
            </template>
            <!-- 아이콘 표시 -->
            <template v-else-if="col.templet === 'icon'">
              <template v-if="col.prop">
                <template v-if="scope.row[col.prop].startsWith('el-icon-')">
                  <el-icon>
                    <component :is="scope.row[col.prop].replace('el-icon-', '')" />
                  </el-icon>
                </template>
                <template v-else>
                  <div class="i-svg:{{ scope.row[col.prop] }}" />
                </template>
              </template>
            </template>
            <!-- 시간 형식화 -->
            <template v-else-if="col.templet === 'date'">
              <template v-if="col.prop">
                {{
                  scope.row[col.prop]
                    ? useDateFormat(scope.row[col.prop], col.dateFormat ?? "YYYY-MM-DD HH:mm:ss")
                        .value
                    : ""
                }}
              </template>
            </template>
            <!-- 열 작업 모음 -->
            <template v-else-if="col.templet === 'tool'">
              <template v-for="(btn, index) in tableToolbarBtn" :key="index">
                <el-button
                  v-if="btn.render === undefined || btn.render(scope.row)"
                  v-hasPerm="btn.perm ?? '*:*:*'"
                  v-bind="btn.attrs"
                  @click="
                    handleOperate({
                      name: btn.name,
                      row: scope.row,
                      column: scope.column,
                      $index: scope.$index,
                    })
                  "
                >
                  {{ btn.text }}
                </el-button>
              </template>
            </template>
            <!-- 사용자 정의 -->
            <template v-else-if="col.templet === 'custom'">
              <slot :name="col.slotName ?? col.prop" :prop="col.prop" v-bind="scope" />
            </template>
          </template>
        </el-table-column>
      </template>
    </el-table>

    <!-- 페이지 매김 -->
    <div v-if="showPagination" class="mt-4">
      <el-scrollbar :class="['h-8!', { 'flex-x-end': contentConfig?.pagePosition === 'right' }]">
        <el-pagination
          v-bind="pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </el-scrollbar>
    </div>

    <!-- 내보내기 모달 -->
    <el-dialog
      v-model="exportsModalVisible"
      :align-center="true"
      title="데이터 내보내기"
      width="600px"
      style="padding-right: 0"
      @close="handleCloseExportsModal"
    >
      <!-- 스크롤 -->
      <el-scrollbar max-height="60vh">
        <!-- 폼 -->
        <el-form
          ref="exportsFormRef"
          style="padding-right: var(--el-dialog-padding-primary)"
          :model="exportsFormData"
          :rules="exportsFormRules"
        >
          <el-form-item label="파일명" prop="filename">
            <el-input v-model="exportsFormData.filename" clearable />
          </el-form-item>
          <el-form-item label="워크시트명" prop="sheetname">
            <el-input v-model="exportsFormData.sheetname" clearable />
          </el-form-item>
          <el-form-item label="데이터 원본" prop="origin">
            <el-select v-model="exportsFormData.origin">
              <el-option label="현재 데이터 (현재 페이지의 데이터)" :value="ExportsOriginEnum.CURRENT" />
              <el-option
                label="선택된 데이터 (모든 선택된 데이터)"
                :value="ExportsOriginEnum.SELECTED"
                :disabled="selectionData.length <= 0"
              />
              <el-option
                label="전체 데이터 (모든 페이지의 데이터)"
                :value="ExportsOriginEnum.REMOTE"
                :disabled="contentConfig.exports액션 === undefined"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="필드" prop="fields">
            <el-checkbox-group v-model="exportsFormData.fields">
              <template v-for="col in cols" :key="col.prop">
                <el-checkbox v-if="col.prop" :value="col.prop" :label="col.label" />
              </template>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </el-scrollbar>
      <!-- 모달 하단 작업 버튼 -->
      <template #footer>
        <div style="padding-right: var(--el-dialog-padding-primary)">
          <el-button type="primary" @click="handleExportsSubmit">확 인</el-button>
          <el-button @click="handleCloseExportsModal">취 소</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 가져오기 모달 -->
    <el-dialog
      v-model="importModalVisible"
      :align-center="true"
      title="데이터 가져오기"
      width="600px"
      style="padding-right: 0"
      @close="handleCloseImportModal"
    >
      <!-- 스크롤 -->
      <el-scrollbar max-height="60vh">
        <!-- 폼 -->
        <el-form
          ref="importFormRef"
          style="padding-right: var(--el-dialog-padding-primary)"
          :model="importFormData"
          :rules="importFormRules"
        >
          <el-form-item label="파일명" prop="files">
            <el-upload
              ref="uploadRef"
              v-model:file-list="importFormData.files"
              class="w-full"
              accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
              :drag="true"
              :limit="1"
              :auto-upload="false"
              :on-exceed="handleFileExceed"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                <span>파일을 여기에 끌어다 놓거나,</span>
                <em>클릭하여 업로드</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  *.xlsx / *.xls
                  <el-link
                    v-if="contentConfig.importTemplate"
                    type="primary"
                    icon="download"
                    underline="never"
                    @click="handleDownloadTemplate"
                  >
                    템플릿 다운로드
                  </el-link>
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form>
      </el-scrollbar>
      <!-- 모달 하단 작업 버튼 -->
      <template #footer>
        <div style="padding-right: var(--el-dialog-padding-primary)">
          <el-button
            type="primary"
            :disabled="importFormData.files.length === 0"
            @click="handleImportSubmit"
          >
            확 인
          </el-button>
          <el-button @click="handleCloseImportModal">취 소</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { hasPerm } from "@/utils/auth";
import { useDateFormat, useThrottleFn } from "@vueuse/core";
import {
  genFileId,
  type FormInstance,
  type FormRules,
  type UploadInstance,
  type UploadRawFile,
  type UploadUserFile,
  type TableInstance,
} from "element-plus";
import ExcelJS from "exceljs";
import { reactive, ref, computed } from "vue";
import type { IContentConfig, IObject, IOperateData } from "./types";
import type { IToolsButton } from "./types";

// 수신 속성 정의
const props = defineProps<{ contentConfig: IContentConfig }>();
// 사용자 정의 이벤트 정의
const emit = defineEmits<{
  addClick: [];
  exportClick: [];
  searchClick: [];
  toolbarClick: [name: string];
  editClick: [row: IObject];
  filterChange: [data: IObject];
  operateClick: [data: IOperateData];
}>();

// 테이블 도구 모음 버튼 구성
const config = computed(() => props.contentConfig);
const buttonConfig = reactive<Record<string, IObject>>({
  add: { text: "추가", attrs: { icon: "plus", type: "success" }, perm: "add" },
  delete: { text: "삭제", attrs: { icon: "delete", type: "danger" }, perm: "delete" },
  import: { text: "가져오기", attrs: { icon: "upload", type: "" }, perm: "import" },
  export: { text: "내보내기", attrs: { icon: "download", type: "" }, perm: "export" },
  refresh: { text: "새로고침", attrs: { icon: "refresh", type: "" }, perm: "*:*:*" },
  filter: { text: "열 필터링", attrs: { icon: "operation", type: "" }, perm: "*:*:*" },
  search: { text: "검색", attrs: { icon: "search", type: "" }, perm: "search" },
  imports: { text: "일괄 가져오기", attrs: { icon: "upload", type: "" }, perm: "imports" },
  exports: { text: "일괄 내보내기", attrs: { icon: "download", type: "" }, perm: "exports" },
  view: { text: "조회", attrs: { icon: "view", type: "primary" }, perm: "view" },
  edit: { text: "편집", attrs: { icon: "edit", type: "primary" }, perm: "edit" },
});

// 기본 키
const pk = props.contentConfig.pk ?? "id";
// 권한 이름 접두사
const authPrefix = computed(() => props.contentConfig.permPrefix);

// 버튼 권한 식별자 가져오기
function getButtonPerm(action: string): string | null {
  // action이 이미 완전한 경로(콜론 포함)를 포함하면 직접 사용
  if (action.includes(":")) {
    return action;
  }
  // 그렇지 않으면 권한 접두사 조합 사용
  return authPrefix.value ? `${authPrefix.value}:${action}` : null;
}

// 권한 여부 확인
function hasButtonPerm(action: string): boolean {
  const perm = getButtonPerm(action);
  // 권한 식별자가 설정되지 않으면 기본적으로 권한 있음
  if (!perm) return true;
  return hasPerm(perm);
}

// 도구 모음 버튼 생성
function createToolbar(toolbar: Array<string | IToolsButton>, attr = {}) {
  return toolbar.map((item) => {
    const isString = typeof item === "string";
    return {
      name: isString ? item : item?.name || "",
      text: isString ? buttonConfig[item].text : item?.text,
      attrs: {
        ...attr,
        ...(isString ? buttonConfig[item].attrs : item?.attrs),
      },
      render: isString ? undefined : (item?.render ?? undefined),
      perm: isString
        ? getButtonPerm(buttonConfig[item].perm)
        : item?.perm
          ? getButtonPerm(item.perm as string)
          : "*:*:*",
    };
  });
}

// 왼쪽 도구 모음 버튼
const toolbarLeftBtn = computed(() => {
  if (!config.value.toolbar || config.value.toolbar.length === 0) return [];
  return createToolbar(config.value.toolbar, {});
});

// 오른쪽 도구 모음 버튼
const toolbarRightBtn = computed(() => {
  if (!config.value.defaultToolbar || config.value.defaultToolbar.length === 0) return [];
  return createToolbar(config.value.defaultToolbar, { circle: true });
});

// 테이블 작업 도구 모음
const tableToolbar = config.value.cols[config.value.cols.length - 1].operat ?? ["edit", "delete"];
const tableToolbarBtn = createToolbar(tableToolbar, { link: true, size: "small" });

// 테이블 열
const cols = ref(
  props.contentConfig.cols.map((col) => {
    if (col.initFn) {
      col.initFn(col);
    }
    if (col.show === undefined) {
      col.show = true;
    }
    if (col.prop !== undefined && col.columnKey === undefined && col["column-key"] === undefined) {
      col.columnKey = col.prop;
    }
    if (
      col.type === "selection" &&
      col.reserveSelection === undefined &&
      col["reserve-selection"] === undefined
    ) {
      // 테이블 row-key와 함께 페이지 간 다중 선택 구현
      col.reserveSelection = true;
    }
    return col;
  })
);
// 로드 상태
const loading = ref(false);
// 목록 데이터
const pageData = ref<IObject[]>([]);
// 페이지 매김 표시
const showPagination = props.contentConfig.pagination !== false;
// 페이지 매김 구성
const defaultPagination = {
  background: true,
  layout: "total, sizes, prev, pager, next, jumper",
  pageSize: 20,
  pageSizes: [10, 20, 30, 50],
  total: 0,
  currentPage: 1,
};
const pagination = reactive(
  typeof props.contentConfig.pagination === "object"
    ? { ...defaultPagination, ...props.contentConfig.pagination }
    : defaultPagination
);
// 페이지네이션 관련 요청 파라미터
const request = props.contentConfig.request ?? {
  pageName: "pageNum",
  limitName: "pageSize",
};

const tableRef = ref<TableInstance>();

// 행 선택
const selectionData = ref<IObject[]>([]);
// ID 집합 삭제 - 일괄 삭제에 사용
const removeIds = ref<(number | string)[]>([]);
function handleSelectionChange(selection: any[]) {
  selectionData.value = selection;
  removeIds.value = selection.map((item) => item[pk]);
}

// 행 선택 가져오기
function getSelectionData() {
  return selectionData.value;
}

// 새로고침
function handleRefresh(isRestart = false) {
  fetchPageData(lastFormData, isRestart);
}

// 삭제
function handleDelete(id?: number | string) {
  const ids = [id || removeIds.value].join(",");
  if (!ids) {
    ElMessage.warning("삭제할 항목을 선택하세요");
    return;
  }

  ElMessageBox.confirm("삭제하시겠습니까?", "경고", {
    confirmButtonText: "확인",
    cancelButtonText: "취소",
    type: "warning",
  })
    .then(function () {
      if (props.contentConfig.delete액션) {
        props.contentConfig
          .delete액션(ids)
          .then(() => {
            ElMessage.success("삭제 성공");
            removeIds.value = [];
            //선택 항목 비우기
            tableRef.value?.clearSelection();
            handleRefresh(true);
          })
          .catch(() => {});
      } else {
        ElMessage.error("delete액션이 구성되지 않았습니다");
      }
    })
    .catch(() => {});
}

// 내보내기 폼
const fields: string[] = [];
cols.value.forEach((item) => {
  if (item.prop !== undefined) {
    fields.push(item.prop);
  }
});
const enum ExportsOriginEnum {
  CURRENT = "current",
  SELECTED = "selected",
  REMOTE = "remote",
}
const exportsModalVisible = ref(false);
const exportsFormRef = ref<FormInstance>();
const exportsFormData = reactive({
  filename: "",
  sheetname: "",
  fields,
  origin: ExportsOriginEnum.CURRENT,
});
const exportsFormRules: FormRules = {
  fields: [{ required: true, message: "필드를 선택하세요" }],
  origin: [{ required: true, message: "데이터 원본을 선택하세요" }],
};
// 내보내기 모달 열기
function handleOpenExportsModal() {
  exportsModalVisible.value = true;
}
// 내보내기 확인
const handleExportsSubmit = useThrottleFn(() => {
  exportsFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      handleExports();
      handleCloseExportsModal();
    }
  });
}, 3000);
// 내보내기 모달 닫기
function handleCloseExportsModal() {
  exportsModalVisible.value = false;
  exportsFormRef.value?.resetFields();
  nextTick(() => {
    exportsFormRef.value?.clearValidate();
  });
}
// 내보내기
function handleExports() {
  const filename = exportsFormData.filename
    ? exportsFormData.filename
    : props.contentConfig.permPrefix || "export";
  const sheetname = exportsFormData.sheetname ? exportsFormData.sheetname : "sheet";
  const workbook = new ExcelJS.Workbook();
  const worksheet = workbook.addWorksheet(sheetname);
  const columns: Partial<ExcelJS.Column>[] = [];
  cols.value.forEach((col) => {
    if (col.label && col.prop && exportsFormData.fields.includes(col.prop)) {
      columns.push({ header: col.label, key: col.prop });
    }
  });
  worksheet.columns = columns;
  if (exportsFormData.origin === ExportsOriginEnum.REMOTE) {
    if (props.contentConfig.exports액션) {
      props.contentConfig.exports액션(lastFormData).then((res) => {
        worksheet.addRows(res);
        workbook.xlsx
          .writeBuffer()
          .then((buffer) => {
            saveXlsx(buffer, filename as string);
          })
          .catch((error) => console.log(error));
      });
    } else {
      ElMessage.error("exports액션이 구성되지 않았습니다");
    }
  } else {
    worksheet.addRows(
      exportsFormData.origin === ExportsOriginEnum.SELECTED ? selectionData.value : pageData.value
    );
    workbook.xlsx
      .writeBuffer()
      .then((buffer) => {
        saveXlsx(buffer, filename as string);
      })
      .catch((error) => console.log(error));
  }
}

// 가져오기 폼
let isFileImport = false;
const uploadRef = ref<UploadInstance>();
const importModalVisible = ref(false);
const importFormRef = ref<FormInstance>();
const importFormData = reactive<{
  files: UploadUserFile[];
}>({
  files: [],
});
const importFormRules: FormRules = {
  files: [{ required: true, message: "파일을 선택하세요" }],
};
// 가져오기 모달 열기
function handleOpenImportModal(isFile: boolean = false) {
  importModalVisible.value = true;
  isFileImport = isFile;
}
// 이전 파일 덮어쓰기
function handleFileExceed(files: File[]) {
  uploadRef.value!.clearFiles();
  const file = files[0] as UploadRawFile;
  file.uid = genFileId();
  uploadRef.value!.handleStart(file);
}
// 가져오기 템플릿 다운로드
function handleDownloadTemplate() {
  const importTemplate = props.contentConfig.importTemplate;
  if (typeof importTemplate === "string") {
    window.open(importTemplate);
  } else if (typeof importTemplate === "function") {
    importTemplate().then((response) => {
      const fileData = response.data;
      const fileName = decodeURI(
        response.headers["content-disposition"].split(";")[1].split("=")[1]
      );
      saveXlsx(fileData, fileName);
    });
  } else {
    ElMessage.error("importTemplate이 구성되지 않았습니다");
  }
}
// 가져오기 확인
const handleImportSubmit = useThrottleFn(() => {
  importFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      if (isFileImport) {
        handleImport();
      } else {
        handleImports();
      }
    }
  });
}, 3000);
// 가져오기 모달 닫기
function handleCloseImportModal() {
  importModalVisible.value = false;
  importFormRef.value?.resetFields();
  nextTick(() => {
    importFormRef.value?.clearValidate();
  });
}
// 파일 가져오기
function handleImport() {
  const import액션 = props.contentConfig.import액션;
  if (import액션 === undefined) {
    ElMessage.error("import액션이 구성되지 않았습니다");
    return;
  }
  import액션(importFormData.files[0].raw as File).then(() => {
    ElMessage.success("데이터 가져오기 성공");
    handleCloseImportModal();
    handleRefresh(true);
  });
}
// 가져오기
function handleImports() {
  const imports액션 = props.contentConfig.imports액션;
  if (imports액션 === undefined) {
    ElMessage.error("imports액션이 구성되지 않았습니다");
    return;
  }
  // 선택한 파일 가져오기
  const file = importFormData.files[0].raw as File;
  // Workbook 인스턴스 생성
  const workbook = new ExcelJS.Workbook();
  // FileReader 객체를 사용하여 파일 내용 읽기
  const fileReader = new FileReader();
  // 바이너리 문자열 형태로 파일 로드
  fileReader.readAsArrayBuffer(file);
  fileReader.onload = (ev) => {
    if (ev.target !== null && ev.target.result !== null) {
      const result = ev.target.result as ArrayBuffer;
      // 버퍼에서 데이터 로드 및 구문 분석
      workbook.xlsx
        .load(result)
        .then((workbook) => {
          // 구문 분석된 데이터
          const data = [];
          // 첫 번째 워크시트 콘텐츠 가져오기
          const worksheet = workbook.getWorksheet(1);
          if (worksheet) {
            // 첫 번째 행의 제목 가져오기
            const fields: any[] = [];
            worksheet.getRow(1).eachCell((cell) => {
              fields.push(cell.value);
            });
            // 워크시트의 각 행 반복 (두 번째 행부터, 첫 번째 행은 일반적으로 제목행)
            for (let rowNumber = 2; rowNumber <= worksheet.rowCount; rowNumber++) {
              const rowData: IObject = {};
              const row = worksheet.getRow(rowNumber);
              // 현재 행의 각 셀 반복
              row.eachCell((cell, colNumber) => {
                // 제목에 해당하는 키를 가져와서 현재 셀의 값을 해당 속성명에 저장
                rowData[fields[colNumber - 1]] = cell.value;
              });
              // 현재 행의 데이터 객체를 배열에 추가
              data.push(rowData);
            }
          }
          if (data.length === 0) {
            ElMessage.error("구문 분석된 데이터가 없습니다");
            return;
          }
          imports액션(data).then(() => {
            ElMessage.success("데이터 가져오기 성공");
            handleCloseImportModal();
            handleRefresh(true);
          });
        })
        .catch((error) => console.log(error));
    } else {
      ElMessage.error("파일 읽기 실패");
    }
  };
}

// 작업열
function handleToolbar(name: string) {
  switch (name) {
    case "refresh":
      handleRefresh();
      break;
    case "exports":
      handleOpenExportsModal();
      break;
    case "imports":
      handleOpenImportModal();
      break;
    case "search":
      emit("searchClick");
      break;
    case "add":
      emit("addClick");
      break;
    case "delete":
      handleDelete();
      break;
    case "import":
      handleOpenImportModal(true);
      break;
    case "export":
      emit("exportClick");
      break;
    default:
      emit("toolbarClick", name);
      break;
  }
}

// 작업 열
function handleOperate(data: IOperateData) {
  switch (data.name) {
    case "delete":
      if (props.contentConfig?.delete액션) {
        handleDelete(data.row[pk]);
      } else {
        emit("operateClick", data);
      }
      break;
    default:
      emit("operateClick", data);
      break;
  }
}

// 속성 수정
function handleModify(field: string, value: boolean | string | number, row: Record<string, any>) {
  if (props.contentConfig.modify액션) {
    props.contentConfig.modify액션({
      [pk]: row[pk],
      field,
      value,
    });
  } else {
    ElMessage.error("modify액션이 설정되지 않았습니다");
  }
}

// 페이지네이션 전환
function handleSizeChange(value: number) {
  pagination.pageSize = value;
  handleRefresh();
}
function handleCurrentChange(value: number) {
  pagination.currentPage = value;
  handleRefresh();
}

// 원격데이터필터
let filterParams: IObject = {};
function handleFilterChange(newFilters: any) {
  const filters: IObject = {};
  for (const key in newFilters) {
    const col = cols.value.find((col) => {
      return col.columnKey === key || col["column-key"] === key;
    });
    if (col && col.filterJoin !== undefined) {
      filters[key] = newFilters[key].join(col.filterJoin);
    } else {
      filters[key] = newFilters[key];
    }
  }
  filterParams = { ...filterParams, ...filters };
  emit("filterChange", filterParams);
}

// 조회필터조건
function getFilterParams() {
  return filterParams;
}

// 페이지네이션 데이터 조회
let lastFormData = {};
function fetchPageData(formData: IObject = {}, isRestart = false) {
  loading.value = true;
  // 이전 검색 조건
  lastFormData = formData;
  // 페이지 초기화
  if (isRestart) {
    pagination.currentPage = 1;
  }
  props.contentConfig
    .index액션(
      showPagination
        ? {
            [request.pageName]: pagination.currentPage,
            [request.limitName]: pagination.pageSize,
            ...formData,
          }
        : formData
    )
    .then((data) => {
      if (showPagination) {
        if (props.contentConfig.parseData) {
          data = props.contentConfig.parseData(data);
        }
        pagination.total = data.total;
        pageData.value = data.list;
      } else {
        pageData.value = data;
      }
    })
    .finally(() => {
      loading.value = false;
    });
}
fetchPageData();

// Excel 내보내기
function exportPageData(formData: IObject = {}) {
  if (props.contentConfig.export액션) {
    props.contentConfig.export액션(formData).then((response) => {
      const fileData = response.data;
      const fileName = decodeURI(
        response.headers["content-disposition"].split(";")[1].split("=")[1]
      );
      saveXlsx(fileData, fileName);
    });
  } else {
    ElMessage.error("export액션이 설정되지 않았습니다");
  }
}

// 브라우저 파일 저장
function saveXlsx(fileData: any, fileName: string) {
  const fileType =
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";

  const blob = new Blob([fileData], { type: fileType });
  const downloadUrl = window.URL.createObjectURL(blob);

  const downloadLink = document.createElement("a");
  downloadLink.href = downloadUrl;
  downloadLink.download = fileName;

  document.body.appendChild(downloadLink);
  downloadLink.click();

  document.body.removeChild(downloadLink);
  window.URL.revokeObjectURL(downloadUrl);
}

// 노출된 속성 및 메서드
defineExpose({ fetchPageData, exportPageData, getFilterParams, getSelectionData, handleRefresh });
</script>

<style lang="scss" scoped>
.toolbar-left,
.toolbar-right {
  .el-button {
    margin-right: 0 !important;
    margin-left: 0 !important;
  }
}
</style>
