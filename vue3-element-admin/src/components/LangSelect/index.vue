<template>
  <el-dropdown trigger="click" @command="handleLanguageChange">
    <div class="i-svg:language" :class="size" />
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in langOptions"
          :key="item.value"
          :disabled="app스토어.language === item.value"
          :command="item.value"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { useApp스토어 } from "@/store/modules/app-store";
import { LanguageEnum } from "@/enums/settings/locale-enum";

defineProps({
  size: {
    type: String,
    required: false,
  },
});

const langOptions = [
  { label: "중국어", value: LanguageEnum.ZH_CN },
  { label: "English", value: LanguageEnum.EN },
];

const app스토어 = useApp스토어();
const { locale, t } = useI18n();

/**
 * 언어 변경 처리
 *
 * @param lang  언어 (zh-cn, en)
 */
function handleLanguageChange(lang: string) {
  locale.value = lang;
  app스토어.changeLanguage(lang);

  ElMessage.success(t("langSelect.message.success"));
}
</script>
