<template>
  <el-alert :closable="false" title="메뉴 두 번째 레벨" type="success" />
  <el-input v-model="value" placeholder="캐시 테스트" />
</template>
<script setup lang="ts">
defineOptions({ name: "MultiLevel2" });

const value = ref("");

onMounted(() => {
  console.log("MultiLevel2 mounted");
});
</script>
