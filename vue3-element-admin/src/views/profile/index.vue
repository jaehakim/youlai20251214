<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 왼쪽 개인정보 카드 -->
      <el-col :span="8">
        <el-card class="user-card">
          <div class="user-info">
            <div class="avatar-wrapper">
              <el-avatar :src="userStore.userInfo.avatar" :size="100" />
              <el-button
                type="info"
                class="avatar-edit-btn"
                circle
                :icon="Camera"
                size="small"
                @click="triggerFileUpload"
              />
              <input
                ref="fileInput"
                type="file"
                style="display: none"
                accept="image/*"
                @change="handleFileChange"
              />
            </div>
            <div class="user-name">
              <span class="nickname">{{ userProfile.nickname }}</span>
              <el-icon class="edit-icon" @click="handleOpenDialog(DialogType.ACCOUNT)">
                <Edit />
              </el-icon>
            </div>
            <div class="user-role">{{ userProfile.roleNames }}</div>
          </div>
          <el-divider />
          <div class="user-stats">
            <div class="stat-item">
              <div class="stat-value">0</div>
              <div class="stat-label">할 일</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">0</div>
              <div class="stat-label">메시지</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">0</div>
              <div class="stat-label">알림</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 오른쪽 정보 카드 -->
      <el-col :span="16">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>계정 정보</span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="사용자명">
              {{ userProfile.username }}
              <el-icon v-if="userProfile.gender === 1" class="gender-icon male">
                <Male />
              </el-icon>
              <el-icon v-else class="gender-icon female">
                <Female />
              </el-icon>
            </el-descriptions-item>
            <el-descriptions-item label="휴대폰 번호">
              {{ userProfile.mobile || "미연동" }}
              <el-button
                v-if="userProfile.mobile"
                type="primary"
                link
                @click="() => handleOpenDialog(DialogType.MOBILE)"
              >
                변경
              </el-button>
              <el-button
                v-else
                type="primary"
                link
                @click="() => handleOpenDialog(DialogType.MOBILE)"
              >
                연동
              </el-button>
            </el-descriptions-item>
            <el-descriptions-item label="이메일">
              {{ userProfile.email || "미연동" }}
              <el-button
                v-if="userProfile.email"
                type="primary"
                link
                @click="() => handleOpenDialog(DialogType.EMAIL)"
              >
                변경
              </el-button>
              <el-button
                v-else
                type="primary"
                link
                @click="() => handleOpenDialog(DialogType.EMAIL)"
              >
                연동
              </el-button>
            </el-descriptions-item>
            <el-descriptions-item label="부서">
              {{ userProfile.deptName }}
            </el-descriptions-item>
            <el-descriptions-item label="생성 시간">
              {{ userProfile.createTime }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="security-card">
          <template #header>
            <div class="card-header">
              <span>보안 설정</span>
            </div>
          </template>
          <div class="security-item">
            <div class="security-info">
              <div class="security-title">계정 비밀번호</div>
              <div class="security-desc">정기적으로 비밀번호를 변경하면 계정 보안에 도움이 됩니다</div>
            </div>
            <el-button type="primary" link @click="() => handleOpenDialog(DialogType.PASSWORD)">
              변경
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 다이얼로그 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" :width="500">
      <!-- 계정 정보 -->
      <el-form
        v-if="dialog.type === DialogType.ACCOUNT"
        ref="userProfileFormRef"
        :model="userProfileForm"
        :label-width="100"
      >
        <el-form-item label="닉네임">
          <el-input v-model="userProfileForm.nickname" />
        </el-form-item>
        <el-form-item label="성별">
          <Dict v-model="userProfileForm.gender" code="gender" />
        </el-form-item>
      </el-form>

      <!-- 비밀번호 변경 -->
      <el-form
        v-if="dialog.type === DialogType.PASSWORD"
        ref="passwordChangeFormRef"
        :model="passwordChangeForm"
        :rules="passwordChangeRules"
        :label-width="100"
      >
        <el-form-item label="기존 비밀번호" prop="oldPassword">
          <el-input v-model="passwordChangeForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="새 비밀번호" prop="newPassword">
          <el-input v-model="passwordChangeForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="비밀번호 확인" prop="confirmPassword">
          <el-input v-model="passwordChangeForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>

      <!-- 휴대폰 연동 -->
      <el-form
        v-else-if="dialog.type === DialogType.MOBILE"
        ref="mobileBindingFormRef"
        :model="mobileUpdateForm"
        :rules="mobileBindingRules"
        :label-width="100"
      >
        <el-form-item label="휴대폰 번호" prop="mobile">
          <el-input v-model="mobileUpdateForm.mobile" style="width: 250px" />
        </el-form-item>
        <el-form-item label="인증번호" prop="code">
          <el-input v-model="mobileUpdateForm.code" style="width: 250px">
            <template #append>
              <el-button :disabled="mobileCountdown > 0" @click="handleSendMobileCode">
                {{ mobileCountdown > 0 ? `${mobileCountdown}s 후 재전송` : "인증번호 전송" }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <!-- 이메일 연동 -->
      <el-form
        v-else-if="dialog.type === DialogType.EMAIL"
        ref="emailBindingFormRef"
        :model="emailUpdateForm"
        :rules="emailBindingRules"
        :label-width="100"
      >
        <el-form-item label="이메일" prop="email">
          <el-input v-model="emailUpdateForm.email" style="width: 250px" />
        </el-form-item>
        <el-form-item label="인증번호" prop="code">
          <el-input v-model="emailUpdateForm.code" style="width: 250px">
            <template #append>
              <el-button :disabled="emailCountdown > 0" @click="handleSendEmailCode">
                {{ emailCountdown > 0 ? `${emailCountdown}s 후 재전송` : "인증번호 전송" }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">취소</el-button>
          <el-button type="primary" @click="handleSubmit">확인</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script lang="ts" setup>
import UserAPI, {
  UserProfileVO,
  PasswordChangeForm,
  MobileUpdateForm,
  EmailUpdateForm,
  UserProfileForm,
} from "@/api/system/user-api";

import FileAPI from "@/api/file-api";
import { useUserStoreHook } from "@/store";

import { Camera } from "@element-plus/icons-vue";

const userStore = useUserStoreHook();

const userProfile = ref<UserProfileVO>({});

const enum DialogType {
  ACCOUNT = "account",
  PASSWORD = "password",
  MOBILE = "mobile",
  EMAIL = "email",
}

const dialog = reactive({
  visible: false,
  title: "",
  type: "" as DialogType, // 계정 정보 수정, 비밀번호 변경, 휴대폰 연동, 이메일 연동
});
const userProfileFormRef = ref();
const passwordChangeFormRef = ref();
const mobileBindingFormRef = ref();
const emailBindingFormRef = ref();

const userProfileForm = reactive<UserProfileForm>({});
const passwordChangeForm = reactive<PasswordChangeForm>({});
const mobileUpdateForm = reactive<MobileUpdateForm>({});
const emailUpdateForm = reactive<EmailUpdateForm>({});

const mobileCountdown = ref(0);
const mobileTimer = ref();

const emailCountdown = ref(0);
const emailTimer = ref();

// 비밀번호 변경 검증 규칙
const passwordChangeRules = {
  oldPassword: [{ required: true, message: "기존 비밀번호를 입력하세요", trigger: "blur" }],
  newPassword: [{ required: true, message: "새 비밀번호를 입력하세요", trigger: "blur" }],
  confirmPassword: [{ required: true, message: "새 비밀번호를 다시 입력하세요", trigger: "blur" }],
};

// 휴대폰 번호 검증 규칙
const mobileBindingRules = {
  mobile: [
    { required: true, message: "휴대폰 번호를 입력하세요", trigger: "blur" },
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: "올바른 휴대폰 번호를 입력하세요",
      trigger: "blur",
    },
  ],
  code: [{ required: true, message: "인증번호를 입력하세요", trigger: "blur" }],
};

// 이메일 검증 규칙
const emailBindingRules = {
  email: [
    { required: true, message: "이메일을 입력하세요", trigger: "blur" },
    {
      pattern: /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/,
      message: "올바른 이메일 주소를 입력하세요",
      trigger: "blur",
    },
  ],
  code: [{ required: true, message: "인증번호를 입력하세요", trigger: "blur" }],
};

/**
 * 다이얼로그 열기
 * @param type 다이얼로그 유형 ACCOUNT: 계정 정보 PASSWORD: 비밀번호 변경 MOBILE: 휴대폰 연동 EMAIL: 이메일 연동
 */
const handleOpenDialog = (type: DialogType) => {
  dialog.type = type;
  dialog.visible = true;
  switch (type) {
    case DialogType.ACCOUNT:
      dialog.title = "계정 정보";
      // 폼 데이터 초기화
      userProfileForm.id = userProfile.value.id;
      userProfileForm.nickname = userProfile.value.nickname;
      userProfileForm.gender = userProfile.value.gender;
      break;
    case DialogType.PASSWORD:
      dialog.title = "비밀번호 변경";
      break;
    case DialogType.MOBILE:
      dialog.title = "휴대폰 연동";
      break;
    case DialogType.EMAIL:
      dialog.title = "이메일 연동";
      break;
  }
};

/**
 * 휴대폰 인증번호 전송
 */
function handleSendMobileCode() {
  if (!mobileUpdateForm.mobile) {
    ElMessage.error("휴대폰 번호를 입력하세요");
    return;
  }
  // 휴대폰 번호 형식 검증
  const reg = /^1[3-9]\d{9}$/;
  if (!reg.test(mobileUpdateForm.mobile)) {
    ElMessage.error("휴대폰 번호 형식이 올바르지 않습니다");
    return;
  }
  // SMS 인증번호 전송
  UserAPI.sendMobileCode(mobileUpdateForm.mobile).then(() => {
    ElMessage.success("인증번호 전송 성공");

    // 60초 카운트다운 재전송
    mobileCountdown.value = 60;
    mobileTimer.value = setInterval(() => {
      if (mobileCountdown.value > 0) {
        mobileCountdown.value -= 1;
      } else {
        clearInterval(mobileTimer.value!);
      }
    }, 1000);
  });
}

/**
 * 이메일 인증번호 전송
 */
function handleSendEmailCode() {
  if (!emailUpdateForm.email) {
    ElMessage.error("이메일을 입력하세요");
    return;
  }
  // 이메일 형식 검증
  const reg = /\w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}/;
  if (!reg.test(emailUpdateForm.email)) {
    ElMessage.error("이메일 형식이 올바르지 않습니다");
    return;
  }

  // 이메일 인증번호 전송
  UserAPI.sendEmailCode(emailUpdateForm.email).then(() => {
    ElMessage.success("인증번호 전송 성공");
    // 60초 카운트다운 재전송
    emailCountdown.value = 60;
    emailTimer.value = setInterval(() => {
      if (emailCountdown.value > 0) {
        emailCountdown.value -= 1;
      } else {
        clearInterval(emailTimer.value!);
      }
    }, 1000);
  });
}

/**
 * 폼 제출
 */
const handleSubmit = async () => {
  if (dialog.type === DialogType.ACCOUNT) {
    UserAPI.updateProfile(userProfileForm).then(() => {
      ElMessage.success("계정 정보 수정 성공");
      dialog.visible = false;
      loadUserProfile();
    });
  } else if (dialog.type === DialogType.PASSWORD) {
    if (passwordChangeForm.newPassword !== passwordChangeForm.confirmPassword) {
      ElMessage.error("두 번 입력한 비밀번호가 일치하지 않습니다");
      return;
    }
    UserAPI.changePassword(passwordChangeForm).then(() => {
      ElMessage.success("비밀번호 변경 성공");
      dialog.visible = false;
    });
  } else if (dialog.type === DialogType.MOBILE) {
    UserAPI.bindOrChangeMobile(mobileUpdateForm).then(() => {
      ElMessage.success("휴대폰 번호 연동 성공");
      dialog.visible = false;
      loadUserProfile();
    });
  } else if (dialog.type === DialogType.EMAIL) {
    UserAPI.bindOrChangeEmail(emailUpdateForm).then(() => {
      ElMessage.success("이메일 연동 성공");
      dialog.visible = false;
      loadUserProfile();
    });
  }
};

/**
 * 취소
 */
const handleCancel = () => {
  dialog.visible = false;
  if (dialog.type === DialogType.ACCOUNT) {
    userProfileFormRef.value?.resetFields();
  } else if (dialog.type === DialogType.PASSWORD) {
    passwordChangeFormRef.value?.resetFields();
  } else if (dialog.type === DialogType.MOBILE) {
    mobileBindingFormRef.value?.resetFields();
  } else if (dialog.type === DialogType.EMAIL) {
    emailBindingFormRef.value?.resetFields();
  }
};

const fileInput = ref<HTMLInputElement | null>(null);

const triggerFileUpload = () => {
  fileInput.value?.click();
};

const handleFileChange = async (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files ? target.files[0] : null;
  if (file) {
    // 파일 업로드 API 호출
    try {
      const data = await FileAPI.uploadFile(file);
      // 사용자 정보 업데이트
      await UserAPI.updateProfile({
        avatar: data.url,
      });
      // 사용자 아바타 업데이트
      userStore.userInfo.avatar = data.url;
    } catch (error) {
      console.error("아바타 업로드 실패: " + error);
      ElMessage.error("아바타 업로드 실패");
    }
  }
};

/** 사용자 정보 로드 */
const loadUserProfile = async () => {
  const data = await UserAPI.getProfile();
  userProfile.value = data;
};

onMounted(async () => {
  if (mobileTimer.value) {
    clearInterval(mobileTimer.value);
  }
  if (emailTimer.value) {
    clearInterval(emailTimer.value);
  }
  await loadUserProfile();
});
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: calc(100vh - 84px);
  padding: 20px;
  background: var(--el-fill-color-blank);
}

.user-card {
  .user-info {
    padding: 20px 0;
    text-align: center;

    .avatar-wrapper {
      position: relative;
      display: inline-block;
      margin-bottom: 16px;

      .avatar-edit-btn {
        position: absolute;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        border: none;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(0, 0, 0, 0.7);
        }
      }
    }

    .user-name {
      margin-bottom: 8px;

      .nickname {
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      .edit-icon {
        margin-left: 8px;
        color: var(--el-text-color-secondary);
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    .user-role {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }

  .user-stats {
    display: flex;
    justify-content: space-around;
    padding: 16px 0;

    .stat-item {
      text-align: center;

      .stat-value {
        font-size: 20px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      .stat-label {
        margin-top: 4px;
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}

.info-card,
.security-card {
  margin-bottom: 20px;

  .card-header {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;

  .security-info {
    .security-title {
      margin-bottom: 4px;
      font-size: 16px;
      font-weight: 500;
      color: var(--el-text-color-primary);
    }

    .security-desc {
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }
  }
}

.el-descriptions {
  .el-descriptions__label {
    font-weight: 500;
    color: var(--el-text-color-regular);
  }

  .el-descriptions__content {
    color: var(--el-text-color-primary);
  }

  .gender-icon {
    margin-left: 8px;
    font-size: 16px;

    &.male {
      color: #409eff;
    }

    &.female {
      color: #f56c6c;
    }
  }
}

.el-dialog {
  .el-dialog__header {
    padding: 20px;
    margin: 0;
    border-bottom: 1px solid var(--el-border-color-light);
  }

  .el-dialog__body {
    padding: 30px 20px;
  }

  .el-dialog__footer {
    padding: 20px;
    border-top: 1px solid var(--el-border-color-light);
  }
}

// 반응형 적응
@media (max-width: 768px) {
  .profile-container {
    padding: 10px;
  }

  .el-col {
    width: 100%;
  }
}
</style>
