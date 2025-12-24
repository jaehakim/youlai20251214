<!--
 * ECharts 기반 Vue3 차트 컴포넌트
 * 저작권 © 2021-present 유래 오픈소스 조직
 *
 * 오픈소스 라이센스: https://opensource.org/licenses/MIT
 * 프로젝트 주소: https://gitee.com/youlaiorg/vue3-element-admin
 * 참고: https://echarts.apache.org/handbook/zh/basics/import/#%E6%8C%89%E9%9C%80%E5%BC%95%E5%85%A5-echarts-%E5%9B%BE%E8%A1%A8%E5%92%8C%E7%BB%84%E4%BB%B6
 *
 * 사용 시 본 주석을 유지해 주세요. 오픈소스를 지원해주셔서 감사합니다!
 -->

<template>
  <div ref="chartRef" :style="{ width, height }"></div>
</template>

<script setup lang="ts">
// echarts 핵심 모듈 가져오기, 핵심 모듈은 echarts 사용에 필요한 인터페이스를 제공합니다.
import * as echarts from "echarts/core";
// 열, 선, 원형 등 자주 사용하는 차트 가져오기
import { BarChart, LineChart, PieChart } from "echarts/charts";
// 제목, 도움말 상자, 직각 좌표계, 데이터 세트, 기본 제공 데이터 변환 컴포넌트 가져오기
import { GridComponent, TooltipComponent, LegendComponent } from "echarts/components";
// Canvas 렌더러 가져오기, CanvasRenderer 또는 SVGRenderer 가져오기는 필수 단계입니다.
import { CanvasRenderer } from "echarts/renderers";

import { useResizeObserver } from "@vueuse/core";

// 필요에 따라 컴포넌트 등록
echarts.use([
  CanvasRenderer,
  BarChart,
  LineChart,
  PieChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
]);

const props = defineProps<{
  options: echarts.EChartsCoreOption;
  width?: string;
  height?: string;
}>();

const chartRef = ref<HTMLDivElement | null>(null);
let chartInstance: echarts.ECharts | null = null;

// 차트 초기화
const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    if (props.options) {
      chartInstance.setOption(props.options);
    }
  }
};

// 크기 변경 감시, 자동 조정
useResizeObserver(chartRef, () => {
  chartInstance?.resize();
});

// options 변경 감시, 차트 업데이트
watch(
  () => props.options,
  (newOptions) => {
    if (chartInstance && newOptions) {
      chartInstance.setOption(newOptions);
    }
  },
  { deep: true }
);

onMounted(() => {
  nextTick(() => initChart());
});

onBeforeUnmount(() => {
  chartInstance?.dispose();
});
</script>
