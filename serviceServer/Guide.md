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
- 예시 이미지 + 프롬프트 텍스트 기반으로 생성된 이미지 반환(basicPrompt 브랜치)

http://localhost:8080/api/generate-images

**input**
```json
{
  "projectId": "12345",
  "exampleImage": "Base64인코딩된이미지데이터1",
  "basicItems": [
    {
      "index": "item1",
      "promptText": "이미지 설명 예시 1"
    },
    {
      "index": "item2",
      "promptText": "이미지 설명 예시 2"
    }
  ]
}
```
**output**
```json
{
  "projectId": "12345",
  "generatedItems": [
    {
      "index": "item1",
      "promptText": "이미지 설명 예시 1",
      "generatedImage": "Base64인코딩된이미지데이터1_generated_item1"
    },
    {
      "index": "item2",
      "promptText": "이미지 설명 예시 2",
      "generatedImage": "Base64인코딩된이미지데이터1_generated_item2"
    }
  ]
}
```
