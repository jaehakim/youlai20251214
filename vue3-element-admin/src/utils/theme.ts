import { ThemeMode } from "@/enums";

// 보조 함수: 16진수 색상을 RGB로 변환
function hexToRgb(hex: string): [number, number, number] {
  const bigint = parseInt(hex.slice(1), 16);
  return [(bigint >> 16) & 255, (bigint >> 8) & 255, bigint & 255];
}

// 보조 함수: RGB를 16진수 색상으로 변환
function rgbToHex(r: number, g: number, b: number): string {
  return `#${((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1)}`;
}

// 보조 함수: 색상 밝기 조정
/** function adjustBrightness(hex: string, factor: number, theme: string): string {
  const rgb = hexToRgb(hex);
  // 어두운 모드 여부
  const isDarkMode = theme === "dark" ? 0 : 255;
  const newRgb = rgb.map((val) =>
    Math.max(0, Math.min(255, Math.round(val + (isDarkMode - val) * factor)))
  ) as [number, number, number];
  return rgbToHex(...newRgb);
} */

/**
 * 색상 값 진하게
 * @param {String} color 색상 값 문자열
 * @param {Number} level 진하게 하는 정도, 0-1 범위 내
 * @returns {String} 처리된 색상 값 반환
 */
export function getDarkColor(color: string, level: number): string {
  const rgb = hexToRgb(color);
  for (let i = 0; i < 3; i++) rgb[i] = Math.round(20.5 * level + rgb[i] * (1 - level));
  return rgbToHex(rgb[0], rgb[1], rgb[2]);
}

/**
 * 색상 값 밝게
 * @param {String} color 색상 값 문자열
 * @param {Number} level 진하게 하는 정도, 0-1 범위 내
 * @returns {String} 처리된 색상 값 반환
 */
export const getLightColor = (color: string, level: number): string => {
  const rgb = hexToRgb(color);
  for (let i = 0; i < 3; i++) rgb[i] = Math.round(255 * level + rgb[i] * (1 - level));
  return rgbToHex(rgb[0], rgb[1], rgb[2]);
};

/**
 * 테마 색상 생성
 * @param primary 테마 색상
 * @param theme 테마 유형
 */
export function generateThemeColors(primary: string, theme: ThemeMode) {
  const colors: Record<string, string> = {
    primary,
  };

  // 밝은 색 변형 생성
  for (let i = 1; i <= 9; i++) {
    colors[`primary-light-${i}`] =
      theme === ThemeMode.LIGHT
        ? `${getLightColor(primary, i / 10)}`
        : `${getDarkColor(primary, i / 10)}`;
  }

  // 어두운 색 변형 생성
  colors["primary-dark-2"] =
    theme === ThemeMode.LIGHT ? `${getLightColor(primary, 0.2)}` : `${getDarkColor(primary, 0.3)}`;

  return colors;
}

export function applyTheme(colors: Record<string, string>) {
  const el = document.documentElement;

  Object.entries(colors).forEach(([key, value]) => {
    el.style.setProperty(`--el-color-${key}`, value);
  });

  // 테마 색상이 즉시 적용되도록 보장, 강제 다시 렌더링
  requestAnimationFrame(() => {
    // 스타일 재계산 트리거
    el.style.setProperty("--theme-update-trigger", Date.now().toString());
  });
}

/**
 * 어두운 모드 전환
 *
 * @param isDark 어두운 모드 활성화 여부
 */
export function toggleDarkMode(isDark: boolean) {
  if (isDark) {
    document.documentElement.classList.add(ThemeMode.DARK);
  } else {
    document.documentElement.classList.remove(ThemeMode.DARK);
  }
}

/**
 * 밝은 색 테마에서 사이드바 색상 체계 전환
 *
 * @param isBlue 부울값, 진한 파란색 사이드바 색상 체계를 활성화할지 여부를 나타냅니다.
 */
export function toggleSidebarColor(isBuleSidebar: boolean) {
  if (isBuleSidebar) {
    document.documentElement.classList.add("sidebar-color-blue");
  } else {
    document.documentElement.classList.remove("sidebar-color-blue");
  }
}
