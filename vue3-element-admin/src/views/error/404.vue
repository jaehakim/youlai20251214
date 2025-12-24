<template>
  <div class="wh-full mx-auto flex-center flex-col lg:flex-row">
    <img class="min-w-[23.4375rem] sm:w-150" src="@/assets/images/404.svg" alt="404" />
    <div class="w-75">
      <div class="oops mb-5 text-[2rem] font-bold">OOPS！</div>
      <div class="info text-gray mb-7 text-[0.8125rem]">
        이 페이지에 접근할 수 없습니다.
        <el-link type="primary" href="https://www.youlai.tech.com" target="_blank">
          유래이 오픈소스 공식 웹사이트
        </el-link>
      </div>
      <div class="headline mb-2.5 text-xl font-bold text-[#222]">죄송합니다, 방문하신 페이지가 존재하지 않습니다.</div>
      <div class="info text-gray mb-7 text-[0.8125rem]">
        입력한 주소가 올바른지 확인하거나 아래 버튼을 클릭하여 홈으로 돌아가세요.
      </div>
      <el-button round type="primary" class="btn h-9 w-28 mb-10" @click="back">홈으로 돌아가기</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({ name: "Page404" });
const router = useRouter();
const back = () => router.push("/");
</script>

<style lang="scss" scoped>
@mixin slide-up-animation($delay: 0s) {
  opacity: 0;
  animation-name: slideUp;
  animation-duration: 0.5s;
  animation-delay: $delay;
  animation-fill-mode: forwards;
}

@keyframes slideUp {
  0% {
    opacity: 0;
    transform: translateY(60px);
  }

  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

.oops {
  color: var(--el-color-primary);
  @include slide-up-animation(0s);
}

.headline {
  @include slide-up-animation(0.1s);
}

.info {
  @include slide-up-animation(0.2s);
}

.btn {
  @include slide-up-animation(0.3s);
}
</style>
