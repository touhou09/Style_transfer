# Service Server

---

## API 명세

swagger-ui 사용  
아래 주소로 들어가서 체크 가능함(서버 실행시)  
이후 JWT 적용 후에는 superKey 보유자만 접근 가능하도록 수정  

http://localhost:8080/swagger-ui/index.html  

---

## 현재 구현해야하는 기능

- 프롬프트 기반으로 예시 이미지 탐색
  http://localhost:8080/api/images?page=0&size=20  
여기서 ?page={정수}&size={정수}

**input**
```json
{
  "text": "예시 텍스트입니다."
}

```

**output**
```json
{
  "content": [
    {
      "id": "1",
      "data": "이미지 데이터 (Base64 인코딩)"
    },
    {
      "id": "2",
      "data": "이미지 데이터 (Base64 인코딩)"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 20,
    "paged": true,
    "unpaged": false
  },
  "last": false,
  "totalPages": 5,
  "totalElements": 100,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "first": true,
  "numberOfElements": 20,
  "empty": false
}
```
pageable이 같이 전송되는 부분은 이해가 잘 안되서 일단 보류  

projectId: 프로젝트의 고유 ID.  
exampleImage: 예시 이미지의 Base64 인코딩된 데이터.  
generatedItems: 여러 개의 생성된 아이템을 포함하는 리스트.  
index: 아이템의 인덱스.  
promptText: 프롬프트 텍스트.  
generatedImage: 생성된 이미지의 Base64 인코딩된 데이터.  
  

- 예시 이미지 + 프롬프트 텍스트 기반으로 생성된 이미지 반환(basicPrompt 브랜치)
  http://localhost:8080/api/generate-images

**input**
````json
{
  "token": "exampleToken123",
  "projectId": "project123",
  "exampleImage": "base64_example_image_data",
  "basicItems": [
    {
      "index": 1,
      "promptText": "Example prompt text 1"
    },
    {
      "index": 2,
      "promptText": "Example prompt text 2"
    },
    {
      "index": 3,
      "promptText": "Example prompt text 3"
    }
  ]
}

````

**output**
```json
{
  "projectId": "12345",
  "exampleImage": "base64_example_image_data",
  "generatedItems": [
    {
      "index": "1",
      "promptText": "Example prompt text 1",
      "generatedImage": "base64_generated_image_data_1"
    },
    {
      "index": "2",
      "promptText": "Example prompt text 2",
      "generatedImage": "base64_generated_image_data_2"
    },
    {
      "index": "3",
      "promptText": "Example prompt text 3",
      "generatedImage": "base64_generated_image_data_3"
    }
  ]
}
```

token: 인증이나 식별을 위한 토큰.  
projectId: 프로젝트의 고유 ID.  
exampleImage: 해당 프로젝트와 관련된 공통 예시 이미지의 Base64 인코딩된 데이터.  
basicItems: 여러 개의 프롬프트 항목을 포함하는 리스트.  
index: 각 항목의 고유 인덱스.  
promptText: 프롬프트 텍스트.

### 1. 구글로 인증하기

- **엔드포인트**: `/user/google`
- **메소드**: `POST`
- **설명**: 구글 인증 코드를 사용하여 사용자를 인증하고 액세스 토큰을 반환합니다.

#### 입력
- **파라미터**:
  - `code` (string, 필수): 구글 인증 코드.

```json
{
  "code": "string"
}
```

#### 출력
- **응답**: `200 OK`
- **본문**:
  - `accessToken` (string): 인증에 성공한 후 생성된 액세스 토큰.

```json
{
  "accessToken": "string"
}
```

### 2. 사용자 정보 조회

- **엔드포인트**: `/user/info`
- **메소드**: `GET`
- **설명**: 제공된 인증 토큰을 기반으로 사용자의 정보를 조회합니다.

#### 입력
- **헤더**:
  - `Authorization` (string, 필수): 인증을 위한 베어러 토큰.

```json
{
  "Authorization": "Bearer token"
}
```

#### 출력
- **응답**: `200 OK` 또는 `404 Not Found`
- **본문**:
  - `id` (string): 사용자의 ID.
  - `token` (string): 사용자의 토큰.
  - `name` (string): 사용자의 이름.
  - `email` (string): 사용자의 이메일.
  - `projects` (array): 사용자의 프로젝트 목록.

```json
{
  "id": "string",
  "token": "string",
  "name": "string",
  "email": "string",
  "projects": [
    {
      "imageUrl": "string",
      "description": "string"
    }
  ]
}
```

### 3. 사용자 프로젝트 조회

- **엔드포인트**: `/user/projects`
- **메소드**: `GET`
- **설명**: 제공된 인증 토큰을 기반으로 사용자의 프로젝트를 조회합니다.

#### 입력
- **헤더**:
  - `Authorization` (string, 필수): 인증을 위한 베어러 토큰.

```json
{
  "Authorization": "Bearer token"
}
```

#### 출력
- **응답**: `200 OK` 또는 `404 Not Found`
- **본문**:
  - `projects` (array): 사용자의 프로젝트 목록.

```json
[
  {
    "imageUrl": "string",
    "description": "string"
  }
]
```

## 요약

### 엔드포인트

1. **POST** `/user/authenticate`
  - **입력**: 구글 인증 코드.
  - **출력**: 액세스 토큰.

2. **GET** `/user/info`
  - **입력**: 인증 토큰.
  - **출력**: 사용자 정보.

3. **GET** `/user/projects`
  - **입력**: 인증 토큰.
  - **출력**: 사용자의 프로젝트 목록.

이 문서는 API 엔드포인트에 대한 상세한 개요를 제공하며, 입력 파라미터와 예상되는 출력을 설명합니다. JSON 예제는 요청 및 응답 데이터 형식을 명확하게 설명합니다.