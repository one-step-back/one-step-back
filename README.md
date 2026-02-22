# ONE-STEP: 예술가들의 성장을 돕는 강력한 한 걸음
**A Platform for Sustainable Artist Growth**

<img width="2000" height="368" alt="header-logo" src="https://github.com/user-attachments/assets/f1bc5e28-d931-494b-9b01-ede0edbe333d" />

## 1. 기획 의도 (Motivation)
<img src="https://github.com/DevNathan/one-step-back/assets/142222091/975b7557-ada5-411a-95f9-63d18d46641b">

예술가들의 제한적인 공연 기회와 불안정한 수입 구조를 혁신합니다. 조성진 피아니스트의 인터뷰처럼 국내의 제한적인 기회와 금전적 문제를 해결하고, 무명 예술가들이 자신의 가치를 증명하여 지속 가능한 창작 활동을 이어갈 수 있는 생태계를 구축합니다.

## 2. 기대 효과 (Core Value)
<img src="https://github.com/DevNathan/one-step-back/assets/142222091/d5259d80-0312-473f-b636-606d94788af0">

- 예술가들의 창작 및 연주 활동을 지속적으로 장려합니다.
- 금전적 어려움을 해결하고 무명 예술가를 발굴하는 데 도움이 됩니다.
- 사회 문화 활동을 더욱 장려함으로써 전반적인 문화 수준을 한층 더 끌어올립니다.

## 3. 테크 스택 (Tech Stack)

| 분류 (Category) | 사용 기술 (Technologies) |
| :--- | :--- |
| **Language** | [![Java 17](https://img.shields.io/badge/Java_17_(LTS)-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/) [![TypeScript](https://img.shields.io/badge/TypeScript_5.9.3-3178C6?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/) |
| **Framework** | [![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.5.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot) [![Vite](https://img.shields.io/badge/Vite_7.3.1-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vitejs.dev/) |
| **Data Access** | [![MyBatis](https://img.shields.io/badge/MyBatis_3.0.3-C60F04?style=for-the-badge&logo=mybatis&logoColor=white)](https://mybatis.org/mybatis-3/) [![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://flywaydb.org/) |
| **Database** | [![Oracle](https://img.shields.io/badge/Oracle_21c_XE-F80000?style=for-the-badge&logo=oracle&logoColor=white)](https://www.oracle.com/) |
| **Library** | [![jQuery](https://img.shields.io/badge/jQuery_4.0.0-0769AD?style=for-the-badge&logo=jquery&logoColor=white)](https://jquery.com/) [![Chart.js](https://img.shields.io/badge/Chart.js_4.5.1-FF6384?style=for-the-badge&logo=chartdotjs&logoColor=white)](https://www.chartjs.org/) |
| **Build & DevOps** | [![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/) [![Node.js](https://img.shields.io/badge/Node.js_20.11.0-339933?style=for-the-badge&logo=nodedotjs&logoColor=white)](https://nodejs.org/) |
| **External API** | [![Kakao](https://img.shields.io/badge/Kakao_Developer-FFCD00?style=for-the-badge&logo=kakao&logoColor=black)](https://developers.kakao.com/) [![PortOne](https://img.shields.io/badge/PortOne-F81404?style=for-the-badge&logo=iamporter&logoColor=white)](https://portone.io/) |

## 4. 핵심 시스템 아키텍처 (Key Architecture)
### 빌드 파이프라인 통합 (Build Pipeline)
Gradle과 Vite를 연동하여 단일 명령어로 백엔드와 프론트엔드를 동시 빌드합니다. `processResources` 실행 전 `npm run build`가 선행되어 번들링된 자산이 정적 리소스 경로에 자동 주입됩니다.

### 데이터 및 보안 (Data & Security)
- **Flyway**: DB 마이그레이션 자동화로 스키마 동기화 문제를 해결합니다.
- **Environment Isolation**: application.yml 및 민감 자원을 Git 추적에서 제외합니다.
- **Type Safety**: 프론트엔드 전 영역에 TypeScript를 적용하여 런타임 에러를 방지합니다.

## 5. 데이터 모델 (ERD)
예술가, 펀딩, 피드, 결제 정보를 유기적으로 관리하는 데이터 관계 설계입니다.

[View ERD on ErdCloud](https://www.erdcloud.com/d/nW9kiBxhw3HMtfbgE)
