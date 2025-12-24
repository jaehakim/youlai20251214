import type { ISearchConfig } from "@/components/CURD/types";
import { deptArr, stateArr } from "./options";

const searchConfig: ISearchConfig = {
  permP참조ix: "sys:user",
  formItems: [
    {
      tips: "支持模糊검색",
      type: "input",
      label: "关키字",
      prop: "키words",
      attrs: {
        placeholder: "사용자이름/닉네임/手机号",
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
      //   // 注意:만약initFn함수不是箭头함수,this会指에此설정항목객체,那么也就可以用this来替代形参formItem
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
      label: "创建시사이",
      prop: "createTime",
      attrs: {
        type: "daterange",
        "range-separator": "~",
        "start-placeholder": "开始시사이",
        "end-placeholder": "截止시사이",
        "value-format": "YYYY-MM-DD",
        style: { width: "200px" },
      },
    },
  ],
};

export default searchConfig;
