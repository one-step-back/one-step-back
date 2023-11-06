# ONE-STEP - 예술가들의 성장을 한걸음씩 도와주는 플랫폼<br>A Platform That Helps Artists Grow Step by Step

<img src="https://github.com/DevNathan/one-step-back/assets/142222091/390822a5-6ad9-4342-af0f-8ad21c37ce92">


## 1. 기획 의도
<img src="https://github.com/DevNathan/one-step-back/assets/142222091/975b7557-ada5-411a-95f9-63d18d46641b">
<br>
현재 국내 피아니스트 중에서 최고로 평가받는 조성진의 인터뷰 내용에 따르면 콩쿨을 제외하면 자신을 알릴 기회가 적다라고 국내의 '제한적인 공연 기회"에 대해 언급했으며 콩쿨로 얻은 명성으로 매니지먼트사와 계약을 맺을 수 있다고 한다. 즉, 대부분의 예술가들은 자신을 알릴 기회가 너무 적으며 그에따라 수입도 불안정적이기 때문에 매 3년마다 문화체육관광부에서 조사하는 "예술인 실태조사"에 따르면 이들의 평균 수입은 월 100만원 수준으로 매우 곤궁하며 이 수익편차조차도 매우 큰 것으로 나타난다.<br>
<br>때문에 자신을 알릴 제한적인 기회와 금전적인 문제들을 완화하고자 현재의 ONE-STEP을 기획하였다.

## 2. 기대 효과
<img src="https://github.com/DevNathan/one-step-back/assets/142222091/d5259d80-0312-473f-b636-606d94788af0">
<br>
1. 예술가들의 창작 및 연주활동 등을 지속적으로 장려할 수 있음<br>
2. 예술가들이 겪는 금전적 어려움을 해결하는데 도움이 될 수 있음<br>
3. 아직 알려지지 않은 무명의 예술가들을 발굴 해내는 플랫폼이 될 수 있음<br>
4. 사회 문화활동을 더욱 장려 함으로서 전반적인 문화수준을 한층 더 끌어올릴 수 있음

## 3. 프로젝트 사용 툴
<img src="https://github.com/DevNathan/one-step-back/assets/142222091/04826042-2a54-4b4b-b4ec-5f69ab731943">
<img src="https://github.com/DevNathan/one-step-back/assets/142222091/d4884c1a-5772-4357-862c-478197a879d3">

<br>
- Java<br>
- Java Script<br>
- tomcat<br>
- jQuery<br>
- MyBatis<br>
- Spring Boot<br>
- Oracle<br>
- Visual Studio Code<br>
- InteliJ<br>
- DBeaver<br>
- Sourcetree<br>
- git, github<br>
- JSON<br>
- Ajax<br>
- JDK 11.0.15<br>
- Kakao DEVELOPER API<br>
- BootPay API
<br><br>
For Communication,<br>
- Slack<br>
- Discord<br>

## 4. ERD
<img src="https://github.com/DevNathan/one-step-back/assets/142222091/c58118ad-0059-44d7-996e-dbddad982cf8">
ErdCloud : https://www.erdcloud.com/d/nW9kiBxhw3HMtfbgE


## 5. 담당업무
### 5-1 웹 퍼블리싱
<img width="1166" alt="OneStep퍼블리싱" src="https://github.com/DevNathan/one-step-back/assets/142222091/0ed7f338-6f54-4303-8b1f-f3c155983bff">
23.09.11 ~ 23.09.25 진행됨<br><br>
1. 나의 아티스트<br>
2. 내 보관함<br>
  - 영상 보관함<br>
  - 게시글 보관함<br>
3. 통합 검색창
  - 게시글 검색결과<br>
  - 영상 검색결과<br>
  - 아티스트 검색결과<br>
4. 크라우드 펀딩<br>
5. 크라우드 펀딩 결제<br>
6. 크라우드 펀딩 이벤트 요청 및 수락 페이지<br>
7. 마이페이지<br>
8. 팬 커뮤니티

### 5-2 백엔드 및 프론트엔드
<img width="1080" alt="OneStep백엔드" src="https://github.com/DevNathan/one-step-back/assets/142222091/76923db5-4fc0-49fa-908c-d8470244701a">
23.10.12 ~ 23.10.30 진행됨<br><br>
1. 메인페이지<br>
2. 예술가 메인페이지<br>
3. 예술가 게시글 페이지<br>
- 게시글 리스트<br>
- 게시글 상세<br>
- 게시글 작성<br>
- 게시글 수정<br>
4. 예술가 영상 페이지<br>
- 영상 리스트<br>
- 영상 상세<br>
- 영상 작성<br>
- 영상 수정<br>
5. 문의 작성페이지<br>
6. 예술가 후원 페이지

## 6. 느낀점
### 6-1 어려웠던 점 및 개선해야 될 점
📌 자바스크립트를 통해 처음으로 콜백함수를 통한 서비스 모듈화를 통해 좋아요나 북마크 서비스등을 모듈화 시켜볼 수 있었으나 html과 자바스크립트를 분리하지 않고 사용함에따라 모듈화를 했음에도 불구하고 결과적으로 재사용하지 못하였다.<br><br>
⏩ 앞으로의 프로젝트에서는 이 문제점을 충분히 인지하여 자바스크립트에 서비스를 분리하여 사용하여 코드의 가독성을 끌어올리고 작업시간을 단축하여야 하겠음. 그리고 가능하다면 현재 만들어진 프로젝트를 다시 수정하여 자바스크립트 부분의 가독성을 고치는 개선작업을 해보면서 연습해보는 것도 중요할 것.<br><br><br>

📌 BootPayAPI를 사용하여 결제까지 되는 것은 확인했으나 결제 완료 후의 데이터를 받아오지 못하는 문제점이 있었음<br><br>
⏩ BootPayAPI 사용 방법을 다시한번더 자세히 읽어보고 추후 완성 작업을 하여야 하겠음.

### 6-2 프로젝트 종료 후 소감
