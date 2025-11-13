# 🌱 GreenCoach

<div align="center">

![GreenCoach Logo](frontend/app/src/main/res/drawable/ic_leaf_logo.png)

**분리배출, 오늘부터 함께 시작해요**

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)](https://developer.android.com/jetpack/compose)

[개발 환경](#-개발-환경) • [Features](#-features) • [Screenshots](#-screenshots) • [개발 팀](#-개발-팀) • [향후 계획](#-향후-계획) • [Tech Stack](#-Tech-Stack) • [Architecture](#-architecture) • [Installation](#-installation) • [API Documentation](#-API-Documentation)

</div>

<br>

## 📖 Overview

**GreenCoach**는 AI 기술을 활용하여 올바른 분리배출을 돕는 환경 보호 앱입니다. 사용자가 쓰레기를 촬영하면 AI가 자동으로 분류하고, 올바른 배출 방법을 안내합니다. 또한 분리배출 활동을 기록하며 나만의 숲을 키우고, 커뮤니티에서 환경 실천 경험을 공유할 수 있습니다.

### 개발 기간
- **2025년 7월 ~ 2025년 11월** (약 4개월)
- Sprint 기반 Agile 개발 방식 적용

### 팀 구성
- **2인 팀 프로젝트**
  - Full-stack 개발 (Backend + Android)
  - AI 모델 통합 및 UI/UX 디자인

### 개발 목적
- 환경부 분리배출 가이드라인 기반 실용적인 솔루션 제공
- AI 기술을 활용한 사용자 편의성 극대화
- 커뮤니티 기반 지속가능한 습관 형성 지원

### 🎯 Key Highlights

- 🤖 **AI-Powered Waste Classification**: ONNX/TensorFlow Lite 기반 실시간 분류
- 📸 **Camera Scan**: 쓰레기 촬영만으로 즉시 분류 및 가이드 제공
- 🌳 **Gamification**: 분리배출 횟수에 따라 성장하는 나만의 숲
- 📊 **CO2 Visualization**: 전세계/한국/내 CO2 배출량 비교 차트
- 👥 **Community**: 환경 실천 경험 공유 및 소통
- 📚 **Comprehensive Guide**: 14개 카테고리, 80+ 세부 품목별 상세 가이드

<br>

## 💻 개발 환경

### Backend
| 구분 | 내용 |
|------|------|
| **IDE** | IntelliJ IDEA 2024.1+ |
| **JDK** | OpenJDK 17 (Eclipse Temurin) |
| **Build Tool** | Gradle 8.13 (Kotlin DSL) |
| **Database** | H2 (개발) / MySQL 8.0 (프로덕션 예정) |
| **Server** | Embedded Tomcat 10.1 |

### Frontend (Android)
| 구분 | 내용 |
|------|------|
| **IDE** | Android Studio Ladybug (2024.2.1) |
| **Language** | Kotlin 2.1.21 |
| **Compile SDK** | API 36 (Android 14+) |
| **Min SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 36 |
| **Gradle** | 8.13 |

### 협업 도구
```
- 버전 관리: Git / GitHub
- 이슈 트래킹: GitHub Issues
- 문서화: GitHub Wiki, Notion
- 커뮤니케이션: Discord
```

<br>

## ✨ Features

### 1. 🏠 Home - 카테고리 & 뉴스

<img width="1847" height="889" alt="Home" src="https://github.com/user-attachments/assets/7d80812e-908b-4f4a-af5a-1fd793069093" />

- **14개 분리배출 카테고리** 빠른 접근
- **실시간 환경 뉴스** (Naver News API 연동)
- **검색 기능**: 키워드로 빠르게 품목 찾기
- **인기 해시태그**: 자주 검색된 항목 표시

### 2. 🌳 Forest - 나만의 숲 키우기

<img width="800" height="671.58" alt="Forest" src="https://github.com/user-attachments/assets/332eb008-6413-41b9-87d9-f0815abca737" />

**성장 단계** (Leafs 기반):
- 🌱 **Seed** (0-10 leafs): 씨앗 단계
- 🌿 **Sprout** (11-20 leafs): 새싹이 돋아남
- 🌾 **Sapling** (21-30 leafs): 어린 나무
- 🌲 **Growing Tree** (31-50 leafs): 무럭무럭 자라는 중
- 🌳 **Mature Tree** (51-70 leafs): 성숙한 나무
- 🍎 **Fruit-bearing Tree** (71+ leafs): 열매 맺은 나무

**CO2 데이터**:
- 📈 세계/한국/나의 CO2 배출량 및 감소량 비교
- 📊 Our World in Data 실시간 데이터 연동

### 3. 📸 Scan - AI 분류

<img width="900" height="641.15" alt="스캔" src="https://github.com/user-attachments/assets/7ac4cccc-5445-47e0-b5bc-5b73d054893f" />

- **AI 기반 자동 분류**: 이미지 업로드 시 실시간 분석
- **신뢰도 표시**: AI 예측 정확도 퍼센트
- **상세 가이드 연결**: "Read More"로 세부 배출 방법 확인
- **히스토리 관리**: 최근 스캔 내역 저장 및 조회
- **Leafs 획득**: 스캔할 때마다 포인트 적립

### 4. ♻️ Category - 상세 가이드

<img width="900" height="545.03" alt="카테고리" src="https://github.com/user-attachments/assets/07608894-b509-4ea5-b13c-3ce5c1459ea8" />

**카테고리별 가이드**:
- 투명 페트병, 플라스틱, 비닐류, 스티로폼
- 캔류, 고철류, 유리병, 종이류
- 섬유류, 대형/소형 전자제품, 가구, 전지류, 음식물

**세부 품목 (80+)**:
- 각 카테고리당 3-7개 대표 품목
- 단계별 배출 방법 (Step by Step)
- ❌ 잘못된 배출 예시

### 5. 👥 Community - 경험 공유

![Community Feed](docs/screenshots/community_feed.png)
![Community Comments](docs/screenshots/community_comments.png)

- **피드**: 환경 실천 경험 공유
- **좋아요 & 댓글**: 실시간 상호작용
- **알림**: 좋아요/댓글/답글 알림
- **북마크**: 마음에 드는 게시글 저장
- **검색**: 키워드로 게시글 필터링

### 6. 👤 Profile - 내 활동

<img width="1568" height="889" alt="프로필" src="https://github.com/user-attachments/assets/35a32174-647a-401e-8b12-5394603482fb" />

- **회원가입/로그인**: JWT 기반 인증
- **내 게시글/저장된 글/댓글**: 탭 전환으로 관리
- **프로필 편집**: 닉네임, 생년월일, 성별 수정
- **소셜 로그인**: Naver, Google 연동 (준비 중)

<br>

## 📱 Screenshots

<details>
<summary>🏠 Home & Search</summary>

| Home | News | Search Result |
|:----:|:----:|:-------------:|
| <img width="300" height="667" alt="Home" src="https://github.com/user-attachments/assets/f8c77a48-52ca-40d6-8f98-25ac2e162660" /> | <video src="https://github.com/user-attachments/assets/2df85e61-fa2b-47cb-b191-02daa440e3c1.mp4" width="370" autoplay muted loop playsinline></video> | <video src="https://github.com/user-attachments/assets/f17bcf33-7b96-4990-bcf7-a6c355099e3a.mp4" width="370" autoplay muted loop playsinline></video> |
| <sub>카테고리 · 뉴스 · 해시태그</sub> | <sub>실시간 환경 뉴스</sub> | <sub>키워드 검색 결과</sub> |

| 검색 결과 없음 (미등록 품목)  |
|:--------------------:|
| <img width="450" height="351.52" alt="Check redundancy" src="https://github.com/user-attachments/assets/6bfeb5f9-e48d-47bb-a4fa-88cfe26baef9"/> |
| <sub>데이터에 없는 품목 검색 시 ‘결과 없음’ 안내</sub> |

</details>

<details>
<summary>🌳 Forest Growth Stages</summary>

| Seed | Sprout | Sapling | Growing Tree | Mature Tree | Fruit-bearing Tree |
|:------:|:--------:|:---------:|:--------------:|:-------------:|:--------------------:|
| <div align="center"><img src="frontend/app/src/main/res/drawable/seed.png" width="100" height="110"><br>0%~10%</div> | <div align="center"><img src="frontend/app/src/main/res/drawable/sprout.png" width="100" height="160"><br>11%~20%</div> | <div align="center"><img src="frontend/app/src/main/res/drawable/sapling.png" width="100" height="160"><br>21%~30%</div> | <div align="center"><img src="frontend/app/src/main/res/drawable/growing_tree.png" width="80"><br>31%~50%</div> | <div align="center"><img src="frontend/app/src/main/res/drawable/mature_tree.png" width="80"><br>51%~70%</div> | <div align="center"><img src="frontend/app/src/main/res/drawable/fruit_tree.png" width="80"><br>71%~</div> |

</details>

<details>
<summary>📸 Scan & AI Classification</summary>

| Scan Main | Scanning | Result | Detail Guide |
|-----------|----------|--------|--------------|
| ![](docs/screenshots/scan_main.png) | ![](docs/screenshots/scan_scanning.png) | ![](docs/screenshots/scan_result.png) | ![](docs/screenshots/scan_detail.png) |

</details>

<details>
<summary>♻️ Category Details</summary>

| Category |
|:----:|
| <video src="https://github.com/user-attachments/assets/556e08d7-5100-4b6c-87a5-d6bd102e44f1.mp4" width="370" autoplay muted loop playsinline></video> |
| <sub>분리수거 카테고리 15종</sub> |

</details>

<details>
<summary>👥 Community & Interactions</summary>

| Feed | Comments | Notifications | Saved Posts |
|------|----------|---------------|-------------|
| ![](docs/screenshots/community_feed.png) | ![](docs/screenshots/community_comments.png) | ![](docs/screenshots/community_notifications.png) | ![](docs/screenshots/community_saved.png) |

</details>

<details>
<summary>👤 Profile & Authentication</summary>

| Sign Up | Login | Profile Home | Edit Profile |
|:-------:|:-----:|:------------:|:------------:|
| <video src="https://github.com/user-attachments/assets/811bedc0-301e-4659-9e13-beaac25a2d5b.mp4" width="370" autoplay muted loop playsinline title="Sign up flow"></video> | <video src="https://github.com/user-attachments/assets/d8e0616f-8ef7-49d5-ba86-26f467c0bdf6.mp4" width="370" autoplay muted loop playsinline title="Login flow"></video> | <video src=".mp4" width="370" autoplay muted loop playsinline title="Profile home"></video> | <video src=".mp4" width="370" autoplay muted loop playsinline title="Edit profile"></video> |
| <sub>회원가입 플로우 · 필드 검증</sub> | <sub>로그인 플로우 · 일반/소셜</sub> | <sub>프로필 홈 · 정보/포인트</sub> | <sub>프로필 편집 · 닉네임/아바타</sub> |

| Nickname Duplication | Invalid Input (Generic) |
|:--------------------:|:----------------------:|
| <img width="300" height="667" alt="Check redundancy" src="https://github.com/user-attachments/assets/925c6b0d-b7fe-4ef9-b73c-1f90f0a828d3"/> | <video src="https://github.com/user-attachments/assets/bac18e1a-01d9-4306-83cc-4105de369d6c.mp4" width="300" autoplay muted loop playsinline title="Form validation demo"></video> |
| <sub>닉네임 중복 검사</sub> | <sub>잘못된 정보 입력 시 유효성</sub> |

</details>

<br>

## 👥 개발 팀

### Team Members (2인)

| 역할 | 담당 업무 |
|------|----------|
| **김하늘** | • UI/UX 디자인<br> • Backend 개발 (Spring Boot, REST API)<br> • Android 개발 (Jetpack Compose)<br> • 상태 관리 (ViewModel, StateFlow) |
| **심재학** | • AI 개발 및 모델 통합 (ONNX, TFLite)<br> • 데이터베이스 설계 (JPA, H2)<br> • 보안 (Spring Security, JWT)<br> • API 연동 (Retrofit, Hilt) |

### 공동 작업
- 프로젝트 기획 및 요구사항 분석
- API 명세 설계
- 테스트 및 디버깅
- 문서화 작업

<br>

## 🎯 향후 계획

- [ ] MySQL 마이그레이션 (H2 → Production DB)
- [ ] 소셜 로그인 (Naver, Google) 완성
- [ ] 푸시 알림 (FCM 연동)
- [ ] 포인트 & 리워드 시스템
- [ ] 지역별 분리배출 규칙 커스터마이징
- [ ] AI 모델 고도화 (더 많은 카테고리, 높은 정확도)

<br>

## 🛠 Tech Stack

### 📱 Android Client

```kotlin
// UI
- Jetpack Compose (Material3)
- Navigation Compose
- Coil (Image Loading)

// Architecture
- MVVM + Repository Pattern
- Hilt (Dependency Injection)
- Kotlin Coroutines & Flow

// Network
- Retrofit + OkHttp
- Gson Converter

// Local Storage
- SharedPreferences (Scan History)
- Room (향후 확장)

// AI/ML (향후)
- TensorFlow Lite
- ONNX Runtime
```

### 🔧 Backend (Spring Boot)

```kotlin
// Framework
- Spring Boot 3.5.3
- Kotlin 2.1.21
- Spring Security + JWT

// Database
- JPA/Hibernate
- MySQL (Production)
- H2 (Development)

// External APIs
- Naver News API
- Naver Image Search API
- Our World in Data (CO2)

// AI/ML
- ONNX Runtime (1.20.0)
- TensorFlow Lite (2.12.0)

// Build Tool
- Gradle (Kotlin DSL)
```

<br>

## 🏗 Architecture

### Android App Architecture

```
frontend/
├── app/src/main/java/com/application/frontend/
│   ├── ui/
│   │   ├── screen/          # Compose Screens
│   │   │   ├── HomeScreen.kt
│   │   │   ├── ForestScreen.kt
│   │   │   ├── ScanScreen.kt
│   │   │   ├── CommunityScreen.kt
│   │   │   └── ProfileScreen.kt
│   │   └── theme/           # Material3 Theme
│   ├── viewmodel/           # ViewModels (MVVM)
│   ├── data/
│   │   ├── repository/      # Data Layer
│   │   └── local/           # Local Storage
│   ├── model/               # Data Models
│   ├── di/                  # Hilt Modules
│   └── navigation/          # Navigation Graph
```

### Backend Architecture

```
src/main/java/com/greencoach/
├── controller/              # REST Controllers
│   ├── AuthController.kt
│   ├── CategoryController.kt
│   ├── ScanController.kt
│   └── CommunityController.kt
├── service/                 # Business Logic
│   ├── AuthService.kt
│   ├── ScanService.kt
│   ├── Co2Service.kt
│   └── ai/
│       └── OnnxAiEngine.kt
├── model/                   # DTOs & Entities
│   ├── auth/
│   ├── community/
│   └── scan/
├── repository/              # JPA Repositories
└── config/                  # Security, JWT, WebClient
```

<br>

## 🚀 Installation

### Prerequisites

```bash
# Android Development
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 24+ (API Level 24+)

# Backend Development
- JDK 17 or later
- MySQL 8.0+ (or H2 for local dev)
- Gradle 8.1+
```

### 1️⃣ Clone Repository

```bash
git clone https://github.com/yourusername/greencoach.git
cd greencoach
```

### 2️⃣ Backend Setup

```bash
# Navigate to backend
cd backend

# Configure application.yml (or use H2 default)
cp src/main/resources/application.yml.example src/main/resources/application.yml

# Edit application.yml
# - Database credentials
# - JWT secret
# - Naver API credentials

# Build and Run
./gradlew bootRun

# API will be available at http://localhost:8080
```

**Environment Variables (Optional)**:
```properties
# Naver API (for News)
naver.client.id=YOUR_CLIENT_ID
naver.client.secret=YOUR_CLIENT_SECRET

# JWT
jwt.secret=your-secret-key-min-32-bytes-long
jwt.expiresMillis=86400000

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/greencoach
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 3️⃣ Android Setup

```bash
# Navigate to frontend
cd frontend

# Open in Android Studio
# File > Open > Select 'frontend' folder

# Configure local.properties
echo "sdk.dir=/path/to/your/Android/Sdk" > local.properties

# Add Naver API keys (optional)
echo "naver.client.id=YOUR_CLIENT_ID" >> gradle.properties
echo "naver.client.secret=YOUR_CLIENT_SECRET" >> gradle.properties

# Sync Gradle
# Build > Make Project

# Run on Emulator/Device
# Run > Run 'app'
```

**Update API Base URL** (if needed):
```kotlin
// frontend/app/build.gradle.kts
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
// For physical device, use your computer's IP
```

<br>

## 🧪 Testing

### Android Tests

```bash
# Unit Tests
./gradlew test

# Instrumented Tests (requires emulator/device)
./gradlew connectedAndroidTest

# Specific test classes
./gradlew test --tests "*ProfileFlowsTest"
```

**Test Coverage**:
- ✅ Login/Sign-up flows
- ✅ Password reset flows
- ✅ Category navigation
- ✅ Scan functionality
- ✅ Community interactions

### Backend Tests

```bash
# Run all tests
./gradlew test

# Specific test
./gradlew test --tests com.greencoach.service.AuthServiceTest
```

<br>

## 📡 API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication

#### Sign Up
```http
POST /auth/signup
Content-Type: application/json

{
  "nickname": "greenuser",
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Categories

#### Get Top Categories
```http
GET /api/categories

Response: 200 OK
[
  {
    "name": "투명 페트병",
    "imageUrl": "/images/icons/pet_bottle.png"
  },
  ...
]
```

#### Get Sub Categories
```http
GET /api/categories/{name}/sub

Response: 200 OK
[
  {
    "name": "생수",
    "imageUrl": "/images/sub/pet_water.png"
  },
  ...
]
```

### Scan

#### Analyze Image
```http
POST /api/scan
Content-Type: multipart/form-data

file: [binary image data]

Response: 200 OK
{
  "items": [
    {
      "label": "plastic_bottle",
      "confidence": 0.87,
      "category": "플라스틱"
    }
  ],
  "model": "onnx-garbage-classifier",
  "processedAt": "2025-01-15T10:30:00Z"
}
```

### CO2 Data

#### Get World CO2 Data
```http
GET /api/co2/world

Response: 200 OK
{
  "emissions": {
    "label": "World Emissions",
    "points": [
      {"year": 2020, "value": 6500.0},
      {"year": 2021, "value": 6800.0}
    ]
  },
  "reduction": {
    "label": "World Reduction",
    "points": [...]
  }
}
```

### Community

#### Get Feed
```http
GET /community/feed
Authorization: Bearer {token}

Response: 200 OK
[
  {
    "id": "1",
    "author": {...},
    "createdAt": "2025-01-15T10:00:00Z",
    "text": "오늘 처음으로 투명 페트병 분리배출 성공!",
    "likeCount": 15,
    "commentCount": 3
  }
]
```

<details>
<summary>📖 More API Endpoints</summary>

**Comments**
- `GET /community/posts/{postId}/comments` - Get comments
- `POST /community/posts/{postId}/comments` - Create comment
- `POST /community/comments/{commentId}/like` - Like comment
- `DELETE /community/comments/{commentId}` - Delete comment

**Notifications**
- `GET /community/notifications` - Get notifications
- `GET /community/notifications/meta` - Get unread count
- `POST /community/notifications/read-all` - Mark all as read

**Profile**
- `GET /users/me` - Get current user
- `PATCH /users/me` - Update profile

</details>

<br>

## 🙏 Acknowledgments

- **Our World in Data** - CO2 emissions data
- **Naver API** - News and image search
- **Material Design 3** - UI/UX guidelines
- **Jetpack Compose** - Modern Android UI toolkit
- All contributors who help improve this project

<br>

<div align="center">

**Made with ❤️ for the Environment**

[⬆ Back to Top](#-greencoach)

</div>