import type { App } from "vue";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";

// 등록모든아이콘
export function setupElIcons(app: App<Element>) {
  for (const [키, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(키, component);
  }
}
