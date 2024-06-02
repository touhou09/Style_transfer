### API 문서

### exampleImageController
이미지 관련 요청을 처리하는 컨트롤러입니다.

**엔드포인트:**
- `POST /api/images`: 이미지를 처리하는 요청.

**예제 요청:**
```json
{
  "text": "Example text"
}
```

**예제 응답:**
```json
{
  "content": [],
  "pageable": {
    "sort": {
      "unsorted": true,
      "sorted": false,
      "empty": true
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 6,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 0,
  "first": true,
  "size": 6,
  "number": 0,
  "sort": {
    "unsorted": true,
    "sorted": false,
    "empty": true
  },
  "numberOfElements": 0,
  "empty": true
}
```
결과가 빈 페이지로 나옴, 여기서 content는 아래와 같음
```json
{
  "summary": "Image summary",
  "images": [
    {
      "id": "image1",
      "data": "base64encodedImageData"
    }
  ]
}

```

### ImageGenerationController

**엔드포인트:**
- `POST /api/generate-images`: 이미지를 생성하는 요청.

**예제 요청:**
```json
{
  "token": "auth_token",
  "projectId": "project123",
  "id": "이전에 선택한 이미지의 id(경로)",
  "basicItems": [
    {
      "index": 0,
      "promptText": "Example prompt 1"
    },
    {
      "index": 1,
      "promptText": "Example prompt 2"
    }
  ]
}
```

**예제 응답:**
```json
{
  "projectId": "project123",
  "generatedItems": [
    {
      "index": 0,
      "promptText": "Example prompt 1",
      "generatedImage": "base64encodedImageData"
    },
    {
      "index": 1,
      "promptText": "Example prompt 2",
      "generatedImage": "base64encodedImageData"
    }
  ]
}
```

#### `UserController`

- **Endpoint:** `/user/google`
- **Method:** POST
- **Input:**
  ```json
  {
    "code": "string"
  }
  ```
- **Output:**
  ```json
  {
    "accessToken": "string"
  }
  ```

- **Endpoint:** `/user/info`
- **Method:** GET
- **Headers:**
  ```json
  {
    "Authorization": "Bearer token"
  }
  ```
- **Output:**
  ```json
  {
    "id": "string",
    "email": "string",
    "name": "string",
    "profileImage": "string"
  }
  ```

- **Endpoint:** `/user/projects`
- **Method:** GET
- **Headers:**
  ```json
  {
    "Authorization": "Bearer token"
  }
  ```
- **Output:**
  ```json
  [
    {
      "projectId": "string",
      "summary": "string",
      "exampleImage": "string",
      "generatedItems": [
        {
          "index": 0,
          "generatedImage": "string"
        }
      ],
      "time": "2023-05-27T14:57:53.123"
    }
  ]
  ```

- **Endpoint:** `/user/project/{projectId}`
- **Method:** GET
- **Headers:**
  ```json
  {
    "Authorization": "Bearer token"
  }
  ```
- **Output:**
  ```json
  {
    "projectId": "string",
    "summary": "string",
    "exampleImage": "string",
    "generatedItems": [
      {
        "index": 0,
        "generatedImage": "string"
      }
    ],
    "time": "2023-05-27T14:57:53.123"
  }
  ```