<template>
  <div>
    <el-dialog
      v-model="visible"
      :align-center="true"
      title="데이터 가져오기"
      width="600px"
      @close="handleClose"
    >
      <el-scrollbar max-height="60vh">
        <el-form
          ref="importFormRef"
          style="padding-right: var(--el-dialog-padding-primary)"
          :model="importFormData"
          :rules="importFormRules"
        >
          <el-form-item label="파일명" prop="files">
            <el-upload
              ref="uploadRef"
              v-model:file-list="importFormData.files"
              class="w-full"
              accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
              :drag="true"
              :limit="1"
              :auto-upload="false"
              :on-exceed="handleFileExceed"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                파일을 여기로 드래그하거나
                <em>클릭하여 업로드</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  格式为*.xlsx / *.xls，文件不超过一个
                  <el-link
                    type="primary"
                    icon="download"
                    underline="never"
                    @click="handleDownloadTemplate"
                  >
                    템플릿 다운로드
                  </el-link>
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </el-form>
      </el-scrollbar>
      <template #footer>
        <div style="padding-right: var(--el-dialog-padding-primary)">
          <el-button v-if="resultData.length > 0" type="primary" @click="handleShowResult">
            오류 정보
          </el-button>
          <el-button
            type="primary"
            :disabled="importFormData.files.length === 0"
            @click="handleUpload"
          >
            确 定
          </el-button>
          <el-button @click="handleClose">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="resultVisible" title="가져오기 결과" width="600px">
      <el-alert
        :title="`가져오기 결과：${invalidCount}개 유효하지 않은 데이터，${validCount}개 유효한 데이터`"
        type="warning"
        :closable="false"
      />
      <el-table :data="resultData" style="width: 100%; max-height: 400px">
        <el-table-column prop="index" align="center" width="100" type="index" label="번호" />
        <el-table-column prop="message" label="오류 정보" width="400">
          <template #default="scope">
            {{ scope.row }}
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCloseResult">닫기</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import { ElMessage, type UploadUserFile } from "element-plus";
import UserAPI from "@/api/system/user-api";
import { ApiCodeEnum } from "@/enums/api/code-enum";

const emit = defineEmits(["import-success"]);
const visible = defineModel("modelValue", {
  type: Boolean,
  required: true,
  default: false,
});

const resultVisible = ref(false);
const resultData = ref<string[]>([]);
const invalidCount = ref(0);
const validCount = ref(0);

const importFormRef = ref(null);
const uploadRef = ref(null);

const importFormData = reactive<{
  files: UploadUserFile[];
}>({
  files: [],
});

watch(visible, (newValue) => {
  if (newValue) {
    resultData.value = [];
    resultVisible.value = false;
    invalidCount.value = 0;
    validCount.value = 0;
  }
});

const importFormRules = {
  files: [{ required: true, message: "파일을 비워둘 수 없습니다", trigger: "blur" }],
};

// 文件超出个数限制
const handleFileExceed = () => {
  ElMessage.warning("파일은 하나만 업로드할 수 있습니다");
};

// 下载导入模板
const handleDownloadTemplate = () => {
  UserAPI.downloadTemplate().then((response: any) => {
    const fileData = response.data;
    const fileName = decodeURI(response.headers["content-disposition"].split(";")[1].split("=")[1]);
    const fileType =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8";

    const blob = new Blob([fileData], { type: fileType });
    const downloadUrl = window.URL.createObjectURL(blob);

    const downloadLink = document.createElement("a");
    downloadLink.href = downloadUrl;
    downloadLink.download = fileName;

    document.body.appendChild(downloadLink);
    downloadLink.click();

    document.body.removeChild(downloadLink);
    window.URL.revokeObjectURL(downloadUrl);
  });
};

// 上传文件
const handleUpload = async () => {
  if (!importFormData.files.length) {
    ElMessage.warning("请选择文件");
    return;
  }

  try {
    const result = await UserAPI.import("1", importFormData.files[0].raw as File);
    if (result.code === ApiCodeEnum.SUCCESS && result.invalidCount === 0) {
      ElMessage.success("导入成功，데이터 가져오기：" + result.validCount + "条");
      emit("import-success");
      handleClose();
    } else {
      ElMessage.error("업로드 실패");
      resultVisible.value = true;
      resultData.value = result.messageList;
      invalidCount.value = result.invalidCount;
      validCount.value = result.validCount;
    }
  } catch (error: any) {
    console.error(error);
    ElMessage.error("업로드 실패：" + error);
  }
};

// 显示오류 정보
const handleShowResult = () => {
  resultVisible.value = true;
};

// 닫기오류 정보弹窗
const handleCloseResult = () => {
  resultVisible.value = false;
};

// 닫기弹窗
const handleClose = () => {
  importFormData.files.length = 0;
  visible.value = false;
};
</script>
