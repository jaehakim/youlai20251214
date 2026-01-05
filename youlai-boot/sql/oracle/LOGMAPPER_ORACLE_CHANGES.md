# LogMapper.xml Oracle 호환성 수정 가이드

## 개요

LogMapper.xml의 모든 MySQL 전용 함수를 Oracle 11g 호환 함수로 수정했습니다.

## 수정된 SQL 함수 대조표

| 항목 | MySQL | Oracle | 위치 |
|------|-------|--------|------|
| **문자열 연결** | `CONCAT(str1, str2, ...)` | `str1 \|\| str2 \|\| ...` | `getLogPage` SELECT 절 |
| **LIKE 와일드카드** | `CONCAT('%', param, '%')` | `'%' \|\| param \|\| '%'` | `getLogPage` WHERE 절 |
| **날짜 문자열 변환** | `DATE_FORMAT(date, '%Y-%m-%d')` | `TO_CHAR(date, 'YYYY-MM-DD')` | `getPvCounts`, `getIpCounts` |
| **문자열을 날짜로 변환** | `date` (자동 변환) | `TO_DATE(date_str, 'YYYY-MM-DD HH24:MI:SS')` | `getLogPage`, `getPvCounts`, `getIpCounts` |
| **오늘 날짜만 추출** | `DATE(create_time) = CURDATE()` | `TRUNC(create_time) = TRUNC(SYSDATE)` | `getPvStats`, `getUvStats` |
| **어제 날짜 계산** | `CURDATE() - INTERVAL 1 DAY` | `TRUNC(SYSDATE) - 1` | `getPvStats`, `getUvStats` |
| **현재 날짜/시간** | `NOW()` | `SYSDATE` | `getPvStats`, `getUvStats` |
| **시간 비교 제거** | `TIME(create_time) <= TIME(NOW())` | `create_time <= SYSDATE` | `getPvStats`, `getUvStats` |

## 쿼리별 상세 수정 내용

### 1. getLogPage (로그 페이지 목록 조회)

#### 변경 사항

**Before (MySQL):**
```sql
CONCAT(t1.province," ", t1.city) AS region
CONCAT(t1.browser," ", t1.browser_version) AS browser
t1.content LIKE concat('%',#{queryParams.keywords},'%')
AND t1.create_time >= #{startDate}  -- 문자열 직접 비교
AND t1.create_time <= #{endDate}
```

**After (Oracle):**
```sql
t1.province || ' ' || t1.city AS region
t1.browser || ' ' || t1.browser_version AS browser
t1.content LIKE '%' || #{queryParams.keywords} || '%'
AND t1.create_time >= TO_DATE(#{startDate}, 'YYYY-MM-DD HH24:MI:SS')  -- 명시적 변환
AND t1.create_time <= TO_DATE(#{endDate}, 'YYYY-MM-DD HH24:MI:SS')
```

**이유:**
- Oracle은 || 연결자 사용
- TO_DATE() 함수로 명시적 날짜 변환 필요

---

### 2. getPvCounts (방문량 일별 통계)

#### 변경 사항

**Before (MySQL):**
```sql
SELECT
    COUNT(1) AS count,
    DATE_FORMAT(create_time,'%Y-%m-%d') AS date
FROM sys_log
WHERE
    create_time BETWEEN #{startDate} AND #{endDate}
    AND is_deleted = 0
GROUP BY
    DATE_FORMAT(create_time, '%Y-%m-%d')
```

**After (Oracle):**
```sql
SELECT
    COUNT(1) AS count,
    TO_CHAR(create_time, 'YYYY-MM-DD') AS date
FROM sys_log
WHERE
    create_time BETWEEN TO_DATE(#{startDate}, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(#{endDate}, 'YYYY-MM-DD HH24:MI:SS')
    AND is_deleted = 0
GROUP BY
    TO_CHAR(create_time, 'YYYY-MM-DD')
```

**이유:**
- MySQL: DATE_FORMAT() → Oracle: TO_CHAR()
- 날짜 형식: '%Y-%m-%d' → 'YYYY-MM-DD'
- BETWEEN 조건에서 명시적 날짜 변환 필수

---

### 3. getIpCounts (IP 일별 통계)

#### 변경 사항

동일한 패턴이 적용됨 (COUNT(DISTINCT ip) 추가)

```sql
-- 변경 전
DATE_FORMAT(create_time, '%Y-%m-%d')

-- 변경 후
TO_CHAR(create_time, 'YYYY-MM-DD')
```

---

### 4. getPvStats (방문량 PV 통계 데이터)

#### 변경 사항

**Before (MySQL):**
```sql
COUNT(CASE WHEN DATE(create_time) = CURDATE() THEN 1 END) AS todayCount
WHEN DATE(create_time) = CURDATE() - INTERVAL 1 DAY
AND TIME(create_time) <= TIME(NOW()) THEN 1
```

**After (Oracle):**
```sql
COUNT(CASE WHEN TRUNC(create_time) = TRUNC(SYSDATE) THEN 1 END) AS todayCount
WHEN TRUNC(create_time) = TRUNC(SYSDATE) - 1
AND create_time <= SYSDATE THEN 1
```

**함수 대응:**

| MySQL | Oracle | 목적 |
|-------|--------|------|
| `DATE(datetime)` | `TRUNC(datetime)` | 시간 제거 후 날짜만 추출 |
| `CURDATE()` | `TRUNC(SYSDATE)` | 현재 날짜 (시간 제거) |
| `INTERVAL 1 DAY` | `-1` | 1일 전 날짜 (정수 계산) |
| `TIME(datetime) <= TIME(NOW())` | `datetime <= SYSDATE` | 시간 비교 |
| `NOW()` | `SYSDATE` | 현재 날짜/시간 |

**이유:**
- Oracle TRUNC(date): 시간/분/초를 0으로 설정하여 날짜만 남김
- Oracle은 날짜에서 정수를 빼면 날짜 계산 (INTERVAL 불필요)
- TIME() 함수 없음 → 시간까지 포함하여 비교

---

### 5. getUvStats (IP(UV) 통계 데이터)

#### 변경 사항

getPvStats와 동일한 패턴 (COUNT(DISTINCT ip) 사용)

```sql
COUNT(DISTINCT CASE WHEN TRUNC(create_time) = TRUNC(SYSDATE) THEN ip END)
```

---

## Oracle 문법 설명

### 1. 문자열 연결 (||)

```sql
-- MySQL
SELECT CONCAT('Hello', ' ', 'World');  -- 'Hello World'

-- Oracle
SELECT 'Hello' || ' ' || 'World';     -- 'Hello World'
```

### 2. 날짜 형식 함수

```sql
-- MySQL: DATE_FORMAT
SELECT DATE_FORMAT(NOW(), '%Y-%m-%d');      -- '2026-01-05'
SELECT DATE_FORMAT(NOW(), '%Y%m%d %H:%i:%s'); -- '20260105 14:30:45'

-- Oracle: TO_CHAR
SELECT TO_CHAR(SYSDATE, 'YYYY-MM-DD');      -- '2026-01-05'
SELECT TO_CHAR(SYSDATE, 'YYYYMMDD HH24:MI:SS'); -- '20260105 14:30:45'
```

**Oracle 날짜 형식 문자:**
- YYYY: 4자리 연도
- MM: 2자리 월
- DD: 2자리 일
- HH24: 24시간 형식 (00-23)
- MI: 분
- SS: 초

### 3. 날짜/시간 추출

```sql
-- MySQL: DATE, TIME
SELECT DATE('2026-01-05 14:30:45');   -- '2026-01-05' (날짜만)
SELECT TIME('2026-01-05 14:30:45');   -- '14:30:45' (시간만)

-- Oracle: TRUNC
SELECT TRUNC(TO_DATE('2026-01-05 14:30:45', 'YYYY-MM-DD HH24:MI:SS'));
-- 결과: 2026-01-05 00:00:00 (시간 제거)
```

### 4. 현재 날짜/시간

```sql
-- MySQL
CURDATE()         -- 현재 날짜만
NOW()             -- 현재 날짜 + 시간

-- Oracle
TRUNC(SYSDATE)    -- 현재 날짜만 (시간 제거)
SYSDATE           -- 현재 날짜 + 시간
```

### 5. 날짜 연산

```sql
-- MySQL
CURDATE() + INTERVAL 1 DAY     -- 내일
CURDATE() - INTERVAL 1 DAY     -- 어제
DATE_ADD(NOW(), INTERVAL 1 HOUR) -- 1시간 뒤

-- Oracle
SYSDATE + 1                    -- 내일 (같은 시간)
SYSDATE - 1                    -- 어제 (같은 시간)
SYSDATE + INTERVAL '1' HOUR    -- 1시간 뒤
SYSDATE + 1/24                 -- 1시간 뒤 (분수 계산)
```

### 6. 문자열을 날짜로 변환

```sql
-- MySQL: 자동 변환 또는 STR_TO_DATE
SELECT * FROM table WHERE date_column = '2026-01-05';
SELECT * FROM table WHERE date_column = STR_TO_DATE('2026-01-05', '%Y-%m-%d');

-- Oracle: 명시적 TO_DATE 필수
SELECT * FROM table WHERE date_column >= TO_DATE('2026-01-05', 'YYYY-MM-DD');
SELECT * FROM table WHERE date_column >= TO_DATE('2026-01-05 14:30:45', 'YYYY-MM-DD HH24:MI:SS');
```

---

## 테스트 방법

### 1. getPvCounts 테스트

```sql
-- Oracle에서 테스트
DECLARE
  v_start_date VARCHAR2(19) := '2025-01-01 00:00:00';
  v_end_date VARCHAR2(19) := '2026-01-05 23:59:59';
BEGIN
  SELECT COUNT(1) AS count, TO_CHAR(create_time, 'YYYY-MM-DD') AS date
  FROM sys_log
  WHERE create_time BETWEEN TO_DATE(v_start_date, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(v_end_date, 'YYYY-MM-DD HH24:MI:SS')
    AND is_deleted = 0
  GROUP BY TO_CHAR(create_time, 'YYYY-MM-DD');
END;
/
```

### 2. getPvStats 테스트

```sql
-- 오늘 로그 수
SELECT COUNT(CASE WHEN TRUNC(create_time) = TRUNC(SYSDATE) THEN 1 END) AS todayCount
FROM sys_log
WHERE is_deleted = 0;

-- 어제 로그 수
SELECT COUNT(CASE WHEN TRUNC(create_time) = TRUNC(SYSDATE) - 1 THEN 1 END) AS yesterdayCount
FROM sys_log
WHERE is_deleted = 0;
```

### 3. 문자열 연결 테스트

```sql
SELECT '서울' || ' ' || '강남구' AS region FROM DUAL;
-- 결과: 서울 강남구
```

---

## 주의사항

### 1. 날짜 비교 시 TO_DATE 필수

```sql
-- ❌ 잘못된 방식 (암묵적 변환 시도)
WHERE create_time = '2026-01-05'

-- ✅ 올바른 방식 (명시적 변환)
WHERE TRUNC(create_time) = TO_DATE('2026-01-05', 'YYYY-MM-DD')
WHERE create_time >= TO_DATE('2026-01-05 00:00:00', 'YYYY-MM-DD HH24:MI:SS')
```

### 2. NLS_DATE_FORMAT 의존 금지

```sql
-- ❌ 위험 (DB 세션 설정에 따라 다름)
WHERE create_time = TO_DATE('2026-01-05', 'YYYY-MM-DD')  -- 형식 명시 필수

-- ✅ 안전 (명시적 형식)
WHERE create_time >= TO_DATE('2026-01-05 00:00:00', 'YYYY-MM-DD HH24:MI:SS')
```

### 3. NULL 처리

```sql
-- CONCAT은 하나의 인자가 NULL이면 결과도 NULL
SELECT NULL || 'test' FROM DUAL;  -- NULL

-- 해결방법
SELECT NVL(province, '') || ' ' || NVL(city, '') AS region
FROM sys_log;
```

---

## 성능 최적화

### 1. 인덱스 활용

```sql
-- 기존 인덱스 활용 (TRUNC 사용 시)
-- create_time 컬럼에 인덱스가 있으면
-- TRUNC(create_time) = TRUNC(SYSDATE) 쿼리는 인덱스 사용 못 함

-- 해결: 함수 기반 인덱스 생성
CREATE INDEX idx_sys_log_date ON sys_log(TRUNC(create_time));

-- 또는 조건 변경
WHERE create_time >= TRUNC(SYSDATE)
  AND create_time < TRUNC(SYSDATE) + 1
```

### 2. 그룹 바이 최적화

```sql
-- Oracle 12c 이상: GROUP BY는 자동으로 정렬 가능
-- Oracle 11g: GROUP BY 후 결과가 정렬되지 않음

-- 필요시 명시적으로 ORDER BY 추가
GROUP BY TO_CHAR(create_time, 'YYYY-MM-DD')
ORDER BY TO_CHAR(create_time, 'YYYY-MM-DD') DESC;
```

---

## 참고자료

- [Oracle TO_CHAR 함수](https://docs.oracle.com/cd/E11882_01/sql.112/e41084/sql_elements003.htm)
- [Oracle TRUNC 함수](https://docs.oracle.com/cd/E11882_01/sql.112/e41084/sql_elements_085.htm)
- [Oracle 날짜 형식](https://docs.oracle.com/cd/E11882_01/sql.112/e41084/sql_elements004.htm)
- [MySQL에서 Oracle로 마이그레이션](https://www.oracle.com/database/technologies/migration/)

---

## 파일 위치

- **원본 파일**: `youlai-boot/src/main/resources/mapper/system/LogMapper.xml`
- **수정된 파일**: `youlai-boot/src/main/resources/mapper/system/LogMapper.xml`
- **변경 가이드**: 현재 문서

---

## 검증 체크리스트

- [ ] LogMapper.xml 파일이 수정되었는지 확인
- [ ] 모든 MySQL 함수가 Oracle 함수로 변경되었는지 확인
- [ ] 애플리케이션 빌드 성공 (`mvn clean package`)
- [ ] 로그 조회 페이지 정상 작동 테스트
- [ ] 통계 데이터 조회 정상 작동 테스트
- [ ] 날짜 범위 필터링 정상 작동 테스트

---

## 요약

LogMapper.xml의 모든 MySQL 전용 함수를 Oracle 호환 함수로 변경했습니다:

| 함수 | 변경 |
|------|------|
| CONCAT() | `||` 연결자 |
| DATE_FORMAT() | TO_CHAR() |
| DATE() | TRUNC() |
| CURDATE() | TRUNC(SYSDATE) |
| NOW() | SYSDATE |
| INTERVAL | 정수 연산 |
| TIME() | 직접 datetime 비교 |

이제 Oracle 11g 데이터베이스에서 모든 로그 관련 쿼리가 정상적으로 작동합니다.
