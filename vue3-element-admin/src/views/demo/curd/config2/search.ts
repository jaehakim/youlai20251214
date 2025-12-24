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
      tips: { effect: "light", placement: "top", content: "사용자 정의텍스트提示" },
      type: "input",
      label: "输입框",
      prop: "testInput",
      attrs: { placeholder: "입력해주세요", clearable: true },
      events: {
        change: (e) => {
          console.log("输입框의값: ", e);
          // 级联작업示例，필요해야사용reactive提前定义배열
          // selectOptions.push({ label: e, value: e });
        },
      },
    },
    {
      type: "input-number",
      label: "숫자输입框",
      prop: "testInputNumber",
      attrs: { placeholder: "입력해주세요", controls: false },
    },
    {
      type: "select",
      label: "下拉선택框",
      prop: "testSelect",
      attrs: { placeholder: "全部", clearable: true },
      options: stateArr as any,
      events: {
        change(e) {
          console.log("选의값: ", e);
        },
      },
    },
    {
      type: "tree-select",
      label: "트리形선택框",
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
      //   // 注意:만약initFn함수不是箭头함수,this会指에此설정항목객체,那么也就可以用this来替代形参formItem
      //   formItem.attrs.data = await DeptAPI.getOptions();
      // },
    },
    {
      type: "cascader",
      label: "级联선택자",
      prop: "testCascader",
      attrs: {
        placeholder: "선택해주세요",
        clearable: true,
        속성: {
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
      label: "범위선택자",
      prop: "createAt",
      attrs: {
        type: "daterange",
        "range-separator": "~",
        "start-placeholder": "开始시사이",
        "end-placeholder": "截止시사이",
        "value-format": "YYYY-MM-DD",
      },
    },
    {
      type: "date-picker",
      label: "日期선택자",
      prop: "testDataPicker",
      attrs: { placeholder: "선택해주세요", type: "date" },
    },
    {
      type: "time-picker",
      label: "시사이선택자",
      prop: "testTimePicker",
      attrs: { placeholder: "선택해주세요", clearable: true },
    },
    {
      type: "time-select",
      label: "시사이선택",
      prop: "testTimeSelect",
      attrs: { placeholder: "선택해주세요", clearable: true },
    },
    {
      type: "input-tag",
      label: "标签선택자",
      prop: "testInputTags",
      attrs: { placeholder: "선택해주세요", clearable: true },
    },
    {
      type: "custom-tag",
      label: "标签선택자",
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
