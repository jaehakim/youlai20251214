<!-- WangEditor 리치 텍스트 에디터 예제 -->
<template>
  <div class="app-container">
    <el-link
      href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/wang-editor.vue"
      type="primary"
      target="_blank"
      class="mb-[20px]"
    >
      예제 소스 코드 클릭>>>>
    </el-link>
    <WangEditor v-model="value" height="400px" />

    <div style="margin-top: 10px">
      <textarea v-model="value" readonly style="width: 100%; height: 200px; outline: none" />
    </div>
  </div>
</template>

<script setup lang="ts">
import WangEditor from "@/components/WangEditor/index.vue";

const value = ref("초기화 내용");
</script>
