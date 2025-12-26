import UserAPI from "@/api/system/user-api";
import type { ISelectConfig } from "@/components/TableSelect/index.vue";

const selectConfig: ISelectConfig = {
  pk: "id",
  width: "70%",
  placeholder: "사용자를 선택해주세요",
  formItems: [
    {
      type: "input",
      label: "키워드",
      prop: "keywords",
      attrs: {
        placeholder: "사용자이름/닉네임/휴대폰",
        clearable: true,
        style: {
          width: "200px",
        },
      },
    },
    {
      type: "tree-select",
      label: "부서",
      prop: "deptId",
      attrs: {
        placeholder: "선택해주세요",
        data: [
          {
            value: 1,
            label: "유래기술",
            children: [
              {
                value: 2,
                label: "연구개발부서",
              },
              {
                value: 3,
                label: "테스트부서",
              },
            ],
          },
        ],
        filterable: true,
        "check-strictly": true,
        "render-after-expand": false,
        clearable: true,
        style: {
          width: "150px",
        },
      },
    },
    {
      type: "select",
      label: "상태",
      prop: "status",
      attrs: {
        placeholder: "전체",
        clearable: true,
        style: {
          width: "100px",
        },
      },
      options: [
        { label: "활성화", value: 1 },
        { label: "비활성화", value: 0 },
      ],
    },
    {
      type: "date-picker",
      label: "생성시간",
      prop: "createAt",
      attrs: {
        type: "daterange",
        "range-separator": "~",
        "start-placeholder": "시작시간",
        "end-placeholder": "종료시간",
        "value-format": "YYYY-MM-DD",
        style: {
          width: "240px",
        },
      },
    },
  ],
  indexAction(params) {
    if ("createAt" in params) {
      const createAt = params.createAt as string[];
      if (createAt?.length > 1) {
        params.startTime = createAt[0];
        params.endTime = createAt[1];
      }
      delete params.createAt;
    }
    return UserAPI.getPage(params);
  },
  tableColumns: [
    { type: "selection", width: 50, align: "center" },
    { label: "번호", align: "center", prop: "id", width: 100 },
    { label: "사용자이름", align: "center", prop: "username" },
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
    { label: "휴대폰 번호", align: "center", prop: "mobile", width: 120 },
    {
      label: "상태",
      align: "center",
      prop: "status",
      templet: "custom",
      slotName: "status",
    },
    { label: "생성시간", align: "center", prop: "createTime", width: 180 },
  ],
};

export default selectConfig;
