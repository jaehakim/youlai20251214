import request from "@/utils/request";

/**
 * AI 명령 요청 파라미터
 */
export interface AiCommandRequest {
  /** 사용자가 입력한 자연어 명령 */
  command: string;
  /** 현재 페이지 라우팅 (컨텍스트용) */
  currentRoute?: string;
  /** 현재 활성화된 컴포넌트 명칭 */
  currentComponent?: string;
  /** 추가 컨텍스트 정보 */
  context?: Record<string, any>;
}

/**
 * 함수 호출 파라미터
 */
export interface FunctionCall {
  /** 함수 명칭 */
  name: string;
  /** 함수 설명 */
  description?: string;
  /** 파라미터 객체 */
  arguments: Record<string, any>;
}

/**
 * AI 명령 파싱 응답
 */
export interface AiCommandResponse {
  /** 파싱 로그 ID (실행 레코드 연결용) */
  parseLogId?: string;
  /** 파싱 성공 여부 */
  success: boolean;
  /** 파싱 후 함수 호출 목록 */
  functionCalls: FunctionCall[];
  /** AI의 이해 및 설명 */
  explanation?: string;
  /** 신뢰도 (0-1) */
  confidence?: number;
  /** 오류 정보 */
  error?: string;
  /** 원본 LLM 응답 (디버깅용) */
  rawResponse?: string;
}

/**
 * AI 명령 실행 요청
 */
export interface AiExecuteRequest {
  /** 연결된 파싱 로그 ID */
  parseLogId?: string;
  /** 원본 명령 (감사용) */
  originalCommand?: string;
  /** 실행할 함수 호출 */
  functionCall: FunctionCall;
  /** 확인 모드: auto=자동 실행, manual=사용자 확인 필요 */
  confirmMode?: "auto" | "manual";
  /** 사용자 확인 플래그 */
  userConfirmed?: boolean;
  /** 멱등성 토큰 (중복 실행 방지) */
  idempotencyKey?: string;
  /** 현재 페이지 라우팅 */
  currentRoute?: string;
}

/**
 * AI 명령 실행 응답
 */
export interface AiExecuteResponse {
  /** 실행 성공 여부 */
  success: boolean;
  /** 실행 결과 데이터 */
  data?: any;
  /** 실행 결과 설명 */
  message?: string;
  /** 영향받은 레코드 수 */
  affectedRows?: number;
  /** 오류 정보 */
  error?: string;
  /** 레코드 ID (추적용) */
  recordId?: string;
  /** 사용자 확인 필요 여부 */
  requiresConfirmation?: boolean;
  /** 확인 프롬프트 정보 */
  confirmationPrompt?: string;
}

export interface AiCommandRecordPageQuery extends PageQuery {
  keywords?: string;
  executeStatus?: string;
  parseSuccess?: boolean;
  userId?: number;
  isDangerous?: boolean;
  provider?: string;
  model?: string;
  functionName?: string;
  createTime?: [string, string];
}

export interface AiCommandRecordVO {
  id: string;
  userId: number;
  username: string;
  originalCommand: string;
  provider?: string;
  model?: string;
  parseSuccess?: boolean;
  functionCalls?: string;
  explanation?: string;
  confidence?: number;
  parseErrorMessage?: string;
  inputTokens?: number;
  outputTokens?: number;
  totalTokens?: number;
  parseTime?: number;
  functionName?: string;
  functionArguments?: string;
  executeStatus?: string;
  executeResult?: string;
  executeErrorMessage?: string;
  affectedRows?: number;
  isDangerous?: boolean;
  requiresConfirmation?: boolean;
  userConfirmed?: boolean;
  executionTime?: number;
  ipAddress?: string;
  userAgent?: string;
  currentRoute?: string;
  createTime?: string;
  updateTime?: string;
  remark?: string;
}

/**
 * AI 명령 API
 */
class AiCommandApi {
  /**
   * 자연어 명령 파싱
   *
   * @param data 명령 요청 파라미터
   * @returns 파싱 결과
   */
  static parseCommand(data: AiCommandRequest): Promise<AiCommandResponse> {
    return request<any, AiCommandResponse>({
      url: "/api/v1/ai/command/parse",
      method: "post",
      data,
    });
  }

  /**
   * 파싱된 명령 실행
   *
   * @param data 실행 요청 파라미터
   * @returns 실행 결과 데이터 (성공시 반환, 실패시 예외 발생)
   */
  static executeCommand(data: AiExecuteRequest): Promise<any> {
    return request<any, any>({
      url: "/api/v1/ai/command/execute",
      method: "post",
      data,
    });
  }

  /**
   * 명령 레코드 페이지네이션 목록 조회
   */
  static getCommandRecordPage(queryParams: AiCommandRecordPageQuery) {
    return request<any, PageResult<AiCommandRecordVO[]>>({
      url: "/api/v1/ai/command/records",
      method: "get",
      params: queryParams,
    });
  }

  /**
   * 명령 실행 취소 (지원하는 경우)
   */
  static rollbackCommand(recordId: string) {
    return request({
      url: `/api/v1/ai/command/rollback/${recordId}`,
      method: "post",
    });
  }
}

export default AiCommandApi;
