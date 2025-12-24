<!--
 * wangEditor-next 기반의 리치 텍스트 편집기 컴포넌트 래핑
 * 저작권 © 2021-present 유래 오픈소스 조직
 *
 * 오픈소스 라이선스：https://opensource.org/licenses/MIT
 * 프로젝트 주소：https://gitee.com/youlaiorg/vue3-element-admin
 *
 * 사용할 때 이 주석을 유지해주세요. 오픈소스 지원해주셔서 감사합니다!
-->

<template>
  <div style="z-index: 999; border: 1px solid var(--el-border-color)">
    <!-- 도구 모음 -->
    <Toolbar
      :editor="editorRef"
      mode="simple"
      :default-config="toolbarConfig"
      style="border-bottom: 1px solid var(--el-border-color)"
    />
    <!-- 편집기 -->
    <Editor
      v-model="modelValue"
      :style="{ height: height, overflowY: 'hidden' }"
      :default-config="editorConfig"
      mode="simple"
      @on-created="handleCreated"
    />
  </div>
</template>

<script setup lang="ts">
import "@wangeditor-next/editor/dist/css/style.css";
import { Toolbar, Editor } from "@wangeditor-next/editor-for-vue";
import { IToolbarConfig, IEditorConfig } from "@wangeditor-next/editor";

// 파일 업로드 API
import FileAPI from "@/api/file-api";

// 이미지 업로드 콜백 함수 타입
type InsertFnType = (_url: string, _alt: string, _href: string) => void;

defineProps({
  height: {
    type: String,
    default: "500px",
  },
});
// 양방향 바인딩
const modelValue = defineModel("modelValue", {
  type: String,
  required: false,
});

// 편집기 인스턴스, shallowRef 사용 필수, 중요!
const editorRef = shallowRef();

// 도구 모음 구성
const toolbarConfig = ref<Partial<IToolbarConfig>>({});

// 편집기 구성
const editorConfig = ref<Partial<IEditorConfig>>({
  placeholder: "내용을 입력해주세요...",
  MENU_CONF: {
    uploadImage: {
      customUpload(file: File, insertFn: InsertFnType) {
        // 이미지 업로드
        FileAPI.uploadFile(file).then((res) => {
          // 이미지 삽입
          insertFn(res.url, res.name, res.url);
        });
      },
    } as any,
  },
});

// 편집기 인스턴스 기록, 중요!
const handleCreated = (editor: any) => {
  editorRef.value = editor;
};

// 컴포넌트 삭제 시, 편집기도 동시에 삭제해야 함, 중요!
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});
</script>
