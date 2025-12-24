<template>
  <div class="app-container h-full flex flex-1 flex-col">
    <div class="flex-x-between mb-10">
      <el-link
        href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/curd/index.vue"
        type="primary"
        target="_blank"
      >
        예제 소스 코드 클릭>>>>
      </el-link>
      <el-button type="primary" plain round size="small" @click="isA = !isA">예제 전환</el-button>
    </div>

    <!-- 목록 -->
    <template v-if="isA">
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
            {{ scope.row[scope.prop] == 1 ? "활성화" : "비활성화" }}
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
      <page-modal
        ref="addModalRef"
        :modal-config="addModalConfig"
        @submit-click="handleSubmitClick"
      >
        <template #gender="scope">
          <Dict v-model="scope.formData[scope.prop]" code="gender" v-bind="scope.attrs" />
        </template>
        <template #openModal>
          <el-button type="primary" @click="openSecondModal">2단계 팝업 열기</el-button>
        </template>
      </page-modal>

      <!-- 2단계 팝업 -->
      <page-modal ref="addModalRef2" :modal-config="addModalConfig2" @custom-submit="secondSubmit">
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
    </template>
    <template v-else>
      <page-search ref="searchRef" :search-config="searchConfig2" @reset-click="handleResetClick" />

      <page-content
        ref="contentRef"
        :content-config="contentConfig2"
        @operate-click="handleOperateClick2"
      >
        <template #status="scope">
          <el-tag :type="scope.row[scope.prop] == 1 ? 'success' : 'info'">
            {{ scope.row[scope.prop] == 1 ? "활성화" : "비활성화" }}
          </el-tag>
        </template>
      </page-content>

      <page-modal ref="editModalRef" :modal-config="editModalConfig2">
        <template #suffix>
          <span style="color: black">%</span>
        </template>
        <template #prefix>
          <span>$</span>
        </template>
        <template #gender="scope">
          <Dict v-model="scope.formData[scope.prop]" code="gender" v-bind="scope.attrs" />
        </template>
      </page-modal>
    </template>
  </div>
</template>

<script setup lang="ts">
import UserAPI from "@/api/system/user-api";
import type { IObject, IOperateData, PageModalInstance } from "@/components/CURD/types";
import usePage from "@/components/CURD/usePage";
import addModalConfig from "./config/add";
import contentConfig from "./config/content";
import editModalConfig from "./config/edit";
import searchConfig from "./config/search";
import { initOptions } from "./config/options";

import addModalConfig2 from "./config2/add";
import contentConfig2 from "./config2/content";
import editModalConfig2 from "./config2/edit";
import searchConfig2 from "./config2/search";

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

// 其他工具열
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
      // 로딩下拉데이터源，제안在初始化설정항목 initFn 내로딩，避免多次요청求
      // editModalConfig.formItems[2]!.attrs!.data = await DeptAPI.getOptions();
      return await UserAPI.getFormData(data.row.id); // 에 따라ID조회상세
    });
  } else if (data.name === "edit") {
    editModalConfig.drawer = { ...editModalConfig.drawer, title: "수정" };
    handleEditClick(data.row, async () => {
      return await UserAPI.getFormData(data.row.id); // 에 따라ID조회상세
    });
  } else if (data.name === "reset_pwd") {
    ElMessageBox.prompt("사용자 「" + data.row.username + "」의 새 비밀번호를 입력하세요", "비밀번호 재설정", {
      confirmButtonText: "확인",
      cancelButtonText: "취소",
    })
      .then(({ value }) => {
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
const handleOperateClick2 = (data: IOperateData) => {
  if (data.name === "view") {
    editModalConfig.drawer = { ...editModalConfig.drawer, title: "보기" };
    handleViewClick(data.row);
  } else if (data.name === "edit") {
    editModalConfig.drawer = { ...editModalConfig.drawer, title: "수정" };
    handleEditClick(data.row);
  } else if (data.name === "delete") {
    ElMessage.success("模拟삭제성공");
  }
};

// 2단계 팝업 열기
const addModalRef2 = ref();
const openSecondModal = () => {
  handleAddClick(addModalRef2 as Ref<PageModalInstance>);
};
const secondSubmit = (formData: any) => {
  console.log("secondSubmit", formData);
  ElMessage.success("2단계 팝업 제출 성공");
};

// 예제 전환
const isA = ref(true);

onMounted(() => {
  initOptions();
});
</script>
