<!-- 파일 업로드 컴포넌트 -->
<template>
  <div>
    <el-upload
      v-model:file-list="fileList"
      :style="props.style"
      :before-upload="handleBeforeUpload"
      :http-request="handleUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-exceed="handleExceed"
      :accept="props.accept"
      :limit="props.limit"
      multiple
    >
      <!-- 파일 업로드 버튼 -->
      <el-button type="primary" :disabled="fileList.length >= props.limit">
        {{ props.uploadBtnText }}
      </el-button>

      <!-- 파일 목록 -->
      <template #file="{ file }">
        <template v-if="file.status === 'success'">
          <div class="el-upload-list__item-info">
            <a class="el-upload-list__item-name" @click="handleDownload(file)">
              <el-icon>
                <Document />
              </el-icon>
              <span class="el-upload-list__item-file-name">{{ file.name }}</span>
              <span class="el-icon--close" @click.stop="handleRemove(file.url!)">
                <el-icon>
                  <Close />
                </el-icon>
              </span>
            </a>
          </div>
        </template>
        <template v-else>
          <div class="el-upload-list__item-info">
            <el-progress style="display: inline-flex" :percentage="file.percentage" />
          </div>
        </template>
      </template>
    </el-upload>
  </div>
</template>
<script lang="ts" setup>
import {
  UploadRawFile,
  UploadUserFile,
  UploadFile,
  UploadFiles,
  UploadRequestOptions,
} from "element-plus";

import FileAPI, { FileInfo } from "@/api/file-api";

const props = defineProps({
  /**
   * 요청에 포함된 추가 매개변수
   */
  data: {
    type: Object,
    default: () => {
      return {};
    },
  },
  /**
   * 파일 업로드 매개변수 이름
   */
  name: {
    type: String,
    default: "file",
  },
  /**
   * 파일 업로드 수량 제한
   */
  limit: {
    type: Number,
    default: 10,
  },
  /**
   * 개별 파일 업로드 크기 제한(단위:MB)
   */
  maxFileSize: {
    type: Number,
    default: 10,
  },
  /**
   * 파일 업로드 유형
   */
  accept: {
    type: String,
    default: "*",
  },
  /**
   * 업로드 버튼 텍스트
   */
  uploadBtnText: {
    type: String,
    default: "파일 업로드",
  },

  /**
   * 스타일
   */
  style: {
    type: Object,
    default: () => {
      return {
        width: "300px",
      };
    },
  },
});
const modelValue = defineModel("modelValue", {
  type: [Array] as PropType<FileInfo[]>,
  required: true,
  default: () => [],
});

const fileList = ref([] as UploadFile[]);

// modelValue를 감시하고 표시할 fileList로 변환
watch(
  modelValue,
  (value) => {
    fileList.value = value.map((item) => {
      const name = item.name ? item.name : item.url?.substring(item.url.lastIndexOf("/") + 1);
      return {
        name,
        url: item.url,
        status: "success",
        uid: getUid(),
      } as UploadFile;
    });
  },
  {
    immediate: true,
  }
);

/**
 * 업로드 전 검증
 */
function handleBeforeUpload(file: UploadRawFile) {
  // 파일 크기 제한
  if (file.size > props.maxFileSize * 1024 * 1024) {
    ElMessage.warning("업로드할 파일은 " + props.maxFileSize + "MB보다 클 수 없습니다");
    return false;
  }
  return true;
}

/*
 * 파일 업로드
 */
function handleUpload(options: UploadRequestOptions) {
  return new Promise((resolve, reject) => {
    const file = options.file;
    const formData = new FormData();
    formData.append(props.name, file);

    // 추가 매개변수 처리
    Object.keys(props.data).forEach((key) => {
      formData.append(key, props.data[key]);
    });
    FileAPI.upload(formData, (percent) => {
      const uid = file.uid;
      const fileItem = fileList.value.find((file) => file.uid === uid);
      if (fileItem) {
        fileItem.percentage = percent;
      }
    })
      .then((res) => {
        resolve(res);
      })
      .catch((err) => {
        reject(err);
      });
  });
}

/**
 * 업로드된 파일이 제한을 초과함
 */
function handleExceed() {
  ElMessage.warning(`최대 ${props.limit}개의 파일만 업로드할 수 있습니다`);
}

/**
 * 업로드 성공
 */
const handleSuccess = (response: any, uploadFile: UploadFile, files: UploadFiles) => {
  ElMessage.success("업로드 성공");
  // 상태가 success 또는 fail일 때만 파일 업로드가 완료되며, 실패도 완료로 간주됩니다
  if (
    files.every((file: UploadFile) => {
      return file.status === "success" || file.status === "fail";
    })
  ) {
    const fileInfos = [] as FileInfo[];
    files.map((file: UploadFile) => {
      if (file.status === "success") {
        // response를 포함하는 것만 방금 업로드된 파일입니다
        const res = file.response as FileInfo;
        if (res) {
          fileInfos.push({ name: res.name, url: res.url } as FileInfo);
        }
      } else {
        // 실패한 업로드는 fileList에서 제거하고 표시하지 않음
        fileList.value.splice(
          fileList.value.findIndex((e) => e.uid === file.uid),
          1
        );
      }
    });
    if (fileInfos.length > 0) {
      modelValue.value = [...modelValue.value, ...fileInfos];
    }
  }
};

/**
 * 업로드 실패
 */
const handleError = (_error: any) => {
  console.error(_error);
  ElMessage.error("업로드 실패");
};

/**
 * 파일 삭제
 */
function handleRemove(fileUrl: string) {
  FileAPI.delete(fileUrl).then(() => {
    modelValue.value = modelValue.value.filter((file) => file.url !== fileUrl);
  });
}

/**
 * 파일 다운로드
 */
function handleDownload(file: UploadUserFile) {
  const { url, name } = file;
  if (url) {
    FileAPI.download(url, name);
  }
}

/** 중복되지 않는 id 가져오기 */
function getUid(): number {
  // 타임스탐프를 13비트 왼쪽으로 이동(8192를 곱하는 것과 동일) + 4비트 난수
  return (Date.now() << 13) | Math.floor(Math.random() * 8192);
}
</script>
<style lang="scss" scoped>
.el-upload-list__item .el-icon--close {
  position: absolute;
  top: 50%;
  right: 5px;
  color: var(--el-text-color-regular);
  cursor: pointer;
  opacity: 0.75;
  transform: translateY(-50%);
  transition: opacity var(--el-transition-duration);
}

:deep(.el-upload-list) {
  margin: 0;
}

:deep(.el-upload-list__item) {
  margin: 0;
}
</style>
