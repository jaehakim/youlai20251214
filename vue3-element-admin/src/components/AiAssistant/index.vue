<template>
  <!-- 떠있는 버튼 -->
  <div class="ai-assistant">
    <!-- AI 어시스턴트 아이콘 버튼 -->
    <el-button
      v-if="!dialogVisible"
      class="ai-fab-button"
      type="primary"
      circle
      size="large"
      @click="handleOpen"
    >
      <div class="i-svg:ai ai-icon" />
    </el-button>

    <!-- AI 대화 상자 -->
    <el-dialog
      v-model="dialogVisible"
      title="AI 스마트 어시스턴트"
      width="600px"
      :close-on-click-modal="false"
      draggable
      class="ai-assistant-dialog"
    >
      <template #header>
        <div class="dialog-header">
          <div class="i-svg:ai header-icon" />
          <span class="title">AI 스마트 어시스턴트</span>
        </div>
      </template>

      <!-- 명령 입력 -->
      <div class="command-input">
        <el-input
          v-model="command"
          type="textarea"
          :rows="3"
          placeholder="시도해보세요: test 사용자의 이름을 테스트 사용자로 변경&#10;또는: 사용자 관리로 이동&#10;Ctrl+Enter를 눌러 빠르게 보냄"
          :disabled="loading"
          @keydown.ctrl.enter="handleExecute"
        />
      </div>

      <!-- 빠른 명령 예제 -->
      <div class="quick-commands">
        <div class="section-title">💡 이 명령들을 시도해보세요:</div>
        <el-tag
          v-for="example in examples"
          :key="example"
          class="command-tag"
          @click="command = example"
        >
          {{ example }}
        </el-tag>
      </div>

      <!-- AI 응답 결과 -->
      <div v-if="response" class="ai-response">
        <el-alert :title="response.explanation" type="success" :closable="false" show-icon />

        <!-- 실행할 작업 -->
        <div v-if="response.action" class="action-preview">
          <div class="action-title">🎯 실행할 예정:</div>
          <div class="action-content">
            <div v-if="response.action.type === 'navigate'">
              <el-icon><Position /></el-icon>
              이동할 위치:
              <strong>{{ response.action.pageName }}</strong>
              <span v-if="response.action.query" class="query-info">
                그리고 검색:
                <el-tag type="warning" size="small">{{ response.action.query }}</el-tag>
              </span>
            </div>
            <div v-if="response.action.type === 'navigate-and-execute'">
              <el-icon><Position /></el-icon>
              이동할 위치:
              <strong>{{ response.action.pageName }}</strong>
              <span v-if="response.action.query" class="query-info">
                그리고 검색:
                <el-tag type="warning" size="small">{{ response.action.query }}</el-tag>
              </span>
              <el-divider direction="vertical" />
              <el-icon><Tools /></el-icon>
              실행:
              <strong>{{ response.action.functionCall.name }}</strong>
            </div>
            <div v-if="response.action.type === 'execute'">
              <el-icon><Tools /></el-icon>
              실행:
              <strong>{{ response.action.functionName }}</strong>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleClose">취소</el-button>
          <el-button type="primary" :loading="loading" @click="handleExecute">
            <el-icon><MagicStick /></el-icon>
            명령 실행
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import AiCommandApi from "@/api/ai";

type ToolFunctionCall = {
  name: string;
  arguments: Record<string, any>;
};

// 통합 동작 설명 ("이동", "이동+실행", "실행만" 세 가지 시나리오 구분)
type Ai액션 =
  | {
      type: "navigate";
      path: string;
      pageName: string;
      query?: string;
    }
  | {
      type: "navigate-and-execute";
      path: string;
      pageName: string;
      query?: string;
      functionCall: ToolFunctionCall;
    }
  | {
      type: "execute";
      functionName: string;
      functionCall: ToolFunctionCall;
    };

type AiResponse = {
  explanation: string;
  action: Ai액션 | null;
};

const router = useRouter();

// 상태 관리
const dialogVisible = ref(false);
const command = ref("");
const loading = ref(false);
const response = ref<AiResponse | null>(null);

// 빠른 명령 예제
const examples = [
  "test 사용자의 이름을 테스트 사용자로 변경",
  "이름이 Zhang San인 사용자 정보 가져오기",
  "사용자 관리로 이동",
  "역할 관리 페이지 열기",
];

// 대화 상자 열기
const handleOpen = () => {
  dialogVisible.value = true;
  command.value = "";
  response.value = null;
};

// 대화 상자 닫기
const handleClose = () => {
  dialogVisible.value = false;
  command.value = "";
  response.value = null;
};

// 명령 실행
const handleExecute = async () => {
  const rawCommand = command.value.trim();
  if (!rawCommand) {
    ElMessage.warning("명령을 입력해주세요");
    return;
  }

  // AI를 호출할 필요 없는 순수 이동 명령을 먼저 감지
  const directNavigation = tryDirectNavigate(rawCommand);
  if (directNavigation && directNavigation.action) {
    response.value = directNavigation;
    await execute액션(directNavigation.action);
    return;
  }

  loading.value = true;

  try {
    // AI API를 호출하여 명령 분석
    const result = await AiCommandApi.parseCommand({
      command: rawCommand,
      currentRoute: router.currentRoute.value.path,
      currentComponent: router.currentRoute.value.name as string,
      context: {
        userRoles: [],
      },
    });

    if (!result.success) {
      ElMessage.error(result.error || "명령 분석 실패");
      return;
    }

    // AI가 반환한 작업 유형 분석
    const action = parse액션(result, rawCommand);
    response.value = {
      explanation: result.explanation ?? "명령 분석 완료, 작업 실행 준비 중",
      action,
    };

    // 사용자 확인 후 실행 대기
    if (action) {
      await execute액션(action);
    }
  } catch (error: any) {
    console.error("AI 명령 실행 실패:", error);
    ElMessage.error(error.message || "명령 실행 실패");
  } finally {
    loading.value = false;
  }
};

// 라우트 구성 맵 (확장 가능)
const routeConfig = [
  { keywords: ["사용자", "user", "user list"], path: "/system/user", name: "사용자 관리" },
  { keywords: ["역할", "role"], path: "/system/role", name: "역할 관리" },
  { keywords: ["메뉴", "menu"], path: "/system/menu", name: "메뉴 관리" },
  { keywords: ["부서", "dept"], path: "/system/dept", name: "부서 관리" },
  { keywords: ["사전", "dict"], path: "/system/dict", name: "사전 관리" },
  { keywords: ["로그", "log"], path: "/system/log", name: "시스템 로그" },
];

// 함수 이름으로 라우트 추론 (예: getUserInfo -> /system/user)
const normalizeText = (text: string) => text.replace(/\s+/g, " ").trim().toLowerCase();

const inferRouteFromFunction = (functionName: string) => {
  const fnLower = normalizeText(functionName);
  for (const config of routeConfig) {
    // 함수 이름에 키워드가 포함되는지 확인 (예: getUserInfo는 user 포함)
    if (config.keywords.some((kw) => fnLower.includes(kw.toLowerCase()))) {
      return { path: config.path, name: config.name };
    }
  }
  return null;
};

// 명령 텍스트로 라우트 매칭
const matchRouteFromCommand = (cmd: string) => {
  const normalized = normalizeText(cmd);
  for (const config of routeConfig) {
    if (config.keywords.some((kw) => normalized.includes(kw.toLowerCase()))) {
      return { path: config.path, name: config.name };
    }
  }
  return null;
};

const extractKeywordFromCommand = (cmd: string): string => {
  const normalized = normalizeText(cmd);
  // routeConfig에서 동적으로 모든 데이터 타입 키워드 가져오기
  const allKeywords = routeConfig.flatMap((config) =>
    config.keywords.map((kw) => kw.toLowerCase())
  );
  const keywordsPattern = allKeywords.join("|");

  const patterns = [
    new RegExp(`(?:조회|가져오기|검색|찾기|찾음).*?([^\\s，,。]+?)(?:의)?(?:${keywordsPattern})`, "i"),
    new RegExp(`(?:${keywordsPattern}).*?([^\\s，,。]+?)(?:의|정보|세부사항)?`, "i"),
    new RegExp(
      `(?:이름은|이름이|부르는|명칭은|이름)([^\\s，,。]+?)(?:의)?(?:${keywordsPattern})?`,
      "i"
    ),
    new RegExp(`([^\\s，,。]+?)(?:의)?(?:${keywordsPattern})(?:정보|세부사항)?`, "i"),
  ];

  for (const pattern of patterns) {
    const match = normalized.match(pattern);
    if (match && match[1]) {
      let extracted = match[1].trim();
      extracted = extracted.replace(/이름은|이름이|부르는|명칭은|이름|의|정보|세부사항/g, "");
      if (
        extracted &&
        !allKeywords.some((type) => extracted.toLowerCase().includes(type.toLowerCase()))
      ) {
        return extracted;
      }
    }
  }
  return "";
};

const tryDirectNavigate = (rawCommand: string): AiResponse | null => {
  const navigationIntents = ["이동", "열기", "들어가기", "앞으로", "가기", "보기", "확인"];
  const operationIntents = [
    "수정",
    "업데이트",
    "변경",
    "삭제",
    "추가",
    "생성",
    "설정",
    "가져오기",
    "조회",
    "검색",
  ];

  const hasNavigationIntent = navigationIntents.some((keyword) => rawCommand.includes(keyword));
  const hasOperationIntent = operationIntents.some((keyword) => rawCommand.includes(keyword));

  if (!hasNavigationIntent || hasOperationIntent) {
    return null;
  }

  const routeInfo = matchRouteFromCommand(rawCommand);
  if (!routeInfo) {
    return null;
  }

  const keyword = extractKeywordFromCommand(rawCommand);
  const action: Ai액션 = {
    type: "navigate",
    path: routeInfo.path,
    pageName: routeInfo.name,
    query: keyword || undefined,
  };

  return {
    explanation: `이동 명령이 감지되었습니다. ${routeInfo.name}로 이동 중입니다.`,
    action,
  };
};

// AI가 반환한 작업 유형 분석
const parse액션 = (result: any, rawCommand: string): Ai액션 | null => {
  const cmd = normalizeText(rawCommand);
  const primaryCall = result.functionCalls?.[0];
  const functionName = primaryCall?.name;

  // 우선 함수 이름에서 라우트 추론, 다음으로 명령 텍스트에서 매칭
  let routeInfo = functionName ? inferRouteFromFunction(functionName) : null;
  if (!routeInfo) {
    routeInfo = matchRouteFromCommand(cmd);
  }

  const routePath = routeInfo?.path || "";
  const pageName = routeInfo?.name || "";
  const keyword = extractKeywordFromCommand(cmd);

  if (primaryCall && functionName) {
    const fnNameLower = functionName.toLowerCase();

    // 1) 조회 함수 (query/search/list/get) -> 필터링 작업 수행 후 이동
    const isQueryFunction =
      fnNameLower.includes("query") ||
      fnNameLower.includes("search") ||
      fnNameLower.includes("list") ||
      fnNameLower.includes("get");

    if (isQueryFunction) {
      // 통합 keywords 파라미터 사용 (규칙 > 설정)
      const args = (primaryCall.arguments || {}) as Record<string, unknown>;
      const keywords =
        typeof args.keywords === "string" && args.keywords.trim().length > 0
          ? args.keywords
          : keyword;

      if (routePath) {
        return {
          type: "navigate-and-execute",
          path: routePath,
          pageName,
          functionCall: primaryCall,
          query: keywords || undefined,
        };
      }
    }

    // 2) 다른 작업 함수 (수정/삭제/생성/업데이트 등) -> 이동 후 실행
    const isModifyFunction =
      fnNameLower.includes("update") ||
      fnNameLower.includes("modify") ||
      fnNameLower.includes("edit") ||
      fnNameLower.includes("delete") ||
      fnNameLower.includes("remove") ||
      fnNameLower.includes("create") ||
      fnNameLower.includes("add") ||
      fnNameLower.includes("save");

    if (isModifyFunction && routePath) {
      return {
        type: "navigate-and-execute",
        path: routePath,
        pageName,
        functionCall: primaryCall,
      };
    }

    // 3) 다른 매칭되지 않은 함수, 라우트가 있으면 이동, 없으면 실행만 함
    if (routePath) {
      return {
        type: "navigate-and-execute",
        path: routePath,
        pageName,
        functionCall: primaryCall,
      };
    }

    return {
      type: "execute",
      functionName,
      functionCall: primaryCall,
    };
  }

  // 4) 함수 호출 없음, 이동만 수행
  if (routePath) {
    return {
      type: "navigate",
      path: routePath,
      pageName,
      query: keyword || undefined,
    };
  }

  return null;
};

// 타이머 참조 (정리용)
let navigationTimer: ReturnType<typeof setTimeout> | null = null;
let executeTimer: ReturnType<typeof setTimeout> | null = null;

// 작업 실행
const execute액션 = async (action: Ai액션) => {
  // 🎯 신규: 이동 및 작업 실행
  if (action.type === "navigate-and-execute") {
    ElMessage.success(`${action.pageName}로 이동 중이며 작업을 실행합니다...`);

    // 이전 타이머 정리
    if (navigationTimer) {
      clearTimeout(navigationTimer);
    }

    // 이동 및 실행할 작업 정보 전달
    navigationTimer = setTimeout(() => {
      navigationTimer = null;
      const queryParams: any = {
        // URL 파라미터를 통해 AI 작업 정보 전달
        ai액션: encodeURIComponent(
          JSON.stringify({
            functionName: action.functionCall.name,
            arguments: action.functionCall.arguments,
            timestamp: Date.now(),
          })
        ),
      };

      // 검색 키워드가 있으면 함께 전달
      if (action.query) {
        queryParams.keywords = action.query;
        queryParams.autoSearch = "true";
      }

      router.push({
        path: action.path,
        query: queryParams,
      });

      // 대화 상자 닫기
      handleClose();
    }, 800);
    return;
  }

  if (action.type === "navigate") {
    // 이미 대상 페이지에 있는지 확인
    const currentPath = router.currentRoute.value.path;

    if (currentPath === action.path) {
      // 이미 대상 페이지에 있는 경우
      if (action.query) {
        // 검색 키워드가 있으면 현재 페이지에서 검색 실행
        ElMessage.info(`${action.pageName} 페이지에 있습니다. 검색을 실행합니다: ${action.query}`);

        // 라우트 업데이트 트리거, 페이지가 검색을 실행하도록 함
        router.replace({
          path: action.path,
          query: {
            keywords: action.query,
            autoSearch: "true",
            _t: Date.now().toString(), // 타임스탬프 추가하여 강제 새로 고침
          },
        });
      } else {
        // 검색 키워드가 없으면 단지 이동만 하고 알림 제공
        ElMessage.warning(`이미 ${action.pageName} 페이지에 있습니다.`);
      }

      // 대화 상자 닫기
      handleClose();
      return;
    }

    // 대상 페이지에 없으므로 정상적으로 이동
    ElMessage.success(`${action.pageName}로 이동 중입니다...`);

    // 이전 타이머 정리
    if (navigationTimer) {
      clearTimeout(navigationTimer);
    }

    // 사용자가 메시지를 볼 수 있도록 지연
    navigationTimer = setTimeout(() => {
      navigationTimer = null;
      // 이동 및 검색 파라미터 전달
      router.push({
        path: action.path,
        query: action.query
          ? {
              keywords: action.query, // 키워드 파라미터 전달
              autoSearch: "true", // 자동 검색 표시
            }
          : undefined,
      });

      // 대화 상자 닫기
      handleClose();
    }, 800);
  } else if (action.type === "execute") {
    // 함수 호출 실행
    ElMessage.info("기능 개발 중입니다. AI 명령 어시스턴트 페이지로 이동하여 전체 기능을 체험해주세요.");

    // 이전 타이머 정리
    if (executeTimer) {
      clearTimeout(executeTimer);
    }

    // 전체 AI 명령 페이지로 이동
    executeTimer = setTimeout(() => {
      executeTimer = null;
      router.push("/function/ai-command");
      handleClose();
    }, 1000);
  }
};

// 컴포넌트 언마운트 시 타이머 정리
onBeforeUnmount(() => {
  if (navigationTimer) {
    clearTimeout(navigationTimer);
    navigationTimer = null;
  }
  if (executeTimer) {
    clearTimeout(executeTimer);
    executeTimer = null;
  }
});
</script>

<style scoped lang="scss">
.ai-assistant {
  .ai-fab-button {
    position: fixed;
    right: 30px;
    bottom: 80px;
    z-index: 9999;
    width: 60px;
    height: 60px;
    box-shadow: 0 4px 12px rgba(2, 119, 252, 0.4);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 6px 20px rgba(2, 119, 252, 0.6);
      transform: scale(1.1);
    }

    .ai-icon {
      width: 32px;
      height: 32px;
    }
  }
}

.ai-assistant-dialog {
  .dialog-header {
    display: flex;
    gap: 12px;
    align-items: center;

    .header-icon {
      width: 28px;
      height: 28px;
    }

    .title {
      font-size: 18px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }
  }

  .command-input {
    margin-bottom: 16px;
  }

  .quick-commands {
    margin-bottom: 20px;

    .section-title {
      margin-bottom: 8px;
      font-size: 14px;
      color: var(--el-text-color-secondary);
    }

    .command-tag {
      margin-right: 8px;
      margin-bottom: 8px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        transform: translateY(-2px);
      }
    }
  }

  .ai-response {
    margin-top: 16px;

    .action-preview {
      padding: 12px;
      margin-top: 12px;
      background-color: var(--el-fill-color-light);
      border-radius: 8px;

      .action-title {
        margin-bottom: 8px;
        font-size: 14px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      .action-content {
        display: flex;
        gap: 8px;
        align-items: center;
        color: var(--el-text-color-regular);

        .el-icon {
          color: var(--el-color-primary);
        }

        .query-info {
          margin-left: 8px;
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    gap: 12px;
    justify-content: flex-end;
  }
}
</style>
