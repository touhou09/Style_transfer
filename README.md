# Style Transfer
## 일관된 이미지 생성 모델을 이용한 서비스
### Service Server
- openjdk 17
- spring boot 3.3.0

### Model Server
- python 3.9.5
- fastapi 0.110.1
- uvicorn 0.29.0
---
## 실행방법

### Service Server

#### 1. Gradle 기반의 서비스 서버 실행

##### 1.1. Gradle Wrapper 사용하기
Gradle Wrapper를 사용하여 Spring Boot 애플리케이션을 실행할 수 있습니다. 프로젝트 루트 디렉토리에서 다음 명령어를 실행합니다:

**유닉스/리눅스/MacOS:**

```sh
./gradlew bootRun
```

**윈도우:**

```sh
gradlew.bat bootRun
```

이 명령어는 `build.gradle` 파일에 정의된 `bootRun` 태스크를 실행하여 애플리케이션을 시작합니다.

#### 2. JAR 파일로 패키징 후 실행하기

##### 2.1. Gradle을 사용하여 JAR 파일 생성하기
프로젝트를 빌드하여 JAR 파일로 패키징합니다. 다음 명령어를 실행합니다:

**유닉스/리눅스/MacOS:**

```sh
./gradlew clean build
```

**윈도우:**

```sh
gradlew.bat clean build
```

이 명령어는 프로젝트를 빌드하고 `build/libs` 디렉토리에 JAR 파일을 생성합니다.

##### 2.2. 생성된 JAR 파일 실행하기
생성된 JAR 파일을 실행하려면 다음 명령어를 사용합니다:

```sh
java -jar build/libs/transfer.jar
```
<hr>

### Model Server

#### Environment Setup

```　bash
conda create -n visual_fable python=3.9
conda activate visual_fable
pip install torch==2.0.1 torchvision==0.15.2 torchaudio==2.0.2 --index-url https://download.pytorch.org/whl/cu118
pip install diffusers mediapy einops transformers accelerate ftfy regex tqdm pandas
pip install git+https://github.com/openai/CLIP.git
pip install openai
pip install pyarrow
pip install fastapi uvicorn pydantic sqlalchemy databases alembic asyncpg



or refer requirements.txt
```

#### start server (where main.py is located)
```　bash
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```
<br>

---

### 기능별 API 및 그 흐름

#### 예시 이미지 생성

Service server의  
POST `/api/images`를 호출  
이후 Model server의  
POST `/exampleImages` 를 호출하여 아래의 형태의 데이터를 json으로 받는다.  
```json
"summury":"text"
"images": [
    {
      "id": "https://example.com/generatedImage1.jpg",
      "data": "Generated image 1"
    },
    {
      "id": "https://example.com/generatedImage1.jpg",
      "data": "Generated image 1"
    },
    {
      "id": "https://example.com/generatedImage1.jpg",
      "data": "Generated image 1"
    },
  ]
```

#### 이미지 생성

Service server의  
POST `/api/generate-images`를 모델서버로 전송  
이후 Model server의  
POST `/generate-images`를 호출하여 아래의 형태의 데이터를 json으로 받는다.  
```json
{
  "projectId" : "id",
  "generatedItems": [
    {
      "index": 0,
      "originalPrompt": "원래 텍스트 0",
      "generatedImage": "이미지 데이터 0 (Base64 인코딩)"
    },
    {
      "index": 1,
      "originalPrompt": "원래 텍스트 1",
      "generatedImage": "이미지 데이터 1 (Base64 인코딩)"
    },
    {
      "index": 2,
      "originalPrompt": "원래 텍스트 2",
      "generatedImage": "이미지 데이터 2 (Base64 인코딩)"
    }
  ]
}
```

#### 생성했던 이미지 조회

Service server에  
GET `/api/projects?page=0&size=10`를 조회하여  
아래와 같은 형태의 pageable json을 반환한다.
```json
{
  "content": [
    {
      "projectId": "projectId_value",
      "generatedItems": [
        {
          "imageUrl": "https://example.com/image1.jpg",
          "description": "Description of image 1"
        }
      ],
      "time": "2023-06-03T12:00:00"
    }
  ],
  "pageable": {
    "sort": {
      "empty": true,
      "unsorted": true,
      "sorted": false
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 10,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": true,
    "unsorted": true,
    "sorted": false
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

#### 생성했던 이미지 조회

Service server에  
GET `/api/project/{projectId}`를 조회하여  
해당하는 projectId의 Image Set을 반환받는다.
```json
{
  "projectId": "12345",
  "generatedItems": [
    {
      "index": 0,
      "promptText": "Generate an image of a sunset",
      "generatedImage": "generated image string"
    }
  ],
  "time": "2023-06-03T12:00:00"
}
```
