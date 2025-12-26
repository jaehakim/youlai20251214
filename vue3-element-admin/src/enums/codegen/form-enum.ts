/**
 * 양식 타입 열거형
 */
export const FormTypeEnum: Record<string, OptionType> = {
  INPUT: { value: 1, label: "입력 필드" },
  SELECT: { value: 2, label: "드롭다운" },
  RADIO: { value: 3, label: "라디오 버튼" },
  CHECK_BOX: { value: 4, label: "체크박스" },
  INPUT_NUMBER: { value: 5, label: "숫자 입력 필드" },
  SWITCH: { value: 6, label: "토글" },
  TEXT_AREA: { value: 7, label: "텍스트 영역" },
  DATE: { value: 8, label: "날짜 선택기" },
  DATE_TIME: { value: 9, label: "날짜시간 선택기" },
  HIDDEN: { value: 10, label: "숨김 필드" },
};
