import UserAPI from "@/api/system/user-api";
import RoleAPI from "@/api/system/role-api";
import type { UserPageQuery } from "@/api/system/user-api";
import type { IContentConfig } from "@/components/CURD/types";

const contentConfig: IContentConfig<UserPageQuery> = {
  permPrefix: "sys:user", // 작성하지 않으면 버튼 권한 검사를 수행하지 않음
  table: {
    border: true,
    highlightCurrentRow: true,
  },
  pagination: {
    background: true,
    layout: "prev,pager,next,jumper,total,sizes",
    pageSize: 20,
    pageSizes: [10, 20, 30, 50],
  },
  parseData(res) {
    return {
      total: res.total,
      list: res.list,
    };
  },
  indexAction(params) {
    return UserAPI.getPage(params);
  },
  deleteAction: UserAPI.deleteByIds,
  importAction(file) {
    return UserAPI.import("1", file);
  },
  exportAction: UserAPI.export,
  importTemplate: UserAPI.downloadTemplate,
  importsAction(data) {
    // 데이터 가져오기 시뮬레이션
    console.log("importsAction", data);
    return Promise.resolve();
  },
  async exportsAction(params) {
    // 전체 데이터 조회 시뮬레이션
    const res = await UserAPI.getPage(params);
    console.log("exportsAction", res.list);
    return res.list;
  },
  pk: "id",
  toolbar: [
    "add",
    "delete",
    "import",
    "export",
    {
      name: "custom1",
      text: "사용자 정의 1",
      perm: "add",
      attrs: { icon: "plus", color: "#626AEF" },
    },
  ],
  defaultToolbar: ["refresh", "filter", "imports", "exports", "search"],
  cols: [
    { type: "selection", width: 50, align: "center" },
    { label: "번호", align: "center", prop: "id", width: 100, show: false },
    { label: "사용자이름", align: "center", prop: "username" },
    { label: "아바타", align: "center", prop: "avatar", templet: "image" },
    { label: "사용자닉네임", align: "center", prop: "nickname", width: 120 },
    {
      label: "성별",
      align: "center",
      prop: "gender",
      width: 100,
      templet: "custom",
      slotName: "gender",
    },
    { label: "부서", align: "center", prop: "deptName", width: 120 },
    {
      label: "역할",
      align: "center",
      prop: "roleNames",
      width: 120,
      columnKey: "roleIds",
      filters: [],
      filterMultiple: true,
      filterJoin: ",",
      async initFn(colItem) {
        const roleOptions = await RoleAPI.getOptions();
        colItem.filters = roleOptions.map((item) => {
          return { text: item.label, value: item.value };
        });
      },
    },
    {
      label: "휴대폰 번호",
      align: "center",
      prop: "mobile",
      templet: "custom",
      slotName: "mobile",
      width: 150,
    },
    {
      label: "상태",
      align: "center",
      prop: "status",
      templet: "custom",
      slotName: "status",
    },
    { label: "생성시간", align: "center", prop: "createTime", width: 180 },
    {
      label: "작업",
      align: "center",
      fixed: "right",
      width: 280,
      templet: "tool",
      operat: [
        {
          name: "detail",
          text: "상세",
          attrs: { icon: "Document", type: "primary" },
        },
        {
          name: "reset_pwd",
          text: "비밀번호 초기화",
          // perm: "password-reset",
          attrs: {
            icon: "refresh-left",
            // color: "#626AEF", // text 속성 사용 시, 색상이 적용되지 않음
            style: {
              "--el-button-text-color": "#626AEF",
              "--el-button-hover-link-text-color": "#9197f4",
            },
          },
        },
        "edit",
        "delete",
      ],
    },
  ],
};

export default contentConfig;
