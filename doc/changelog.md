# Change Log

## [0.1.0] - 2025-12-08
Major Changes (Architecture & Infrastructure)

    Framework Upgrade: Spring Boot 2.7.x → 3.2.4 (Java 11 → 17) 마이그레이션

        보안 이슈 해결 및 최신 기술 스택 적용을 위해 OSS 지원이 종료된 구버전에서 업그레이드

    Database Migration: Oracle 10g/11g → Oracle 21c XE

        Docker 환경에서의 이미지 수급 문제 및 최신 Flyway/Spring Boot와의 호환성 확보를 위해 DB 버전 상향

        Legacy 드라이버(ojdbc8)를 ojdbc11로 교체

Refactor

    Schema Management Strategy: JPA(ddl-auto) 의존성 제거 및 Flyway 도입

        JPA의 불필요한 오버헤드를 제거하고, MyBatis 중심의 프로젝트 성격에 맞춰 Flyway를 통한 명시적인 DB 형상 관리 체계 구축

    Configuration Modernization: MyBatis 설정 방식 변경

        Legacy 방식의 Java Config(MyBatisConfiguration.java) 삭제

        Spring Boot Auto Configuration을 활용한 application.yml 기반 설정으로 이관하여 관리 포인트 일원화

Fixed

    Dependency Conflicts: snakeyaml 누락 및 javax → jakarta 패키지명 변경 등 버전 업그레이드에 따른 호환성 문제 해결

    MyBatis Mapping: TypeAliases 패키지 스캔 경로 누락으로 인한 DTO/VO 매핑 에러 수정