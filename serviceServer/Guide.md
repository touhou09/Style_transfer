### API 문서

#### `ImageGenerationController`

- **Endpoint:** `/generate-images`
- **Method:** POST
- **Input:**
  ```json
  {
    "token": "string",
    "projectId": "string",
    "summary": "string",
    "exampleImage": "string",
    "basicItems": [
      {
        "index": 0,
        "promptText": "string"
      }
    ]
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

#### `exampleImageController`

- **Endpoint:** `/images`
- **Method:** POST
- **Input:**
  ```json
  {
    "text": "string",
    "page": 0,
    "size": 10
  }
  ```
- **Output:**
  ```json
  {
    "content": [
      {
        "id": "string",
        "url": "string",
        "description": "string"
      }
    ],
    "pageable": {
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "pageSize": 10,
      "pageNumber": 0,
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 1,
    "totalElements": 1,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 1,
    "first": true,
    "empty": false
  }
  ```

이 문서는 각 API의 경로, HTTP 메서드, 입력 및 출력 형식을 JSON으로 나타냅니다. 이를 통해 클라이언트가 API를 올바르게 호출하고 응답을 처리할 수 있습니다.
