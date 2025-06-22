# PAY: 통합 결제 노티피케이션 처리 모듈

이 모듈은 **PG 통합 시스템** 내에서 결제 결과 통보(Webhook) 수신, 정규화, 영속화 및 후처리를 담당합니다.  
다양한 상위 PG사로부터 수신된 결제 결과 데이터를 **일관된 내부 도메인 모델**로 변환하고, **상태 기반의 후처리 로직**을 수행합니다.

현재는 **DLA (Data Lifeline Architecture)** 기반의 `payment-service` 일부를 구현 중이며,  
향후 Kafka, Embedded Debezium, 멀티모듈 Docker 기반의 확장형 아키텍처를 지원할 예정입니다.

## 프로젝트 구조

```
com.pg.payment
│
├── ingress            // Controller 계층: 외부 Webhook 수신 엔드포인트
├── dto                // 상위 PG사별 원시 DTO 클래스 (예: Kcp, Smartro 등)
├── domain             // 도메인 모델 및 결제 상태 관리
├── normalize          // PG사별 DTO → 내부 이벤트 매핑 계층
├── processor          // 이벤트 처리 로직 (승인/취소/환불 등)
├── persistence        // Entity 및 Repository 정의 (Pay, Refund, ReqLog 등)
├── config             // 설정 클래스 (ThreadPool, Jackson, PGType 등)
└── util               // 예외 처리, 응답 포맷, 로깅, MDC 등 공통 유틸
```

## 주요 특징

### PG사별 DTO 처리

- 상위 PG사의 Webhook 데이터를 전용 DTO로 정의
- 데이터 형식 변경 및 필드 추가에 유연하게 대응

### 정규화 계층 분리

- PG사별 DTO → 내부 표준 이벤트 객체로 매핑
- 비즈니스 로직과 외부 스키마를 명확히 분리

### Processor 기반 라우팅 구조

- 정규화된 이벤트는 `EventProcessor` 구현체로 위임
- `PayProcessor`, `RefundProcessor` 등 책임 분리 명확화

### 추적 가능한 영속화

- `trxId`, `trackId`, `regDate`, `vanTrxId` 등 전 메타 필드 포함 저장
- 이중 트랜잭션, 트리거, 캡처 연동 고려한 설계

### 전략 기반 TRX ID 생성

- 현재는 DB Sequence 기반
- 향후 Redis, Snowflake 등으로 확장 가능하도록 전략 패턴 설계

### Virtual Thread 기반 비동기 확장

- Java 24 가상 쓰레드를 활용하여 수신 처리 시 경량 동시성 지원

### MDC 기반 로깅

- `traceId`, `requestId`, `pg사` 기반의 전 구간 트레이싱 로깅 가능
- 로그 파일 및 분리 정책(결제/에러/운영) 고려

## 기술 스택

- Java 24 (Virtual Thread 기반)
- Spring Boot 3.2+
- JPA / Hibernate
- MariaDB / Aurora MySQL
- MapStruct
- Logback (MDC 적용)
- 향후 확장:
    - Kafka 기반 이벤트 전파
    - Embedded Debezium CDC 연계
    - Docker 기반 멀티모듈 및 GitOps 배포

## 네이밍 및 데이터 정책

- `Pay`, `Refund` 엔티티에는 payload 전문을 저장하지 않음  
  → 전문은 별도 `pg_trx_reqlog`, `pg_trx_reslog` 등 테이블로 관리

- 주 테이블은 `pg_trx_pay`이며, 트리거를 통해 아래 연계됨:
    - `pg_trx_cap` (정산 대상)
    - `ht_trx_pay` (이력 백업용)

- 컬럼은 모두 CamelCase 명명으로 유지되며, JPA 물리 네이밍 전략 비활성화 필요

## 개발 로드맵

### 1차 예정

- [x] PG사별 Webhook DTO 정의 (예: Daou, Toss, Kcp 등)
- [x] Normalize 매핑 계층 구현
- [x] TrxIdGenerator 인터페이스 및 DB 전략 구현
- [x] Processor 구조 설계 및 구현
- [x] Entity 및 Repository 구성

### 2차 예정

- [ ] 관리자용 Dashboard 및 상태 조회 UI
- [ ] MDC 기반 trace 시각화 및 로그 파일 분리
- [ ] 상태머신 기반 처리 흐름 구현 (`PaymentState`, `RefundState`)
- [ ] Webhook 처리 Worker 및 Retry Queue 구성
- [ ] Webhook 실패 건에 대한 수동 재처리 기능 (UI or API)

### 향후 확장

- [x] Debezium 기반 CDC 연동
- [ ] DLA 기반 State Machine 구현
- [ ] APP Module, Batch Module 등 멀티모듈 구조 확장
- [ ] Kafka 기반 메시징 연계
- [ ] Docker 기반 멀티모듈 배포 환경 구축
- [ ] Redis 기반 TrxIdGenerator 전략 구현
- [ ] Webhook 시뮬레이터 CLI / UI 구현
- [ ] 서버 환경 구축 (APP, Payment, Batch, Util 서버 구성)

## 문의

개발 관련 문의: `dev_patrick@naver.com`  
(PG 결제 시스템 설계/운영 관련 지식 공유를 환영합니다.)
