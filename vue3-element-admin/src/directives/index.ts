import type { App } from "vue";

import { hasPerm } from "./permission";

// 글로벌 directive 등록
export function setupDirective(app: App<Element>) {
  // v-hasPerm을 모든 컴포넌트에서 사용 가능하도록 함
  app.directive("hasPerm", hasPerm);
}
