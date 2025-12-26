import UserAPI, { type UserForm } from "@/api/system/user-api";
import type { IModalConfig } from "@/components/CURD/types";
import { deptArr, roleArr } from "./options";

const modalConfig: IModalConfig<UserForm> = {
  permPrefix: "sys:user",
  dialog: {
    title: "신규 사용자",
    width: 800,
    draggable: true,
  },
  form: {
    labelWidth: 100,
  },
  formAction: UserAPI.create,
  beforeSubmit(data) {
    console.log("제출 전 처리", data);
  },
  formItems: [
    {
      label: "사용자이름",
      prop: "username",
      rules: [{ required: true, message: "사용자 이름은 비워둘 수 없습니다", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "사용자 이름을 입력해주세요",
      },
      col: {
        xs: 24,
        sm: 12,
      },
    },
    {
      label: "사용자닉네임",
      prop: "nickname",
      rules: [{ required: true, message: "사용자 닉네임은 비워둘 수 없습니다", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "사용자 닉네임을 입력해주세요",
      },
      col: {
        xs: 24,
        sm: 12,
      },
    },
    {
      label: "소속부서",
      prop: "deptId",
      rules: [{ required: true, message: "소속 부서는 비워둘 수 없습니다", trigger: "change" }],
      type: "tree-select",
      attrs: {
        placeholder: "소속 부서를 선택해주세요",
        data: deptArr,
        filterable: true,
        "check-strictly": true,
        "render-after-expand": false,
      },
      // async initFn(formItem) {
      //   // 주의: initFn 함수가 화살표 함수가 아닌 경우, this는 이 설정 항목 객체를 가리키므로 형식 매개변수 formItem 대신 this를 사용할 수 있음
      //   formItem.attrs.data = await DeptAPI.getOptions();
      // },
    },
    {
      type: "custom",
      label: "성별",
      prop: "gender",
      initialValue: 1,
      attrs: { style: { width: "100%" } },
    },
    {
      label: "역할",
      prop: "roleIds",
      rules: [{ required: true, message: "사용자 역할은 비워둘 수 없습니다", trigger: "change" }],
      type: "select",
      attrs: {
        placeholder: "선택해주세요",
        multiple: true,
      },
      options: roleArr,
      initialValue: [],
      // async initFn(formItem) {
      //   formItem.options = await RoleAPI.getOptions();
      // },
    },
    {
      type: "input",
      label: "휴대폰 번호",
      prop: "mobile",
      rules: [
        {
          pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
          message: "올바른 휴대폰 번호를 입력해주세요",
          trigger: "blur",
        },
      ],
      attrs: {
        placeholder: "휴대폰 번호를 입력해주세요",
        maxlength: 11,
      },
    },
    {
      label: "이메일",
      prop: "email",
      rules: [
        {
          pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/,
          message: "올바른 이메일 주소를 입력해주세요",
          trigger: "blur",
        },
      ],
      type: "input",
      attrs: {
        placeholder: "이메일을 입력해주세요",
        maxlength: 50,
      },
    },
    {
      label: "상태",
      prop: "status",
      type: "radio",
      options: [
        { label: "활성화", value: 1 },
        { label: "비활성화", value: 0 },
      ],
      initialValue: 1,
    },
    {
      type: "custom",
      label: "2차 팝업",
      prop: "openModal",
      slotName: "openModal",
    },
  ],
};

// 비동기 데이터가 설정을 수정할 경우 reactive로 감싸는 것을 권장하며, 순수 정적 설정은 직접 내보내기 가능
export default reactive(modalConfig);
