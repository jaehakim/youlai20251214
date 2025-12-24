import UserAPI, { type UserForm } from "@/api/system/user-api";
import type { IModalConfig } from "@/components/CURD/types";
import { DeviceEnum } from "@/enums/settings/device-enum";
import { useApp스토어 } from "@/저장소";
import { deptArr, roleArr } from "./options";

const modalConfig: IModalConfig<UserForm> = {
  permP참조ix: "sys:user",
  component: "drawer",
  drawer: {
    title: "수정사용자",
    size: useApp스토어().device === DeviceEnum.MOBILE ? "80%" : 500,
  },
  pk: "id",
  beforeSubmit(data) {
    console.log("beforeSubmit", data);
  },
  form액션(data) {
    return UserAPI.update(data.id as string, data);
  },
  formItems: [
    {
      label: "사용자이름",
      prop: "username",
      rules: [{ required: true, message: "사용자이름비어있을 수 없음비어있음", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "입력해주세요사용자이름",
        readonly: true,
      },
    },
    {
      label: "사용자닉네임",
      prop: "nickname",
      rules: [{ required: true, message: "사용자닉네임비어있을 수 없음비어있음", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "입력해주세요사용자닉네임",
      },
    },
    {
      label: "소속부서",
      prop: "deptId",
      rules: [{ required: true, message: "소속부서비어있을 수 없음비어있음", trigger: "blur" }],
      type: "tree-select",
      attrs: {
        placeholder: "선택해주세요소속부서",
        data: deptArr, // setup，Vue회의자동解패키지참조，아님필요해야.value
        filterable: true,
        "check-strictly": true,
        "render-after-expand": false,
      },
      // async initFn(formItem) {
      //   // 주의:만약initFn함수아님예箭头함수,this회의指에이설정항목객체,那么也就可以用this来替代形参formItem
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
      rules: [{ required: true, message: "사용자역할비어있을 수 없음비어있음", trigger: "blur" }],
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
          message: "입력해주세요正确의휴대폰 번호",
          trigger: "blur",
        },
      ],
      attrs: {
        placeholder: "입력해주세요휴대폰 번호",
        maxlength: 11,
      },
    },
    {
      label: "이메일",
      prop: "email",
      rules: [
        {
          pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/,
          message: "입력해주세요正确의이메일주소",
          trigger: "blur",
        },
      ],
      type: "input",
      attrs: {
        placeholder: "입력해주세요이메일",
        maxlength: 50,
      },
    },
    {
      label: "상태",
      prop: "status",
      type: "switch",
      attrs: {
        inlinePrompt: true,
        activeText: "정상",
        inactiveText: "비활성화",
        activeValue: 1,
        inactiveValue: 0,
      },
    },
  ],
};

export default reactive(modalConfig);
