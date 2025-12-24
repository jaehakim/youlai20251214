<!-- 부서 트리 -->
<template>
  <el-card shadow="never">
    <el-input v-model="deptName" placeholder="부서명" clearable>
      <template #prefix>
        <el-icon><Search /></el-icon>
      </template>
    </el-input>

    <el-tree
      ref="deptTreeRef"
      class="mt-2"
      :data="deptList"
      :props="{ children: 'children', label: 'label', disabled: '' }"
      :expand-on-click-node="false"
      :filter-node-method="handleFilter"
      default-expand-all
      @node-click="handleNodeClick"
    />
  </el-card>
</template>

<script setup lang="ts">
import DeptAPI from "@/api/system/dept-api";
const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: undefined,
  },
});

const deptList = ref<OptionType[]>(); // 부서 목록
const deptTreeRef = ref(); // 부서 트리
const deptName = ref(); // 부서명

const emits = defineEmits(["node-click"]);

const deptId = useVModel(props, "modelValue", emits);

watchEffect(
  () => {
    deptTreeRef.value.filter(deptName.value);
  },
  {
    flush: "post", // watchEffect는 DOM 마운트 또는 업데이트 전에 트리거되므로, 이 속성은 DOM 요소 업데이트 후 실행을 제어합니다
  }
);

/**
 * 부서 필터링
 */
function handleFilter(value: string, data: any) {
  if (!value) {
    return true;
  }
  return data.label.indexOf(value) !== -1;
}

/** 부서 트리 노드 클릭 */
function handleNodeClick(data: { [key: string]: any }) {
  deptId.value = data.value;
  emits("node-click");
}

onBeforeMount(() => {
  DeptAPI.getOptions().then((data) => {
    deptList.value = data;
  });
});
</script>
