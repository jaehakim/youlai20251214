<!--
  TextScroll 컴포넌트 - 텍스트 스크롤 공지

  기능:
  - 수평 방향 텍스트 스크롤 지원
  - 다양한 사전 설정 스타일 제공 (기본값, 성공, 경고, 위험, 정보)
  - 사용자 정의 스크롤 속도 및 방향 지원
  - 선택 가능한 타자기 입력 효과
  - 마우스 호버 시 스크롤 일시 중지
  - 선택 가능한 닫기 버튼
-->
<template>
  <div
    ref="containerRef"
    class="text-scroll-container"
    :class="[`text-scroll--${props.type}`]"
    :typewriter="props.typewriter ? 'true' : undefined"
  >
    <!-- 왼쪽 아이콘 -->
    <div class="left-icon">
      <el-icon><Bell /></el-icon>
    </div>
    <!-- 스크롤 콘텐츠 래퍼 -->
    <div class="scroll-wrapper">
      <div
        ref="scrollContent"
        class="text-scroll-content"
        :class="{ scrolling: shouldScroll }"
        :style="scrollStyle"
      >
        <!-- 스크롤 콘텐츠, 2개 복사하여 원활한 스크롤 구현 -->
        <div class="scroll-item" v-html="sanitizedContent" />
        <div class="scroll-item" v-html="sanitizedContent" />
      </div>
    </div>
    <!-- 선택 가능한 닫기 버튼 -->
    <div v-if="showClose" class="right-icon" @click="handleRightIconClick">
      <el-icon><Close /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useElementHover } from "@vueuse/core";

const emit = defineEmits(["close"]);

interface Props {
  /** 스크롤 텍스트 콘텐츠 (필수) */
  text: string;
  /** 스크롤 속도, 값이 작을수록 스크롤이 느림 */
  speed?: number;
  /** 스크롤 방향: 왼쪽 또는 오른쪽 */
  direction?: "left" | "right";
  /** 스타일 유형 */
  type?: "default" | "success" | "warning" | "danger" | "info";
  /** 닫기 버튼 표시 여부 */
  showClose?: boolean;
  /** 타자기 효과 활성화 여부 */
  typewriter?: boolean;
  /** 타자기 효과의 속도, 값이 작을수록 타이핑이 빠름 */
  typewriterSpeed?: number;
}

// 컴포넌트 속성 및 기본값 정의
const props = withDefaults(defineProps<Props>(), {
  speed: 70,
  direction: "left",
  type: "default",
  showClose: false,
  typewriter: false,
  typewriterSpeed: 100,
});

// 컨테이너 요소 참조
const containerRef = ref<HTMLElement | null>(null);
// vueuse의 useElementHover를 사용하여 마우스 호버 상태 감지
const isHovered = useElementHover(containerRef);
// 스크롤 콘텐츠 요소 참조
const scrollContent = ref<HTMLElement | null>(null);
// 애니메이션 지속 시간 (초)
const animationDuration = ref(0);

/**
 * 타자기 효과 관련 상태
 */
// 현재 표시된 텍스트 콘텐츠
const currentText = ref("");
// 타자기 타이머 참조, 정리용
let typewriterTimer: ReturnType<typeof setTimeout> | null = null;
// 타자기 효과 완료 여부
const isTypewriterComplete = ref(false);

/**
 * 스크롤 여부 계산
 * 조건:
 * 1. 마우스가 컴포넌트 위에 없음
 * 2. 타자기 효과가 활성화된 경우 타자기 효과 완료 대기
 */
const shouldScroll = computed(() => {
  if (props.typewriter) {
    return !isHovered.value && isTypewriterComplete.value;
  }
  return !isHovered.value;
});

/**
 * 최종 표시 콘텐츠 계산
 * 타자기 효과가 활성화된 경우 현재까지 입력된 텍스트 표시
 * 그렇지 않으면 전체 텍스트 직접 표시
 * 주의: 콘텐츠는 HTML을 지원하므로 사용 시 XSS 위험 유의
 */
const sanitizedContent = computed(() => (props.typewriter ? currentText.value : props.text));

/**
 * 스크롤 스타일 계산
 * 애니메이션 지속 시간, 재생 상태 및 방향 포함
 * 이러한 값은 CSS 변수를 통해 스타일에 전달됨
 */
const scrollStyle = computed(() => ({
  "--animation-duration": `${animationDuration.value}s`,
  "--animation-play-state": shouldScroll.value ? "running" : "paused",
  "--animation-direction": props.direction === "left" ? "normal" : "reverse",
}));

/**
 * 애니메이션 지속 시간 계산
 * 콘텐츠 너비 및 설정된 속도에 따라 적절한 애니메이션 지속 시간 계산
 * 콘텐츠가 길수록 또는 속도 값이 작을수록 애니메이션 지속 시간이 길어짐
 */
const calculateDuration = () => {
  if (scrollContent.value) {
    const contentWidth = scrollContent.value.scrollWidth / 2;
    animationDuration.value = contentWidth / props.speed;
  }
};

/**
 * 닫기 버튼 클릭 이벤트 처리
 * close 이벤트를 트리거하고 현재 컴포넌트 직접 삭제
 */
const handleRightIconClick = () => {
  emit("close");
  // 현재 컴포넌트의 DOM 요소 가져오기
  if (containerRef.value) {
    // DOM에서 요소 제거
    containerRef.value.remove();
  }
};

/**
 * 타자기 효과 시작
 * 텍스트를 문자 단위로 표시하고 완료 후 상태 설정하여 스크롤 시작
 */
const startTypewriter = () => {
  let index = 0;
  currentText.value = "";
  isTypewriterComplete.value = false; // 상태 재설정

  // 재귀 함수, 문자를 하나씩 추가
  const type = () => {
    if (index < props.text.length) {
      // 한 문자 추가
      currentText.value += props.text[index];
      index++;
      // 다음 문자의 지연 설정
      typewriterTimer = setTimeout(type, props.typewriterSpeed);
    } else {
      // 모든 문자가 추가됨, 완료 상태 설정
      isTypewriterComplete.value = true;
    }
  };

  // 타자기 프로세스 시작
  type();
};

onMounted(() => {
  // 초기 애니메이션 지속 시간 계산
  calculateDuration();
  // 윈도우 크기 변경 감시, 애니메이션 지속 시간 재계산
  window.addEventListener("resize", calculateDuration);

  // 타자기 효과가 활성화된 경우 타이핑 시작
  if (props.typewriter) {
    startTypewriter();
  }
});

onUnmounted(() => {
  // 이벤트 리스너 제거
  window.removeEventListener("resize", calculateDuration);
  // 타자기 타이머 정리
  if (typewriterTimer) {
    clearTimeout(typewriterTimer);
  }
});

/**
 * 텍스트 콘텐츠 변경 감시
 * 텍스트 콘텐츠가 변경되면 타자기 효과가 활성화된 경우 타이핑 다시 시작
 */
watch(
  () => props.text,
  () => {
    if (props.typewriter) {
      // 清除现有定시器
      if (typewriterTimer) {
        clearTimeout(typewriterTimer);
      }
      // 重新开始打字效果
      startTypewriter();
    }
  }
);
</script>

<style scoped lang="scss">
.text-scroll-container {
  position: relative;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  width: 100%;
  padding-right: 16px;
  overflow: hidden;
  background-color: var(--el-color-primary-light-9) !important;
  border: 1px solid var(--main-color);
  border-radius: calc(var(--custom-radius) / 2 + 2px) !important;

  .left-icon,
  .right-icon {
    position: absolute;
    top: 0;
    bottom: 0;
    z-index: 2;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 100%;
    text-align: center;
    background-color: var(--el-color-primary-light-9) !important;
  }

  .left-icon {
    left: 0;
  }

  .right-icon {
    right: 0;
    cursor: pointer;
    background-color: transparent !important;
  }

  .scroll-wrapper {
    flex: 1;
    margin-left: 34px;
    overflow: hidden;
  }

  .text-scroll-content {
    display: flex;
    height: 34px;
    line-height: 34px;
    white-space: nowrap;
    animation: scroll linear infinite;
    animation-duration: var(--animation-duration);
    animation-direction: var(--animation-direction);
    animation-play-state: var(--animation-play-state);

    .scroll-item {
      display: inline-block;
      min-width: 100%;
      padding: 0 10px;
      font-size: 14px;
      color: var(--el-color-primary-light-2) !important;
      text-align: left;
      text-align: center;

      :deep(a) {
        color: #fd4e4e !important;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }

  @keyframes scroll {
    0% {
      transform: translateX(0);
    }

    100% {
      transform: translateX(-100%);
    }
  }

  // 추가유형스타일
  &.text-scroll--default {
    background-color: var(--el-color-primary-light-9) !important;
    border-color: var(--el-color-primary);

    .right-icon,
    .left-icon i {
      color: var(--el-color-primary) !important;
    }

    .scroll-item {
      color: var(--el-color-primary) !important;
    }
  }

  &.text-scroll--success {
    background-color: var(--el-color-success-light-9) !important;
    border-color: var(--el-color-success);

    .left-icon {
      background-color: var(--el-color-success-light-9) !important;

      i {
        color: var(--el-color-success);
      }
    }

    .scroll-item {
      color: var(--el-color-success) !important;
    }
  }

  &.text-scroll--warning {
    background-color: var(--el-color-warning-light-9) !important;
    border-color: var(--el-color-warning);

    .left-icon {
      background-color: var(--el-color-warning-light-9) !important;

      i {
        color: var(--el-color-warning);
      }
    }

    .scroll-item {
      color: var(--el-color-warning) !important;
    }
  }

  &.text-scroll--danger {
    background-color: var(--el-color-danger-light-9) !important;
    border-color: var(--el-color-danger);

    .left-icon {
      background-color: var(--el-color-danger-light-9) !important;

      i {
        color: var(--el-color-danger);
      }
    }

    .scroll-item {
      color: var(--el-color-danger) !important;
    }
  }

  &.text-scroll--info {
    background-color: var(--el-color-info-light-9) !important;
    border-color: var(--el-color-info);

    .left-icon {
      background-color: var(--el-color-info-light-9) !important;

      i {
        color: var(--el-color-info);
      }
    }

    .scroll-item {
      color: var(--el-color-info) !important;
    }
  }
}

// 추가打字机效果의커서스타일
.text-scroll-content .scroll-item {
  &::after {
    content: "";
    opacity: 0;
    animation: none;
  }
}

// 오직在활성화打字机效果시표시커서
.text-scroll-container[typewriter] .text-scroll-content .scroll-item::after {
  content: "|";
  opacity: 0;
  animation: cursor 1s infinite;
}

@keyframes cursor {
  0%,
  100% {
    opacity: 0;
  }

  50% {
    opacity: 1;
  }
}
</style>
