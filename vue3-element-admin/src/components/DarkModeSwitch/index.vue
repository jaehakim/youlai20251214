<template>
  <el-dropdown trigger="click" @command="handleDarkChange">
    <el-icon :size="20">
      <component :is="settings스토어.theme === ThemeMode.DARK ? Moon : Sunny" />
    </el-icon>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in theneList"
          :key="item.value"
          :command="item.value"
          :disabled="settings스토어.theme === item.value"
        >
          <el-icon>
            <component :is="item.component" />
          </el-icon>
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>
<script setup lang="ts">
import { useSettings스토어 } from "@/store";
import { ThemeMode } from "@/enums";
import { Moon, Sunny } from "@element-plus/icons-vue";

const { t } = useI18n();
const settings스토어 = useSettings스토어();

const theneList = [
  { label: t("login.light"), value: ThemeMode.LIGHT, component: Sunny },
  { label: t("login.dark"), value: ThemeMode.DARK, component: Moon },
];

const handleDarkChange = (theme: ThemeMode) => {
  settings스토어.updateTheme(theme);
};
</script>
