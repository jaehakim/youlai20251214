/**
 * 양식타입열거형
 */
export const FormTypeEnum: Record<string, OptionType> = {
  INPUT: { value: 1, label: "输입框" },
  SELECT: { value: 2, label: "下拉框" },
  RADIO: { value: 3, label: "단일选框" },
  CHECK_BOX: { value: 4, label: "复选框" },
  INPUT_NUMBER: { value: 5, label: "숫자输입框" },
  SWITCH: { value: 6, label: "开关" },
  TEXT_AREA: { value: 7, label: "文本域" },
  DATE: { value: 8, label: "日期框" },
  DATE_TIME: { value: 9, label: "日期시사이框" },
  HIDDEN: { value: 10, label: "隐藏域" },
};
