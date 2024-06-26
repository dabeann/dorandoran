# 👥 dorandoran 👥
![image](https://github.com/dabeann/dorandoran/assets/127164905/478979b6-a701-42a5-beda-2c7d4d9cf0eb)

## 📃 서비스 소개 📃
"우울증을 앓고 있는 노숙인들의 비율이 매우 높아요." <br>
"심리상담 전문 인력이 부족해요💦" <br>
그래서 만들었습니다! dorandoran 은 정서안정에 도움이 되는 생성형 AI를 활용한 개인화 챗봇을 제공합니다.
<br/>
<br/>
<br/>

## 😽 핵심 기능 😽
- 심리검사를 통해 노숙인의 상태를 파악하고 점수화
- 상담을 통한 나의 심리상태 변화 확인
- 심리 상담 채팅 + 음성 인식 기능
- 채팅 중 부정적인 단어를 사용할 경우 AI가 판단하여 사회복지사분께 알림 발송
- 개인화된 오늘의 명언, 명상, 치료 콘텐츠
<br/>
<br/>

## 🛠 Tech Stack 🛠 
### Backend
- ![QueryDSL](https://img.shields.io/badge/QueryDSL-6DB33F?style=plastic&logo=Spring&logoColor=white)
- ![Java](https://img.shields.io/badge/Java-ED8B00?style=plastic&logo=&logoColor=white)
- ![ChatGPT API](https://img.shields.io/badge/ChatGPT%20API-7FE719?style=plastic&logo=&logoColor=white)
- ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=plastic&logo=Spring&logoColor=white)
- ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=plastic&logo=Spring%20Security&logoColor=white)
- ![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=plastic&logo=Spring&logoColor=white)
- ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=plastic&logo=PostgreSQL&logoColor=white)

### Infrastructure
- ![Amazon EC2](https://img.shields.io/badge/Amazon_EC2-FF9900?style=plastic&logo=Amazon%20EC2&logoColor=white)
- ![Amazon RDS](https://img.shields.io/badge/Amazon_RDS-527FFF?style=plastic&logo=Amazon%20RDS&logoColor=white)
- ![Redis](https://img.shields.io/badge/Redis-FF4438?style=plastic&logo=Redis&logoColor=white)

### DevOps
- ![Github Actions](https://img.shields.io/badge/Github_Actions-2088FF?style=plastic&logo=Github%20Actions&logoColor=white)

### Security
- ![JSON Web Tokens](https://img.shields.io/badge/JSON_Web_Tokens-000000?style=plastic&logo=JSON%20Web%20Tokens&logoColor=white)
<br/>
<br/>

## 🌲 서비스 아키텍처 🌲

<br/>
<br/>
<br/>

## 🌱 ERD 🌱
![image](https://github.com/dabeann/dorandoran/assets/127164905/b0bab939-f1f4-4737-8c40-a57be5de0444)
<br/>
<br/>
<br/>

## 📙 API Docs 📙
[Swagger 문서 확인하러 가기](https://dorandoran.store/swagger-ui/index.html)
<br/>
<br/>
<br/>

## 폴더 구조
```markdown
├─DorandoranApplication.java
├─assessment
│  ├─controller
│  │      PsychologicalAssessmentController.java
│  │      
│  ├─domain
│  │  ├─entity
│  │  │      PsychologicalAssessmentAnswer.java
│  │  │      PsychologicalAssessmentQuestion.java
│  │  │      
│  │  ├─request
│  │  │      PsychologicalAssessmentRequest.java
│  │  │      
│  │  └─response
│  │          PsychologicalAssessmentResponse.java
│  │          
│  ├─repository
│  │      PsychologicalAssessmentRepository.java
│  │      UserMentalStateRepository.java
│  │      
│  ├─service
│  │      PsychologicalAssessmentService.java
│  │      
│  └─swagger
│          UserSwaggerConfig.java
│          
├─common
│  ├─domain
│  │  │  BaseDateTimeEntity.java
│  │  │  Disease.java
│  │  │  ErrorCode.java
│  │  │  MeditationDuration.java
│  │  │  
│  │  ├─assessment
│  │  │      PsychologicalAssessmentCategory.java
│  │  │      PsychologicalAssessmentStandard.java
│  │  │      
│  │  ├─counsel
│  │  │      CounselorType.java
│  │  │      CounselResult.java
│  │  │      CounselState.java
│  │  │      SuggestCallCenter.java
│  │  │      SuggestComment.java
│  │  │      
│  │  ├─dialog
│  │  │      DialogRole.java
│  │  │      
│  │  ├─response
│  │  │      BasicApiSwaggerResponse.java
│  │  │      CommonResponse.java
│  │  │      ErrorResponse.java
│  │  │      
│  │  └─user
│  │          UserAgency.java
│  │          UserRole.java
│  │          
│  ├─exception
│  │      CommonException.java
│  │      
│  ├─handler
│  │      ValidationExceptionHandler.java
│  │      
│  ├─response
│  │      CommonResponse.java
│  │      
│  └─validator
│          CommonValidator.java
│          
├─config
│      GptConfig.java
│      QuerydslConfig.java
│      RedisConfig.java
│      SecurityConfig.java
│      SwaggerConfig.java
│      
├─contents
│  ├─controller
│  │      ContentsController.java
│  │      
│  ├─domain
│  │  ├─entity
│  │  │      MeditationContents.java
│  │  │      PsychotherapyContents.java
│  │  │      Quotation.java
│  │  │      
│  │  ├─request
│  │  └─response
│  │          ContentsResponse.java
│  │          MeditationResponse.java
│  │          
│  ├─repository
│  │  │  MeditationContentsRepository.java
│  │  │  PsychotherapyContentsRepository.java
│  │  │  QuotationRepository.java
│  │  │  
│  │  └─querydsl
│  │          PsychotherapyContentsQueryRepository.java
│  │          PsychotherapyContentsQueryRepositoryImpl.java
│  │          
│  ├─service
│  │      ContentsScheduler.java
│  │      ContentsService.java
│  │      
│  └─swagger
│          ContentsSwaggerConfig.java
│          
├─counsel
│  ├─controller
│  │      CounselController.java
│  │      
│  ├─domain
│  │  ├─entity
│  │  │      Counsel.java
│  │  │      Dialog.java
│  │  │      
│  │  ├─request
│  │  │      ChatRequest.java
│  │  │      
│  │  └─response
│  │          CounselHistoryResponse.java
│  │          CounselResultResponse.java
│  │          DialogHistory.java
│  │          FinishCounselResponse.java
│  │          ProceedCounselResponse.java
│  │          StartCounselResponse.java
│  │          SuggestHospitalResponse.java
│  │          
│  ├─repository
│  │      CounselQueryRepository.java
│  │      CounselQueryRepositoryImpl.java
│  │      CounselRepository.java
│  │      DialogRepository.java
│  │      
│  ├─service
│  │      CounselChatService.java
│  │      CounselResultService.java
│  │      CounselService.java
│  │      
│  └─swagger
│          CounselSwaggerConfig.java
│          
├─mypage
│  ├─controller
│  │      MypageController.java
│  │      
│  ├─domain
│  │  ├─request
│  │  │      CompletedCounselRequest.java
│  │  │      PsychologicalChangeTrendRequest.java
│  │  │      
│  │  └─response
│  │          CompletedCounselResponse.java
│  │          MypageMainResponse.java
│  │          PsychologicalChangeTrendResponse.java
│  │          
│  ├─repository
│  │      MypageQueryRepository.java
│  │      MypageQueryRepositoryImpl.java
│  │      
│  ├─service
│  │      MypageService.java
│  │      
│  └─swagger
│          MypageSwaggerConfig.java
│          
├─security
│  ├─jwt
│  │  ├─filter
│  │  │      JwtFailureFilter.java
│  │  │      JwtRequestFilter.java
│  │  │      
│  │  └─service
│  │          JwtUtil.java
│  │          
│  └─service
│          CustomUserDetailService.java
│          UserInfoUtil.java
│          
└─user
    ├─controller
    │      UserController.java
    │      
    ├─domain
    │  ├─entity
    │  │      User.java
    │  │      UserMentalState.java
    │  │      UserToken.java
    │  │      
    │  └─request
    │          SmsSendRequest.java
    │          SmsVerificationRequest.java
    │          UserJoinRequest.java
    │          
    ├─repository
    │  │  SmsVerificationRepository.java
    │  │  UserRepository.java
    │  │  UserTokenRepository.java
    │  │  
    │  └─querydsl
    │          UserQueryRepository.java
    │          UserQueryRepositoryImpl.java
    │          
    ├─service
    │      SmsUtil.java
    │      UserService.java
    │      
    └─swagger
            UserSwaggerConfig.java
```
<br/>
<br/>
