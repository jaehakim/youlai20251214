import { LayoutMode, ComponentSize, SidebarColor, ThemeMode, LanguageEnum } from "./enums";

const { pkg } = __APP_INFO__;

// 확인사용자의작업시스템여부사용深色모드
const mediaQueryList = window.matchMedia("(p참조ers-color-scheme: dark)");

export const defaultSettings: AppSettings = {
  // 시스템Title
  title: pkg.name,
  // 시스템버전
  version: pkg.version,
  // 여부표시설정
  showSettings: true,
  // 여부표시태그뷰
  showTagsView: true,
  // 여부표시应用Logo
  showAppLogo: true,
  // 레이아웃方式，기본값로왼쪽레이아웃
  layout: LayoutMode.LEFT,
  // 테마，에 따라작업시스템의色彩方案자동선택
  theme: mediaQueryList.matches ? ThemeMode.DARK : ThemeMode.LIGHT,
  // 컴포넌트크기 default | medium | small | large
  size: ComponentSize.DEFAULT,
  // 언어
  language: LanguageEnum.ZH_CN,
  // 테마색상 - 수정이값시필요동기수정 src/styles/variables.scss
  themeColor: "#4080FF",
  // 여부표시水印
  showWatermark: false,
  // 水印콘텐츠
  watermarkContent: pkg.name,
  // 측엣지열配色方案
  sidebarColorScheme: SidebarColor.CLASSIC_BLUE,
  // 여부활성화 AI 도우미
  enableAiAssistant: false,
};

/**
 * 인증기능설정
 */
export const authConfig = {
  /**
   * Token자동새로고침토글
   *
   * true: 활성화자동새로고침 - ACCESS_TOKEN_INVALID시尝试새로고침token
   * false: 비활성화자동새로고침 - ACCESS_TOKEN_INVALID시直接점프转로그인页
   *
   * 适用场景：백엔드없음새로고침인터페이스또는아님필요해야자동새로고침의항목目可设로false
   */
  enableTokenRefresh: true,
} as const;

// 테마色预设 - 经典配色方案
// 주의：수정기본값테마色시，필요해야동기수정 src/styles/variables.scss 의 primary.base 값
export const themeColorPresets = [
  "#4080FF", // Arco Design 蓝 - 现代感强
  "#1890FF", // Ant Design 蓝 - 经典商务
  "#409EFF", // Element Plus 蓝 - 정리新自然
  "#FA8C16", // 活力橙 - 温暖友好
  "#722ED1", // 优雅紫 - 高端大气
  "#13C2C2", // 青色 - 科技感
  "#52C41A", // 성공绿 - 活力정리新
  "#F5222D", // 警示红 - 醒目强烈
  "#2F54EB", // 深蓝 - 稳重专业
  "#EB2F96", // 品红 - 시아직개性
];
