import UserAPI from "@/api/system/user-api";
import RoleAPI from "@/api/system/role-api";
import type { UserPageQuery } from "@/api/system/user-api";
import type { IContentConfig } from "@/components/CURD/types";

const contentConfig: IContentConfig<UserPageQuery> = {
  permP참조ix: "sys:user", // 아님쓰아님进행버튼권한검사
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
  index액션(params) {
    return UserAPI.getPage(params);
  },
  delete액션: UserAPI.deleteByIds,
  import액션(file) {
    return UserAPI.import("1", file);
  },
  export액션: UserAPI.export,
  importTemplate: UserAPI.downloadTemplate,
  imports액션(data) {
    // 模拟가져오기데이터
    console.log("imports액션", data);
    return Promise.resolve();
  },
  async exports액션(params) {
    // 模拟조회到의예全양데이터
    const res = await UserAPI.getPage(params);
    console.log("exports액션", res.list);
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
      text: "사용자 정의1",
      perm: "add",
      attrs: { icon: "plus", color: "#626AEF" },
    },
  ],
  defaultToolbar: ["참조resh", "filter", "imports", "exports", "search"],
  cols: [
    { type: "selection", width: 50, align: "center" },
    { label: "编号", align: "center", prop: "id", width: 100, show: false },
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
    { label: "생성시사이", align: "center", prop: "createTime", width: 180 },
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
          text: "초기화비밀번호",
          // perm: "password-reset",
          attrs: {
            icon: "참조resh-left",
            // color: "#626AEF", // 사용 text 속성，색상아님生效
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
