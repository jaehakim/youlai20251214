import type { App } from "vue";
import VXETable from "vxe-table"; // https://vxetable.cn/v4.6/#/table/start/install

// 글로벌기본값파라미터
VXETable.setConfig({
  // 글로벌尺寸
  size: "medium",
  // 글로벌 zIndex 起始값，만약항목目의의 z-index 스타일값거치大시就필요해야跟随설정更大，避免被遮挡
  zIndex: 9999,
  // 버전 번호，对于某些带데이터캐시의기능있음用到，上升버전 번호可以용도초기화데이터
  version: 0,
  // 글로벌 loading 提示콘텐츠，만약로 null 그러면아님표시텍스트
  loadingText: null,
  table: {
    showHeader: true,
    showOverflow: "tooltip",
    showHeaderOverflow: "tooltip",
    autoResize: true,
    // stripe: false,
    border: "inner",
    // round: false,
    emptyText: "暂无데이터",
    rowConfig: {
      isHover: true,
      isCurrent: true,
      // 행데이터의唯하나주요키필드이름
      키Field: "_VXE_ID",
    },
    columnConfig: {
      resizable: false,
    },
    align: "center",
    headerAlign: "center",
  },
  pager: {
    // size: "medium",
    // 配套의스타일
    perfect: false,
    pageSize: 10,
    pagerCount: 7,
    pageSizes: [10, 20, 50],
    layouts: [
      "Total",
      "PrevJump",
      "PrevPage",
      "Number",
      "NextPage",
      "NextJump",
      "Sizes",
      "FullJump",
    ],
  },
  modal: {
    minWidth: 500,
    minHeight: 400,
    lockView: true,
    mask: true,
    // duration: 3000,
    // marginSize: 20,
    dblclickZoom: false,
    showTitleOverflow: true,
    transfer: true,
    draggable: false,
  },
});

export function setupVxeTable(app: App) {
  // Vxe Table 컴포넌트完整引입
  app.use(VXETable);
}
