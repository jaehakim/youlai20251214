<template>
  <div class="app-container">
    <el-row :gutter="24">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span class="card-header">기본 예제</span></template>
          <VueDraggable ref="el" v-model="userList" class="drag-container">
            <div v-for="item in userList" :key="item.name" class="drag-item">
              {{ item.name }}
            </div>
          </VueDraggable>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span class="card-header">전환 애니메이션</span></template>
          <VueDraggable
            v-model="userList"
            target=".sort-target"
            :scroll="true"
            class="drag-container"
          >
            <TransitionGroup type="transition" tag="ul" name="fade" class="sort-target">
              <li v-for="item in userList" :key="item.name" class="drag-item">
                {{ item.name }}
              </li>
            </TransitionGroup>
          </VueDraggable>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <template #header><span class="card-header">표 드래그 정렬</span></template>
      <VueDraggable v-model="userList" target="tbody" :animation="150">
        <el-table :data="userList" row-key="name">
          <el-table-column label="이름" prop="name" />
          <el-table-column label="역할" prop="roles" />
        </el-table>
      </VueDraggable>
    </el-card>

    <el-card shadow="never">
      <template #header><span class="card-header">지정된 요소 드래그 정렬</span></template>
      <VueDraggable v-model="userList" target="tbody" handle=".handle" :animation="150">
        <el-table :data="userList" row-key="name">
          <el-table-column label="이름" prop="name" />
          <el-table-column label="역할" prop="roles" />
          <el-table-column label="작업" width="100">
            <template #default>
              <el-button size="default" class="handle">이동</el-button>
            </template>
          </el-table-column>
        </el-table>
      </VueDraggable>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { VueDraggable } from "vue-draggable-plus";

const userList = ref([
  { name: "루피", roles: "선장·격투가·D의 일족" },
  { name: "조로", roles: "검호·전투원·세 칼 유파의 대사" },
  { name: "나미", roles: "항해사·기상학자·재무 담당" },
  { name: "상디", roles: "요리사·격투가·검은 발" },
  { name: "로빈", roles: "고고학자·역사 정문 해석자" },
]);
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;

  .el-card {
    margin-bottom: 20px;
    border-radius: 8px;
  }

  .card-header {
    font-size: 16px;
    font-weight: bold;
  }

  .drag-container {
    min-height: 200px;
  }

  .drag-item {
    padding: 12px;
    margin-bottom: 8px;
    font-weight: 500;
    text-align: center;
    cursor: grab;
    border-radius: 6px;
    transition: transform 0.2s ease-in-out;
  }

  .drag-item:active {
    cursor: grabbing;
    transform: scale(1.05);
  }
}

/* 전환 애니메이션 */
.fade-move,
.fade-enter-active,
.fade-leave-active {
  transition: all 0.5s cubic-bezier(0.55, 0, 0.1, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scaleY(0.01) translate(20px, 0);
}

.fade-leave-active {
  position: absolute;
}
</style>
