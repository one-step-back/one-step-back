# Changelog
## [1.0.0] - 2026-02-21

### Infrastructure
* Spring Boot 3.2.4 및 Java 17 마이그레이션 완료
* Oracle 21c XE 및 ojdbc11 적용
* Gradle-Vite 통합 빌드 파이프라인 구축

### Refactoring
* Flyway 도입 및 DB 형상 관리 수립
* MyBatis Auto Config 및 application.yml 설정 통합
* 모든 JS 로직 TypeScript 전환
* 에러 페이지 스타일 통합 및 외부 의존성 제거

### Fixed
* 펀딩 결제 로직 및 PortOne 연동 보정
* Chart.js v4 규격 적용 및 렌더링 에러 수정
* 메인 페이지 펀딩 라우팅 정상화

### Security
* application.yml 및 업로드 디렉토리 격리
* 빌드 스크립트 JSDoc 표준 주석 적용

---

# OneStep v1.0.0 Renewal Report

### 1. Infrastructure
* Java 17, Spring Boot 3.2 마이그레이션
* Oracle 21c XE 전환 및 ojdbc11 적용
* Flyway 도입으로 DB 버전 관리 기강 확립

### 2. Frontend
* Gradle-Vite 통합 파이프라인으로 빌드 자동화
* JS 전량 TypeScript 전환 및 타입 안정성 확보
* 에러 페이지(403, 404, 500) 통합 및 외부 의존성 제거

### 3. Logic & Security
* 펀딩 상세 라우팅 구조 정상화
* 결제 로직 내 인라인 스크립트 제거 및 PortOne 연동 최적화
* application.yml 보안 격리 및 템플릿 제공