import vue from "@vitejs/plugin-vue";
import { type ConfigEnv, type UserConfig, loadEnv, defineConfig, PluginOption } from "vite";

import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

import { mockDevServerPlugin } from "vite-plugin-mock-dev-server";

import UnoCSS from "unocss/vite";
import { resolve } from "path";
import { name, version, engines, dependencies, devDependencies } from "./package.json";

// 플랫폼의 이름, 버전, 실행에 필요한 node 버전, 의존성, 빌드 시간의 타입 힌트
const __APP_INFO__ = {
  pkg: { name, version, engines, dependencies, devDependencies },
  buildTimestamp: Date.now(),
};

const pathSrc = resolve(__dirname, "src");

// Vite 설정  https://cn.vitejs.dev/config
export default defineConfig(({ mode }: ConfigEnv): UserConfig => {
  const env = loadEnv(mode, process.cwd());
  const isProduction = mode === "production";

  return {
    resolve: {
      alias: {
        "@": pathSrc,
      },
    },
    css: {
      preprocessorOptions: {
        // 전역 SCSS 변수 정의
        scss: {
          additionalData: `@use "@/styles/variables.scss" as *;`,
        },
      },
    },
    server: {
      host: "0.0.0.0",
      port: +env.VITE_APP_PORT,
      open: true,
      proxy: {
        // /dev-api 요청 프록시
        [env.VITE_APP_BASE_API]: {
          changeOrigin: true,
          // 프록시 대상 주소: https://api.youlai.tech
          target: env.VITE_APP_API_URL,
          rewrite: (path: string) => path.replace(new RegExp("^" + env.VITE_APP_BASE_API), ""),
        },
        // /upload 파일 요청 프록시
        "/upload": {
          changeOrigin: true,
          target: env.VITE_APP_API_URL,
        },
      },
    },
    plugins: [
      vue(),
      ...(env.VITE_MOCK_DEV_SERVER === "true" ? [mockDevServerPlugin()] : []),
      UnoCSS(),
      // API 자동 가져오기
      AutoImport({
        // Vue 함수 가져오기, 예: ref, reactive, toRef 등
        imports: ["vue", "@vueuse/core", "pinia", "vue-router", "vue-i18n"],
        resolvers: [
          // Element Plus 함수 가져오기, 예: ElMessage, ElMessageBox 등
          ElementPlusResolver({ importStyle: "sass" }),
        ],
        eslintrc: {
          enabled: false,
          filepath: "./.eslintrc-auto-import.json",
          globalsPropValue: true,
        },
        vueTemplate: true,
        // 함수 타입 선언 파일 경로 가져오기 (false: 자동 생성 비활성화)
        dts: false,
        // dts: "src/types/auto-imports.d.ts",
      }),
      // 컴포넌트 자동 가져오기
      Components({
        resolvers: [
          // Element Plus 컴포넌트 가져오기
          ElementPlusResolver({ importStyle: "sass" }),
        ],
        // 사용자 지정 컴포넌트 위치 지정 (기본값: src/components)
        dirs: ["src/components", "src/**/components"],
        // 컴포넌트 타입 선언 파일 경로 가져오기 (false: 자동 생성 비활성화)
        dts: false,
        // dts: "src/types/components.d.ts",
      }),
    ] as PluginOption[],
    // 프로젝트에 필요한 컴포넌트 사전 로드
    optimizeDeps: {
      include: [
        "vue",
        "vue-router",
        "element-plus",
        "pinia",
        "axios",
        "@vueuse/core",
        "codemirror-editor-vue3",
        "default-passive-events",
        "exceljs",
        "path-to-regexp",
        "echarts/core",
        "echarts/renderers",
        "echarts/charts",
        "echarts/components",
        "vue-i18n",
        "nprogress",
        "sortablejs",
        "qs",
        "path-browserify",
        "@stomp/stompjs",
        "@element-plus/icons-vue",
        "element-plus/es",
        "element-plus/es/locale/lang/en",
        "element-plus/es/locale/lang/zh-cn",
        "element-plus/es/components/alert/style/index",
        "element-plus/es/components/avatar/style/index",
        "element-plus/es/components/backtop/style/index",
        "element-plus/es/components/badge/style/index",
        "element-plus/es/components/base/style/index",
        "element-plus/es/components/breadcrumb-item/style/index",
        "element-plus/es/components/breadcrumb/style/index",
        "element-plus/es/components/button/style/index",
        "element-plus/es/components/card/style/index",
        "element-plus/es/components/cascader/style/index",
        "element-plus/es/components/checkbox-group/style/index",
        "element-plus/es/components/checkbox/style/index",
        "element-plus/es/components/col/style/index",
        "element-plus/es/components/color-picker/style/index",
        "element-plus/es/components/config-provider/style/index",
        "element-plus/es/components/date-picker/style/index",
        "element-plus/es/components/descriptions-item/style/index",
        "element-plus/es/components/descriptions/style/index",
        "element-plus/es/components/dialog/style/index",
        "element-plus/es/components/divider/style/index",
        "element-plus/es/components/drawer/style/index",
        "element-plus/es/components/dropdown-item/style/index",
        "element-plus/es/components/dropdown-menu/style/index",
        "element-plus/es/components/dropdown/style/index",
        "element-plus/es/components/empty/style/index",
        "element-plus/es/components/form-item/style/index",
        "element-plus/es/components/form/style/index",
        "element-plus/es/components/icon/style/index",
        "element-plus/es/components/image-viewer/style/index",
        "element-plus/es/components/image/style/index",
        "element-plus/es/components/input-number/style/index",
        "element-plus/es/components/input-tag/style/index",
        "element-plus/es/components/input/style/index",
        "element-plus/es/components/link/style/index",
        "element-plus/es/components/loading/style/index",
        "element-plus/es/components/menu-item/style/index",
        "element-plus/es/components/menu/style/index",
        "element-plus/es/components/message-box/style/index",
        "element-plus/es/components/message/style/index",
        "element-plus/es/components/notification/style/index",
        "element-plus/es/components/option/style/index",
        "element-plus/es/components/pagination/style/index",
        "element-plus/es/components/popover/style/index",
        "element-plus/es/components/progress/style/index",
        "element-plus/es/components/radio-button/style/index",
        "element-plus/es/components/radio-group/style/index",
        "element-plus/es/components/radio/style/index",
        "element-plus/es/components/row/style/index",
        "element-plus/es/components/scrollbar/style/index",
        "element-plus/es/components/select/style/index",
        "element-plus/es/components/skeleton-item/style/index",
        "element-plus/es/components/skeleton/style/index",
        "element-plus/es/components/step/style/index",
        "element-plus/es/components/steps/style/index",
        "element-plus/es/components/sub-menu/style/index",
        "element-plus/es/components/switch/style/index",
        "element-plus/es/components/tab-pane/style/index",
        "element-plus/es/components/table-column/style/index",
        "element-plus/es/components/table/style/index",
        "element-plus/es/components/tabs/style/index",
        "element-plus/es/components/tag/style/index",
        "element-plus/es/components/text/style/index",
        "element-plus/es/components/time-picker/style/index",
        "element-plus/es/components/time-select/style/index",
        "element-plus/es/components/timeline-item/style/index",
        "element-plus/es/components/timeline/style/index",
        "element-plus/es/components/tooltip/style/index",
        "element-plus/es/components/tree-select/style/index",
        "element-plus/es/components/tree/style/index",
        "element-plus/es/components/upload/style/index",
        "element-plus/es/components/watermark/style/index",
        "element-plus/es/components/checkbox-button/style/index",
        "element-plus/es/components/space/style/index",
      ],
    },
    // 빌드 설정
    build: {
      chunkSizeWarningLimit: 2000, // 패킹 크기 500kb 초과 경고 제거
      minify: isProduction ? "terser" : false, // 프로덕션 환경에서만 압축 활성화
      terserOptions: isProduction
        ? {
            compress: {
              keep_infinity: true, // Infinity가 1/0으로 압축되는 것을 방지합니다. 이는 Chrome에서 성능 문제를 야기할 수 있습니다.
              drop_console: true, // 프로덕션 환경에서 console.log, console.warn, console.error 등 제거
              drop_debugger: true, // 프로덕션 환경에서 debugger 제거
              pure_funcs: ["console.log", "console.info"], // 지정된 함수 호출 제거
            },
            format: {
              comments: false, // 주석 삭제
            },
          }
        : {},
      rollupOptions: {
        output: {
          // manualChunks: {
          //   "vue-i18n": ["vue-i18n"],
          // },
          // 진입점에서 생성된 청크의 패킹 출력 형식 [name]은 파일명, [hash]는 파일 콘텐츠 해시값을 나타냅니다.
          entryFileNames: "js/[name].[hash].js",
          // 코드 분할 시 생성된 공유 청크의 출력 명명 사용
          chunkFileNames: "js/[name].[hash].js",
          // 정적 자산 출력 명명 사용, [ext]은 파일 확장명을 나타냅니다.
          assetFileNames: (assetInfo: any) => {
            const info = assetInfo.name.split(".");
            let extType = info[info.length - 1];
            // console.log('파일 정보', assetInfo.name)
            if (/\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/i.test(assetInfo.name)) {
              extType = "media";
            } else if (/\.(png|jpe?g|gif|svg)(\?.*)?$/.test(assetInfo.name)) {
              extType = "img";
            } else if (/\.(woff2?|eot|ttf|otf)(\?.*)?$/i.test(assetInfo.name)) {
              extType = "fonts";
            }
            return `${extType}/[name].[hash].[ext]`;
          },
        },
      },
    },
    define: {
      __APP_INFO__: JSON.stringify(__APP_INFO__),
    },
  };
});
