import router from "@/router";
import { ElButton } from "element-plus";
import { useTagsViewStore } from "@/store";

export default defineComponent({
  name: "ToDetail",
  setup() {
    const route = useRoute();
    const tagsViewStore = useTagsViewStore();

    // 상세로 이동
    const navigateToDetail = async (id: number) => {
      await router.push({
        path: "/detail/" + id,
        query: { message: `msg${id}` },
      });
      // 제목 변경
      tagsViewStore.updateTagName(route.fullPath, `상세 페이지 캐시(id=${id})`);
    };
    return () =>
      h("div", null, [
        h(
          ElButton,
          {
            type: "primary",
            onClick: () => navigateToDetail(1),
          },
          { default: () => "상세1로 이동" }
        ),
        h(
          ElButton,
          {
            type: "success",
            onClick: () => navigateToDetail(2),
          },
          { default: () => "상세2로 이동" }
        ),
      ]);
  },
});
