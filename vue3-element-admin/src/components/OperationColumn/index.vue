<template>
  <el-table-column
    :label="label"
    :fixed="fixed"
    :align="align"
    :show-overflow-tooltip="showOverflowTooltip"
    :width="finalWidth"
  >
    <template #default="{ row }">
      <div v-auto-width class="operation-buttons">
        <slot :row="row"></slot>
      </div>
    </template>
  </el-table-column>
</template>

<script setup lang="ts">
interface Props {
  listDataLength: number;
  prop?: string;
  label?: string;
  fixed?: string;
  align?: string;
  width?: number;
  showOverflowTooltip?: boolean;
  minWidth?: number;
}

const props = withDefaults(defineProps<Props>(), {
  label: "작업",
  fixed: "right",
  align: "center",
  minWidth: 80,
});

const count = ref(0);
const operationWidth = ref(props.minWidth || 80);

// 작업 열 너비 계산
const calculateWidth = () => {
  count.value++;

  if (count.value !== props.listDataLength) return;
  const maxWidth = getOperationMaxWidth();
  operationWidth.value = Math.max(maxWidth, props.minWidth);
  count.value = 0;
};

// 최종 너비 계산
const finalWidth = computed(() => {
  return props.width || operationWidth.value || props.minWidth;
});

// 자동 너비 조정 지시문
const vAutoWidth = {
  mounted() {
    // 초기 마운트 시 한 번 계산
    calculateWidth();
  },
  updated() {
    // 데이터 업데이트 시 재계산
    calculateWidth();
  },
};

/**
 * 버튼 수와 너비로 작업 그룹의 최대 너비 획득
 * 사용 시 `class="operation-buttons"` 태그로 작업 버튼을 감싸야 함
 * @returns {number} 작업 그룹의 최대 너비 반환
 */
const getOperationMaxWidth = () => {
  const el = document.getElementsByClassName("operation-buttons");

  // 작업 그룹의 최대 너비 가져오기
  let maxWidth = 0;
  let totalWidth: any = 0;
  Array.prototype.forEach.call(el, (item) => {
    // 각 item의 dom 가져오기
    const buttons = item.querySelectorAll(".el-button");
    // 각 행 버튼의 총 너비 가져오기
    totalWidth = Array.from(buttons).reduce((acc, button: any) => {
      return acc + button.scrollWidth + 22; // 각 버튼의 너비 + 예약 너비
    }, 0);

    // 최대 너비 가져오기
    if (totalWidth > maxWidth) maxWidth = totalWidth;
  });

  return maxWidth;
};
</script>
