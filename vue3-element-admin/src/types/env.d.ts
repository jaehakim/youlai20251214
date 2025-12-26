// https://cn.vitejs.dev/guide/env-and-mode

// TypeScript 타입 힌트가 string으로 표시됨: https://github.com/vitejs/vite/issues/6930
interface ImportMetaEnv {
  /** 애플리케이션 포트 */
  VITE_APP_PORT: number;
  /** 애플리케이션 명칭 */
  VITE_APP_NAME: string;
  /** API 기본 경로(프록시 접두사) */
  VITE_APP_BASE_API: string;
  /** API 주소 */
  VITE_APP_API_URL: string;
  /** Mock 서비스 활성화 여부 */
  VITE_MOCK_DEV_SERVER: boolean;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

/**
 * 플랫폼 명칭, 버전, 실행에 필요한 node 버전, 의존성, 빌드 시간 타입 힌트
 */
declare const __APP_INFO__: {
  pkg: {
    name: string;
    version: string;
    engines: {
      node: string;
    };
    dependencies: Record<string, string>;
    devDependencies: Record<string, string>;
  };
  buildTimestamp: number;
};
