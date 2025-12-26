
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
- 메뉴 라우팅 파라미터 설정 추가 (author by [haoxianrui](https://github.com/haoxianrui))
- 목록 선택 컴포넌트 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 목록 선택 컴포넌트 사용 예제 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- defaultToolbar 구성 파라미터 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 폼 팝업 drawer 모드 지원 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 폼 항목 computed 및 watchEffect 구성 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- switch 속성 수정 지원 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 폼 항목 텍스트 유형 지원 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 목록 열에 show 구성 항목 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 검색 폼 표시/숨김 제어 지원 (author by [cshaptx4869](https://github.com/cshaptx4869))
- input 속성 수정 지원 (author by [cshaptx4869](https://github.com/cshaptx4869))
- search 구성 새로운 함수 기능 확장 (author by [xiudaozhe](https://github.com/xiudaozhe))
- 테이블 새 열 설정 제어 (author by [haoxianrui](https://github.com/haoxianrui))
- 검색 확장 및 축소 추가 (author by [haoxianrui](https://github.com/haoxianrui))
- watch 함수 구성 항목 파라미터 반환 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))

## ♻️ refactor
- 아이콘 선택 컴포넌트 리팩토링 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 목록 선택 컴포넌트 기본 스타일 리팩토링 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 대화 폼 컴포넌트 및 목록 선택 컴포넌트 강화 (author by [cshaptx4869](https://github.com/cshaptx4869))
- routeMeta에 alwaysShow 필드 선언 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 페이지 매김 컴포넌트 오버플로우 스크롤 효과 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 로그인 폼 Ref 유형 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 테이블 새로 고침 버튼 클릭 시 페이지 번호 리셋 안 함 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 필터 열이 특정 높이를 초과하면 스크롤 (author by [cshaptx4869](https://github.com/cshaptx4869))
- initFn 함수 최적화 강화, 폼 항목에 initFn 함수 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- watch, computed, watchEffect 호출 리팩토링 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 작업 성공 알림 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- PageSearch를 card를 컨테이너로 변경, 스타일을 unocss로 변경 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 홈 페이지 로딩 애니메이션 효과 최적화 (author by [haoxianrui](https://github.com/haoxianrui))


## 🐛 fix
- 라우팅 항상 표시 여부 제한을 최상위 디렉토리에만 적용하고 메뉴까지 확장 (author by [haoxianrui](https://github.com/haoxianrui))
- sockjs-client 오류 global is not defined 원인으로 개발 환경에서 WebSocket 페이지를 열 수 없는 문제 수정 (author by [haoxianrui](https://github.com/haoxianrui))
- 사용자 비밀번호 재설정 기능, 최소 6자리 문자 필요 (6자리 미만 로그인 불가 문제) (author by [dreamnyj](https://gitee.com/dreamnyj))
- 시스템 설정 패널 스크롤바 문제 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 폼 슬롯 무효 문제 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- tagsview 새로고침 시 query 손실 문제 수정 (author by [xiudaozhe](https://github.com/xiudaozhe))

## 📦️ build
- NPM 패키지 버전을 최신으로 업그레이드 (author by [haoxianrui](https://github.com/haoxianrui))

## ⚙️ ci
- 스크립트 실행 명령 정리 (author by [cshaptx4869](https://github.com/cshaptx4869))


# 2.10.1 (2024/5/4)

## ♻️ refactor
- CURD 사용 부분 코드를 Hooks로 분리하여 구현 (author by [cshaptx4869](https://github.com/cshaptx4869))
- CURD 가져오기 권한 포인트 식별자명 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- CURD 폼 필드 watch 감시 지원 (author by [cshaptx4869](https://github.com/cshaptx4869))
- CURD 폼 input number 수정자 지원 (author by [cshaptx4869](https://github.com/cshaptx4869))
- CURD 폼 컴포넌트 checkbox 체크박스 지원 (author by [cshaptx4869](https://github.com/cshaptx4869))
- axios 응답 데이터 TS 타입 안내 최적화 (author by [cshaptx4869](https://github.com/cshaptx4869))
- CURD 폼 컴포넌트 사용자 정의 타입의 attrs 전달 값 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 비밀번호 초기화 버튼 권한 식별자 이름 변경 동기화 (author by [haoxianrui](https://github.com/haoxianrui))
- API를 정적 메서드로 리팩토링하여 모듈화 관리 구현, types.ts를 model.ts로 이름 변경하여 인터페이스 모델 정의 저장 (author by [haoxianrui](https://github.com/haoxianrui))


## 🐛 fix
- sockjs-client 오류 global is not defined 원인으로 개발 환경에서 WebSocket 페이지를 열 수 없는 문제 수정 (author by [haoxianrui](https://github.com/haoxianrui))
- 테마 색상 설정이 다크 모드에서 el-table 행 활성화 배경색을 덮어쓰는 문제 수정 (author by [haoxianrui](https://github.com/haoxianrui))
- API 인터페이스 조정으로 인한 호출 페이지 영향 문제 수정 (author by [haoxianrui](https://github.com/haoxianrui))

## 📦️ build
- NPM 패키지 버전을 최신으로 업그레이드 (author by [haoxianrui](https://github.com/haoxianrui))


# 2.10.0 (2024/4/26)
## ✨ feat
- CURD 컴포넌트 캡슐화 (author by [cshaptx4869](https://github.com/cshaptx4869))
- vite-plugin-vue-devtools 플러그인 통합 (author by [Tricker39](https://github.com/Tricker39))
- CURD 설정화 구현 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))


# 2.9.3 (2024/04/14)
## ✨ feat
- vue 파일 코드 스니펫 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 메뉴 hover 배경색을 글로벌 SCSS 변수로 제어하도록 추가 (author by [haoxianrui](https://github.com/haoxianrui))

## ♻️ refactor
- 기본 국제화 강화 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 언어 및 레이아웃 크기 열거형 타입 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 사이드바 상태 열거형 타입 추가 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 레이아웃 열거형으로 리터럴 값 대체 (author by [haoxianrui](https://github.com/haoxianrui))
- 콘솔에서 정적 데이터 순환 렌더링 사용 (author by [april](mailto:april@zen-game.cn))
- 로컬 캐시의 token 변수 이름 변경 (author by [haoxianrui](https://github.com/haoxianrui))
- Vite 환경 변수 타입 선언 완성 (author by [haoxianrui](https://github.com/haoxianrui))

## 🐛 fix
- 빌드 시 iconComponent.name이 undefined일 수 있다는 오류 안내 수정 (author by [wangji1042](https://github.com/wangji1042))
- 브라우저 비밀번호 자동 입력 시 발생할 수 있는 오류 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- eslint 오류 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 모바일에서 왼쪽 메뉴 노드 클릭 후 사이드바 닫기 (author by [haoxianrui](https://github.com/haoxianrui))
- size 타입 단언 추가로 타입 오류 수정 (author by [haoxianrui](https://github.com/haoxianrui))

## 📦️ build
- husky 9.x 버전 적용 (author by [cshaptx4869](https://github.com/cshaptx4869))
- npm 패키지 버전을 최신으로 업그레이드 (author by [haoxianrui](https://github.com/haoxianrui))

# 2.9.2 (2024/03/05)
## ✨ feat
- vscode 개발 확장 추천 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 기본 CURD Mock 인터페이스 완성 (author by [haoxianrui](https://github.com/haoxianrui))

## ♻️ refactor
- login 비밀번호 입력창 기능 구현 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 페이지 진입 애니메이션 효과 약화 (author by [cshaptx4869](https://github.com/cshaptx4869))
- TypeScript Vue Plugin 추천 취소 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 웹사이트 로드 애니메이션 교체 (author by [haoxianrui](https://github.com/haoxianrui))
- 테마 및 테마 색상 감시 최적화, 여러 페이지에서 중복 초기화 방지 (author by [haoxianrui](https://github.com/haoxianrui))

## 🐛 fix
- AppMain 높이가 비고정 헤더에서 정확하지 않아 스크롤바가 나타나는 문제 수정 (author by [haoxianrui](https://github.com/haoxianrui))
- 혼합 모드에서 고정 헤드 활성화 시 스타일 문제 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 설정 패널 폰트 크기 통일 (author by [cshaptx4869](https://github.com/cshaptx4869))

## 📦️build
- env 설정으로 mock 서비스 제어 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 의존성 패키지를 최신 버전으로 업그레이드 (author by [haoxianrui](https://github.com/haoxianrui))
- vite 글로벌 상수로 프로젝트 제목 및 버전 대체 정의 (author by [cshaptx4869](https://github.com/cshaptx4869))

# 2.9.1 (2024/02/28)
## ♻️ refactor
- 프로젝트 설정 버튼을 navbar로 이동 (author by [cshaptx4869](https://github.com/cshaptx4869))
- user 데이터 정의 최적화 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 설정 바의 SVG 아이콘 스타일 통일

## 🐛 fix
- 일부 개발 의존성 정리 (author by [cshaptx4869](https://github.com/cshaptx4869))
- 로그인 페이지 테마 전환 문제 수정 (author by [cshaptx4869](https://github.com/cshaptx4869))

## 🚀 pref

- 이미지 리소스 압축 (author by [cshaptx4869](https://github.com/cshaptx4869))


# 2.9.0 (2024/02/25)

## ✨ feat
- animate.css 애니메이션 라이브러리 도입
- 워터마크 및 설정 추가
- 동적 라우트 메뉴 element plus 아이콘 지원

## ♻️ refactor
- Layout 레이아웃 리팩토링 및 관련 문제 수정
- sass에서 @import 대신 @use로 외부 파일 지시문 사용

## 🐛 fix
- 관리 페이지 일부 팝업 열리지 않는 문제 수정
- 테마 색상 설정 버튼 hover 등 미변경 문제 수정


# 2.8.1 (2024/01/10)

## ✨ feat
- Mock 솔루션을 vite-plugin-mock에서 vite-plugin-mock-dev-server로 교체하여 Vite5에 적용

# 2.8.0 (2023/12/27)

## ⬆️ chore
- Vite4에서 Vite5로 업그레이드

# 2.7.1 (2023/12/12)

## ♻️ refactor
- 패키징 후 파일 분류 수행 (author by [ityangzhiwen](https://gitee.com/ityangzhiwen))

# 2.7.0 (2023/11/19)

## ♻️ refactor
- 코드 리팩토링 최적화
- 자동 가져오기 컴포넌트 타입 선언 파일 경로 수정
- typescript 타입 완성

## 🐛 fix
- 관리 페이지 일부 팝업 열리지 않는 문제 수정


# 2.7.0 (2023/11/19)

## ♻️ refactor
- 코드 리팩토링
- 자동 가져오기 컴포넌트 타입 선언 파일 경로 수정
- typescript 타입 완성

## 🐛 fix
- 관리 페이지 일부 팝업 열리지 않는 문제 수정


# 2.6.3 (2023/10/22)

- 메뉴 관리에 디렉토리에 1급 하위 라우팅만 있는 경우 항상 표시(alwaysShow) 및 라우팅 페이지 캐시(keepAlive) 설정 추가
- API 문서에 swagger, knife4j 추가
- tsx 도입 및 지원
- tsx 도입 및 지원

- 코드 다이어트, 미사용 svg 정리 및 삭제
- 콘솔 스타일 최적화
- 콘솔스타일최적화

- 메뉴 접기 및 펼치기 아이콘 다크 모드 표시 문제 수정
- 메뉴 접기 및 펼치기 아이콘 다크 모드 표시 문제 수정


# 2.6.2 (2023/10/11)

- 테마 설정 미영구화 문제
- UnoCSS 플러그인 스마트 안내 없음
- UnoCSS 플러그인 스마트 안내 없음

- WebSocket 데모 스타일 및 코드 최적화
- 사용자 관리 코드 리팩토링
- 사용자 관리 코드 리팩토링

# 2.6.1 (2023/9/4)

- 내비게이션 상단 모드, 혼합 모드 스타일이 고정 헤더에서 나타나는 스타일 문제 수정
- 고정 헤더 영구화 없음 문제 수정
- 사전이 String 및 Number 타입 호환 표시
- 사전이 String 및 Number 타입 호환 표시
# 2.6.0 (2023/8/24)
# 2.6.0 (2023/8/24)💥💥💥

- 내비게이션 상단 모드, 혼합 모드 지원 (author by [april-tong](https://april-tong.com/))
- 플랫폼 문서 (내장) (author by [april-tong](https://april-tong.com/))
- 플랫폼 문서 (내장) (author by [april-tong](https://april-tong.com/))

# 2.5.0 (2023/8/8)

- Mock 추가 (author by [ygcaicn](https://github.com/ygcaicn))
- 아이콘 데모 (author by [ygcaicn](https://github.com/ygcaicn))
- 아이콘 데모 (author by [ygcaicn](https://github.com/ygcaicn))

- 사전 Number 타입 지원
- 사전지원 Number 타입

# 2.4.1 (2023/7/20)

- vite-plugin-compression 플러그인 통합으로 패키징 최적화 (3.66MB -> 1.58MB) (author by [april-tong](https://april-tong.com/))
- 사전 컴포넌트 캡슐화 (author by [haoxr](https://juejin.cn/user/4187394044331261/posts))
- 사전 컴포넌트 캡슐화 (author by [haoxr](https://juejin.cn/user/4187394044331261/posts))

- 페이지네이션 컴포넌트 hidden 무효
- 서명 백엔드에 저장 불가
- Git 커밋 stylelint 검증 일부 기기 오류
- Git 커밋 stylelint 검증 일부 기기 오류

# 2.4.0 (2023/6/17)

- 신규 컴포넌트 태그 제거 입력창 (author by [april-tong](https://april-tong.com/))
- 신규 컴포넌트 서명 (author by [april-tong](https://april-tong.com/))
- 신규 컴포넌트 표 (author by [april-tong](https://april-tong.com/))
- Echarts 차트 다운로드 기능 추가 (author by [april-tong](https://april-tong.com/))
- Echarts 차트 다운로드 기능 추가 (author by [april-tong](https://april-tong.com/))

- 패키지 관리자를 pnpm으로 제한 및 node 버전 16+
- 사용자 정의 컴포넌트 자동 가져오기 설정
- 검색창 스타일 작성법 최적화
- 검색창 스타일 작성법 최적화

- 사용자 가져오기 시 부서가 숫자로 표시되는 문제 수정
- 사용자 가져오기 시 부서가 숫자로 표시되는 문제 수정

- element-plus 버전 업그레이드 2.3.5 -> 2.3.6
- element-plus 버전업그레이드 2.3.5 → 2.3.6

# 2.3.1 (2023/5/21)

- 컴포넌트 예제 파일 이름 최적화
- 컴포넌트예제파일이름최적화

# 2.2.2 (2023/5/11)

- 컴포넌트 캡슐화 예제에 소스코드 주소 추가
- 역할, 메뉴, 부서, 필드 버튼에 권한 제어 추가
- 역할, 메뉴, 부서, 필드 버튼에 권한 제어 추가


# 2.3.0 (2023/5/12)

- vue 버전 업그레이드 3.2.45 -> 3.3.1 ([CHANGELOG](https://github.com/vuejs/core/blob/main/CHANGELOG.md))
- vite 버전 업그레이드 4.3.1 -> 4.3.5
- vite 버전업그레이드 4.3.1 → 4.3.5

- vue 3.3 버전 신기능 `defineOptions`를 `setup`에서 사용하여 컴포넌트 이름 정의, 중복 `script` 태그 제거
- vue 3.3 버전 신기능 `defineOptions`를 `setup`에서 사용하여 컴포넌트 이름 정의, 중복 `script` 태그 제거

# 2.2.2 (2023/5/11)

- 사용자 신규 커밋에 `vueUse`의 `useDebounceFn` 함수 추가로 버튼 디바운스/쓰로틀 구현
- 사용자 신규 커밋에 `vueUse`의 `useDebounceFn` 함수 추가로 버튼 디바운스/쓰로틀 구현


# 2.2.1 (2023/4/25)

- 아이콘 선택기 컴포넌트에서 `onClickOutside` 사용 시 드롭다운 팝업 요소가 제외되지 않아 입력 검색 불가 문제
- 아이콘 선택기 컴포넌트에서 `onClickOutside` 사용 시 드롭다운 팝업 요소가 제외되지 않아 입력 검색 불가 문제

