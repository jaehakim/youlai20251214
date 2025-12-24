<!-- 파일 업로드 컴포넌트 예제 -->
<template>
  <div class="app-container">
    <el-link
      href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/auto-opreation-column.vue"
      type="primary"
      target="_blank"
      class="mb-10"
    >
      예제 소스 코드 클릭>>>>
    </el-link>

    <div>
      <h3>적응형 테이블 작업 열</h3>
      <div class="text-14px color-#999">
        이 컴포넌트는 작업 열이 있는 테이블에 적용됩니다. 특정 상황에서 버튼은 데이터 상태 또는 기타 조건에 따라 동적으로 표시되어야 할 수 있으며 고정된 너비를 미리 설정할 수 없습니다. 작업 열 컴포넌트는 버튼 수에 따라 너비를 자동 조정할 수 있으므로 더 이상 수동으로 너비를 설정할 필요가 없습니다.
      </div>
      <div class="mt-30px">
        <el-checkbox v-model="checked1" label="보기" size="large" />
        <el-checkbox v-model="checked2" label="6자 이상이면 어떻게 될까" size="large" />
        <el-checkbox v-model="checked3" label="추가" size="large" />
        <el-checkbox v-model="checked4" label="많은 문자 반환" size="large" />
        <el-checkbox v-model="checked5" label="편집" size="large" />
      </div>

      <el-table :data="tableData" style="width: 100%" border>
        <el-table-column prop="date" label="Date" />
        <el-table-column prop="name" label="Name" />
        <el-table-column prop="state" label="State" />
        <el-table-column prop="city" label="City" />
        <el-table-column prop="address" label="Address" />
        <el-table-column prop="zip" label="Zip" />
        <OperationColumn :list-data-length="tableData.length">
          <template #default="{ row }">
            <el-button v-if="checked1" link type="primary" size="small">보기</el-button>
            <el-button v-if="checked2" link type="primary" size="small">
              6자 이상이면 어떻게 될까
            </el-button>
            <el-button v-if="checked3" link type="primary" size="small">추가</el-button>
            <el-button v-if="checked4" link type="primary" size="small">많은 문자 반환</el-button>
            <el-button v-if="checked5" link type="primary" size="small">편집</el-button>
            <el-button v-if="row.tag === 'Home'" link type="primary" size="small">기본값</el-button>
          </template>
        </OperationColumn>
      </el-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
import OperationColumn from "@/components/OperationColumn/index.vue";

const checked1 = ref(true);
const checked2 = ref(false);
const checked3 = ref(false);
const checked4 = ref(false);
const checked5 = ref(false);

const tableData = ref<any>([]);
setTimeout(() => {
  tableData.value = [
    {
      date: "2016-05-03",
      name: "Tom",
      state: "California",
      city: "Los Angeles",
      address: "No. 189, Grove St, Los Angeles",
      zip: "CA 90036",
      tag: "Home",
    },
    {
      date: "2016-05-02",
      name: "Tom",
      state: "California",
      city: "Los Angeles",
      address: "No. 189, Grove St, Los Angeles",
      zip: "CA 90036",
      tag: "Office",
    },
    {
      date: "2016-05-04",
      name: "Tom",
      state: "California",
      city: "Los Angeles",
      address: "No. 189, Grove St, Los Angeles",
      zip: "CA 90036",
      tag: "Home",
    },
    {
      date: "2016-05-01",
      name: "Tom",
      state: "California",
      city: "Los Angeles",
      address: "No. 189, Grove St, Los Angeles",
      zip: "CA 90036",
      tag: "Office",
    },
  ];
}, 300);
</script>
