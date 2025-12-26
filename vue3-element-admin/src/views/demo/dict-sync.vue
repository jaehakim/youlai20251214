<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>사전 WebSocket 실시간 업데이트 데모</span>
          <el-tag :type="wsConnected ? 'success' : 'danger'" size="small" class="ml-2">
            WebSocket {{ wsStatusText }}
          </el-tag>
        </div>
      </template>

      <el-alert type="info" :closable="false" class="mb-4">
        이 예제는 WebSocket을 통한 사전 캐시 실시간 업데이트를 보여줍니다. "남" 성별 사전 항목을 편집하고 저장하면 백엔드는 WebSocket을 통해 모든 클라이언트에 캐시 새로고침을 알립니다.
      </el-alert>

      <el-row :gutter="16">
        <el-col :span="8">
          <el-card shadow="hover" class="dict-card">
            <template #header>
              <div class="flex justify-between items-center">
                <span>성별 사전 항목 - 남</span>
                <el-button type="warning" size="small" @click="loadMaleDict">다시 로드</el-button>
              </div>
            </template>
            <div>
              <div v-if="dictForm" class="dict-form">
                <el-form :model="dictForm" label-width="80px">
                  <el-form-item label="사전 코드">
                    <el-input v-model="dictForm.dictCode" disabled />
                  </el-form-item>
                  <el-form-item label="사전 레이블">
                    <el-input v-model="dictForm.label" />
                  </el-form-item>
                  <el-form-item label="사전 값">
                    <el-input v-model="dictForm.value" disabled />
                  </el-form-item>
                  <el-form-item label="태그 색상">
                    <el-select
                      v-model="dictForm.tagType"
                      placeholder="태그 유형 선택"
                      style="width: 100%"
                    >
                      <el-option value="success" label="success">
                        <el-tag type="success">success</el-tag>
                      </el-option>
                      <el-option value="warning" label="warning">
                        <el-tag type="warning">warning</el-tag>
                      </el-option>
                      <el-option value="danger" label="danger">
                        <el-tag type="danger">danger</el-tag>
                      </el-option>
                      <el-option value="info" label="info">
                        <el-tag type="info">info</el-tag>
                      </el-option>
                      <el-option value="primary" label="primary">
                        <el-tag type="primary">primary</el-tag>
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" :loading="saving" @click="saveDict">저장</el-button>
                    <el-button @click="loadMaleDict">재설정</el-button>
                  </el-form-item>
                </el-form>
              </div>
              <el-empty v-else description="사전 데이터 없음" />
            </div>
          </el-card>
        </el-col>

        <!-- 열2: 사전 구성 요소 표시 -->
        <el-col :span="8">
          <el-card shadow="hover" class="dict-card">
            <template #header>
              <div class="flex justify-between items-center">
                <span>사전 구성 요소 표시</span>
                <el-button type="primary" size="small" @click="refreshDictComponent">
                  수동으로 새로고침
                </el-button>
              </div>
            </template>
            <div class="dict-component-demo">
              <h4 class="mt-4 mb-3">성별 구성 요소</h4>
              <el-radio-group v-model="selectedGender">
                <el-radio
                  v-for="item in dictStore.getDictItems('gender')"
                  :key="item.value"
                  :value="item.value"
                >
                  {{ item.label }}
                </el-radio>
              </el-radio-group>

              <h4 class="mt-4 mb-3">성별 태그</h4>
              <div>
                <el-tag
                  v-for="item in dictStore.getDictItems('gender')"
                  :key="item.value"
                  :type="item.tagType || undefined"
                  class="mr-2"
                >
                  {{ item.label }}
                </el-tag>
              </div>

              <div class="mt-4 pt-3 border-top">
                <div class="text-muted mb-2">선택된 값: {{ selectedGender }}</div>
                <div class="text-muted">마지막 업데이트: {{ lastUpdateTime }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 열3: 사전 캐시 데이터 -->
        <el-col :span="8">
          <el-card shadow="hover" class="dict-card">
            <template #header>
              <div class="flex justify-between items-center">
                <span>사전 캐시 데이터</span>
                <div>
                  <el-tag v-if="dictCacheStatus" type="success" class="ml-2" size="small">
                    캐시됨
                  </el-tag>
                  <el-tag v-else type="danger" class="ml-2" size="small">캐시되지 않음</el-tag>
                </div>
              </div>
            </template>
            <div class="cache-content">
              <pre class="cache-data">{{
                JSON.stringify(dictStore.getDictItems("gender"), null, 2)
              }}</pre>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { useDictStoreHook } from "@/store/modules/dict-store";
import { useDateFormat } from "@vueuse/core";
import DictAPI, { DictItemForm } from "@/api/system/dict-api";
import { useDictSync, DictMessage } from "@/composables";

// 성별 사전 코드
const DICT_CODE = "gender";
// 남성 사전 항목 ID
const MALE_ITEM_ID = "1";

// 사전 저장소
const dictStore = useDictStoreHook();
// 저장 상태
const saving = ref(false);
// 마지막 업데이트 시간
const lastUpdateTime = ref("-");
// 사전 양식 데이터
const dictForm = ref<DictItemForm | null>(null);
// 선택된 성별
const selectedGender = ref("");

// WebSocket 초기화
const dictWebSocket = useDictSync();

// 연결 상태 가져오기
const wsConnected = computed(() => dictWebSocket.isConnected);

// WebSocket 연결 상태 표시 텍스트
const wsStatusText = computed(() => (wsConnected.value ? "연결됨" : "연결 안 됨"));

// WebSocket 정리 함수 저장
let unregisterCallback: (() => void) | null = null;

// 현재 선택한 사전의 캐시 상태
const dictCacheStatus = computed(() => {
  // 사전이 캐시에 있는지 확인
  return dictStore.getDictItems(DICT_CODE).length > 0;
});

// WebSocket 설정
const setupWebSocket = () => {
  // WebSocket 연결 초기화
  dictWebSocket.initWebSocket();

  // 사전 메시지 콜백 등록
  unregisterCallback = dictWebSocket.onDictMessage((message: DictMessage) => {
    // 성별 사전 업데이트에 대한 메시지일 때만 처리
    if (message.dictCode === DICT_CODE) {
      // 마지막 업데이트 시간 업데이트
      lastUpdateTime.value = useDateFormat(new Date(), "YYYY-MM-DD HH:mm:ss").value;

      // 사전 구성 요소 다시 로드 트리거
      nextTick(() => {
        refreshDictComponent();
      });
    }
  });
};

// 사전 구성 요소 새로고침, 강제로 사전 데이터 다시 로드
const refreshDictComponent = async () => {
  // 여기서 사전 데이터를 다시 가져와 온디맨드 로드 트리거
  await dictStore.loadDictItems(DICT_CODE);
  ElMessage.success("사전 구성 요소가 새로고쳐졌습니다");
};

// 남성 사전 양식 데이터 로드
const loadMaleDict = async () => {
  // 남성 사전 항목 양식 데이터 가져오기 - /dicts/gender/items/1/form 인터페이스 사용
  const data = await DictAPI.getDictItemFormData(DICT_CODE, MALE_ITEM_ID);
  dictForm.value = data;
};

// 사전 항목 저장
const saveDict = async () => {
  if (!dictForm.value) return;

  saving.value = true;
  try {
    // dictForm의 타입은 이미 DictItemForm이므로 직접 전달
    await DictAPI.updateDictItem(DICT_CODE, MALE_ITEM_ID, dictForm.value);

    // 시간 업데이트
    lastUpdateTime.value = useDateFormat(new Date(), "YYYY-MM-DD HH:mm:ss").value;

    ElMessage.success("저장 성공, 백엔드가 WebSocket을 통해 모든 클라이언트에 알립니다");
  } catch (error) {
    console.error("사전 항목 저장 실패:", error);
    ElMessage.error("저장 실패");
  } finally {
    saving.value = false;
  }
};

// 구성 요소 마운트 시 성별 사전 로드
onMounted(async () => {
  await loadMaleDict();
  // 초기 사전 데이터 로드
  await dictStore.loadDictItems(DICT_CODE);
  // 선택한 성별을 남성으로 초기화
  selectedGender.value = "1";
  // WebSocket 설정
  setupWebSocket();
});

// 구성 요소 언마운트 시 WebSocket 정리
onUnmounted(() => {
  unregisterCallback?.();
});
</script>

<style scoped>
.dict-card {
  display: flex;
  flex-direction: column;
  height: 600px;
  overflow: hidden;
}

.dict-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.dict-component-demo {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 12px;
}

.cache-content {
  height: 100%;
  overflow: hidden;
}

pre {
  padding: 8px;
  overflow-y: auto;
  word-wrap: break-word;
  white-space: pre-wrap;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.cache-data {
  height: 100%;
  padding: 8px;
  overflow-y: auto;
  font-size: 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.dict-form {
  margin-bottom: 20px;
}

.text-muted {
  font-size: 0.9em;
  color: #909399;
}

.border-top {
  border-top: 1px solid #ebeef5;
}
</style>
