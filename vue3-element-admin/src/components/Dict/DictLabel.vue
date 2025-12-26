<template>
  <template v-if="tagType">
    <el-tag :type="tagType" :size="tagSize">{{ label }}</el-tag>
  </template>
  <template v-else>
    <span>{{ label }}</span>
  </template>
</template>
<script setup lang="ts">
import { useDictStore } from "@/store";

const props = defineProps({
  code: String, // 사전인코딩
  modelValue: [String, Number], // 사전 항목의값
  size: {
    type: String,
    default: "default", // 레이블크기
  },
});
const label = ref("");
const tagType = ref<"success" | "warning" | "info" | "primary" | "danger" | undefined>(); // 레이블유형
const tagSize = ref<"default" | "large" | "small">(props.size as "default" | "large" | "small"); // 레이블크기

const dictStore = useDictStore();
/**
 * 사전 항목의 값에 따라 해당하는 label 및 tagType 조회
 * @param dictCode 사전인코딩
 * @param value 사전 항목의값
 * @returns label 및 tagType을 포함하는 객체
 */
const getLabelAndTagByValue = async (dictCode: string, value: any) => {
  // 필요에 따라 사전 데이터 로딩
  await dictStore.loadDictItems(dictCode);
  // 캐시에서 사전 데이터 조회
  const dictItems = dictStore.getDictItems(dictCode);
  // 해당하는 사전 항목 찾기
  const dictItem = dictItems.find((item) => item.value == value);
  return {
    label: dictItem?.label || "",
    tagType: dictItem?.tagType,
  };
};
/**
 * 업데이트 label 및 tagType
 */
const updateLabelAndTag = async () => {
  if (!props.code || props.modelValue === undefined) return;
  const { label: newLabel, tagType: newTagType } = await getLabelAndTagByValue(
    props.code,
    props.modelValue
  );
  label.value = newLabel;
  tagType.value = newTagType as typeof tagType.value;
};

// 초기화 또는 code 변경 시 레이블 및 레이블 스타일 업데이트
watch(
  [() => props.code, () => props.modelValue],
  async () => {
    if (props.code) {
      await updateLabelAndTag();
    }
  },
  { immediate: true }
);
</script>
