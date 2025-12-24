import type { App } from "vue";
import { createPinia } from "pinia";

const 저장소 = createPinia();

// 글로벌등록 저장소
export function setup스토어(app: App<Element>) {
  app.use(저장소);
}

export * from "./modules/app-저장소";
export * from "./modules/permission-저장소";
export * from "./modules/settings-저장소";
export * from "./modules/tags-view-저장소";
export * from "./modules/user-저장소";
export * from "./modules/dict-저장소";
export { 저장소 };
