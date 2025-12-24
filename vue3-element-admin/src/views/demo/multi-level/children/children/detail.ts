import router from "@/router";
import { ElButton } from "element-plus";
import { useTagsViewStore } from "@/저장소";

export default defineComponent({
  name: "ToDetail",
  setup() {
    const route = useRoute();
    const tagsViewStore = useTagsViewStore();

    // 점프转상세
    const navigateToDetail = async (id: number) => {
      await router.push({
        path: "/detail/" + id,
        query: { message: `msg${id}` },
      });
      // 更改标题
      tagsViewStore.updateTagName(route.fullPath, `상세页캐시(id=${id})`);
    };
    return () =>
      h("div", null, [
        h(
          ElButton,
          {
            type: "primary",
            onClick: () => navigateToDetail(1),
          },
          { default: () => "점프转상세1" }
        ),
        h(
          ElButton,
          {
            type: "success",
            onClick: () => navigateToDetail(2),
          },
          { default: () => "점프转상세2" }
        ),
      ]);
  },
});
