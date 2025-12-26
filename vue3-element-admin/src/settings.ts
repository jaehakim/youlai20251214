import { LayoutMode, ComponentSize, SidebarColor, ThemeMode, LanguageEnum } from "./enums";

const { pkg } = __APP_INFO__;

// 사용자의 운영체제가 다크 모드를 사용하는지 확인
const mediaQueryList = window.matchMedia("(prefers-color-scheme: dark)");

export const defaultSettings: AppSettings = {
  // 시스템 제목
  title: pkg.name,
  // 시스템 버전
  version: pkg.version,
  // 설정 표시 여부
  showSettings: true,
  // 태그 뷰 표시 여부
  showTagsView: true,
  // 앱 로고 표시 여부
  showAppLogo: true,
  // 레이아웃 방식, 기본값은 왼쪽 레이아웃
  layout: LayoutMode.LEFT,
  // 테마, 운영체제의 색상 방안에 따라 자동 선택
  theme: mediaQueryList.matches ? ThemeMode.DARK : ThemeMode.LIGHT,
  // 컴포넌트 크기 default | medium | small | large
  size: ComponentSize.DEFAULT,
  // 언어
  language: LanguageEnum.ZH_CN,
  // 테마 색상 - 이 값 수정 시 src/styles/variables.scss도 동기 수정 필요
  themeColor: "#4080FF",
  // 워터마크 표시 여부
  showWatermark: false,
  // 워터마크 내용
  watermarkContent: pkg.name,
  // 사이드바 배색 방안
  sidebarColorScheme: SidebarColor.CLASSIC_BLUE,
  // AI 도우미 활성화 여부
  enableAiAssistant: false,
};

/**
 * 인증 기능 설정
 */
export const authConfig = {
  /**
   * 토큰 자동 새로고침 스위치
   *
   * true: 자동 새로고침 활성화 - ACCESS_TOKEN_INVALID 시 토큰 새로고침 시도
   * false: 자동 새로고침 비활성화 - ACCESS_TOKEN_INVALID 시 로그인 페이지로 바로 이동
   *
   * 적용 시나리오: 백엔드에 새로고침 인터페이스가 없거나 자동 새로고침이 필요 없는 프로젝트는 false로 설정
   */
  enableTokenRefresh: true,
} as const;

// 테마 색상 프리셋 - 클래식 배색 방안
// 주의: 기본 테마 색상 수정 시 src/styles/variables.scss의 primary.base 값도 동기 수정 필요
export const themeColorPresets = [
  "#4080FF", // Arco Design 블루 - 현대적 감각
  "#1890FF", // Ant Design 블루 - 클래식 비즈니스
  "#409EFF", // Element Plus 블루 - 깔끔하고 자연스러움
  "#FA8C16", // 활력 오렌지 - 따뜻하고 친근함
  "#722ED1", // 우아한 퍼플 - 고급스럽고 대기
  "#13C2C2", // 시안 - 테크 느낌
  "#52C41A", // 성공 그린 - 활력 있고 깔끔함
  "#F5222D", // 경고 레드 - 눈에 띄고 강렬함
  "#2F54EB", // 딥 블루 - 안정적이고 전문적
  "#EB2F96", // 마젠타 - 세련되고 개성 있음
];
