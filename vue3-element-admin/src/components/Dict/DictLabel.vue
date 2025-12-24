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
 * 에 따라사전 항목의값조회对应의 label 및 tagType
 * @param dictCode 사전인코딩
 * @param value 사전 항목의값
 * @returns 패키지含 label 및 tagType 의对象
 */
const getLabelAndTagByValue = async (dictCode: string, value: any) => {
  // 按需로딩사전데이터
  await dictStore.loadDictItems(dictCode);
  // 从缓存내조회사전데이터
  const dictItems = dictStore.getDictItems(dictCode);
  // 查找对应의사전 항목
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

// 初始化或code变化시업데이트레이블및레이블스타일
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
