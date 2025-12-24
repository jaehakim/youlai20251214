<!-- 복사 컴포넌트 -->
<template>
  <el-button link :style="style" @click="handleClipboard">
    <slot>
      <el-icon><DocumentCopy color="var(--el-color-primary)" /></el-icon>
    </slot>
  </el-button>
</template>

<script setup lang="ts">
defineOptions({
  name: "CopyButton",
  inheritAttrs: false,
});

const props = defineProps({
  text: {
    type: String,
    default: "",
  },
  style: {
    type: Object,
    default: () => ({}),
  },
});

function handleClipboard() {
  if (navigator.clipboard && navigator.clipboard.writeText) {
    // Clipboard API 사용
    navigator.clipboard
      .writeText(props.text)
      .then(() => {
        ElMessage.success("Copy successfully");
      })
      .catch((error) => {
        ElMessage.warning("Copy failed");
        console.log("[CopyButton] Copy failed", error);
      });
  } else {
    // 호환성 처리 (useClipboard 호환성 문제 있음)
    const input = document.createElement("input");
    input.style.position = "absolute";
    input.style.left = "-9999px";
    input.setAttribute("value", props.text);
    document.body.appendChild(input);
    input.select();
    try {
      const successful = document.execCommand("copy");
      if (successful) {
        ElMessage.success("Copy successfully!");
      } else {
        ElMessage.warning("Copy failed!");
      }
    } catch (err) {
      ElMessage.error("Copy failed.");
      console.log("[CopyButton] Copy failed.", err);
    } finally {
      document.body.removeChild(input);
    }
  }
}
</script>
