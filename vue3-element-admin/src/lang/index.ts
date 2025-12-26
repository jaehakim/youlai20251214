import type { App } from "vue";
import { createI18n } from "vue-i18n";
import { useApp스토어Hook } from "@/저장소/modules/app-저장소";
// 로컬언어패키지
import enLocale from "./package/en.json";
import zhCnLocale from "./package/zh-cn.json";

const app스토어 = useApp스토어Hook();

const messages = {
  "zh-cn": zhCnLocale,
  en: enLocale,
};

const i18n = createI18n({
  legacy: false,
  locale: app스토어.language,
  messages,
  globalInjection: true,
});

// 글로벌등록 i18n
export function setupI18n(app: App<Element>) {
  app.use(i18n);
}

export default i18n;
