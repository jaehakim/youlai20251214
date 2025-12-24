<template>
  <div class="p-5 pb-0 sm:p-10 lg:p-20 w-full">
    <el-button icon="arrow-left" @click="router.back()">뒤로 가기</el-button>
    <el-row>
      <el-col :xs="24" :span="12">
        <h1 class="text-6xl font-bold text-[#484848]">Oops!</h1>
        <h2>이 페이지에 접근할 권한이 없습니다</h2>
        <h6>불만이 있으면 직급자에게 문의하세요</h6>
        <div class="flex flex-col items-start gap-1.5 text-sm">
          <span>또는 다음으로 이동할 수 있습니다:</span>
          <el-link type="primary" @click="router.push('/dashboard')">홈으로</el-link>
          <el-link type="primary" href="https://www.youlai.tech/">둘러보기</el-link>
        </div>
      </el-col>
      <el-col :xs="24" :span="12">
        <img src="@/assets/images/401.svg" class="w-full" />
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: "Page401" });
const router = useRouter();
</script>
