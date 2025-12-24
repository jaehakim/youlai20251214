<!-- 아이콘 선택기 예제 -->
<template>
  <div class="app-container">
    <el-link
      href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/icon-selector.vue"
      type="primary"
      target="_blank"
      class="mb-10"
    >
      예제 소스 코드 클릭>>>>
    </el-link>
    <icon-select v-model="iconName" />
  </div>
</template>

<script setup lang="ts">
// element-plus 아이콘 형식은 el-icon-으로 시작합니다
const iconName = ref("el-icon-edit");
// 로컬 SVG 아이콘 형식은 src/assets/icons 아래 파일 이름을 가져오고 svg 확장자는 필요 없습니다
// const iconName = ref("api");
</script>
