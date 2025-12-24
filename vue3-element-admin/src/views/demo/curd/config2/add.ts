import type { UserForm } from "@/api/system/user-api";
import type { IModalConfig } from "@/components/CURD/types";
import { deptArr } from "../config/options";

const modalConfig: IModalConfig<UserForm> = {
  colon: true,
  dialog: {
    title: "二级弹窗",
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
      rules: [{ required: true, message: "사용자이름비어있을 수 없음비어있음", trigger: "blur" }],
      type: "input",
      attrs: { placeholder: "입력해주세요" },
    },
    {
      label: "사용자닉네임",
      prop: "nickname",
      rules: [{ required: true, message: "사용자닉네임비어있을 수 없음비어있음", trigger: "blur" }],
      type: "input",
      attrs: { placeholder: "입력해주세요" },
    },
    {
      label: "所属부서",
      prop: "deptId",
      rules: [{ required: true, message: "所属부서비어있을 수 없음비어있음", trigger: "change" }],
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

// 만약有비동기데이터会수정설정의，推荐用reactive패키지裹，而纯静态설정의可以直接내보내기
export default reactive(modalConfig);
