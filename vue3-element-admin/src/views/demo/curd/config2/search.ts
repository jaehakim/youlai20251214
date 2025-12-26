import type { ISearchConfig } from "@/components/CURD/types";
import { deptArr, stateArr } from "../config/options";

const searchConfig: ISearchConfig = {
  grid: "right",
  colon: true,
  showNumber: 3,
  form: { labelPosition: "right", labelWidth: "90px" },
  cardAttrs: { shadow: "hover", style: { "margin-bottom": "12px" } },
  formItems: [
    {
      tips: { effect: "light", placement: "top", content: "사용자 정의 텍스트 힌트" },
      type: "input",
      label: "입력창",
      prop: "testInput",
      attrs: { placeholder: "입력해주세요", clearable: true },
      events: {
        change: (e) => {
          console.log("입력창 값: ", e);
          // 연쇄 작업 예제, reactive를 사용하여 배열을 미리 정의해야 함
          // selectOptions.push({ label: e, value: e });
        },
      },
    },
    {
      type: "input-number",
      label: "숫자 입력창",
      prop: "testInputNumber",
      attrs: { placeholder: "입력해주세요", controls: false },
    },
    {
      type: "select",
      label: "드롭다운 선택창",
      prop: "testSelect",
      attrs: { placeholder: "전체", clearable: true },
      options: stateArr as any,
      events: {
        change(e) {
          console.log("선택한 값: ", e);
        },
      },
    },
    {
      type: "tree-select",
      label: "트리 선택창",
      prop: "testTreeSelect",
      attrs: {
        placeholder: "선택해주세요",
        data: deptArr,
        filterable: true,
        "check-strictly": true,
        "render-after-expand": false,
        clearable: true,
      },
      // async initFn(formItem) {
      //   // 주의: initFn 함수가 화살표 함수가 아닌 경우, this는 이 설정 항목 객체를 가리키므로 형식 매개변수 formItem 대신 this를 사용할 수 있음
      //   formItem.attrs.data = await DeptAPI.getOptions();
      // },
    },
    {
      type: "cascader",
      label: "연쇄 선택기",
      prop: "testCascader",
      attrs: {
        placeholder: "선택해주세요",
        clearable: true,
        props: {
          expandTrigger: "hover",
          label: "label",
          value: "value",
          children: "children",
        },
        options: [
          {
            value: "guide",
            label: "Guide",
            children: [
              {
                value: "disciplines",
                label: "Disciplines",
                children: [
                  {
                    value: "consistency",
                    label: "Consistency",
                  },
                ],
              },
              {
                value: "navigation",
                label: "Navigation",
                children: [
                  {
                    value: "side nav",
                    label: "Side Navigation",
                  },
                ],
              },
            ],
          },
        ],
      },
    },
    {
      type: "date-picker",
      label: "범위 선택기",
      prop: "createAt",
      attrs: {
        type: "daterange",
        "range-separator": "~",
        "start-placeholder": "시작시간",
        "end-placeholder": "종료시간",
        "value-format": "YYYY-MM-DD",
      },
    },
    {
      type: "date-picker",
      label: "날짜 선택기",
      prop: "testDataPicker",
      attrs: { placeholder: "선택해주세요", type: "date" },
    },
    {
      type: "time-picker",
      label: "시간 선택기",
      prop: "testTimePicker",
      attrs: { placeholder: "선택해주세요", clearable: true },
    },
    {
      type: "time-select",
      label: "시간 선택",
      prop: "testTimeSelect",
      attrs: { placeholder: "선택해주세요", clearable: true },
    },
    {
      type: "input-tag",
      label: "태그 선택기",
      prop: "testInputTags",
      attrs: { placeholder: "선택해주세요", clearable: true },
    },
    {
      type: "custom-tag",
      label: "태그 선택기",
      prop: "testCustomTags",
      attrs: {
        buttonAttrs: { btnText: "+ New Tag" },
        inputAttrs: {},
        tagAttrs: {},
      },
    },
  ],
};

export default searchConfig;
