### API 문서

### ProjectController

#### 엔드포인트: 프로젝트 목록 조회

- **URL**: `/api/projects`
- **Method**: GET
- **Description**: 프로젝트 목록을 페이지네이션하여 반환합니다.
- **Request Parameters**:
  - `page` (int): 요청 페이지 번호 (0부터 시작)
  - `size` (int): 페이지당 항목 수

- **Request Example**:
  ```
  GET /api/projects?page=0&size=10
  ```

- **Response**:
  - **Status**: 200 OK
  - **Body**:
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

#### 엔드포인트: 특정 프로젝트 조회

- **URL**: `/api/project/{projectId}`
- **Method**: GET
- **Description**: 특정 프로젝트의 세부 사항을 반환합니다.
- **Path Variables**:
  - `projectId` (String): 프로젝트 ID

- **Request Example**:
  ```
  GET /api/project/12345
  ```

- **Response**:
  - **Status**: 200 OK
  - **Body**:
    ```json
    {
      "projectId": "12345",
      "generatedItems": [
        {
          "imageUrl": "https://example.com/image1.jpg",
          "description": "Description of image 1"
        }
      ],
      "time": "2023-06-03T12:00:00"
    }
    ```

  - **Status**: 404 Not Found (프로젝트가 존재하지 않을 경우)

### exampleImageController

#### 엔드포인트: 이미지 생성 요청

- **URL**: `/api/images`
- **Method**: POST
- **Description**: 이미지 생성을 요청하고, 결과를 페이지네이션하여 반환합니다.
- **Request Body**:
  - `exampleRequestDto` (JSON):
    ```json
    {
      "text": "example text"
    }
    ```

- **Request Parameters**:
  - `page` (int): 요청 페이지 번호 (0부터 시작)
  - `size` (int): 페이지당 항목 수

- **Request Example**:
  ```
  POST /api/images?page=0&size=10
  Content-Type: application/json

  {
    "text": "example text"
  }
  ```

- **Response**:
  - **Status**: 200 OK
  - **Body**:
    ```json
    {
      "content": [
        {
          "imageUrl": "https://example.com/generatedImage1.jpg",
          "description": "Generated image 1"
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

  - **Status**: 200 OK (빈 페이지 반환):
    ```json
    {
      "content": [],
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
      "totalElements": 0,
      "last": true,
      "size": 10,
      "number": 0,
      "sort": {
        "empty": true,
        "unsorted": true,
        "sorted": false
      },
      "numberOfElements": 0,
      "first": true,
      "empty": true
    }
    ```

### ImageGenerationController

#### 엔드포인트: 이미지 생성

- **URL**: `/api/generate-images`
- **Method**: POST
- **Description**: 프롬프트 요청에 따라 이미지를 생성하고, 결과를 반환합니다.
- **Request Body**:
  - `promptRequestDto` (JSON):
    ```json
    {
      "projectId": "12345",
      "id": "exampleId",
      "basicItems": [
        {
          "index": 1,
          "promptText": "Generate an image of a sunset"
        }
      ]
    }
    ```

- **Request Example**:
  ```
  POST /api/generate-images
  Content-Type: application/json

  {
    "projectId": "12345",
    "id": "exampleId",
    "basicItems": [
      {
        "index": 1,
        "promptText": "Generate an image of a sunset"
      }
    ]
  }
  ```

- **Response**:
  - **Status**: 200 OK
  - **Body**:
    ```json
    {
      "projectId": "12345",
      "generatedItems": [
        {
          "imageUrl": "https://example.com/sunset1.jpg",
          "description": "A beautiful sunset over the mountains"
        },
        {
          "imageUrl": "https://example.com/sunset2.jpg",
          "description": "Sunset with vibrant colors"
        }
      ],
      "time": "2024-06-03T12:00:00"
    }
    ```