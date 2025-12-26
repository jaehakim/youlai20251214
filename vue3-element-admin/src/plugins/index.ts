import type { App } from "vue";

import { setupDirective } from "@/directives";
import { setupI18n } from "@/lang";
import { setupRouter } from "@/router";
import { setupStore } from "@/store";
import { setupElIcons } from "./icons";
import { setupPermission } from "./permission";
import { setupWebSocket } from "./websocket";
import { InstallCodeMirror } from "codemirror-editor-vue3";
import { setupVxeTable } from "./vxeTable";

export default {
  install(app: App<Element>) {
    // 사용자 정의 지시문(directive)
    setupDirective(app);
    // 라우팅(router)
    setupRouter(app);
    // 상태 관리(store)
    setupStore(app);
    // 국제화
    setupI18n(app);
    // Element-plus 아이콘
    setupElIcons(app);
    // 라우팅 가드
    setupPermission();
    // 웹소켓 서비스
    setupWebSocket();
    // vxe-table
    setupVxeTable(app);
    // CodeMirror 등록
    app.use(InstallCodeMirror);
  },
};
