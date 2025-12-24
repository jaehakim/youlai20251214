<template>
  <!-- 레이아웃 크기 -->
  <el-tooltip :content="t('sizeSelect.tooltip')" effect="dark" placement="bottom">
    <el-dropdown trigger="click" @command="handleSizeChange">
      <div class="i-svg:size" />
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="item of sizeOptions"
            :key="item.value"
            :disabled="app스토어.size == item.value"
            :command="item.value"
          >
            {{ item.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </el-tooltip>
</template>

<script setup lang="ts">
import { ComponentSize } from "@/enums/settings/layout-enum";
import { useApp스토어 } from "@/store/modules/app-store";

const { t } = useI18n();
const sizeOptions = computed(() => {
  return [
    { label: t("sizeSelect.default"), value: ComponentSize.DEFAULT },
    { label: t("sizeSelect.large"), value: ComponentSize.LARGE },
    { label: t("sizeSelect.small"), value: ComponentSize.SMALL },
  ];
});

const app스토어 = useApp스토어();
function handleSizeChange(size: string) {
  app스토어.changeSize(size);
  ElMessage.success(t("sizeSelect.message.success"));
}
</script>
