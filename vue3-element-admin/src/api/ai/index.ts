import request from "@/utils/request";

/**
 * AI 명령요청 파라미터
 */
export interface AiCommandRequest {
  /** 사용자输입의자연어명령 */
  command: string;
  /** 当前페이지라우팅（용도上下文） */
  currentRoute?: string;
  /** 当前激活의컴포넌트이름칭 */
  currentComponent?: string;
  /** 额外上下文정보 */
  context?: Record<string, any>;
}

/**
 * 함수호출파라미터
 */
export interface FunctionCall {
  /** 함수이름칭 */
  name: string;
  /** 함수描述 */
  description?: string;
  /** 파라미터객체 */
  arguments: Record<string, any>;
}

/**
 * AI 명령파싱응답
 */
export interface AiCommandResponse {
  /** 파싱日志ID（용도关联실행레코드） */
  parseLogId?: string;
  /** 是否성공파싱 */
  success: boolean;
  /** 파싱후의함수호출목록 */
  functionCalls: FunctionCall[];
  /** AI 의理解및说明 */
  explanation?: string;
  /** 置信度 (0-1) */
  confidence?: number;
  /** 오류정보 */
  error?: string;
  /** 원본 LLM 응답（용도调试） */
  rawResponse?: string;
}

/**
 * AI 명령실행요청
 */
export interface AiExecuteRequest {
  /** 关联의파싱日志ID */
  parseLogId?: string;
  /** 원본명령（용도审계） */
  originalCommand?: string;
  /** 해야실행의함수호출 */
  functionCall: FunctionCall;
  /** 确认模式：auto=자동실행, manual=필요해야사용자确认 */
  confirmMode?: "auto" | "manual";
  /** 사용자确认标志 */
  userConfirmed?: boolean;
  /** 幂等性令牌（방지重复실행） */
  idempotencyKey?: string;
  /** 当前페이지라우팅 */
  currentRoute?: string;
}

/**
 * AI 명령실행응답
 */
export interface AiExecuteResponse {
  /** 是否실행성공 */
  success: boolean;
  /** 실행结果데이터 */
  data?: any;
  /** 실행结果说明 */
  message?: string;
  /** 影响의레코드개 */
  affectedRows?: number;
  /** 오류정보 */
  error?: string;
  /** 레코드ID（용도追踪） */
  recordId?: string;
  /** 필요해야사용자确认 */
  requiresConfirmation?: boolean;
  /** 确认提示정보 */
  confirmationPrompt?: string;
}

export interface AiCommandRecordPageQuery extends PageQuery {
  키words?: string;
  executeStatus?: string;
  parseSuccess?: boolean;
  userId?: number;
  isDangerous?: boolean;
  제공r?: string;
  model?: string;
  functionName?: string;
  createTime?: [string, string];
}

export interface AiCommandRecordVO {
  id: string;
  userId: number;
  username: string;
  originalCommand: string;
  제공r?: string;
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
   * 파싱자연어명령
   *
   * @param data 명령요청 파라미터
   * @returns 파싱结果
   */
  static parseCommand(data: AiCommandRequest): Promise<AiCommandResponse> {
    return request<any, AiCommandResponse>({
      url: "/api/v1/ai/command/parse",
      method: "post",
      data,
    });
  }

  /**
   * 실행已파싱의명령
   *
   * @param data 실행요청 파라미터
   * @returns 실행结果데이터（성공시돌아가기，실패시抛出예외）
   */
  static executeCommand(data: AiExecuteRequest): Promise<any> {
    return request<any, any>({
      url: "/api/v1/ai/command/execute",
      method: "post",
      data,
    });
  }

  /**
   * 조회명령레코드페이지네이션목록
   */
  static getCommandRecordPage(queryParams: AiCommandRecordPageQuery) {
    return request<any, PageResult<AiCommandRecordVO[]>>({
      url: "/api/v1/ai/command/records",
      method: "get",
      params: queryParams,
    });
  }

  /**
   * 撤销명령실행（만약支持）
   */
  static rollbackCommand(recordId: string) {
    return request({
      url: `/api/v1/ai/command/rollback/${recordId}`,
      method: "post",
    });
  }
}

export default AiCommandApi;
