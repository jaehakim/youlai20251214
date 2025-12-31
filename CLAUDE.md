# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

**youlai-boot**는 모노레포 구조의 풀스택 관리자 시스템입니다:
- **youlai-boot**: Java 17 + Spring Boot 3.5.6 + Spring Security 6 백엔드
- **vue3-element-admin**: Vue 3 + Vite 7 + TypeScript + Element-Plus 프론트엔드

RBAC 기반 권한 관리 시스템으로 이중 인증 메커니즘(JWT 무상태 + Redis 세션 관리)을 구현합니다.

## 저장소 구조

```
/
├── youlai-boot/          # 백엔드 - Java Spring Boot 애플리케이션
└── vue3-element-admin/   # 프론트엔드 - Vue 3 관리자 애플리케이션
```

## 백엔드 (youlai-boot)

### 아키텍처

백엔드는 명확한 관심사 분리를 가진 모듈식 모노리식 아키텍처를 따릅니다:

- **auth**: 인증 진입점 (로그인 엔드포인트)
- **security**: Spring Security 6 구현 (커스텀 필터 및 토큰 관리)
  - 이중 인증: JWT(무상태) 또는 Redis 기반 세션(다중 기기 제어 가능)
  - 커스텀 필터: `토큰AuthenticationFilter`, `CaptchaValidationFilter`, `RateLimiterFilter`
  - 커스텀 프로바이더: `SmsAuthenticationProvider`, `WxMiniAppCodeAuthenticationProvider`
- **system**: 핵심 비즈니스 모듈 (사용자, 역할, 메뉴, 부서, 사전 관리)
- **platform**: 공유 플랫폼 서비스
  - `codegen`: MyBatis-Plus 코드 생성기
  - `file`: 파일 스토리지 (MinIO/Aliyun OSS/로컬)
  - `mail`: 이메일 서비스
  - `sms`: SMS 서비스 (Aliyun)
  - `websocket`: WebSocket 지원
- **core**: 프레임워크 핵심 컴포넌트 (예외 처리, 검증, 필터, 어스펙트)
- **common**: 공유 유틸리티, 상수, 열거형, 기본 클래스
- **config**: Spring 자동 구성 클래스
- **plugin**: 프레임워크 확장 (Knife4j, MyBatis-Plus)

### 주요 명령어

```bash
# 백엔드 시작 (youlai-boot 디렉토리에서)
cd youlai-boot
mvn spring-boot:run

# 빌드
mvn clean package

# 테스트 실행
mvn test
```

### 개발 환경 설정

1. **데이터베이스**: `youlai-boot/sql/mysql/youlai_boot.sql` SQL 스크립트 실행
2. **설정 파일**: `youlai-boot/src/main/resources/application-dev.yml`에서 데이터베이스/Redis 연결 정보 업데이트
   - 기본값: 온라인 데모 DB 연결 (읽기 전용): `www.youlai.tech:3306`
   - 로컬 개발: MySQL 및 Redis 연결 문자열 업데이트 필요
3. **메인 클래스**: `com.youlai.boot.YouLaiBootApplication`
4. **기본 포트**: 8989
5. **API 문서**: http://localhost:8989/doc.html (Knife4j)

### 핵심 기술 스택

- **ORM**: MyBatis-Plus 3.5.5 (`platform/codegen`을 통한 코드 생성)
- **API 문서**: Knife4j 4.5.0 (OpenAPI 3.0)
- **객체 매핑**: MapStruct 1.6.3
- **분산 락**: Redisson 3.51.0
- **작업 스케줄러**: XXL-Job 3.2.0 (선택 사항, 기본값 비활성화)
- **엑셀**: FastExcel 1.3.0
- **객체 스토리지**: MinIO 8.5.10 / Aliyun OSS 3.16.3
- **캡차**: Hutool captcha (수식/랜덤 타입)
- **AI 통합**: Spring AI OpenAI 1.0.0-M6 (Qwen, DeepSeek 지원)

### 보안 설정

- **세션 타입**: `jwt` (무상태) 또는 `redis-token` (다중 기기 제어)
- **토큰 TTL**: 액세스 토큰 2시간, 리프레시 토큰 7일 (`application-dev.yml`에서 설정 가능)
- **무시 URL**: 인증 엔드포인트, 캡차, WebSocket (`security.ignore-urls` 참조)
- **비보안 URL**: Swagger/Knife4j 문서 (Spring Security 완전 우회)

### Docker 설정

`youlai-boot/docker/docker-compose.yml`을 사용하여 의존성 시작:

```bash
cd youlai-boot/docker
docker-compose up -d
```

서비스: MySQL 8.0.29, Redis 7.2.3, MinIO, XXL-Job Admin

## 프론트엔드 (vue3-element-admin)

### 아키텍처

- **라우터**: Vue Router 4 (권한 기반 동적 라우트 생성)
- **상태 관리**: Pinia 3 스토어
- **UI**: Element-Plus 2.11.8 (온디맨드 자동 임포트)
- **HTTP**: Axios (요청/응답 인터셉터 포함)
- **국제화**: Vue I18n 11 (영어/중국어)
- **차트**: ECharts 6
- **리치 에디터**: @wangeditor-next/editor 5
- **WebSocket**: @stomp/stompjs 7
- **유틸리티**: @vueuse/core 12, lodash-es

### 주요 명령어

```bash
# 의존성 설치 (vue3-element-admin 디렉토리에서)
cd vue3-element-admin
pnpm install

# 개발 서버 실행 (포트 3000)
pnpm run dev

# 프로덕션 빌드
pnpm run build

# 타입 체크
pnpm run type-check

# 린트 및 포맷
pnpm run lint                  # 모든 린터 실행
pnpm run lint:eslint           # ESLint만
pnpm run lint:prettier         # Prettier만
pnpm run lint:stylelint        # Stylelint만

# Commitizen으로 Git 커밋
pnpm run commit
```

### 프론트엔드 프로젝트 구조

```
src/
├── router/              # Vue Router 4 라우팅 설정 (RBAC 기반 동적 라우팅)
├── views/               # 페이지 컴포넌트
├── api/                 # Axios API 클라이언트 및 인터셉터
├── stores/              # Pinia 상태 관리 스토어
├── components/          # 재사용 가능한 UI 컴포넌트
├── composables/         # 재사용 가능한 Vue 컴포저블
├── lang/                # 다국어 번역 (영어/중국어/한국어)
├── styles/              # 전역 스타일 및 SCSS 변수
├── utils/               # 유틸리티 함수
├── enums/               # TypeScript 열거형
├── types/               # TypeScript 타입 정의
├── constants/           # 프로젝트 전역 상수
└── main.ts              # 애플리케이션 진입점
```

**주요 특징:**
- **자동 임포트**: `unplugin-auto-import` & `unplugin-vue-components` 사용
- **타입 안전**: 전체 TypeScript 기반 개발
- **동적 라우팅**: RBAC 권한 기반 라우트 자동 생성
- **다국어 지원**: Vue I18n으로 영어/중국어/한국어 지원

### 개발 환경 설정

1. **Node 버전**: ^20.19.0 또는 >=22.12.0 (LTS 권장)
2. **패키지 매니저**: pnpm >= 8.0.0 (preinstall 훅으로 강제)
3. **백엔드 API**:
   - 기본값: `https://api.youlai.tech` (온라인 데모)
   - 로컬: `.env.development`에서 `VITE_APP_API_URL`을 `http://localhost:8989`로 변경
4. **Mock 데이터**: `.env.development`에서 `VITE_MOCK_DEV_SERVER=true`로 설정하면 로컬 mock 사용
5. **WebSocket**: 필요시 `VITE_APP_WS_ENDPOINT` 설정

### 자동 임포트 설정

프로젝트는 `unplugin-auto-import` 및 `unplugin-vue-components` 사용:
- **기본값**: 타입 생성 비활성화 (`dts: false`)
- **타입 재생성**: `vite.config.ts`에서 `dts`를 파일 경로로 설정, dev 실행 후 다시 `false`로 변경

### 코드 품질 및 DevOps

**린팅 및 포맷팅:**
- **ESLint 9**: TypeScript, Vue, Prettier 플러그인 포함
- **Stylelint 16**: SCSS, Vue 지원
- **Prettier**: 자동 코드 포매팅
- **Husky + lint-staged**: 커밋 전 린트 훅 자동 실행
- **Commitlint**: Conventional commit 메시지 강제

**빌드 최적화:**
- **코드 분할**: 해시 기반 네이밍으로 자동 분할
- **번들 압축**: 프로덕션에서 Terser 사용
- **선택적 제거**: console.log, debugger, 주석 프로덕션 제거
- **청크 크기 제한**: 2000kb (대용량 번들 경고)
- **자산 구성**: `js/`, `img/`, `fonts/`, `media/` 디렉토리


## 프론트엔드-백엔드 통합

### 프론트엔드 → 백엔드 통신

1. 개발 환경 프록시: `/dev-api` → 백엔드 URL (`vite.config.ts`에서 설정)
2. 프로덕션: Nginx 리버스 프록시 `/prod-api/` → 백엔드 API
3. 인증: `Authorization` 헤더의 JWT 토큰 또는 Redis 기반 세션

### 공유 개념

**데이터 계층 아키텍처:**

```
API 요청 → query → form → bo → service → mapper → entity
API 응답 → dto/vo ← service ← mapper ← entity
```

- **Entity**: 데이터베이스 엔티티 모델
- **DTO**: 데이터 전송 객체 (API 응답에 사용)
- **VO**: 뷰 객체 (복잡한 쿼리 결과 조합)
- **Form**: 요청 본문 객체 (API 입력)
- **Query**: 쿼리 파라미터 (목록/검색 필터)
- **BO**: 비즈니스 객체 (내부 비즈니스 로직)

**권한 관리:**
- **RBAC**: 메뉴/버튼 권한을 가진 역할 기반 접근 제어
- **메뉴 관리**: 시스템 메뉴 및 버튼 권한 정의
- **동적 라우팅**: 프론트엔드에서 사용자 권한에 따라 메뉴/라우트 자동 생성

## 개발 워크플로우

### 새 기능 모듈 추가

**1. 백엔드 개발:**
   - 데이터베이스 테이블 생성 (`youlai-boot/sql/mysql/`)
   - `/api/v1/codegen`에서 코드 생성기 사용 (Knife4j 문서에서)
   - 생성되는 파일: Entity, Mapper, Service, Controller, XML 매퍼
   - `system/converter`에 MapStruct 컨버터 추가 (Entity ↔ DTO/VO/Form 변환)
   - 비즈니스 로직 구현 (Service 레이어)
   - 메뉴 관리 시스템에서 권한 추가 및 설정

**2. 프론트엔드 개발:**
   - `src/router/modules`에 라우트 정의 추가
   - `src/views`에 Vue 페이지 컴포넌트 생성
   - `src/api`에 API 호출 함수 추가
   - `src/lang`에서 다국어(i18n) 문자열 업데이트
   - Element-Plus 컴포넌트로 UI 구성

**3. 테스트 및 배포:**
   - Knife4j API 문서로 엔드포인트 테스트
   - 백엔드 `mvn test` 실행
   - 프론트엔드 `pnpm run lint` 및 `pnpm run type-check` 실행
   - 프로덕션 빌드 테스트 (`pnpm run build`)

### API 테스트

- Knife4j: http://localhost:8989/doc.html
- 온라인 Apifox: https://www.apifox.cn/apidoc/shared-195e783f-4d85-4235-a038-eec696de4ea5

### API 엔드포인트 설계

**명명 규칙:**
```
GET    /api/v1/{resource}              # 목록 조회 (페이지네이션)
GET    /api/v1/{resource}/{id}         # 상세 조회
GET    /api/v1/{resource}/{id}/detail  # 상세 조회 (복잡한 경우)
POST   /api/v1/{resource}              # 생성
PUT    /api/v1/{resource}/{id}         # 수정
DELETE /api/v1/{resource}/{id}         # 삭제
```

**쿼리 파라미터:**
- `pageNum`: 페이지 번호 (기본값: 1)
- `pageSize`: 페이지당 행 수 (기본값: 10)
- `keywords`: 검색 키워드
- 기타 필터: DTO의 Query 클래스에 정의

**주의사항:**
- API 경로와 프론트엔드에서 호출하는 경로가 일치해야 함
- 예: 백엔드 `/my` → 프론트엔드도 `/my` (not `/my-page`)

### 데이터베이스 변경

- SQL 마이그레이션은 수동 (Flyway/Liquibase 미사용)
- MyBatis-Plus는 기본 CRUD 처리; 복잡한 쿼리는 XML 매퍼 사용
- 전역 논리 삭제 활성화: `isDeleted` 필드 (0=활성, 1=삭제)

## 중요 참고 사항

### 보안 및 세션

- **세션 관리**: `security.session.type`을 `jwt`와 `redis-token` 간 변경 시 로그아웃/리프레시 전략이 달라짐
- **CSRF 설정**: Spring Security 6에서 비활성화됨 (JWT 기반 무상태 인증 사용)
- **토큰 TTL**: 액세스 토큰 2시간, 리프레시 토큰 7일 (`application-dev.yml`에서 설정 가능)

### 기능별 주의사항

- **다중 테넌시**: 미구현 (단일 테넌트 시스템)
- **파일 업로드**: 기본 최대 50MB (`application.yml`에서 설정 가능)
- **캡차 타입**: `circle`, `gif`, `line`, `shear` (수식 또는 랜덤, `application-dev.yml`에서 설정)
- **XXL-Job**: 기본값 비활성화 (`xxl.job.enabled=false`), 스케줄 작업 필요시 활성화
- **WebSocket**: 설정 파일에서 비활성화됨 (`VITE_APP_WS_ENDPOINT=` 빈 값)

### 로컬 개발 환경 빠른 시작

**필수 구성요소:**
```bash
# 백엔드 실행
cd youlai-boot
mvn spring-boot:run
# 또는 IDE에서 YouLaiBootApplication.java 실행

# 프론트엔드 실행 (새로운 터미널)
cd vue3-element-admin
pnpm install  # 처음 한 번만
pnpm run dev
```

**접근 주소:**
- 프론트엔드: http://localhost:3000
- 백엔드 API: http://localhost:8989
- API 문서 (Knife4j): http://localhost:8989/doc.html

**기본 인증 정보:**
- 사용자명: admin
- 비밀번호: admin123

## 총 455개 파일에 중국어가 포함
  총 454개 파일 남음:
  - Java 파일: 263개 (주석, JavaDoc, 문자열 리터럴)
  - Vue 파일: 83개 (템플릿 텍스트, 레이블, 메시지)
  - TypeScript 파일: 83개 (문자열, 주석, 설정값)
  - XML 파일: 13개 (MyBatis 매퍼 주석, 쿼리)
  - YAML 파일: 6개 (설정 파일 설명)
  - Markdown 파일: 6개 (문서)