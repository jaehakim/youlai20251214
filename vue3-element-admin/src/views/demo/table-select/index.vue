<!-- 테이블 선택기 예제 -->
<template>
  <div class="app-container">
    <el-link
      href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/table-select/index.vue"
      type="primary"
      target="_blank"
      class="mb-10"
    >
      예제 소스 코드 클릭>>>>
    </el-link>
    <table-select :text="text" :select-config="selectConfig" @confirm-click="handleConfirm">
      <template #status="scope">
        <el-tag :type="scope.row[scope.prop] == 1 ? 'success' : 'info'">
          {{ scope.row[scope.prop] == 1 ? "활성화" : "비활성화" }}
        </el-tag>
      </template>
      <template #gender="scope">
        <DictLabel v-model="scope.row.gender" code="gender" />
      </template>
    </table-select>
  </div>
</template>

<script setup lang="ts">
import selectConfig from "./config/select";
import { useDict스토어 } from "@/store";
const dict스토어 = useDict스토어();
interface IUser {
  id: string;
  username: string;
  nickname: string;
  mobile: string;
  gender: string;
  avatar: string;
  email: string | null;
  status: number;
  deptName: string;
  roleNames: string;
  createTime: string;
}
const selectedUser = ref<IUser>();
function handleConfirm(data: IUser[]) {
  selectedUser.value = data[0];
}
const text = computed(() => {
  // 사전 데이터 가져오기
  const dictData = dict스토어.getDictItems("gender");
  const genderLabel = dictData.find((item: any) => item.value == selectedUser.value?.gender)?.label;
  return selectedUser.value
    ? `${selectedUser.value.username} - ${genderLabel} - ${selectedUser.value.deptName}`
    : "";
});
</script>
