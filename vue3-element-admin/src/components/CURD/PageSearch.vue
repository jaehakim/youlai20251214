<template>
  <div v-show="visible">
    <el-card v-bind="cardAttrs">
      <el-form ref="queryFormRef" :model="queryParams" v-bind="formAttrs" :class="isGrid">
        <template v-for="(item, index) in formItems" :key="item.prop">
          <el-form-item
            v-show="isExpand ? true : index < showNumber"
            :label="item?.label"
            :prop="item.prop"
          >
            <!-- Label -->
            <template #label>
              <span class="flex-y-center">
                {{ item?.label || "" }}
                <el-tooltip v-if="item?.tips" v-bind="getTooltipProps(item.tips)">
                  <QuestionFilled class="w-4 h-4 mx-1" />
                </el-tooltip>
                <span v-if="searchConfig.colon" class="ml-0.5">:</span>
              </span>
            </template>

            <!-- 사용자 정의 슬롯 -->
            <slot
              v-if="item.type === 'custom'"
              :name="item.slotName"
              :form-data="queryParams"
              :prop="item.prop"
              :attrs="{ style: { width: '100%' }, ...item.attrs }"
            />
            <el-cascader
              v-else-if="item.type === 'cascader'"
              v-model.trim="queryParams[item.prop]"
              v-bind="{ style: { width: '100%' }, ...item.attrs }"
              v-on="item.events || {}"
            />
            <component
              :is="componentMap.get(item.type)"
              v-else
              v-model.trim="queryParams[item.prop]"
              v-bind="{ style: { width: '100%' }, ...item.attrs }"
              v-on="item.events || {}"
            >
              <template v-if="item.type === 'select'">
                <template v-for="opt in item.options" :key="opt.value">
                  <el-option :label="opt.label" :value="opt.value" />
                </template>
              </template>
            </component>
          </el-form-item>
        </template>

        <el-form-item :class="{ 'col-[auto/-1] justify-self-end': searchConfig?.grid === 'right' }">
          <el-button icon="search" type="primary" @click="handleQuery">검색</el-button>
          <el-button icon="refresh" @click="handleReset">초기화</el-button>
          <!-- 전개/축소 -->
          <template v-if="isExpandable && formItems.length > showNumber">
            <el-link class="ml-3" type="primary" underline="never" @click="isExpand = !isExpand">
              {{ isExpand ? "축소" : "전개" }}
              <component :is="isExpand ? ArrowUp : ArrowDown" class="w-4 h-4 ml-2" />
            </el-link>
          </template>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import type { IObject, IForm, ISearchConfig, ISearchComponent } from "./types";
import { ArrowUp, ArrowDown } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import InputTag from "@/components/InputTag/index.vue";

// 수신 속성 정의
const props = defineProps<{ searchConfig: ISearchConfig }>();
// 사용자 정의 이벤트
const emit = defineEmits<{
  queryClick: [queryParams: IObject];
  resetClick: [queryParams: IObject];
}>();
// 컴포넌트 매핑 테이블
const componentMap = new Map<ISearchComponent, any>([
  // @ts-ignore
  ["input", markRaw(ElInput)], // @ts-ignore
  ["select", markRaw(ElSelect)], // @ts-ignore
  ["cascader", markRaw(ElCascader)], // @ts-ignore
  ["input-number", markRaw(ElInputNumber)], // @ts-ignore
  ["date-picker", markRaw(ElDatePicker)], // @ts-ignore
  ["time-picker", markRaw(ElTimePicker)], // @ts-ignore
  ["time-select", markRaw(ElTimeSelect)], // @ts-ignore
  ["tree-select", markRaw(ElTreeSelect)], // @ts-ignore
  ["input-tag", markRaw(ElInputTag)], // @ts-ignore
  ["custom-tag", markRaw(InputTag)],
]);

// 폼 인스턴스 저장
const queryFormRef = ref<FormInstance>();
// 쿼리 매개변수 저장
const queryParams = reactive<IObject>({});
// 표시 여부
const visible = ref(true);
// 반응형 formItems
const formItems = reactive(props.searchConfig?.formItems ?? []);
// 전개/축소 가능 여부
const isExpandable = ref(props.searchConfig?.isExpandable ?? true);
// 이미 전개됨 여부
const isExpand = ref(false);
// 폼 항목 표시 수, 전개 가능한 경우 표시 수를 초과하는 폼 항목은 숨김
const showNumber = computed(() =>
  isExpandable.value ? (props.searchConfig?.showNumber ?? 3) : formItems.length
);
// 카드 컴포넌트 사용자 정의 속성 (그림자, 사용자 정의 여백 스타일 등)
const cardAttrs = computed<IObject>(() => {
  return { shadow: "never", style: { "margin-bottom": "12px" }, ...props.searchConfig?.cardAttrs };
});
// 폼 컴포넌트 사용자 정의 속성 (레이블 위치, 너비, 정렬 방식 등)
const formAttrs = computed<IForm>(() => {
  return { inline: true, ...props.searchConfig?.form };
});
// 반응형 그리드 레이아웃 사용 여부
const isGrid = computed(() =>
  props.searchConfig?.grid
    ? "grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 3xl:grid-cols-5 4xl:grid-cols-6 gap-5"
    : "flex flex-wrap gap-x-8 gap-y-4"
);

// 도구 설명 프롬프트 상자 속성 가져오기
const getTooltipProps = (tips: string | IObject) => {
  return typeof tips === "string" ? { content: tips } : tips;
};
// 검색/초기화 작업
const handleQuery = () => emit("queryClick", queryParams);
const handleReset = () => {
  queryFormRef.value?.resetFields();
  emit("resetClick", queryParams);
};

onMounted(() => {
  formItems.forEach((item) => {
    if (item?.initFn) {
      item.initFn(item);
    }
    if (["input-tag", "custom-tag", "cascader"].includes(item?.type ?? "")) {
      queryParams[item.prop] = Array.isArray(item.initialValue) ? item.initialValue : [];
    } else if (item.type === "input-number") {
      queryParams[item.prop] = item.initialValue ?? null;
    } else {
      queryParams[item.prop] = item.initialValue ?? "";
    }
  });
});
// 노출된 속성 및 메서드
defineExpose({
  // 페이지 매김 데이터 가져오기
  getQueryParams: () => queryParams,
  // SearchForm 표시/숨기기
  toggleVisible: () => (visible.value = !visible.value),
});
</script>

<style lang="scss" scoped>
:deep(.el-input-number .el-input__inner) {
  text-align: left;
}
.el-form-item {
  margin-right: 0;
  margin-bottom: 0;
}
</style>
