import request from "@/utils/request";

const LOG_BASE_URL = "/api/v1/logs";

const LogAPI = {
  /** 로그 페이지 목록 조회 */
  getPage(queryParams: LogPageQuery) {
    return request<any, PageResult<LogPageVO[]>>({
      url: `${LOG_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /** 방문 추세 조회 */
  getVisitTrend(queryParams: VisitTrendQuery) {
    return request<any, VisitTrendVO>({
      url: `${LOG_BASE_URL}/visit-trend`,
      method: "get",
      params: queryParams,
    });
  },
  /** 방문 통계 조회 */
  getVisitStats() {
    return request<any, VisitStatsVO>({ url: `${LOG_BASE_URL}/visit-stats`, method: "get" });
  },
};

export default LogAPI;

export interface LogPageQuery extends PageQuery {
  /** 검색 키워드 */
  keywords?: string;
  /** 작업 시간 */
  createTime?: [string, string];
}
export interface LogPageVO {
  /** 기본 키 */
  id: string;
  /** 로그 모듈 */
  module: string;
  /** 로그 내용 */
  content: string;
  /** 요청 경로 */
  requestUri: string;
  /** 요청 메서드 */
  method: string;
  /** IP 주소 */
  ip: string;
  /** 지역 */
  region: string;
  /** 브라우저 */
  browser: string;
  /** 운영 체제 */
  os: string;
  /** 실행 시간(밀리초) */
  executionTime: number;
  /** 작업자 */
  operator: string;
}
export interface VisitTrendVO {
  /** 날짜 목록 */
  dates: string[];
  /** 페이지뷰(PV) */
  pvList: number[];
  /** 방문자 수(UV) */
  uvList: number[];
  /** IP 수 */
  ipList: number[];
}
export interface VisitTrendQuery {
  /** 시작 날짜 */
  startDate: string;
  /** 종료 날짜 */
  endDate: string;
}
export interface VisitStatsVO {
  /** 오늘 방문자 수(UV) */
  todayUvCount: number;
  /** 총 방문자 수 */
  totalUvCount: number;
  /** 방문자 수 증가율(전일 대비) */
  uvGrowthRate: number;
  /** 오늘 페이지뷰(PV) */
  todayPvCount: number;
  /** 총 페이지뷰 */
  totalPvCount: number;
  /** 페이지뷰 증가율(전일 대비) */
  pvGrowthRate: number;
}
