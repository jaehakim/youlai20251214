declare global {
  /**
   * 응답 데이터
   */
  interface ApiResponse<T = any> {
    code: string;
    data: T;
    msg: string;
  }

  /**
   * 페이지네이션 조회 파라미터
   */
  interface PageQuery {
    pageNum: number;
    pageSize: number;
  }

  /**
   * 페이지네이션 응답 객체
   */
  interface PageResult<T> {
    /** 데이터 목록 */
    list: T;
    /** 총 개수 */
    total: number;
  }

  /**
   * 페이지 탭 객체
   */
  interface TagView {
    /** 페이지 탭 명칭 */
    name: string;
    /** 페이지 탭 제목 */
    title: string;
    /** 페이지 탭 라우팅 경로 */
    path: string;
    /** 페이지 탭 라우팅 전체 경로 */
    fullPath: string;
    /** 페이지 탭 아이콘 */
    icon?: string;
    /** 페이지 탭 고정 여부 */
    affix?: boolean;
    /** 캐시 활성화 여부 */
    keepAlive?: boolean;
    /** 라우팅 조회 파라미터 */
    query?: any;
  }

  /**
   * 시스템 설정
   */
  interface AppSettings {
    /** 시스템 제목 */
    title: string;
    /** 시스템 버전 */
    version: string;
    /** 설정 표시 여부 */
    showSettings: boolean;
    /** 다중 태그 네비게이션 표시 여부 */
    showTagsView: boolean;
    /** 앱 로고 표시 여부 */
    showAppLogo: boolean;
    /** 네비게이션 열 레이아웃(left|top|mix) */
    layout: "left" | "top" | "mix";
    /** 테마 색상 */
    themeColor: string;
    /** 테마 모드(dark|light) */
    theme: import("@/enums/settings/theme-enum").ThemeMode;
    /** 레이아웃 크기(default|large|small) */
    size: string;
    /** 언어(zh-cn|en) */
    language: string;
    /** 워터마크 표시 여부 */
    showWatermark: boolean;
    /** 워터마크 내용 */
    watermarkContent: string;
    /** 사이드바 색상 구성 */
    sidebarColorScheme: "classic-blue" | "minimal-white";
    /** AI 도우미 활성화 여부 */
    enableAiAssistant: boolean;
  }

  /**
   * 드롭다운 옵션 데이터 타입
   */
  interface OptionType {
    /** 값 */
    value: string | number;
    /** 텍스트 */
    label: string;
    /** 하위 목록 */
    children?: OptionType[];
  }

  /**
   * 가져오기 결과
   */
  interface ExcelResult {
    /** 상태 코드 */
    code: string;
    /** 무효 데이터 개수 */
    invalidCount: number;
    /** 유효 데이터 개수 */
    validCount: number;
    /** 오류 정보 */
    messageList: Array<string>;
  }
}
export {};
