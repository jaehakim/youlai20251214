import { useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, onBeforeUnmount, nextTick } from "vue";
import AiCommandApi from "@/api/ai";

/**
 * AI 작업처리기기（简化版）
 *
 * 可以예简단일함수，也可以예설정객체
 */
export type Ai액션Handler<T = any> =
  | ((args: T) => Promise<void> | void)
  | {
      /** 함수 실행 */
      execute: (args: T) => Promise<void> | void;
      /** 여부필요해야확인（기본값 true） */
      needConfirm?: boolean;
      /** 확인메시지（지원함수또는문자열） */
      confirmMessage?: string | ((args: T) => string);
      /** 성공메시지（지원함수또는문자열） */
      successMessage?: string | ((args: T) => string);
      /** 여부호출백엔드 API（기본값 false，만약로 true 그러면자동호출 executeCommand） */
      callBackendApi?: boolean;
    };

/**
 * AI 작업설정
 */
export interface UseAi액션Options {
  /** 작업매핑테이블：함수이름 -> 처리기기 */
  actionHandlers?: Record<string, Ai액션Handler>;
  /** 데이터새로고침함수（작업완료후호출） */
  onRefresh?: () => Promise<void> | void;
  /** 자동검색처리함수 */
  onAutoSearch?: (키words: string) => void;
  /** 当前라우팅경로（용도실행명령시传递） */
  currentRoute?: string;
}

/**
 * AI 작업 Composable
 *
 * 통계하나처리 AI 도우미传递의작업，지원：
 * - 자동검색（通거치 키words + autoSearch 파라미터）
 * - 실행 AI 작업（通거치 ai액션 파라미터）
 * - 설정化의작업처리기기
 */
export function useAi액션(options: UseAi액션Options = {}) {
  const route = useRoute();
  const { actionHandlers = {}, onRefresh, onAutoSearch, currentRoute = route.path } = options;

  // 용도跟踪여부이미언마운트，방지에언마운트후실행콜백
  let isUnmounted = false;

  /**
   * 실행 AI 작업（통계하나처리확인、실행、피드백流程）
   */
  async function executeAi액션(action: any) {
    if (isUnmounted) return;

    // 兼容两种입参：{ functionName, arguments } 또는 { functionCall: { name, arguments } }
    const fnCall = action.functionCall ?? {
      name: action.functionName,
      arguments: action.arguments,
    };

    if (!fnCall?.name) {
      ElMessage.warning("미识别의 AI 작업");
      return;
    }

    // 조회对应의처리기기
    const handler = actionHandlers[fnCall.name];
    if (!handler) {
      ElMessage.warning(`暂지원하지 않음작업: ${fnCall.name}`);
      return;
    }

    try {
      // 判断처리기기타입（함수 or 설정객체）
      const isSimpleFunction = typeof handler === "function";

      if (isSimpleFunction) {
        // 简단일함수形式：直接실행
        await handler(fnCall.arguments);
      } else {
        // 설정객체形式：통계하나처리확인、실행、피드백
        const config = handler;

        // 1. 확인阶段（기본값필요해야확인）
        if (config.needConfirm !== false) {
          const confirmMsg =
            typeof config.confirmMessage === "function"
              ? config.confirmMessage(fnCall.arguments)
              : config.confirmMessage || "확인실행이작업하나？";

          await ElMessageBox.confirm(confirmMsg, "AI 도우미작업확인", {
            confirmButtonText: "확인실행",
            cancelButtonText: "취소",
            type: "warning",
            dangerouslyUseHTMLString: true,
          });
        }

        // 2. 실행阶段
        if (config.callBackendApi) {
          // 자동호출백엔드 API
          await AiCommandApi.executeCommand({
            originalCommand: action.originalCommand || "",
            confirmMode: "manual",
            userConfirmed: true,
            currentRoute,
            functionCall: {
              name: fnCall.name,
              arguments: fnCall.arguments,
            },
          });
        } else {
          // 실행사용자 정의함수
          await config.execute(fnCall.arguments);
        }

        // 3. 성공피드백
        const successMsg =
          typeof config.successMessage === "function"
            ? config.successMessage(fnCall.arguments)
            : config.successMessage || "작업실행성공";
        ElMessage.success(successMsg);
      }

      // 4. 새로고침데이터
      if (onRefresh) {
        await onRefresh();
      }
    } catch (error: any) {
      // 처리취소작업
      if (error === "cancel") {
        ElMessage.info("이미취소작업");
        return;
      }

      console.error("AI 작업실행실패:", error);
      ElMessage.error(error.message || "작업실행실패");
    }
  }

  /**
   * 실행백엔드명령（通用메서드）
   */
  async function executeCommand(
    functionName: string,
    args: any,
    options: {
      originalCommand?: string;
      confirmMode?: "auto" | "manual";
      needConfirm?: boolean;
      confirmMessage?: string;
    } = {}
  ) {
    const {
      originalCommand = "",
      confirmMode = "manual",
      needConfirm = false,
      confirmMessage,
    } = options;

    // 만약필요해야확인，先표시확인다이얼로그
    if (needConfirm && confirmMessage) {
      try {
        await ElMessageBox.confirm(confirmMessage, "AI 도우미작업확인", {
          confirmButtonText: "확인실행",
          cancelButtonText: "취소",
          type: "warning",
          dangerouslyUseHTMLString: true,
        });
      } catch {
        ElMessage.info("이미취소작업");
        return;
      }
    }

    try {
      await AiCommandApi.executeCommand({
        originalCommand,
        confirmMode,
        userConfirmed: true,
        currentRoute,
        functionCall: {
          name: functionName,
          arguments: args,
        },
      });

      ElMessage.success("작업실행성공");
    } catch (error: any) {
      if (error !== "cancel") {
        throw error;
      }
    }
  }

  /**
   * 처리자동검색
   */
  function handleAutoSearch(키words: string) {
    if (onAutoSearch) {
      onAutoSearch(키words);
    } else {
      ElMessage.info(`AI 도우미이미로귀사자동검색：${키words}`);
    }
  }

  /**
   * 초기화：처리 URL 파라미터의 AI 작업
   *
   * 주의：이메서드오직처리 AI 관련파라미터，아님负责페이지데이터의初始로드
   * 페이지데이터로드应由컴포넌트의 onMounted 훅自행처리
   */
  async function init() {
    if (isUnmounted) return;

    // 확인여부있음 AI 도우미传递의파라미터
    const 키words = route.query.키words as string;
    const autoSearch = route.query.autoSearch as string;
    const ai액션Param = route.query.ai액션 as string;

    // 만약없음任何 AI 파라미터，直接돌아가기
    if (!키words && !autoSearch && !ai액션Param) {
      return;
    }

    // 에 nextTick 내실행，보장페이지데이터이미로드
    nextTick(async () => {
      if (isUnmounted) return;

      // 1. 처리자동검색
      if (autoSearch === "true" && 키words) {
        handleAutoSearch(키words);
      }

      // 2. 처리 AI 작업
      if (ai액션Param) {
        try {
          const ai액션 = JSON.parse(decodeURIComponent(ai액션Param));
          await executeAi액션(ai액션);
        } catch (error) {
          console.error("파싱 AI 작업실패:", error);
          ElMessage.error("AI 작업파라미터파싱실패");
        }
      }
    });
  }

  // 컴포넌트마운트시자동초기화
  onMounted(() => {
    init();
  });

  // 컴포넌트언마운트시정리
  onBeforeUnmount(() => {
    isUnmounted = true;
  });

  return {
    executeAi액션,
    executeCommand,
    handleAutoSearch,
    init,
  };
}
