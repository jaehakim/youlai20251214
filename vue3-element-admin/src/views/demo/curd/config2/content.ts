import type { IContentConfig } from "@/components/CURD/types";

const contentConfig: IContentConfig = {
  // permP참조ix: "sys:demo", // 不쓰不进行버튼权限검사
  table: {
    showOverflowTooltip: true,
  },
  pagePosition: "right",
  toolbar: [],
  indexAction(params) {
    // 模拟发起网络요청조회목록데이터
    console.log("indexAction:", params);
    return Promise.resolve({
      total: 2,
      list: [
        {
          id: 1,
          username: "root",
          avatar: "https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif",
          percent: 99,
          price: 10,
          url: "https://www.baidu.com",
          icon: "el-icon-setting",
          gender: 1,
          status: 1,
          status2: 1,
          sort: 99,
          createTime: 1715647982437,
        },
        {
          id: 2,
          username: "jerry",
          avatar: "https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif",
          percent: 88,
          price: 999,
          url: "https://www.google.com",
          icon: "el-icon-user",
          gender: 0,
          status: 0,
          status2: 0,
          sort: 0,
          createTime: 1715648977426,
        },
      ],
    });
  },
  modifyAction(data) {
    // 模拟发起网络요청수정字段
    // console.log("modifyAction:", data);
    ElMessage.success(JSON.stringify(data));
    return Promise.resolve(null);
  },
  cols: [
    { type: "index", width: 50, align: "center" },
    { label: "ID", align: "center", prop: "id", show: false },
    { label: "文本", align: "center", prop: "username" },
    { label: "그래프片", align: "center", prop: "avatar", templet: "image" },
    {
      label: "百分比",
      align: "center",
      prop: "percent",
      templet: "percent",
    },
    {
      label: "货币符",
      align: "center",
      prop: "price",
      templet: "price",
      priceFormat: "$",
    },
    { label: "链接", align: "center", prop: "url", width: 180, templet: "url" },
    { label: "아이콘", align: "center", prop: "icon", templet: "icon" },
    {
      label: "목록값",
      align: "center",
      prop: "gender",
      templet: "list",
      selectList: { "0": "女", "1": "男" },
    },
    {
      label: "사용자 정의",
      align: "center",
      prop: "status",
      templet: "custom",
      slotName: "status",
    },
    {
      label: "Switch",
      align: "center",
      prop: "status2",
      templet: "switch",
      activeValue: 1,
      inactiveValue: 0,
      activeText: "启用",
      inactiveText: "비활성화",
    },
    {
      label: "输입框",
      align: "center",
      prop: "sort",
      templet: "input",
      inputType: "number",
    },
    {
      label: "日期포맷",
      align: "center",
      prop: "createTime",
      minWidth: 120,
      templet: "date",
      dateFormat: "YYYY/MM/DD HH:mm:ss",
    },
    {
      label: "작업열",
      align: "center",
      fixed: "right",
      width: 220,
      templet: "tool",
      operat: [
        "view",
        "edit",
        {
          name: "delete",
          text: "展示삭제",
          perm: "delete",
          attrs: { icon: "delete", type: "danger" },
          render(row) {
            // 에 따라조건，显示或隐藏
            return row.id !== 1;
          },
        },
      ],
    },
  ],
};

export default contentConfig;
