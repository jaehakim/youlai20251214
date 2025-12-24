<template>
  <div class="app-container">
    <!-- 검색 영역 -->
    <div class="search-container">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item prop="keywords" label="키워드">
          <el-input
            v-model="queryParams.keywords"
            placeholder="원본 명령어/함수명/사용자명"
            clearable
            style="width: 220px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item prop="provider" label="AI 제공자">
          <el-select
            v-model="queryParams.provider"
            placeholder="선택하세요"
            clearable
            style="width: 140px"
          >
            <el-option label="Qwen" value="qwen" />
            <el-option label="OpenAI" value="openai" />
            <el-option label="DeepSeek" value="deepseek" />
            <el-option label="Gemini" value="gemini" />
          </el-select>
        </el-form-item>

        <el-form-item prop="model" label="AI 모델">
          <el-input
            v-model="queryParams.model"
            placeholder="예: qwen-plus"
            clearable
            style="width: 160px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>

        <el-form-item prop="parseSuccess" label="파싱 상태">
          <el-select
            v-model="queryParams.parseSuccess"
            placeholder="선택하세요"
            clearable
            style="width: 140px"
          >
            <el-option label="성공" :value="true" />
            <el-option label="실패" :value="false" />
          </el-select>
        </el-form-item>

        <el-form-item prop="executeStatus" label="실행 상태">
          <el-select
            v-model="queryParams.executeStatus"
            placeholder="선택하세요"
            clearable
            style="width: 140px"
          >
            <el-option label="대기 중" value="pending" />
            <el-option label="성공" value="success" />
            <el-option label="실패" value="failed" />
          </el-select>
        </el-form-item>

        <el-form-item prop="isDangerous" label="위험 작업">
          <el-select
            v-model="queryParams.isDangerous"
            placeholder="선택하세요"
            clearable
            style="width: 140px"
          >
            <el-option label="예" :value="true" />
            <el-option label="아니오" :value="false" />
          </el-select>
        </el-form-item>

        <el-form-item prop="createTime" label="생성 시간">
          <el-date-picker
            v-model="queryParams.createTime"
            :editable="false"
            type="daterange"
            range-separator="~"
            start-placeholder="시작 시간"
            end-placeholder="종료 시간"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </el-form-item>

        <el-form-item class="search-buttons">
          <el-button type="primary" icon="Search" @click="handleQuery">검색</el-button>
          <el-button icon="Refresh" @click="handleResetQuery">초기화</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 데이터 표 -->
    <el-card shadow="hover" class="data-table">
      <el-table
        v-loading="loading"
        :data="pageData"
        highlight-current-row
        border
        class="data-table__content"
      >
        <el-table-column label="생성 시간" prop="createTime" width="180" />
        <el-table-column label="사용자명" prop="username" width="120" />
        <el-table-column
          label="원본 명령어"
          prop="originalCommand"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column
          label="함수명"
          prop="functionName"
          min-width="160"
          show-overflow-tooltip
        />
        <el-table-column label="AI 제공자" prop="provider" width="120" />
        <el-table-column label="AI 모델" prop="model" width="160" show-overflow-tooltip />
        <el-table-column label="파싱 상태" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="row.parseSuccess ? 'success' : 'danger'" size="small">
              {{ row.parseSuccess ? "성공" : "실패" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="실행 상태" width="110" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.executeStatus" :type="statusTagType[row.executeStatus]" size="small">
              {{ statusText[row.executeStatus] }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="위험" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isDangerous" type="warning" size="small">위험</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="신뢰도" prop="confidence" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.confidence !== undefined && row.confidence !== null">
              {{ (row.confidence * 100).toFixed(0) }}%
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="파싱 소요시간(ms)" prop="parseTime" width="120" align="center" />
        <el-table-column label="실행 소요시간(ms)" prop="executionTime" width="120" align="center" />
        <el-table-column label="IP 주소" prop="ipAddress" width="140" />
        <el-table-column label="작업" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewDetail(row)">
              상세정보
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-if="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="fetchData"
      />
    </el-card>

    <!-- 상세다이얼로그 -->
    <el-dialog v-model="detailDialogVisible" title="AI 명령레코드상세" width="880px" append-to-body>
      <el-descriptions v-if="currentRow" :column="2" border>
        <el-descriptions-item label="레코드ID">
          {{ currentRow.id }}
        </el-descriptions-item>
        <el-descriptions-item label="사용자명">
          {{ currentRow.username }}
        </el-descriptions-item>

        <el-descriptions-item label="AI提供商">
          {{ currentRow.provider || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="AI模型">
          {{ currentRow.model || "-" }}
        </el-descriptions-item>

        <el-descriptions-item label="파싱상태">
          <el-tag :type="currentRow.parseSuccess ? 'success' : 'danger'" size="small">
            {{ currentRow.parseSuccess ? "성공" : "실패" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="置信度">
          <span v-if="currentRow.confidence !== undefined && currentRow.confidence !== null">
            {{ (currentRow.confidence * 100).toFixed(0) }}%
          </span>
          <span v-else>-</span>
        </el-descriptions-item>

        <el-descriptions-item label="파싱耗시">
          {{ formatNumber(currentRow.parseTime) }} ms
        </el-descriptions-item>
        <el-descriptions-item label="Token통계计">
          출력입 {{ currentRow.inputTokens || 0 }} / 출력 {{ currentRow.outputTokens || 0 }} / 총计
          {{ currentRow.totalTokens || 0 }}
        </el-descriptions-item>

        <el-descriptions-item label="원본명령" :span="2">
          <el-input :model-value="currentRow.originalCommand" type="textarea" :rows="2" readonly />
        </el-descriptions-item>

        <el-descriptions-item v-if="currentRow.explanation" label="AI설명" :span="2">
          {{ currentRow.explanation }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentRow.functionCalls" label="함수개호출" :span="2">
          <el-input
            :model-value="formatJson(currentRow.functionCalls)"
            type="textarea"
            :rows="6"
            readonly
          />
        </el-descriptions-item>

        <el-descriptions-item v-if="currentRow.parseErrorMessage" label="파싱오류" :span="2">
          <el-alert :title="currentRow.parseErrorMessage" type="error" :closable="false" />
        </el-descriptions-item>

        <el-descriptions-item label="함수개이름">
          {{ currentRow.functionName || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="실행 상태">
          <el-tag
            v-if="currentRow.executeStatus"
            :type="statusTagType[currentRow.executeStatus]"
            size="small"
          >
            {{ statusText[currentRow.executeStatus] }}
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>

        <el-descriptions-item label="실행耗시">
          {{ formatNumber(currentRow.executionTime) }} ms
        </el-descriptions-item>
        <el-descriptions-item label="影响행개">
          {{ formatNumber(currentRow.affectedRows) }}
        </el-descriptions-item>

        <el-descriptions-item v-if="currentRow.functionArguments" label="실행파라미터" :span="2">
          <el-input
            :model-value="formatJson(currentRow.functionArguments)"
            type="textarea"
            :rows="4"
            readonly
          />
        </el-descriptions-item>

        <el-descriptions-item v-if="currentRow.executeResult" label="실행 결과" :span="2">
          <el-input
            :model-value="formatJson(currentRow.executeResult)"
            type="textarea"
            :rows="4"
            readonly
          />
        </el-descriptions-item>

        <el-descriptions-item v-if="currentRow.executeErrorMessage" label="실행오류" :span="2">
          <el-alert :title="currentRow.executeErrorMessage" type="error" :closable="false" />
        </el-descriptions-item>

        <el-descriptions-item label="위험작업">
          <el-tag v-if="currentRow.isDangerous" type="warning" size="small">위험작업</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="여부확인">
          <span v-if="currentRow.requiresConfirmation">
            {{ currentRow.userConfirmed ? "이미확인" : "待확인" }}
          </span>
          <span v-else>-</span>
        </el-descriptions-item>

        <el-descriptions-item label="IP주소">
          {{ currentRow.ipAddress || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="페이지라우팅">
          {{ currentRow.currentRoute || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="User-Agent" :span="2">
          {{ currentRow.userAgent || "-" }}
        </el-descriptions-item>

        <el-descriptions-item label="생성 시간">
          {{ currentRow.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="업데이트시간">
          {{ currentRow.updateTime || "-" }}
        </el-descriptions-item>
        <el-descriptions-item label="비고" :span="2">
          {{ currentRow.remark || "-" }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailDialogVisible = false">닫기</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "AiCommandRecord",
  inheritAttrs: false,
});

import AiCommandApi, { AiCommandRecordVO, AiCommandRecordPageQuery } from "@/api/ai";
import { onMounted, reactive, ref } from "vue";

const queryFormRef = ref();

const loading = ref(false);
const total = ref(0);

const queryParams = reactive<AiCommandRecordPageQuery>({
  pageNum: 1,
  pageSize: 10,
  keywords: "",
  provider: "",
  model: "",
  parseSuccess: undefined,
  executeStatus: "",
  isDangerous: undefined,
  createTime: ["", ""],
});

const pageData = ref<AiCommandRecordVO[]>([]);

const detailDialogVisible = ref(false);
const currentRow = ref<AiCommandRecordVO>();

const statusText: Record<string, string> = {
  pending: "待실행",
  success: "성공",
  failed: "실패",
};

const statusTagType: Record<string, "info" | "success" | "danger"> = {
  pending: "info",
  success: "success",
  failed: "danger",
};

function fetchData() {
  loading.value = true;
  AiCommandApi.getCommandRecordPage(queryParams)
    .then((data) => {
      pageData.value = data.list || [];
      total.value = data.total || 0;
    })
    .finally(() => {
      loading.value = false;
    });
}

function handleQuery() {
  queryParams.pageNum = 1;
  fetchData();
}

function handleResetQuery() {
  queryFormRef.value?.resetFields();
  queryParams.pageNum = 1;
  fetchData();
}

function handleViewDetail(row: AiCommandRecordVO) {
  currentRow.value = row;
  detailDialogVisible.value = true;
}

function formatJson(jsonStr?: string) {
  if (!jsonStr) return "-";
  try {
    return JSON.stringify(JSON.parse(jsonStr), null, 2);
  } catch {
    return jsonStr;
  }
}

function formatNumber(value?: number | string | null) {
  if (value === undefined || value === null || value === "") {
    return "-";
  }
  return value;
}

onMounted(() => {
  fetchData();
});
</script>

<style lang="scss" scoped>
.search-container {
  margin-bottom: 20px;
}

.search-buttons {
  margin-left: 10px;
}
</style>
