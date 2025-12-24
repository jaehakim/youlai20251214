import { createApp } from "vue";
import App from "./App.vue";
import setupPlugins from "@/plugins";

// 어두운 테마 스타일
import "element-plus/theme-chalk/dark/css-vars.css";
import "vxe-table/lib/style.css";
// 어두운 모드 사용자 정의 변수
import "@/styles/dark/css-vars.css";
import "@/styles/index.scss";
import "uno.css";

// 전환 애니메이션
import "animate.css";

// 특정 기본 이벤트(예: touchstart, wheel 등)에 자동으로 { passive: true }를 추가하여 스크롤 성능을 향상시키고 콘솔의 수동이 아닌 이벤트 리스너 경고를 제거합니다.
import "default-passive-events";

const app = createApp(App);
// 플러그인 등록
app.use(setupPlugins);
app.mount("#app");
