import type { IModalConfig } from "@/components/CURD/types";
import { DeviceEnum } from "@/enums/settings/device-enum";
import { useAppStore } from "@/저장소";

const modalConfig: IModalConfig = {
  permP참조ix: "sys:user",
  component: "drawer",
  colon: true,
  pk: "id",
  drawer: {
    title: "수정사용자",
    size: useAppStore().device === DeviceEnum.MOBILE ? "80%" : 500,
  },
  form: { labelPosition: "right", labelWidth: "auto" },
  beforeSubmit(data) {
    console.log("beforeSubmit", data);
  },
  formAction(data) {
    // return UserAPI.update(data.id as string, data);
    // 模拟发起网络요청수정字段
    ElMessage.success(JSON.stringify(data));
    return Promise.resolve(null);
  },
  formItems: [
    {
      tips: { effect: "light", placement: "top", content: "사용자 정의텍스트提示" },
      type: "input",
      label: "文本",
      prop: "username",
      attrs: { placeholder: "입력해주세요", clearable: true },
    },
    {
      type: "input-number",
      label: "百分比",
      prop: "percent",
      attrs: { placeholder: "입력해주세요", controls: false },
      slotName: "suffix",
    },
    {
      type: "input-number",
      label: "货币符",
      prop: "price",
      attrs: { placeholder: "입력해주세요", controls: false },
      slotName: "p참조ix",
    },
    {
      type: "input",
      label: "链接",
      prop: "url",
      attrs: { placeholder: "입력해주세요", clearable: true },
    },
    {
      type: "icon-select",
      label: "链接",
      prop: "icon",
    },
    {
      type: "custom",
      label: "목록값",
      prop: "gender",
      slotName: "gender",
      attrs: { style: { width: "100%" } },
    },
    {
      type: "select",
      label: "사용자 정의",
      prop: "status",
      attrs: { placeholder: "全部", clearable: true },
      options: [
        { label: "启用", value: 1 },
        { label: "비활성화", value: 0 },
      ],
    },
    {
      type: "switch",
      label: "Switch",
      prop: "status2",
      attrs: {
        inlinePrompt: true,
        activeValue: 1,
        inactiveValue: 0,
        activeText: "启用",
        inactiveText: "비활성화",
      },
    },
    {
      type: "input-number",
      label: "输입框",
      prop: "sort",
      attrs: { placeholder: "입력해주세요", controls: false },
    },
    {
      type: "date-picker",
      label: "日期포맷",
      prop: "createTime",
      attrs: {
        type: "datetime",
        format: "YYYY/MM/DD hh:mm:ss",
        "value-format": "x",
      },
    },
  ],
};

export default reactive(modalConfig);
