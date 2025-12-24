import UserAPI from "@/api/system/user-api";
import RoleAPI from "@/api/system/role-api";
import type { UserPageQuery } from "@/api/system/user-api";
import type { IContentConfig } from "@/components/CURD/types";

const contentConfig: IContentConfig<UserPageQuery> = {
  permP참조ix: "sys:user", // 不쓰不进行버튼权限검사
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
    // 模拟가져오기데이터
    console.log("importsAction", data);
    return Promise.resolve();
  },
  async exportsAction(params) {
    // 模拟조회到의是全量데이터
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
    { label: "头像", align: "center", prop: "avatar", templet: "image" },
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
      label: "手机号码",
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
    { label: "创建시사이", align: "center", prop: "createTime", width: 180 },
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
          text: "초기화密码",
          // perm: "password-reset",
          attrs: {
            icon: "참조resh-left",
            // color: "#626AEF", // 사용 text 속성，颜色不生效
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
