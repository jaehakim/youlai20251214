<template>
  <el-alert :closable="false" title="메뉴 첫 번째 레벨" />
</template>
<script setup lang="ts">
defineOptions({ name: "MultiLevel1" });

onMounted(() => {
  console.log("MultiLevel1 mounted");
});
</script>
