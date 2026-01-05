# MyBatis Mapper Oracle 호환성 변경 가이드

## 개요

모든 MyBatis Mapper XML 파일의 MySQL 전용 함수를 Oracle 11g 호환 함수로 변경했습니다.

**변경된 파일 목록 (8개):**
1. ✅ `system/LogMapper.xml` - 로그 조회/통계
2. ✅ `system/UserNoticeMapper.xml` - 사용자 알림
3. ✅ `system/UserMapper.xml` - 사용자 조회/검색
4. ✅ `system/NoticeMapper.xml` - 공지사항 조회
5. ✅ `system/DictMapper.xml` - 사전 조회
6. ✅ `system/DictItemMapper.xml` - 사전 항목 조회
7. ✅ `ai/AiCommandRecordMapper.xml` - AI 명령 기록 조회
8. ✅ `codegen/DatabaseMapper.xml` - 데이터베이스 메타데이터 조회

---

## 변경된 함수 요약표

| MySQL 함수 | Oracle 함수 | 파일 수 | 설명 |
|-----------|-----------|--------|------|
| `CONCAT()` | `\|\|` | 8개 파일 | 문자열 연결 |
| `GROUP_CONCAT()` | `LISTAGG()` | 1개 파일 | 그룹 문자열 집계 |
| `DATE_FORMAT()` | `TO_CHAR()` | 1개 파일 | 날짜 형식 변환 |
| (날짜 문자열) | `TO_DATE()` | 3개 파일 | 문자열을 날짜로 변환 |

---

## 파일별 상세 변경 내용

### 1. LogMapper.xml

**변경 함수:**
- `CONCAT()` → `||` (3곳)
- `DATE_FORMAT()` → `TO_CHAR()` (4곳)
- 날짜 문자열 → `TO_DATE()` (2곳)
- `DATE()` → `TRUNC()` (4곳)
- `CURDATE()` → `TRUNC(SYSDATE)` (4곳)
- `INTERVAL` → 정수 연산 (4곳)
- `TIME()` 제거 → 직접 datetime 비교

**주요 변경 쿼리:**

```sql
-- Before (MySQL)
SELECT CONCAT(t1.province," ", t1.city) AS region
SELECT COUNT(CASE WHEN DATE(create_time) = CURDATE() THEN 1 END) AS todayCount
SELECT DATE_FORMAT(create_time,'%Y-%m-%d') AS date

-- After (Oracle)
SELECT t1.province || ' ' || t1.city AS region
SELECT COUNT(CASE WHEN TRUNC(create_time) = TRUNC(SYSDATE) THEN 1 END) AS todayCount
SELECT TO_CHAR(create_time, 'YYYY-MM-DD') AS date
```

---

### 2. UserNoticeMapper.xml

**변경 함수:**
- `CONCAT()` → `||` (1곳)

```sql
-- Before
AND t2.title LIKE CONCAT('%',#{queryParams.title},'%')

-- After
AND t2.title LIKE '%' || #{queryParams.title} || '%'
```

---

### 3. UserMapper.xml ⭐ (가장 많은 변경)

**변경 함수:**
- `CONCAT()` → `||` (5곳)
- `GROUP_CONCAT()` → `LISTAGG()` (2곳)
- `concat()` (tree_path) → `||` (2곳)
- 날짜 문자열 → `TO_DATE()` (2곳)

**주요 변경:**

```sql
-- 1. 문자열 연결
-- Before
GROUP_CONCAT( r.NAME ) AS roleNames
-- After
LISTAGG(r.NAME, ',') WITHIN GROUP (ORDER BY r.NAME) AS roleNames

-- 2. LIKE 검색
-- Before
u.username LIKE CONCAT('%',#{queryParams.keywords},'%')
-- After
u.username LIKE '%' || #{queryParams.keywords} || '%'

-- 3. 트리 경로 검색
-- Before
concat(',',concat(d.tree_path,',',d.id),',') like concat('%,',#{queryParams.deptId},',%')
-- After
',' || d.tree_path || ',' || d.id || ',' LIKE '%,' || #{queryParams.deptId} || ',%'

-- 4. 날짜 비교
-- Before
AND u.create_time >= #{startDate}
-- After
AND u.create_time >= TO_DATE(#{startDate}, 'YYYY-MM-DD HH24:MI:SS')
```

**쿼리별 변경:**
- `getUserPage`: GROUP_CONCAT → LISTAGG, CONCAT → ||, 날짜 변환
- `listExportUsers`: CONCAT → ||, tree_path 연결
- `getUserProfile`: GROUP_CONCAT → LISTAGG

---

### 4. NoticeMapper.xml

**변경 함수:**
- `CONCAT()` → `||` (1곳)
- 날짜 문자열 → `TO_DATE()` (2곳)

```sql
-- Before
AND t1.title LIKE CONCAT('%',#{queryParams.title},'%')
AND t1.publish_time >= #{startDate}

-- After
AND t1.title LIKE '%' || #{queryParams.title} || '%'
AND t1.publish_time >= TO_DATE(#{startDate}, 'YYYY-MM-DD HH24:MI:SS')
```

---

### 5. DictMapper.xml

**변경 함수:**
- `CONCAT()` → `||` (2곳)

```sql
-- Before
t1.name LIKE CONCAT('%',#{queryParams.keywords},'%')
OR t1.dict_code LIKE CONCAT('%',#{queryParams.keywords},'%')

-- After
t1.name LIKE '%' || #{queryParams.keywords} || '%'
OR t1.dict_code LIKE '%' || #{queryParams.keywords} || '%'
```

---

### 6. DictItemMapper.xml

**변경 함수:**
- `CONCAT()` → `||` (2곳)

```sql
-- Before
value LIKE CONCAT('%', #{queryParams.keywords} ,'%')
label LIKE CONCAT('%', #{queryParams.keywords} ,'%')

-- After
value LIKE '%' || #{queryParams.keywords} || '%'
label LIKE '%' || #{queryParams.keywords} || '%'
```

---

### 7. AiCommandRecordMapper.xml

**변경 함수:**
- `CONCAT()` → `||` (3곳)

```sql
-- Before
acr.original_command LIKE CONCAT('%', #{queryParams.keywords}, '%')
acr.function_name LIKE CONCAT('%', #{queryParams.keywords}, '%')
acr.username LIKE CONCAT('%', #{queryParams.keywords}, '%')

-- After
acr.original_command LIKE '%' || #{queryParams.keywords} || '%'
acr.function_name LIKE '%' || #{queryParams.keywords} || '%'
acr.username LIKE '%' || #{queryParams.keywords} || '%'
```

---

### 8. DatabaseMapper.xml ⚠️ (부분 호환)

**현황:**
- ❌ `information_schema` 사용 (MySQL 전용)
- ❌ `TABLE_SCHEMA = DATABASE()` (Oracle 미지원)
- ❌ `TABLE_COLLATION`, `ENGINE` (Oracle 미지원)
- ✅ `CONCAT()` → `||` 변경 완료

**주의사항:**
```
이 파일은 MySQL 전용입니다.
Oracle에서 코드 생성 기능을 사용하려면
별도의 DatabaseMapper_Oracle.xml을 작성하여야 합니다.

현재 변경사항:
- CONCAT() → || 변경
- 주석 추가: "MySQL 전용", "Oracle 버전 필요"
```

**Oracle 버전이 필요할 경우:**

```sql
-- Oracle 메타데이터 조회 (예시)
SELECT
    t1.TABLE_NAME,
    t1.COMMENTS AS TABLE_COMMENT,
    NULL AS TABLE_COLLATION,
    NULL AS ENGINE,
    t1.CREATED AS CREATE_TIME
FROM user_tables t1
WHERE TABLE_NAME LIKE UPPER('%' || #{queryParams.keywords} || '%')
ORDER BY CREATED DESC
```

---

## 적용 방법

### 1. 변경 사항 확인

프로젝트 빌드 전에 모든 XML 파일이 수정되었는지 확인:

```bash
cd youlai-boot
grep -r "CONCAT(" src/main/resources/mapper/
# 결과: DatabaseMapper.xml만 information_schema.CONCAT (MySQL 전용)
```

### 2. 백엔드 빌드 및 테스트

```bash
mvn clean package

# 빌드 성공 확인
# target/classes/mapper/ 에서 변경된 XML 확인
```

### 3. 애플리케이션 시작

```bash
mvn spring-boot:run
```

### 4. 각 화면별 테스트

| 화면 | 기능 | Mapper |
|------|------|--------|
| 사용자 관리 | 조회/검색 | UserMapper (LISTAGG 테스트) |
| 사용자 관리 | 부서 트리 검색 | UserMapper (tree_path 검색) |
| 로그 조회 | 목록/통계 | LogMapper (DATE 함수 테스트) |
| 공지사항 | 조회/검색 | NoticeMapper |
| 사전 관리 | 조회/검색 | DictMapper, DictItemMapper |
| AI 명령 기록 | 조회/검색 | AiCommandRecordMapper |

---

## 특별 주의사항

### 1. LISTAGG() 사용 시 NULL 처리

```sql
-- LISTAGG는 하나의 행도 NULL이면 결과가 NULL이 될 수 있음
-- NVL() 사용 권장

-- Before (MySQL)
GROUP_CONCAT(r.NAME)

-- After (Oracle) - 개선된 버전
LISTAGG(NVL(r.NAME, ''), ',') WITHIN GROUP (ORDER BY r.NAME)
```

현재 UserMapper는 개선 전 버전입니다.
필요시 NVL() 추가 권장.

### 2. 날짜 비교 시 고려사항

Oracle에서는 문자열이 자동으로 DATE로 변환되지 않습니다.
반드시 TO_DATE()를 사용해야 합니다.

```sql
-- ❌ 잘못된 방식
WHERE create_time >= '2026-01-05'  -- 에러 또는 비교 불가

-- ✅ 올바른 방식
WHERE create_time >= TO_DATE('2026-01-05', 'YYYY-MM-DD')
WHERE create_time >= TO_DATE('2026-01-05 00:00:00', 'YYYY-MM-DD HH24:MI:SS')
```

### 3. LIKE 연산자 주의

```sql
-- Oracle에서 || 연결자로 LIKE 패턴 만들 때
-- 공백이 포함되면 주의

-- Before (MySQL): CONCAT('%', key, '%')
-- After (Oracle):  '%' || key || '%'

-- 공백이 포함된 경우
'%' || #{keyword} || '%'  -- OK
```

### 4. GROUP_CONCAT vs LISTAGG 차이

| 기능 | GROUP_CONCAT | LISTAGG |
|------|-------------|---------|
| 구분자 | 2번째 인자 | WITHIN GROUP 절 |
| 정렬 | 불가능 | WITHIN GROUP으로 정렬 가능 |
| 길이 제한 | `ORDER BY ... DESC LIMIT` | `ON OVERFLOW TRUNCATE` |
| NULL 처리 | NULL 무시 | NULL 포함 가능 |

현재 UserMapper의 LISTAGG:
```sql
LISTAGG(r.NAME, ',') WITHIN GROUP (ORDER BY r.NAME)
```

---

## 테스트 체크리스트

- [ ] 애플리케이션 빌드 성공
- [ ] 사용자 목록 조회 (GROUP_CONCAT 변경 테스트)
- [ ] 사용자 검색 (CONCAT 변경 테스트)
- [ ] 사용자 부서 트리 필터 (tree_path 연결 테스트)
- [ ] 로그 목록 조회 (DATE_FORMAT 변경 테스트)
- [ ] 로그 통계 (DATE/CURDATE/INTERVAL 변경 테스트)
- [ ] 로그 날짜 범위 검색 (TO_DATE 변경 테스트)
- [ ] 공지사항 조회 (CONCAT + TO_DATE 변경 테스트)
- [ ] 공지사항 날짜 범위 검색
- [ ] 사전 조회 및 검색
- [ ] 사전 항목 조회 및 검색
- [ ] AI 명령 기록 조회 및 검색

---

## 롤백 방법

만약 문제가 발생할 경우:

```bash
# Git에서 원본 파일로 복구
git checkout src/main/resources/mapper/

# 또는 MySQL 버전의 Mapper 파일로 교체
```

---

## 추가 리소스

### 변경된 파일 위치

```
youlai-boot/src/main/resources/mapper/
├── ai/AiCommandRecordMapper.xml          ✅ 변경완료
├── codegen/DatabaseMapper.xml             ✅ 부분 변경
├── system/
│   ├── DictItemMapper.xml                ✅ 변경완료
│   ├── DictMapper.xml                    ✅ 변경완료
│   ├── LogMapper.xml                     ✅ 변경완료
│   ├── NoticeMapper.xml                  ✅ 변경완료
│   ├── UserMapper.xml                    ✅ 변경완료
│   ├── UserNoticeMapper.xml              ✅ 변경완료
│   └── [기타 파일]                        ❌ 변경 불필요
```

### 관련 문서

- `LOGMAPPER_ORACLE_CHANGES.md` - LogMapper 상세 변경 가이드
- `ORACLE_TRIGGER_FIX_GUIDE.md` - Trigger ORA-04098 해결 방법
- `youlai_boot_oracle11g_under.sql` - Oracle 11g DDL 스크립트

---

## 요약

✅ **완료된 작업:**
- 8개 MyBatis Mapper XML 파일 수정
- MySQL → Oracle 함수 변환
  - CONCAT() → ||
  - GROUP_CONCAT() → LISTAGG()
  - DATE_FORMAT() → TO_CHAR()
  - DATE/CURDATE() → TRUNC(SYSDATE)
  - INTERVAL → 정수 연산
  - TIME() → 직접 datetime 비교
- DatabaseMapper 주석 추가

⚠️ **추가 작업 (필요시):**
- DatabaseMapper Oracle 전용 버전 작성
- LISTAGG NULL 처리 개선 (NVL 추가)
- 코드 생성기(codegen) Oracle 지원

---

## 문의 사항

Mapper 파일 관련 추가 문제가 발생하면:

1. 해당 쿼리의 Oracle 문법 확인
2. 오류 메시지와 함께 보고
3. 필요시 Oracle 전용 쿼리 작성

**예상되는 추가 변경 필요 사항:**
- DatabaseMapper: Oracle 버전 필요
- Custom SQL 쿼리가 있는 다른 Mapper들
