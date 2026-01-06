/**
 * MS 메신저 스타일 기본 아바타 생성 유틸리티
 * SVG + Base64로 외부 API 없이 구현
 */

/**
 * 문자열을 기반으로 색상 코드 생성 (일관성 있는 색상)
 * @param str - 입력 문자열 (사용자명 등)
 * @returns 16진수 색상 코드
 */
function hashStringToColor(str: string): string {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = ((hash << 5) - hash) + str.charCodeAt(i);
    hash = hash & hash; // 32비트 정수로 변환
  }

  // 해시값을 16진수 색상으로 변환
  const hue = Math.abs(hash) % 360;
  const saturation = 60 + (Math.abs(hash) % 20); // 60-80%
  const lightness = 45 + (Math.abs(hash) % 10); // 45-55%

  return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
}

/**
 * HSL 색상을 16진수로 변환
 * @param hslString - HSL 형식의 색상 문자열
 * @returns 16진수 색상 코드
 */
function hslToHex(hslString: string): string {
  const match = hslString.match(/\d+/g);
  if (!match || match.length < 3) return "#4080FF";

  const h = parseInt(match[0]);
  const s = parseInt(match[1]) / 100;
  const l = parseInt(match[2]) / 100;

  const c = (1 - Math.abs(2 * l - 1)) * s;
  const x = c * (1 - ((h / 60) % 2 - 1));
  const m = l - c / 2;

  let r = 0,
    g = 0,
    b = 0;
  if (h >= 0 && h < 60) {
    r = c;
    g = x;
    b = 0;
  } else if (h >= 60 && h < 120) {
    r = x;
    g = c;
    b = 0;
  } else if (h >= 120 && h < 180) {
    r = 0;
    g = c;
    b = x;
  } else if (h >= 180 && h < 240) {
    r = 0;
    g = x;
    b = c;
  } else if (h >= 240 && h < 300) {
    r = x;
    g = 0;
    b = c;
  } else if (h >= 300 && h < 360) {
    r = c;
    g = 0;
    b = x;
  }

  r = Math.round((r + m) * 255);
  g = Math.round((g + m) * 255);
  b = Math.round((b + m) * 255);

  return `#${((1 << 24) + (r << 16) + (g << 8) + b).toString(16).slice(1).toUpperCase()}`;
}

/**
 * MS 메신저 스타일 기본 아바타 생성 (SVG Base64)
 * @param username - 사용자명
 * @param size - 아바타 크기 (기본값: 200)
 * @returns Data URL 형식의 Base64 SVG
 */
export function generateMSAvatarBase64(username: string, size: number = 200): string {
  if (!username) {
    username = "User";
  }

  // 사용자 이니셜 추출 (첫 글자)
  const initial = username.charAt(0).toUpperCase();

  // 배경색 생성 (사용자명 기반, 일관성 있음)
  const backgroundColor = hashStringToColor(username);
  const hexColor = hslToHex(backgroundColor);

  // SVG 생성
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 ${size} ${size}" width="${size}" height="${size}">
    <!-- 배경색 원 -->
    <circle cx="${size / 2}" cy="${size / 2}" r="${size / 2}" fill="${hexColor}" />

    <!-- 텍스트 (사용자 이니셜) -->
    <text
      x="${size / 2}"
      y="${size / 2 + size * 0.1}"
      font-size="${size * 0.5}"
      font-weight="bold"
      text-anchor="middle"
      dominant-baseline="middle"
      fill="white"
      font-family="Arial, sans-serif"
    >${initial}</text>
  </svg>`;

  // SVG를 Base64로 인코딩
  const base64 = btoa(unescape(encodeURIComponent(svg)));
  return `data:image/svg+xml;base64,${base64}`;
}

/**
 * 배경색만 생성 (MS 메신저 스타일)
 * @param username - 사용자명
 * @returns 16진수 색상 코드
 */
export function generateAvatarBackgroundColor(username: string): string {
  const hslColor = hashStringToColor(username);
  return hslToHex(hslColor);
}

/**
 * 사용자 이니셜 추출
 * @param username - 사용자명
 * @returns 첫 글자 (대문자)
 */
export function extractInitial(username: string): string {
  return username ? username.charAt(0).toUpperCase() : "U";
}
