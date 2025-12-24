<!-- 단일 이미지 업로드 컴포넌트 -->
<template>
  <el-upload
    class="single-upload"
    list-type="picture-card"
    :show-file-list="false"
    :accept="props.accept"
    :before-upload="handleBeforeUpload"
    :http-request="handleUpload"
    :on-success="onSuccess"
    :on-error="onError"
  >
    <template #default>
      <template v-if="modelValue">
        <el-image
          class="single-upload__image"
          :src="modelValue"
          :preview-src-list="[modelValue]"
          @click.stop="handlePreview"
        />
        <el-icon class="single-upload__delete-btn" @click.stop="handleDelete">
          <CircleCloseFilled />
        </el-icon>
      </template>
      <template v-else>
        <el-icon>
          <Plus />
        </el-icon>
      </template>
    </template>
  </el-upload>
</template>

<script setup lang="ts">
import { UploadRawFile, UploadRequestOptions } from "element-plus";
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
   * 최대 파일 크기(단위: MB)
   */
  maxFileSize: {
    type: Number,
    default: 10,
  },

  /**
   * 이미지 업로드 형식, 기본적으로 모든 이미지(image/*)를 지원하며, 지정 형식 예: '.png,.jpg,.jpeg,.gif,.bmp'
   */
  accept: {
    type: String,
    default: "image/*",
  },

  /**
   * 컴포넌트의 너비, 높이 등 기타 스타일을 설정하는 데 사용되는 사용자 정의 스타일
   */
  style: {
    type: Object,
    default: () => {
      return {
        width: "150px",
        height: "150px",
      };
    },
  },
});

const modelValue = defineModel("modelValue", {
  type: String,
  default: () => "",
});

/**
 * 사용자 업로드 파일의 형식과 크기 제한
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
 * 이미지 업로드
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
 * 이미지 미리 보기
 */
function handlePreview() {
  console.log("이미지 미리 보기, 이벤트 전파 중지");
}

/**
 * 이미지 삭제
 */
function handleDelete() {
  modelValue.value = "";
}

/**
 * 업로드 성공 콜백
 *
 * @param fileInfo 업로드 성공 후 파일 정보
 */
const onSuccess = (fileInfo: FileInfo) => {
  ElMessage.success("업로드 성공");
  modelValue.value = fileInfo.url;
};

/**
 * 업로드 실패 콜백
 */
const onError = (error: any) => {
  console.log("onError");
  ElMessage.error("업로드 실패: " + error.message);
};
</script>

<style scoped lang="scss">
:deep(.el-upload--picture-card) {
  position: relative;
  width: v-bind("props.style.width ?? '150px'");
  height: v-bind("props.style.height ?? '150px'");
}

.single-upload {
  &__image {
    border-radius: 6px;
  }

  &__delete-btn {
    position: absolute;
    top: 1px;
    right: 1px;
    font-size: 16px;
    color: #ff7901;
    cursor: pointer;
    background: #fff;
    border-radius: 100%;

    :hover {
      color: #ff4500;
    }
  }
}
</style>
