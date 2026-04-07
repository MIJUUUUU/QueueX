# QueueX
자바 미니 프로젝트

## common
- 공통 기능

## dto
- 데이터

## dao
- DB

## service
- 로직
---
# 🟢 QueueX - 스마트 웨이팅 시스템

> “줄이 아닌, 가장 적합한 고객을 호출하는 시스템”

---

## 📌 프로젝트 소개

QueueX는 기존 웨이팅 시스템의 단순 순번(FIFO) 방식에서 발생하는 비효율을 개선하기 위해 개발된  
**인원수 기반 고객 추천 웨이팅 시스템**입니다.

기존 시스템은 고객의 인원수를 고려하지 않아  
좌석이 비어 있음에도 입장이 불가능한 문제가 발생합니다.

QueueX는 이를 해결하기 위해  
👉 **매장 수용 인원에 맞는 고객을 자동으로 추천하는 방식**을 도입했습니다.

---

## 🎯 주요 기능

### 👤 고객 기능
- 로그인 / 회원가입
- 가게 선택
- 메뉴 조회
- 대기 등록
- 선주문 기능
- 내 대기 상태 조회
- 대기 취소

---

### 🛠 관리자 기능
- 관리자 인증 (인증번호 기반)
- 매장 선택 및 운영
- 인원수 기반 고객 추천 ⭐
- 고객 호출
- 입장 처리 / 노쇼 처리
- 통계 조회

---

## 🧠 핵심 로직

### 📌 인원수 기반 고객 추천
관리자 → 수용 인원 입력
→ 대기 고객 중 조건 만족 고객 필터링
→ FIFO 기준으로 우선순위 정렬
→ 추천 리스트 출력

✔ 단순 순번이 아닌  
👉 **“입장 가능한 고객 우선” 구조**

---

## 🗂 데이터베이스 설계

### 📌 주요 테이블

- `customer` : 고객 정보
- `store` : 매장 정보
- `admin` : 관리자 정보 (매장 단위)
- `menu` : 메뉴 정보
- `waiting` : 대기 정보 (핵심)
- `order_item` : 선주문 정보

---

## 프로젝트 구조

- `src/main/java/` : Java 소스 코드
- `src/main/java/app/Main.java` : 애플리케이션 엔트리포인트
- `docker/mysql/init/` : Docker MySQL 초기화 SQL
- `pom.xml` : Maven 빌드 설정
- `Dockerfile` : 앱 Docker 빌드 설정
- `docker-compose.yml` : 앱 + MySQL Docker Compose 설정

## 빌드 및 실행

로컬 Maven 빌드:

```bash
mvn clean package
```

JAR 실행:

```bash
java -jar target/queuex-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Docker 실행

앱과 DB를 한 번에 실행:

```bash
docker compose up --build -d
```

초기 스키마와 테스트 데이터는 `docker/mysql/init/` 아래 SQL 파일이 자동 실행됩니다.

앱 로그 확인:

```bash
docker compose logs -f app
```

DB 로그 확인:

```bash
docker compose logs -f mysql
```

현재 `app` 컨테이너는 [`Main.java`](/Users/miju/QueueX/src/main/java/app/Main.java) 실행 후 종료될 수 있습니다. 장시간 실행되는 서버 기능을 붙이면 계속 살아 있게 됩니다.
