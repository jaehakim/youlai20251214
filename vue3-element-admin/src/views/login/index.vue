<template>
  <div class="login-container">
    <!-- 오른쪽 테마, 언어 전환 버튼 -->
    <div class="action-bar">
      <el-tooltip :content="t('login.themeToggle')" placement="bottom">
        <CommonWrapper>
          <DarkModeSwitch />
        </CommonWrapper>
      </el-tooltip>
      <el-tooltip :content="t('login.languageToggle')" placement="bottom">
        <CommonWrapper>
          <LangSelect size="text-20px" />
        </CommonWrapper>
      </el-tooltip>
    </div>
    <!-- 로그인 페이지 본문 -->
    <div flex-1 flex-center>
      <div
        class="p-4xl w-full h-auto sm:w-450px sm:h-700px shadow-[var(--el-box-shadow-light)] border-rd-2"
      >
        <div w-full flex flex-col items-center>
          <!-- 로고 -->
          <el-image :src="logo" style="width: 84px" />

          <!-- 제목 -->
          <h2>
            <el-badge :value="`v ${defaultSettings.version}`" type="success">
              {{ defaultSettings.title }}
            </el-badge>
          </h2>

          <!-- 컴포넌트 전환 -->
          <transition name="fade-slide" mode="out-in">
            <component :is="formComponents[component]" v-model="component" class="w-90%" />
          </transition>
        </div>
      </div>
      <!-- 로그인 페이지 하단 저작권 -->
      <el-text size="small" class="py-2.5! fixed bottom-0 text-center">
        Copyright © 2021 - 2025 youlai.tech All Rights Reserved.
        <a href="http://beian.miit.gov.cn/" target="_blank">ICP 등록번호: 皖ICP备20006496号-2</a>
      </el-text>
    </div>
  </div>
</template>

<script setup lang="ts">
import logo from "@/assets/logo.png";
import { defaultSettings } from "@/settings";
import CommonWrapper from "@/components/CommonWrapper/index.vue";
import DarkModeSwitch from "@/components/DarkModeSwitch/index.vue";

type LayoutMap = "login" | "register" | "resetPwd";

const t = useI18n().t;

const component = ref<LayoutMap>("login"); // 표시할 컴포넌트 전환
const formComponents = {
  login: defineAsyncComponent(() => import("./components/Login.vue")),
  register: defineAsyncComponent(() => import("./components/Register.vue")),
  resetPwd: defineAsyncComponent(() => import("./components/ResetPwd.vue")),
};

// 투표 알림
const voteUrl = "https://gitee.com/activity/2025opensource?ident=I6VXEH";
// 알림 인스턴스 저장, 컴포넌트 언마운트 시 닫기 위함
let notificationInstance: ReturnType<typeof ElNotification> | null = null;

// 투표 알림 표시
const showVoteNotification = () => {
  notificationInstance = ElNotification({
    title: "⭐ Gitee 2025 오픈소스 선정 · 여러분의 지원을 정성으로 기다립니다! 🙏",
    message: `저는 Gitee 2025 가장 인기 있는 오픈소스 소프트웨어 투표 활동에 참가하고 있습니다. 저에게 투표해주세요!<br/><a href="${voteUrl}" target="_blank" style="color: var(--el-color-primary); text-decoration: none; font-weight: 500;">투표하기 →</a>`,
    type: "success",
    position: "bottom-right",
    duration: 0,
    dangerouslyUseHTMLString: true,
  });
};

// 지연 표시
onMounted(() => {
  setTimeout(() => {
    showVoteNotification();
  }, 500);
});

// 컴포넌트 언마운트 시 알림 닫기
onBeforeUnmount(() => {
  if (notificationInstance) {
    notificationInstance.close();
    notificationInstance = null;
  }
});
</script>

<style lang="scss" scoped>
.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

// 배경 레이어로 의사 요소 추가
.login-container::before {
  position: fixed;
  top: 0;
  left: 0;
  z-index: -1;
  width: 100%;
  height: 100%;
  content: "";
  background: url("@/assets/images/login-bg.svg");
  background-position: center center;
  background-size: cover;
}

.action-bar {
  position: fixed;
  top: 10px;
  right: 10px;
  z-index: 10;
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
  font-size: 1.125rem;

  @media (max-width: 480px) {
    top: 10px;
    right: auto;
    left: 10px;
  }

  @media (min-width: 640px) {
    top: 40px;
    right: 40px;
  }
}

/* fade-slide 애니메이션 */
.fade-slide-leave-active,
.fade-slide-enter-active {
  transition: all 0.3s;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
