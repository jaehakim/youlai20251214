/**
 * 양식타입열거형
 */
export const FormTypeEnum: Record<string, OptionType> = {
  INPUT: { value: 1, label: "출력입프레임" },
  SELECT: { value: 2, label: "드롭다운" },
  RADIO: { value: 3, label: "단일선택프레임" },
  CHECK_BOX: { value: 4, label: "체크박스" },
  INPUT_NUMBER: { value: 5, label: "숫자출력입프레임" },
  SWITCH: { value: 6, label: "토글" },
  TEXT_AREA: { value: 7, label: "텍스트域" },
  DATE: { value: 8, label: "날짜프레임" },
  DATE_TIME: { value: 9, label: "날짜시사이프레임" },
  HIDDEN: { value: 10, label: "隐藏域" },
};
