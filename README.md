# JobRadar

데이터 기반 산업별 취업시장 분석 및 진로 탐색 지원 시스템입니다.

구직자가 산업별 성장성과 구인 활력을 객관적인 지표로 비교하고, 성장 산업과 연계된 직업 정보를 탐색할 수 있도록 공공데이터 수집부터 분산처리, 데이터베이스 적재, 웹 시각화까지 하나의 파이프라인으로 구성했습니다.

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| 언어 | Java |
| 분산 처리 | Hadoop 2.5.1, MapReduce, Linux, VMware |
| 데이터베이스 | Oracle 19c, JDBC |
| 백엔드 | Spring Boot, Spring Data JPA, Hibernate |
| 프론트엔드 | JSP, JSTL, Chart.js |
| 빌드/개발환경 | Maven, Eclipse IDE |

> 프로젝트 문서에는 전처리/Hadoop 영역은 JDK 11, Spring Boot 영역은 JDK 17로 기록되어 있습니다.

## 시스템 구조

```text
공공데이터 수집
      ↓
Java 전처리
      ↓
Hadoop MapReduce
      ↓
Oracle 적재
      ↓
Spring Boot / JPA
      ↓
JSP / Chart.js
```

오프라인 배치 영역과 온라인 서비스 영역을 분리했습니다. Hadoop 처리 결과를 Oracle에 미리 적재하고, 웹 요청 시에는 DB 조회를 통해 결과를 제공하는 구조입니다.

## 데이터 출처

- KOSIS 경제활동인구조사: 성/산업별 취업자
- 공공데이터포털 사업체노동력조사: 산업별 빈일자리율, 입직률, 이직률
- 커리어넷 직업정보 Open API: 직업, 자격증, 훈련, 교육과정

## 데이터 처리

### 전처리

- CP949 / UTF-8 인코딩 정규화
- 가로형 데이터를 세로형 데이터로 변환
- CSV 필드 내부 콤마 처리를 위해 따옴표 문맥을 고려한 파싱

### Hadoop MapReduce

#### VacancyAggregationJob

지역별 원자료를 `산업코드 + 연도 + 반기` 단위로 재분배한 뒤, 전체 종사자 수를 가중치로 사용하여 전국 단위 빈일자리율·입직률·이직률을 계산합니다.

#### EmploymentAggregationJob

KSIC 산업코드를 프로젝트에서 사용하는 산업 그룹으로 변환하고 동일 그룹의 취업자 수를 합산합니다.

#### GrowthRateJob

산업별 시계열 데이터를 시간순으로 정렬하고 연속된 두 시점을 비교하여 증감률을 계산합니다.

## CareerNet Open API

프로젝트의 산업 분류와 CareerNet 직군 분류 체계가 서로 달라 별도의 직군-산업 매핑을 적용했습니다.

1. 직업 목록 API를 호출해 대상 직업 선별
2. 상세조회 API를 순차 호출
3. 자격증, 훈련, 교육과정 정보 수집
4. Oracle `TB_CAREER_JOB` 테이블에 적재

## 데이터베이스

주요 테이블은 다음과 같습니다.

- `TB_INDUSTRY_VACANCY`
- `TB_INDUSTRY_EMPLOYMENT`
- `TB_INDUSTRY_INDEX`
- `TB_INDUSTRY_MAP`
- `TB_EMPLOYMENT_GENDER`
- `TB_CAREER_JOB`
- `TB_BOARD_POST`

SQL 스키마는 `database/`에서 확인할 수 있습니다.

## 웹 애플리케이션 구조

프로젝트 문서 기준 주요 계층은 다음과 같습니다.

### Entity
- IndustryVacancy
- IndustryEmployment
- IndustryIndex
- EmploymentGender
- BoardPost
- CareerJob

### Repository
- IndustryVacancyRepository
- IndustryEmploymentRepository
- IndustryIndexRepository
- EmploymentGenderRepository
- BoardPostRepository
- CareerJobRepository

### Service
- IndustryService
- BoardService
- CareerJobService

### Controller
- IndustryController
- IndustryViewController
- BoardController

## 주요 기능

- 산업별 취업자 증감률 순위
- 빈일자리율·입직률·이직률 시각화
- 성별 취업자 추이 비교
- 산업 대분류별 세부 구성 조회
- CareerNet 기반 세부 직업 탐색
- 성장 산업 및 적성유형 기반 진로 추천
- 산업별 커뮤니티 게시판

## 트러블슈팅

### CSV 콤마 파싱

산업명 등 값 자체에 콤마가 포함될 때 단순 `split(",")` 방식으로는 컬럼이 밀리는 문제가 있었습니다. 따옴표 내부/외부 상태를 추적하는 방식으로 CSV를 분리하도록 수정했습니다.

### CareerNet API 응답 포맷

상세조회 API에서 JSON을 기대했으나 XML 응답이 오는 문제가 있어 요청 파라미터에 `contentType=json`을 명시해 해결했습니다.

## 보안 설정

DB 비밀번호와 Open API Key는 저장소에 직접 기록하지 않습니다.

`.env.example`에는 필요한 환경변수 이름만 제공하며 실제 값은 로컬 환경에서 설정합니다.

```env
DB_URL=jdbc:oracle:thin:@localhost:1521/orcl
DB_USERNAME=your_username
DB_PASSWORD=your_password
CAREERNET_API_KEY=your_api_key
```

Spring 설정에서는 다음과 같이 환경변수를 참조할 수 있습니다.

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
careernet.api.key=${CAREERNET_API_KEY}
```

## 현재 저장소 구조

```text
JobRader/
├─ README.md
├─ .gitignore
├─ .env.example
├─ database/
│  ├─ schema.sql
│  └─ career_job.sql
├─ hadoop/
│  └─ src/main/java/com/jobradar/hadoop/
│     ├─ VacancyAggregationJob.java
│     ├─ EmploymentAggregationJob.java
│     └─ GrowthRateJob.java
└─ spring/
   └─ application.properties.example
```

## 시연 영상

YouTube: https://youtu.be/_K97BqRLlQ0

## 향후 개선

- 산업별 경쟁도 분석
- 청년·고령층 등 연령별 맞춤 추천 기능 확장
