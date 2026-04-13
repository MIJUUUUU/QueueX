# QueueX 파일 정리

현재 워크스페이스를 기준으로, 각 파일이 어떤 역할인지 빠르게 파악할 수 있도록 정리한 문서입니다.

## 한눈에 보기

- 이 프로젝트는 `Java 17 + Maven + MySQL` 기반의 콘솔형 스마트 웨이팅 시스템입니다.
- 실행 진입점은 `src/main/java/app/Main.java` 입니다.
- 구조는 대체로 `UI -> Service -> DAO -> DB`, 그리고 데이터 전달용 `DTO` 로 나뉩니다.
- `docker/` 와 `docker-compose*.yml` 은 로컬/공유 실행 환경 구성을 담당합니다.
- `target/` 은 Maven 빌드 결과물이므로 보통 직접 수정하지 않습니다.

## 루트 파일

| 파일 | 쓰임 |
| --- | --- |
| `README.md` | 프로젝트 소개, 핵심 기능, 고객/관리자 플로우, 입력 규칙, 추천 로직 등 전반적인 사용 문서입니다. |
| `pom.xml` | Maven 빌드 설정 파일입니다. Java 17, MySQL Connector, 실행 가능한 fat jar 생성 설정이 들어 있습니다. |
| `Dockerfile` | Maven으로 프로젝트를 빌드한 뒤 JRE 이미지에서 `app.jar` 를 실행하는 멀티 스테이지 이미지 정의입니다. |
| `docker-compose.yml` | MySQL 컨테이너와 앱 컨테이너를 함께 실행하는 기본 구성입니다. |
| `docker-compose.dev.yml` | 앱 이미지를 로컬 `Dockerfile` 로 빌드할 때 쓰는 개발용 Compose 오버라이드 파일입니다. |
| `docker-compose.shared.yml` | 앱이 외부 MySQL 주소를 바라보도록 환경변수를 덮어쓰는 공유/협업용 Compose 설정입니다. |
| `PR.md` | PR 작성 예시 문서입니다. 현재는 DB 스키마 초기 구축 예시가 들어 있습니다. |
| `.gitignore` | 빌드 산출물, IDE 설정, 로컬 캐시 파일을 Git 추적 대상에서 제외합니다. |
| `.dockerignore` | Docker 이미지 빌드 시 불필요한 파일과 폴더를 제외합니다. |
| `QueueX.iml` | IntelliJ 계열 IDE 프로젝트 메타데이터입니다. |
| `.vscode/settings.json` | VS Code Java 프로젝트의 소스 경로와 출력 경로를 맞추는 설정입니다. |

## Docker / DB 초기화

| 파일 | 쓰임 |
| --- | --- |
| `docker/mysql/init/schema.sql` | `customer`, `admin`, `store`, `menu`, `waiting`, `order_item` 테이블과 인덱스를 생성하는 초기 스키마입니다. |
| `docker/mysql/init/seed.sql` | 관리자/고객/매장/메뉴/대기/주문 샘플 데이터를 넣는 초기 데이터 파일입니다. |

## 소스 코드 구조

### `src/main/java/app`

| 파일 | 쓰임 |
| --- | --- |
| `src/main/java/app/Main.java` | 프로그램 시작점입니다. DB 연결 확인 후 고객/관리자 콘솔 UI를 분기하고 전체 메인 루프를 관리합니다. |

### `src/main/java/ui`

화면 입출력과 사용자 흐름 제어를 담당하는 콘솔 UI 계층입니다.

| 파일 | 쓰임 |
| --- | --- |
| `src/main/java/ui/CustomerUI.java` | 고객 로그인/회원가입, 가게 선택, 내 대기 조회 등 고객 메인 흐름을 담당합니다. |
| `src/main/java/ui/WaitingRegisterUI.java` | 고객이 매장을 선택한 뒤 인원 입력, 선주문, 대기 등록까지 진행하는 화면 로직입니다. |
| `src/main/java/ui/AdminUI.java` | 관리자 인증, 관리자 메인 메뉴 진입, 하위 관리자 UI 연결을 담당합니다. |
| `src/main/java/ui/AdminMenuUI.java` | 관리자 메뉴 선택 흐름의 일부를 담당하는 보조 UI 클래스입니다. |
| `src/main/java/ui/AdminStoreUI.java` | 관리자가 운영할 매장을 선택하고 대기 현황을 보는 흐름을 담당합니다. |
| `src/main/java/ui/AdminSeatUI.java` | 수용 가능 인원 입력, 추천 손님 호출, 입장/노쇼 처리 등 좌석 운영 화면을 담당합니다. |
| `src/main/java/ui/AdminStatsUI.java` | 매장별 오늘 통계/전체 통계를 조회하는 관리자 통계 화면입니다. |

### `src/main/java/service`

비즈니스 규칙과 여러 DAO 조합 로직을 담당하는 계층입니다.

| 파일 | 쓰임 |
| --- | --- |
| `src/main/java/service/CustomerService.java` | 고객 회원가입, 로그인, 중복 가입 여부, 비밀번호 검증 같은 인증 관련 로직을 담당합니다. |
| `src/main/java/service/AdminService.java` | 관리자 인증, 로그인 시도 제한, 매장/통계 조회 등 관리자 기능용 서비스입니다. |
| `src/main/java/service/WaitingService.java` | 매장 조회, 메뉴 조회, 대기 등록, 호출/입장/노쇼/취소, 주문 요약, 대기 순서 계산 등 웨이팅 핵심 로직을 담당합니다. |
| `src/main/java/service/RecommendationService.java` | 매장 수용 인원과 대기 리스트를 바탕으로 추천 호출 대상을 계산하는 추천 로직 전용 서비스입니다. |

### `src/main/java/dao`

DB와 직접 통신하는 데이터 접근 계층입니다.

| 파일 | 쓰임 |
| --- | --- |
| `src/main/java/dao/CustomerDAO.java` | 고객 전화번호 조회, 고객 등록을 담당합니다. |
| `src/main/java/dao/AdminDAO.java` | 관리자 인증 조회, 관리 매장 조회, 관리자 통계 조회를 담당하는 DAO입니다. |
| `src/main/java/dao/StoreDAO.java` | 전체 매장 조회, 매장 단건 조회를 담당합니다. |
| `src/main/java/dao/MenuDAO.java` | 매장별 메뉴 목록 조회를 담당합니다. |
| `src/main/java/dao/WaitingDAO.java` | 활성 대기 여부 확인, 다음 대기번호 계산, 대기 등록, 매장별 대기 조회, 상태 변경 등 웨이팅 DB 작업을 담당합니다. |
| `src/main/java/dao/OrderItemDAO.java` | 선주문 항목 저장과 대기별 주문 요약 조회를 담당합니다. |

### `src/main/java/dto`

계층 간 데이터를 주고받기 위한 객체 모음입니다.

| 파일 | 쓰임 |
| --- | --- |
| `src/main/java/dto/Customer.java` | 고객 정보 DTO 입니다. |
| `src/main/java/dto/Admin.java` | 관리자 정보 DTO 입니다. |
| `src/main/java/dto/Store.java` | 매장 정보 DTO 입니다. |
| `src/main/java/dto/Menu.java` | 메뉴 정보 DTO 입니다. |
| `src/main/java/dto/Waiting.java` | 웨이팅 정보 DTO 입니다. 상태, 호출 시각, 등록 시각 등을 가집니다. |
| `src/main/java/dto/OrderItem.java` | 선주문 항목 DTO 입니다. |
| `src/main/java/dto/MenuStat.java` | 메뉴별 주문 통계 표현용 DTO 입니다. |
| `src/main/java/dto/AdminStatistics.java` | 관리자 통계 화면에 필요한 집계 데이터를 담는 DTO 입니다. |

### `src/main/java/common`

공통 유틸리티와 상수, DB 연결 보조 코드를 모아둔 패키지입니다.

| 파일 | 쓰임 |
| --- | --- |
| `src/main/java/common/DBUtil.java` | 환경변수 또는 기본값을 통해 MySQL 연결을 생성하는 DB 연결 유틸입니다. |
| `src/main/java/common/PasswordUtil.java` | 비밀번호/인증번호 해시 생성과 비교를 담당하는 보안 유틸입니다. |
| `src/main/java/common/PhoneNumberUtil.java` | 전화번호 정규화, 검증, 화면 표시용 포맷팅을 담당합니다. |
| `src/main/java/common/ValidationUtil.java` | 숫자 여부, 공백 여부, 범위 검사 등 입력 검증용 공통 함수 모음입니다. |
| `src/main/java/common/WaitingStatus.java` | `WAITING`, `CALLED`, `ENTERED`, `NOSHOW`, `CANCELED` 상태 문자열 상수 모음입니다. |
| `src/main/java/common/ConsoleStyle.java` | 콘솔 제목, 구분선, 정렬, 색상 표현 등 화면 출력 스타일 유틸입니다. |
| `src/main/java/common/TestDB.java` | DB 연결이 되는지 빠르게 확인하기 위한 단순 테스트 실행용 클래스입니다. |

## 테스트 / 빌드 산출물

| 경로 | 쓰임 |
| --- | --- |
| `src/test` | 현재 구조만 있고 실제 테스트 코드 파일은 없는 상태입니다. |
| `target/queuex-1.0-SNAPSHOT.jar` | Maven 빌드로 생성된 기본 JAR 파일입니다. |
| `target/queuex-1.0-SNAPSHOT-jar-with-dependencies.jar` | 의존성이 포함된 실행용 JAR 파일입니다. |
| `target/classes`, `target/test-classes` 등 | 컴파일 결과와 Maven 중간 산출물입니다. 직접 수정 대상은 아닙니다. |

## 구조 이해 포인트

- 실행 흐름은 보통 `Main -> UI -> Service -> DAO -> MySQL` 순서입니다.
- 고객과 관리자는 각각 별도 UI 흐름을 가지지만, 실제 대기/매장/주문 데이터는 동일한 DAO/DTO 를 공유합니다.
- 핵심 비즈니스 가치는 `RecommendationService` 와 `WaitingService` 에 모여 있습니다.
- DB 초기화가 필요한 경우 `docker/mysql/init/schema.sql`, `seed.sql` 을 먼저 보는 것이 가장 빠릅니다.

## 먼저 보면 좋은 파일

1. `README.md`
2. `src/main/java/app/Main.java`
3. `src/main/java/ui/CustomerUI.java`
4. `src/main/java/ui/AdminUI.java`
5. `src/main/java/service/WaitingService.java`
6. `src/main/java/service/RecommendationService.java`
7. `docker/mysql/init/schema.sql`
