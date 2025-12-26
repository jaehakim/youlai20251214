import type { App } from "vue";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";

// 모든 아이콘 등록
export function setupElIcons(app: App<Element>) {
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
  }
}
