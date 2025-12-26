<template>
  <div class="app-container h-full flex flex-1 flex-col">
    <div class="mb-10">
      <el-link
        href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/curd-demo.vue"
        type="primary"
        target="_blank"
      >
        통합 예제 소스 코드 클릭>>>>
      </el-link>
    </div>

    <!-- 검색 -->
    <page-search
      ref="searchRef"
      :search-config="searchConfig"
      @query-click="handleQueryClick"
      @reset-click="handleResetClick"
    />

    <!-- 목록 -->
    <page-content
      ref="contentRef"
      :content-config="contentConfig"
      @add-click="handleAddClick"
      @export-click="handleExportClick"
      @search-click="handleSearchClick"
      @toolbar-click="handleToolbarClick"
      @operate-click="handleOperateClick"
      @filter-change="handleFilterChange"
    >
      <template #status="scope">
        <el-tag :type="scope.row[scope.prop] == 1 ? 'success' : 'info'">
          {{ scope.row[scope.prop] == 1 ? "활성" : "비활성" }}
        </el-tag>
      </template>
      <template #gender="scope">
        <DictLabel v-model="scope.row[scope.prop]" code="gender" />
      </template>
      <template #mobile="scope">
        <el-text>{{ scope.row[scope.prop] }}</el-text>
        <copy-button
          v-if="scope.row[scope.prop]"
          :text="scope.row[scope.prop]"
          :style="{ marginLeft: '2px' }"
        />
      </template>
    </page-content>

    <!-- 추가 -->
    <page-modal ref="addModalRef" :modal-config="addModalConfig" @submit-click="handleSubmitClick">
      <template #gender="scope">
        <Dict v-model="scope.formData[scope.prop]" code="gender" v-bind="scope.attrs" />
      </template>
    </page-modal>

    <!-- 편집 -->
    <page-modal
      ref="editModalRef"
      :modal-config="editModalConfig"
      @submit-click="handleSubmitClick"
    >
      <template #gender="scope">
        <Dict v-model="scope.formData[scope.prop]" code="gender" v-bind="scope.attrs" />
      </template>
    </page-modal>
  </div>
</template>

<script setup lang="ts">
import UserAPI from "@/api/system/user-api";
import DeptAPI from "@/api/system/dept-api";
import RoleAPI from "@/api/system/role-api";
import type { UserForm, UserPageQuery } from "@/api/system/user-api";
import type { IObject, IModalConfig, IContentConfig, ISearchConfig } from "@/components/CURD/types";
import { DeviceEnum } from "@/enums/settings/device-enum";
import { useAppStore } from "@/store";
import usePage from "@/components/CURD/usePage";

defineOptions({
  name: "CurdDemo",
  inheritAttrs: false,
});

// ========================= 옵션 데이터 관리 =========================
interface OptionType {
  label: string;
  value: any;
  [key: string]: any;
}

// 공유 옵션 데이터
const deptArr = ref<OptionType[]>([]);
const roleArr = ref<OptionType[]>([]);
const stateArr = ref<OptionType[]>([
  { label: "활성", value: 1 },
  { label: "비활성", value: 0 },
]);

// 옵션 데이터 초기화
const initOptions = async () => {
  try {
    const [dept, roles] = await Promise.all([DeptAPI.getOptions(), RoleAPI.getOptions()]);
    deptArr.value = dept;
    roleArr.value = roles;
  } catch (error) {
    console.error("옵션 초기화 실패:", error);
  }
};

// ========================= 검색 설정 =========================
const searchConfig: ISearchConfig = reactive({
  permPrefix: "sys:user",
  formItems: [
    {
      tips: "모호한 검색 지원",
      type: "input",
      label: "키워드",
      prop: "keywords",
      attrs: {
        placeholder: "사용자명/닉네임/휴대폰 번호",
        clearable: true,
        style: { width: "200px" },
      },
    },
    {
      type: "tree-select",
      label: "부서",
      prop: "deptId",
      attrs: {
        placeholder: "선택하세요",
        data: deptArr,
        filterable: true,
        "check-strictly": true,
        "render-after-expand": false,
        clearable: true,
        style: { width: "200px" },
      },
    },
    {
      type: "select",
      label: "상태",
      prop: "status",
      attrs: {
        placeholder: "전체",
        clearable: true,
        style: { width: "200px" },
      },
      options: stateArr,
    },
    {
      type: "date-picker",
      label: "생성 시간",
      prop: "createTime",
      attrs: {
        type: "daterange",
        "range-separator": "~",
        "start-placeholder": "시작 시간",
        "end-placeholder": "종료 시간",
        "value-format": "YYYY-MM-DD",
        style: { width: "200px" },
      },
    },
  ],
});

// ========================= 내용 설정 =========================
const contentConfig: IContentConfig<UserPageQuery> = reactive({
  permPrefix: "sys:user",
  table: {
    border: true,
    highlightCurrentRow: true,
  },
  pagination: {
    background: true,
    layout: "prev,pager,next,jumper,total,sizes",
    pageSize: 20,
    pageSizes: [10, 20, 30, 50],
  },
  parseData(res: any) {
    return {
      total: res.total,
      list: res.list,
    };
  },
  index액션(params: any) {
    return UserAPI.getPage(params);
  },
  delete액션: UserAPI.deleteByIds,
  import액션(file: File) {
    return UserAPI.import("1", file);
  },
  export액션: UserAPI.export,
  importTemplate: UserAPI.downloadTemplate,
  imports액션(data: any) {
    console.log("imports액션", data);
    return Promise.resolve();
  },
  async exports액션(params: any) {
    const res = await UserAPI.getPage(params);
    console.log("exports액션", res.list);
    return res.list;
  },
  pk: "id",
  toolbar: [
    "add",
    "delete",
    "import",
    "export",
    {
      name: "custom1",
      text: "사용자정의1",
      perm: "add",
      attrs: { icon: "plus", color: "#626AEF" },
    },
  ],
  defaultToolbar: ["refresh", "filter", "imports", "exports", "search"],
  cols: [
    { type: "selection", width: 50, align: "center" },
    { label: "번호", align: "center", prop: "id", width: 100, show: false },
    { label: "사용자명", align: "center", prop: "username" },
    { label: "프로필 사진", align: "center", prop: "avatar", templet: "image" },
    { label: "사용자 닉네임", align: "center", prop: "nickname", width: 120 },
    {
      label: "성별",
      align: "center",
      prop: "gender",
      width: 100,
      templet: "custom",
      slotName: "gender",
    },
    { label: "부서", align: "center", prop: "deptName", width: 120 },
    {
      label: "역할",
      align: "center",
      prop: "roleNames",
      width: 120,
      columnKey: "roleIds",
      filters: [],
      filterMultiple: true,
      filterJoin: ",",
      async initFn(colItem: any) {
        const roleOptions = await RoleAPI.getOptions();
        colItem.filters = roleOptions.map((item) => {
          return { text: item.label, value: item.value };
        });
      },
    },
    {
      label: "휴대폰 번호",
      align: "center",
      prop: "mobile",
      templet: "custom",
      slotName: "mobile",
      width: 150,
    },
    {
      label: "상태",
      align: "center",
      prop: "status",
      templet: "custom",
      slotName: "status",
    },
    { label: "생성 시간", align: "center", prop: "createTime", width: 180 },
    {
      label: "작업",
      align: "center",
      fixed: "right",
      width: 280,
      templet: "tool",
      operat: [
        {
          name: "detail",
          text: "상세정보",
          attrs: { icon: "Document", type: "primary" },
        },
        {
          name: "reset_pwd",
          text: "비밀번호 재설정",
          attrs: {
            icon: "refresh-left",
            style: {
              "--el-button-text-color": "#626AEF",
              "--el-button-hover-link-text-color": "#9197f4",
            },
          },
        },
        "edit",
        "delete",
      ],
    },
  ],
});

// ========================= 추가 설정 =========================
const addModalConfig: IModalConfig<UserForm> = reactive({
  permPrefix: "sys:user",
  dialog: {
    title: "사용자 추가",
    width: 800,
    draggable: true,
  },
  form: {
    labelWidth: 100,
  },
  form액션: UserAPI.create,
  beforeSubmit(data: any) {
    console.log("제출 전 처리", data);
  },
  formItems: [
    {
      label: "사용자명",
      prop: "username",
      rules: [{ required: true, message: "사용자명은 필수입니다", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "사용자명을 입력하세요",
      },
      col: {
        xs: 24,
        sm: 12,
      },
    },
    {
      label: "사용자 닉네임",
      prop: "nickname",
      rules: [{ required: true, message: "사용자 닉네임은 필수입니다", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "사용자 닉네임을 입력하세요",
      },
      col: {
        xs: 24,
        sm: 12,
      },
    },
    {
      label: "소속 부서",
      prop: "deptId",
      rules: [{ required: true, message: "소속 부서는 필수입니다", trigger: "change" }],
      type: "tree-select",
      attrs: {
        placeholder: "소속 부서를 선택하세요",
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
    {
      label: "역할",
      prop: "roleIds",
      rules: [{ required: true, message: "사용자 역할은 필수입니다", trigger: "change" }],
      type: "select",
      attrs: {
        placeholder: "선택하세요",
        multiple: true,
      },
      options: roleArr,
      initialValue: [],
    },
    {
      type: "input",
      label: "휴대폰 번호",
      prop: "mobile",
      rules: [
        {
          pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
          message: "올바른 휴대폰 번호를 입력하세요",
          trigger: "blur",
        },
      ],
      attrs: {
        placeholder: "휴대폰 번호를 입력하세요",
        maxlength: 11,
      },
    },
    {
      label: "이메일",
      prop: "email",
      rules: [
        {
          pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/,
          message: "올바른 이메일 주소를 입력하세요",
          trigger: "blur",
        },
      ],
      type: "input",
      attrs: {
        placeholder: "이메일을 입력하세요",
        maxlength: 50,
      },
    },
    {
      label: "상태",
      prop: "status",
      type: "radio",
      options: [
        { label: "정상", value: 1 },
        { label: "비활성화", value: 0 },
      ],
      initialValue: 1,
    },
  ],
});

// ========================= 수정 설정 =========================
const editModalConfig: IModalConfig<UserForm> = reactive({
  permPrefix: "sys:user",
  component: "drawer",
  drawer: {
    title: "사용자 수정",
    size: useAppStore().device === DeviceEnum.MOBILE ? "80%" : 500,
  },
  pk: "id",
  beforeSubmit(data: any) {
    console.log("수정 전 처리", data);
  },
  form액션(data: any) {
    return UserAPI.update(data.id as string, data);
  },
  formItems: [
    {
      label: "사용자명",
      prop: "username",
      rules: [{ required: true, message: "사용자명은 필수입니다", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "사용자명을 입력하세요",
        readonly: true,
      },
    },
    {
      label: "사용자 닉네임",
      prop: "nickname",
      rules: [{ required: true, message: "사용자 닉네임은 필수입니다", trigger: "blur" }],
      type: "input",
      attrs: {
        placeholder: "사용자 닉네임을 입력하세요",
      },
    },
    {
      label: "소속 부서",
      prop: "deptId",
      rules: [{ required: true, message: "소속 부서는 필수입니다", trigger: "blur" }],
      type: "tree-select",
      attrs: {
        placeholder: "소속 부서를 선택하세요",
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
    {
      label: "역할",
      prop: "roleIds",
      rules: [{ required: true, message: "사용자 역할은 필수입니다", trigger: "blur" }],
      type: "select",
      attrs: {
        placeholder: "선택하세요",
        multiple: true,
      },
      options: roleArr,
      initialValue: [],
    },
    {
      type: "input",
      label: "휴대폰 번호",
      prop: "mobile",
      rules: [
        {
          pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
          message: "올바른 휴대폰 번호를 입력하세요",
          trigger: "blur",
        },
      ],
      attrs: {
        placeholder: "휴대폰 번호를 입력하세요",
        maxlength: 11,
      },
    },
    {
      label: "이메일",
      prop: "email",
      rules: [
        {
          pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/,
          message: "올바른 이메일 주소를 입력하세요",
          trigger: "blur",
        },
      ],
      type: "input",
      attrs: {
        placeholder: "이메일을 입력하세요",
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
});

// ========================= 페이지 로직 =========================
const {
  searchRef,
  contentRef,
  addModalRef,
  editModalRef,
  handleQueryClick,
  handleResetClick,
  handleAddClick,
  handleEditClick,
  handleViewClick,
  handleSubmitClick,
  handleExportClick,
  handleSearchClick,
  handleFilterChange,
} = usePage();

// 기타 도구 모음
function handleToolbarClick(name: string) {
  console.log(name);
  if (name === "custom1") {
    ElMessage.success("사용자정의1 버튼을 클릭했습니다");
  }
}

// 테이블 도구 모음
const handleOperateClick = (data: IObject) => {
  if (data.name === "detail") {
    editModalConfig.drawer = { ...editModalConfig.drawer, title: "보기" };
    handleViewClick(data.row, async () => {
      return await UserAPI.getFormData(data.row.id);
    });
  } else if (data.name === "edit") {
    editModalConfig.drawer = { ...editModalConfig.drawer, title: "수정" };
    handleEditClick(data.row, async () => {
      return await UserAPI.getFormData(data.row.id);
    });
  } else if (data.name === "reset_pwd") {
    ElMessageBox.prompt("사용자 「" + data.row.username + "」의 새 비밀번호를 입력하세요", "비밀번호 재설정", {
      confirmButtonText: "확인",
      cancelButtonText: "취소",
    })
      .then(({ value }: any) => {
        if (!value || value.length < 6) {
          ElMessage.warning("비밀번호는 최소 6자 이상이어야 합니다. 다시 입력하세요");
          return false;
        }
        UserAPI.resetPassword(data.row.id, value).then(() => {
          ElMessage.success("비밀번호 재설정 완료, 새 비밀번호는: " + value);
        });
      })
      .catch(() => {});
  }
};

// 초기화
onMounted(() => {
  initOptions();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>
