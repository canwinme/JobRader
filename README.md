# JobRadar

데이터 기반 산업별 취업시장 분석 및 진로 탐색 지원 시스템입니다.

공공데이터를 수집·전처리하고 Hadoop MapReduce로 집계한 뒤 Oracle에 적재하여, Spring Boot/JPA와 JSP/Chart.js 기반 웹 화면에서 산업별 성장성·구인활력·취업자 추이·직업 정보를 조회할 수 있도록 구성했습니다.

## 기술 스택

| 구분 | 기술 |
| --- | --- |
| 언어 | Java |
| 전처리 / 분산 처리 | JDK 11, Hadoop 2.5.1, MapReduce, Linux/VMware |
| 데이터베이스 | Oracle 19c, JDBC |
| 백엔드 | Spring Boot 4.1.1, Spring Data JPA, Hibernate |
| 프론트엔드 | JSP, JSTL, Chart.js |
| 빌드 / 개발환경 | Maven, Eclipse IDE |

## 시스템 흐름

```text
KOSIS / 공공데이터포털 / CareerNet
                ↓
           Java 전처리
                ↓
        Hadoop MapReduce
                ↓
         JDBC DataLoader
                ↓
             Oracle
                ↓
       Spring Boot / JPA
                ↓
         JSP / Chart.js
```

오프라인 배치 영역과 온라인 서비스 영역을 분리했습니다. 배치 결과를 Oracle에 미리 적재하고, 웹 요청 시에는 DB 조회 중심으로 응답합니다.

## 데이터 출처

- KOSIS 경제활동인구조사: 성/산업별 취업자
- 공공데이터포털 사업체노동력조사: 산업별 빈일자리율·입직률·이직률
- CareerNet 직업정보 Open API: 직업·자격증·직업훈련·정규교육과정

## 전처리

### EmploymentDataPreprocessor
- KOSIS 가로형 데이터를 세로형으로 변환
- KSIC 산업 코드를 프로젝트 산업 그룹으로 매핑
- Hadoop 입력 형식인 `산업코드,연도,분기,취업자수` 생성

### VacancyDataPreprocessor
- CP949 원본을 읽어 UTF-8 기반 처리
- 산업명을 표준 산업 코드로 매핑
- 지역·산업·시점별로 분리된 4개 항목을 한 행으로 재조합
- Hadoop 입력 형식인 `지역,산업코드,연도,반기,전체종사자,빈일자리율,입직률,이직률` 생성

## Hadoop MapReduce

### VacancyAggregationJob
지역별 원자료를 산업·연도·반기 기준으로 재분배하고, 전체 종사자 수를 가중치로 사용해 전국 빈일자리율·입직률·이직률을 계산합니다.

### EmploymentAggregationJob
산업 코드를 프로젝트 산업 그룹으로 변환하고 동일 그룹의 취업자 수를 합산합니다.

### GrowthRateJob
산업별 시계열 데이터를 시간순으로 정렬한 뒤 연속된 시점을 비교해 증감률을 계산합니다.

## CareerNet 배치

### CareerNetJobSelector
CareerNet 직군과 프로젝트 산업 분류 간 매핑표를 이용해 대상 직업을 선별합니다.

### CareerNetDetailFetcher
선별된 직업의 상세 API를 순차 호출해 관련자격증·직업훈련·정규교육과정을 수집합니다.

API Key는 코드에 저장하지 않고 실행 인자로 전달하도록 구성했습니다.

## Oracle 적재

`DataLoader.java`가 전처리·집계 결과 CSV를 JDBC `PreparedStatement` 배치로 Oracle에 적재합니다.

주요 테이블:

- `TB_INDUSTRY_VACANCY`
- `TB_INDUSTRY_EMPLOYMENT`
- `TB_INDUSTRY_INDEX`
- `TB_EMPLOYMENT_GENDER`
- `TB_INDUSTRY_MAP`
- `TB_CAREER_JOB`
- `TB_BOARD_POST`

DB 접속 정보는 환경변수로 분리했습니다.

## Spring Boot 구조

### Controller
- `IndustryController`: 산업 분석 REST API
- `IndustryViewController`: JSP 화면 라우팅
- `BoardController`: 게시판 CRUD 및 첨부파일 다운로드

### Service
- `IndustryService`
- `CareerJobService`
- `BoardService`

### Repository
- `IndustryVacancyRepository`
- `IndustryEmploymentRepository`
- `IndustryIndexRepository`
- `EmploymentGenderRepository`
- `CareerJobRepository`
- `BoardPostRepository`

### Entity
- `IndustryVacancy`
- `IndustryEmployment`
- `IndustryIndex`
- `EmploymentGender`
- `CareerJob`
- `BoardPost`

## 주요 화면

현재 실제 라우팅 기준 JSP는 11종입니다.

- `index.jsp`
- `ranking.jsp`
- `trend.jsp`
- `industry-detail.jsp`
- `jobsearch.jsp`
- `recommend.jsp`
- `expansion.jsp`
- `expansion-youthsenior.jsp`
- `board-list.jsp`
- `board-write.jsp`
- `board-detail.jsp`

기존에 사용했던 `expansion-jobsearch.jsp`는 현재 Controller 라우팅과 중복되어 저장소에서 제외했습니다.

## 주요 기능

- 산업별 취업자 증감률 순위 조회
- 빈일자리율·입직률·이직률 시각화
- 성별 취업자 추이 비교
- 산업 대분류별 세부 구성 조회
- CareerNet 기반 세부 직업 탐색
- 성장 산업 및 직군 기반 진로 추천
- 산업별 커뮤니티 게시판 및 첨부파일 처리

## 트러블슈팅

### CSV 내부 콤마 처리
산업명 등 필드 내부에 콤마가 포함되어 단순 `split(",")` 방식에서 컬럼이 밀리는 문제가 있었습니다. 따옴표 내부/외부 상태를 추적하는 파서를 적용했습니다.

### CareerNet 응답 포맷
상세조회 API에서 JSON 대신 다른 형식이 오는 문제를 방지하기 위해 `contentType=json`을 명시했습니다.

### 인코딩
사업체노동력조사 원본의 CP949와 UTF-8 데이터를 전처리 단계에서 구분해 처리했습니다.

## 보안 / 환경 설정

실제 DB 계정·비밀번호와 로컬 업로드 경로는 저장소에 직접 기록하지 않습니다.

```env
DB_URL=jdbc:oracle:thin:@localhost:1521/orcl
DB_USERNAME=your_username
DB_PASSWORD=your_password
CAREERNET_API_KEY=your_api_key
UPLOAD_DIR=./uploads
```

Spring 설정은 환경변수를 참조합니다.

```properties
spring.datasource.url=${DB_URL:jdbc:oracle:thin:@localhost:1521/orcl}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jobradar.upload.dir=${UPLOAD_DIR:./uploads}
```

## 저장소 구조

```text
JobRader/
├─ README.md
├─ .gitignore
├─ .env.example
├─ database/
│  ├─ schema.sql
│  └─ career_job.sql
├─ pipeline/
│  ├─ preprocess/
│  │  └─ src/main/java/com/jobradar/preprocess/
│  │     ├─ EmploymentDataPreprocessor.java
│  │     └─ VacancyDataPreprocessor.java
│  └─ loader/
│     └─ DataLoader.java
├─ hadoop/
│  └─ src/main/java/com/jobradar/hadoop/
│     ├─ VacancyAggregationJob.java
│     ├─ EmploymentAggregationJob.java
│     └─ GrowthRateJob.java
└─ spring/
   ├─ pom.xml
   └─ src/main/
      ├─ java/com/jobradar/api/
      │  ├─ batch/
      │  ├─ controller/
      │  ├─ entity/
      │  ├─ repository/
      │  └─ service/
      ├─ resources/
      │  └─ application.properties
      └─ webapp/WEB-INF/views/
```

## 시연 영상

YouTube: https://youtu.be/_K97BqRLlQ0

## 향후 개선

- 산업별 경쟁도 분석
- 청년·고령층 등 연령별 맞춤 추천 기능 확장
