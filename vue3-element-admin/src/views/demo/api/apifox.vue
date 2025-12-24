<!-- API 문서 -->
<template>
  <div class="app-container">
    <iframe
      src="https://www.apifox.cn/apidoc/shared-195e783f-4d85-4235-a038-eec696de4ea5"
      width="100%"
      height="100%"
      frameborder="0"
    />
  </div>
</template>

<style lang="scss" scoped>
/** 태그 태그 비활성화  */
.app-container {
  /* 50px = navbar = 50px */
  height: calc(100vh - 50px);
}

/** 태그 태그 활성화  */
.hasTagsView {
  .app-container {
    /* 84px = navbar + tags-view = 50px + 34px */
    height: calc(100vh - 84px);
  }
}
</style>
