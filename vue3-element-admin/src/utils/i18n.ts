// translate router.meta.title, be used in breadcrumb sidebar tagsview
import i18n from "@/lang/index";

export function translateRouteTitle(title: any) {
  // 국제화 설정이 있는지 확인, 없으면 원본 반환
  const hasKey = i18n.global.te("route." + title);
  if (hasKey) {
    const translatedTitle = i18n.global.t("route." + title);
    return translatedTitle;
  }
  return title;
}
