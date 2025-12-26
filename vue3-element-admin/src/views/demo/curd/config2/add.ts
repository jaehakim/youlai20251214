import type { UserForm } from "@/api/system/user-api";
import type { IModalConfig } from "@/components/CURD/types";
import { deptArr } from "../config/options";

const modalConfig: IModalConfig<UserForm> = {
  colon: true,
  dialog: {
    title: "2차 팝업",
    width: 500,
    draggable: true,
  },
  form: {
    labelWidth: "auto",
    labelPosition: "top",
  },
  formItems: [
    {
      label: "사용자이름",
      prop: "username",
      rules: [{ required: true, message: "사용자 이름은 비워둘 수 없습니다", trigger: "blur" }],
      type: "input",
      attrs: { placeholder: "입력해주세요" },
    },
    {
      label: "사용자닉네임",
      prop: "nickname",
      rules: [{ required: true, message: "사용자 닉네임은 비워둘 수 없습니다", trigger: "blur" }],
      type: "input",
      attrs: { placeholder: "입력해주세요" },
    },
    {
      label: "소속부서",
      prop: "deptId",
      rules: [{ required: true, message: "소속 부서는 비워둘 수 없습니다", trigger: "change" }],
      type: "tree-select",
      attrs: {
        placeholder: "선택해주세요",
        data: deptArr,
        filterable: true,
        "check-strictly": true,
        "render-after-expand": false,
      },
    },
    {
      type: "custom",
      label: "성별",
      prop: "gender",
      initialValue: 1,
      attrs: { style: { width: "100%" } },
    },
  ],
};

// 비동기 데이터가 설정을 수정할 경우 reactive로 감싸는 것을 권장하며, 순수 정적 설정은 직접 내보내기 가능
export default reactive(modalConfig);
