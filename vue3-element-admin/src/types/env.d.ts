// https://cn.vitejs.dev/guide/env-and-mode

// TypeScript 타입提示都로 string： https://github.com/vitejs/vite/issues/6930
interface ImportMetaEnv {
  /** 应用포트 */
  VITE_APP_PORT: number;
  /** 应用이름칭 */
  VITE_APP_NAME: string;
  /** API 기본경로(프록시접두사) */
  VITE_APP_BASE_API: string;
  /** API 주소 */
  VITE_APP_API_URL: string;
  /** 여부开启 Mock 서비스 */
  VITE_MOCK_DEV_SERVER: boolean;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}

/**
 * 플랫폼의이름칭、버전、실행所필요의`node`버전、依赖、빌드시사이의타입提示
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
