import { useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { onMounted, onBeforeUnmount, nextTick } from "vue";
import AiCommandApi from "@/api/ai";

/**
 * AI 작업 처리기(간소화 버전)
 *
 * 단순 함수이거나 설정 객체일 수 있음
 */
export type Ai액션Handler<T = any> =
  | ((args: T) => Promise<void> | void)
  | {
      /** 함수 실행 */
      execute: (args: T) => Promise<void> | void;
      /** 확인 필요 여부(기본값 true) */
      needConfirm?: boolean;
      /** 확인 메시지(함수 또는 문자열 지원) */
      confirmMessage?: string | ((args: T) => string);
      /** 성공 메시지(함수 또는 문자열 지원) */
      successMessage?: string | ((args: T) => string);
      /** 백엔드 API 호출 여부(기본값 false, true면 executeCommand 자동 호출) */
      callBackendApi?: boolean;
    };

/**
 * AI 작업 설정
 */
export interface UseAi액션Options {
  /** 작업 매핑 테이블: 함수 이름 -> 처리기 */
  actionHandlers?: Record<string, Ai액션Handler>;
  /** 데이터 새로고침 함수(작업 완료 후 호출) */
  onRefresh?: () => Promise<void> | void;
  /** 자동 검색 처리 함수 */
  onAutoSearch?: (키words: string) => void;
  /** 현재 라우팅 경로(명령 실행 시 전달용) */
  currentRoute?: string;
}

/**
 * AI 작업 Composable
 *
 * AI 어시스턴트가 전달한 작업 통합 처리, 지원:
 * - 자동 검색(키words + autoSearch 파라미터로)
 * - AI 작업 실행(ai액션 파라미터로)
 * - 설정 가능한 작업 처리기
 */
export function useAi액션(options: UseAi액션Options = {}) {
  const route = useRoute();
  const { actionHandlers = {}, onRefresh, onAutoSearch, currentRoute = route.path } = options;

  // 언마운트 여부 추적용, 언마운트 후 콜백 실행 방지
  let isUnmounted = false;

  /**
   * AI 작업 실행(확인, 실행, 피드백 통합 처리)
   */
  async function executeAi액션(action: any) {
    if (isUnmounted) return;

    // 두 가지 입력 파라미터 형식 호환: { functionName, arguments } 또는 { functionCall: { name, arguments } }
    const fnCall = action.functionCall ?? {
      name: action.functionName,
      arguments: action.arguments,
    };

    if (!fnCall?.name) {
      ElMessage.warning("인식되지 않은 AI 작업");
      return;
    }

    // 해당 처리기 조회
    const handler = actionHandlers[fnCall.name];
    if (!handler) {
      ElMessage.warning(`지원하지 않는 작업: ${fnCall.name}`);
      return;
    }

    try {
      // 처리기 타입 판단(함수 또는 설정 객체)
      const isSimpleFunction = typeof handler === "function";

      if (isSimpleFunction) {
        // 단순 함수 형식: 직접 실행
        await handler(fnCall.arguments);
      } else {
        // 설정 객체 형식: 확인, 실행, 피드백 통합 처리
        const config = handler;

        // 1. 확인 단계(기본값 확인 필요)
        if (config.needConfirm !== false) {
          const confirmMsg =
            typeof config.confirmMessage === "function"
              ? config.confirmMessage(fnCall.arguments)
              : config.confirmMessage || "이 작업을 실행하시겠습니까?";

          await ElMessageBox.confirm(confirmMsg, "AI 어시스턴트 작업 확인", {
            confirmButtonText: "확인 실행",
            cancelButtonText: "취소",
            type: "warning",
            dangerouslyUseHTMLString: true,
          });
        }

        // 2. 실행 단계
        if (config.callBackendApi) {
          // 백엔드 API 자동 호출
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
          // 사용자 정의 함수 실행
          await config.execute(fnCall.arguments);
        }

        // 3. 성공 피드백
        const successMsg =
          typeof config.successMessage === "function"
            ? config.successMessage(fnCall.arguments)
            : config.successMessage || "작업 실행 성공";
        ElMessage.success(successMsg);
      }

      // 4. 데이터 새로고침
      if (onRefresh) {
        await onRefresh();
      }
    } catch (error: any) {
      // 작업 취소 처리
      if (error === "cancel") {
        ElMessage.info("작업이 취소되었습니다");
        return;
      }

      console.error("AI 작업 실행 실패:", error);
      ElMessage.error(error.message || "작업 실행 실패");
    }
  }

  /**
   * 백엔드 명령 실행(공통 메서드)
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

    // 확인이 필요하면 먼저 확인 다이얼로그 표시
    if (needConfirm && confirmMessage) {
      try {
        await ElMessageBox.confirm(confirmMessage, "AI 어시스턴트 작업 확인", {
          confirmButtonText: "확인 실행",
          cancelButtonText: "취소",
          type: "warning",
          dangerouslyUseHTMLString: true,
        });
      } catch {
        ElMessage.info("작업이 취소되었습니다");
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

      ElMessage.success("작업 실행 성공");
    } catch (error: any) {
      if (error !== "cancel") {
        throw error;
      }
    }
  }

  /**
   * 자동 검색 처리
   */
  function handleAutoSearch(키words: string) {
    if (onAutoSearch) {
      onAutoSearch(키words);
    } else {
      ElMessage.info(`AI 어시스턴트가 자동 검색을 수행했습니다: ${키words}`);
    }
  }

  /**
   * 초기화: URL 파라미터의 AI 작업 처리
   *
   * 주의: 이 메서드는 AI 관련 파라미터만 처리하며, 페이지 데이터 초기 로드는 담당하지 않음
   * 페이지 데이터 로드는 컴포넌트의 onMounted 훅에서 직접 처리해야 함
   */
  async function init() {
    if (isUnmounted) return;

    // AI 어시스턴트가 전달한 파라미터 확인
    const 키words = route.query.키words as string;
    const autoSearch = route.query.autoSearch as string;
    const ai액션Param = route.query.ai액션 as string;

    // AI 파라미터가 없으면 바로 반환
    if (!키words && !autoSearch && !ai액션Param) {
      return;
    }

    // nextTick 내에서 실행, 페이지 데이터가 로드되었는지 확인
    nextTick(async () => {
      if (isUnmounted) return;

      // 1. 자동 검색 처리
      if (autoSearch === "true" && 키words) {
        handleAutoSearch(키words);
      }

      // 2. AI 작업 처리
      if (ai액션Param) {
        try {
          const ai액션 = JSON.parse(decodeURIComponent(ai액션Param));
          await executeAi액션(ai액션);
        } catch (error) {
          console.error("AI 작업 파싱 실패:", error);
          ElMessage.error("AI 작업 파라미터 파싱 실패");
        }
      }
    });
  }

  // 컴포넌트 마운트 시 자동 초기화
  onMounted(() => {
    init();
  });

  // 컴포넌트 언마운트 시 정리
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
