<template>
  <div ref="tableSelectRef" :style="'width:' + width">
    <el-popover
      :visible="popoverVisible"
      :width="popoverWidth"
      placement="bottom-end"
      v-bind="selectConfig.popover"
      @show="handleShow"
    >
      <template #reference>
        <div @click="popoverVisible = !popoverVisible">
          <slot>
            <el-input
              class="reference"
              :model-value="text"
              :readonly="true"
              :placeholder="placeholder"
            >
              <template #suffix>
                <el-icon
                  :style="{
                    transform: popoverVisible ? 'rotate(180deg)' : 'rotate(0)',
                    transition: 'transform .5s',
                  }"
                >
                  <ArrowDown />
                </el-icon>
              </template>
            </el-input>
          </slot>
        </div>
      </template>
      <!-- 팝업 내용 -->
      <div ref="popoverContentRef">
        <!-- 폼 -->
        <el-form ref="formRef" :model="queryParams" :inline="true">
          <template v-for="item in selectConfig.formItems" :key="item.prop">
            <el-form-item :label="item.label" :prop="item.prop">
              <!-- Input 입력 상자 -->
              <template v-if="item.type === 'input'">
                <template v-if="item.attrs?.type === 'number'">
                  <el-input
                    v-model.number="queryParams[item.prop]"
                    v-bind="item.attrs"
                    @keyup.enter="handleQuery"
                  />
                </template>
                <template v-else>
                  <el-input
                    v-model="queryParams[item.prop]"
                    v-bind="item.attrs"
                    @keyup.enter="handleQuery"
                  />
                </template>
              </template>
              <!-- Select 선택기 -->
              <template v-else-if="item.type === 'select'">
                <el-select v-model="queryParams[item.prop]" v-bind="item.attrs">
                  <template v-for="option in item.options" :key="option.value">
                    <el-option :label="option.label" :value="option.value" />
                  </template>
                </el-select>
              </template>
              <!-- TreeSelect 트리 선택 -->
              <template v-else-if="item.type === 'tree-select'">
                <el-tree-select v-model="queryParams[item.prop]" v-bind="item.attrs" />
              </template>
              <!-- DatePicker 날짜 선택기 -->
              <template v-else-if="item.type === 'date-picker'">
                <el-date-picker v-model="queryParams[item.prop]" v-bind="item.attrs" />
              </template>
              <!-- Input 입력 상자 -->
              <template v-else>
                <template v-if="item.attrs?.type === 'number'">
                  <el-input
                    v-model.number="queryParams[item.prop]"
                    v-bind="item.attrs"
                    @keyup.enter="handleQuery"
                  />
                </template>
                <template v-else>
                  <el-input
                    v-model="queryParams[item.prop]"
                    v-bind="item.attrs"
                    @keyup.enter="handleQuery"
                  />
                </template>
              </template>
            </el-form-item>
          </template>
          <el-form-item>
            <el-button type="primary" icon="search" @click="handleQuery">검색</el-button>
            <el-button icon="refresh" @click="handleReset">초기화</el-button>
          </el-form-item>
        </el-form>
        <!-- 목록 -->
        <el-table
          ref="tableRef"
          v-loading="loading"
          :data="pageData"
          :border="true"
          :max-height="250"
          :row-key="pk"
          :highlight-current-row="true"
          :class="{ radio: !isMultiple }"
          @select="handleSelect"
          @select-all="handleSelectAll"
        >
          <template v-for="col in selectConfig.tableColumns" :key="col.prop">
            <!-- 사용자 정의 -->
            <template v-if="col.templet === 'custom'">
              <el-table-column v-bind="col">
                <template #default="scope">
                  <slot :name="col.slotName ?? col.prop" :prop="col.prop" v-bind="scope" />
                </template>
              </el-table-column>
            </template>
            <!-- 기타 -->
            <template v-else>
              <el-table-column v-bind="col" />
            </template>
          </template>
        </el-table>
        <!-- 페이지네이션 -->
        <pagination
          v-if="total > 0"
          v-model:total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="handlePagination"
        />
        <div class="feedback">
          <el-button type="primary" size="small" @click="handleConfirm">
            {{ confirmText }}
          </el-button>
          <el-button size="small" @click="handleClear">비우기</el-button>
          <el-button size="small" @click="handleClose">닫기</el-button>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from "vue";
import { useResizeObserver } from "@vueuse/core";
import type { FormInstance, PopoverProps, TableInstance } from "element-plus";

// 객체 타입
export type IObject = Record<string, any>;
// 수신할 속성 정의
export interface ISelectConfig<T = any> {
  // 너비
  width?: string;
  // 플레이스홀더
  placeholder?: string;
  // popover 컴포넌트 속성
  popover?: Partial<Omit<PopoverProps, "visible" | "v-model:visible">>;
  // 목록의 네트워크 요청 함수 (promise 반환 필요)
  index액션: (_queryParams: T) => Promise<any>;
  // 주 키 이름 (페이지 간 선택 필수, 기본값: id)
  pk?: string;
  // 다중 선택
  multiple?: boolean;
  // 폼 항목
  formItems: Array<{
    // 컴포넌트 타입 (input, select 등)
    type?: "input" | "select" | "tree-select" | "date-picker";
    // 레이블 텍스트
    label: string;
    // 키 이름
    prop: string;
    // 컴포넌트 속성
    attrs?: IObject;
    // 초기값
    initialValue?: any;
    // 옵션 (select 컴포넌트에 적용)
    options?: { label: string; value: any }[];
  }>;
  // 열 옵션
  tableColumns: Array<{
    type?: "default" | "selection" | "index" | "expand";
    label?: string;
    prop?: string;
    width?: string | number;
    [key: string]: any;
  }>;
}
const props = withDefaults(
  defineProps<{
    selectConfig: ISelectConfig;
    text?: string;
  }>(),
  {
    text: "",
  }
);

// 사용자 정의 이벤트
const emit = defineEmits<{
  confirmClick: [selection: any[]];
}>();

// 주 키
const pk = props.selectConfig.pk ?? "id";
// 다중 선택 여부
const isMultiple = props.selectConfig.multiple === true;
// 너비
const width = props.selectConfig.width ?? "100%";
// 플레이스홀더
const placeholder = props.selectConfig.placeholder ?? "선택하세요";
// 팝업 표시 여부
const popoverVisible = ref(false);
// 로드 상태
const loading = ref(false);
// 데이터 총 개수
const total = ref(0);
// 목록 데이터
const pageData = ref<IObject[]>([]);
// 페이지당 항목 수
const pageSize = 10;
// 검색 매개변수
const queryParams = reactive<{
  pageNum: number;
  pageSize: number;
  [key: string]: any;
}>({
  pageNum: 1,
  pageSize,
});

// popover 너비 계산
const tableSelectRef = ref();
const popoverWidth = ref(width);
useResizeObserver(tableSelectRef, (entries) => {
  popoverWidth.value = `${entries[0].contentRect.width}px`;
});

// 폼 작업
const formRef = ref<FormInstance>();
// 검색 조건 초기화
for (const item of props.selectConfig.formItems) {
  queryParams[item.prop] = item.initialValue ?? "";
}
// 초기화 작업
function handleReset() {
  formRef.value?.resetFields();
  fetchPageData(true);
}
// 검색 작업
function handleQuery() {
  fetchPageData(true);
}

// 페이지네이션 데이터 획득
function fetchPageData(isRestart = false) {
  loading.value = true;
  if (isRestart) {
    queryParams.pageNum = 1;
    queryParams.pageSize = pageSize;
  }
  props.selectConfig
    .index액션(queryParams)
    .then((data) => {
      total.value = data.total;
      pageData.value = data.list;
    })
    .finally(() => {
      loading.value = false;
    });
}

// 목록 작업
const tableRef = ref<TableInstance>();
// 데이터 새로 고침 후 선택 항목 유지 여부
for (const item of props.selectConfig.tableColumns) {
  if (item.type === "selection") {
    item.reserveSelection = true;
    break;
  }
}
// 선택
const selectedItems = ref<IObject[]>([]);
const confirmText = computed(() => {
  return selectedItems.value.length > 0 ? `선택됨(${selectedItems.value.length})` : "확 인";
});
function handleSelect(selection: any[]) {
  if (isMultiple || selection.length === 0) {
    // 다중 선택
    selectedItems.value = selection;
  } else {
    // 단일 선택
    selectedItems.value = [selection[selection.length - 1]];
    tableRef.value?.clearSelection();
    tableRef.value?.toggleRowSelection(selectedItems.value[0], true);
    tableRef.value?.setCurrentRow(selectedItems.value[0]);
  }
}
function handleSelectAll(selection: any[]) {
  if (isMultiple) {
    selectedItems.value = selection;
  }
}
// 페이지네이션
function handlePagination() {
  fetchPageData();
}

// 팝업
const isInit = ref(false);
// 표시
function handleShow() {
  if (isInit.value === false) {
    isInit.value = true;
    fetchPageData();
  }
}
// 확인
function handleConfirm() {
  if (selectedItems.value.length === 0) {
    ElMessage.error("데이터를 선택하세요");
    return;
  }
  popoverVisible.value = false;
  emit("confirmClick", selectedItems.value);
}
// 비우기
function handleClear() {
  tableRef.value?.clearSelection();
  selectedItems.value = [];
}
// 닫기
function handleClose() {
  popoverVisible.value = false;
}
const popoverContentRef = ref();
/* onClickOutside(tableSelectRef, () => (popoverVisible.value = false), {
  ignore: [popoverContentRef],
}); */
</script>

<style scoped lang="scss">
.reference :deep(.el-input__wrapper),
.reference :deep(.el-input__inner) {
  cursor: pointer;
}

.feedback {
  display: flex;
  justify-content: flex-end;
  margin-top: 6px;
}
// 모두 선택 버튼 숨기기
.radio :deep(.el-table__header th.el-table__cell:nth-child(1) .el-checkbox) {
  visibility: hidden;
}
</style>
