import type { App } from "vue";
import VXETable from "vxe-table"; // https://vxetable.cn/v4.6/#/table/start/install

// 글로벌 기본값 파라미터
VXETable.setConfig({
  // 글로벌 크기
  size: "medium",
  // 글로벌 zIndex 시작값, 프로젝트의 z-index 스타일 값이 크면 이보다 크게 설정해야 가려지지 않음
  zIndex: 9999,
  // 버전 번호, 데이터 캐시가 있는 일부 기능에서 사용됨, 버전 번호를 올리면 데이터 초기화에 사용할 수 있음
  version: 0,
  // 글로벌 loading 힌트 내용, null이면 텍스트 표시 안 함
  loadingText: null,
  table: {
    showHeader: true,
    showOverflow: "tooltip",
    showHeaderOverflow: "tooltip",
    autoResize: true,
    // stripe: false,
    border: "inner",
    // round: false,
    emptyText: "데이터 없음",
    rowConfig: {
      isHover: true,
      isCurrent: true,
      // 행 데이터의 유일한 기본 키 필드 이름
      keyField: "_VXE_ID",
    },
    columnConfig: {
      resizable: false,
    },
    align: "center",
    headerAlign: "center",
  },
  pager: {
    // size: "medium",
    // 매칭되는 스타일
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
  // Vxe Table 컴포넌트 완전히 가져오기
  app.use(VXETable);
}
