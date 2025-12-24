import type { App } from "vue";

import { hasPerm } from "./permission";

// 全局등록 directive
export function setupDirective(app: App<Element>) {
  // 使 v-hasPerm 에모든컴포넌트내都可用
  app.directive("hasPerm", hasPerm);
}
