# ⚽ ScoutVelocity - FIFA 선수 데이터 조회 성능 최적화 프로젝트

> 100,000건의 선수 데이터와 3,000,000건의 경기 기록을 효율적으로 조회하기 위한 단계별 최적화 전략

[![Java](https://img.shields.io/badge/Java-17-007396?style=flat&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-6DB33F?style=flat&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-9.4.0-4479A1?style=flat&logo=mysql)](https://www.mysql.com/)
[![QueryDSL](https://img.shields.io/badge/QueryDSL-5.0.0-0088CC?style=flat)](http://querydsl.com/)

---

## 📌 프로젝트 개요

실무에서 흔히 겪는 대용량 데이터 조회 성능 문제를 **의도적으로 재현**하고, 단계별 최적화를 통해 **어떻게 개선되는지 정량적으로 측정**하는 프로젝트입니다.

### 핵심 목표
-  비효율적인 코드의 실제 성능 영향 측정
-  인덱스만으로는 해결되지 않는 문제 증명
-  QueryDSL 도입의 극적인 개선 효과 증명
-  단계별 성능 개선 과정 문서화

---

## 🚀 핵심 성과

### 전체 성능 개선

| Step | 평균 응답 시간 | 개선율 | 주요 기술 |
|------|--------------|--------|----------|
| **Step 1: Plain** | **25.7초** | - (기준) | `findAll()` + Stream 필터링 |
| **Step 2: Index** | **21.2초** | 1.2배 | 쿼리 메서드 + 인덱스 |
| **Step 3: QueryDSL** | **1.5초** | **16.9배** ⚡ | 동적 쿼리 + DB 집계 |

### 극적인 개선 사례

| 시나리오 | Step 1 | Step 3 | 개선율 |
|---------|--------|--------|--------|
| 경기 기록 조회 | 15.3초 | 24ms | **765배** 🔥 |
| 득점왕 집계 | 79.6초 | 789ms | **98배** 🔥 |
| 최근 폼 조회 | 76.7초 | 9.4초 | **8배** |

**Step 2의 실패:**
- 시나리오 6 (득점왕 집계): 오히려 **악화** (77초 → 79초)
- 집계 쿼리는 쿼리 메서드로 표현 불가능 증명

---

## 🏗️ 기술 스택

### Backend
- **Java**
- **Spring Boot**
- **Spring Data JPA**
- **QueryDSL**
- **Hibernate**

### Database
- **MySQL**
- **인덱스 최적화**
- 데이터 규모: 선수 100,000건, 경기 기록 3,000,000건

### Tools
- **Gradle**
- **Lombok**
- **Spring AOP** (성능 측정)

---

## 📂 프로젝트 구조
```
ScoutVelocity/
├── src/main/java/
│   └── com/scoutvelocity/scoutvelocity/
│       ├── domain/
│       │   ├── player/              # 선수 도메인
│       │   │   ├── controller/      # REST API
│       │   │   ├── service/         # 비즈니스 로직
│       │   │   ├── repository/      # 데이터 접근 (QueryDSL 포함)
│       │   │   └── dto/             # 응답 DTO
│       │   ├── matchrecord/         # 경기 기록 도메인
│       │   └── ...
│       └── global/
│           ├── config/              # QueryDSL 설정
│           ├── performance/         # 성능 측정 AOP
│           └── response/            # 공통 응답
├── build.gradle
└── README.md
```

---

## 🌿 브랜치 전략
```
main
└── dev
    ├── feature/step1-plain       # 비효율 Baseline
    ├── feature/step2-index       # 인덱스 + 쿼리 메서드
    └── feature/step3-querydsl    # QueryDSL 최적화
```

각 브랜치는 **독립적인 구현**으로, 단계별 성능 비교를 위해 분리되어 있습니다.

---

## 🧪 테스트 시나리오

### 시나리오 1: 클럽별 선수 조회
```http
GET /api/v1/players/club/10
```
- **Step 1**: 2,605ms (100,000건 전체 로드)
- **Step 2**: 93ms (DB 필터링)
- **Step 3**: 112ms (fetchJoin으로 N+1 해결)

### 시나리오 2: 복합 능력치 필터링
```http
GET /api/v1/players/search?position=ST&minPace=85&minShooting=80&minPhysical=75
```
- **Step 1**: 2,526ms (메모리 필터링)
- **Step 2**: 508ms (부분 DB 필터링)
- **Step 3**: 176ms (BooleanBuilder 동적 조건)

### 시나리오 3: 유망주 발굴
```http
GET /api/v1/players/talents?minAge=18&maxAge=23&minPotential=85&sortBy=potential&order=desc
```
- **Step 1**: 2,574ms (메모리 정렬)
- **Step 2**: 104ms (DB 필터링, 메모리 정렬)
- **Step 3**: 110ms (OrderSpecifier 동적 정렬)

### 시나리오 4: 리그 + 국적 + 페이징
```http
GET /api/v1/players?leagueId=13&nationalityId=45&page=0&size=20
```
- **Step 1**: 2,699ms (메모리 페이징)
- **Step 2**: 56ms (Pageable)
- **Step 3**: 56ms (동적 조건 + fetchJoin)

### 시나리오 5: 경기 기록 조회 ⚡
```http
GET /api/v1/players/231747/match-records
```
- **Step 1**: 15,301ms (3,000,000건 전체 로드)
- **Step 2**: 20ms (**765배 개선**)
- **Step 3**: 24ms (fetchJoin 안정성)

### 시나리오 6: 득점왕 집계 🔥
```http
GET /api/v1/players/top-scorers?position=ST&minGoals=10&minAssists=5&limit=20
```
- **Step 1**: 77,577ms (메모리 GROUP BY)
- **Step 2**: 79,642ms (**악화!**)
- **Step 3**: 789ms (**98배 개선**, DB GROUP BY)

### 시나리오 7: 최근 폼 조회
```http
GET /api/v1/players/recent-form?startDate=2024-01-01&endDate=2024-03-31&minMatches=5
```
- **Step 1**: 76,665ms (메모리 집계)
- **Step 2**: 67,879ms (부분 개선)
- **Step 3**: 9,401ms (DB 집계)

---

## 📊 단계별 상세 분석

### Step 1: Plain (의도적 비효율)

**전략**
```java
// 😱 극악의 비효율
List<Player> allPlayers = playerRepository.findAll(); // 100,000건
return allPlayers.stream()
    .filter(p -> p.getClub().getId().equals(clubId))
    .collect(Collectors.toList());
```

**문제점**
- ❌ 모든 데이터를 메모리에 로드
- ❌ 애플리케이션 레벨 필터링
- ❌ DB 인덱스 미사용
- ❌ N+1 문제 발생 가능

**결과:** 평균 25.7초

---

### Step 2: Index + 쿼리 메서드 (부분 개선, 명확한 한계)

**전략**
```java
// ✅ DB 필터링
List<Player> players = playerRepository.findByClub_Id(clubId);

// 인덱스 추가
@Index(name = "idx_club_id", columnList = "club_id")
```

**개선점**
- ✅ DB 레벨 필터링
- ✅ 인덱스 활용
- ✅ 단순 조회에서 극적 개선

**치명적 한계**
- ❌ **메서드 폭발**: 조건 조합마다 메서드 필요
```java
  findByClub_Id()
  findByClub_IdAndPosition()
  findByClub_IdAndPositionAndStats_PaceGreaterThan()
  // 끝도 없음...
```
- ❌ **동적 쿼리 불가**: 선택적 파라미터 처리 어려움
- ❌ **집계 쿼리 불가**: GROUP BY, SUM을 표현 불가능
    - 시나리오 6에서 **오히려 악화** (77초 → 79초)
- ❌ N+1 위험 존재

**결과:** 평균 21.2초 (1.2배 개선)

---

### Step 3: QueryDSL (드라마틱한 전환점 🚀)

**전략**
```java
// 🎯 동적 조건 조합
BooleanBuilder builder = new BooleanBuilder();
if (clubId != null) builder.and(player.club.id.eq(clubId));
if (minPace != null) builder.and(player.stats.pace.goe(minPace));

// 🎯 DB 집계 쿼리
queryFactory
    .select(
        matchRecord.player,
        matchRecord.goals.sum(),
        matchRecord.assists.sum()
    )
    .groupBy(matchRecord.player)
    .having(matchRecord.goals.sum().goe(minGoals))
    .fetch();

// 🎯 fetchJoin으로 N+1 해결
queryFactory
    .selectFrom(player)
    .leftJoin(player.club, club).fetchJoin()
    .leftJoin(club.league, league).fetchJoin()
    .where(builder)
    .fetch();
```

**핵심 기능**
-  **BooleanBuilder**: 동적 조건 조합 (메서드 폭발 해결)
-  **OrderSpecifier**: 동적 정렬
-  **fetchJoin**: N+1 완전 해결
-  **집계 쿼리**: GROUP BY, SUM, AVG, HAVING 완벽 지원
-  **타입 안전**: 컴파일 타임 오류 검증

**결과:** 평균 1.5초 (16.9배 개선)

---

## 💡 핵심 교훈

### 1. 인덱스만으로는 부족하다
Step 2에서 인덱스를 추가했지만:
- ✅ 단순 조회는 개선됨
- ❌ 집계 쿼리는 **오히려 악화**
- ❌ 복잡한 조건은 여전히 비효율

**결론:** 쿼리 구조 자체를 개선해야 한다

### 2. 쿼리 메서드의 한계
- 간단한 조회: ✅ 적합
- 동적 조건: ❌ 메서드 폭발
- 집계 쿼리: ❌ 표현 불가능

**결론:** 복잡한 조회는 QueryDSL 필수

### 3. DB에서 처리할 수 있는 건 DB에서
- 필터링: DB WHERE 절
- 정렬: DB ORDER BY
- 집계: DB GROUP BY, SUM
- 페이징: DB LIMIT, OFFSET

**결론:** 애플리케이션 메모리는 최후의 수단

---

## 🎯 기술 선택 가이드

### 쿼리 메서드를 사용해야 할 때
-  단순 CRUD
-  정적 조회 조건
-  빠른 프로토타이핑
-  소규모 데이터 (< 10,000건)

### QueryDSL을 사용해야 할 때
-  복잡한 조건 조합
-  동적 쿼리 필요
-  집계 쿼리 (GROUP BY, SUM, AVG)
-  N+1 문제 해결 필요
-  대용량 데이터 (> 10,000건)
-  높은 성능 요구사항

### 데이터 규모별 권장 기술

| 데이터 규모 | 쿼리 복잡도 | 권장 기술 |
|------------|-----------|----------|
| < 1,000건 | 단순 | JPA 쿼리 메서드 |
| < 10,000건 | 중간 | JPA + 부분 QueryDSL |
| > 10,000건 | 복잡 | QueryDSL 필수 |
| > 100,000건 | 집계 포함 | QueryDSL + 인덱스 최적화 |

---

## 🚀 실행 방법

### 1. 환경 설정
```bash
# Java 17 설치 확인
java -version

# MySQL 실행
mysql -u root -p
```

### 2. 데이터베이스 생성
```sql
CREATE DATABASE scout_velocity;
```

### 3. 데이터 임포트
```bash
# CSV 데이터를 MySQL에 임포트 (Spring Batch)
./gradlew bootRun --args='--spring.profiles.active=local,import'
```

### 4. 브랜치별 실행
```bash
# Step 1: Plain
git checkout feature/step1-plain
./gradlew bootRun

# Step 2: Index
git checkout feature/step2-index
./gradlew bootRun

# Step 3: QueryDSL
git checkout feature/step3-querydsl
./gradlew bootRun
```

### 5. API 테스트
```bash
# 클럽별 조회
curl http://localhost:8080/api/v1/players/club/10

# 복합 필터링
curl "http://localhost:8080/api/v1/players/search?position=ST&minPace=85"

# 득점왕 집계
curl "http://localhost:8080/api/v1/players/top-scorers?position=ST&minGoals=10&limit=20"
```

---

## 📈 성능 측정 방법

프로젝트에는 **Spring AOP 기반 성능 측정 시스템**이 내장되어 있습니다.

### 성능 로그 테이블
```sql
SELECT 
    step,
    method_name,
    AVG(duration_ms) as avg_ms,
    AVG(query_count) as avg_queries
FROM performance_logs
WHERE layer = 'CONTROLLER'
GROUP BY step, method_name;
```

### 측정 결과 확인
```sql
-- 단계별 평균 응답 시간
SELECT step, AVG(duration_ms) 
FROM performance_logs 
GROUP BY step;
```
