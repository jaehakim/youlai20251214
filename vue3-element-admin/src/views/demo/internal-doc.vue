<template>
  <div class="app-container">
    <iframe src="https://juejin.cn/post/7228990409909108793" frameborder="0" />
  </div>
</template>
<style lang="scss" scoped>
/** 태그 태그 비활성화  */
.app-container {
  /* 50px = 네비게이션 바 = 50px */
  height: calc(100vh - 50px);
}

/** 태그 태그 활성화  */
.hasTagsView {
  .app-container {
    /* 84px = 네비게이션 바 + 태그 보기 = 50px + 34px */
    height: calc(100vh - 84px);
  }
}

iframe {
  width: 100%;
  height: 100%;
}
</style>
