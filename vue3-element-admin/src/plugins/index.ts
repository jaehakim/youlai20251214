import type { App } from "vue";

import { setupDirective } from "@/directives";
import { setupI18n } from "@/lang";
import { setupRouter } from "@/router";
import { setup스토어 } from "@/저장소";
import { setupElIcons } from "./icons";
import { setupPermission } from "./permission";
import { setup웹소켓 } from "./websocket";
import { InstallCodeMirror } from "codemirror-editor-vue3";
import { setupVxeTable } from "./vxeTable";

export default {
  install(app: App<Element>) {
    // 사용자 정의지시문(directive)
    setupDirective(app);
    // 라우팅(router)
    setupRouter(app);
    // 상태 관리(저장소)
    setup스토어(app);
    // 국제화
    setupI18n(app);
    // Element-plus아이콘
    setupElIcons(app);
    // 라우팅守卫
    setupPermission();
    // 웹소켓서비스
    setup웹소켓();
    // vxe-table
    setupVxeTable(app);
    // 등록 CodeMirror
    app.use(InstallCodeMirror);
  },
};
