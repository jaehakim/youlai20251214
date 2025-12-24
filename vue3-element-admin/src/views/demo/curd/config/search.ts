import type { ISearchConfig } from "@/components/CURD/types";
import { deptArr, stateArr } from "./options";

const searchConfig: ISearchConfig = {
  permP참조ix: "sys:user",
  formItems: [
    {
      tips: "지원模糊검색",
      type: "input",
      label: "关키字",
      prop: "키words",
      attrs: {
        placeholder: "사용자이름/닉네임/휴대폰",
        clearable: true,
        style: { width: "200px" },
      },
    },
    {
      type: "tree-select",
      label: "부서",
      prop: "deptId",
      attrs: {
        placeholder: "선택해주세요",
        data: deptArr,
        filterable: true,
        "check-strictly": true,
        "render-after-expand": false,
        clearable: true,
        style: { width: "200px" },
      },
      // async initFn(formItem) {
      //   // 주의:만약initFn함수아님예箭头함수,this회의指에이설정항목객체,那么也就可以用this来替代形参formItem
      //   formItem.attrs.data = await DeptAPI.getOptions();
      // },
    },
    {
      type: "select",
      label: "상태",
      prop: "status",
      attrs: {
        placeholder: "全部",
        clearable: true,
        style: { width: "200px" },
      },
      options: stateArr,
    },
    {
      type: "date-picker",
      label: "생성시사이",
      prop: "createTime",
      attrs: {
        type: "daterange",
        "range-separator": "~",
        "start-placeholder": "시작시사이",
        "end-placeholder": "截止시사이",
        "value-format": "YYYY-MM-DD",
        style: { width: "200px" },
      },
    },
  ],
};

export default searchConfig;
