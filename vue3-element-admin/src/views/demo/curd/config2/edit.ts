import type { IModalConfig } from "@/components/CURD/types";
import { DeviceEnum } from "@/enums/settings/device-enum";
import { useAppStore } from "@/store";

const modalConfig: IModalConfig = {
  permPrefix: "sys:user",
  component: "drawer",
  colon: true,
  pk: "id",
  drawer: {
    title: "사용자 수정",
    size: useAppStore().device === DeviceEnum.MOBILE ? "80%" : 500,
  },
  form: { labelPosition: "right", labelWidth: "auto" },
  beforeSubmit(data) {
    console.log("beforeSubmit", data);
  },
  formAction(data) {
    // return UserAPI.update(data.id as string, data);
    // 네트워크 요청을 시뮬레이션하여 필드 수정
    ElMessage.success(JSON.stringify(data));
    return Promise.resolve(null);
  },
  formItems: [
    {
      tips: { effect: "light", placement: "top", content: "사용자 정의 텍스트 힌트" },
      type: "input",
      label: "텍스트",
      prop: "username",
      attrs: { placeholder: "입력해주세요", clearable: true },
    },
    {
      type: "input-number",
      label: "백분율",
      prop: "percent",
      attrs: { placeholder: "입력해주세요", controls: false },
      slotName: "suffix",
    },
    {
      type: "input-number",
      label: "통화 기호",
      prop: "price",
      attrs: { placeholder: "입력해주세요", controls: false },
      slotName: "prefix",
    },
    {
      type: "input",
      label: "링크",
      prop: "url",
      attrs: { placeholder: "입력해주세요", clearable: true },
    },
    {
      type: "icon-select",
      label: "아이콘",
      prop: "icon",
    },
    {
      type: "custom",
      label: "목록 값",
      prop: "gender",
      slotName: "gender",
      attrs: { style: { width: "100%" } },
    },
    {
      type: "select",
      label: "사용자 정의",
      prop: "status",
      attrs: { placeholder: "전체", clearable: true },
      options: [
        { label: "활성화", value: 1 },
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
        activeText: "활성화",
        inactiveText: "비활성화",
      },
    },
    {
      type: "input-number",
      label: "입력창",
      prop: "sort",
      attrs: { placeholder: "입력해주세요", controls: false },
    },
    {
      type: "date-picker",
      label: "날짜 포맷",
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
