import type { IContentConfig } from "@/components/CURD/types";

const contentConfig: IContentConfig = {
  // permPrefix: "sys:demo", // 작성하지 않으면 버튼 권한 검사를 수행하지 않음
  table: {
    showOverflowTooltip: true,
  },
  pagePosition: "right",
  toolbar: [],
  indexAction(params) {
    // 네트워크 요청을 시뮬레이션하여 목록 데이터 조회
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
    // 네트워크 요청을 시뮬레이션하여 필드 수정
    // console.log("modifyAction:", data);
    ElMessage.success(JSON.stringify(data));
    return Promise.resolve(null);
  },
  cols: [
    { type: "index", width: 50, align: "center" },
    { label: "ID", align: "center", prop: "id", show: false },
    { label: "텍스트", align: "center", prop: "username" },
    { label: "이미지", align: "center", prop: "avatar", templet: "image" },
    {
      label: "백분율",
      align: "center",
      prop: "percent",
      templet: "percent",
    },
    {
      label: "통화 기호",
      align: "center",
      prop: "price",
      templet: "price",
      priceFormat: "$",
    },
    { label: "링크", align: "center", prop: "url", width: 180, templet: "url" },
    { label: "아이콘", align: "center", prop: "icon", templet: "icon" },
    {
      label: "목록 값",
      align: "center",
      prop: "gender",
      templet: "list",
      selectList: { "0": "여", "1": "남" },
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
      activeText: "활성화",
      inactiveText: "비활성화",
    },
    {
      label: "입력창",
      align: "center",
      prop: "sort",
      templet: "input",
      inputType: "number",
    },
    {
      label: "날짜 포맷",
      align: "center",
      prop: "createTime",
      minWidth: 120,
      templet: "date",
      dateFormat: "YYYY/MM/DD HH:mm:ss",
    },
    {
      label: "작업",
      align: "center",
      fixed: "right",
      width: 220,
      templet: "tool",
      operat: [
        "view",
        "edit",
        {
          name: "delete",
          text: "삭제 표시",
          perm: "delete",
          attrs: { icon: "delete", type: "danger" },
          render(row) {
            // 조건에 따라 표시 또는 숨김
            return row.id !== 1;
          },
        },
      ],
    },
  ],
};

export default contentConfig;
