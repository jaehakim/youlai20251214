<template>
  <div class="canvas-dom">
    <h3>Canvas 기반 서명 구성 요소</h3>
    <header>
      <el-button type="primary" @click="handleSaveImg">이미지로 저장</el-button>
      <el-button @click="handleToFile">백엔드에 저장</el-button>
      <el-button @click="handleClearSign">서명 지우기</el-button>
    </header>
    <canvas
      ref="canvas"
      height="200"
      width="500"
      @mousedown="onEventStart"
      @mousemove.stop.prevent="onEventMove"
      @mouseup="onEventEnd"
      @touchstart="onEventStart"
      @touchmove.stop.prevent="onEventMove"
      @touchend="onEventEnd"
    />
    <img v-if="imgUrl" :src="imgUrl" alt="서명" />
  </div>
</template>
<script setup lang="ts">
import FileAPI from "@/api/file-api";

const imgUrl = ref("");
const canvas = ref();
let ctx: CanvasRenderingContext2D;

// 그리기 중, move 및 end 이벤트를 제어합니다
let painting = false;

// 트리거된 DOM에 상대적인 트리거 포인트의 왼쪽 및 위쪽 거리 가져오기
const getOffset = (event: MouseEvent | TouchEvent) => {
  let offset: [number, number];
  if ((event as MouseEvent).offsetX) {
    // PC
    const { offsetX, offsetY } = event as MouseEvent;
    offset = [offsetX, offsetY];
  } else {
    // 모바일
    const { top, left } = canvas.value.getBoundingClientRect();
    const offsetX = (event as TouchEvent).touches[0].clientX - left;
    const offsetY = (event as TouchEvent).touches[0].clientY - top;
    offset = [offsetX, offsetY];
  }

  return offset;
};

// 드로잉 시작점
let startX = 0,
  startY = 0;

// 마우스/터치 누르면 트리거된 DOM에 상대적인 트리거 포인트의 왼쪽 및 위쪽 거리 저장
const onEventStart = (event: MouseEvent | TouchEvent) => {
  [startX, startY] = getOffset(event);
  painting = true;
};

const onEventMove = (event: MouseEvent | TouchEvent) => {
  if (painting) {
    // 마우스/터치 이동 시 이동 포인트의 상대 왼쪽 및 위쪽 거리 저장
    const [endX, endY] = getOffset(event);
    paint(startX, startY, endX, endY, ctx);

    // 매번 그리기 또는 지운 후 시작점을 이전 끝점으로 재설정
    startX = endX;
    startY = endY;
  }
};

const onEventEnd = () => {
  if (painting) {
    painting = false; // 그리기 중지
  }
};

onMounted(() => {
  ctx = canvas.value.getContext("2d") as CanvasRenderingContext2D;
});
const handleToFile = async () => {
  if (isCanvasBlank(canvas.value)) {
    ElMessage({
      type: "warning",
      message: "현재 서명 파일이 비어 있음",
    });
    return;
  }
  const file = dataURLtoFile(canvas.value.toDataURL(), "서명.png");

  if (!file) return;
  const data = await FileAPI.uploadFile(file);
  handleClearSign();
  imgUrl.value = data.url;
};
const handleClearSign = () => {
  ctx.clearRect(0, 0, canvas.value.width, canvas.value.height);
};
const isCanvasBlank = (canvas: HTMLCanvasElement) => {
  const blank = document.createElement("canvas"); // 시스템에서 빈 canvas 객체 가져오기
  blank.width = canvas.width;
  blank.height = canvas.height;
  return canvas.toDataURL() == blank.toDataURL(); // 값을 비교하면 같으면 비어 있음
};

// 이미지로 저장
const handleSaveImg = () => {
  if (isCanvasBlank(canvas.value)) {
    ElMessage({
      type: "warning",
      message: "현재 서명 파일이 비어 있음",
    });
    return;
  }
  const el = document.createElement("a");
  // href를 base64로 인코딩된 이미지 문자열로 설정, 기본은 png 형식
  el.href = canvas.value.toDataURL();
  el.download = "서명";
  // 클릭 이벤트를 생성하고 a 태그를 트리거
  const event = new MouseEvent("click");
  el.dispatchEvent(event);
};
// 파일 형식으로 변환, 백엔드로 전달 가능
const dataURLtoFile = (dataurl: string, filename: string) => {
  const arr: string[] = dataurl.split(",");
  if (!arr.length) return;

  const mime = arr[0].match(/:(.*?);/);
  if (mime) {
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, { type: mime[1] });
  }
};
// canvas 그리기
function paint(
  startX: number,
  startY: number,
  endX: number,
  endY: number,
  ctx: CanvasRenderingContext2D
) {
  ctx.beginPath();
  ctx.globalAlpha = 1;
  ctx.lineWidth = 2;
  ctx.strokeStyle = "#000";
  ctx.moveTo(startX, startY);
  ctx.lineTo(endX, endY);
  ctx.closePath();
  ctx.stroke();
}
</script>
<style scoped lang="scss">
.canvas-dom {
  width: 100%;
  height: 100%;
  padding: 0 20px;
  background-color: #fff;

  canvas {
    border: 1px solid #e6e6e6;
  }

  header {
    display: flex;
    flex-flow: row nowrap;
    align-items: center;
    width: 100%;
    margin: 8px;

    .eraser-option {
      display: flex;

      label {
        white-space: nowrap;
      }
    }
  }
}
</style>
