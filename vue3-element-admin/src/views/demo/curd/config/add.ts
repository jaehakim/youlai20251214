import UserAPI, { type UserForm } from "@/api/system/user-api";
import type { IModalConfig } from "@/components/CURD/types";
import { deptArr, roleArr } from "./options";

const modalConfig: IModalConfig<UserForm> = {
  permP참조ix: "sys:user",
  dialog: {
    title: "신규사용자",
    width: 800,
    draggable: true,
  },
  form: {
    labelWidth: 100,
  },
  formAction: UserAPI.create,
  beforeSubmit(data) {
    console.log("제출之前처리", data);
  },
  formItems: [
    {
      label: "사용자이름",
      prop: "username",
      rules: [{ required: true, message: "사용자이름비어있을 수 없음비어있음", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "입력해주세요사용자이름",
      },
      col: {
        xs: 24,
        sm: 12,
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
      col: {
        xs: 24,
        sm: 12,
      },
    },
    {
      label: "所属부서",
      prop: "deptId",
      rules: [{ required: true, message: "所属부서비어있을 수 없음비어있음", trigger: "change" }],
      type: "tree-select",
      attrs: {
        placeholder: "선택해주세요所属부서",
        data: deptArr,
        filterable: true,
        "check-strictly": true,
        "render-after-expand": false,
      },
      // async initFn(formItem) {
      //   // 注意:만약initFn함수不是箭头함수,this会指에此설정항목객체,那么也就可以用this来替代形参formItem
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
      rules: [{ required: true, message: "사용자역할비어있을 수 없음비어있음", trigger: "change" }],
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
      label: "手机号码",
      prop: "mobile",
      rules: [
        {
          pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
          message: "입력해주세요正确의手机号码",
          trigger: "blur",
        },
      ],
      attrs: {
        placeholder: "입력해주세요手机号码",
        maxlength: 11,
      },
    },
    {
      label: "이메일",
      prop: "email",
      rules: [
        {
          pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/,
          message: "입력해주세요正确의이메일地址",
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
      type: "radio",
      options: [
        { label: "正常", value: 1 },
        { label: "비활성화", value: 0 },
      ],
      initialValue: 1,
    },
    {
      type: "custom",
      label: "二级弹窗",
      prop: "openModal",
      slotName: "openModal",
    },
  ],
};

// 만약有비동기데이터会수정설정의，推荐用reactive패키지裹，而纯静态설정의可以直接내보내기
export default reactive(modalConfig);
