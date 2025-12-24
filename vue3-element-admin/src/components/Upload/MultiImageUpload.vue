<!-- 이미지 업로드 컴포넌트 -->
<template>
  <el-upload
    v-model:file-list="fileList"
    list-type="picture-card"
    :before-upload="handleBeforeUpload"
    :http-request="handleUpload"
    :on-success="handleSuccess"
    :on-error="handleError"
    :on-exceed="handleExceed"
    :accept="props.accept"
    :limit="props.limit"
    multiple
  >
    <el-icon><Plus /></el-icon>
    <template #file="{ file }">
      <div style="width: 100%">
        <img class="el-upload-list__item-thumbnail" :src="file.url" />
        <span class="el-upload-list__item-actions">
          <!-- 미리 보기 -->
          <span @click="handlePreviewImage(file.url!)">
            <el-icon><zoom-in /></el-icon>
          </span>
          <!-- 삭제 -->
          <span @click="handleRemove(file.url!)">
            <el-icon><Delete /></el-icon>
          </span>
        </span>
      </div>
    </template>
  </el-upload>

  <el-image-viewer
    v-if="previewVisible"
    :zoom-rate="1.2"
    :initial-index="previewImageIndex"
    :url-list="modelValue"
    @close="handlePreviewClose"
  />
</template>
<script setup lang="ts">
import { UploadRawFile, UploadRequestOptions, UploadUserFile } from "element-plus";
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
   * 개별 파일의 최대 허용 크기
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
    default: "image/*", //  기본적으로 모든 이미지 형식을 지원하며, 지정된 형식이 필요한 경우 형식은 다음과 같습니다: '.png,.jpg,.jpeg,.gif,.bmp'
  },
});

const previewVisible = ref(false); // 미리 보기 표시 여부
const previewImageIndex = ref(0); // 미리 보기 이미지의 인덱스

const modelValue = defineModel("modelValue", {
  type: [Array] as PropType<string[]>,
  default: () => [],
});

const fileList = ref<UploadUserFile[]>([]);

/**
 * 이미지 삭제
 */
function handleRemove(imageUrl: string) {
  FileAPI.delete(imageUrl).then(() => {
    const index = modelValue.value.indexOf(imageUrl);
    if (index !== -1) {
      // 배열을 직접 수정하여 전체 업데이트를 트리거하지 않음
      modelValue.value.splice(index, 1);
      fileList.value.splice(index, 1); // fileList 동기화 업데이트
    }
  });
}

/**
 * 업로드 전 검증
 */
function handleBeforeUpload(file: UploadRawFile) {
  // 파일 유형 검증: accept 속성이 파일 선택기에서 사용자가 선택할 수 있는 파일 유형을 제한하지만, 업로드 시 파일의 실제 유형을 다시 검증하여 accept 규칙을 준수하도록 해야 합니다
  const acceptTypes = props.accept.split(",").map((type) => type.trim());

  // 파일 형식이 accept를 준수하는지 확인
  const isValidType = acceptTypes.some((type) => {
    if (type === "image/*") {
      // image/*인 경우 MIME 유형이 "image/"로 시작하는지 확인
      return file.type.startsWith("image/");
    } else if (type.startsWith(".")) {
      // 확장명(.png, .jpg)인 경우 파일명이 지정된 확장명으로 끝나는지 확인
      return file.name.toLowerCase().endsWith(type);
    } else {
      // 구체적인 MIME 유형(image/png, image/jpeg)인 경우 완전히 일치하는지 확인
      return file.type === type;
    }
  });

  if (!isValidType) {
    ElMessage.warning(`업로드 파일 형식이 잘못되었습니다. 다음만 지원됩니다: ${props.accept}`);
    return false;
  }

  // 파일 크기 제한
  if (file.size > props.maxFileSize * 1024 * 1024) {
    ElMessage.warning("업로드할 이미지는 " + props.maxFileSize + "MB보다 클 수 없습니다");
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

    FileAPI.upload(formData)
      .then((data) => {
        resolve(data);
      })
      .catch((error) => {
        reject(error);
      });
  });
}

/**
 * 업로드된 파일이 제한을 초과함
 */
function handleExceed() {
  ElMessage.warning("최대 " + props.limit + "개의 이미지만 업로드할 수 있습니다");
}

/**
 * 업로드 성공 콜백
 */
const handleSuccess = (fileInfo: FileInfo, uploadFile: UploadUserFile) => {
  ElMessage.success("업로드 성공");
  const index = fileList.value.findIndex((file) => file.uid === uploadFile.uid);
  if (index !== -1) {
    fileList.value[index].url = fileInfo.url;
    fileList.value[index].status = "success";
    modelValue.value[index] = fileInfo.url;
  }
};

/**
 * 업로드 실패 콜백
 */
const handleError = (error: any) => {
  console.log("handleError");
  ElMessage.error("업로드 실패: " + error.message);
};

/**
 * 이미지 미리 보기
 */
const handlePreviewImage = (imageUrl: string) => {
  previewImageIndex.value = modelValue.value.findIndex((url) => url === imageUrl);
  previewVisible.value = true;
};

/**
 * 미리 보기 닫기
 */
const handlePreviewClose = () => {
  previewVisible.value = false;
};

onMounted(() => {
  fileList.value = modelValue.value.map((url) => ({ url }) as UploadUserFile);
});
</script>
<style lang="scss" scoped></style>
