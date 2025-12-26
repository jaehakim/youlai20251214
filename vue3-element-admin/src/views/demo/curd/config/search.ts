import type { ISearchConfig } from "@/components/CURD/types";
import { deptArr, stateArr } from "./options";

const searchConfig: ISearchConfig = {
  permPrefix: "sys:user",
  formItems: [
    {
      tips: "퍼지 검색 지원",
      type: "input",
      label: "키워드",
      prop: "keywords",
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
      //   // 주의: initFn 함수가 화살표 함수가 아닌 경우, this는 이 설정 항목 객체를 가리키므로 형식 매개변수 formItem 대신 this를 사용할 수 있음
      //   formItem.attrs.data = await DeptAPI.getOptions();
      // },
    },
    {
      type: "select",
      label: "상태",
      prop: "status",
      attrs: {
        placeholder: "전체",
        clearable: true,
        style: { width: "200px" },
      },
      options: stateArr,
    },
    {
      type: "date-picker",
      label: "생성시간",
      prop: "createTime",
      attrs: {
        type: "daterange",
        "range-separator": "~",
        "start-placeholder": "시작시간",
        "end-placeholder": "종료시간",
        "value-format": "YYYY-MM-DD",
        style: { width: "200px" },
      },
    },
  ],
};

export default searchConfig;
