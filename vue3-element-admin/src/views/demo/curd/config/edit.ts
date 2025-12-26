import UserAPI, { type UserForm } from "@/api/system/user-api";
import type { IModalConfig } from "@/components/CURD/types";
import { DeviceEnum } from "@/enums/settings/device-enum";
import { useAppStore } from "@/store";
import { deptArr, roleArr } from "./options";

const modalConfig: IModalConfig<UserForm> = {
  permPrefix: "sys:user",
  component: "drawer",
  drawer: {
    title: "사용자 수정",
    size: useAppStore().device === DeviceEnum.MOBILE ? "80%" : 500,
  },
  pk: "id",
  beforeSubmit(data) {
    console.log("beforeSubmit", data);
  },
  formAction(data) {
    return UserAPI.update(data.id as string, data);
  },
  formItems: [
    {
      label: "사용자이름",
      prop: "username",
      rules: [{ required: true, message: "사용자 이름은 비워둘 수 없습니다", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "사용자 이름을 입력해주세요",
        readonly: true,
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
    },
    {
      label: "소속부서",
      prop: "deptId",
      rules: [{ required: true, message: "소속 부서는 비워둘 수 없습니다", trigger: "blur" }],
      type: "tree-select",
      attrs: {
        placeholder: "소속 부서를 선택해주세요",
        data: deptArr, // setup에서 Vue가 자동으로 ref를 언래핑하므로 .value 불필요
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
      rules: [{ required: true, message: "사용자 역할은 비워둘 수 없습니다", trigger: "blur" }],
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
      type: "switch",
      attrs: {
        inlinePrompt: true,
        activeText: "활성화",
        inactiveText: "비활성화",
        activeValue: 1,
        inactiveValue: 0,
      },
    },
  ],
};

export default reactive(modalConfig);
