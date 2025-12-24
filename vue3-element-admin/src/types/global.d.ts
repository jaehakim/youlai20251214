declare global {
  /**
   * 응답데이터
   */
  interface ApiResponse<T = any> {
    code: string;
    data: T;
    msg: string;
  }

  /**
   * 페이지네이션조회 파라미터
   */
  interface PageQuery {
    pageNum: number;
    pageSize: number;
  }

  /**
   * 페이지네이션응답객체
   */
  interface PageResult<T> {
    /** 데이터목록 */
    list: T;
    /** 총개 */
    total: number;
  }

  /**
   * 页签객체
   */
  interface TagView {
    /** 页签이름칭 */
    name: string;
    /** 页签标题 */
    title: string;
    /** 页签라우팅경로 */
    path: string;
    /** 页签라우팅完整경로 */
    fullPath: string;
    /** 页签아이콘 */
    icon?: string;
    /** 是否固定页签 */
    affix?: boolean;
    /** 是否开启캐시 */
    keepAlive?: boolean;
    /** 라우팅조회 파라미터 */
    query?: any;
  }

  /**
   * 系统설정
   */
  interface AppSettings {
    /** 系统标题 */
    title: string;
    /** 系统版本 */
    version: string;
    /** 是否显示설정 */
    showSettings: boolean;
    /** 是否显示多标签导航 */
    showTagsView: boolean;
    /** 是否显示应用Logo */
    showAppLogo: boolean;
    /** 导航열레이아웃(left|top|mix) */
    layout: "left" | "top" | "mix";
    /** 테마颜色 */
    themeColor: string;
    /** 테마模式(dark|light) */
    theme: import("@/enums/settings/theme-enum").ThemeMode;
    /** 레이아웃크기(default |large |small) */
    size: string;
    /** 语言( zh-cn| en) */
    language: string;
    /** 是否显示水印 */
    showWatermark: boolean;
    /** 水印内容 */
    watermarkContent: string;
    /** 측엣지열配色方案 */
    sidebarColorScheme: "classic-blue" | "minimal-white";
    /** 是否启用 AI 助手 */
    enableAiAssistant: boolean;
  }

  /**
   * 下拉옵션데이터 타입
   */
  interface OptionType {
    /** 값 */
    value: string | number;
    /** 文本 */
    label: string;
    /** 子목록  */
    children?: OptionType[];
  }

  /**
   * 가져오기结果
   */
  interface ExcelResult {
    /** 상태 코드 */
    code: string;
    /** 无效데이터条개 */
    invalidCount: number;
    /** 유효데이터条개 */
    validCount: number;
    /** 오류정보 */
    messageList: Array<string>;
  }
}
export {};
