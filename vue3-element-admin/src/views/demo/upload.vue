<!-- 파일 업로드 컴포넌트 예제 -->
<template>
  <div class="app-container">
    <el-link
      href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/upload.vue"
      type="primary"
      target="_blank"
      class="mb-10"
    >
      예제 소스 코드 클릭>>>>
    </el-link>

    <el-form>
      <el-form-item label="단일 이미지 업로드">
        <SingleImageUpload v-model="picUrl" />
      </el-form-item>

      <el-form-item label="다중 이미지 업로드">
        <MultiImageUpload v-model="picUrls" :limit="2" />
      </el-form-item>

      <el-form-item label="파일 업로드">
        <FileUpload v-model="fileUrls" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import MultiImageUpload from "@/components/Upload/MultiImageUpload.vue";

// 단일 이미지
const picUrl = ref("https://s2.loli.net/2023/05/24/yNsxFC8rLHMZQcK.jpg");
const picUrls = ref(["https://s2.loli.net/2023/05/24/yNsxFC8rLHMZQcK.jpg"]);

const fileUrls = ref([
  { name: "사진1.jpg", url: "https://s2.loli.net/2023/05/24/yNsxFC8rLHMZQcK.jpg" },
  { name: "사진2.jpg", url: "https://s2.loli.net/2023/05/24/RuHFMwW4rG5lIqs.jpg" },
]);
</script>
