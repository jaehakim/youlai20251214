<!-- 사전 구성 요소 예제 -->
<template>
  <div class="app-container">
    <el-link
      href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/dictionary.vue"
      type="primary"
      target="_blank"
      class="mb-[20px]"
    >
      예제 소스 코드 클릭>>>>
    </el-link>
    <el-form>
      <el-form-item label="성별">
        <dict v-model="stringValue" code="gender" />
        <el-link underline="never" type="primary" class="ml-5">
          값: 문자열 const value = ref("1");
        </el-link>
      </el-form-item>

      <el-form-item label="성별">
        <dict v-model="numberValue" code="gender" />
        <el-link underline="never" type="success" class="ml-5">
          값: 숫자 const value = ref(1);
        </el-link>
      </el-form-item>

      <el-form-item label="라디오 버튼 사전">
        <dict v-model="numberValue" type="radio" code="gender" />
        <el-link underline="never" type="success" class="ml-5">
          값: 숫자 const value = ref(1);
        </el-link>
      </el-form-item>

      <el-form-item label="체크박스 사전">
        <dict v-model="arrayValue" type="checkbox" code="gender" />
        <el-link underline="never" type="success" class="ml-5">
          값: 배열 const value = ref(["1", "2"]);
        </el-link>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
const stringValue = ref("1"); // 성별(문자열 값)
const numberValue = ref(1); // 성별(숫자 값)
const arrayValue = ref(["1", "2"]); // 성별(배열 값)
</script>
