import type { App } from "vue";
import { createI18n } from "vue-i18n";
import { useAppStoreHook } from "@/store/modules/app-store";
// 로컬언어패키지
import enLocale from "./package/en.json";
import zhCnLocale from "./package/zh-cn.json";

const appStore = useAppStoreHook();

const messages = {
  "zh-cn": zhCnLocale,
  en: enLocale,
};

const i18n = createI18n({
  legacy: false,
  locale: appStore.language,
  messages,
  globalInjection: true,
});

// 글로벌등록 i18n
export function setupI18n(app: App<Element>) {
  app.use(i18n);
}

export default i18n;
