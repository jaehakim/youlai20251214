<template>
  <div class="p-5">라우트 매개 변수 type: {{ query }}</div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Other",
  inheritAttrs: false,
});
import { useRoute } from "vue-router";

// query 매개 변수 가져오기
const query = useRoute().query.type as string;
</script>

<style lang="scss" scoped></style>
