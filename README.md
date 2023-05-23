# 📝 설문조사 중개 플랫폼, SSAFVEY

![logo](docs/img/SSAFVEY_LOGO.png)

## ⌛️ 프로젝트 진행 기간

2023.04.10(월) ~ 2023.05.19(금) (40일간 진행)<br>
SSAFY 8기 2학기 자율 프로젝트

## ✨ SSAFVEY 서비스 개요

### 기획 배경

- AI 생성 모델 중 GPT 를 활용한 서비스 개발
- 인공지능 영상 도메인으로써 그림을 활용

## 🏃 SSAFVEY 의 이용 목적

- 그림만으로 간단하게 소설을 작성할 수 있습니다.
- AI의 상상력을 빌려 생각치도 못한 소설을 볼 수 있습니다.
- 친구들과 그림 실력을 뽐내며 서비스를 즐길 수 있습니다.

## 🌞 팀원 소개

### 6B

비전공자 여섯이 모인 새로움에 관심 많은 6B

- 이정범 - 팀장, BackEnd, 배포
- 강신욱 - FrontEnd
- 김성수 - FrontEnd
- 김수빈 - FrontEnd
- 이유영 - BackEnd, 배포
- 최성빈 - BackEnd

## ✔️ 아키텍처 구성도

![architecture](docs/img/architecture.png)

## ✔️ 주요 기능

### 1) 손쉬운 설문조사 생성

- 원하는 장르(로맨스, 판타지, 추리, SF, 자유)를 선택
- Image Captioning을 통해 그림을 캡셔닝해 소재 추출
- ChatGPT를 통해 소설 생성
- 사용자가 원한다면 소설을 이어가기 위한 질문 생성 후에 위 2-3번을 반복

### 2) 설문조사 참여

- 소설 표지를 그림으로 그리면 Stable Diffusion을 이용하여 표지 생성
- 맘에 들지 않을 경우, 계속하여 생성 가능
- 사용자가 직접 제목, 한마디 요약을 작성

### 3) 설문조사 보상으로 받는 복권

- 소설 생성시 기다리는 시간에 지루함을 느낄 수 있음
- 이를 위한 빠른 템포의 '스네이크 게임'을 추가
- LocalStorage에 자신의 최고 점수도 저장하여 승부욕 증가
- 바닐라 JS를 이용하여 구현

### 4) 포인트로 상품 교환

- 다른 사용자가 제작한 소설을 열람하고, 한줄평 작성 가능
- 소설의 제목 혹은 소설 작성자를 기반으로 keyword 검색 가능
- 장르별 정렬로 자신이 관심있는 분야의 소설 확인 가능

## ✔️ 주요 기술

### 1) PWA

- Progressive Web App으로 웹과 네이티브 앱의 기능 모두의 이점을 갖도록 수 많은 특정 기술과 표준 패턴을 사용해 개발된 웹 앱
- AOS, IOS 모두 링크/설치를 통한 사용, 데스크탑을 이용한 사용 모두 가능

## ✔️ 프로젝트 파일 구조

```
Repository
 │
 ├── BE
 │     │
 │     ├── build
 │     │     ├── ...
 │     │
 │     ├── out
 │     │     ├── ...
 │     │
 │     ├── ssafvey
 │     │     ├── gradle
 |     |     |     ├── ...
 │     │     ├── src
 |     |     |     ├── main
 |     |     |     |     ├── ...
 |     |     |     ├── test
 │     │
 │     ├── build.gradle
 │     ├── gradlew
 │
 │
 ├── FE
 │     │
 │     ├── public
 │     │      ├── fonts
 │     │      │    ├── ...
 │     │      │
 │     │      ├── banner
 │     │      │    ├── ...
 │     │      │
 │     │      ├── excel
 │     │      │    ├── ...
 │     │      │
 │     │      ├── icons
 │     │      │    ├── ...
 │     │      │
 │     │      ├── navbar
 │     │      │    ├── ...
 │     │      │
 │     │      ├── survey
 │     │      │    ├── ...
 │     │      │
 │     │      ├── ...
 │     │
 │     ├── src
 │     │       ├── Api
 │     │       │       ├── ...
 │     │       │
 │     │       ├── Components
 │     │       │       ├── Create
 │     │       │       │      ├── ...
 │     │       │       │
 │     │       │       ├── Exchange
 │     │       │       │      ├── ...
 │     │       │       │
 │     │       │       ├── Home
 │     │       │       │      ├── ...
 │     │       │       │
 │     │       │       ├── Modal
 │     │       │       │      ├── ...
 │     │       │       │
 │     │       │       ├── MyPage
 │     │       │       │      ├── ...
 │     │       │       │
 │     │       │       ├── Search
 │     │       │       │      ├── ...
 │     │       │       │
 │     │       │       ├── SignIn
 │     │       │       │      ├── ...
 │     │       │       │
 │     │       │       ├── Survey
 │     │       │       │      ├── ...
 │     │       │
 │     │       ├── hooks
 │     │       │       ├── ...
 │     │       │
 │     │       ├── module
 │     │       │       ├── ...
 │     │       │
 │     │       ├── Pages
 │     │       │       ├── ...
 │     │       │
 │     │       ├── Store
 │     │       │       ├── ...
 │     │       │
 │     │       ├── types
 │     │       │       ├── ...
 │     │       │
 │     │       ├── UI
 │     │       │       ├── ...
 │     │       │
 │     │       ├── Util
 │     │       │       ├── ...
 │     │       │
 │     │       ├── App.module.css
 │     │       ├── App.tsx
 │     │       ├── index.css
 │     │       ├── main.tsx
 │     │       ├── router.tsx
 │     │       ├── vite-env.d.ts
 │     │
 │     ├── README.md
 │     ├── ...
 │
 │
 ├── jenkins
 │     │
 │     ├── backjenkinsfile
 │     ├── frontjenkinsfile
 │
 │
 ├── docs
 │     │
 │     ├── img
 │     │    ├── ...
 │     │
 │     ├── ...
 │
 │
 ├── exec
 │     │
 │     ├── SSAFVEY_포팅_매뉴얼.pdf
 │
 ├── README.md
 ├── ...
```

## ✔️ 협업 툴

- GitLab
- Notion
- Figma
- JIRA
- MatterMost
- Webex

## ✔️ 협업 환경

- GitLab
  - 코드의 버전 관리
  - 개발 이슈 관리 및 해결을 위한 회의
  - MR과 팀원의 코드리뷰
- Notion
  - 기획 단계에서 도출된 아이디어 정리
  - 회의록과 팀미팅을 기록하여 의견과 해결사항을 정리
  - 팀 그라운드 룰을 정리
  - Jira 일정 계획 수립
  - Git Commit 컨벤션 정리
  - 참고자료 정리
  - Back-end, Front-end 별 개발 이슈 정리
  - API 상태 코드 정리
- Figma
  - UI/UX에 초점을 둔 실시간 협업
  - SSAFVEY 만의 메인 컬러 지정
  - 목업과 와이어프레임 정리
- Jira
  - 일주일 단위로 프로젝트 일정 관리
  - 2023.04.10 ~ 2023.05.19 기간의 일정 관리
  - 기획, 설계, 개발, 정기 회의, 내부 행사, 개인 공부, 발표

## ✔️ 프로젝트 산출물

- [기능 명세서](https://chipped-cart-851.notion.site/3af3b36b0dcf41b2a16622d5b459edfd?v=1b071cec2e7f4afdbfc087d9dcd0939c)
- [API 명세서](https://chipped-cart-851.notion.site/8c6085ecf0884dba9fadcbb47c576b34?v=7d87f6d9b8e74d2e91888e05f74e39ba)
- [ER-Diagram](docs/img/erd.png)

## ✔️ 프로젝트 결과물

- [포팅 메뉴얼](https://chipped-cart-851.notion.site/SSAFVEY-895b4f7e839c4e87a54aabb1c600d5db)

## 📝 Next Novel 서비스 화면

### 홈 및 로그인/회원가입

![img](docs/img/kakaologin.gif)

### 설문조사 참여하기

![img](docs/img/dosurvey.gif)

### 설문조사 공유하기

![img](docs/img/sharesurvey.gif)

### 설문조사 만들기

![img](docs/img/makesurvey.gif)

### 설문조사 결과보기

![img](docs/img/resultsurvey.gif)

### 내정보보기

![img](docs/img/mypage.gif)

### 기프티콘 사용하기

![img](docs/img/usecoupon.gif)

### 로또 열기

![img](docs/img/opencoupon.gif)

<hr>

## 👀 총평
