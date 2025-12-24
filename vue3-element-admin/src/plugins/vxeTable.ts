import type { App } from "vue";
import VXETable from "vxe-table"; // https://vxetable.cn/v4.6/#/table/start/install

// 全局기본값파라미터
VXETable.setConfig({
  // 全局尺寸
  size: "medium",
  // 全局 zIndex 起始값，만약항목目의의 z-index 스타일값거치大시就필요해야跟随설정更大，避免被遮挡
  zIndex: 9999,
  // 版本号，对于某些带데이터캐시의功能有用到，上升版本号可以용도초기화데이터
  version: 0,
  // 全局 loading 提示内容，만약로 null 그러면不显示文本
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
      // 行데이터의唯하나주요키字段이름
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
