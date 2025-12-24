
# 2.11.5 (2024/6/18)

## ✨ feat

- 백엔드 파일 임포트 지원([#142](https://github.com/youlaitech/vue3-element-admin/pull/142)) [@cshaptx4869](https://github.com/cshaptx4869)


## 🐛 fix
- vue-dev-tools 플러그인으로 인한 메뉴 라우팅 전환 프리징, 임시 비활성화 ([28349e](https://github.com/youlaitech/vue3-element-admin/commit/28349efe147afab36531ba148eaac3a448fe6c71)) [@haoxianrui](https://github.com/haoxianrui)



# 2.11.4 (2024/6/16)

## ✨ feat

- 작업 바에 render 구성 파라미터 추가([#138](https://github.com/youlaitech/vue3-element-admin/pull/140)) [@cshaptx4869](https://github.com/cshaptx4869)
- 왼쪽 도구 모음에 type 구성 파라미터 추가([#141](https://github.com/youlaitech/vue3-element-admin/pull/141)) [@diamont1001](https://github.com/diamont1001)

## ♻️ refactor
- 권한 배분 팝업 유형을 drawer로 변경하고 부모-자식 연동 토글 추가([2d9193](https://github.com/youlaitech/vue3-element-admin/commit/2d9193c47fd224f01f82b9c0b2bbeb5e7cb33584)) [@haoxianrui](https://github.com/haoxianrui)



# 2.11.3 (2024/6/11)

## ✨ feat

- 기본 도구 모음 임포트 지원([#138](https://github.com/youlaitech/vue3-element-admin/pull/138)) [@cshaptx4869](https://github.com/cshaptx4869)
- CURD 임포트 예제 추가([19e7bb](https://github.com/youlaitech/vue3-element-admin/commit/eab91effd6a01d5a3d9257249c8d06aa252b3bf8)) [@cshaptx4869](https://github.com/cshaptx4869)

## ♻️ refactor
- 전체 데이터 내보내기 옵션 텍스트 변경([904fec](https://github.com/youlaitech/vue3-element-admin/commit/904fecad65217650482fcdbb10ffb7f3d27eb9ea)) [@cshaptx4869](https://github.com/cshaptx4869)

## 🐛 fix
- 메뉴 목록 el-icon 미적응으로 인한 아이콘 미표시 문제 수정([e72b68](https://github.com/youlaitech/vue3-element-admin/commit/e72b68337562b5a7ea24ad55bbe00023e1266b40)) [@haoxianrui](https://github.com/haoxianrui)

# 2.11.2 (2024/6/8)

## ✨ feat

- 테이블 원격 필터링 지원([#131](https://github.com/youlaitech/vue3-element-admin/pull/131)) [@cshaptx4869](https://github.com/cshaptx4869)
- 태그 제거 입력 창 지원([#132](https://github.com/youlaitech/vue3-element-admin/pull/132)) [@cshaptx4869](https://github.com/cshaptx4869)
- 폼 항목 팁 구성 지원([#133](https://github.com/youlaitech/vue3-element-admin/pull/133)) [@cshaptx4869](https://github.com/cshaptx4869)
- 프론트엔드 내보내기 전체 데이터 지원([#134](https://github.com/youlaitech/vue3-element-admin/pull/134)) [@cshaptx4869](https://github.com/cshaptx4869)
- 선택한 데이터 내보내기 지원([#135](https://github.com/youlaitech/vue3-element-admin/pull/135)) [@cshaptx4869](https://github.com/cshaptx4869)
- 테이블 기본 도구 모음의 내보내기, 검색 버튼에 권한 포인트 제어 추가([883128](https://github.com/youlaitech/vue3-element-admin/commit/8831289b655f2cc086ecdababaa89f8d8a087c42)) [@cshaptx4869](https://github.com/cshaptx4869)
- 탭 제목 동적 설정 지원([23876a](https://github.com/youlaitech/vue3-element-admin/commit/23876aa396143bf77cb5c86af8d6023d9ff6555a)) [@haoxianrui](https://github.com/haoxianrui)

## ♻️ refactor
- 기본 도구 모음 커스텀 지원([#136](https://github.com/youlaitech/vue3-element-admin/pull/136)) [@cshaptx4869](https://github.com/cshaptx4869)
- 전체 내보내기 인터페이스가 구성되지 않았을 때 옵션 숨김([eab91ef](https://github.com/youlaitech/vue3-element-admin/commit/eab91effd6a01d5a3d9257249c8d06aa252b3bf8)) [@cshaptx4869](https://github.com/cshaptx4869)

## 🐛 fix
- 로그아웃 후 redirect 라우팅 파라미터 손실 문제 수정([5626017](https://github.com/youlaitech/vue3-element-admin/commit/562601736731afd20bb1a5140d856f6515720159)) [@haoxianrui](https://github.com/haoxianrui)

# 2.11.1 (2024/6/6)

## ✨ feat

- pagination, request, parseData 구성 파라미터 추가([#119](https://github.com/youlaitech/vue3-element-admin/pull/119)) [@cshaptx4869](https://github.com/cshaptx4869)
- 맨 위로 돌아가기 기능 추가([#120](https://github.com/youlaitech/vue3-element-admin/pull/120)) [@cshaptx4869](https://github.com/cshaptx4869)
- 프론트엔드 내보내기 지원([#126](https://github.com/youlaitech/vue3-element-admin/pull/126)) [@cshaptx4869](https://github.com/cshaptx4869)

## ♻️ refactor
- 레이아웃 스타일 리팩토링 (페이지 떨림 문제 해결)([#116](https://github.com/youlaitech/vue3-element-admin/pull/116)) [@cshaptx4869](https://github.com/cshaptx4869)
- CURD 예제 편집 팝업 크기 변경([#121](https://github.com/youlaitech/vue3-element-admin/pull/121)) [@cshaptx4869](https://github.com/cshaptx4869)
- Vue 플러그인 일일이 등록([#122](https://github.com/youlaitech/vue3-element-admin/pull/122)) [@cshaptx4869](https://github.com/cshaptx4869)
- 기본 테마 시스템 따라가기([#128](https://github.com/youlaitech/vue3-element-admin/pull/128)) [@cshaptx4869](https://github.com/cshaptx4869)
- "scss.lint.unknownAtRules": "ignore" 코드 추가, style에서 @apply 사용 시 unknow at rules 문제 해결([Gitee#22](https://gitee.com/youlaiorg/vue3-element-admin/pulls/22))  [@zjsy521](https://gitee.com/zjsy521)

## 🐛 fix
- 왼쪽 레이아웃 모바일 메뉴 팝업 스타일 수정 ([#117](https://github.com/youlaitech/vue3-element-admin/pull/117))  [@cshaptx4869](https://github.com/cshaptx4869)

- 편집 후 ID를 비우지 않고 새 메뉴 추가 시 덮어쓰기 문제 수정([0e78eeb](https://github.com/youlaitech/vue3-element-admin/commit/0e78eeb75008fa8e9732b1b4e7d7a1ea345c7a1b)) [@haoxianrui](https://github.com/haoxianrui)
- 워터마크 계층 문제 수정([#123](https://github.com/youlaitech/vue3-element-admin/pull/123))  [@cshaptx4869](https://github.com/cshaptx4869)
- 혼합 레이아웃 스타일 문제 수정([#124](https://github.com/youlaitech/vue3-element-admin/pull/124))  [@cshaptx4869](https://github.com/cshaptx4869)
- 팝업 창 닫기 시 clearValidate 미적용 문제 수정([#125](https://github.com/youlaitech/vue3-element-admin/pull/125))  [@andm31](https://github.com/andm31)



# 2.11.0 (2024/5/27)

## ✨ feat
- 메뉴 라우팅 파라미터 설정 추가（author by [haoxianrui](https://github.com/haoxianrui)）
- 목록 선택 컴포넌트 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 목록 선택 컴포넌트 사용 예제 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- defaultToolbar 구성 파라미터 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 폼 팝업 drawer 모드 지원（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 폼 항목 computed 및 watchEffect 구성 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- switch 속성 수정 지원（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 폼 항목 텍스트 유형 지원 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 목록 열에 show 구성 항목 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 검색 폼 표시/숨김 제어 지원（author by [cshaptx4869](https://github.com/cshaptx4869)）
- input 속성 수정 지원（author by [cshaptx4869](https://github.com/cshaptx4869)）
- search 구성 새로운 함수 기능 확장（author by [xiudaozhe](https://github.com/xiudaozhe)）
- 테이블 새 열 설정 제어（author by [haoxianrui](https://github.com/haoxianrui)）
- 검색 확장 및 축소 추가（author by [haoxianrui](https://github.com/haoxianrui)）
- watch 함수 구성 항목 파라미터 반환 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）

## ♻️ refactor
- 아이콘 선택 컴포넌트 리팩토링（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 목록 선택 컴포넌트 기본 스타일 리팩토링（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 대화 폼 컴포넌트 및 목록 선택 컴포넌트 강화（author by [cshaptx4869](https://github.com/cshaptx4869)）
- routeMeta에 alwaysShow 필드 선언 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 페이지 매김 컴포넌트 오버플로우 스크롤 효과 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 로그인 폼 Ref 유형 수정（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 테이블 새로 고침 버튼 클릭 시 페이지 번호 리셋 안 함（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 필터 열이 특정 높이를 초과하면 스크롤（author by [cshaptx4869](https://github.com/cshaptx4869)）
- initFn 함수 최적화 강화, 폼 항목에 initFn 함수 추가（author by [cshaptx4869](https://github.com/cshaptx4869)）
- watch, computed, watchEffect 호출 리팩토링（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 작업 성공 알림 수정（author by [cshaptx4869](https://github.com/cshaptx4869)）
- PageSearch를 card를 컨테이너로 변경, 스타일을 unocss로 변경（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 홈 페이지 로딩 애니메이션 효과 최적화（author by [haoxianrui](https://github.com/haoxianrui)）


## 🐛 fix
- 라우팅是否始终显示不제한只有顶级디렉토리才有의설정，开放至메뉴 （author by [haoxianrui](https://github.com/haoxianrui)）
- sockjs-client 报错 global is not defined 导致개발 환경无法열기 WebSocket 페이지问题修复 （author by [haoxianrui](https://github.com/haoxianrui)）
- 发送사용자重启비밀번호기능，最少为6位문자（小于6位登陆时不允许의问题） （author by [dreamnyj](https://gitee.com/dreamnyj)）
- 修复시스템설정面板스크롤条问题（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 修复폼插槽失效问题（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 수정tagsview새로고침丢失query问题（author by [xiudaozhe](https://github.com/xiudaozhe)）

## 📦️ build
- 升级 NPM 패키지버전至最新 （author by [haoxianrui](https://github.com/haoxianrui)）

## ⚙️ ci
- 规整脚本실행命令（author by [cshaptx4869](https://github.com/cshaptx4869)）


# 2.10.1 (2024/5/4)

## ♻️ refactor
- 抽离CURD의使用部分코드为Hooks实现（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 수정CURD가져오기권한点标识名（author by [cshaptx4869](https://github.com/cshaptx4869)）
- cURD폼필드支持watch감시（author by [cshaptx4869](https://github.com/cshaptx4869)）
- cURD폼input支持number修饰（author by [cshaptx4869](https://github.com/cshaptx4869)）
- cURD폼컴포넌트支持checkbox체크박스框（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 优化axios응답데이터TS타입안내（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 수정CURD폼컴포넌트自定义타입의attrs传값（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 동기초기화비밀번호버튼권한标识重命名（author by [haoxianrui](https://github.com/haoxianrui)）
- 重构API为静态메서드实现모듈化관리，并将types.ts重命名为model.ts用于存放인터페이스模型定义（author by [haoxianrui](https://github.com/haoxianrui)）


## 🐛 fix
- sockjs-client 报错 global is not defined 导致개발 환경无法열기 WebSocket 페이지问题修复 （author by [haoxianrui](https://github.com/haoxianrui)）
- 테마색상설정覆盖暗黑모드下el-table행激活의背景色问题修复 （author by [haoxianrui](https://github.com/haoxianrui)）
- 修复因API 인터페이스调整而影响의调用페이지의问题 （author by [haoxianrui](https://github.com/haoxianrui)）

## 📦️ build
- 升级 NPM 패키지버전至最新 （author by [haoxianrui](https://github.com/haoxianrui)）


# 2.10.0 (2024/4/26)
## ✨ feat
- 封装增删改查컴포넌트（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 集成 vite-plugin-vue-devtools 플러그인（author by [Tricker39](https://github.com/Tricker39)）
- 增加CURD설정化实现（author by [cshaptx4869](https://github.com/cshaptx4869)）


# 2.9.3 (2024/04/14)
## ✨ feat
- 增加vue파일코드片段（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 메뉴 hover 背景色추가값全局SCSS변수进행控制（author by [haoxianrui](https://github.com/haoxianrui)）

## ♻️ refactor
- 加强基础국제화（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 增加언어和布局大小열거형타입（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 增加侧边栏상태열거형타입（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 使用布局열거형替换字面量（author by [haoxianrui](https://github.com/haoxianrui)）
- 콘솔使用静态데이터循环渲染（author by [april](mailto:april@zen-game.cn)）
- 로컬캐시의 token 변수重命名（author by [haoxianrui](https://github.com/haoxianrui)）
- 完善 Vite 环境변수타입声明（author by [haoxianrui](https://github.com/haoxianrui)）

## 🐛 fix
- 修复빌드时안내iconComponent.name可能为undefined의报错 （author by [wangji1042](https://github.com/wangji1042)）
- 修复浏览器비밀번호自动填充时可能存在의报错 （author by [cshaptx4869](https://github.com/cshaptx4869)）
- 修复eslint报错（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 移动端下点击左侧메뉴노드后닫기侧边栏（author by [haoxianrui](https://github.com/haoxianrui)）
- 추가 size 타입断言修复타입报错（author by [haoxianrui](https://github.com/haoxianrui)）

## 📦️ build
- husky9.x버전适配 （author by [cshaptx4869](https://github.com/cshaptx4869)）
- 升级 npm 패키지버전至最新（author by [haoxianrui](https://github.com/haoxianrui)）

# 2.9.2 (2024/03/05)
## ✨ feat
- vscode개발扩展推荐（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 完善基础增删改查Mock인터페이스（author by [haoxianrui](https://github.com/haoxianrui)）

## ♻️ refactor
- 수정login비밀번호框기능实现（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 弱化페이지进入动画效果（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 취소推荐TypeScript Vue Plugin （author by [cshaptx4869](https://github.com/cshaptx4869)）
- 网站로드动画替换 （author by [haoxianrui](https://github.com/haoxianrui)）
- 优化테마和테마色감시，避免多个페이지重复初始化 （author by [haoxianrui](https://github.com/haoxianrui)）

## 🐛 fix
- AppMain 高度在非固定头部不正确导致出现스크롤条问题修复 （author by [haoxianrui](https://github.com/haoxianrui)）
- 修复混合모드开启固定Head时의스타일问题 （author by [cshaptx4869](https://github.com/cshaptx4869)）
- 설정面板统一폰트 크기 （author by [cshaptx4869](https://github.com/cshaptx4869)）

## 📦️build
- 通过env설정控制mock서비스 （author by [cshaptx4869](https://github.com/cshaptx4869)）
- 升级依赖패키지至最新버전 （author by [haoxianrui](https://github.com/haoxianrui)）
- 定义vite全局상수替换프로젝트标题和버전 （author by [cshaptx4869](https://github.com/cshaptx4869)）

# 2.9.1 (2024/02/28)
## ♻️ refactor
- 프로젝트설정버튼移入navbar（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 优化user데이터定义（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 统一설정栏의 SVG 图标风格

## 🐛 fix
- 规整一些개발依赖（author by [cshaptx4869](https://github.com/cshaptx4869)）
- 修复로그인页테마切换问题 （author by [cshaptx4869](https://github.com/cshaptx4869)）

## 🚀 pref

- 压缩이미지资源  （author by [cshaptx4869](https://github.com/cshaptx4869)）


# 2.9.0 (2024/02/25)

## ✨ feat
- 引入 animate.css 动画라이브러리
- 신규水印和설정
- 동적 라우트메뉴支持 element plus 의图标

## ♻️ refactor
- Layout 布局重构和相关问题修复
- sass 使用 @use 替代 @import 引入外部파일指令

## 🐛 fix
- 修复관리페이지部分팝업无法열기问题
- 테마색상설정버튼 hover 等未变化问题修复


# 2.8.1 (2024/01/10)

## ✨ feat
- 替换 Mock 解决方案 vite-plugin-mock 为 vite-plugin-mock-dev-server 适配 Vite5

# 2.8.0 (2023/12/27)

## ⬆️ chore
- 升级 Vite4 至 Vite5

# 2.7.1 (2023/12/12)

## ♻️ refactor
- 将패킹后의파일进행분류 （author by [ityangzhiwen](https://gitee.com/ityangzhiwen)）

# 2.7.0 (2023/11/19)

## ♻️ refactor
- 코드重构优化
- 수정自动가져오기컴포넌트타입声明파일경로
- 完善 typescript 타입

## 🐛 fix
- 修复관리페이지部分팝업无法열기问题


# 2.7.0 (2023/11/19)

## ♻️ refactor
- 코드重构
- 수정自动가져오기컴포넌트타입声明파일경로
- 完善 typescript 타입

## 🐛 fix
- 修复관리페이지部分팝업无法열기问题


# 2.6.3 (2023/10/22)

## ✨ feat
- 메뉴 관리신규디렉토리只有一级子라우팅是否始终显示(alwaysShow)和라우팅페이지是否캐시(keepAlive)의설정
- API 문서신규 swagger、knife4j
- 引入和支持 tsx

## ♻️ refactor
- 코드瘦身，整理并삭제未使用의 svg
- 콘솔스타일优化

## 🐛 fix
- 메뉴栏折叠和확장의图标暗黑모드显示问题修复


# 2.6.2 (2023/10/11)

## 🐛 fix
- 테마설정未持久化问题
- UnoCSS 플러그인无智能안내

## ♻️ refactor
- WebSocket 演示스타일和코드优化
- 사용자 관리코드重构

# 2.6.1 (2023/9/4)

## 🐛 fix
- 导航顶部모드、混合모드스타일在固定 Header 出现의스타일问题修复
- 固定 Header 没有持久化问题修复
- 사전回显兼容 String 和 Number 타입

# 2.6.0 (2023/8/24)💥💥💥

## ✨ feat
- 导航顶部모드、混合모드支持（author by [april-tong](https://april-tong.com/)）
- 平台문서(内嵌)（author by [april-tong](https://april-tong.com/)）

# 2.5.0 (2023/8/8)

## ✨ feat
- 신규 Mock（author by [ygcaicn](https://github.com/ygcaicn)）
- 아이콘 데모（author by [ygcaicn](https://github.com/ygcaicn)）

## 🐛 fix
- 사전支持 Number 타입

# 2.4.1 (2023/7/20)

## ✨ feat
- 整合 vite-plugin-compression 플러그인패킹优化(3.66MB → 1.58MB) （author by [april-tong](https://april-tong.com/)）
- 사전컴포넌트封装（author by [haoxr](https://juejin.cn/user/4187394044331261/posts)）

## 🐛 fix
- 페이지네이션컴포넌트hidden无效
- 서명无法저장至后端
- Git 커밋 stylelint 校验部分机器报错

# 2.4.0 (2023/6/17)

## ✨ feat
- 신규컴포넌트태그 제거입력框（author by [april-tong](https://april-tong.com/)）
- 신규컴포넌트서명（author by [april-tong](https://april-tong.com/)）
- 신규컴포넌트표（author by [april-tong](https://april-tong.com/)）
- Echarts 차트추가다운로드기능 author by [april-tong](https://april-tong.com/)）

## ♻️ refactor
- 제한패키지관리器为 pnpm 和 node 버전16+
- 사용자 정의 컴포넌트自动가져오기설정
- 검색框스타일写法优化

## 🐛 fix
- 사용자가져오기의부서回显成숫자问题修复

## ⬆️ chore
- element-plus 버전升级 2.3.5 → 2.3.6

# 2.3.1 (2023/5/21)

## 🔄 refactor
- 컴포넌트示例파일이름优化

# 2.2.2 (2023/5/11)

## ✨ feat
- 컴포넌트封装示例추가源码주소
- 역할、메뉴、부서、필드버튼추가권한控制


# 2.3.0 (2023/5/12)

## ⬆️ chore
- vue 버전升级 3.2.45 → 3.3.1 ([CHANGELOG](https://github.com/vuejs/core/blob/main/CHANGELOG.md))
- vite 버전升级 4.3.1 → 4.3.5

## ♻️ refactor
- 使用 vue 3.3 버전新기능 `defineOptions` 在 `setup` 定义컴포넌트이름，제거重复의 `script` 태그 제거

# 2.2.2 (2023/5/11)

## ✨ feat
-  사용자신규커밋추가 `vueUse` 의 `useDebounceFn` 함수实现버튼防抖节流


# 2.2.1 (2023/4/25)

## 🐛 fix
- 图标선택器컴포넌트使用 `onClickOutside` 未排除下拉弹出框元素导致无法입력검색。

