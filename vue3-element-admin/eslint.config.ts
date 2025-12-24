// https://eslint.org/docs/latest/use/configure/configuration-files-new

// 기본 ESLint 설정
import eslint from "@eslint/js";
import globals from "globals";
// TypeScript 지원
import * as typescriptEslint from "typescript-eslint";
// Vue 지원
import pluginVue from "eslint-plugin-vue";
import vueParser from "vue-eslint-parser";
// 코드 스타일 및 형식화
import configPrettier from "eslint-config-prettier";
import prettierPlugin from "eslint-plugin-prettier";

// 자동 가져오기 설정 파싱
import fs from "node:fs";
let autoImportGlobals = {};
try {
  autoImportGlobals =
    JSON.parse(fs.readFileSync("./.eslintrc-auto-import.json", "utf-8")).globals || {};
} catch (error) {
  // 파일이 없거나 파싱 오류 시 빈 객체 사용
  console.warn("Could not load auto-import globals", error);
}

// Element Plus 컴포넌트
const elementPlusComponents = {
  // Element Plus 컴포넌트를 전역 변수로 추가하여 no-undef 오류 방지
  ElInput: "readonly",
  ElSelect: "readonly",
  ElSwitch: "readonly",
  ElCascader: "readonly",
  ElInputNumber: "readonly",
  ElTimePicker: "readonly",
  ElTimeSelect: "readonly",
  ElDatePicker: "readonly",
  ElTreeSelect: "readonly",
  ElText: "readonly",
  ElRadioGroup: "readonly",
  ElCheckboxGroup: "readonly",
  ElOption: "readonly",
  ElRadio: "readonly",
  ElCheckbox: "readonly",
  ElInputTag: "readonly",
  ElForm: "readonly",
  ElFormItem: "readonly",
  ElTable: "readonly",
  ElTableColumn: "readonly",
  ElButton: "readonly",
  ElDialog: "readonly",
  ElPagination: "readonly",
  ElMessage: "readonly",
  ElMessageBox: "readonly",
  ElNotification: "readonly",
  ElTree: "readonly",
};

export default [
  // 무시 파일 설정
  {
    ignores: [
      "**/node_modules/**",
      "**/dist/**",
      "**/*.min.*",
      "**/auto-imports.d.ts",
      "**/components.d.ts",
    ],
  },

  // 기본 JavaScript 설정
  eslint.configs.recommended,

  // Vue 권장 설정
  ...pluginVue.configs["flat/recommended"],

  // TypeScript 권장 설정
  ...typescriptEslint.configs.recommended,

  // 전역 설정
  {
    // 확인할 파일 지정
    files: ["**/*.{js,mjs,cjs,ts,mts,cts,vue}"],
    languageOptions: {
      ecmaVersion: "latest",
      sourceType: "module",
      globals: {
        ...globals.browser, // 브라우저 환경 전역 변수
        ...globals.node, // Node.js 환경 전역 변수
        ...globals.es2022, // ES2022 전역 객체
        ...autoImportGlobals, // 자동 가져온 API 함수
        ...elementPlusComponents, // Element Plus 컴포넌트
        // 전역 타입 정의, TypeScript에서 정의되었지만 ESLint가 인식하지 못하는 문제 해결
        PageQuery: "readonly",
        PageResult: "readonly",
        OptionType: "readonly",
        ApiResponse: "readonly",
        ExcelResult: "readonly",
        TagView: "readonly",
        AppSettings: "readonly",
        __APP_INFO__: "readonly",
      },
    },
    plugins: {
      vue: pluginVue,
      "@typescript-eslint": typescriptEslint.plugin,
    },
    rules: {
      // 기본 규칙
      "no-console": process.env.NODE_ENV === "production" ? "warn" : "off",
      "no-debugger": process.env.NODE_ENV === "production" ? "warn" : "off",

      // ES6+ 규칙
      "prefer-const": "error",
      "no-var": "error",
      "object-shorthand": "error",

      // 최고의 실천
      eqeqeq: "off",
      "no-multi-spaces": "error",
      "no-multiple-empty-lines": ["error", { max: 1, maxBOF: 0, maxEOF: 0 }],

      // TypeScript와 충돌하는 규칙 비활성화
      "no-unused-vars": "off",
      "no-undef": "off",
      "no-redeclare": "off",
      "@typescript-eslint/ban-ts-comment": "off",
    },
  },

  // Vue 파일 특정 설정
  {
    files: ["**/*.vue"],
    languageOptions: {
      parser: vueParser,
      parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
        parser: typescriptEslint.parser,
        extraFileExtensions: [".vue"],
        tsconfigRootDir: __dirname,
      },
    },
    rules: {
      // Vue 규칙
      "vue/multi-word-component-names": "off",
      "vue/no-v-html": "off",
      "vue/require-default-prop": "off",
      "vue/require-explicit-emits": "error",
      "vue/no-unused-vars": "error",
      "vue/no-mutating-props": "off",
      "vue/valid-v-for": "warn",
      "vue/no-template-shadow": "warn",
      "vue/return-in-computed-property": "warn",
      "vue/block-order": [
        "error",
        {
          order: ["template", "script", "style"],
        },
      ],
      "vue/html-self-closing": [
        "error",
        {
          html: {
            void: "always",
            normal: "never",
            component: "always",
          },
          svg: "always",
          math: "always",
        },
      ],
      "vue/component-name-in-template-casing": ["error", "PascalCase"],
      "@typescript-eslint/no-explicit-any": "off",
    },
  },

  // TypeScript 파일 특정 설정
  {
    files: ["**/*.{ts,tsx,mts,cts}"],
    languageOptions: {
      parser: typescriptEslint.parser,
      parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
        project: "./tsconfig.json",
        tsconfigRootDir: __dirname,
      },
    },
    rules: {
      // TypeScript 규칙
      "@typescript-eslint/no-explicit-any": "off", // any 타입 사용 허용, 개발 편의성
      "@typescript-eslint/no-empty-function": "off",
      "@typescript-eslint/no-empty-object-type": "off",
      "@typescript-eslint/ban-ts-comment": "off",
      "@typescript-eslint/no-non-null-assertion": "off",
      "@typescript-eslint/no-unused-vars": "warn", // 경고로 낮춤
      "@typescript-eslint/no-unused-expressions": "warn", // 경고로 낮춤
      "@typescript-eslint/consistent-type-imports": "off", // type import 강제 사용 비활성화
      "@typescript-eslint/no-import-type-side-effects": "error",
    },
  },

  // .d.ts 파일 설정
  {
    files: ["**/*.d.ts"],
    rules: {
      "@typescript-eslint/no-explicit-any": "off",
      "@typescript-eslint/no-unused-vars": "off",
    },
  },

  // CURD 컴포넌트 설정
  {
    files: ["**/components/CURD/**/*.{ts,vue}"],
    rules: {
      "no-unused-vars": "off",
      "@typescript-eslint/no-unused-vars": "off",
      "@typescript-eslint/no-explicit-any": "off",
    },
  },

  // Prettier 통합 (반드시 마지막에 배치)
  {
    plugins: {
      prettier: prettierPlugin, // Prettier의 출력을 ESLint 문제로 보고
    },
    rules: {
      ...configPrettier.rules,
      "prettier/prettier": ["error", {}, { usePrettierrc: true }],
      "arrow-body-style": "off",
      "prefer-arrow-callback": "off",
    },
  },
];
